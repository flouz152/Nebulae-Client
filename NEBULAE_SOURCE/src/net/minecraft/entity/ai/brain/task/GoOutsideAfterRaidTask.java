package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;

public class GoOutsideAfterRaidTask extends MoveToSkylightTask
{
// leaked by itskekoff; discord.gg/sk3d VhdwcYn4
    public GoOutsideAfterRaidTask(float speed)
    {
        super(speed);
    }

    protected boolean shouldExecute(ServerWorld worldIn, LivingEntity owner)
    {
        Raid raid = worldIn.findRaid(owner.getPosition());
        return raid != null && raid.isVictory() && super.shouldExecute(worldIn, owner);
    }
}
