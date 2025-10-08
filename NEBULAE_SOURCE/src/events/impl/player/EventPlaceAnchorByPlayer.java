package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/**
 * @author dedinside
 * @since 06.06.2023
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventPlaceAnchorByPlayer extends Event {
// leaked by itskekoff; discord.gg/sk3d RZ4GphMG

    private final Block block;
    private final BlockPos pos;

}
