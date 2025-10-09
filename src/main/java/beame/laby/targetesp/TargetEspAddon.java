package beame.laby.targetesp;

import net.labymod.api.LabyModAddon;
import net.labymod.api.event.EventService;

public class TargetEspAddon extends LabyModAddon {

    private TargetEspConfig config;
    private TargetEspController controller;

    @Override
    public void onEnable() {
        EventService events = getApi().getEventService();
        controller = new TargetEspController(this);
        events.registerListener(controller);
    }

    @Override
    public void onDisable() {
        if (controller != null) {
            getApi().getEventService().unregisterListener(controller);
            controller = null;
        }
    }

    @Override
    public void loadConfig() {
        this.config = new TargetEspConfig();
    }

    public TargetEspConfig configuration() {
        return config;
    }
}
