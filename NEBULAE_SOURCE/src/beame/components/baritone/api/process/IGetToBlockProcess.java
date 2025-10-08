package beame.components.baritone.api.process;

import beame.components.baritone.api.utils.BlockOptionalMeta;
import net.minecraft.block.Block;

/**
 * but it rescans the world every once in a while so it doesn't get fooled by its cache
 */
public interface IGetToBlockProcess extends IBaritoneProcess {
// leaked by itskekoff; discord.gg/sk3d wbm5u5Z3

    void getToBlock(BlockOptionalMeta block);

    default void getToBlock(Block block) {
        getToBlock(new BlockOptionalMeta(block));
    }

    boolean blacklistClosest();
}
