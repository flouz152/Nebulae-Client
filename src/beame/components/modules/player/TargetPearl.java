package beame.components.modules.player;

import beame.Essence;
import beame.components.modules.combat.Aura;
import beame.components.modules.combat.AuraHandlers.component.core.combat.Rotation;
import beame.components.modules.combat.AuraHandlers.component.core.combat.RotationComponent;
import beame.components.modules.render.Predictions;
import beame.util.math.TimerUtil;
import beame.util.other.Script;
import beame.util.other.ViaUtil;
import beame.util.player.InventoryUtility;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EnderPearlEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.time.StopWatch;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

public class TargetPearl extends Module {
// leaked by itskekoff; discord.gg/sk3d haTJxbAM
    private final RadioSetting mode = new RadioSetting("Режим", "Авто", "Авто", "Клавиша");
    private final BindSetting bind = new BindSetting("Кинуть перл", -1).setVisible(() -> mode.is("Клавиша"));
    private final BooleanSetting onlyTarget = new BooleanSetting("Только за таргетом", false);
    private final SliderSetting distance = new SliderSetting("Минимальная дистанция до перла", 10, 8, 20, 1);
    private final StopWatch stopWatch = new StopWatch();
    private final Script script = new Script();
    private Entity lastTarget = null;
    private final TimerUtil targetTimer = new TimerUtil();

    private boolean cooldownCheck() {
        return !mc.player.getCooldownTracker().hasCooldown(Items.ENDER_PEARL);
    }

    public TargetPearl() {
        super("TargetPearl", Category.Player, true, "Помощник в преследовании за эндер-жемчугом игрока");
        addSettings(mode, bind, onlyTarget, distance);
    }

