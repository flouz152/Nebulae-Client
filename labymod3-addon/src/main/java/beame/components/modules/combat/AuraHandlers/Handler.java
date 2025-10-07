package beame.components.modules.combat.AuraHandlers;

import beame.util.IMinecraft;
import events.Event;
import lombok.Getter;

@Getter
public abstract class Handler implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d kcfUZmMh
    public Handler() { }

    public abstract void event(final Event event);
}