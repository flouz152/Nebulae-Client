package beame.components.baritone.api.schematic;

import net.minecraft.block.BlockState;

public class ShellSchematic extends MaskSchematic {
// leaked by itskekoff; discord.gg/sk3d eSa4yBlx

    public ShellSchematic(ISchematic schematic) {
        super(schematic);
    }

    @Override
    protected boolean partOfMask(int x, int y, int z, BlockState currentState) {
        return x == 0 || y == 0 || z == 0 || x == widthX() - 1 || y == heightY() - 1 || z == lengthZ() - 1;
    }
}
