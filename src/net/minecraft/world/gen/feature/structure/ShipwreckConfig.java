package net.minecraft.world.gen.feature.structure;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class ShipwreckConfig implements IFeatureConfig
{
// leaked by itskekoff; discord.gg/sk3d UXid2puU
    public static final Codec<ShipwreckConfig> field_236634_a_ = Codec.BOOL.fieldOf("is_beached").orElse(false).xmap(ShipwreckConfig::new, (p_236635_0_) ->
    {
        return p_236635_0_.isBeached;
    }).codec();
    public final boolean isBeached;

    public ShipwreckConfig(boolean isBeachedIn)
    {
        this.isBeached = isBeachedIn;
    }
}
