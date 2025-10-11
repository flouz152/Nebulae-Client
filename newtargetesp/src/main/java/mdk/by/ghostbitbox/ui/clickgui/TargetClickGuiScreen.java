package mdk.by.ghostbitbox.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import mdk.by.ghostbitbox.TargetEspAddon;
import mdk.by.ghostbitbox.TargetEspConfig;
import mdk.by.ghostbitbox.modules.render.targetesp.TargetEspMode;
import mdk.by.ghostbitbox.ui.clickgui.component.CycleComponent;
import mdk.by.ghostbitbox.ui.clickgui.component.HeadingComponent;
import mdk.by.ghostbitbox.ui.clickgui.component.SettingComponent;
import mdk.by.ghostbitbox.ui.clickgui.component.SliderComponent;
import mdk.by.ghostbitbox.ui.clickgui.component.ToggleComponent;
import mdk.by.ghostbitbox.util.ColorUtil;
import mdk.by.ghostbitbox.util.HudRenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

public class TargetClickGuiScreen extends Screen {

    private static final float PANEL_WIDTH = 156.0f;
    private static final float PANEL_HEIGHT = 286.0f;
    private static final float PANEL_GAP = 12.0f;
    private static final float PANEL_HEADER_HEIGHT = 28.0f;
    private static final float MODULE_SPACING = 8.0f;
    private static final float MODULE_HEADER_HEIGHT = 34.0f;
    private static final float SETTINGS_SPACING = 6.0f;

    private final TargetEspAddon addon;
    private final List<Panel> panels = new ArrayList<>();
    private int accentColor;

    public TargetClickGuiScreen(TargetEspAddon addon) {
        super(new StringTextComponent("Target Settings"));
        this.addon = addon;
    }

    @Override
    protected void init() {
        panels.clear();
        buildPanels();
    }

    private void buildPanels() {
        TargetEspConfig config = addon.configuration();
        Runnable save = addon::persistConfiguration;

        ModuleCard espModule = new ModuleCard(
                "Target ESP",
                "Animated outlines and orbiting ghosts",
                config::isEnabled,
                value -> {
                    addon.setEnabled(value);
                    addon.persistConfiguration();
                },
                buildTargetEspSettings(config, save)
        );

        ModuleCard hudModule = new ModuleCard(
                "Target HUD",
                "Draggable info panel for your target",
                config::isTargetHudEnabled,
                value -> {
                    config.setTargetHudEnabled(value);
                    addon.persistConfiguration();
                },
                buildTargetHudSettings(config, save)
        );

        Map<Category, List<ModuleCard>> modulesByCategory = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            modulesByCategory.put(category, new ArrayList<>());
        }
        modulesByCategory.get(Category.VISUALS).add(espModule);
        modulesByCategory.get(Category.HUD).add(hudModule);

