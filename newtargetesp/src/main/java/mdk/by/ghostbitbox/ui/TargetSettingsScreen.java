package mdk.by.ghostbitbox.ui;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.StringTextComponent;

public class TargetSettingsScreen extends Screen {

    private static final int SIDEBAR_WIDTH = 120;
    private static final int PANEL_MARGIN = 16;
    private static final int ENTRY_SPACING = 6;
    private static final int PANEL_PADDING = 8;

    private final TargetEspAddon addon;
    private final Map<Category, List<SettingComponent>> categoryComponents = new EnumMap<>(Category.class);
    private final Map<Category, Float> scrollOffsets = new EnumMap<>(Category.class);
    private Category activeCategory = Category.GENERAL;

    public TargetSettingsScreen(TargetEspAddon addon) {
        super(new StringTextComponent("Target ESP"));
        this.addon = addon;
        for (Category category : Category.values()) {
            scrollOffsets.put(category, 0.0f);
        }
    }

    @Override
    protected void init() {
        rebuildComponents();
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        drawCenteredString(matrixStack, this.font, this.title.getString(), this.width / 2, 12, 0xFFFFFFFF);

        float panelLeft = PANEL_MARGIN + SIDEBAR_WIDTH + PANEL_MARGIN;
        float panelWidth = this.width - panelLeft - PANEL_MARGIN;
        float panelTop = 36;
        float panelHeight = this.height - panelTop - PANEL_MARGIN;

        renderSidebar(matrixStack, mouseX, mouseY, panelTop, panelHeight);
        renderActiveCategory(matrixStack, mouseX, mouseY, partialTicks, panelLeft, panelTop, panelWidth, panelHeight);

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (handleSidebarClick(mouseX, mouseY)) {
            return true;
        }

        List<SettingComponent> components = categoryComponents.get(activeCategory);
        if (components != null) {
            for (SettingComponent component : components) {
                if (component.mouseClicked(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        List<SettingComponent> components = categoryComponents.get(activeCategory);
        if (components != null) {
            for (SettingComponent component : components) {
                if (component.mouseReleased(mouseX, mouseY, button)) {
                    return true;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        List<SettingComponent> components = categoryComponents.get(activeCategory);
        if (components != null) {
            for (SettingComponent component : components) {
                if (component.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
                    return true;
                }
            }
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        float panelLeft = PANEL_MARGIN + SIDEBAR_WIDTH + PANEL_MARGIN;
        float panelWidth = this.width - panelLeft - PANEL_MARGIN;
        float panelTop = 36;
        float panelHeight = this.height - panelTop - PANEL_MARGIN;

        if (mouseX >= panelLeft && mouseX <= panelLeft + panelWidth && mouseY >= panelTop && mouseY <= panelTop + panelHeight) {
            float current = scrollOffsets.getOrDefault(activeCategory, 0.0f);
            float maxScroll = getMaxScroll(activeCategory, panelHeight);
            current = MathHelper.clamp(current - (float) (delta * 18.0f), 0.0f, maxScroll);
            scrollOffsets.put(activeCategory, current);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public void onClose() {
        addon.persistConfiguration();
        super.onClose();
    }

    private void rebuildComponents() {
        categoryComponents.clear();
        TargetEspConfig config = addon.configuration();

        categoryComponents.put(Category.GENERAL, createGeneralSettings(config));
        categoryComponents.put(Category.COLORS, createColorSettings(config));
        categoryComponents.put(Category.GHOSTS, createGhostSettings(config));
        categoryComponents.put(Category.CIRCLE, createCircleSettings(config));
        categoryComponents.put(Category.SQUARES, createSquareSettings(config));
        categoryComponents.put(Category.HUD, createHudSettings(config));
        categoryComponents.put(Category.HUD_BACKGROUND, createHudBackgroundSettings(config));
        categoryComponents.put(Category.HUD_OUTLINE, createHudOutlineSettings(config));
        categoryComponents.put(Category.HUD_BAR, createHudBarSettings(config));
        categoryComponents.put(Category.HUD_TEXT, createHudTextSettings(config));
    }

    private List<SettingComponent> createGeneralSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("General"));
        list.add(new ToggleComponent("Enable Target ESP", config::isEnabled, value -> addon.setEnabled(value)));
        list.add(new CycleComponent<>("Highlight mode", TargetEspMode.values(), config::getMode, addon::setMode,
                mode -> mode.getDisplayName()));
        return list;
    }

    private List<SettingComponent> createColorSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("Base color"));
        list.add(new SliderComponent("Red", 0.0f, 255.0f, 1.0f, () -> (float) config.getBaseColorRed(),
                value -> {
                    config.setBaseColor(Math.round(value), config.getBaseColorGreen(), config.getBaseColorBlue());
                }, this::formatInt));
        list.add(new SliderComponent("Green", 0.0f, 255.0f, 1.0f, () -> (float) config.getBaseColorGreen(),
                value -> config.setBaseColor(config.getBaseColorRed(), Math.round(value), config.getBaseColorBlue()), this::formatInt));
        list.add(new SliderComponent("Blue", 0.0f, 255.0f, 1.0f, () -> (float) config.getBaseColorBlue(),
                value -> config.setBaseColor(config.getBaseColorRed(), config.getBaseColorGreen(), Math.round(value)), this::formatInt));
        list.add(new HeadingComponent("Hurt tint"));
        list.add(new ToggleComponent("Tint when hurt", config::isHurtTintEnabled, config::setHurtTintEnabled));
        list.add(new SliderComponent("Hurt red", 0.0f, 255.0f, 1.0f, () -> (float) config.getHurtColorRed(),
                value -> config.setHurtColor(Math.round(value), config.getHurtColorGreen(), config.getHurtColorBlue()), this::formatInt));
        list.add(new SliderComponent("Hurt green", 0.0f, 255.0f, 1.0f, () -> (float) config.getHurtColorGreen(),
                value -> config.setHurtColor(config.getHurtColorRed(), Math.round(value), config.getHurtColorBlue()), this::formatInt));
        list.add(new SliderComponent("Hurt blue", 0.0f, 255.0f, 1.0f, () -> (float) config.getHurtColorBlue(),
                value -> config.setHurtColor(config.getHurtColorRed(), config.getHurtColorGreen(), Math.round(value)), this::formatInt));
        return list;
    }

    private List<SettingComponent> createGhostSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("Ghosts"));
        list.add(new SliderComponent("Speed", 5.0f, 100.0f, 1.0f, config::getGhostSpeed, config::setGhostSpeed, this::formatFloat));
        list.add(new SliderComponent("Count", 5.0f, 64.0f, 1.0f, () -> (float) config.getGhostLength(),
                value -> config.setGhostLength(Math.round(value)), this::formatInt));
        list.add(new SliderComponent("Width", 0.1f, 1.5f, 0.01f, config::getGhostWidth, config::setGhostWidth, this::formatFloat));
        list.add(new SliderComponent("Orbit radius", 0.2f, 2.0f, 0.01f, config::getGhostRadius, config::setGhostRadius, this::formatFloat));
        list.add(new SliderComponent("Angle step", 0.01f, 1.0f, 0.01f, config::getGhostAngle, config::setGhostAngle, this::formatFloat));
        list.add(new SliderComponent("Spacing", 4.0f, 30.0f, 0.5f, config::getGhostSpacing, config::setGhostSpacing, this::formatFloat));
        list.add(new SliderComponent("Height offset", -1.5f, 1.5f, 0.01f, config::getGhostHeightOffset, config::setGhostHeightOffset, this::formatFloat));
        return list;
    }

    private List<SettingComponent> createCircleSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("Circle"));
        list.add(new SliderComponent("Duration", 500.0f, 10000.0f, 10.0f, () -> (float) config.getCircleDuration(),
                value -> config.setCircleDuration(value), this::formatInt));
        list.add(new SliderComponent("Radius scale", 0.2f, 2.0f, 0.01f, config::getCircleRadius, config::setCircleRadius, this::formatFloat));
        return list;
    }

    private List<SettingComponent> createSquareSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("Squares"));
        list.add(new SliderComponent("First-person size", 40.0f, 160.0f, 1.0f, config::getHudSizeFirstPerson, config::setHudSizeFirstPerson, this::formatFloat));
        list.add(new SliderComponent("Third-person size", 30.0f, 140.0f, 1.0f, config::getHudSizeThirdPerson, config::setHudSizeThirdPerson, this::formatFloat));
        return list;
    }

