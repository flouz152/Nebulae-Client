package dev.nebulae.targetesp;

import net.labymod.api.LabyModAddon;
import net.labymod.api.config.ConfigProperty;
import net.labymod.settings.elements.BooleanElement;
import net.labymod.settings.elements.ControlElement;
import net.labymod.settings.elements.HeaderElement;
import net.labymod.settings.elements.SelectBoxElement;
import net.labymod.settings.elements.SliderElement;
import net.labymod.settings.elements.SettingsElement;
import net.labymod.utils.Material;
import net.minecraftforge.common.MinecraftForge;

import java.util.List;

public class TargetEspAddon extends LabyModAddon {

    private TargetEspConfiguration configuration;
    private TargetEntityTracker tracker;

    @Override
    public void onEnable() {
        if (this.configuration == null) {
            this.configuration = this.loadConfig(new TargetEspConfiguration());
        }
        if (this.tracker != null) {
            MinecraftForge.EVENT_BUS.unregister(this.tracker);
        }
        this.tracker = new TargetEntityTracker(this);
        MinecraftForge.EVENT_BUS.register(this.tracker);
    }

    @Override
    public void loadConfig() {
        this.configuration = this.loadConfig(new TargetEspConfiguration());
    }

    @Override
    public void onDisable() {
        if (this.tracker != null) {
            MinecraftForge.EVENT_BUS.unregister(this.tracker);
            this.tracker = null;
        }
    }

    @Override
    protected void fillSettings(List<SettingsElement> settings) {
        settings.add(new HeaderElement("Target ESP"));

        settings.add(new SelectBoxElement<>(
                "Режим Target ESP",
                new ControlElement.IconData(Material.ENDER_EYE),
                this.configuration.modeProperty(),
                TargetEspMode.values()
        ));

        settings.add(new BooleanElement(
                "Краснеть при ударе",
                this,
                new ControlElement.IconData(Material.REDSTONE),
                "red_on_hurt",
                this.configuration.redOnHurtProperty()
        ));

        settings.add(createSlider(
                new ControlElement.IconData(Material.BLAZE_POWDER),
                "Скорость призраков",
                this.configuration.ghostSpeedProperty(),
                5.0D,
                100.0D,
                1.0D
        ));

        settings.add(createSlider(
                new ControlElement.IconData(Material.BLAZE_POWDER),
                "Длина призраков",
                this.configuration.ghostLengthProperty(),
                5.0D,
                64.0D,
                1.0D
        ));

        settings.add(createSlider(
                new ControlElement.IconData(Material.BLAZE_POWDER),
                "Ширина призраков",
                this.configuration.ghostWidthProperty(),
                0.1D,
                1.0D,
                0.01D
        ));

        settings.add(createSlider(
                new ControlElement.IconData(Material.BLAZE_POWDER),
                "Угол вращения призраков",
                this.configuration.ghostAngleProperty(),
                0.01D,
                1.0D,
                0.01D
        ));

        settings.add(createSlider(
                new ControlElement.IconData(Material.CLOCK),
                "Скорость круга",
                this.configuration.circleSpeedProperty(),
                10.0D,
                10000.0D,
                1.0D
        ));
    }

    private SliderElement createSlider(ControlElement.IconData icon, String name, ConfigProperty<Double> property, double min, double max, double step) {
        return new SliderElement(icon, name, property, min, max, step);
    }

    public TargetEspConfiguration configuration() {
        return configuration;
    }
}