    @Override
    public void event(Event event) {
        if(event instanceof EventKey) {
            EventKey k = (EventKey) event;
            if(k.key == bind.get() && k.key != 0 && mode.is("Клавиша") && cooldownCheck() && script.isFinished()) {
                aimAndThrowPearl();
            }
        } else if (event instanceof EventUpdate) {
            if (mode.is("Авто") && cooldownCheck() && script.isFinished()) {
                aimAndThrowPearl();
            }
            script.update();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        targetTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        resetRotation();
        script.cleanup();
    }

    public float[] calculateYawPitch(Vector3d targetPosition, double velocity) {
        Vector3d playerPosition = mc.player.getPositionVec();
        double deltaX = targetPosition.x - playerPosition.x;
        double deltaY = targetPosition.y - (playerPosition.y + mc.player.getEyeHeight());
        double deltaZ = targetPosition.z - playerPosition.z;
        float yaw = (float) (Math.atan2(deltaZ, deltaX) * (180 / Math.PI)) - 90;
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        float gravity = 0.03F;
        float pitch = (float) -Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(velocity * velocity * velocity * velocity - gravity * (gravity * horizontalDistance * horizontalDistance + 2 * deltaY * velocity * velocity))) / (gravity * horizontalDistance)));
        return new float[]{yaw, pitch};
    }

    private void resetRotation() {
        if (mc.player != null) {
            RotationComponent.getInstance().stopRotation();
        }
    }

    public void aimAndThrowPearl() {
        Vector3d targetPearlLandingPosition = getTargetPearlLandingPosition();
        if (targetPearlLandingPosition == null) return;

        double distanceToTarget = mc.player.getPositionVec().distanceTo(targetPearlLandingPosition);
        if (distanceToTarget > distance.get()) {
            float[] yawPitch = calculateYawPitch(targetPearlLandingPosition, 1.5F);
            Vector3d trajectoryPearl = checkTrajectory(yawPitch[0], yawPitch[1]);
            if (trajectoryPearl == null) return;

            if (ViaUtil.allowedBypass()) {
                InventoryUtility.inventorySwapClick(Items.ENDER_PEARL, false);
                script.cleanup().addTickStep(1, this::resetRotation);
            } else {
                RotationComponent.update(new Rotation(yawPitch[0], yawPitch[1]), 360, 360, 0, 100);
                script.cleanup()
                        .addTickStep(1, () -> {
                            InventoryUtility.inventorySwapClick(Items.ENDER_PEARL, true);
                        })
                        .addTickStep(2, this::resetRotation);
            }
        }
    }

    private Vector3d getTargetPearlLandingPosition() {
        if (mc.world == null || mc.player == null) {

            return null;
        }

        Aura aura = (Aura) Essence.getHandler().getModuleList().aura;
        Entity currentTarget = null;
        
        if (aura != null) {
            currentTarget = aura.getTarget();
            if (currentTarget != null) {
                lastTarget = currentTarget;
                targetTimer.reset();
            }
        }

        if (lastTarget != null && targetTimer.finished(40000)) {
            lastTarget = null;
            return null;
        }

        if (onlyTarget.get() && lastTarget == null) {
            return null;
        }

        Entity targetToCheck = currentTarget != null ? currentTarget : lastTarget;
        if (targetToCheck != null) {
        }

        int entityCount = 0;
        int pearlCount = 0;
        for (Entity entity : mc.world.getAllEntities()) {
            entityCount++;

            
            if (entity instanceof EnderPearlEntity pearl) {
                pearlCount++;
                Entity shooter = pearl.getShooter();


                if (onlyTarget.get()) {
                    if (targetToCheck == null) {

                        return null;
                    }
                    if (shooter == null || shooter.equals(targetToCheck)) {
                        return calculatePearlLanding(pearl);
                    }
                } else {

                    return calculatePearlLanding(pearl);
                }
            }
        };
        return null;
    }

    private Vector3d calculatePearlLanding(EnderPearlEntity pearl) {
        Vector3d pearlPosition = pearl.getPositionVec();
        Vector3d pearlMotion = pearl.getMotion();
        Vector3d lastPosition;

        for (int i = 0; i <= 300; i++) {
            lastPosition = pearlPosition;
            pearlPosition = pearlPosition.add(pearlMotion);
            pearlMotion = Predictions.updatePearlMotion(pearl, pearlMotion, pearlPosition);

            if (Predictions.shouldEntityHit(pearlPosition, lastPosition) || pearlPosition.y <= 0) {
                return lastPosition;
            }
        }
        return null;
    }

    private Vector3d checkTrajectory(float yaw, float pitch) {
        if (Float.isNaN(pitch)) return null;
        float yawRad = yaw / 180.0f * 3.1415927f;
        float pitchRad = pitch / 180.0f * 3.1415927f;
        double x = mc.player.getPosX() - MathHelper.cos(yawRad) * 0.16f;
        double y = mc.player.getPosY() + mc.player.getEyeHeight(mc.player.getPose()) - 0.1;
        double z = mc.player.getPosZ() - MathHelper.sin(yawRad) * 0.16f;
        double motionX = -MathHelper.sin(yawRad) * MathHelper.cos(pitchRad) * 0.4f;
        double motionY = -MathHelper.sin(pitchRad) * 0.4f;
        double motionZ = MathHelper.cos(yawRad) * MathHelper.cos(pitchRad) * 0.4f;
        final float distance = MathHelper.sqrt((float) (motionX * motionX + motionY * motionY + motionZ * motionZ));
        motionX /= distance;
        motionY /= distance;
        motionZ /= distance;
        motionX *= 1.5f;
        motionY *= 1.5f;
        motionZ *= 1.5f;
        if (!mc.player.isOnGround()) motionY += mc.player.getMotion().getY();
        return traceTrajectory(new Vector3d(x, y, z), new Vector3d(motionX, motionY, motionZ));
    }

    private Vector3d traceTrajectory(Vector3d pearlPos, Vector3d motion) {
        Vector3d lastPos;
        for (int i = 0; i <= 300; i++) {
            lastPos = pearlPos;
            pearlPos = pearlPos.add(motion);
            motion = Predictions.updatePearlMotion(new EnderPearlEntity(mc.world, 0, 0, 0), motion, pearlPos);

            if (Predictions.shouldEntityHit(pearlPos, lastPos) || pearlPos.y <= 0) {
                return pearlPos;
            }
        }
        return null;
    }
}
 