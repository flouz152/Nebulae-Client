package beame.components.modules.combat.AuraHandlers.other;

import beame.components.modules.combat.AuraHandlers.component.core.combat.FreeLookHandler;
import events.Event;
import java.util.ArrayList;
import java.util.List;

public class AuraHelper {
// leaked by itskekoff; discord.gg/sk3d B1QWAHtv
    public FreeLookHandler freeLookHandler = new FreeLookHandler();
    public RotationHandler rotationHandler = new RotationHandler();

    private void initialize() {
        handlerList.add(freeLookHandler);
        handlerList.add(rotationHandler);
    }

    public AuraHelper() {
        initialize();
    }

    public List<Handler> handlerList = new ArrayList<>();
    public void callAll(final Event event) {
        for(Handler handle : handlerList) {
            handle.event(event);
        }
    }
}
