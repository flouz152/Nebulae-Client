package net.minecraft.world.gen.layer.traits;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;

public interface IC1Transformer extends IAreaTransformer1, IDimOffset1Transformer
{
// leaked by itskekoff; discord.gg/sk3d 3DIWC23j
    int apply(INoiseRandom context, int value);

default int apply(IExtendedNoiseRandom<?> context, IArea area, int x, int z)
    {
        int i = area.getValue(this.getOffsetX(x + 1), this.getOffsetZ(z + 1));
        return this.apply(context, i);
    }
}