        int index = 0;
        for (Category category : Category.values()) {
            List<ModuleCard> modules = modulesByCategory.get(category);
            if (modules.isEmpty()) {
                continue;
            }
            panels.add(new Panel(category, index++, modules));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        accentColor = computeAccentColor();
        SettingComponent.setThemeColor(accentColor);

        float totalWidth = panels.size() * PANEL_WIDTH + Math.max(0, panels.size() - 1) * PANEL_GAP;
        float startX = (this.width - totalWidth) / 2.0f;
        float panelY = (this.height - PANEL_HEIGHT) / 2.0f;

        for (int i = 0; i < panels.size(); i++) {
            Panel panel = panels.get(i);
            float x = startX + i * (PANEL_WIDTH + PANEL_GAP);
            panel.render(matrixStack, x, panelY, PANEL_WIDTH, PANEL_HEIGHT, mouseX, mouseY, partialTicks);
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            if (panel.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (Panel panel : panels) {
            if (panel.mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (Panel panel : panels) {
            if (panel.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                return true;
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        boolean handled = false;
        for (Panel panel : panels) {
            handled |= panel.handleScroll(mouseX, mouseY, delta);
        }
        return handled || super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (Panel panel : panels) {
            if (panel.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        for (Panel panel : panels) {
            if (panel.charTyped(codePoint, modifiers)) {
                return true;
            }
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        addon.persistConfiguration();
        super.onClose();
    }

    private int computeAccentColor() {
        int base = addon.configuration().getBaseColor();
        int mix = ColorUtil.interpolate(base, ColorUtil.rgba(255, 255, 255, 255), 0.25f);
        return ColorUtil.setAlpha(mix, 255);
    }

    private List<SettingComponent> buildTargetEspSettings(TargetEspConfig config, Runnable save) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("General"));
        list.add(new ToggleComponent("Enable", config::isEnabled, value -> {
            addon.setEnabled(value);
            addon.persistConfiguration();
        }, save));
        list.add(new CycleComponent<>("Mode", TargetEspMode.values(), config::getMode, addon::setMode,
                TargetEspMode::getDisplayName, save));

        list.add(new HeadingComponent("Base color"));
        list.add(createColorSlider("Red", config::getBaseColorRed,
                value -> config.setBaseColor(Math.round(value), config.getBaseColorGreen(), config.getBaseColorBlue()), save));
        list.add(createColorSlider("Green", config::getBaseColorGreen,
                value -> config.setBaseColor(config.getBaseColorRed(), Math.round(value), config.getBaseColorBlue()), save));
        list.add(createColorSlider("Blue", config::getBaseColorBlue,
                value -> config.setBaseColor(config.getBaseColorRed(), config.getBaseColorGreen(), Math.round(value)), save));

        list.add(new HeadingComponent("Hurt tint"));
        list.add(new ToggleComponent("Tint when hurt", config::isHurtTintEnabled, config::setHurtTintEnabled, save));
        list.add(createColorSlider("Hurt red", config::getHurtColorRed,
                value -> config.setHurtColor(Math.round(value), config.getHurtColorGreen(), config.getHurtColorBlue()), save));
        list.add(createColorSlider("Hurt green", config::getHurtColorGreen,
                value -> config.setHurtColor(config.getHurtColorRed(), Math.round(value), config.getHurtColorBlue()), save));
        list.add(createColorSlider("Hurt blue", config::getHurtColorBlue,
                value -> config.setHurtColor(config.getHurtColorRed(), config.getHurtColorGreen(), Math.round(value)), save));

        list.add(new HeadingComponent("Ghosts"));
        list.add(new SliderComponent("Speed", 5.0f, 100.0f, 1.0f, config::getGhostSpeed, config::setGhostSpeed,
                this::formatFloat, save));
        list.add(new SliderComponent("Count", 5.0f, 64.0f, 1.0f,
                () -> (float) config.getGhostLength(), value -> config.setGhostLength(Math.round(value)), this::formatInt, save));
        list.add(new SliderComponent("Width", 0.1f, 1.5f, 0.01f, config::getGhostWidth, config::setGhostWidth,
                this::formatFloat, save));
        list.add(new SliderComponent("Orbit radius", 0.2f, 2.0f, 0.01f, config::getGhostRadius, config::setGhostRadius,
                this::formatFloat, save));
        list.add(new SliderComponent("Angle step", 0.01f, 1.0f, 0.01f, config::getGhostAngle, config::setGhostAngle,
                this::formatFloat, save));
        list.add(new SliderComponent("Spacing", 4.0f, 30.0f, 0.5f, config::getGhostSpacing, config::setGhostSpacing,
                this::formatFloat, save));
        list.add(new SliderComponent("Height offset", -2.0f, 2.0f, 0.05f, config::getGhostHeightOffset,
                config::setGhostHeightOffset, this::formatFloat, save));

        list.add(new HeadingComponent("Circle"));
        list.add(new SliderComponent("Duration", 200.0f, 10000.0f, 10.0f,
                () -> (float) config.getCircleDuration(), value -> config.setCircleDuration(value), this::formatFloat, save));
        list.add(new SliderComponent("Radius multiplier", 0.2f, 2.0f, 0.01f, config::getCircleRadius, config::setCircleRadius,
                this::formatFloat, save));

        list.add(new HeadingComponent("HUD mode"));
        list.add(new SliderComponent("First-person size", 20.0f, 140.0f, 1.0f, config::getHudSizeFirstPerson,
                config::setHudSizeFirstPerson, this::formatFloat, save));
        list.add(new SliderComponent("Third-person size", 20.0f, 140.0f, 1.0f, config::getHudSizeThirdPerson,
                config::setHudSizeThirdPerson, this::formatFloat, save));

        return list;
    }

    private List<SettingComponent> buildTargetHudSettings(TargetEspConfig config, Runnable save) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("Visibility"));
        list.add(new ToggleComponent("Enable HUD", config::isTargetHudEnabled, config::setTargetHudEnabled, save));
        list.add(new ToggleComponent("Show equipment", config::isTargetHudShowEquipment, config::setTargetHudShowEquipment, save));
        list.add(new ToggleComponent("Show offhand", config::isTargetHudShowOffhand, config::setTargetHudShowOffhand, save));
        list.add(new ToggleComponent("Show health text", config::isTargetHudShowHealthText, config::setTargetHudShowHealthText, save));

        list.add(new HeadingComponent("Layout"));
        list.add(new SliderComponent("Anchor X", 0.05f, 0.95f, 0.01f, config::getTargetHudAnchorX, config::setTargetHudAnchorX,
                this::formatFloat, save));
        list.add(new SliderComponent("Anchor Y", 0.05f, 0.95f, 0.01f, config::getTargetHudAnchorY, config::setTargetHudAnchorY,
                this::formatFloat, save));
        list.add(new SliderComponent("Width", 80.0f, 220.0f, 1.0f, config::getTargetHudWidth, config::setTargetHudWidth,
                this::formatFloat, save));
        list.add(new SliderComponent("Height", 30.0f, 140.0f, 1.0f, config::getTargetHudHeight, config::setTargetHudHeight,
                this::formatFloat, save));
        list.add(new SliderComponent("Bar height", 3.0f, 16.0f, 0.5f, config::getTargetHudBarHeight, config::setTargetHudBarHeight,
                this::formatFloat, save));
        list.add(new SliderComponent("Item scale", 0.5f, 1.2f, 0.01f, config::getTargetHudItemScale, config::setTargetHudItemScale,
                this::formatFloat, save));

        list.add(new HeadingComponent("Background color"));
        list.add(createColorSlider("Red", () -> (float) config.getTargetHudBackgroundRed(),
                value -> config.setTargetHudBackgroundColor(Math.round(value), config.getTargetHudBackgroundGreen(),
                        config.getTargetHudBackgroundBlue(), config.getTargetHudBackgroundAlpha()), save));
        list.add(createColorSlider("Green", () -> (float) config.getTargetHudBackgroundGreen(),
                value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), Math.round(value),
                        config.getTargetHudBackgroundBlue(), config.getTargetHudBackgroundAlpha()), save));
        list.add(createColorSlider("Blue", () -> (float) config.getTargetHudBackgroundBlue(),
                value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), config.getTargetHudBackgroundGreen(),
                        Math.round(value), config.getTargetHudBackgroundAlpha()), save));
        list.add(createColorSlider("Alpha", () -> (float) config.getTargetHudBackgroundAlpha(),
                value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), config.getTargetHudBackgroundGreen(),
                        config.getTargetHudBackgroundBlue(), Math.round(value)), save));

