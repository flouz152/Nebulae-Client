package beame.setting.Render;

import beame.Essence;
import beame.util.Scissor;
import beame.util.animation.Animation;
import beame.util.animation.AnimationMath;
import beame.util.animation.Direction;
import beame.util.animation.impl.EaseBackIn;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.MathUtil;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.module.Component;
import net.minecraft.util.math.MathHelper;
import beame.setting.SettingList.SliderSetting;

import java.awt.*;

import static beame.util.render.ClientHandler.drawRound;

/**
 * SliderComponent
 */
public class SliderRender extends Component {
// leaked by itskekoff; discord.gg/sk3d vY9WmCIh

    private final SliderSetting setting;
    private float anim;
    private boolean drag;
    private boolean lastHovered  = false;
    private float hAnimation;
    Animation animation = new EaseBackIn(250, 1, 0.1f);

    public SliderRender(SliderSetting setting) {
        this.setting = setting;
        this.animation.setDirection(Direction.BACKWARDS);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        setWidth(90-10);
        setHeight(10);


        boolean hovered = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int)getX(), (int) getY(), 90-10, 10);


        this.hAnimation = AnimationMath.fast(hAnimation,
                (Fonts.SUISSEINTL.get(14).getWidth(setting.getName()) > 90-10) ?
                        (hovered ? -((float) Fonts.SUISSEINTL.get(14).getWidth(setting.getName()) /2 - 15) : 0) : 0, 1);


        Scissor.push();
        Scissor.setFromComponentCoordinates(getX(), getY(), 90-10, 10);
        Fonts.SUISSEINTL.get(14).drawString(setting.getName(), getX() + hAnimation, getY() + 2,
                ColorUtils.rgb(Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120));
        Scissor.unset();
        Scissor.pop();

        anim = MathUtil.fast(anim, (getWidth()) * (setting.get() - setting.min) / (setting.max - setting.min), 20);
        float sliderWidth = anim;
        double scaler = animation.getOutput();

        if (drag) {
            animation.setDirection(Direction.FORWARDS);
        } else {
            animation.setDirection(Direction.BACKWARDS);
        }

        if (drag) {
            float newValue = (mouseX - getX()) / (getWidth() - 4) * (setting.max - setting.min) + setting.min;
            setting.set((float) MathHelper.clamp(MathUtil.round(newValue, setting.increment), setting.min, setting.max));
        }

        drawRound(getX(), getY() + 10, getWidth() + 4, 7, 1.5f, new Color(Essence.getHandler().styler.clr24, Essence.getHandler().styler.clr24, Essence.getHandler().styler.clr24, 255).getRGB());
        int clr1 = Essence.getHandler().themeManager.getColor(0);
        int clr2 = ColorUtils.interpolateColor(Essence.getHandler().themeManager.getColor(180), 3, 0.5f);
        drawRound(getX() + getWidth() - 2, getY() + 11.5f, 4, 4, 2f, new Color(60, 60, 60, 50).getRGB());
        drawRound(getX(), getY() + 10, sliderWidth + 4, 7,1.5f, Essence.getHandler().getThemeManager().getColor(0));

        String text = String.valueOf(setting.get().floatValue()).replace(".0", "");
        Fonts.SUISSEINTL.get(11).drawString(text, Math.max(getX() + 5, getX() - Fonts.SUISSEINTL.get(11).getWidth(text) + sliderWidth), getY() + 13f, new Color(0, 0, 0, 255).getRGB());
    }


    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouse) {
        super.mouseClicked(mouseX, mouseY, mouse);

        if (MathUtil.isHovered(mouseX, mouseY, getX(), getY() + 8, getWidth(), 8)) {
            drag = true;
        }
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouse) {
        drag = false;

        super.mouseReleased(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}