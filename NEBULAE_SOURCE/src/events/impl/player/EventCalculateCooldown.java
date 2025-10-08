package events.impl.player;

import events.Event;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.minecraft.item.Item;

/**
 * @author dedinside
 * @since 09.06.2023
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class EventCalculateCooldown extends Event {
// leaked by itskekoff; discord.gg/sk3d UvoLneeh

    public Item itemStack;
    public float cooldown;

    public EventCalculateCooldown(Item item) {
        this.itemStack = item;
    }
}

