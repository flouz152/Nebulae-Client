package beame.setting.Render;

import beame.Nebulae;
import beame.feature.notify.NotificationManager;
import beame.util.BindMapping;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.module.Component;
import org.lwjgl.glfw.GLFW;
import beame.setting.SettingList.BindSetting;

import java.awt.*;

public class BindRender extends Component {
// leaked by itskekoff; discord.gg/sk3d M8iw8pdO

    final BindSetting setting;
    private boolean lastHovered;

    public float hAnimation;

    public BindRender(BindSetting setting) {
        this.setting = setting;

    }
    int x,y, width, height;
    boolean activated;
    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        setHeight(2);

        String bind;
        if (activated) {
            bind = "...";
        } else {
            bind = BindMapping.getKey(setting.get());
            if (bind == null || setting.get() == -1 || bind.isEmpty()) {
                bind = "none";
            }
        }

        x = (int) (getX() + getWidth() - width - 7);
        y = (int) (getY());
        width = (int) (Fonts.SUISSEINTL.get(11).getStringWidth(bind) + 5);
        height = 10;

        boolean hovered = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) (getX()), (int) (getY() + 3f), 90 - 20, 10);

        this.hAnimation = AnimationMath.fast(hAnimation, (Fonts.SUISSEINTL.get(14).getStringWidth(setting.getName()) > 90 - 24) ?
                (hovered ? -((float) Fonts.SUISSEINTL.get(14).getStringWidth(setting.getName()) / 2 - 15) : 0) : 0, 1);

        Scissor.push();
        Scissor.setFromComponentCoordinates((getX()), (getY()), 90 - 20, 10);
        Fonts.SUISSEINTL.get(14).drawString(setting.getName(), getX() + hAnimation, getY() + 3f, ColorUtils.getColor(90, 90, 90, 255));
        Scissor.unset();
        Scissor.pop();

        ClientHandler.drawSexyRect(x, y + 1, width, height - 2, 2, false);
        Fonts.SUISSEINTL.get(11).drawString(bind, x + 1.8f, y + 4.5f, new Color(255, 255, 255, 100).getRGB());
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        super.keyPressed(key, scanCode, modifiers);
        if (activated) {
            if (key == GLFW.GLFW_KEY_DELETE) {
                Nebulae.getHandler().notificationManager.pushNotify("Функциональный бинд убран.", NotificationManager.Type.Info);
                setting.set(-1);
                activated = false;
                return;
            }
            Nebulae.getHandler().notificationManager.pushNotify(String.format("Функциональный бинд поставлен на кнопку: %s", BindMapping.getReverseKey(key)), NotificationManager.Type.Info);
            setting.set(key);
            activated = false;
        }
    }


    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouse) {
        String bind = BindMapping.getKey(setting.get());
        if (bind == null || setting.get() == -1 || bind.isEmpty()) {
            bind = "none";
        }
        width = (Fonts.SUISSEINTL.get(14).getStringWidth(bind) + 2);
        x = (int) (getX() + getWidth() - width - 7);
        y = (int) (getY() + 1);
        height = 10;


        if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, x,y,width,height) && mouse == 0) {
            activated = !activated;
        }

        if (activated && mouse >= 1) {
            System.out.println(-100 + mouse);
            setting.set(-100 + mouse);
            activated = false;
        }
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouse) {
        super.mouseReleased(mouseX, mouseY, mouse);
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }
}
