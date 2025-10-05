package net.minecraft.block;

import net.minecraft.state.DirectionProperty;
import net.minecraft.state.properties.BlockStateProperties;

public abstract class DirectionalBlock extends Block
{
// leaked by itskekoff; discord.gg/sk3d KZMoRn5o
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    protected DirectionalBlock(AbstractBlock.Properties builder)
    {
        super(builder);
    }
}
