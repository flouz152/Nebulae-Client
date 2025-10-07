package beame.setting.Render;

import beame.Nebulae;
import beame.feature.notify.NotificationManager;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.fonts.Fonts;
import beame.util.other.SoundUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import beame.util.color.ColorUtils;
import beame.util.render.ClientHandler;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import beame.module.Component;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;
import beame.setting.SettingList.InputFieldSetting;

import java.awt.*;

import static beame.util.IMinecraft.mc;
import static beame.util.color.ColorUtils.interpolateColor;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class InputFieldRender extends Component {
// leaked by itskekoff; discord.gg/sk3d xXw4rmri


    final InputFieldSetting setting;
    boolean fieldTyping;
    private String fieldText = "";

    public float copyAnimation = 0;
    public float firstColorAnimation;
    public boolean isInRegionModule = false;

    public InputFieldRender(InputFieldSetting setting) {
        this.setting = setting;
    }

    public void render(MatrixStack stack, float mouseX, float mouseY) {
        super.render(stack, mouseX, mouseY);
        fieldText = setting.get();
        ClientHandler.drawRound(getX() + 1, getY() + 9f, getWidth() - 8f, 13f, 2, new Color(26, 26, 26, 100).getRGB());
        Fonts.SUISSEINTL.get(14).drawString(setting.getName(), getX(), getY() + 2f, ColorUtils.rgb(Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120));

        isInRegionModule = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) (getX() + 71.5f), (int) (getY() + 12f), 7, 6);

        firstColorAnimation = AnimationMath.fast(firstColorAnimation, isInRegionModule ? 1 : 0, 8);

        copyAnimation = AnimationMath.lerp(copyAnimation, isInRegionModule ? 1.0f : 0.0f, isInRegionModule ? 12 : 6);

        int startColor = Nebulae.getHandler().getThemeManager().getThemeColor(0);

        int themeColor = Nebulae.getHandler().getThemeManager().getThemeColor(0);

        int r = (themeColor >> 16) & 0xFF;
        int g = (themeColor >> 8) & 0xFF;
        int b = themeColor & 0xFF;

        float darkenFactor = 0.6f;
        r *= darkenFactor;
        g *= darkenFactor;
        b *= darkenFactor;

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        int colordark = new Color(r, g, b, 225).getRGB();

        int animatedColor = interpolateColor(colordark, startColor, copyAnimation);


        Fonts.ESSENCE_ICONS.get(14f).drawString(stack, "s", getX() + 71.5f, getY() + 11.5f, animatedColor, 50);

        Scissor.push();
        Scissor.setFromComponentCoordinates(getX() - 15, getY() + 7f, getWidth() - 5.5f, 12f);
        if (!fieldTyping && fieldText.isEmpty()) {
            Fonts.SUISSEINTL.get(14f).drawString(stack, "Введите текст...", getX() + 2.5f, getY() + 14f, ColorUtils.rgba(Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120, 50));
        } else {
            Fonts.SUISSEINTL.get(14).drawString(stack, fieldText + (fieldTyping ? (System.currentTimeMillis() % 1000 > 500 ? "_" : "") : ""), getX() + 2.5f, getY() + 14f, ColorUtils.rgba(Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120, Nebulae.getHandler().styler.clr120, 255));
        }

        Scissor.unset();
        Scissor.pop();

        setHeight(17);
    }


    @Override
    public void charTyped(char codePoint, int modifiers) {
        super.charTyped(codePoint, modifiers);
        if (fieldTyping && fieldText.length() < 14) {
            fieldText += codePoint;
            setting.set(fieldText);
        }
    }


    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouse) {
        if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) getX(), (int) (getY() + 7f), (int) (getWidth() - 21f), (int) 12f)) {
            fieldTyping = !fieldTyping;
        }

        if (ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) (getX() + 71.5f), (int) (getY() + 12f), 7, 6)) {
            copyFromClipboard();
        }
        super.mouseReleased(mouseX, mouseY, mouse);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (fieldTyping) {
            if (key == GLFW.GLFW_KEY_V && GLFW.glfwGetKey(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW_PRESS) {
                pasteFromClipboard();
            }

            if (Nebulae.getHandler().getModuleList().getClientSounds().isState() && Nebulae.getHandler().getModuleList().getClientSounds().soundActive.get(1).get()) {
                SoundUtil.playSound("writing.wav", Nebulae.getHandler().getModuleList().getClientSounds().volume.get(), false);
            }

            if (key == GLFW.GLFW_KEY_BACKSPACE) {
                if (!fieldText.isEmpty()) {
                    fieldText = fieldText.substring(0, fieldText.length() - 1);
                    setting.set(fieldText);
                }
            }
            if (key == GLFW.GLFW_KEY_ENTER) {
                fieldTyping = false;
            }
        }

        super.keyPressed(key, scanCode, modifiers);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int mouse) {
        super.mouseClicked(mouseX, mouseY, mouse);
    }

    private void pasteFromClipboard() {
        try {
            fieldText += GLFW.glfwGetClipboardString(Minecraft.getInstance().getMainWindow().getHandle());
            setting.set(fieldText);
            Nebulae.getHandler().notificationManager.pushNotify("Текст успешно вставлен в поле!", NotificationManager.Type.Info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void copyFromClipboard() {
        try {
            if (!fieldText.isEmpty()) {
                GLFW.glfwSetClipboardString(Minecraft.getInstance().getMainWindow().getHandle(), fieldText);
                Nebulae.getHandler().notificationManager.pushNotify("Текст поля успешно скопирован: " + fieldText + "!", NotificationManager.Type.Info);
            } else {
                Nebulae.getHandler().notificationManager.pushNotify("Текст поля не может быть пустым для копирования!" , NotificationManager.Type.Info);
            }
            System.err.println(fieldText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isVisible() {
        return setting.visible.get();
    }

}
