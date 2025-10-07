package beame.module;

import beame.Essence;
import beame.components.clickgui.DropDownGui;
import beame.feature.notify.NotificationManager;
import beame.util.BindMapping;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.MathUtil;
import beame.util.other.SoundUtil;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;
import beame.setting.Render.*;
import beame.setting.SettingList.*;
import java.awt.*;
import java.util.List;
import java.util.Objects;

import static beame.util.IMinecraft.mc;
import static beame.util.color.ColorUtils.interpolateColor;

@Getter
@Setter

public class ModuleComponent extends Component {
// leaked by itskekoff; discord.gg/sk3d NkdKepqE
    public static float settingWidth;
    private final Module module;
    private DropDownGui gui;
    private boolean bind;
    private List<Component> components;
    private boolean lastHovered = false;
    public float firstColorAnimation;
    public float twoColorAnimation;
    public int hoveredAlpha = 0;

    private long hoverStartTime;
    public float hoveredAnimation = 19;
    public float hoverAnimation = 0;
    private float toggleAnimation;

    public boolean isInRegionModule = false;
    public boolean wasInRegionModule = false;
    public boolean hovered = false;



    public ModuleComponent(Module module) {
        this.module = module;
        List<Component> settingList = module.getConfigSettings().stream().map(setting -> {
            if (setting instanceof BooleanSetting bool) return new BooleanRender(bool);
            if (setting instanceof SliderSetting slider) return new SliderRender(slider);
            if (setting instanceof BindSetting bind) return new BindRender(bind);
            if (setting instanceof RadioSetting mode) return new RadioRender(mode);
            if (setting instanceof EnumSetting multiMode) return new EnumRender(multiMode);
            if (setting instanceof InputFieldSetting string) return new InputFieldRender(string);
            if (setting instanceof LabelSetting label) return new LabelRender(label);
            return null;
        }).toList();
        setComponents(settingList);
    }

