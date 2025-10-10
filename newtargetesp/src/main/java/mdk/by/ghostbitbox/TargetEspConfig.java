package mdk.by.ghostbitbox;

import com.google.gson.JsonObject;
import mdk.by.ghostbitbox.util.ColorUtil;

public class TargetEspConfig {

    private static final String ENABLED_KEY = "enabled";
    private static final String MODE_KEY = "mode";
    private static final String BASE_COLOR_KEY = "baseColor";
    private static final String HURT_COLOR_KEY = "hurtColor";
    private static final String HURT_TINT_KEY = "hurtTint";
    private static final String GHOST_SPEED_KEY = "ghostSpeed";
    private static final String GHOST_LENGTH_KEY = "ghostLength";
    private static final String GHOST_WIDTH_KEY = "ghostWidth";
    private static final String GHOST_ANGLE_KEY = "ghostAngle";
    private static final String GHOST_RADIUS_KEY = "ghostRadius";
    private static final String GHOST_SPACING_KEY = "ghostSpacing";
    private static final String CIRCLE_DURATION_KEY = "circleDuration";
    private static final String CIRCLE_RADIUS_KEY = "circleRadius";
    private static final String HUD_SIZE_FIRST_KEY = "hudSizeFirstPerson";
    private static final String HUD_SIZE_THIRD_KEY = "hudSizeThirdPerson";

    private boolean enabled = true;
    private TargetEspMode mode = TargetEspMode.GHOSTS;
    private int baseColor = ColorUtil.rgba(120, 190, 255, 255);
    private int hurtColor = ColorUtil.rgba(220, 80, 80, 255);
    private boolean hurtTintEnabled = true;
    private float ghostSpeed = 33.0f;
    private int ghostLength = 24;
    private float ghostWidth = 0.4f;
    private float ghostAngle = 0.18f;
    private float ghostRadius = 0.7f;
    private float ghostSpacing = 12.0f;
    private double circleDuration = 2000.0d;
    private float circleRadius = 0.8f;
    private float hudSizeFirstPerson = 90.0f;
    private float hudSizeThirdPerson = 60.0f;

    public void load(JsonObject json) {
        if (json == null) {
            return;
        }

        if (json.has(ENABLED_KEY) && json.get(ENABLED_KEY).isJsonPrimitive()) {
            enabled = json.get(ENABLED_KEY).getAsBoolean();
        }

        if (json.has(MODE_KEY) && json.get(MODE_KEY).isJsonPrimitive()) {
            String name = json.get(MODE_KEY).getAsString();
            try {
                mode = TargetEspMode.valueOf(name);
            } catch (IllegalArgumentException ignored) {
                mode = TargetEspMode.GHOSTS;
            }
        }
        if (json.has(BASE_COLOR_KEY) && json.get(BASE_COLOR_KEY).isJsonPrimitive()) {
            baseColor = json.get(BASE_COLOR_KEY).getAsInt();
        }

        if (json.has(HURT_COLOR_KEY) && json.get(HURT_COLOR_KEY).isJsonPrimitive()) {
            hurtColor = json.get(HURT_COLOR_KEY).getAsInt();
        }

        if (json.has(HURT_TINT_KEY) && json.get(HURT_TINT_KEY).isJsonPrimitive()) {
            hurtTintEnabled = json.get(HURT_TINT_KEY).getAsBoolean();
        }

        if (json.has(GHOST_SPEED_KEY) && json.get(GHOST_SPEED_KEY).isJsonPrimitive()) {
            ghostSpeed = json.get(GHOST_SPEED_KEY).getAsFloat();
        }

        if (json.has(GHOST_LENGTH_KEY) && json.get(GHOST_LENGTH_KEY).isJsonPrimitive()) {
            ghostLength = json.get(GHOST_LENGTH_KEY).getAsInt();
        }

        if (json.has(GHOST_WIDTH_KEY) && json.get(GHOST_WIDTH_KEY).isJsonPrimitive()) {
            ghostWidth = json.get(GHOST_WIDTH_KEY).getAsFloat();
        }

        if (json.has(GHOST_ANGLE_KEY) && json.get(GHOST_ANGLE_KEY).isJsonPrimitive()) {
            ghostAngle = json.get(GHOST_ANGLE_KEY).getAsFloat();
        }

        if (json.has(GHOST_RADIUS_KEY) && json.get(GHOST_RADIUS_KEY).isJsonPrimitive()) {
            ghostRadius = json.get(GHOST_RADIUS_KEY).getAsFloat();
        }

        if (json.has(GHOST_SPACING_KEY) && json.get(GHOST_SPACING_KEY).isJsonPrimitive()) {
            ghostSpacing = json.get(GHOST_SPACING_KEY).getAsFloat();
        }

        if (json.has(CIRCLE_DURATION_KEY) && json.get(CIRCLE_DURATION_KEY).isJsonPrimitive()) {
            circleDuration = json.get(CIRCLE_DURATION_KEY).getAsDouble();
        }

        if (json.has(CIRCLE_RADIUS_KEY) && json.get(CIRCLE_RADIUS_KEY).isJsonPrimitive()) {
            circleRadius = json.get(CIRCLE_RADIUS_KEY).getAsFloat();
        }

        if (json.has(HUD_SIZE_FIRST_KEY) && json.get(HUD_SIZE_FIRST_KEY).isJsonPrimitive()) {
            hudSizeFirstPerson = json.get(HUD_SIZE_FIRST_KEY).getAsFloat();
        }

        if (json.has(HUD_SIZE_THIRD_KEY) && json.get(HUD_SIZE_THIRD_KEY).isJsonPrimitive()) {
            hudSizeThirdPerson = json.get(HUD_SIZE_THIRD_KEY).getAsFloat();
        }
    }

