package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.area.IAreaFactory;

public interface IAreaTransformer0
{
// leaked by itskekoff; discord.gg/sk3d lAcBBNcc
default <R extends IArea> IAreaFactory<R> apply(IExtendedNoiseRandom<R> context)
    {
        return () ->
        {
            return context.makeArea((p_202820_2_, p_202820_3_) -> {
                context.setPosition((long)p_202820_2_, (long)p_202820_3_);
                return this.apply(context, p_202820_2_, p_202820_3_);
            });
        };
    }

    int apply(INoiseRandom p_215735_1_, int p_215735_2_, int p_215735_3_);
}