    @Override
    public void render(MatrixStack stack, float mouseX, float mouseY) {
        setWidth(90);

        firstColorAnimation = AnimationMath.fast(firstColorAnimation, module.isState() ? 1 : 0, 8);
        int colordark = ColorUtils.rgba(255, 255, 255, 255);
        int color0 = interpolateColor(Essence.getHandler().themeManager.getColor(0), colordark, firstColorAnimation);
        int color1 = interpolateColor(-1, colordark, firstColorAnimation);

        int bg_0 = interpolateColor(interpolateColor(Essence.getHandler().themeManager.getColor(0), 3, 0.7f), ColorUtils.rgba(45, 45, 45, 80), firstColorAnimation);
        int bg01 = ColorUtils.rgba(45, 45, 45, 80);
        int bg_1 = interpolateColor(interpolateColor(Essence.getHandler().themeManager.getColor(180), 3, 0.5f), bg01, firstColorAnimation);


        ClientHandler.drawGradientRound(getX(), getY(), getWidth(), getHeight(), 3, bg_0, bg_0, new Color(25, 25, 25, 150).getRGB(), new Color(25, 25, 25, 150).getRGB());
        ClientHandler.drawRound(getX() + 0.5f, getY() + 0.5f, getWidth() - 1, getHeight() - 1, 3, ColorUtils.rgba(14, 14, 14, 180));

        boolean showKeyBind = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), GLFW.GLFW_KEY_LEFT_SHIFT);

        if(!components.isEmpty() && (!showKeyBind && !bind)) {
            twoColorAnimation = AnimationMath.lerp(twoColorAnimation, getHeight() > 25 ? 1.0f : 0.0f, 6);
            int twoColorAnim = interpolateColor(Essence.getHandler().themeManager.getColor(0), new Color(60, 60, 60, 255).getRGB(), twoColorAnimation);

            Fonts.ESSENCE_ICONS.get(17).drawString("f", getX() + (getWidth() - 15), getY() + 6.5f, twoColorAnim);
        }

        String bindKey = BindMapping.getReverseKey(module.getBind());

        String bindText = (bindKey.isEmpty() || bindKey == null) ? "none" : BindMapping.getReverseKey(module.getBind());
        if(showKeyBind || bind) {
            Fonts.SUISSEINTL.get(11).drawString(bindText, getX() + (getWidth() - 8) - Fonts.SUISSEINTL.get(11).getWidth(bindText), getY() + 7.5f, new Color(255, 255, 255, 100).getRGB());
        }

        updateCursor(mouseX, mouseY, getX(), getY(), 90, 15);

        int startColor = new Color(255, 255, 255, 100).getRGB();

        isInRegionModule = ClientHandler.isInRegion((int) mouseX, (int) mouseY, (int) getX(), (int) getY(), 90, 15);

        hoverAnimation = AnimationMath.lerp(hoverAnimation, isInRegionModule ? 1.0f : 0.0f, isInRegionModule ? 12 : 6);

        int animatedColor = interpolateColor(color1, startColor, hoverAnimation);

        Fonts.SUISSEINTL.get(14).drawString(showKeyBind ? module.name.length() > 12 ? module.name.substring(0, 12) + "..." : module.name : module.name, getX() + 5, getY() + 6.5f, !module.isState() ? animatedColor : color1);
        int sizeoo2 = Fonts.SF_BOLD.get(14).getStringWidth(module.name);

        if(!Objects.equals(module.  suffix, "")) {
            boolean isFt = Objects.equals(module.suffix, "FT");
            int bgcolor1 = isFt ? ColorUtils.rgba(255, 30, 30, 255) : Essence.getHandler().themeManager.getColor(0);
            int bgcolor2 = isFt ? ColorUtils.rgba(200, 90, 90, 255) : Essence.getHandler().themeManager.getColor(0);

            int textcolor = isFt ? -1 : ColorUtils.rgb(0, 0, 0);

            ClientHandler.drawGradientRound(getX() + 6 + sizeoo2 + 3.5f, getY() + 3.5f, Fonts.SUISSEINTL.get(12).getWidth(module.suffix) + 5.25f, 9, 2f, bgcolor1, bgcolor1, bgcolor2, bgcolor2);
            Fonts.SUISSEINTL.get(10).drawString(module.suffix, getX() + 6 + sizeoo2 + 3 + 3.5f, getY() + 8f, textcolor);
        }

        int add = 16;
        float y = getY() + add;

        hoveredAnimation = AnimationMath.lerp(hoveredAnimation, calculateTotalHeight() + (y - getY() - add), 8);
        setHeight(hoveredAnimation);

        Scissor.push();
        Scissor.setFromComponentCoordinates(getX(), getY(), getWidth(), getHeight());
        renderComponents(stack, mouseX, mouseY, y);
        Scissor.unset();
        Scissor.pop();


        super.render(stack, mouseX, mouseY);
    }


    private void updateCursor(float mouseX, float mouseY, float x, float y, float width, float height) {
        boolean isHovered = MathUtil.isHovered(mouseX, mouseY, x, y, width, height);
        if (isHovered && !hovered) {
            hovered = true;
            for (Component component : components) {
                if (component.isVisible()) {
                    if (Essence.getHandler().getModuleList().clientSounds.isState() && Essence.getHandler().getModuleList().getClientSounds().soundActive.get(2).get()) {
                        SoundUtil.playSound("hover.wav", 60, false);
                    }
                }
            }
        } else if (!isHovered && hovered) {
            hovered = false;
        }
    }

    private float totalHeight;

    public float calculateTotalHeight() {
        float totalHeight = 16;
        if(lastHovered) {
            for (Component component : components) {
                if (component.isVisible()) {
                    totalHeight += component.getHeight() + 10;
                }
            }
        }
        return totalHeight;
    }

    public boolean isMouseOver(float f, float f2) {
        float f3 = this.getX();
        float f4 = this.getY();
        float f5 = this.getWidth();
        float f6 = this.getHeight();
        return f >= f3 && f <= f3 + f5 && f2 >= f4 && f2 <= f4 + f6;
    }


    private void drawModuleName() {
        toggleAnimation = AnimationMath.fast(toggleAnimation, module.isState() ? 1 : 0, 8);
        int blendedColor = interpolateColor(Essence.getHandler().themeManager.getColor(0), new Color(Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120, Essence.getHandler().styler.clr120).getRGB(), toggleAnimation);
        Fonts.ICONS2.get(18).drawString("s", getX() + 4f, getY() + 6.5f, blendedColor);
        Fonts.SUISSEINTL.get(16).drawString(module.getName(), getX() + 16f, getY() + 6f, blendedColor);
    }

    private void drawBindKey() {
        String bindText = BindMapping.getReverseKey(module.getBind());
        if (module.getBind() == 0) {
            bindText = !bind ? "None" : "Binding";
        } else if (bind) {
            bindText =  "Binding";
        } else {
            bindText = BindMapping.getReverseKey(module.getBind());
        }
        if (module.getConfigSettings().isEmpty()) {
            ClientHandler.drawRound(getX() + getWidth() - Fonts.SUISSEINTL.get(16).getStringWidth(bindText) - 7 - 2, getY() + 3,  Fonts.SUISSEINTL.get(16).getStringWidth(bindText) + 4, 9, 1, new Color(Essence.getHandler().styler.clr24, Essence.getHandler().styler.clr24, Essence.getHandler().styler.clr24).getRGB());
            Fonts.SUISSEINTL.get(13).drawString(bindText, getX() + getWidth() - Fonts.SUISSEINTL.get(16).getStringWidth(bindText) - 7, getY() + 6.5f, -1);
        } else {
            ClientHandler.drawRound(getX() + getWidth() - Fonts.SUISSEINTL.get(16).getStringWidth(bindText) - 7 - 2, getY() + 3.5f,  Fonts.SUISSEINTL.get(16).getStringWidth(bindText) + 4, 9, 1, new Color(Essence.getHandler().styler.clr24, Essence.getHandler().styler.clr24, Essence.getHandler().styler.clr24).getRGB());
            Fonts.SUISSEINTL.get(13).drawString(bindText, getX() + getWidth() - Fonts.SUISSEINTL.get(16).getStringWidth(bindText) - 6, getY() + 6.5f, -1);
        }
    }

    private void renderComponents(MatrixStack stack, float mouseX, float mouseY, float y) {
        for (Component component : components) {
            if (component.isVisible()) {
                component.setX(getX() + 3);
                component.setY(y);
                component.setWidth(getWidth());
                component.setHeight(getHeight());
                component.render(stack, mouseX, mouseY);
                y += component.getHeight() + 10;
            }
        }
    }

    @Override
    public void mouseReleased(float mouseX, float mouseY, int mouseButton) {
        if(lastHovered) {
            for (Component component : components) {
                component.mouseReleased(mouseX, mouseY, mouseButton);
            }
        }
        super.mouseReleased(mouseX, mouseY, mouseButton);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        if (isHovered(mouseX, mouseY, 18)) {
            if (button == 0) {
                module.toggle();
            }
            if (button == 1) {
                if (!lastHovered) {
                    if (Essence.getHandler().getModuleList().getClientSounds().isState() && Essence.getHandler().getModuleList().getClientSounds().soundActive.get(8).get() && !module.getConfigSettings().isEmpty()) {
                        SoundUtil.playSound("opened.wav", 65, false);
                    }
                } else {
                    if (Essence.getHandler().getModuleList().getClientSounds().isState() && Essence.getHandler().getModuleList().getClientSounds().soundActive.get(8).get() && !module.getConfigSettings().isEmpty()) {
                        SoundUtil.playSound("closed.wav", 65, false);
                    }
                }
                lastHovered = !lastHovered;
            }
            if (button == 2) {
                bind = !bind;
            }
        }

        if(lastHovered) {
            for (Component component : components) {
                component.mouseClicked(mouseX, mouseY, button);
            }
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void charTyped(char codePoint, int modifiers) {
        for (Component component : components) {
            if (component.isVisible()) component.charTyped(codePoint, modifiers);
        }
        super.charTyped(codePoint, modifiers);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (Component component : components) {
            if (component.isVisible()) component.keyPressed(key, scanCode, modifiers);
        }
        if (bind) {
            if (key == GLFW.GLFW_KEY_DELETE) {
                module.setBind(0);
                Essence.getHandler().notificationManager.pushNotify("Убран бинд с функции " + module.getName(), NotificationManager.Type.Info);
            } else {
                module.setBind(key);
                Essence.getHandler().notificationManager.pushNotify("Добавлен бинд на " + BindMapping.getReverseKey(module.getBind()) + " для функции " + module.getName(), NotificationManager.Type.Info);
            }
            bind = false;
        }
        super.keyPressed(key, scanCode, modifiers);
    }
}