    public void save(JsonObject json) {
        if (json == null) {
            return;
        }

        json.addProperty(ENABLED_KEY, enabled);
        json.addProperty(MODE_KEY, mode.name());
        json.addProperty(BASE_COLOR_KEY, baseColor);
        json.addProperty(HURT_COLOR_KEY, hurtColor);
        json.addProperty(HURT_TINT_KEY, hurtTintEnabled);
        json.addProperty(GHOST_SPEED_KEY, ghostSpeed);
        json.addProperty(GHOST_LENGTH_KEY, ghostLength);
        json.addProperty(GHOST_WIDTH_KEY, ghostWidth);
        json.addProperty(GHOST_ANGLE_KEY, ghostAngle);
        json.addProperty(GHOST_RADIUS_KEY, ghostRadius);
        json.addProperty(GHOST_SPACING_KEY, ghostSpacing);
        json.addProperty(CIRCLE_DURATION_KEY, circleDuration);
        json.addProperty(CIRCLE_RADIUS_KEY, circleRadius);
        json.addProperty(HUD_SIZE_FIRST_KEY, hudSizeFirstPerson);
        json.addProperty(HUD_SIZE_THIRD_KEY, hudSizeThirdPerson);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TargetEspMode getMode() {
        return mode;
    }

    public void setMode(TargetEspMode mode) {
        if (mode != null) {
            this.mode = mode;
        }
    }

    public int getBaseColor() {
        return baseColor;
    }

    public void setBaseColor(int red, int green, int blue) {
        this.baseColor = ColorUtil.rgba(red, green, blue, 255);
    }

    public int getBaseColorRed() {
        return (baseColor >> 16) & 0xFF;
    }

    public int getBaseColorGreen() {
        return (baseColor >> 8) & 0xFF;
    }

    public int getBaseColorBlue() {
        return baseColor & 0xFF;
    }

    public int getHurtColor() {
        return hurtColor;
    }

    public void setHurtColor(int red, int green, int blue) {
        this.hurtColor = ColorUtil.rgba(red, green, blue, 255);
    }

    public int getHurtColorRed() {
        return (hurtColor >> 16) & 0xFF;
    }

    public int getHurtColorGreen() {
        return (hurtColor >> 8) & 0xFF;
    }

    public int getHurtColorBlue() {
        return hurtColor & 0xFF;
    }

    public boolean isHurtTintEnabled() {
        return hurtTintEnabled;
    }

    public void setHurtTintEnabled(boolean hurtTintEnabled) {
        this.hurtTintEnabled = hurtTintEnabled;
    }

    public float getGhostSpeed() {
        return ghostSpeed;
    }

    public void setGhostSpeed(float ghostSpeed) {
        this.ghostSpeed = ghostSpeed;
    }

    public int getGhostLength() {
        return ghostLength;
    }

    public void setGhostLength(int ghostLength) {
        this.ghostLength = ghostLength;
    }

    public float getGhostWidth() {
        return ghostWidth;
    }

    public void setGhostWidth(float ghostWidth) {
        this.ghostWidth = ghostWidth;
    }

    public float getGhostAngle() {
        return ghostAngle;
    }

    public void setGhostAngle(float ghostAngle) {
        this.ghostAngle = ghostAngle;
    }

    public float getGhostRadius() {
        return ghostRadius;
    }

    public void setGhostRadius(float ghostRadius) {
        this.ghostRadius = ghostRadius;
    }

    public float getGhostSpacing() {
        return ghostSpacing;
    }

    public void setGhostSpacing(float ghostSpacing) {
        this.ghostSpacing = ghostSpacing;
    }

    public double getCircleDuration() {
        return circleDuration;
    }

    public void setCircleDuration(double circleDuration) {
        this.circleDuration = circleDuration;
    }

    public float getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(float circleRadius) {
        this.circleRadius = circleRadius;
    }

    public float getHudSizeFirstPerson() {
        return hudSizeFirstPerson;
    }

    public void setHudSizeFirstPerson(float hudSizeFirstPerson) {
        this.hudSizeFirstPerson = hudSizeFirstPerson;
    }

    public float getHudSizeThirdPerson() {
        return hudSizeThirdPerson;
    }

    public void setHudSizeThirdPerson(float hudSizeThirdPerson) {
        this.hudSizeThirdPerson = hudSizeThirdPerson;
    }
}
