package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.entity.Entity;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TotemPopEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d i0pt6FqF
    private Entity entity;
}