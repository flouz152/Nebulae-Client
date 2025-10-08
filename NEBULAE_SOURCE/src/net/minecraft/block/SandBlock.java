package net.minecraft.block;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class SandBlock extends FallingBlock
{
// leaked by itskekoff; discord.gg/sk3d Q6dgwHrG
    private final int dustColor;

    public SandBlock(int dustColorIn, AbstractBlock.Properties properties)
    {
        super(properties);
        this.dustColor = dustColorIn;
    }

    public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos)
    {
        return this.dustColor;
    }
}
