package mdk.by.ghostbitbox;

import java.util.List;
import java.util.Locale;
import net.labymod.api.LabyModAddon;
import net.labymod.api.event.EventService;
import net.labymod.gui.elements.DropDownMenu;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement.IconData;
import net.labymod.settings.elements.DropDownElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.settings.elements.SliderElement;
import net.labymod.utils.Material;

public class TargetEspAddon extends LabyModAddon {

    private final TargetEspConfig configuration = new TargetEspConfig();
    private final TargetEspController controller = new TargetEspController(this);
    private boolean eventsRegistered;

    @Override
    public void onEnable() {
        if (eventsRegistered) {
            return;
        }

        EventService eventService = EventService.getInstance();
        if (eventService != null) {
            eventService.registerListener(controller);
            eventsRegistered = true;
        }
    }

    @Override
    public void onDisable() {
        controller.shutdown();
    }

    @Override
    public void loadConfig() {
        configuration.load(getConfig());
        persistConfiguration();
    }

    @Override
    protected void fillSettings(List<SettingsElement> list) {
        IconData enableIcon = new IconData(Material.ENDER_EYE);
        IconData colorIcon = new IconData(Material.GLOWSTONE_DUST);
        IconData ghostIcon = new IconData(Material.GHAST_TEAR);
        IconData circleIcon = new IconData(Material.MAGMA_CREAM);
        IconData squareIcon = new IconData(Material.ITEM_FRAME);
        IconData hudIcon = new IconData(Material.PAINTING);

        DropDownMenu<TargetEspMode> menu = new DropDownMenu<>("Highlight mode", 0, 0, 0, 0);
        menu.fill(TargetEspMode.values());
        menu.setSelected(configuration.getMode());
        menu.setEnabled(configuration.isEnabled());

        DropDownElement<TargetEspMode> modeElement = new DropDownElement<>("Highlight mode", menu);
        modeElement.setChangeListener(mode -> {
            if (mode == null) {
                return;
            }
            configuration.setMode(mode);
            persistConfiguration();
        });
        modeElement.bindDescription("Choose how the current target should be highlighted.");

        BooleanElement enabledToggle = new BooleanElement("Enable Target ESP", enableIcon, enabled -> {
            configuration.setEnabled(enabled);
            menu.setEnabled(enabled);
            if (!enabled) {
                controller.shutdown();
            }
            persistConfiguration();
        }, configuration.isEnabled());
        enabledToggle.bindDescription("Toggles highlighting for the last attacked entity.");

        list.add(new HeaderElement("General"));
        list.add(enabledToggle);
        list.add(modeElement);

        list.add(new HeaderElement("Base color"));
        SliderElement baseRed = new SliderElement("Base color - Red", colorIcon, configuration.getBaseColorRed());
        baseRed.setRange(0, 255);
        updateSliderDisplay(baseRed, "Base color - Red", configuration.getBaseColorRed());
        baseRed.addCallback(value -> {
            configuration.setBaseColor(value, configuration.getBaseColorGreen(), configuration.getBaseColorBlue());
            updateSliderDisplay(baseRed, "Base color - Red", value);
            persistConfiguration();
        });
        list.add(baseRed);

        SliderElement baseGreen = new SliderElement("Base color - Green", colorIcon, configuration.getBaseColorGreen());
        baseGreen.setRange(0, 255);
        updateSliderDisplay(baseGreen, "Base color - Green", configuration.getBaseColorGreen());
        baseGreen.addCallback(value -> {
            configuration.setBaseColor(configuration.getBaseColorRed(), value, configuration.getBaseColorBlue());
            updateSliderDisplay(baseGreen, "Base color - Green", value);
            persistConfiguration();
        });
        list.add(baseGreen);

        SliderElement baseBlue = new SliderElement("Base color - Blue", colorIcon, configuration.getBaseColorBlue());
        baseBlue.setRange(0, 255);
        updateSliderDisplay(baseBlue, "Base color - Blue", configuration.getBaseColorBlue());
        baseBlue.addCallback(value -> {
            configuration.setBaseColor(configuration.getBaseColorRed(), configuration.getBaseColorGreen(), value);
            updateSliderDisplay(baseBlue, "Base color - Blue", value);
            persistConfiguration();
        });
        list.add(baseBlue);

        list.add(new HeaderElement("Hurt tint"));
        BooleanElement hurtTint = new BooleanElement("Tint when hurt", colorIcon, enabled -> {
            configuration.setHurtTintEnabled(enabled);
            persistConfiguration();
        }, configuration.isHurtTintEnabled());
        hurtTint.bindDescription("Blend into the hurt color while the target takes damage.");
        list.add(hurtTint);

        SliderElement hurtRed = new SliderElement("Hurt color - Red", colorIcon, configuration.getHurtColorRed());
        hurtRed.setRange(0, 255);
        updateSliderDisplay(hurtRed, "Hurt color - Red", configuration.getHurtColorRed());
        hurtRed.addCallback(value -> {
            configuration.setHurtColor(value, configuration.getHurtColorGreen(), configuration.getHurtColorBlue());
            updateSliderDisplay(hurtRed, "Hurt color - Red", value);
            persistConfiguration();
        });
        list.add(hurtRed);

        SliderElement hurtGreen = new SliderElement("Hurt color - Green", colorIcon, configuration.getHurtColorGreen());
        hurtGreen.setRange(0, 255);
        updateSliderDisplay(hurtGreen, "Hurt color - Green", configuration.getHurtColorGreen());
        hurtGreen.addCallback(value -> {
            configuration.setHurtColor(configuration.getHurtColorRed(), value, configuration.getHurtColorBlue());
            updateSliderDisplay(hurtGreen, "Hurt color - Green", value);
            persistConfiguration();
        });
        list.add(hurtGreen);

        SliderElement hurtBlue = new SliderElement("Hurt color - Blue", colorIcon, configuration.getHurtColorBlue());
        hurtBlue.setRange(0, 255);
        updateSliderDisplay(hurtBlue, "Hurt color - Blue", configuration.getHurtColorBlue());
        hurtBlue.addCallback(value -> {
            configuration.setHurtColor(configuration.getHurtColorRed(), configuration.getHurtColorGreen(), value);
            updateSliderDisplay(hurtBlue, "Hurt color - Blue", value);
            persistConfiguration();
        });
        list.add(hurtBlue);

        list.add(new HeaderElement("Ghosts"));
        SliderElement ghostSpeed = new SliderElement("Ghost speed", ghostIcon, Math.round(configuration.getGhostSpeed()));
        ghostSpeed.setRange(5, 100);
        updateSliderDisplay(ghostSpeed, "Ghost speed", formatFloat(configuration.getGhostSpeed()));
        ghostSpeed.addCallback(value -> {
            configuration.setGhostSpeed(value.floatValue());
            updateSliderDisplay(ghostSpeed, "Ghost speed", formatFloat(value.floatValue()));
            persistConfiguration();
        });
        list.add(ghostSpeed);

        SliderElement ghostLength = new SliderElement("Ghost count", ghostIcon, configuration.getGhostLength());
        ghostLength.setRange(5, 64);
        updateSliderDisplay(ghostLength, "Ghost count", configuration.getGhostLength());
        ghostLength.addCallback(value -> {
            configuration.setGhostLength(value);
            updateSliderDisplay(ghostLength, "Ghost count", value);
            persistConfiguration();
        });
        list.add(ghostLength);

        int widthValue = Math.round(configuration.getGhostWidth() * 100.0f);
        SliderElement ghostWidth = new SliderElement("Ghost width", ghostIcon, widthValue);
        ghostWidth.setRange(10, 150);
        updateSliderDisplay(ghostWidth, "Ghost width", formatFloat(widthValue / 100.0f));
        ghostWidth.addCallback(value -> {
            configuration.setGhostWidth(value / 100.0f);
            updateSliderDisplay(ghostWidth, "Ghost width", formatFloat(value / 100.0f));
            persistConfiguration();
        });
        list.add(ghostWidth);

        int radiusValue = Math.round(configuration.getGhostRadius() * 100.0f);
        SliderElement ghostRadius = new SliderElement("Ghost orbit radius", ghostIcon, radiusValue);
        ghostRadius.setRange(20, 200);
        updateSliderDisplay(ghostRadius, "Ghost orbit radius", formatFloat(radiusValue / 100.0f));
        ghostRadius.addCallback(value -> {
            configuration.setGhostRadius(value / 100.0f);
            updateSliderDisplay(ghostRadius, "Ghost orbit radius", formatFloat(value / 100.0f));
            persistConfiguration();
        });
        list.add(ghostRadius);

        int angleValue = Math.round(configuration.getGhostAngle() * 100.0f);
        SliderElement ghostAngle = new SliderElement("Ghost angle step", ghostIcon, angleValue);
        ghostAngle.setRange(1, 100);
        updateSliderDisplay(ghostAngle, "Ghost angle step", formatFloat(angleValue / 100.0f));
        ghostAngle.addCallback(value -> {
            configuration.setGhostAngle(value / 100.0f);
            updateSliderDisplay(ghostAngle, "Ghost angle step", formatFloat(value / 100.0f));
            persistConfiguration();
        });
        list.add(ghostAngle);

        SliderElement ghostSpacing = new SliderElement("Ghost spacing", ghostIcon, Math.round(configuration.getGhostSpacing()));
        ghostSpacing.setRange(4, 30);
        updateSliderDisplay(ghostSpacing, "Ghost spacing", formatFloat(configuration.getGhostSpacing()));
        ghostSpacing.addCallback(value -> {
            configuration.setGhostSpacing(value.floatValue());
            updateSliderDisplay(ghostSpacing, "Ghost spacing", formatFloat(value.floatValue()));
            persistConfiguration();
        });
        list.add(ghostSpacing);

        float ghostOffset = configuration.getGhostHeightOffset();
        int ghostHeightInitial = Math.round((ghostOffset + 1.5f) * 100.0f);
        SliderElement ghostHeight = new SliderElement("Ghost height offset", ghostIcon, ghostHeightInitial);
        ghostHeight.setRange(0, 350);
        updateSliderDisplay(ghostHeight, "Ghost height offset", formatFloat(ghostOffset));
        ghostHeight.addCallback(value -> {
            float offset = value / 100.0f - 1.5f;
            configuration.setGhostHeightOffset(offset);
            updateSliderDisplay(ghostHeight, "Ghost height offset", formatFloat(offset));
            persistConfiguration();
        });
        list.add(ghostHeight);

        list.add(new HeaderElement("Circle"));
        SliderElement circleDuration = new SliderElement("Circle duration", circleIcon, (int) Math.round(configuration.getCircleDuration()));
        circleDuration.setRange(500, 10000);
        updateSliderDisplay(circleDuration, "Circle duration", (int) Math.round(configuration.getCircleDuration()));
        circleDuration.addCallback(value -> {
            configuration.setCircleDuration(value);
            updateSliderDisplay(circleDuration, "Circle duration", value);
            persistConfiguration();
        });
        list.add(circleDuration);

        int circleRadiusValue = Math.round(configuration.getCircleRadius() * 100.0f);
        SliderElement circleRadius = new SliderElement("Circle radius scale", circleIcon, circleRadiusValue);
        circleRadius.setRange(40, 200);
        updateSliderDisplay(circleRadius, "Circle radius scale", formatFloat(circleRadiusValue / 100.0f));
        circleRadius.addCallback(value -> {
            configuration.setCircleRadius(value / 100.0f);
            updateSliderDisplay(circleRadius, "Circle radius scale", formatFloat(value / 100.0f));
            persistConfiguration();
        });
        list.add(circleRadius);

        list.add(new HeaderElement("Squares"));
        SliderElement hudSizeFirst = new SliderElement("Square size (first person)", squareIcon, Math.round(configuration.getHudSizeFirstPerson()));
        hudSizeFirst.setRange(40, 160);
        updateSliderDisplay(hudSizeFirst, "Square size (first person)", formatFloat(configuration.getHudSizeFirstPerson()));
        hudSizeFirst.addCallback(value -> {
            configuration.setHudSizeFirstPerson(value.floatValue());
            updateSliderDisplay(hudSizeFirst, "Square size (first person)", formatFloat(value.floatValue()));
            persistConfiguration();
        });
        list.add(hudSizeFirst);

        SliderElement hudSizeThird = new SliderElement("Square size (third person)", squareIcon, Math.round(configuration.getHudSizeThirdPerson()));
        hudSizeThird.setRange(30, 140);
        updateSliderDisplay(hudSizeThird, "Square size (third person)", formatFloat(configuration.getHudSizeThirdPerson()));
        hudSizeThird.addCallback(value -> {
            configuration.setHudSizeThirdPerson(value.floatValue());
            updateSliderDisplay(hudSizeThird, "Square size (third person)", formatFloat(value.floatValue()));
            persistConfiguration();
        });
        list.add(hudSizeThird);

        list.add(new HeaderElement("Target HUD"));

        BooleanElement hudEnabled = new BooleanElement("Enable Target HUD", hudIcon, enabled -> {
            configuration.setTargetHudEnabled(enabled);
            persistConfiguration();
        }, configuration.isTargetHudEnabled());
        hudEnabled.bindDescription("Toggle the Nebulae-style target information panel.");
        list.add(hudEnabled);

        SliderElement hudPosX = new SliderElement("HUD horizontal position", hudIcon, Math.round(configuration.getTargetHudAnchorX() * 100.0f));
        hudPosX.setRange(0, 100);
        updateSliderDisplay(hudPosX, "HUD horizontal position", formatFloat(configuration.getTargetHudAnchorX()));
        hudPosX.addCallback(value -> {
            float anchor = value / 100.0f;
            configuration.setTargetHudAnchorX(anchor);
            updateSliderDisplay(hudPosX, "HUD horizontal position", formatFloat(anchor));
            persistConfiguration();
        });
        list.add(hudPosX);

        SliderElement hudPosY = new SliderElement("HUD vertical position", hudIcon, Math.round(configuration.getTargetHudAnchorY() * 100.0f));
        hudPosY.setRange(0, 100);
        updateSliderDisplay(hudPosY, "HUD vertical position", formatFloat(configuration.getTargetHudAnchorY()));
        hudPosY.addCallback(value -> {
            float anchor = value / 100.0f;
            configuration.setTargetHudAnchorY(anchor);
            updateSliderDisplay(hudPosY, "HUD vertical position", formatFloat(anchor));
            persistConfiguration();
        });
        list.add(hudPosY);

        SliderElement hudWidth = new SliderElement("HUD width", hudIcon, Math.round(configuration.getTargetHudWidth()));
        hudWidth.setRange(80, 220);
        updateSliderDisplay(hudWidth, "HUD width", formatFloat(configuration.getTargetHudWidth()));
        hudWidth.addCallback(value -> {
            configuration.setTargetHudWidth(value.floatValue());
            updateSliderDisplay(hudWidth, "HUD width", formatFloat(value.floatValue()));
            persistConfiguration();
        });
        list.add(hudWidth);

        SliderElement hudHeight = new SliderElement("HUD height", hudIcon, Math.round(configuration.getTargetHudHeight()));
        hudHeight.setRange(32, 140);
        updateSliderDisplay(hudHeight, "HUD height", formatFloat(configuration.getTargetHudHeight()));
        hudHeight.addCallback(value -> {
            configuration.setTargetHudHeight(value.floatValue());
            updateSliderDisplay(hudHeight, "HUD height", formatFloat(value.floatValue()));
            persistConfiguration();
        });
        list.add(hudHeight);

        SliderElement hudBarHeight = new SliderElement("Health bar height", hudIcon, Math.round(configuration.getTargetHudBarHeight()));
        hudBarHeight.setRange(2, 16);
        updateSliderDisplay(hudBarHeight, "Health bar height", formatFloat(configuration.getTargetHudBarHeight()));
        hudBarHeight.addCallback(value -> {
            configuration.setTargetHudBarHeight(value.floatValue());
            updateSliderDisplay(hudBarHeight, "Health bar height", formatFloat(value.floatValue()));
            persistConfiguration();
        });
        list.add(hudBarHeight);

        SliderElement hudItemScale = new SliderElement("Equipment scale", hudIcon, Math.round(configuration.getTargetHudItemScale() * 100.0f));
        hudItemScale.setRange(40, 120);
        updateSliderDisplay(hudItemScale, "Equipment scale", formatFloat(configuration.getTargetHudItemScale()));
        hudItemScale.addCallback(value -> {
            float scale = value / 100.0f;
            configuration.setTargetHudItemScale(scale);
            updateSliderDisplay(hudItemScale, "Equipment scale", formatFloat(scale));
            persistConfiguration();
        });
        list.add(hudItemScale);

        BooleanElement hudShowEquipment = new BooleanElement("Show equipment", hudIcon, enabled -> {
            configuration.setTargetHudShowEquipment(enabled);
            persistConfiguration();
        }, configuration.isTargetHudShowEquipment());
        list.add(hudShowEquipment);

        BooleanElement hudShowOffhand = new BooleanElement("Show offhand", hudIcon, enabled -> {
            configuration.setTargetHudShowOffhand(enabled);
            persistConfiguration();
        }, configuration.isTargetHudShowOffhand());
        list.add(hudShowOffhand);

        BooleanElement hudShowHealthText = new BooleanElement("Show health numbers", hudIcon, enabled -> {
            configuration.setTargetHudShowHealthText(enabled);
            persistConfiguration();
        }, configuration.isTargetHudShowHealthText());
        list.add(hudShowHealthText);

        list.add(new HeaderElement("Target HUD colors"));

        SliderElement hudBackgroundRed = new SliderElement("Background red", hudIcon, configuration.getTargetHudBackgroundRed());
        hudBackgroundRed.setRange(0, 255);
        updateSliderDisplay(hudBackgroundRed, "Background red", configuration.getTargetHudBackgroundRed());
        hudBackgroundRed.addCallback(value -> {
            configuration.setTargetHudBackgroundColor(value, configuration.getTargetHudBackgroundGreen(), configuration.getTargetHudBackgroundBlue(), configuration.getTargetHudBackgroundAlpha());
            updateSliderDisplay(hudBackgroundRed, "Background red", value);
            persistConfiguration();
        });
        list.add(hudBackgroundRed);

        SliderElement hudBackgroundGreen = new SliderElement("Background green", hudIcon, configuration.getTargetHudBackgroundGreen());
        hudBackgroundGreen.setRange(0, 255);
        updateSliderDisplay(hudBackgroundGreen, "Background green", configuration.getTargetHudBackgroundGreen());
        hudBackgroundGreen.addCallback(value -> {
            configuration.setTargetHudBackgroundColor(configuration.getTargetHudBackgroundRed(), value, configuration.getTargetHudBackgroundBlue(), configuration.getTargetHudBackgroundAlpha());
            updateSliderDisplay(hudBackgroundGreen, "Background green", value);
            persistConfiguration();
        });
        list.add(hudBackgroundGreen);

        SliderElement hudBackgroundBlue = new SliderElement("Background blue", hudIcon, configuration.getTargetHudBackgroundBlue());
        hudBackgroundBlue.setRange(0, 255);
        updateSliderDisplay(hudBackgroundBlue, "Background blue", configuration.getTargetHudBackgroundBlue());
        hudBackgroundBlue.addCallback(value -> {
            configuration.setTargetHudBackgroundColor(configuration.getTargetHudBackgroundRed(), configuration.getTargetHudBackgroundGreen(), value, configuration.getTargetHudBackgroundAlpha());
            updateSliderDisplay(hudBackgroundBlue, "Background blue", value);
            persistConfiguration();
        });
        list.add(hudBackgroundBlue);

        SliderElement hudBackgroundAlpha = new SliderElement("Background alpha", hudIcon, configuration.getTargetHudBackgroundAlpha());
        hudBackgroundAlpha.setRange(0, 255);
        updateSliderDisplay(hudBackgroundAlpha, "Background alpha", configuration.getTargetHudBackgroundAlpha());
        hudBackgroundAlpha.addCallback(value -> {
            configuration.setTargetHudBackgroundColor(configuration.getTargetHudBackgroundRed(), configuration.getTargetHudBackgroundGreen(), configuration.getTargetHudBackgroundBlue(), value);
            updateSliderDisplay(hudBackgroundAlpha, "Background alpha", value);
            persistConfiguration();
        });
        list.add(hudBackgroundAlpha);

        SliderElement hudOutlineRed = new SliderElement("Outline red", hudIcon, configuration.getTargetHudOutlineRed());
        hudOutlineRed.setRange(0, 255);
        updateSliderDisplay(hudOutlineRed, "Outline red", configuration.getTargetHudOutlineRed());
        hudOutlineRed.addCallback(value -> {
            configuration.setTargetHudOutlineColor(value, configuration.getTargetHudOutlineGreen(), configuration.getTargetHudOutlineBlue(), configuration.getTargetHudOutlineAlpha());
            updateSliderDisplay(hudOutlineRed, "Outline red", value);
            persistConfiguration();
        });
        list.add(hudOutlineRed);

        SliderElement hudOutlineGreen = new SliderElement("Outline green", hudIcon, configuration.getTargetHudOutlineGreen());
        hudOutlineGreen.setRange(0, 255);
        updateSliderDisplay(hudOutlineGreen, "Outline green", configuration.getTargetHudOutlineGreen());
        hudOutlineGreen.addCallback(value -> {
            configuration.setTargetHudOutlineColor(configuration.getTargetHudOutlineRed(), value, configuration.getTargetHudOutlineBlue(), configuration.getTargetHudOutlineAlpha());
            updateSliderDisplay(hudOutlineGreen, "Outline green", value);
            persistConfiguration();
        });
        list.add(hudOutlineGreen);

        SliderElement hudOutlineBlue = new SliderElement("Outline blue", hudIcon, configuration.getTargetHudOutlineBlue());
        hudOutlineBlue.setRange(0, 255);
        updateSliderDisplay(hudOutlineBlue, "Outline blue", configuration.getTargetHudOutlineBlue());
        hudOutlineBlue.addCallback(value -> {
            configuration.setTargetHudOutlineColor(configuration.getTargetHudOutlineRed(), configuration.getTargetHudOutlineGreen(), value, configuration.getTargetHudOutlineAlpha());
            updateSliderDisplay(hudOutlineBlue, "Outline blue", value);
            persistConfiguration();
        });
        list.add(hudOutlineBlue);

        SliderElement hudOutlineAlpha = new SliderElement("Outline alpha", hudIcon, configuration.getTargetHudOutlineAlpha());
        hudOutlineAlpha.setRange(0, 255);
        updateSliderDisplay(hudOutlineAlpha, "Outline alpha", configuration.getTargetHudOutlineAlpha());
        hudOutlineAlpha.addCallback(value -> {
            configuration.setTargetHudOutlineColor(configuration.getTargetHudOutlineRed(), configuration.getTargetHudOutlineGreen(), configuration.getTargetHudOutlineBlue(), value);
            updateSliderDisplay(hudOutlineAlpha, "Outline alpha", value);
            persistConfiguration();
        });
        list.add(hudOutlineAlpha);

        SliderElement hudBarRed = new SliderElement("Bar background red", hudIcon, configuration.getTargetHudBarBackgroundRed());
        hudBarRed.setRange(0, 255);
        updateSliderDisplay(hudBarRed, "Bar background red", configuration.getTargetHudBarBackgroundRed());
        hudBarRed.addCallback(value -> {
            configuration.setTargetHudBarBackgroundColor(value, configuration.getTargetHudBarBackgroundGreen(), configuration.getTargetHudBarBackgroundBlue(), configuration.getTargetHudBarBackgroundAlpha());
            updateSliderDisplay(hudBarRed, "Bar background red", value);
            persistConfiguration();
        });
        list.add(hudBarRed);

        SliderElement hudBarGreen = new SliderElement("Bar background green", hudIcon, configuration.getTargetHudBarBackgroundGreen());
        hudBarGreen.setRange(0, 255);
        updateSliderDisplay(hudBarGreen, "Bar background green", configuration.getTargetHudBarBackgroundGreen());
        hudBarGreen.addCallback(value -> {
            configuration.setTargetHudBarBackgroundColor(configuration.getTargetHudBarBackgroundRed(), value, configuration.getTargetHudBarBackgroundBlue(), configuration.getTargetHudBarBackgroundAlpha());
            updateSliderDisplay(hudBarGreen, "Bar background green", value);
            persistConfiguration();
        });
        list.add(hudBarGreen);

        SliderElement hudBarBlue = new SliderElement("Bar background blue", hudIcon, configuration.getTargetHudBarBackgroundBlue());
        hudBarBlue.setRange(0, 255);
        updateSliderDisplay(hudBarBlue, "Bar background blue", configuration.getTargetHudBarBackgroundBlue());
        hudBarBlue.addCallback(value -> {
            configuration.setTargetHudBarBackgroundColor(configuration.getTargetHudBarBackgroundRed(), configuration.getTargetHudBarBackgroundGreen(), value, configuration.getTargetHudBarBackgroundAlpha());
            updateSliderDisplay(hudBarBlue, "Bar background blue", value);
            persistConfiguration();
        });
        list.add(hudBarBlue);

        SliderElement hudBarAlpha = new SliderElement("Bar background alpha", hudIcon, configuration.getTargetHudBarBackgroundAlpha());
        hudBarAlpha.setRange(0, 255);
        updateSliderDisplay(hudBarAlpha, "Bar background alpha", configuration.getTargetHudBarBackgroundAlpha());
        hudBarAlpha.addCallback(value -> {
            configuration.setTargetHudBarBackgroundColor(configuration.getTargetHudBarBackgroundRed(), configuration.getTargetHudBarBackgroundGreen(), configuration.getTargetHudBarBackgroundBlue(), value);
            updateSliderDisplay(hudBarAlpha, "Bar background alpha", value);
            persistConfiguration();
        });
        list.add(hudBarAlpha);

        SliderElement hudTextRed = new SliderElement("Text red", hudIcon, configuration.getTargetHudTextRed());
        hudTextRed.setRange(0, 255);
        updateSliderDisplay(hudTextRed, "Text red", configuration.getTargetHudTextRed());
        hudTextRed.addCallback(value -> {
            configuration.setTargetHudTextColor(value, configuration.getTargetHudTextGreen(), configuration.getTargetHudTextBlue(), configuration.getTargetHudTextAlpha());
            updateSliderDisplay(hudTextRed, "Text red", value);
            persistConfiguration();
        });
        list.add(hudTextRed);

        SliderElement hudTextGreen = new SliderElement("Text green", hudIcon, configuration.getTargetHudTextGreen());
        hudTextGreen.setRange(0, 255);
        updateSliderDisplay(hudTextGreen, "Text green", configuration.getTargetHudTextGreen());
        hudTextGreen.addCallback(value -> {
            configuration.setTargetHudTextColor(configuration.getTargetHudTextRed(), value, configuration.getTargetHudTextBlue(), configuration.getTargetHudTextAlpha());
            updateSliderDisplay(hudTextGreen, "Text green", value);
            persistConfiguration();
        });
        list.add(hudTextGreen);

        SliderElement hudTextBlue = new SliderElement("Text blue", hudIcon, configuration.getTargetHudTextBlue());
        hudTextBlue.setRange(0, 255);
        updateSliderDisplay(hudTextBlue, "Text blue", configuration.getTargetHudTextBlue());
        hudTextBlue.addCallback(value -> {
            configuration.setTargetHudTextColor(configuration.getTargetHudTextRed(), configuration.getTargetHudTextGreen(), value, configuration.getTargetHudTextAlpha());
            updateSliderDisplay(hudTextBlue, "Text blue", value);
            persistConfiguration();
        });
        list.add(hudTextBlue);

        SliderElement hudTextAlpha = new SliderElement("Text alpha", hudIcon, configuration.getTargetHudTextAlpha());
        hudTextAlpha.setRange(0, 255);
        updateSliderDisplay(hudTextAlpha, "Text alpha", configuration.getTargetHudTextAlpha());
        hudTextAlpha.addCallback(value -> {
            configuration.setTargetHudTextColor(configuration.getTargetHudTextRed(), configuration.getTargetHudTextGreen(), configuration.getTargetHudTextBlue(), value);
            updateSliderDisplay(hudTextAlpha, "Text alpha", value);
            persistConfiguration();
        });
        list.add(hudTextAlpha);
    }

    public TargetEspConfig configuration() {
        return configuration;
    }

    private void persistConfiguration() {
        com.google.gson.JsonObject config = getConfig();
        if (config != null) {
            configuration.save(config);
        }
        saveConfig();
    }

    private void updateSliderDisplay(SliderElement slider, String label, int value) {
        slider.setDisplayName(label + " (" + value + ")");
    }

    private void updateSliderDisplay(SliderElement slider, String label, String formattedValue) {
        slider.setDisplayName(label + " (" + formattedValue + ")");
    }

    private String formatFloat(float value) {
        return String.format(Locale.ROOT, "%.2f", value);
    }
}
