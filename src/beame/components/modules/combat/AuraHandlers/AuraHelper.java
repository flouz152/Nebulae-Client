package beame.components.modules.combat.AuraHandlers;

import events.Event;

import java.util.List;

public final class AuraHelper {
    public final FreeLookHandler freeLookHandler = new FreeLookHandler();
    public final RotationHandler rotationHandler = new RotationHandler();

    private final List<Handler> handlers = List.of(freeLookHandler, rotationHandler);

    public void callAll(Event event) {
        handlers.forEach(handler -> handler.event(event));
    }
}
