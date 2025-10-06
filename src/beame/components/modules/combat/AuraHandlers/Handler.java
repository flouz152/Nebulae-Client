package beame.components.modules.combat.AuraHandlers;

import beame.util.IMinecraft;
import events.Event;

public abstract class Handler implements IMinecraft {
    protected Handler() {
    }

    public abstract void event(Event event);
}
