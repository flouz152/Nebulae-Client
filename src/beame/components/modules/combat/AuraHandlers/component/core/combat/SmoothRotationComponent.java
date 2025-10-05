package beame.components.modules.combat.AuraHandlers.component.core.combat;

import beame.components.modules.combat.AuraHandlers.component.Component;
import beame.components.modules.combat.AuraHandlers.component.Instance;
import beame.util.math.SensUtil;
import beame.util.other.MoveUtil;
import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventUpdate;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.util.math.MathHelper;

@Getter
@Setter
@Accessors(fluent = true)
public class SmoothRotationComponent extends Component {
// leaked by itskekoff; discord.gg/sk3d Iue7bR09
    public static SmoothRotationComponent getInstance() {
        return Instance.getComponent(SmoothRotationComponent.class);
    }

    private SmoothRotationComponent.RotationTask currentTask = SmoothRotationComponent.RotationTask.IDLE;
    private float currentYawSpeed;
    private float currentPitchSpeed;
    private float currentYawReturnSpeed;
    private float currentPitchReturnSpeed;
    private int currentPriority;
    private int currentTimeout;
    private int idleTicks;
    private Rotation targetRotation;

    @Override
    public void event(Event event) {
        if (event instanceof EventInput) {
            if (isRotating()) {
                MoveUtil.fixMovement((EventInput) event, MathHelper.wrapDegrees(mc.gameRenderer.getActiveRenderInfo().getYaw()));
            }

            if (event instanceof EventUpdate eventUpdate) {
                if (currentTask().equals(SmoothRotationComponent.RotationTask.AIM) && idleTicks() > currentTimeout()) {
                    currentTask(SmoothRotationComponent.RotationTask.RESET);
                }

                if (currentTask().equals(SmoothRotationComponent.RotationTask.RESET)) {
                    resetRotation();
                }
                idleTicks++;
            }
        }
    }

    private void resetRotation() {
        Rotation targetRotation = new Rotation(FreeLookHandler.getFreeYaw(), FreeLookHandler.getFreePitch());
        if (updateRotation(targetRotation, currentYawReturnSpeed(), currentPitchReturnSpeed())) {
            stopRotation();
        }
    }

    public static void update(Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout, int priority, boolean clientRotation) {
        final SmoothRotationComponent instance = SmoothRotationComponent.getInstance();
        if (instance.currentPriority() > priority) {
            return;
        }

        if (instance.currentTask().equals(SmoothRotationComponent.RotationTask.IDLE) && !clientRotation) {
            FreeLookHandler.setActive(true);
        }

        instance.currentYawSpeed(yawSpeed);
        instance.currentPitchSpeed(pitchSpeed);
        instance.currentYawReturnSpeed(yawReturnSpeed);
        instance.currentPitchReturnSpeed(pitchReturnSpeed);
        instance.currentTimeout(timeout);
        instance.currentPriority(priority);
        instance.currentTask(SmoothRotationComponent.RotationTask.AIM);
        instance.targetRotation(target);

        instance.updateRotation(target, yawSpeed, pitchSpeed);
    }

    public static void update(Rotation targetRotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
        update(targetRotation, turnSpeed, turnSpeed, returnSpeed, returnSpeed, timeout, priority, false);
    }

    private boolean updateRotation(Rotation targetRotation, float lazinessH, float lazinessV) {
        if (mc.player == null) return false;

        mc.player.rotationYaw = smoothRotation(mc.player.rotationYaw, targetRotation.getYaw(), lazinessH);
        mc.player.rotationPitch = MathHelper.clamp(smoothRotation(mc.player.rotationPitch, targetRotation.getPitch(), lazinessV), -90F, 90F);

        idleTicks(0);
        return new Rotation(mc.player).getDelta(targetRotation) < 1F;
    }

    public void stopRotation() {
        currentTask(SmoothRotationComponent.RotationTask.IDLE);
        currentPriority(0);
        if (!RotationComponent.getInstance().isRotating()) {
            FreeLookHandler.setActive(false);
        }
    }

    public boolean isRotating() {
        return !currentTask.equals(SmoothRotationComponent.RotationTask.IDLE);
    }

    private float smoothRotation(float currentAngle, double targetAngle, float smoothFactor) {
        float angleDifference = (float) MathHelper.wrapDegrees(targetAngle - currentAngle);
        float adjustmentSpeed = Math.abs(angleDifference / smoothFactor);
        float angleAdjustment = adjustmentSpeed * Math.signum(Math.signum(angleDifference));

        if (Math.abs(angleAdjustment) > Math.abs(angleDifference)) {
            angleAdjustment = angleDifference;
        }

        return currentAngle + SensUtil.getSens(angleAdjustment);
    }

    public enum RotationTask {
        AIM,
        RESET,
        IDLE
    }
}