package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.network.play.client.CPlayerPacket;

/**
 * @author dedinside
 * @since 16.06.2023
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class EventTeleport extends Event {
// leaked by itskekoff; discord.gg/sk3d s7ykNHbk

    private CPlayerPacket response;

    public double posX, posY, posZ;
    public float yaw, pitch;

}
