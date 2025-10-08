package beame.components.modules.movement;

import beame.util.math.TimerUtil;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventInput;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SEntityMetadataPacket;
import net.minecraft.util.math.MathHelper;
import beame.setting.SettingList.RadioSetting;

public class Flight extends Module {
// leaked by itskekoff; discord.gg/sk3d WTJ0UlL1
    public final RadioSetting mode = new RadioSetting("Режим", "СпукиТайм", "СпукиТайм");

    public Flight() {
        super("Flight", Category.Movement, true, "Позволяет летать");
        addSettings(mode);
    }

    private final TimerUtil timer = new TimerUtil();
    private int oldItemSlot = -1;


    @Override
    public void event(Event event) {
        if (event instanceof EventMotion eventMotion) {
            onMotion(eventMotion);
        }
        if (event instanceof EventUpdate) {
            onUpdate();
        }
        if (event instanceof EventPacket eventPacket) {
            onPacket(eventPacket);
        }
        if (event instanceof EventInput eventInput) {
            onInput(eventInput);
        }
    }

    private void onMotion(EventMotion eventMotion) {
        if (mc.player == null || !isElytraEquipped()) return;
        eventMotion.setPitch(0);
    }

    private void onUpdate() {
        if (mc.player == null) return;
        if (!isElytraEquipped()) {
            equipElytra();
        }
        if (mc.player.isOnGround()) {
            mc.player.jump();
        }
        if (isElytraEquipped() && mc.player.getMotion().y < 0.3 && mc.player.getMotion().y > 0.20) {
            if (mc.player.fallDistance == 0.0f) {
                mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
                setMotion(0.02);
                mc.player.setMotion(mc.player.getMotion().x, 0.29f, mc.player.getMotion().z);
            }
        }
    }

    private void onPacket(EventPacket eventPacket) {
        if (eventPacket.isReceivePacket() && eventPacket.getPacket() instanceof SEntityMetadataPacket packet) {
            if (packet.getEntityId() == mc.player.getEntityId()) {
                eventPacket.setCancel(true);
            }
        }
    }

    private void onInput(EventInput eventInput) {
        eventInput.setForward(0);
        eventInput.setStrafe(0);
        eventInput.setCancel(false);
        eventInput.setJump(false);
        eventInput.setSneak(false);
    }

    private boolean isElytraEquipped() {
        return mc.player.inventory.armorInventory.get(2).getItem() == Items.ELYTRA;
    }

    private void equipElytra() {
        if (!timer.hasTimeElapsed(500)) return;

        for (int i = 0; i < 36; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == Items.ELYTRA) {
                ClientPlayerEntity player = mc.player;
                if (player == null || !(player.openContainer instanceof PlayerContainer container)) return;

                int targetSlot = i < 9 ? 36 + i : i;
                int chestSlot = 38;
                mc.playerController.windowClick(container.windowId, targetSlot, chestSlot, ClickType.SWAP, player);
                oldItemSlot = i;
                timer.reset();
                break;
            }
        }
    }

    private void setMotion(double speed) {
        double yaw = Math.toRadians(mc.player.rotationYaw);
        double motionX = -MathHelper.sin((float) yaw) * speed;
        double motionZ = MathHelper.cos((float) yaw) * speed;
        mc.player.setMotion(motionX, mc.player.getMotion().y, motionZ);
    }

    @Override
    public void onDisable() {
        if (oldItemSlot != -1) {
            ClientPlayerEntity player = mc.player;
            if (player != null && player.openContainer instanceof PlayerContainer container) {
                int chestSlot = 38;
                int targetSlot = oldItemSlot < 9 ? 36 + oldItemSlot : oldItemSlot;
                mc.playerController.windowClick(container.windowId, targetSlot, chestSlot, ClickType.SWAP, player);
                oldItemSlot = -1;
            }
        }
        if (mc.player != null) {
            mc.player.setMotion(0, mc.player.getMotion().y, 0);
        }

        timer.reset();
        super.onDisable();
    }

    @Override
    public void onEnable() {
        oldItemSlot = -1;
        timer.reset();
        super.onEnable();
    }

}

