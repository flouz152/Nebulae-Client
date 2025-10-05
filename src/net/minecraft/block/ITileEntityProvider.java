package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public interface ITileEntityProvider
{
// leaked by itskekoff; discord.gg/sk3d ZQqhg8TS
    @Nullable
    TileEntity createNewTileEntity(IBlockReader worldIn);
}
