package beame.components.modules.combat.AuraHandlers;

import beame.Essence;
import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventUpdate;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;

import static beame.util.math.SensUtil.getSensitivity;
import static net.minecraft.util.math.MathHelper.clamp;
import static net.minecraft.util.math.MathHelper.wrapDegrees;

public class RotationHandler extends Handler {
    private static Task currentTask = Task.IDLE;
    private static Rotates targetRotates = new Rotates();
    private static float currentTurnSpeed;
    public static float currentReturnSpeed;
    private static int currentPriority;
    private static int currentTimeout;

    private static int idleTicks;

    @Override
    public void event(Event event) {
        if (mc.player == null) {
            return;
        }

        if (event instanceof EventUpdate) {
            tickRotation();
            return;
        }

        if (event instanceof EventInput movementEvent) {
            handleMovementCorrection(movementEvent);
        }
    }

    private void tickRotation() {
        if (currentTask == Task.IDLE) {
            return;
        }

        if (currentTask == Task.AIM) {
            if (++idleTicks > currentTimeout) {
                beginReset();
                return;
            }

            if (applyRotation(targetRotates, currentTurnSpeed)) {
                beginReset();
            }
            return;
        }

        if (currentTask == Task.RESET) {
            Rotates resetTarget = new Rotates(mc.gameRenderer.getActiveRenderInfo().getYaw(),
                    mc.gameRenderer.getActiveRenderInfo().getPitch());
            if (applyRotation(resetTarget, currentReturnSpeed)) {
                finishReset();
            }
        }
    }

    private void handleMovementCorrection(EventInput movementEvent) {
        if (Essence.getHandler().getModuleList().aura.correctionType.is("Свободная")) {
            return;
        }

        float forward = movementEvent.getForward();
        float strafe = movementEvent.getStrafe();
        if (forward == 0 && strafe == 0) {
            return;
        }

        double targetAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(
                mc.player.isElytraFlying() ? mc.player.rotationYaw : FreeLookHandler.getFreeYaw(),
                forward,
                strafe)));

        float bestForward = 0;
        float bestStrafe = 0;
        float smallestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedForward == 0 && predictedStrafe == 0) {
                    continue;
                }

                double predictedAngle = MathHelper.wrapDegrees(Math.toDegrees(direction(
                        mc.player.rotationYaw,
                        predictedForward,
                        predictedStrafe)));
                float difference = (float) Math.abs(targetAngle - predictedAngle);
                if (difference < smallestDifference) {
                    smallestDifference = difference;
                    bestForward = predictedForward;
                    bestStrafe = predictedStrafe;
                }
            }
        }

        movementEvent.setForward(bestForward);
        movementEvent.setStrafe(bestStrafe);
    }

    private void beginReset() {
        currentTask = Task.RESET;
        idleTicks = 0;
    }

    private void finishReset() {
        currentTask = Task.IDLE;
        currentPriority = 0;
        FreeLookHandler.setActive(false);
    }

    private static boolean applyRotation(Rotates desired, float turnSpeed) {
        Rotates currentRotation = new Rotates(mc.player);
        float yawDelta = wrapDegrees(desired.getYaw() - currentRotation.getYaw());
        float pitchDelta = desired.getPitch() - currentRotation.getPitch();
        float totalDelta = Math.abs(yawDelta) + Math.abs(pitchDelta);

        float yawSpeed = totalDelta == 0 ? 0 : Math.abs(yawDelta / totalDelta) * turnSpeed;
        float pitchSpeed = totalDelta == 0 ? 0 : Math.abs(pitchDelta / totalDelta) * turnSpeed;

        mc.player.rotationYaw += getSensitivity(clamp(yawDelta, -yawSpeed, yawSpeed));
        mc.player.rotationPitch = clamp(
                mc.player.rotationPitch + getSensitivity(clamp(pitchDelta, -pitchSpeed, pitchSpeed)),
                -90,
                90);

        idleTicks = 0;
        return new Rotates(mc.player).getDelta(desired) < (currentTask == Task.RESET ? currentReturnSpeed : currentTurnSpeed);
    }

    public double direction(float rotationYaw, double moveForward, double moveStrafing) {
        float forward = moveForward < 0F ? -0.5F : moveForward > 0F ? 0.5F : 1F;
        rotationYaw += (90F * forward) * (moveStrafing > 0F ? -1 : moveStrafing < 0F ? 1 : 0);
        if (moveForward < 0F) {
            rotationYaw += 180F;
        }
        return Math.toRadians(rotationYaw);
    }

    public static void update(Rotates rotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
        if (currentPriority > priority) {
            return;
        }

        currentTurnSpeed = turnSpeed;
        currentReturnSpeed = returnSpeed;
        currentTimeout = timeout;
        currentPriority = priority;
        targetRotates = rotation;
        idleTicks = 0;
        currentTask = Task.AIM;

        if (FreeLookHandler.isActive()) {
            applyRotation(rotation, turnSpeed);
        } else {
            FreeLookHandler.setActive(true);
            applyRotation(rotation, turnSpeed);
        }
    }

    public static void updateRots(Rotates rotation, float turnSpeed, int timeout, int priority) {
        update(rotation, turnSpeed, turnSpeed, timeout, priority);
    }

    public static Vector2f applySensitivityPatch(Vector2f rotation, Vector2f previousRotates) {
        double sens = mc.gameSettings.mouseSensitivity;
        double gcd = Math.pow(sens * 0.6D + 0.2D, 3.0D) * 8.0D;

        double prevYaw = previousRotates.x;
        double prevPitch = previousRotates.y;

        double currentYaw = rotation.x;
        double currentPitch = rotation.y;

        double yaw = (Math.ceil(((currentYaw - prevYaw) / gcd) / 0.15F) * gcd) * 0.15F;
        double pitch = (Math.ceil(((currentPitch - prevPitch) / gcd) / 0.15F) * gcd) * 0.15F;

        return new Vector2f((float) (prevYaw + yaw), (float) (prevPitch + pitch));
    }

    private enum Task {
        AIM,
        RESET,
        IDLE
    }
}
