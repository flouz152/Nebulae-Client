package beame.components.baritone.api.pathing.movement;

import beame.components.baritone.api.utils.BetterBlockPos;
import net.minecraft.util.math.BlockPos;

/**
 * @author Brady
 * @since 10/8/2018
 */
public interface IMovement {
// leaked by itskekoff; discord.gg/sk3d cLfBK37U

    double getCost();

    MovementStatus update();

    /**
     * Resets the current state status to {@link MovementStatus#PREPPING}
     */
    void reset();

    /**
     * Resets the cache for special break, place, and walk into blocks
     */
    void resetBlockCache();

    /**
     * @return Whether or not it is safe to cancel the current movement state
     */
    boolean safeToCancel();

    boolean calculatedWhileLoaded();

    BetterBlockPos getSrc();

    BetterBlockPos getDest();

    BlockPos getDirection();
}
