package beame.setting.Render;

import beame.Essence;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.MathUtil;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.module.Component;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;

import java.awt.*;

import static beame.util.render.ClientHandler.*;

public class EnumRender extends Component {
// leaked by itskekoff; discord.gg/sk3d clm8axnp

    final EnumSetting setting;
    private float titleScroll = 0f;
    private boolean isHoveringTitle = false;

    boolean open = false;

    public EnumRender(EnumSetting setting ) {
        this.setting = setting;
    }

    private float aanim;
    private float rotateImage;

    private static final int HORIZONTAL_PADDING = 2;
    private static final int VERTICAL_PADDING = 2;
    private static final int HORIZONTAL_SPACING = 0;

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
            Fonts.SUISSEINTL.get(14).drawString(stack, setting.getName(), getX() + titleScroll, getY() + 2, 
                ColorUtils.rgb(Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120));
            Scissor.unset();
            Scissor.pop();
        } else {
            // Обычная отрисовка заголовка без прокрутки
            Fonts.SUISSEINTL.get(14).drawString(stack, setting.getName(), getX(), getY() + 2, 
                ColorUtils.rgb(Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120));
        }
        
        setWidth(85);

        float xOffset = getX();
        float yOffset = getY() + 10;
        float lineHeight = 0;

        for (BooleanSetting text : setting.get()) {
            float buttonWidth = Math.min(85, Fonts.SUISSEINTL.get(14).getStringWidth(text.getName()) + 3 * 2);
            float buttonHeight = 12;

            if (xOffset + buttonWidth > getX() + getWidth()) {
                xOffset = getX();
                yOffset += lineHeight + VERTICAL_PADDING;
            }

            boolean isEnum = text.get();
            int color = isEnum ? Essence.getHandler().getThemeManager().getColor(0) : new Color(60, 60, 60, 50).getRGB();
            text.alpha = AnimationMath.fast(text.alpha, isEnum ? 1 : 0, 8);

            if (isEnum) {
                drawSexyRectFromPanel(xOffset, yOffset, buttonWidth - 2, buttonHeight, 3.5f, true);
            } else {
                drawRound(xOffset, yOffset, buttonWidth - 2, buttonHeight, 3.5f, new Color(60, 60, 60, 50).getRGB());
            }

            boolean hovered = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) xOffset, (int) yOffset, (int) buttonWidth, (int) buttonHeight);
            boolean textInRegion = !(Fonts.SUISSEINTL.get(14).getStringWidth(text.getName()) > (buttonWidth - 4));
            if(hovered) {
                if(!textInRegion) text.textScroll = AnimationMath.fast(text.textScroll, (buttonWidth - 8) - Fonts.SUISSEINTL.get(14).getWidth(text.getName()), 2);
            } else {
                text.textScroll = AnimationMath.fast(text.textScroll, 0, 3);
            }
            Scissor.push();
            Scissor.setFromComponentCoordinates(xOffset + 2, yOffset, buttonWidth - 5.5f, buttonHeight);
            Fonts.SUISSEINTL.get(14).drawString(stack,text.getName(), xOffset + text.textScroll + 1F, yOffset + VERTICAL_PADDING + 2.5f, -1);
            Scissor.unset();
            Scissor.pop();

            xOffset += buttonWidth + HORIZONTAL_SPACING;
            lineHeight = buttonHeight;
        }

        setHeight(yOffset - getY() + lineHeight - 5);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouse) {
        super.mouseClicked(mouseX, mouseY, mouse);

        float xOffset = getX();
        float yOffset = getY() + 10;
        float lineHeight = 0;

        for (BooleanSetting text : setting.get()) {
            float buttonWidth = Math.min(85, Fonts.SUISSEINTL.get(14).getStringWidth(text.getName()) + 3 * 2);
            float buttonHeight = 12;

            if (xOffset + buttonWidth > getX() + getWidth()) {
                xOffset = getX();
                yOffset += lineHeight + VERTICAL_PADDING;
            }

            if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) xOffset, (int) yOffset, (int) buttonWidth, (int) buttonHeight) && mouse == 0) {
                text.set(!text.get());
            }
            xOffset += buttonWidth + HORIZONTAL_SPACING;
            lineHeight = buttonHeight;
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
