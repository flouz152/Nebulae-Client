package beame.components.modules.combat.AuraHandlers.other;

import beame.components.modules.combat.AuraHandlers.component.core.combat.FreeLookHandler;
import events.Event;

import java.util.List;

/**
 * Lightweight facade that wires together the helper handlers used by the aura
 * module.  The helper simply loops through the registered handlers and forwards
 * events from the global bus.
 */
public final class AuraHelper {
    public final FreeLookHandler freeLookHandler = new FreeLookHandler();
    public final RotationHandler rotationHandler = new RotationHandler();

    private final List<Handler> handlers = List.of(freeLookHandler, rotationHandler);

    public void callAll(Event event) {
        handlers.forEach(handler -> handler.event(event));
    }
}
