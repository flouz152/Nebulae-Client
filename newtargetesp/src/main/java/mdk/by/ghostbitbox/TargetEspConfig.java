package mdk.by.ghostbitbox;

import com.google.gson.JsonObject;
import mdk.by.ghostbitbox.modules.render.targetesp.TargetEspMode;
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
    private static final String GHOST_HEIGHT_OFFSET_KEY = "ghostHeightOffset";
    private static final String CIRCLE_DURATION_KEY = "circleDuration";
    private static final String CIRCLE_RADIUS_KEY = "circleRadius";
    private static final String HUD_SIZE_FIRST_KEY = "hudSizeFirstPerson";
    private static final String HUD_SIZE_THIRD_KEY = "hudSizeThirdPerson";
    private static final String TARGET_HUD_ENABLED_KEY = "targetHudEnabled";
    private static final String TARGET_HUD_ANCHOR_X_KEY = "targetHudAnchorX";
    private static final String TARGET_HUD_ANCHOR_Y_KEY = "targetHudAnchorY";
    private static final String TARGET_HUD_WIDTH_KEY = "targetHudWidth";
    private static final String TARGET_HUD_HEIGHT_KEY = "targetHudHeight";
    private static final String TARGET_HUD_BAR_HEIGHT_KEY = "targetHudBarHeight";
    private static final String TARGET_HUD_ITEM_SCALE_KEY = "targetHudItemScale";
    private static final String TARGET_HUD_SHOW_EQUIPMENT_KEY = "targetHudShowEquipment";
    private static final String TARGET_HUD_SHOW_OFFHAND_KEY = "targetHudShowOffhand";
    private static final String TARGET_HUD_SHOW_HEALTH_TEXT_KEY = "targetHudShowHealthText";
    private static final String TARGET_HUD_BACKGROUND_COLOR_KEY = "targetHudBackgroundColor";
    private static final String TARGET_HUD_OUTLINE_COLOR_KEY = "targetHudOutlineColor";
    private static final String TARGET_HUD_BAR_BACKGROUND_COLOR_KEY = "targetHudBarBackgroundColor";
    private static final String TARGET_HUD_TEXT_COLOR_KEY = "targetHudTextColor";

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
    private float ghostHeightOffset = 0.0f;
    private double circleDuration = 2000.0d;
    private float circleRadius = 0.8f;
    private float hudSizeFirstPerson = 90.0f;
    private float hudSizeThirdPerson = 60.0f;
    private boolean targetHudEnabled = true;
    private float targetHudAnchorX = 0.65f;
    private float targetHudAnchorY = 0.7f;
    private float targetHudWidth = 118.0f;
    private float targetHudHeight = 44.0f;
    private float targetHudBarHeight = 6.0f;
    private float targetHudItemScale = 0.65f;
    private boolean targetHudShowEquipment = true;
    private boolean targetHudShowOffhand = true;
    private boolean targetHudShowHealthText = true;
    private int targetHudBackgroundColor = ColorUtil.rgba(14, 14, 18, 160);
    private int targetHudOutlineColor = ColorUtil.rgba(255, 255, 255, 35);
    private int targetHudBarBackgroundColor = ColorUtil.rgba(30, 30, 40, 180);
    private int targetHudTextColor = ColorUtil.rgba(235, 235, 245, 255);

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

        if (json.has(GHOST_HEIGHT_OFFSET_KEY) && json.get(GHOST_HEIGHT_OFFSET_KEY).isJsonPrimitive()) {
            ghostHeightOffset = json.get(GHOST_HEIGHT_OFFSET_KEY).getAsFloat();
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

        if (json.has(TARGET_HUD_ENABLED_KEY) && json.get(TARGET_HUD_ENABLED_KEY).isJsonPrimitive()) {
            targetHudEnabled = json.get(TARGET_HUD_ENABLED_KEY).getAsBoolean();
        }

        if (json.has(TARGET_HUD_ANCHOR_X_KEY) && json.get(TARGET_HUD_ANCHOR_X_KEY).isJsonPrimitive()) {
            targetHudAnchorX = json.get(TARGET_HUD_ANCHOR_X_KEY).getAsFloat();
        }

        if (json.has(TARGET_HUD_ANCHOR_Y_KEY) && json.get(TARGET_HUD_ANCHOR_Y_KEY).isJsonPrimitive()) {
            targetHudAnchorY = json.get(TARGET_HUD_ANCHOR_Y_KEY).getAsFloat();
        }

        if (json.has(TARGET_HUD_WIDTH_KEY) && json.get(TARGET_HUD_WIDTH_KEY).isJsonPrimitive()) {
            targetHudWidth = json.get(TARGET_HUD_WIDTH_KEY).getAsFloat();
        }

        if (json.has(TARGET_HUD_HEIGHT_KEY) && json.get(TARGET_HUD_HEIGHT_KEY).isJsonPrimitive()) {
            targetHudHeight = json.get(TARGET_HUD_HEIGHT_KEY).getAsFloat();
        }

        if (json.has(TARGET_HUD_BAR_HEIGHT_KEY) && json.get(TARGET_HUD_BAR_HEIGHT_KEY).isJsonPrimitive()) {
            targetHudBarHeight = json.get(TARGET_HUD_BAR_HEIGHT_KEY).getAsFloat();
        }

        if (json.has(TARGET_HUD_ITEM_SCALE_KEY) && json.get(TARGET_HUD_ITEM_SCALE_KEY).isJsonPrimitive()) {
            targetHudItemScale = json.get(TARGET_HUD_ITEM_SCALE_KEY).getAsFloat();
        }

        if (json.has(TARGET_HUD_SHOW_EQUIPMENT_KEY) && json.get(TARGET_HUD_SHOW_EQUIPMENT_KEY).isJsonPrimitive()) {
            targetHudShowEquipment = json.get(TARGET_HUD_SHOW_EQUIPMENT_KEY).getAsBoolean();
        }

        if (json.has(TARGET_HUD_SHOW_OFFHAND_KEY) && json.get(TARGET_HUD_SHOW_OFFHAND_KEY).isJsonPrimitive()) {
            targetHudShowOffhand = json.get(TARGET_HUD_SHOW_OFFHAND_KEY).getAsBoolean();
        }

        if (json.has(TARGET_HUD_SHOW_HEALTH_TEXT_KEY) && json.get(TARGET_HUD_SHOW_HEALTH_TEXT_KEY).isJsonPrimitive()) {
            targetHudShowHealthText = json.get(TARGET_HUD_SHOW_HEALTH_TEXT_KEY).getAsBoolean();
        }

        if (json.has(TARGET_HUD_BACKGROUND_COLOR_KEY) && json.get(TARGET_HUD_BACKGROUND_COLOR_KEY).isJsonPrimitive()) {
            targetHudBackgroundColor = json.get(TARGET_HUD_BACKGROUND_COLOR_KEY).getAsInt();
        }

        if (json.has(TARGET_HUD_OUTLINE_COLOR_KEY) && json.get(TARGET_HUD_OUTLINE_COLOR_KEY).isJsonPrimitive()) {
            targetHudOutlineColor = json.get(TARGET_HUD_OUTLINE_COLOR_KEY).getAsInt();
        }

        if (json.has(TARGET_HUD_BAR_BACKGROUND_COLOR_KEY) && json.get(TARGET_HUD_BAR_BACKGROUND_COLOR_KEY).isJsonPrimitive()) {
            targetHudBarBackgroundColor = json.get(TARGET_HUD_BAR_BACKGROUND_COLOR_KEY).getAsInt();
        }

        if (json.has(TARGET_HUD_TEXT_COLOR_KEY) && json.get(TARGET_HUD_TEXT_COLOR_KEY).isJsonPrimitive()) {
            targetHudTextColor = json.get(TARGET_HUD_TEXT_COLOR_KEY).getAsInt();
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
        json.addProperty(GHOST_HEIGHT_OFFSET_KEY, ghostHeightOffset);
        json.addProperty(CIRCLE_DURATION_KEY, circleDuration);
        json.addProperty(CIRCLE_RADIUS_KEY, circleRadius);
        json.addProperty(HUD_SIZE_FIRST_KEY, hudSizeFirstPerson);
        json.addProperty(HUD_SIZE_THIRD_KEY, hudSizeThirdPerson);
        json.addProperty(TARGET_HUD_ENABLED_KEY, targetHudEnabled);
        json.addProperty(TARGET_HUD_ANCHOR_X_KEY, targetHudAnchorX);
        json.addProperty(TARGET_HUD_ANCHOR_Y_KEY, targetHudAnchorY);
        json.addProperty(TARGET_HUD_WIDTH_KEY, targetHudWidth);
        json.addProperty(TARGET_HUD_HEIGHT_KEY, targetHudHeight);
        json.addProperty(TARGET_HUD_BAR_HEIGHT_KEY, targetHudBarHeight);
        json.addProperty(TARGET_HUD_ITEM_SCALE_KEY, targetHudItemScale);
        json.addProperty(TARGET_HUD_SHOW_EQUIPMENT_KEY, targetHudShowEquipment);
        json.addProperty(TARGET_HUD_SHOW_OFFHAND_KEY, targetHudShowOffhand);
        json.addProperty(TARGET_HUD_SHOW_HEALTH_TEXT_KEY, targetHudShowHealthText);
        json.addProperty(TARGET_HUD_BACKGROUND_COLOR_KEY, targetHudBackgroundColor);
        json.addProperty(TARGET_HUD_OUTLINE_COLOR_KEY, targetHudOutlineColor);
        json.addProperty(TARGET_HUD_BAR_BACKGROUND_COLOR_KEY, targetHudBarBackgroundColor);
        json.addProperty(TARGET_HUD_TEXT_COLOR_KEY, targetHudTextColor);
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

    public float getGhostHeightOffset() {
        return ghostHeightOffset;
    }

    public void setGhostHeightOffset(float ghostHeightOffset) {
        this.ghostHeightOffset = ghostHeightOffset;
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

    public boolean isTargetHudEnabled() {
        return targetHudEnabled;
    }

    public void setTargetHudEnabled(boolean targetHudEnabled) {
        this.targetHudEnabled = targetHudEnabled;
    }

    public float getTargetHudAnchorX() {
        return targetHudAnchorX;
    }

    public void setTargetHudAnchorX(float targetHudAnchorX) {
        this.targetHudAnchorX = targetHudAnchorX;
    }

    public float getTargetHudAnchorY() {
        return targetHudAnchorY;
    }

    public void setTargetHudAnchorY(float targetHudAnchorY) {
        this.targetHudAnchorY = targetHudAnchorY;
    }

    public float getTargetHudWidth() {
        return targetHudWidth;
    }

    public void setTargetHudWidth(float targetHudWidth) {
        this.targetHudWidth = targetHudWidth;
    }

    public float getTargetHudHeight() {
        return targetHudHeight;
    }

    public void setTargetHudHeight(float targetHudHeight) {
        this.targetHudHeight = targetHudHeight;
    }

    public float getTargetHudBarHeight() {
        return targetHudBarHeight;
    }

    public void setTargetHudBarHeight(float targetHudBarHeight) {
        this.targetHudBarHeight = targetHudBarHeight;
    }

    public float getTargetHudItemScale() {
        return targetHudItemScale;
    }

    public void setTargetHudItemScale(float targetHudItemScale) {
        this.targetHudItemScale = targetHudItemScale;
    }

    public boolean isTargetHudShowEquipment() {
        return targetHudShowEquipment;
    }

    public void setTargetHudShowEquipment(boolean targetHudShowEquipment) {
        this.targetHudShowEquipment = targetHudShowEquipment;
    }

    public boolean isTargetHudShowOffhand() {
        return targetHudShowOffhand;
    }

    public void setTargetHudShowOffhand(boolean targetHudShowOffhand) {
        this.targetHudShowOffhand = targetHudShowOffhand;
    }

    public boolean isTargetHudShowHealthText() {
        return targetHudShowHealthText;
    }

    public void setTargetHudShowHealthText(boolean targetHudShowHealthText) {
        this.targetHudShowHealthText = targetHudShowHealthText;
    }

    public int getTargetHudBackgroundColor() {
        return targetHudBackgroundColor;
    }

    public void setTargetHudBackgroundColor(int targetHudBackgroundColor) {
        this.targetHudBackgroundColor = targetHudBackgroundColor;
    }

    public void setTargetHudBackgroundColor(int red, int green, int blue, int alpha) {
        this.targetHudBackgroundColor = ColorUtil.rgba(red, green, blue, alpha);
    }

    public int getTargetHudBackgroundRed() {
        return (targetHudBackgroundColor >> 16) & 0xFF;
    }

    public int getTargetHudBackgroundGreen() {
        return (targetHudBackgroundColor >> 8) & 0xFF;
    }

    public int getTargetHudBackgroundBlue() {
        return targetHudBackgroundColor & 0xFF;
    }

    public int getTargetHudBackgroundAlpha() {
        return (targetHudBackgroundColor >> 24) & 0xFF;
    }

    public int getTargetHudOutlineColor() {
        return targetHudOutlineColor;
    }

    public void setTargetHudOutlineColor(int targetHudOutlineColor) {
        this.targetHudOutlineColor = targetHudOutlineColor;
    }

    public void setTargetHudOutlineColor(int red, int green, int blue, int alpha) {
        this.targetHudOutlineColor = ColorUtil.rgba(red, green, blue, alpha);
    }

    public int getTargetHudOutlineRed() {
        return (targetHudOutlineColor >> 16) & 0xFF;
    }

    public int getTargetHudOutlineGreen() {
        return (targetHudOutlineColor >> 8) & 0xFF;
    }

    public int getTargetHudOutlineBlue() {
        return targetHudOutlineColor & 0xFF;
    }

    public int getTargetHudOutlineAlpha() {
        return (targetHudOutlineColor >> 24) & 0xFF;
    }

    public int getTargetHudBarBackgroundColor() {
        return targetHudBarBackgroundColor;
    }

    public void setTargetHudBarBackgroundColor(int targetHudBarBackgroundColor) {
        this.targetHudBarBackgroundColor = targetHudBarBackgroundColor;
    }

    public void setTargetHudBarBackgroundColor(int red, int green, int blue, int alpha) {
        this.targetHudBarBackgroundColor = ColorUtil.rgba(red, green, blue, alpha);
    }

    public int getTargetHudBarBackgroundRed() {
        return (targetHudBarBackgroundColor >> 16) & 0xFF;
    }

    public int getTargetHudBarBackgroundGreen() {
        return (targetHudBarBackgroundColor >> 8) & 0xFF;
    }

    public int getTargetHudBarBackgroundBlue() {
        return targetHudBarBackgroundColor & 0xFF;
    }

    public int getTargetHudBarBackgroundAlpha() {
        return (targetHudBarBackgroundColor >> 24) & 0xFF;
    }

    public int getTargetHudTextColor() {
        return targetHudTextColor;
    }

    public void setTargetHudTextColor(int targetHudTextColor) {
        this.targetHudTextColor = targetHudTextColor;
    }

    public void setTargetHudTextColor(int red, int green, int blue, int alpha) {
        this.targetHudTextColor = ColorUtil.rgba(red, green, blue, alpha);
    }

    public int getTargetHudTextRed() {
        return (targetHudTextColor >> 16) & 0xFF;
    }

    public int getTargetHudTextGreen() {
        return (targetHudTextColor >> 8) & 0xFF;
    }

    public int getTargetHudTextBlue() {
        return targetHudTextColor & 0xFF;
    }

    public int getTargetHudTextAlpha() {
        return (targetHudTextColor >> 24) & 0xFF;
    }
}
