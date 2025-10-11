package mdk.by.ghostbitbox.ui.clickgui;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class TargetClickGuiScreen extends Screen {

    private static final float PANEL_WIDTH = 140.0f;
    private static final float PANEL_HEIGHT = 290.0f;
    private static final float PANEL_GAP = 12.0f;
    private static final float PANEL_PADDING = 10.0f;
    private static final float MODULE_SPACING = 6.0f;

    private final TargetEspAddon addon;
    private final List<Panel> panels = new ArrayList<>();

    public TargetClickGuiScreen(TargetEspAddon addon) {
        super(new StringTextComponent("Target GUI"));
        this.addon = addon;
    }

    @Override
    protected void init() {
        panels.clear();
        buildPanels();
    }

    private void buildPanels() {
        TargetEspConfig config = addon.configuration();
        Runnable saveAction = addon::persistConfiguration;

        ModuleDescriptor espModule = new ModuleDescriptor(
                "Target ESP",
                "Target highlighting",
                config::isEnabled,
                value -> {
                    addon.setEnabled(value);
                    addon.persistConfiguration();
                },
                createTargetEspSettings(config, saveAction)
        );

        ModuleDescriptor hudModule = new ModuleDescriptor(
                "Target HUD",
                "Target information display",
                config::isTargetHudEnabled,
                value -> {
                    config.setTargetHudEnabled(value);
                    addon.persistConfiguration();
                },
                createTargetHudSettings(config, saveAction)
        );

        Map<Category, List<ModuleDescriptor>> modulesByCategory = new EnumMap<>(Category.class);
        for (Category category : Category.values()) {
            modulesByCategory.put(category, new ArrayList<>());
        }

        modulesByCategory.get(Category.RENDER).add(espModule);
        modulesByCategory.get(Category.HUD).add(hudModule);

        int index = 0;
        for (Category category : Category.values()) {
            List<ModuleDescriptor> modules = modulesByCategory.get(category);
            if (modules.isEmpty()) {
                continue;
            }
            panels.add(new Panel(category, index++, modules));
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

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
        for (Panel panel : panels) {
            if (panel.handleScroll(mouseX, mouseY, delta)) {
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
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

    private List<SettingComponent> createTargetEspSettings(TargetEspConfig config, Runnable saveAction) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("General"));
        list.add(new ToggleComponent("Enable", config::isEnabled, value -> {
            addon.setEnabled(value);
        }, saveAction));
        list.add(new CycleComponent<>("Mode", TargetEspMode.values(), config::getMode, addon::setMode, TargetEspMode::getDisplayName, saveAction));

        list.add(new HeadingComponent("Base color"));
        list.add(createColorSlider("Red", config::getBaseColorRed, value -> config.setBaseColor(Math.round(value), config.getBaseColorGreen(), config.getBaseColorBlue()), saveAction));
        list.add(createColorSlider("Green", config::getBaseColorGreen, value -> config.setBaseColor(config.getBaseColorRed(), Math.round(value), config.getBaseColorBlue()), saveAction));
        list.add(createColorSlider("Blue", config::getBaseColorBlue, value -> config.setBaseColor(config.getBaseColorRed(), config.getBaseColorGreen(), Math.round(value)), saveAction));

        list.add(new HeadingComponent("Hurt tint"));
        list.add(new ToggleComponent("Tint when hurt", config::isHurtTintEnabled, config::setHurtTintEnabled, saveAction));
        list.add(createColorSlider("Hurt red", config::getHurtColorRed, value -> config.setHurtColor(Math.round(value), config.getHurtColorGreen(), config.getHurtColorBlue()), saveAction));
        list.add(createColorSlider("Hurt green", config::getHurtColorGreen, value -> config.setHurtColor(config.getHurtColorRed(), Math.round(value), config.getHurtColorBlue()), saveAction));
        list.add(createColorSlider("Hurt blue", config::getHurtColorBlue, value -> config.setHurtColor(config.getHurtColorRed(), config.getHurtColorGreen(), Math.round(value)), saveAction));

        list.add(new HeadingComponent("Ghosts"));
        list.add(new SliderComponent("Speed", 5.0f, 100.0f, 1.0f, config::getGhostSpeed, config::setGhostSpeed, this::formatFloat, saveAction));
        list.add(new SliderComponent("Count", 5.0f, 64.0f, 1.0f, () -> (float) config.getGhostLength(), value -> config.setGhostLength(Math.round(value)), this::formatInt, saveAction));
        list.add(new SliderComponent("Width", 0.1f, 1.5f, 0.01f, config::getGhostWidth, config::setGhostWidth, this::formatFloat, saveAction));
        list.add(new SliderComponent("Orbit radius", 0.2f, 2.0f, 0.01f, config::getGhostRadius, config::setGhostRadius, this::formatFloat, saveAction));
        list.add(new SliderComponent("Angle step", 0.01f, 1.0f, 0.01f, config::getGhostAngle, config::setGhostAngle, this::formatFloat, saveAction));
        list.add(new SliderComponent("Spacing", 4.0f, 30.0f, 0.5f, config::getGhostSpacing, config::setGhostSpacing, this::formatFloat, saveAction));
        list.add(new SliderComponent("Height offset", -1.5f, 1.5f, 0.01f, config::getGhostHeightOffset, config::setGhostHeightOffset, this::formatFloat, saveAction));

        list.add(new HeadingComponent("Circle"));
        list.add(new SliderComponent("Duration", 500.0f, 10000.0f, 10.0f, () -> (float) config.getCircleDuration(), value -> config.setCircleDuration(value), this::formatInt, saveAction));
        list.add(new SliderComponent("Radius scale", 0.2f, 2.0f, 0.01f, config::getCircleRadius, config::setCircleRadius, this::formatFloat, saveAction));

        list.add(new HeadingComponent("Squares"));
        list.add(new SliderComponent("First-person size", 40.0f, 160.0f, 1.0f, config::getHudSizeFirstPerson, config::setHudSizeFirstPerson, this::formatFloat, saveAction));
        list.add(new SliderComponent("Third-person size", 30.0f, 140.0f, 1.0f, config::getHudSizeThirdPerson, config::setHudSizeThirdPerson, this::formatFloat, saveAction));

        return list;
    }

    private List<SettingComponent> createTargetHudSettings(TargetEspConfig config, Runnable saveAction) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("General"));
        list.add(new ToggleComponent("Enable", config::isTargetHudEnabled, value -> config.setTargetHudEnabled(value), saveAction));
        list.add(new SliderComponent("Horizontal position", 0.0f, 1.0f, 0.01f, config::getTargetHudAnchorX, config::setTargetHudAnchorX, this::formatFloat, saveAction));
        list.add(new SliderComponent("Vertical position", 0.0f, 1.0f, 0.01f, config::getTargetHudAnchorY, config::setTargetHudAnchorY, this::formatFloat, saveAction));
        list.add(new SliderComponent("Width", 80.0f, 220.0f, 1.0f, config::getTargetHudWidth, config::setTargetHudWidth, this::formatFloat, saveAction));
        list.add(new SliderComponent("Height", 32.0f, 140.0f, 1.0f, config::getTargetHudHeight, config::setTargetHudHeight, this::formatFloat, saveAction));
        list.add(new SliderComponent("Health bar height", 2.0f, 16.0f, 0.5f, config::getTargetHudBarHeight, config::setTargetHudBarHeight, this::formatFloat, saveAction));
        list.add(new SliderComponent("Equipment scale", 0.4f, 1.2f, 0.01f, config::getTargetHudItemScale, config::setTargetHudItemScale, this::formatFloat, saveAction));
        list.add(new ToggleComponent("Show equipment", config::isTargetHudShowEquipment, config::setTargetHudShowEquipment, saveAction));
        list.add(new ToggleComponent("Show offhand", config::isTargetHudShowOffhand, config::setTargetHudShowOffhand, saveAction));
        list.add(new ToggleComponent("Show health numbers", config::isTargetHudShowHealthText, config::setTargetHudShowHealthText, saveAction));

        list.add(new HeadingComponent("Background"));
        list.add(createColorSlider("Red", config::getTargetHudBackgroundRed, value -> config.setTargetHudBackgroundColor(Math.round(value), config.getTargetHudBackgroundGreen(), config.getTargetHudBackgroundBlue(), config.getTargetHudBackgroundAlpha()), saveAction));
        list.add(createColorSlider("Green", config::getTargetHudBackgroundGreen, value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), Math.round(value), config.getTargetHudBackgroundBlue(), config.getTargetHudBackgroundAlpha()), saveAction));
        list.add(createColorSlider("Blue", config::getTargetHudBackgroundBlue, value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), config.getTargetHudBackgroundGreen(), Math.round(value), config.getTargetHudBackgroundAlpha()), saveAction));
        list.add(createColorSlider("Alpha", config::getTargetHudBackgroundAlpha, value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), config.getTargetHudBackgroundGreen(), config.getTargetHudBackgroundBlue(), Math.round(value)), saveAction));

        list.add(new HeadingComponent("Outline"));
        list.add(createColorSlider("Red", config::getTargetHudOutlineRed, value -> config.setTargetHudOutlineColor(Math.round(value), config.getTargetHudOutlineGreen(), config.getTargetHudOutlineBlue(), config.getTargetHudOutlineAlpha()), saveAction));
        list.add(createColorSlider("Green", config::getTargetHudOutlineGreen, value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), Math.round(value), config.getTargetHudOutlineBlue(), config.getTargetHudOutlineAlpha()), saveAction));
        list.add(createColorSlider("Blue", config::getTargetHudOutlineBlue, value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), config.getTargetHudOutlineGreen(), Math.round(value), config.getTargetHudOutlineAlpha()), saveAction));
        list.add(createColorSlider("Alpha", config::getTargetHudOutlineAlpha, value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), config.getTargetHudOutlineGreen(), config.getTargetHudOutlineBlue(), Math.round(value)), saveAction));

        list.add(new HeadingComponent("Health bar"));
        list.add(createColorSlider("Red", config::getTargetHudBarBackgroundRed, value -> config.setTargetHudBarBackgroundColor(Math.round(value), config.getTargetHudBarBackgroundGreen(), config.getTargetHudBarBackgroundBlue(), config.getTargetHudBarBackgroundAlpha()), saveAction));
        list.add(createColorSlider("Green", config::getTargetHudBarBackgroundGreen, value -> config.setTargetHudBarBackgroundColor(config.getTargetHudBarBackgroundRed(), Math.round(value), config.getTargetHudBarBackgroundBlue(), config.getTargetHudBarBackgroundAlpha()), saveAction));
        list.add(createColorSlider("Blue", config::getTargetHudBarBackgroundBlue, value -> config.setTargetHudBarBackgroundColor(config.getTargetHudBarBackgroundRed(), config.getTargetHudBarBackgroundGreen(), Math.round(value), config.getTargetHudBarBackgroundAlpha()), saveAction));
        list.add(createColorSlider("Alpha", config::getTargetHudBarBackgroundAlpha, value -> config.setTargetHudBarBackgroundColor(config.getTargetHudBarBackgroundRed(), config.getTargetHudBarBackgroundGreen(), config.getTargetHudBarBackgroundBlue(), Math.round(value)), saveAction));

        list.add(new HeadingComponent("Text"));
        list.add(createColorSlider("Red", config::getTargetHudTextRed, value -> config.setTargetHudTextColor(Math.round(value), config.getTargetHudTextGreen(), config.getTargetHudTextBlue(), config.getTargetHudTextAlpha()), saveAction));
        list.add(createColorSlider("Green", config::getTargetHudTextGreen, value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), Math.round(value), config.getTargetHudTextBlue(), config.getTargetHudTextAlpha()), saveAction));
        list.add(createColorSlider("Blue", config::getTargetHudTextBlue, value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), config.getTargetHudTextGreen(), Math.round(value), config.getTargetHudTextAlpha()), saveAction));
        list.add(createColorSlider("Alpha", config::getTargetHudTextAlpha, value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), config.getTargetHudTextGreen(), config.getTargetHudTextBlue(), Math.round(value)), saveAction));

        return list;
    }

    private SliderComponent createColorSlider(String label, Supplier<Integer> getter, Consumer<Float> setter, Runnable saveAction) {
        return new SliderComponent(label, 0.0f, 255.0f, 1.0f, () -> getter.get().floatValue(), setter, this::formatInt, saveAction);
    }

    private String formatInt(float value) {
        return String.valueOf(Math.round(value));
    }

    private String formatFloat(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private class Panel {

        private final Category category;
        private final int index;
        private final List<ModuleCard> modules;
        private float x;
        private float y;
        private float width;
        private float height;
        private float scroll;
        private float scrollTarget;

        Panel(Category category, int index, List<ModuleDescriptor> descriptors) {
            this.category = category;
            this.index = index;
            this.modules = new ArrayList<>();
            for (ModuleDescriptor descriptor : descriptors) {
                this.modules.add(new ModuleCard(descriptor));
            }
        }

        void render(MatrixStack stack, float x, float y, float width, float height, int mouseX, int mouseY, float partialTicks) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;

            float titleHeight = 22.0f;
            AbstractGui.fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + height), 0xAA0E0E12);
            AbstractGui.fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + titleHeight), 0xDD15151C);

            if (TargetClickGuiScreen.this.font != null) {
                TargetClickGuiScreen.this.font.drawString(stack, category.displayName, x + 10, y + 7, 0xFFEDEDF9);
            }

            float contentTop = y + titleHeight + PANEL_PADDING;
            float contentHeight = height - titleHeight - PANEL_PADDING * 2;

            scroll = MathHelper.lerp(0.2f, scroll, scrollTarget);
            float totalHeight = 0.0f;
            for (ModuleCard module : modules) {
                totalHeight += module.getHeight() + MODULE_SPACING;
            }
            float maxScroll = Math.max(0.0f, totalHeight - MODULE_SPACING - contentHeight);
            scrollTarget = MathHelper.clamp(scrollTarget, 0.0f, maxScroll);
            scroll = MathHelper.clamp(scroll, 0.0f, maxScroll);

            enableScissor(x, contentTop, x + width, contentTop + contentHeight);

            float yOffset = contentTop - scroll;
            for (ModuleCard module : modules) {
                float moduleHeight = module.getHeight();
                if (yOffset + moduleHeight >= contentTop && yOffset <= contentTop + contentHeight) {
                    module.render(stack, x + PANEL_PADDING, yOffset, width - PANEL_PADDING * 2, mouseX, mouseY, partialTicks);
                } else {
                    module.setBounds(x + PANEL_PADDING, yOffset, width - PANEL_PADDING * 2);
                    module.hideComponents();
                }
                yOffset += moduleHeight + MODULE_SPACING;
            }

            disableScissor();
        }

        boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isInside(mouseX, mouseY)) {
                return false;
            }
            for (ModuleCard module : modules) {
                if (module.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return false;
        }

        boolean mouseReleased(double mouseX, double mouseY, int button) {
            for (ModuleCard module : modules) {
                if (module.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
            return false;
        }

        boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            for (ModuleCard module : modules) {
                if (module.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                    return true;
                }
            }
            return false;
        }

        boolean handleScroll(double mouseX, double mouseY, double delta) {
            if (!isInside(mouseX, mouseY)) {
                return false;
            }
            scrollTarget = MathHelper.clamp(scrollTarget - (float) delta * 12.0f, 0.0f, Float.MAX_VALUE);
            return true;
        }

        boolean keyPressed(int keyCode, int scanCode, int modifiers) {
            for (ModuleCard module : modules) {
                if (module.keyPressed(keyCode, scanCode, modifiers)) {
                    return true;
                }
            }
            return false;
        }

        boolean charTyped(char codePoint, int modifiers) {
            for (ModuleCard module : modules) {
                if (module.charTyped(codePoint, modifiers)) {
                    return true;
                }
            }
            return false;
        }

        private boolean isInside(double mouseX, double mouseY) {
            return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }
    }

    private class ModuleCard {

        private final ModuleDescriptor descriptor;
        private final List<SettingComponent> settings;
        private boolean expanded = true;
        private float x;
        private float y;
        private float width;

        ModuleCard(ModuleDescriptor descriptor) {
            this.descriptor = descriptor;
            this.settings = descriptor.settings;
        }

        void render(MatrixStack stack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
            setBounds(x, y, width);
            float headerHeight = 20.0f;
            boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight;

            int bgColor = hovered ? 0x55333344 : 0x3320202A;
            AbstractGui.fill(stack, (int) x, (int) y, (int) (x + width), (int) (y + headerHeight), bgColor);

            if (TargetClickGuiScreen.this.font != null) {
                TargetClickGuiScreen.this.font.drawString(stack, descriptor.name, x + 6, y + 6, 0xFFEDEDF9);
                String state = descriptor.enabledGetter.get() ? "ON" : "OFF";
                int color = descriptor.enabledGetter.get() ? 0xFF6CE3B6 : 0xFFEF7474;
                TargetClickGuiScreen.this.font.drawString(stack, state, x + width - 6 - TargetClickGuiScreen.this.font.getStringWidth(state), y + 6, color);
            }

            if (expanded) {
                float totalHeight = getHeight();
                AbstractGui.fill(stack, (int) x, (int) (y + headerHeight), (int) (x + width), (int) (y + totalHeight), 0x22181822);

                float componentY = y + headerHeight + 4.0f;
                for (SettingComponent component : settings) {
                    float componentHeight = component.getHeight();
                    component.render(stack, x + 4.0f, componentY, width - 8.0f, mouseX, mouseY, partialTicks);
                    componentY += componentHeight + 4.0f;
                }
            } else {
                hideComponents();
            }
        }

        void setBounds(float x, float y, float width) {
            this.x = x;
            this.y = y;
            this.width = width;
        }

        void hideComponents() {
            for (SettingComponent component : settings) {
                component.hide();
            }
        }

        float getHeight() {
            float headerHeight = 20.0f;
            if (!expanded) {
                return headerHeight;
            }
            float height = headerHeight + 4.0f;
            for (SettingComponent component : settings) {
                height += component.getHeight() + 4.0f;
            }
            return height + 2.0f;
        }

        boolean mouseClicked(double mouseX, double mouseY, int button) {
            float headerHeight = 20.0f;
            if (mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + headerHeight) {
                if (button == 0) {
                    descriptor.enabledSetter.accept(!descriptor.enabledGetter.get());
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

        boolean mouseReleased(double mouseX, double mouseY, int button) {
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

        boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
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

        boolean keyPressed(int keyCode, int scanCode, int modifiers) {
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

        boolean charTyped(char codePoint, int modifiers) {
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
    }

    private static class ModuleDescriptor {
        private final String name;
        private final String description;
        private final Supplier<Boolean> enabledGetter;
        private final Consumer<Boolean> enabledSetter;
        private final List<SettingComponent> settings;

        ModuleDescriptor(String name, String description, Supplier<Boolean> enabledGetter, Consumer<Boolean> enabledSetter, List<SettingComponent> settings) {
            this.name = name;
            this.description = description;
            this.enabledGetter = enabledGetter;
            this.enabledSetter = enabledSetter;
            this.settings = settings;
        }
    }

    private enum Category {
        RENDER("Render"),
        HUD("HUD");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }
    }

    private void enableScissor(double x1, double y1, double x2, double y2) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getMainWindow() == null) {
            return;
        }
        double scale = minecraft.getMainWindow().getGuiScaleFactor();
        double scissorX = x1 * scale;
        double scissorY = (minecraft.getMainWindow().getScaledHeight() - y2) * scale;
        double scissorWidth = Math.max(0.0, (x2 - x1) * scale);
        double scissorHeight = Math.max(0.0, (y2 - y1) * scale);
        RenderSystem.enableScissorTest();
        RenderSystem.scissor((int) scissorX, (int) scissorY, (int) scissorWidth, (int) scissorHeight);
    }

    private void disableScissor() {
        RenderSystem.disableScissorTest();
    }
}
