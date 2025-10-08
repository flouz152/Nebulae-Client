package net.minecraft.util;

import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.GroundPathNavigator;

public class GroundPathHelper
{
// leaked by itskekoff; discord.gg/sk3d tGAdqLIz
    public static boolean isGroundNavigator(MobEntity mob)
    {
        return mob.getNavigator() instanceof GroundPathNavigator;
    }
}
