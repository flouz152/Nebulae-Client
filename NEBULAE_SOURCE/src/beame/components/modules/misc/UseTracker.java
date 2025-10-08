package beame.components.modules.misc;

import events.Event;
import events.impl.player.TotemPopEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UseTracker extends Module {
// leaked by itskekoff; discord.gg/sk3d 4PVp8Vof
    // public final RadioSetting mode = new RadioSetting("Режим", "СпукиТайм", "СпукиТайм");
    private static final Map<UUID, Boolean> enchantedTotems = new HashMap<>();
    public UseTracker() {
        super("UseTracker", Category.Misc, true, "Отслеживает какие зелья получил игрок/какой тотем был снесен");
    }

    @Override
    public void event(Event event) {
        if (event instanceof TotemPopEvent e) {
            if (!(e.getEntity() instanceof PlayerEntity player)) return;

            boolean isEnchanted = player.getHeldItemOffhand().isEnchanted() ||
                    player.getHeldItemMainhand().isEnchanted();

            enchantedTotems.put(player.getUniqueID(), isEnchanted);

            String color = isEnchanted ? "§a" : "§c";
            String message = String.format("§7Вы снесли тотем игроку §f%s§7, зачарован %s●",
                    player.getName().getString(), color);
        }
    }
}

