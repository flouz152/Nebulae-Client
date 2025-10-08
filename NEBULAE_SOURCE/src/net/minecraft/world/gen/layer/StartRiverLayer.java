package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum StartRiverLayer implements IC0Transformer
{
// leaked by itskekoff; discord.gg/sk3d 4zJ8Ustv
    INSTANCE;

    public int apply(INoiseRandom context, int value)
    {
        return LayerUtil.isShallowOcean(value) ? value : context.random(299999) + 2;
    }
}
