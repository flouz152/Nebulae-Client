package beame.components.modules.movement;

import beame.Essence;
import beame.components.modules.combat.Aura;
import beame.util.math.TimerUtil;
import beame.util.other.MoveUtil;
import beame.util.player.DamagePlayerUtility;
import beame.util.player.StrafeMovement;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.*;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoulSandBlock;
import net.minecraft.block.material.Material;
import net.minecraft.network.play.client.CEntityActionPacket;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.Objects;

public class Strafe extends Module {
// leaked by itskekoff; discord.gg/sk3d rBtgrzUb
    public RadioSetting mode = new RadioSetting("Тип", "FunTime Ice & Snow", "FunTime Ice & Snow", "ХвХ");

    private final BooleanSetting damageBoost = new BooleanSetting("Буст от дамага", true);
    private final SliderSetting boostSpeed = new SliderSetting("Скорость буста", 0.7f, 0.1F, 1.5f, 0.1F).setVisible(() -> damageBoost.get());
    private final BooleanSetting buffBoost = new BooleanSetting("Буст от зелья", false);
    private final SliderSetting speedIIMultiplierSetting = new SliderSetting("Скорость 2", 1.04f, 1.0f, 1.5f, 0.01f).setVisible(() -> buffBoost.get());
    private final SliderSetting speedIIIMultiplierSetting = new SliderSetting("Скорость 3", 1.05f, 1.0f, 1.5f, 0.01f).setVisible(() -> buffBoost.get());
    private final SliderSetting speedIVMultiplierSetting = new SliderSetting("Скорость 4", 1.05f, 1.0f, 1.5f, 0.01f).setVisible(() -> buffBoost.get());
    private final DamagePlayerUtility damageUtil = new DamagePlayerUtility();
    private final StrafeMovement strafeMovement = new StrafeMovement();
    private final Aura killAura = new Aura();
    public static int waterTicks;

    public Strafe() {
        super("Strafe", Category.Movement, true, "Позволяет стрейфиться");
        addSettings(mode, damageBoost, boostSpeed, buffBoost, speedIIMultiplierSetting, speedIIIMultiplierSetting, speedIVMultiplierSetting);
    }

    TimerUtil stopWatch = new TimerUtil();

    @Override
    public void event(Event event) {
        if (Essence.getHandler().getModuleList().freeCamera.isState()) return;
        if (event instanceof EventAction) {
            handleEventAction((EventAction) event);
        } else if (event instanceof EventMove) {
            handleEventMove((EventMove) event);
        } else if (event instanceof EventPostMove) {
            handleEventPostMove((EventPostMove) event);
        } else if (event instanceof EventPacket) {
            handleEventPacket((EventPacket) event);
        } else if (event instanceof EventDamage) {
            handleDamageEvent((EventDamage) event);
        }
    }

    private void handleDamageEvent(EventDamage damage) {
        if (damageBoost.get()) {
            damageUtil.processDamage(damage);
        }
    }

    private void handleEventAction(EventAction action) {
        if (strafes()) {
            handleStrafesEventAction(action);
        }
        if (strafeMovement.isNeedSwap()) {
            handleNeedSwapEventAction(action);
        }
    }

    private void handleEventMove(EventMove eventMove) {
        if (buffBoost.get()) {
            if (strafes()) {
                handleStrafesEventMove(eventMove);
            } else {
                strafeMovement.setOldSpeed(1);
            }
        } else {
            strafeMovement.setOldSpeed(1);
        }
    }

    private void handleEventPostMove(EventPostMove eventPostMove) {
        strafeMovement.postMove(eventPostMove.getHorizontalMove());
    }

    private void handleEventPacket(EventPacket packet) {
        if (packet.isReceivePacket()) {
            if (damageBoost.get()) {
                damageUtil.onPacketEvent(packet);
            }
            handleReceivePacketEventPacket(packet);
        }
    }

