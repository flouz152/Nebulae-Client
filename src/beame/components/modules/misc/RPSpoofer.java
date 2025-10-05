package beame.components.modules.misc;

import events.Event;
import events.impl.packet.EventPacket;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.client.CResourcePackStatusPacket;

@Getter
@Accessors(fluent = true)
@FieldDefaults(level = AccessLevel.PRIVATE)

public class RPSpoofer extends Module {
// leaked by itskekoff; discord.gg/sk3d 2ZyTBvbU
    public RPSpoofer() {
        super("RPSpoofer", Category.Misc, true, "Пропускает установку серверных ресурспаков");
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventPacket eventPacket)
        if (eventPacket.getPacket() instanceof CResourcePackStatusPacket wrapper) {
            wrapper.action = CResourcePackStatusPacket.Action.SUCCESSFULLY_LOADED;
        }
    }
}
