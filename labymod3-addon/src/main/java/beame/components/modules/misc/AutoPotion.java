package beame.components.modules.misc;

import beame.components.modules.player.AutoInvisible;
import beame.util.ClientHelper;
import beame.util.math.TimerUtil;
import beame.util.player.PotionUtil;
import events.Event;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;

import java.util.function.Supplier;

public class AutoPotion extends Module {
// leaked by itskekoff; discord.gg/sk3d JHs7rqlM

    private static EnumSetting potions = new EnumSetting("Бафать",
            new BooleanSetting("Силу", true),
            new BooleanSetting("Скорость", true),
            new BooleanSetting("Огнестойкость", true));
    private BooleanSetting autoDisable = new BooleanSetting("Авто выключение", false);
    private BooleanSetting onlyPvP = new BooleanSetting("Только в PVP", false);
    public boolean isActive;
    private int selectedSlot;
    private float previousPitch;
    private TimerUtil time = new TimerUtil();
    private PotionUtil potionUtil = new PotionUtil();

    public AutoPotion() {
        super("AutoPotion", Category.Player, true, "Автоматически бросает зелья под себя");
        addSettings(potions, onlyPvP, autoDisable);
    }

    public boolean isActivePotion;

    @Override
    public void event(Event event) {
        if (AutoInvisible.isDrinking()) {
            return;
        }
        

        
        if (event instanceof EventUpdate eventUpdate) {
            if (mc.player.isElytraFlying() || (mc.player.isHandActive() && !mc.player.isBlocking())) return;
            if (this.isActive() && this.shouldUsePotion()) {
                for (PotionType potionType : PotionType.values()) {
                    isActivePotion = potionType.isEnabled();
                }
            } else {
                isActivePotion = false;
            }

            if (this.isActive() && this.shouldUsePotion() && previousPitch == mc.player.getLastReportedPitch()) {
                int oldItem = mc.player.inventory.currentItem;
                this.selectedSlot = -1;

                for (PotionType potionType : PotionType.values()) {
                    if (potionType.isEnabled()) {
                        int slot = this.findPotionSlot(potionType);
                        if (this.selectedSlot == -1) {
                            this.selectedSlot = slot;
                        }

                        this.isActive = true;
                    }
                }

                if (this.selectedSlot > 8) {
                    mc.playerController.pickItem(this.selectedSlot);
                }

                mc.player.connection.sendPacket(new CHeldItemChangePacket(oldItem));
            }

            if (time.hasTimeElapsed(500L)) {
                try {
                    this.reset();
                    this.selectedSlot = -2;
                } catch (Exception ignored) {
                }
            }

            this.potionUtil.changeItemSlot(this.selectedSlot == -2);
            if (this.autoDisable.get() && this.isActive && this.selectedSlot == -2) {
                this.setState(false);
                this.isActive = false;
            }
        }
        if (event instanceof EventMotion e) {
            if (AutoInvisible.isDrinking()) {
                return;
            }
            
            if (!this.isActive() || !this.shouldUsePotion()) return;

            float[] angles = new float[]{mc.player.rotationYaw, 90.0F};
            this.previousPitch = 90.0F;
            e.setYaw(angles[0]);
            e.setPitch(this.previousPitch);
            mc.player.rotationPitchHead = this.previousPitch;
            mc.player.rotationYawHead = angles[0];
            mc.player.renderYawOffset = angles[0];
        }
        return;
    }

    private boolean shouldUsePotion() {
        return !(onlyPvP.get() && !ClientHelper.isPvP());
    }

    private void reset() {
        for (PotionType potionType : PotionType.values()) {
            if (potionType.isPotionSettingEnabled().get()) {
                potionType.setEnabled(this.isPotionActive(potionType));
            }
        }
    }

    private int findPotionSlot(PotionType type) {
        int hbSlot = this.getPotionIndexHb(type.getPotionId());
        if (hbSlot != -1) {
            this.potionUtil.setPreviousSlot(mc.player.inventory.currentItem);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            PotionUtil.useItem(Hand.MAIN_HAND);
            type.setEnabled(false);
            time.reset();
            return hbSlot;
        } else {
            int invSlot = this.getPotionIndexInv(type.getPotionId());
            if (invSlot != -1) {
                this.potionUtil.setPreviousSlot(mc.player.inventory.currentItem);
                mc.playerController.pickItem(invSlot);
                PotionUtil.useItem(Hand.MAIN_HAND);
                mc.player.connection.sendPacket(new CHeldItemChangePacket(mc.player.inventory.currentItem));
                type.setEnabled(false);
                time.reset();
                return invSlot;
            } else {
                return -1;
            }
        }
    }

    public boolean isActive() {
        for (PotionType potionType : PotionType.values()) {
            if (potionType.isPotionSettingEnabled().get() && potionType.isEnabled()) {
                return true;
            }
        }
        return false;
    }

    private boolean isPotionActive(PotionType type) {
        if (mc.player.isPotionActive(type.getPotion())) {
            this.isActive = false;
            return false;
        } else {
            return this.getPotionIndexInv(type.getPotionId()) != -1 || this.getPotionIndexHb(type.getPotionId()) != -1;
        }
    }

    private int getPotionIndexHb(int id) {
        for (int i = 0; i < 9; ++i) {
            for (EffectInstance potion : net.minecraft.potion.PotionUtils.getEffectsFromStack(mc.player.inventory.getStackInSlot(i))) {
                if (potion.getPotion() == Effect.get(id) && mc.player.inventory.getStackInSlot(i).getItem() == Items.SPLASH_POTION) {
                    return i;
                }
            }
        }

        return -1;
    }

    private int getPotionIndexInv(int id) {
        for (int i = 9; i < 36; ++i) {
            for (EffectInstance potion : net.minecraft.potion.PotionUtils.getEffectsFromStack(mc.player.inventory.getStackInSlot(i))) {
                if (potion.getPotion() == Effect.get(id) && mc.player.inventory.getStackInSlot(i).getItem() == Items.SPLASH_POTION) {
                    return i;
                }
            }
        }

        return -1;
    }

    @Override
    protected void onDisable() {
        isActive = false;
        super.onDisable();
    }

    enum PotionType {
        STRENGHT(Effects.STRENGTH, 5, () -> potions.getB(0)),
        SPEED(Effects.SPEED, 1, () -> potions.getB(1)),
        FIRE_RESIST(Effects.STRENGTH, 12, () -> potions.getB(2));

        private final Effect potion;
        private final int potionId;
        private final Supplier<Boolean> potionSetting;
        private boolean enabled;

        PotionType(Effect potion, int potionId, Supplier<Boolean> potionSetting) {
            this.potion = potion;
            this.potionId = potionId;
            this.potionSetting = potionSetting;
        }

        public Effect getPotion() {
            return this.potion;
        }

        public int getPotionId() {
            return this.potionId;
        }

        public Supplier<Boolean> isPotionSettingEnabled() {
            return this.potionSetting;
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean var1) {
            this.enabled = var1;
        }

    }
}
