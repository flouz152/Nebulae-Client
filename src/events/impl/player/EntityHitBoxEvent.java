package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
public class EntityHitBoxEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d 7nw3tIEF
    private Entity entity;
    private float size;
}