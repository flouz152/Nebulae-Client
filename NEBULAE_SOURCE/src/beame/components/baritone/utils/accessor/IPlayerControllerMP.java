package beame.components.baritone.utils.accessor;

import net.minecraft.util.math.BlockPos;

public interface IPlayerControllerMP {
// leaked by itskekoff; discord.gg/sk3d xvYKupKi

    void setIsHittingBlock(boolean isHittingBlock);

    BlockPos getCurrentBlock();

    void callSyncCurrentPlayItem();
}
