package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;

public class BlockStateFeatureConfig implements IFeatureConfig
{
// leaked by itskekoff; discord.gg/sk3d OvWs9l1s
    public static final Codec<BlockStateFeatureConfig> field_236455_a_ = BlockState.CODEC.fieldOf("state").xmap(BlockStateFeatureConfig::new, (p_236456_0_) ->
    {
        return p_236456_0_.state;
    }).codec();
    public final BlockState state;

    public BlockStateFeatureConfig(BlockState p_i225831_1_)
    {
        this.state = p_i225831_1_;
    }
}