    private List<SettingComponent> createHudSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("Target HUD"));
        list.add(new ToggleComponent("Enable Target HUD", config::isTargetHudEnabled, config::setTargetHudEnabled));
        list.add(new SliderComponent("Horizontal position", 0.0f, 1.0f, 0.01f, config::getTargetHudAnchorX, config::setTargetHudAnchorX, this::formatFloat));
        list.add(new SliderComponent("Vertical position", 0.0f, 1.0f, 0.01f, config::getTargetHudAnchorY, config::setTargetHudAnchorY, this::formatFloat));
        list.add(new SliderComponent("Width", 80.0f, 220.0f, 1.0f, config::getTargetHudWidth, config::setTargetHudWidth, this::formatFloat));
        list.add(new SliderComponent("Height", 32.0f, 140.0f, 1.0f, config::getTargetHudHeight, config::setTargetHudHeight, this::formatFloat));
        list.add(new SliderComponent("Health bar height", 2.0f, 16.0f, 0.5f, config::getTargetHudBarHeight, config::setTargetHudBarHeight, this::formatFloat));
        list.add(new SliderComponent("Equipment scale", 0.4f, 1.2f, 0.01f, config::getTargetHudItemScale, config::setTargetHudItemScale, this::formatFloat));
        list.add(new ToggleComponent("Show equipment", config::isTargetHudShowEquipment, config::setTargetHudShowEquipment));
        list.add(new ToggleComponent("Show offhand", config::isTargetHudShowOffhand, config::setTargetHudShowOffhand));
        list.add(new ToggleComponent("Show health numbers", config::isTargetHudShowHealthText, config::setTargetHudShowHealthText));
        return list;
    }

    private List<SettingComponent> createHudBackgroundSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("HUD background"));
        list.add(new SliderComponent("Red", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBackgroundRed(),
                value -> config.setTargetHudBackgroundColor(Math.round(value), config.getTargetHudBackgroundGreen(), config.getTargetHudBackgroundBlue(), config.getTargetHudBackgroundAlpha()), this::formatInt));
        list.add(new SliderComponent("Green", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBackgroundGreen(),
                value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), Math.round(value), config.getTargetHudBackgroundBlue(), config.getTargetHudBackgroundAlpha()), this::formatInt));
        list.add(new SliderComponent("Blue", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBackgroundBlue(),
                value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), config.getTargetHudBackgroundGreen(), Math.round(value), config.getTargetHudBackgroundAlpha()), this::formatInt));
        list.add(new SliderComponent("Alpha", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBackgroundAlpha(),
                value -> config.setTargetHudBackgroundColor(config.getTargetHudBackgroundRed(), config.getTargetHudBackgroundGreen(), config.getTargetHudBackgroundBlue(), Math.round(value)), this::formatInt));
        return list;
    }

    private List<SettingComponent> createHudOutlineSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("HUD outline"));
        list.add(new SliderComponent("Red", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudOutlineRed(),
                value -> config.setTargetHudOutlineColor(Math.round(value), config.getTargetHudOutlineGreen(), config.getTargetHudOutlineBlue(), config.getTargetHudOutlineAlpha()), this::formatInt));
        list.add(new SliderComponent("Green", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudOutlineGreen(),
                value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), Math.round(value), config.getTargetHudOutlineBlue(), config.getTargetHudOutlineAlpha()), this::formatInt));
        list.add(new SliderComponent("Blue", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudOutlineBlue(),
                value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), config.getTargetHudOutlineGreen(), Math.round(value), config.getTargetHudOutlineAlpha()), this::formatInt));
        list.add(new SliderComponent("Alpha", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudOutlineAlpha(),
                value -> config.setTargetHudOutlineColor(config.getTargetHudOutlineRed(), config.getTargetHudOutlineGreen(), config.getTargetHudOutlineBlue(), Math.round(value)), this::formatInt));
        return list;
    }

    private List<SettingComponent> createHudBarSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("Health bar background"));
        list.add(new SliderComponent("Red", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBarBackgroundRed(),
                value -> config.setTargetHudBarBackgroundColor(Math.round(value), config.getTargetHudBarBackgroundGreen(), config.getTargetHudBarBackgroundBlue(), config.getTargetHudBarBackgroundAlpha()), this::formatInt));
        list.add(new SliderComponent("Green", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBarBackgroundGreen(),
                value -> config.setTargetHudBarBackgroundColor(config.getTargetHudBarBackgroundRed(), Math.round(value), config.getTargetHudBarBackgroundBlue(), config.getTargetHudBarBackgroundAlpha()), this::formatInt));
        list.add(new SliderComponent("Blue", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBarBackgroundBlue(),
                value -> config.setTargetHudBarBackgroundColor(config.getTargetHudBarBackgroundRed(), config.getTargetHudBarBackgroundGreen(), Math.round(value), config.getTargetHudBarBackgroundAlpha()), this::formatInt));
        list.add(new SliderComponent("Alpha", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudBarBackgroundAlpha(),
                value -> config.setTargetHudBarBackgroundColor(config.getTargetHudBarBackgroundRed(), config.getTargetHudBarBackgroundGreen(), config.getTargetHudBarBackgroundBlue(), Math.round(value)), this::formatInt));
        return list;
    }

    private List<SettingComponent> createHudTextSettings(TargetEspConfig config) {
        List<SettingComponent> list = new ArrayList<>();
        list.add(new HeadingComponent("HUD text"));
        list.add(new SliderComponent("Red", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudTextRed(),
                value -> config.setTargetHudTextColor(Math.round(value), config.getTargetHudTextGreen(), config.getTargetHudTextBlue(), config.getTargetHudTextAlpha()), this::formatInt));
        list.add(new SliderComponent("Green", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudTextGreen(),
                value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), Math.round(value), config.getTargetHudTextBlue(), config.getTargetHudTextAlpha()), this::formatInt));
        list.add(new SliderComponent("Blue", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudTextBlue(),
                value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), config.getTargetHudTextGreen(), Math.round(value), config.getTargetHudTextAlpha()), this::formatInt));
        list.add(new SliderComponent("Alpha", 0.0f, 255.0f, 1.0f, () -> (float) config.getTargetHudTextAlpha(),
                value -> config.setTargetHudTextColor(config.getTargetHudTextRed(), config.getTargetHudTextGreen(), config.getTargetHudTextBlue(), Math.round(value)), this::formatInt));
        return list;
    }

    private void renderSidebar(MatrixStack matrixStack, int mouseX, int mouseY, float top, float height) {
        float left = PANEL_MARGIN;
        float bottom = top + height;
        AbstractGui.fill(matrixStack, (int) left, (int) top, (int) (left + SIDEBAR_WIDTH), (int) bottom, 0xAA0D0D12);

        float entryHeight = 18.0f;
        float currentY = top + PANEL_PADDING;
        for (Category category : Category.values()) {
            boolean active = category == activeCategory;
            int color = active ? 0xFFE0E0F0 : 0xFF9AA0A8;
            if (mouseX >= left && mouseX <= left + SIDEBAR_WIDTH && mouseY >= currentY && mouseY <= currentY + entryHeight) {
                color = 0xFFFFFFFF;
            }
            if (active) {
                AbstractGui.fill(matrixStack, (int) left + 2, (int) currentY - 2, (int) (left + SIDEBAR_WIDTH - 2), (int) (currentY + entryHeight + 2), 0x33505070);
            }
            this.font.drawString(matrixStack, category.displayName, left + 8, currentY, color);
            currentY += entryHeight + 4.0f;
        }
    }

    private boolean handleSidebarClick(double mouseX, double mouseY) {
        float top = 36 + PANEL_PADDING;
        float entryHeight = 18.0f;
        float left = PANEL_MARGIN;
        for (Category category : Category.values()) {
            if (mouseX >= left && mouseX <= left + SIDEBAR_WIDTH && mouseY >= top && mouseY <= top + entryHeight) {
                activeCategory = category;
                scrollOffsets.put(activeCategory, 0.0f);
                return true;
            }
            top += entryHeight + 4.0f;
        }
        return false;
    }

    private void renderActiveCategory(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks,
                                      float left, float top, float width, float height) {
        AbstractGui.fill(matrixStack, (int) left, (int) top, (int) (left + width), (int) (top + height), 0xAA101016);
        List<SettingComponent> components = categoryComponents.get(activeCategory);
        if (components == null || components.isEmpty()) {
            drawCenteredString(matrixStack, this.font, "No settings", (int) (left + width / 2), (int) (top + height / 2), 0xFFAAAAAA);
            return;
        }

        float scroll = scrollOffsets.getOrDefault(activeCategory, 0.0f);
        float y = top + PANEL_PADDING - scroll;

        for (SettingComponent component : components) {
            float componentHeight = component.getHeight();
            float maxVisibleY = top + height;
            if (y + componentHeight >= top && y <= maxVisibleY) {
                component.render(matrixStack, left + PANEL_PADDING, y, width - PANEL_PADDING * 2, mouseX, mouseY, partialTicks);
            } else {
                component.setBounds(left + PANEL_PADDING, y, width - PANEL_PADDING * 2, componentHeight, false);
            }
            y += componentHeight + ENTRY_SPACING;
        }

        float maxScroll = getMaxScroll(activeCategory, height);
        float current = scrollOffsets.getOrDefault(activeCategory, 0.0f);
        if (current > maxScroll) {
            scrollOffsets.put(activeCategory, maxScroll);
        }
    }

    private float getMaxScroll(Category category, float panelHeight) {
        List<SettingComponent> components = categoryComponents.get(category);
        if (components == null) {
            return 0.0f;
        }
        float total = 0.0f;
        for (SettingComponent component : components) {
            total += component.getHeight() + ENTRY_SPACING;
        }
        float visible = panelHeight - PANEL_PADDING * 2;
        return Math.max(0.0f, total - ENTRY_SPACING - visible);
    }

    private String formatInt(float value) {
        return String.valueOf(Math.round(value));
    }

    private String formatFloat(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private enum Category {
        GENERAL("General"),
        COLORS("Colors"),
        GHOSTS("Ghosts"),
        CIRCLE("Circle"),
        SQUARES("Squares"),
        HUD("Target HUD"),
        HUD_BACKGROUND("HUD Background"),
        HUD_OUTLINE("HUD Outline"),
        HUD_BAR("HUD Bar"),
        HUD_TEXT("HUD Text");

        private final String displayName;

        Category(String displayName) {
            this.displayName = displayName;
        }
    }

    private interface ValueFormatter {
        String format(float value);
    }

    private abstract class SettingComponent {
        private float x;
        private float y;
        private float width;
        private float height;
        private boolean visible;

        protected SettingComponent(float height) {
            this.height = height;
        }

        public float getHeight() {
            return height;
        }

        protected void setBounds(float x, float y, float width, float height, boolean visible) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.visible = visible;
        }

        protected boolean isMouseOver(double mouseX, double mouseY) {
            return visible && mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
        }

        public abstract void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks);

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return false;
        }

        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            return false;
        }

        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            return false;
        }
    }

    private class HeadingComponent extends SettingComponent {

        private final String text;

        HeadingComponent(String text) {
            super(18.0f);
            this.text = text;
        }

        @Override
        public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
            setBounds(x, y, width, getHeight(), true);
            font.drawString(matrixStack, text, x, y + 4, 0xFFD0D0D0);
        }
    }

    private class ToggleComponent extends SettingComponent {

        private final String label;
        private final Supplier<Boolean> getter;
        private final Consumer<Boolean> setter;

        ToggleComponent(String label, Supplier<Boolean> getter, Consumer<Boolean> setter) {
            super(26.0f);
            this.label = label;
            this.getter = getter;
            this.setter = setter;
        }

        @Override
        public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
            setBounds(x, y, width, getHeight(), true);
            AbstractGui.fill(matrixStack, (int) x, (int) y, (int) (x + width), (int) (y + getHeight()), 0x331B1B26);
            boolean value = getter.get();
            font.drawString(matrixStack, label, x + 6, y + 8, 0xFFEFEFF5);
            String status = value ? "ON" : "OFF";
            int color = value ? 0xFF6CE3B6 : 0xFFEF7474;
            font.drawString(matrixStack, status, x + width - 6 - font.getStringWidth(status), y + 8, color);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && isMouseOver(mouseX, mouseY)) {
                boolean newValue = !getter.get();
                setter.accept(newValue);
                addon.persistConfiguration();
                return true;
            }
            return false;
        }
    }

    private class CycleComponent<T> extends SettingComponent {

        private final String label;
        private final T[] values;
        private final Supplier<T> getter;
        private final Consumer<T> setter;
        private final java.util.function.Function<T, String> formatter;

        CycleComponent(String label, T[] values, Supplier<T> getter, Consumer<T> setter,
                       java.util.function.Function<T, String> formatter) {
            super(26.0f);
            this.label = label;
            this.values = values;
            this.getter = getter;
            this.setter = setter;
            this.formatter = formatter;
        }

        @Override
        public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
            setBounds(x, y, width, getHeight(), true);
            AbstractGui.fill(matrixStack, (int) x, (int) y, (int) (x + width), (int) (y + getHeight()), 0x331B1B26);
            font.drawString(matrixStack, label, x + 6, y + 8, 0xFFEFEFF5);
            String value = formatter.apply(getter.get());
            font.drawString(matrixStack, value, x + width - 6 - font.getStringWidth(value), y + 8, 0xFF9AA0A8);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (!isMouseOver(mouseX, mouseY) || values.length == 0) {
                return false;
            }
            int direction = button == 1 ? -1 : 1;
            T current = getter.get();
            int index = 0;
            for (int i = 0; i < values.length; i++) {
                if (values[i] == current) {
                    index = i;
                    break;
                }
            }
            index = (index + direction + values.length) % values.length;
            setter.accept(values[index]);
            addon.persistConfiguration();
            return true;
        }
    }

    private class SliderComponent extends SettingComponent {

        private final String label;
        private final float min;
        private final float max;
        private final float step;
        private final Supplier<Float> getter;
        private final Consumer<Float> setter;
        private final ValueFormatter formatter;
        private boolean dragging;
        private float sliderLeft;
        private float sliderWidth;
        private float value;

        SliderComponent(String label, float min, float max, float step, Supplier<Float> getter, Consumer<Float> setter,
                        ValueFormatter formatter) {
            super(34.0f);
            this.label = label;
            this.min = min;
            this.max = max;
            this.step = step;
            this.getter = getter;
            this.setter = setter;
            this.formatter = formatter;
            this.value = getter.get();
        }

        @Override
        public void render(MatrixStack matrixStack, float x, float y, float width, int mouseX, int mouseY, float partialTicks) {
            setBounds(x, y, width, getHeight(), true);
            if (!dragging) {
                value = getter.get();
            }
            AbstractGui.fill(matrixStack, (int) x, (int) y, (int) (x + width), (int) (y + getHeight()), 0x331B1B26);
            font.drawString(matrixStack, label, x + 6, y + 6, 0xFFEFEFF5);
            String text = formatter.format(value);
            font.drawString(matrixStack, text, x + width - 6 - font.getStringWidth(text), y + 6, 0xFF9AA0A8);

            sliderLeft = x + 8;
            sliderWidth = width - 16;
            float sliderTop = y + getHeight() - 12;
            float sliderBottom = sliderTop + 6;
            AbstractGui.fill(matrixStack, (int) sliderLeft, (int) sliderTop, (int) (sliderLeft + sliderWidth), (int) sliderBottom, 0x5520202A);
            float percentage = (value - min) / (max - min);
            float filledWidth = sliderWidth * MathHelper.clamp(percentage, 0.0f, 1.0f);
            AbstractGui.fill(matrixStack, (int) sliderLeft, (int) sliderTop, (int) (sliderLeft + filledWidth), (int) sliderBottom, 0xFF4C84FF);
            int knobX = (int) (sliderLeft + filledWidth - 3);
            AbstractGui.fill(matrixStack, knobX, (int) sliderTop - 2, knobX + 6, (int) sliderBottom + 2, 0xFFCBD9FF);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == 0 && isMouseOver(mouseX, mouseY)) {
                dragging = true;
                updateValue(mouseX);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            if (dragging) {
                updateValue(mouseX);
                return true;
            }
            return false;
        }

        @Override
        public boolean mouseReleased(double mouseX, double mouseY, int button) {
            if (dragging && button == 0) {
                dragging = false;
                addon.persistConfiguration();
                return true;
            }
            return false;
        }

        private void updateValue(double mouseX) {
            if (sliderWidth <= 0.0f) {
                return;
            }
            float ratio = (float) ((mouseX - sliderLeft) / sliderWidth);
            ratio = MathHelper.clamp(ratio, 0.0f, 1.0f);
            float raw = min + ratio * (max - min);
            if (step > 0.0f) {
                raw = Math.round(raw / step) * step;
            }
            raw = MathHelper.clamp(raw, min, max);
            value = raw;
            setter.accept(raw);
        }
    }
}
