package beame.util.other;

import beame.util.IMinecraft;
import beame.util.player.InventoryUtility;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;

public class JoinerUtils implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d Dm4EWAE7

    public static void selectCompass() {
        int slot = InventoryUtility.getItemInHotBar(Items.COMPASS);

        if (slot == -1) {
            return;
        }

        mc.player.inventory.currentItem = slot;
        mc.player.connection.sendPacket(new CHeldItemChangePacket(slot));
    }
}
