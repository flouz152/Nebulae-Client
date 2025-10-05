package events.impl.player;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;

@Getter
@Setter
@AllArgsConstructor
public class HitEvent extends CancellableEvent {
// leaked by itskekoff; discord.gg/sk3d YPrWeDyP
    private Entity entity;
    private DamageSource damageSource;
    private float damage;
}