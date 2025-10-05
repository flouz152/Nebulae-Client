package beame.util.player;

import beame.util.IMinecraft;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Hand;

public class PotionUtil implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d 6TDaXizN

    public static boolean isChangingItem;
    private boolean isItemChangeRequested;
    private int previousSlot = -1;

    public void changeItemSlot(boolean resetAfter) {
        if (this.isItemChangeRequested && this.previousSlot != -1) {
            isChangingItem = true;
            mc.player.inventory.currentItem = this.previousSlot;
            if (resetAfter) {
                this.isItemChangeRequested = false;
                this.previousSlot = -1;
                isChangingItem = false;
            }
        }
    }

    public void setPreviousSlot(int slot) {
        this.previousSlot = slot;
    }


    public static void useItem(Hand hand) {
        mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(hand));
        mc.gameRenderer.itemRenderer.resetEquippedProgress(hand);
    }

}
