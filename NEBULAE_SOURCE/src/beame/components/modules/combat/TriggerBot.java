package beame.components.modules.combat;

import beame.util.math.MathUtil;
import beame.util.math.TimerUtil2;
import events.Event;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.CarpetBlock;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import beame.setting.SettingList.BooleanSetting;

public class TriggerBot extends Module {
// leaked by itskekoff; discord.gg/sk3d yePqsP2w
    private final BooleanSetting onlyCritical = new BooleanSetting("Только криты", true, 0);
    private final BooleanSetting spaceCrits = new BooleanSetting("Криты только с пробелом", true);
    private final BooleanSetting isUsingFood = new BooleanSetting("Не бить если ешь", true);
    private long cpsLimit = 0L;

    public TriggerBot() {
        super("Trigger Bot", Category.Combat, true, "Автоматически бьет энтити при наведении");
        addSettings(onlyCritical, spaceCrits, isUsingFood);
    }
    private final TimerUtil2 timerUtil = new TimerUtil2();

    @Override
    public void event(Event event) {
        if (event instanceof Render2DEvent) {
            if (cpsLimit > System.currentTimeMillis()) {
                --cpsLimit;
            }
            if (mc.objectMouseOver.getType() == RayTraceResult.Type.ENTITY && canAttack() && cpsLimit <= System.currentTimeMillis()) {
                cpsLimit = System.currentTimeMillis() + 550L;
                Entity target = ((EntityRayTraceResult) mc.objectMouseOver).getEntity();
                mc.playerController.attackEntity(mc.player, target);
                mc.player.swingArm(Hand.MAIN_HAND);
            }
        }
    }

    public float getAttackStrength() {
        float val = (float) MathUtil.random(8, 9);
        return MathHelper.clamp(((float) mc.player.ticksSinceLastSwing) / val, 0.0F, 1.0F);
    }

    private boolean canAttack() {
        if (isUsingFood.get() && mc.player.isHandActive() && mc.player.getActiveItemStack().getItem().isFood()) {
            return false;
        }

        boolean onSpace = spaceCrits.get() && mc.player.isOnGround() && !mc.gameSettings.keyBindJump.isKeyDown();
        boolean isInWater = (mc.player.isInWater() && mc.player.areEyesInFluid(FluidTags.WATER)) ||
                (mc.player.isInLava() && mc.player.areEyesInFluid(FluidTags.LAVA)) || mc.player.isSwimming();
        boolean reasonForAttack = mc.player.isPotionActive(Effects.LEVITATION) || mc.player.isPotionActive(Effects.BLINDNESS) ||
                mc.player.isPotionActive(Effects.SLOW_FALLING) || mc.player.isOnLadder() ||
                mc.player.isPassenger() || mc.player.abilities.isFlying ||
                mc.player.isElytraFlying() || isInWater;

        Block cur = mc.world.getBlockState(new BlockPos(mc.player.getPosX(), mc.player.getPosY() + 0.1, mc.player.getPosZ())).getBlock();
        boolean isSlab = cur instanceof SlabBlock || cur instanceof StairsBlock;
        boolean isCarpet = cur instanceof CarpetBlock;
        boolean isFalling = !isSlab && !isCarpet && (!mc.player.isOnGround() && mc.player.fallDistance > 0);
        boolean isReady = timerUtil.hasTimeElapsed() && mc.player.getCooledAttackStrength(1.5F) >= 1F && getAttackStrength() > 0.96f;

        if (isSlab) {
            double yPos = mc.player.getPosY();
            double fractionalY = yPos - Math.floor(yPos);
            if (fractionalY <= 0.5) {
                if (!onlyCritical.get()) {
                    return timerUtil.hasTimeElapsed() && mc.player.getCooledAttackStrength(0.5F) >= 1F;
                }
                if (spaceCrits.get()) {
                    return timerUtil.hasTimeElapsed() && mc.player.getCooledAttackStrength(0.5F) >= 1F;
                }
                return false;
            }
        }

        if (isCarpet) {
            double yPos = mc.player.getPosY();
            double fractionalY = yPos - Math.floor(yPos);
            if (fractionalY > 0.1) {
                if (onlyCritical.get()) {
                    return isReady;
                } else {
                    return timerUtil.hasTimeElapsed() && mc.player.getCooledAttackStrength(0.5F) >= 1F;
                }
            }
        }

        if (!reasonForAttack && onlyCritical.get()) {
            if (mc.player.fallDistance > 0 && !mc.player.isOnGround()) {
                return isReady;
            }
            return (isReady && onSpace);
        }

        return (isReady && (isInWater || !onlyCritical.get()));
    }
}