    private void handleStrafesEventAction(EventAction action) {
        if (CEntityActionPacket.lastUpdatedSprint != strafeMovement.isNeedSprintState()) {
            action.setSprintState(!CEntityActionPacket.lastUpdatedSprint);
        }
    }

    private void handleStrafesEventMove(EventMove eventMove) {
        if (!killAura.isState() || killAura.getTarget() == null) {
            if (damageBoost.get()) {
                damageUtil.time(700L);
            }

            float damageSpeed = boostSpeed.get() / 10.0F;

            boolean hasSpeedII = mc.player.isPotionActive(Effects.SPEED) && Objects.requireNonNull(mc.player.getActivePotionEffect(Effects.SPEED)).getAmplifier() == 1;
            boolean hasSpeedIII = mc.player.isPotionActive(Effects.SPEED) && Objects.requireNonNull(mc.player.getActivePotionEffect(Effects.SPEED)).getAmplifier() == 2;
            boolean hasSpeedIV = mc.player.isPotionActive(Effects.SPEED) && Objects.requireNonNull(mc.player.getActivePotionEffect(Effects.SPEED)).getAmplifier() == 3;

            double speedMultiplier = 1.0;

            if (buffBoost.get()) {
                if (hasSpeedIV) {
                    speedMultiplier = speedIVMultiplierSetting.get();
                } else if (hasSpeedIII) {
                    speedMultiplier = speedIIIMultiplierSetting.get();
                } else if (hasSpeedII) {
                    speedMultiplier = speedIIMultiplierSetting.get();
                }
            }

            double speed = strafeMovement.calculateSpeed(eventMove, damageBoost.get(), damageUtil.isNormalDamage(), false, damageSpeed) * speedMultiplier;
            MoveUtil.setMoveMotion(eventMove, speed);
        }
    }

    private void handleNeedSwapEventAction(EventAction action) {
        action.setSprintState(!mc.player.serverSprintState);
        strafeMovement.setNeedSwap(false);
    }

    private void handleReceivePacketEventPacket(EventPacket packet) {
        if (packet.getPacket() instanceof SPlayerPositionLookPacket) {
            strafeMovement.setOldSpeed(0);
        }
    }

    public boolean strafes() {
        if (isInvalidPlayerState()) {
            return false;
        }

        BlockPos playerPosition = new BlockPos(mc.player.getPositionVec());
        BlockPos abovePosition = playerPosition.up();
        BlockPos belowPosition = playerPosition.down();

        if (isSurfaceLiquid(abovePosition, belowPosition)) {
            return false;
        }

        return !isPlayerInWebOrSoulSand(playerPosition) && isPlayerAbleToStrafe();
    }

    private boolean isInvalidPlayerState() {
        return mc.player == null || mc.world == null || mc.player.isSneaking() || mc.player.isElytraFlying() || mc.player.isInWater() || mc.player.isInLava();
    }

    private boolean isSurfaceLiquid(BlockPos abovePosition, BlockPos belowPosition) {
        Block aboveBlock = mc.world.getBlockState(abovePosition).getBlock();
        Block belowBlock = mc.world.getBlockState(belowPosition).getBlock();
        return aboveBlock instanceof AirBlock && belowBlock == Blocks.WATER;
    }

    private boolean isPlayerInWebOrSoulSand(BlockPos playerPosition) {
        Material playerMaterial = mc.world.getBlockState(playerPosition).getMaterial();
        Block oneBelowBlock = mc.world.getBlockState(playerPosition.down()).getBlock();
        return playerMaterial == Material.WEB || oneBelowBlock instanceof SoulSandBlock;
    }

    private boolean isPlayerAbleToStrafe() {
        return !mc.player.abilities.isFlying && !mc.player.isPotionActive(Effects.LEVITATION);
    }

    @Override
    public void onEnable() {
        strafeMovement.setOldSpeed(0);
        super.onEnable();
    }
}