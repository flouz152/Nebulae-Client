package beame.components.baritone.api.schematic.mask;

import beame.components.baritone.api.schematic.mask.operator.BinaryOperatorMask;
import beame.components.baritone.api.schematic.mask.operator.NotMask;
import beame.components.baritone.api.utils.BooleanBinaryOperators;
import net.minecraft.block.BlockState;

/**
 * @author Brady
 */
public interface Mask {
// leaked by itskekoff; discord.gg/sk3d OfRVpbyD

    /**
     * @param x            The relative x position of the block
     * @param y            The relative y position of the block
     * @param z            The relative z position of the block
     * @param currentState The current state of that block in the world, may be {@code null}
     * @return Whether the given position is included in this mask
     */
    boolean partOfMask(int x, int y, int z, BlockState currentState);

    int widthX();

    int heightY();

    int lengthZ();

    default Mask not() {
        return new NotMask(this);
    }

    default Mask union(Mask other) {
        return new BinaryOperatorMask(this, other, BooleanBinaryOperators.OR);
    }

    default Mask intersection(Mask other) {
        return new BinaryOperatorMask(this, other, BooleanBinaryOperators.AND);
    }

    default Mask xor(Mask other) {
        return new BinaryOperatorMask(this, other, BooleanBinaryOperators.XOR);
    }
}
