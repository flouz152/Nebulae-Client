package beame.setting.Render;

import beame.Essence;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.MathUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.module.Component;
import beame.setting.SettingList.LabelSetting;

public class LabelRender extends Component {
// leaked by itskekoff; discord.gg/sk3d J99JoPmG
    private final LabelSetting setting;
    public boolean drag = false;

    public LabelRender(LabelSetting setting) {
        this.setting = setting;
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        setWidth(67);
        setHeight(0);

        Fonts.SUISSEINTL.get(14).drawString(setting.getName(), getX() + 2, getY() + 1, ColorUtils.rgb(Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120));
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouse) {
        super.mouseClicked(mouseX, mouseY, mouse);

        if (MathUtil.isHovered(mouseX, mouseY, getX(), getY() + 9 + 2, getWidth() + 4, 5)) {
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
