package beame.components.modules.combat.AuraHandlers.other;

import beame.util.IMinecraft;
import events.Event;
import lombok.Getter;

@Getter
public abstract class Handler implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 1llxyFrk
    public Handler() { }

    public abstract void event(final Event event);
}