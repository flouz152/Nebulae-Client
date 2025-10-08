package dev.nebulae.targetesp;

import net.labymod.api.config.AddonConfig;
import net.labymod.api.config.ConfigProperty;

public class TargetEspConfiguration extends AddonConfig {

    private final ConfigProperty<Boolean> enabled = new ConfigProperty<>(true);
    private final ConfigProperty<TargetEspMode> mode = new ConfigProperty<>(TargetEspMode.GHOSTS);
    private final ConfigProperty<Boolean> redOnHurt = new ConfigProperty<>(true);
    private final ConfigProperty<Double> ghostSpeed = new ConfigProperty<>(33.0D);
    private final ConfigProperty<Double> ghostLength = new ConfigProperty<>(24.0D);
    private final ConfigProperty<Double> ghostWidth = new ConfigProperty<>(0.4D);
    private final ConfigProperty<Double> ghostAngle = new ConfigProperty<>(0.18D);
    private final ConfigProperty<Double> circleSpeed = new ConfigProperty<>(2000.0D);

    @Override
    public ConfigProperty<Boolean> enabled() {
        return enabled;
    }

    public ConfigProperty<TargetEspMode> modeProperty() {
        return mode;
    }

    public ConfigProperty<Boolean> redOnHurtProperty() {
        return redOnHurt;
    }

    public ConfigProperty<Double> ghostSpeedProperty() {
        return ghostSpeed;
    }

    public ConfigProperty<Double> ghostLengthProperty() {
        return ghostLength;
    }

    public ConfigProperty<Double> ghostWidthProperty() {
        return ghostWidth;
    }

    public ConfigProperty<Double> ghostAngleProperty() {
        return ghostAngle;
    }

    public ConfigProperty<Double> circleSpeedProperty() {
        return circleSpeed;
    }
}
