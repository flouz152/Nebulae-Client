package beame.components.modules.movement;

import events.Event;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ElytraItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.potion.Effects;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

public class ElytraRecast extends Module {
// leaked by itskekoff; discord.gg/sk3d K0yqMgrx


    public BooleanSetting changePitch = new BooleanSetting("ChangePitch", true);
    public SliderSetting pitchValue = new SliderSetting("PitchValue", 55f, -90f, 90f, 1).setVisible(() -> changePitch.get());
    public BooleanSetting autoWalk = new BooleanSetting("Автом ходьба", true);
    public BooleanSetting autoJump = new BooleanSetting("AutoJump", true);
    public BooleanSetting allowBroken = new BooleanSetting("На сломанной", false);

    private float jitter;
    private long startTime = System.currentTimeMillis();

    public ElytraRecast() {
        super("ElytraRecast", Category.Movement, true, "");
        addSettings(  autoWalk, allowBroken);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventMotion eventMotion) {
            if (changePitch.get()) {
                eventMotion.setPitch(90 - Math.abs(jitter / 2f));
            }
        }

        if (event instanceof EventUpdate) {
            onUpdate();
        }
    }

    @Override
    public void onDisable() {
        if (!mc.gameSettings.keyBindForward.isKeyDown())
            mc.gameSettings.keyBindForward.setPressed(false);
        if (!mc.gameSettings.keyBindJump.isKeyDown())
            mc.gameSettings.keyBindJump.setPressed(false);
    }

    private void onUpdate() {
        if (autoJump.get()) mc.gameSettings.keyBindJump.setPressed(true);
        if (autoWalk.get()) mc.gameSettings.keyBindForward.setPressed(true);

        if (!mc.player.isElytraFlying() && mc.player.fallDistance > 0 && checkElytra() && !mc.player.isElytraFlying())
            castElytra();

        jitter = (20 * (float) Math.sin((System.currentTimeMillis() - startTime) / 50f));
        mc.player.jumpTicks = 0;
    }

    public boolean castElytra() {
        if (checkElytra() && check()) {
            mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.START_FALL_FLYING));
            return true;
        }
        return false;
    }

    private boolean checkElytra() {
        if (mc.player.movementInput.jump && !mc.player.abilities.isFlying
                && mc.player.getRidingEntity() == null && !mc.player.isOnLadder()) {
            ItemStack is = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            return is.getItem() == Items.ELYTRA && (ElytraItem.isUsable(is) || allowBroken.get());
        }
        return false;
    }

    private boolean check() {
        return mc.player != null && mc.world != null && !mc.player.isCreative() && !mc.player.isSpectator()
                && !mc.player.isPotionActive(Effects.LEVITATION);
    }
}