package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;

/**
 * @author dedinside
 * @since 07.06.2023
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventPlace extends Event {
// leaked by itskekoff; discord.gg/sk3d 7vQ9PKKV

    private Block block;
    private BlockPos pos;

}
