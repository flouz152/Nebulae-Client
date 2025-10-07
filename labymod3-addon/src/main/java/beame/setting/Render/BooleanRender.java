package beame.setting.Render;

import beame.Essence;
import beame.util.Scissor;
import beame.util.animation.Animation;
import beame.util.animation.AnimationMath;
import beame.util.animation.impl.EaseBackIn;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.module.Component;
import beame.module.ModuleComponent;
import beame.setting.SettingList.BooleanSetting;

import static beame.util.color.ColorUtils.interpolateColor;

public class BooleanRender extends Component {
// leaked by itskekoff; discord.gg/sk3d gVkQqTis

    private final BooleanSetting setting;
    public float enabledAnimation;
    public Animation animation;
    public float hAnimation;
    public boolean openB;
    public float hAnimationB;
    public float x, y, width, height;
    private static BooleanRender currentOpen = null;
    boolean activated;

    public BooleanRender(BooleanSetting setting) {
        this.setting = setting;
        this.animation = new EaseBackIn(250, 1, 0.1f);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        double scale = animation.getOutput();

        setY(getY() + 1);

        boolean hovered = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int)(getX()+11), (int) (getY()), 90-20, 10);

        this.hAnimation = AnimationMath.fast(hAnimation, (Fonts.SUISSEINTL.get(14).getStringWidth(setting.getName())>90-24) ? (hovered ? -((float) Fonts.SUISSEINTL.get(14).getStringWidth(setting.getName()) /2 - 15) : 0) : 0, 8);
        Scissor.push();
        Scissor.setFromComponentCoordinates((getX()+11), (getY()), 90-20, 10);
        Fonts.SUISSEINTL.get(14).drawString(setting.getName(), getX() + 11 + hAnimation, getY() + 2.5f, ColorUtils.rgb(Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120));
        Scissor.unset();
        Scissor.pop();

        setWidth(10);
        setHeight(3);
        this.enabledAnimation = AnimationMath.fast(enabledAnimation, setting.get() ? 1 : 0, 8);

        int blendedColor = interpolateColor(Essence.getHandler().themeManager.getColor(0), ColorUtils.rgba(Essence.getHandler().styler.clr30, Essence.getHandler().styler.clr30, Essence.getHandler().styler.clr30, 255), enabledAnimation);
        int blendedColor2 = interpolateColor(ColorUtils.rgba(0, 0, 0, 255), ColorUtils.setAlpha(ColorUtils.rgba(0, 0, 0, 255), 0), enabledAnimation);

        ClientHandler.drawRound(getX() + 1f, getY() + 0.5f - 1, 9, 9, 2, blendedColor);
        Fonts.ESSENCE_ICONS.get(12).drawString("x", getX() + 1.3f, getY() + 4f, blendedColor2);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        super.keyPressed(key, scanCode, modifiers);
    }


    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouse) {
        super.mouseReleased(mouseX, mouseY, mouse);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouse) {
        super.mouseClicked(mouseX, mouseY, mouse);
        boolean hovered = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) (getX() + 3), (int) (getY()) - 1, 10, 10);

        if (hovered && mouse == 0) {
            setting.set(!setting.get());
        }

        boolean hoveredBS = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) (getX() + ModuleComponent.settingWidth - getWidth() - 17), (int) (getY() + 1), 7, 7);
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
