package events.impl.game;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.entity.Entity;

@AllArgsConstructor
public class EventSpawn extends Event {
// leaked by itskekoff; discord.gg/sk3d 6NrqpnMU
    @Getter
    private final Entity entity;
}
