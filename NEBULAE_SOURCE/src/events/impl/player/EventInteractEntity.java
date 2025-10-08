package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.entity.Entity;

/**
 * @author dedinside
 * @since 24.06.2023
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventInteractEntity extends Event {
// leaked by itskekoff; discord.gg/sk3d v4iQs9Rh
    private Entity entity;

}
