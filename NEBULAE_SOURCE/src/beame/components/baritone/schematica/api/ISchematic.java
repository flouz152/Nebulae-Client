package beame.components.baritone.schematica.api;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

public interface ISchematic {
// leaked by itskekoff; discord.gg/sk3d v3Psi4LF

    BlockState getBlockState(BlockPos var1);

    int getWidth();

    int getHeight();

    int getLength();
}
