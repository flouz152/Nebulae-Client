package beame.components.modules.combat.AuraHandlers;

import beame.Essence;
import beame.util.IMinecraft;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventUpdate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

import static beame.util.math.SensUtil.getSensitivity;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class RotationHandler extends Handler implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d lFIC6VOf
    public RotationHandler() { }

    private static RotatesTask currentTask = RotatesTask.IDLE;

    private static float currentTurnSpeed;
    public static float currentReturnSpeed;
    private static int currentPriority;
    private static int currentTimeout;

    private static int idleTicks;

    private static TimerUtil returnTimer = new TimerUtil();
    private static int returnSide = 0;

    @Override
    public void event(Event event) {
        if(event instanceof EventUpdate) {
            if(currentTask != RotatesTask.RESET) {
                if (returnTimer.hasTimeElapsed(500)) {
                    returnSide = (returnSide == 0 ? 1 : 0);
                    returnTimer.reset();
                }
            }

            idleTicks++;

            if (currentTask == RotatesTask.AIM && idleTicks > currentTimeout) {
                currentTask = RotatesTask.RESET;
            }

            if (currentTask == RotatesTask.RESET) {
                Rotates rotation = new Rotates(mc.gameRenderer.getActiveRenderInfo().getYaw(), mc.gameRenderer.getActiveRenderInfo().getPitch());

                if (updateRotates(rotation, currentReturnSpeed)) {
                    currentTask = RotatesTask.IDLE;
                    currentPriority = 0;
                    Essence.getHandler().auraHelper.freeLookHandler.setActive(false);
                }
            }
        }
        if(event instanceof EventInput) {
            if(Essence.getHandler().getModuleList().aura.getCorrectionType().is("Свободная")){
                return;
            }

            EventInput movementInputEvent = (EventInput) event;

            final float forward = movementInputEvent.getForward();
            final float strafe = movementInputEvent.getStrafe();

            final double angle = MathHelper.wrapDegrees(Math.toDegrees(direction(mc.player.isElytraFlying() ? mc.player.rotationYaw : FreeLookHandler.getFreeYaw(), forward, strafe)));

            if (forward == 0
                    && strafe == 0)
                return;

            float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

            for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
                for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                    if (predictedStrafe == 0 && predictedForward == 0)
                        continue;

                    final double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(mc.player.rotationYaw, predictedForward, predictedStrafe)));
                    final double difference = Math.abs(angle - predictedAngle);

                    if (difference < closestDifference) {
                        closestDifference = (float) difference;
                        closestForward = predictedForward;
                        closestStrafe = predictedStrafe;
                    }
                }
            }

            movementInputEvent.setForward(closestForward);
            movementInputEvent.setStrafe(closestStrafe);
        }
    }

    public double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        float forward = moveForward < 0F ? -.5F : moveForward > 0F ? .5F : 1F;
        rotationYaw += ((90F * forward) * (moveStrafing > 0F ? -1 : moveStrafing < 0F ? 1 : 0)) + (moveForward < 0F ? 180F : 0);

        return Math.toRadians(rotationYaw);
    }

    public static void update(Rotates rotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
        if (currentPriority > priority) {
            return;
        }

        if (currentTask == RotatesTask.IDLE) {
            FreeLookHandler.setActive(true);
        }

        currentTurnSpeed = turnSpeed;
        currentReturnSpeed = returnSpeed;
        currentTimeout = timeout;
        currentPriority = priority;

        currentTask = RotatesTask.AIM;

        updateRotates(rotation, turnSpeed);
    }

    public static void updateRots(Rotates rotation, float turnSpeed, int timeout, int priority) {
        if (currentPriority > priority) {
            return;
        }

        if (currentTask == RotatesTask.IDLE) {
            FreeLookHandler.setActive(true);
        }

        currentTurnSpeed = turnSpeed;
        currentTimeout = timeout;
        currentPriority = priority;

        currentTask = RotatesTask.AIM;

        updateRotation(rotation, turnSpeed);
    }

    private static void updateRotation(Rotates rotation, float turnSpeed) {
        assert mc.player != null;
        Rotates currentRotation = new Rotates(mc.player);

        float yawDelta = wrapDegrees(rotation.getYaw() - currentRotation.getYaw());
        float pitchDelta = rotation.getPitch() - currentRotation.getPitch();

        float totalDelta = Math.abs(yawDelta) + Math.abs(pitchDelta);

        float yawSpeed = (totalDelta == 0) ? 0 : Math.abs(yawDelta / totalDelta) * turnSpeed;
        float pitchSpeed = (totalDelta == 0) ? 0 : Math.abs(pitchDelta / totalDelta) * turnSpeed;

        mc.player.rotationYaw += getSensitivity(clamp(yawDelta, -yawSpeed, yawSpeed));
        mc.player.rotationPitch = clamp(mc.player.rotationPitch + getSensitivity(clamp(pitchDelta, -pitchSpeed, pitchSpeed)), -90, 90);

        Rotates finalRotation = new Rotates(mc.player);

        idleTicks = 0;

        finalRotation.getDelta(rotation);
    }

    public static Vector2f applySensitivityPatch(Vector2f rotation, Vector2f previousRotates) {
        double sens = mc.gameSettings.mouseSensitivity;
        double gcd = Math.pow(sens * (double) 0.6F + (double) 0.2F, 3.0D) * 8.0D;

        double prevYaw = previousRotates.x;
        double prevPitch = previousRotates.y;

        double currentYaw = rotation.x;
        double currentPitch = rotation.y;

        double yaw = (Math.ceil(((currentYaw - prevYaw) / gcd) / 0.15F) * gcd) * 0.15F;
        double pitch = (Math.ceil(((currentPitch - prevPitch) / gcd) / 0.15F) * gcd) * 0.15F;

        return new Vector2f((float) (prevYaw + yaw), (float) (prevPitch + pitch));
    }

    private static boolean updateRotates(Rotates rotation, float turnSpeed) {
        Rotates currentRotation = new Rotates(mc.player);

        float yawDelta = wrapDegrees(rotation.getYaw() - currentRotation.getYaw());
        float pitchDelta = rotation.getPitch() - currentRotation.getPitch();

        float totalDelta = Math.abs(yawDelta) + Math.abs(pitchDelta);

        float yawSpeed = (totalDelta == 0) ? 0 : Math.abs(yawDelta / totalDelta) * turnSpeed;
        float pitchSpeed = (totalDelta == 0) ? 0 : Math.abs(pitchDelta / totalDelta) * turnSpeed;

        Vector2f rot = applySensitivityPatch(
                new Vector2f(
                        mc.player.rotationYaw + clamp(yawDelta, -yawSpeed, yawSpeed),
                        MathHelper.clamp(mc.player.rotationPitch + clamp(pitchDelta, -pitchSpeed, pitchSpeed), -90, 90)
                ),
                new Vector2f(
                        mc.player.rotationYaw,
                        mc.player.rotationPitch
                )
        );

        mc.player.rotationYaw = rot.x;
        mc.player.rotationPitch = rot.y;

//        mc.player.rotationYaw += GCDUtil.getSensitivity(clamp(yawDelta, -yawSpeed, yawSpeed));
//        mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch + GCDUtil.getSensitivity(clamp(pitchDelta, -pitchSpeed, pitchSpeed)), -90, 90);

        Rotates finalRotation = new Rotates(mc.player);

        idleTicks = 0;

        return finalRotation.getDelta(rotation) < (currentTask.equals(RotatesTask.RESET) ? currentReturnSpeed : currentTurnSpeed);
    }

    public enum RotatesTask {
        AIM,
        RESET,
        IDLE
    }
}
