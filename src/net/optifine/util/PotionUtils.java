package net.optifine.util;

import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class PotionUtils
{
// leaked by itskekoff; discord.gg/sk3d zYXJIZB6
    public static Effect getPotion(ResourceLocation loc)
    {
        return !Registry.EFFECTS.containsKey(loc) ? null : Registry.EFFECTS.getOrDefault(loc);
    }
}