        list.add(new HeadingComponent("Outline color"));
        list.add(createColorSlider("Red", () -> (float) config.getTargetHudOutlineRed(),
                value -> config.setTargetHudOutlineColor(Math.round(value), config.getTargetHudOutlineGreen(),
                        config.getTargetHudOutlineBlue(), config.getTargetHudOutlineAlpha()), save));
        list.add(createColorSlider("Green", () -> (float) config.getTargetHudOutlineGreen(),
                value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), Math.round(value),
                        config.getTargetHudOutlineBlue(), config.getTargetHudOutlineAlpha()), save));
        list.add(createColorSlider("Blue", () -> (float) config.getTargetHudOutlineBlue(),
                value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), config.getTargetHudOutlineGreen(),
                        Math.round(value), config.getTargetHudOutlineAlpha()), save));
        list.add(createColorSlider("Alpha", () -> (float) config.getTargetHudOutlineAlpha(),
                value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), config.getTargetHudOutlineGreen(),
                        config.getTargetHudOutlineBlue(), Math.round(value)), save));

        list.add(new HeadingComponent("Text color"));
        list.add(createColorSlider("Red", () -> (float) config.getTargetHudTextRed(),
                value -> config.setTargetHudTextColor(Math.round(value), config.getTargetHudTextGreen(),
                        config.getTargetHudTextBlue(), config.getTargetHudTextAlpha()), save));
        list.add(createColorSlider("Green", () -> (float) config.getTargetHudTextGreen(),
                value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), Math.round(value),
                        config.getTargetHudTextBlue(), config.getTargetHudTextAlpha()), save));
        list.add(createColorSlider("Blue", () -> (float) config.getTargetHudTextBlue(),
                value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), config.getTargetHudTextGreen(),
                        Math.round(value), config.getTargetHudTextAlpha()), save));
        list.add(createColorSlider("Alpha", () -> (float) config.getTargetHudTextAlpha(),
                value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), config.getTargetHudTextGreen(),
                        config.getTargetHudTextBlue(), Math.round(value)), save));

        return list;
    }

    private SliderComponent createColorSlider(String label, Supplier<Float> getter, Consumer<Float> setter, Runnable save) {
        return new SliderComponent(label, 0.0f, 255.0f, 1.0f, getter, setter, this::formatInt, save);
    }

    private String formatFloat(float value) {
        return String.format("%.2f", value);
    }

    private String formatInt(float value) {
        return Integer.toString(Math.round(value));
    }

    private enum Category {
        VISUALS("Visuals"),
        HUD("Target HUD");

        private final String display;

        Category(String display) {
            this.display = display;
        }

        public String display() {
            return display;
        }
    }

    private final class Panel {

        private final Category category;
        private final int index;
        private final List<ModuleCard> modules;
        private float x;
        private float y;
        private float width;
        private float height;
        private float scrollTarget;
        private float scroll;

        private Panel(Category category, int index, List<ModuleCard> modules) {
            this.category = category;
            this.index = index;
            this.modules = modules;
        }

        private void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            FontRenderer font = Minecraft.getInstance().fontRenderer;
            HudRenderUtil.drawBorderedRoundedRect(stack, x, y, width, height, 6.0f, 0x3310151B, 0x551F252F, 1.0f);

            float headerX = x + 10.0f;
            float headerY = y + 8.0f;
            font.drawStringWithShadow(stack, category.display(), headerX, headerY, 0xFFEFF2F8);

            float contentX = x + 8.0f;
            float contentY = y + PANEL_HEADER_HEIGHT;
            float contentWidth = width - 16.0f;
            float contentHeight = height - PANEL_HEADER_HEIGHT - 10.0f;

            float totalHeight = 0.0f;
            for (ModuleCard module : modules) {
                totalHeight += module.getHeight(contentWidth) + MODULE_SPACING;
            }
            totalHeight = Math.max(0.0f, totalHeight - MODULE_SPACING);

            if (totalHeight > contentHeight) {
                float minScroll = -(totalHeight - contentHeight);
                scrollTarget = MathHelper.clamp(scrollTarget, minScroll - 12.0f, 0.0f);
            } else {
                scrollTarget = 0.0f;
            }

            scroll = MathHelper.lerp(0.25f, scroll, scrollTarget);

            enableScissor(contentX, contentY, contentWidth, contentHeight);
            float currentY = contentY + scroll;
            for (ModuleCard module : modules) {
                currentY += module.render(stack, contentX, currentY, contentWidth, mouseX, mouseY, partialTicks) + MODULE_SPACING;
            }
            disableScissor();
        }

        private boolean handleScroll(double mouseX, double mouseY, double delta) {
            float contentX = x + 8.0f;
            float contentY = y + PANEL_HEADER_HEIGHT;
            float contentWidth = width - 16.0f;
            float contentHeight = height - PANEL_HEADER_HEIGHT - 10.0f;
            if (mouseX < contentX || mouseX > contentX + contentWidth || mouseY < contentY || mouseY > contentY + contentHeight) {
                return false;
            }

            float totalHeight = 0.0f;
            for (ModuleCard module : modules) {
                totalHeight += module.getHeight(contentWidth) + MODULE_SPACING;
            }
            totalHeight = Math.max(0.0f, totalHeight - MODULE_SPACING);
            if (totalHeight <= contentHeight) {
                scrollTarget = 0.0f;
                return false;
            }

            float minScroll = -(totalHeight - contentHeight);
            scrollTarget = MathHelper.clamp(scrollTarget + (float) (delta * 10.0f), minScroll - 12.0f, 8.0f);
            return true;
        }

        private boolean mouseClicked(double mouseX, double mouseY, int button) {
            for (ModuleCard module : modules) {
                if (module.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return false;
        }

        private boolean mouseReleased(double mouseX, double mouseY, int button) {
            for (ModuleCard module : modules) {
                if (module.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return false;
        }

        private boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            for (ModuleCard module : modules) {
                if (module.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                    return true;
                }
            }
            return false;
        }

        private boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            for (ModuleCard module : modules) {
                if (module.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
            return false;
        }

        private boolean charTyped(char codePoint, int modifiers) {
            for (ModuleCard module : modules) {
                if (module.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
            return false;
        }
    }

    private final class ModuleCard {

        private final String title;
        private final String description;
        private final Supplier<Boolean> enabledGetter;
        private final Consumer<Boolean> enabledSetter;
        private final List<SettingComponent> settings;
        private boolean expanded = true;
        private float lastHeight;
        private float x;
        private float y;
        private float width;

        private ModuleCard(String title, String description, Supplier<Boolean> enabledGetter,
                            Consumer<Boolean> enabledSetter, List<SettingComponent> settings) {
            this.title = title;
            this.description = description;
            this.enabledGetter = enabledGetter;
            this.enabledSetter = enabledSetter;
            this.settings = settings;
        }

        private float render(MatrixStack stack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
            this.x = x;
            this.y = y;
            this.width = width;

            boolean enabled = enabledGetter.get();
            float totalHeight = getHeight(width);
            lastHeight = totalHeight;

            int headerColor = enabled ? ColorUtil.setAlpha(accentColor, 60) : 0x22161A22;
            HudRenderUtil.drawBorderedRoundedRect(stack, x, y, width, totalHeight, 6.0f, 0x4410141A, 0x551E232C, 1.0f);
            HudRenderUtil.drawRoundedRect(stack, x + 2.0f, y + 2.0f, width - 4.0f, MODULE_HEADER_HEIGHT - 4.0f, 4.0f, headerColor);

            FontRenderer font = Minecraft.getInstance().fontRenderer;
            font.drawStringWithShadow(stack, title, x + 10.0f, y + 8.0f, enabled ? 0xFFEEF3FA : 0xFFC5CBD7);
            font.drawString(stack, description, x + 10.0f, y + 18.0f, 0xFFA5ABB6);

            float toggleRadius = 5.5f;
            float toggleX = x + width - 20.0f;
            float toggleY = y + 11.0f;
            int toggleColor = enabled ? accentColor : 0xFF5A606C;
            HudRenderUtil.drawRoundedRect(stack, toggleX, toggleY, toggleRadius * 2.0f, toggleRadius * 2.0f, toggleRadius, toggleColor);

            float arrowX = x + width - 16.0f;
            float arrowY = y + MODULE_HEADER_HEIGHT - 10.0f;
            drawArrow(stack, arrowX, arrowY, expanded);

            float contentY = y + MODULE_HEADER_HEIGHT;
            if (expanded && !settings.isEmpty()) {
                contentY += 4.0f;
                for (SettingComponent component : settings) {
                    component.render(stack, x + 8.0f, contentY, width - 16.0f, mouseX, mouseY, partialTicks);
                    contentY += component.getHeight() + SETTINGS_SPACING;
                }
            } else {
                for (SettingComponent component : settings) {
                    component.hide();
                }
            }

            return totalHeight;
        }

        private boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isHeaderHovered(mouseX, mouseY)) {
                if (button == 0) {
                    boolean newState = !enabledGetter.get();
                    enabledSetter.accept(newState);
                    return true;
                }
                if (button == 1) {
                    expanded = !expanded;
                    return true;
                }
            }

            if (!expanded) {
                return false;
            }

            for (SettingComponent component : settings) {
                if (component.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return false;
        }

        private boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (!expanded) {
                return false;
            }
            for (SettingComponent component : settings) {
                if (component.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return false;
        }

        private boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (!expanded) {
                return false;
            }
            for (SettingComponent component : settings) {
                if (component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                    return true;
                }
            }
            return false;
        }

        private boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            if (!expanded) {
                return false;
            }
            for (SettingComponent component : settings) {
                if (component.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
            return false;
        }

        private boolean charTyped(char codePoint, int modifiers) {
            if (!expanded) {
                return false;
            }
            for (SettingComponent component : settings) {
                if (component.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
            return false;
        }

        private float getHeight(float width) {
            float height = MODULE_HEADER_HEIGHT;
            if (expanded && !settings.isEmpty()) {
                float content = 0.0f;
                for (SettingComponent component : settings) {
                    content += component.getHeight() + SETTINGS_SPACING;
                }
                content = Math.max(0.0f, content - SETTINGS_SPACING);
                height += 6.0f + content;
            }
            return height + 4.0f;
        }

        private boolean isHeaderHovered(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + MODULE_HEADER_HEIGHT;
        }

        private void drawArrow(MatrixStack stack, float x, float y, boolean open) {
            float size = 6.0f;
            float startX = x;
            float startY = y;
            float midX = x + size;

            HudRenderUtil.drawRoundedRect(stack, startX, startY, size, 1.5f, 0.75f, 0xFF5F6673);
            if (open) {
                HudRenderUtil.drawRoundedRect(stack, startX, startY, 1.5f, size, 0.75f, 0xFF5F6673);
            } else {
                HudRenderUtil.drawRoundedRect(stack, midX - 1.5f, startY, 1.5f, size, 0.75f, 0xFF5F6673);
            }
        }
    }

    private void enableScissor(float x, float y, float width, float height) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getMainWindow() == null) {
            return;
        }
        double scale = minecraft.getMainWindow().getGuiScaleFactor();
        int scissorX = (int) (x * scale);
        int scissorY = (int) ((minecraft.getMainWindow().getScaledHeight() - (y + height)) * scale);
        int scissorW = (int) (width * scale);
        int scissorH = (int) (height * scale);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorX, scissorY, scissorW, scissorH);
    }

    private void disableScissor() {
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
