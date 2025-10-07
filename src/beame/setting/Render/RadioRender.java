package beame.setting.Render;

import beame.Nebulae;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.MathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.module.Component;
import beame.setting.SettingList.RadioSetting;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static beame.util.render.ClientHandler.drawRound;

public class RadioRender extends Component {
// leaked by itskekoff; discord.gg/sk3d g1dwJsIS

    private final RadioSetting setting;
    private final Map<String, Float> textScrolls = new HashMap<>();
    private float titleScroll = 0f;
    private boolean isHoveringTitle = false;

    public RadioRender(RadioSetting setting) {
        this.setting = setting;
        for (String text : setting.strings) {
            textScrolls.put(text, 0f);
        }
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);

        // Обработка анимации для заголовка
        float titleWidth = Fonts.SUISSEINTL.get(14).getStringWidth(setting.getName());
        boolean titleTooLong = titleWidth > getWidth() - 10;
        isHoveringTitle = MathUtil.isHovered(mouseX, mouseY, getX(), getY(), getWidth(), 10);
        
        if (titleTooLong) {
            if (isHoveringTitle) {
                titleScroll = AnimationMath.fast(titleScroll, (getWidth() - 10) - titleWidth - 4, 6);
            } else {
                titleScroll = AnimationMath.fast(titleScroll, 0, 3);
            }
            
            // Отрисовка заголовка с эффектом прокрутки
            Scissor.push();
            Scissor.setFromComponentCoordinates(getX(), getY(), getWidth() - 10, 10);
            Fonts.SUISSEINTL.get(14).drawString(stack, setting.getName(), getX() + 1f + titleScroll, getY() + 2, 
                ColorUtils.rgb(Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120));
            Scissor.unset();
            Scissor.pop();
        } else {
            // Обычная отрисовка заголовка без прокрутки
            Fonts.SUISSEINTL.get(14).drawString(setting.getName(), getX() + 1f, getY() + 2, 
                ColorUtils.rgb(Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120));
        }
        
        setWidth(90);

        float offset = 0;
        for (String text : setting.strings) {
            float off = 10;
            boolean isSelected = text.equals(setting.get());

            drawRound(getX() + 1.5f, (getY() + 9 + offset), 8, 8, 4, isSelected ? Nebulae.getHandler().getThemeManager().getColor(0) : new Color(60, 60, 60, 50).getRGB());
            drawRound(getX() + 4.2f, (getY() + 11.5f + offset), 3f, 3f, 1f, isSelected ? new Color(0, 0, 0, 255).getRGB() : 0);

            boolean hovered = MathUtil.isHovered(mouseX, mouseY, getX() + 10.5f, getY() + 11.2f + offset, getWidth() - 12, 12);
            float textWidth = Fonts.SUISSEINTL.get(14).getStringWidth(text);
            boolean textInRegion = !(textWidth > getWidth() - 20);

            float currentScroll = textScrolls.getOrDefault(text, 0f);
            if(hovered) {
                if(!textInRegion) {
                    textScrolls.put(text, AnimationMath.fast(currentScroll, (getWidth() - 20) - textWidth - 4, 6));
                }
            } else {
                textScrolls.put(text, AnimationMath.fast(currentScroll, 0, 3));
            }

            Scissor.push();
            Scissor.setFromComponentCoordinates(getX() + 10.5f, getY() + 9 + offset, getWidth() - 20, 12);
            Fonts.SUISSEINTL.get(14).drawString(stack, text, getX() + 10.5f + textScrolls.get(text), getY() + 11.2f + offset, -1);
            Scissor.unset();
            Scissor.pop();

            offset += off;
        }
        setHeight(offset + 2);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouse) {
        super.mouseClicked(mouseX, mouseY, mouse);
        float offset = 0;
        for (String text : setting.strings) {
            offset += 10;
            if (MathUtil.isHovered(mouseX, mouseY, getX(), (getY() - 2.5f + offset), 8, 8) && mouse == 0) {
                setting.set(text);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
