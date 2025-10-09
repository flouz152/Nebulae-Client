package beame.components.modules.combat.AuraHandlers.other;

import beame.util.IMinecraft;
import events.Event;

/**
 * Base class for the aura helper handlers.  Implementations only need to
 * override {@link #event(Event)}; all shared Minecraft access lives here.
 */
public abstract class Handler implements IMinecraft {
    protected Handler() {
    }

    public abstract void event(Event event);
}
