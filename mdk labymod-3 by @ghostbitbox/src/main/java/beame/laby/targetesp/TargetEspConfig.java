package beame.laby.targetesp;

import com.google.gson.JsonObject;

public class TargetEspConfig {

    private static final String ENABLED_KEY = "enabled";
    private static final String MODE_KEY = "mode";

    private boolean enabled = true;
    private TargetEspMode mode = TargetEspMode.GHOSTS;

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
    }

    public void save(JsonObject json) {
        if (json == null) {
            return;
        }

        json.addProperty(ENABLED_KEY, enabled);
        json.addProperty(MODE_KEY, mode.name());
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
}
