package net.minecraft.entity.passive;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class AmbientEntity extends MobEntity
{
// leaked by itskekoff; discord.gg/sk3d n5AH4VbU
    protected AmbientEntity(EntityType <? extends AmbientEntity > type, World p_i48570_2_)
    {
        super(type, p_i48570_2_);
    }

    public boolean canBeLeashedTo(PlayerEntity player)
    {
        return false;
    }
}
