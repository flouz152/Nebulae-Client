package beame.components.modules.movement;

import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import beame.setting.SettingList.RadioSetting;

public class WaterSpeed extends Module {
// leaked by itskekoff; discord.gg/sk3d cTBzoRVE

    public final RadioSetting mode = new RadioSetting("Режим", "ФанТайм", "ФанТайм", "МетаХвХ");

    public WaterSpeed() {
        super("WaterSpeed", Category.Movement, true, "Ускоренное плавание в воде");
        addSettings(mode);
    }

    TimerUtil timerUtil = new TimerUtil();

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate eventUpdate) {
            if (mode.is("МетаХвХ") && mc.player != null) {
                if (timerUtil.hasReached(600)) {
                    ClientPlayerEntity clientPlayerEntity = mc.player;
                    if (clientPlayerEntity != null && clientPlayerEntity.isAlive() && clientPlayerEntity.isSwimming() && clientPlayerEntity.isInWater()) {
                        boolean hasSpeedII = mc.player.isPotionActive(Effects.SPEED) && mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() == 1;
                        boolean hasSpeedIII = mc.player.isPotionActive(Effects.SPEED) && mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() == 2;
                        boolean hasSpeedIV = mc.player.isPotionActive(Effects.SPEED) && mc.player.getActivePotionEffect(Effects.SPEED).getAmplifier() == 3;
                        double speedMultiplier = 4;
                        if (hasSpeedIV) {
                            speedMultiplier = 4;
                        } else if (hasSpeedIII) {
                            speedMultiplier = 3;
                        } else if (hasSpeedII) {
                            speedMultiplier = 2;
                        }
                        clientPlayerEntity.setMotion(clientPlayerEntity.getMotion().x * speedMultiplier, clientPlayerEntity.getMotion().y, clientPlayerEntity.getMotion().z * speedMultiplier);
                    }
                    timerUtil.reset();
                }
            } else if (mode.is("ФанТайм") && mc.player != null) {
                if (mc.gameSettings.keyBindForward.isKeyDown()) {
                    mc.gameSettings.keyBindSprint.setPressed(true);
                }
                if (mc.player.isSwimming() && mc.player.isInWater() && !mc.player.isPotionActive(Effects.WITHER) && !mc.gameSettings.keyBindUseItem.isKeyDown()) {
                    BlockRayTraceResult zov = (BlockRayTraceResult) rayTraceGoidi(0, mc.player.rotationYaw, mc.player.rotationPitch, mc.player);
                    mc.playerController.processRightClick(mc.player, mc.world, Hand.MAIN_HAND);
                    if (mc.player.isPotionActive(Effects.SPEED)) {
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, mc.player.getItemStackFromSlot(EquipmentSlotType.FEET)) > 0) {
                            if (mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                            } else {
                                mc.player.moveRelative(0.0025f, new Vector3d(mc.player.moveStrafing, mc.player.moveVertical, mc.player.moveForward));
                            }
                        } else {
                            if (timerUtil.hasReached(15)) {
                                timerUtil.reset();
                                mc.player.moveRelative(0.0025f, new Vector3d(mc.player.moveStrafing, mc.player.moveVertical, mc.player.moveForward));
                            }
                        }
                    } else {
                        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, mc.player.getItemStackFromSlot(EquipmentSlotType.FEET)) > 0) {
                            if (mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) {
                            } else {
                                mc.player.moveRelative(0.0025f, new Vector3d(mc.player.moveStrafing, mc.player.moveVertical, mc.player.moveForward));
                            }
                        } else {
                            mc.player.moveRelative(0.0025f, new Vector3d(mc.player.moveStrafing, mc.player.moveVertical, mc.player.moveForward));
                        }
                    }
                }
            }
        }
    }

    public static RayTraceResult rayTraceGoidi(double zovDistance, float yawZova, float pitchZova, Entity goida) {
        Vector3d startVec = mc.player.getEyePosition(1.0F);
        Vector3d directionVec = getVectorForZovRotation(pitchZova, yawZova);
        Vector3d endVec = startVec.add(directionVec.x * zovDistance, directionVec.y * zovDistance, directionVec.z * zovDistance);

        return mc.world.rayTraceBlocks(new RayTraceContext(startVec, endVec, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, goida));
    }

    public static Vector3d getVectorForZovRotation(float zaPitch, float zaYaw) {
        float yawRadians = -zaYaw * ((float) Math.PI / 180) - (float) Math.PI;
        float pitchRadians = -zaPitch * ((float) Math.PI / 180);

        float cosYaw = MathHelper.cos(yawRadians);
        float sinYaw = MathHelper.sin(yawRadians);
        float cosPitch = -MathHelper.cos(pitchRadians);
        float sinPitch = MathHelper.sin(pitchRadians);

        return new Vector3d(sinYaw * cosPitch, sinPitch, cosYaw * cosPitch);
    }
}
