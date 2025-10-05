package beame.components.modules.combat.AuraHandlers;

import events.Event;

import java.util.ArrayList;
import java.util.List;

public class AuraHelper {
// leaked by itskekoff; discord.gg/sk3d 8gTezmh8
    public FreeLookHandler freeLookHandler = new FreeLookHandler();
    public RotationHandler rotationHandler = new RotationHandler();

    public AuraHelper() {
        handlerList.add(freeLookHandler);
        handlerList.add(rotationHandler);
    }

    public List<Handler> handlerList = new ArrayList<>();
    public void callAll(final Event event) {
        for(Handler handle : handlerList) {
            handle.event(event);
        }
    }
}
