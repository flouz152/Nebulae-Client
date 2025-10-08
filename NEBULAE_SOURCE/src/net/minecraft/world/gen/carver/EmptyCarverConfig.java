package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;

public class EmptyCarverConfig implements ICarverConfig
{
// leaked by itskekoff; discord.gg/sk3d TZ8oojbs
    public static final Codec<EmptyCarverConfig> field_236237_b_;
    public static final EmptyCarverConfig field_236238_c_ = new EmptyCarverConfig();

    static
    {
        field_236237_b_ = Codec.unit(() ->
        {
            return field_236238_c_;
        });
    }
}
