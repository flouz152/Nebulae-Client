package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
public class EventAttack extends Event {
// leaked by itskekoff; discord.gg/sk3d mvrff4MH
    public Entity target;
}
