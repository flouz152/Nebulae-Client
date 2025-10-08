package beame.components.baritone.utils.accessor;

import net.minecraft.util.BitArray;
import net.minecraft.util.palette.IPalette;

public interface IPalettedContainer<T> {
// leaked by itskekoff; discord.gg/sk3d SLVe9DYK

    IPalette<T> getPalette();

    BitArray getStorage();
}
