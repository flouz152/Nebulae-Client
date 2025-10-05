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
public class RotationComponent extends Component {
// leaked by itskekoff; discord.gg/sk3d qsAAERgw
    public static RotationComponent getInstance() {
        return Instance.getComponent(RotationComponent.class);
    }

    private RotationTask currentTask = RotationTask.IDLE;
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
        }
        if (event instanceof EventUpdate) {
            if (currentTask().equals(RotationTask.AIM) && idleTicks() > currentTimeout()) {
                currentTask(RotationTask.RESET);
            }

            if (currentTask().equals(RotationTask.RESET)) {
                resetRotation();
            }
            idleTicks++;
        }
    }

    private void resetRotation() {
        Rotation targetRotation = new Rotation(FreeLookHandler.getFreeYaw(), FreeLookHandler.getFreePitch());
        if (updateRotation(targetRotation, currentYawReturnSpeed(), currentPitchReturnSpeed())) {
            stopRotation();
        }
    }

    public static void update(Rotation target, float yawSpeed, float pitchSpeed, float yawReturnSpeed, float pitchReturnSpeed, int timeout, int priority, boolean clientRotation) {
        final RotationComponent instance = RotationComponent.getInstance();
        if (instance.currentPriority() > priority) {
            return;
        }

        if (instance.currentTask().equals(RotationTask.IDLE) && !clientRotation) {
            FreeLookHandler.setActive(true);
        }

        instance.currentYawSpeed(yawSpeed);
        instance.currentPitchSpeed(pitchSpeed);
        instance.currentYawReturnSpeed(yawReturnSpeed);
        instance.currentPitchReturnSpeed(pitchReturnSpeed);
        instance.currentTimeout(timeout);
        instance.currentPriority(priority);
        instance.currentTask(RotationTask.AIM);
        instance.targetRotation(target);

        instance.updateRotation(target, yawSpeed, pitchSpeed);
    }

    public static void update(Rotation targetRotation, float turnSpeed, float returnSpeed, int timeout, int priority) {
        update(targetRotation, turnSpeed, turnSpeed, returnSpeed, returnSpeed, timeout, priority, false);
    }

    private boolean updateRotation(Rotation targetRotation, float yawSpeed, float pitchSpeed) {
        if (mc.player == null) return false;

        Rotation currentRotation = new Rotation(mc.player);
        float yawDelta = MathHelper.wrapDegrees(targetRotation.getYaw() - currentRotation.getYaw());
        float pitchDelta = targetRotation.getPitch() - currentRotation.getPitch();

        float clampedYaw = Math.min(Math.abs(yawDelta), yawSpeed);
        float clampedPitch = Math.min(Math.abs(pitchDelta), pitchSpeed);

        mc.player.rotationYaw += SensUtil.getSens(MathHelper.clamp(yawDelta, -clampedYaw, clampedYaw));
        mc.player.rotationPitch = MathHelper.clamp(mc.player.rotationPitch + SensUtil.getSens(MathHelper.clamp(pitchDelta, -clampedPitch, clampedPitch)), -90F, 90F);

        idleTicks(0);
        return new Rotation(mc.player).getDelta(targetRotation) < 1F;
    }

    public void stopRotation() {
        currentTask(RotationTask.IDLE);
        currentPriority(0);
        if (!SmoothRotationComponent.getInstance().isRotating()) {
            FreeLookHandler.setActive(false);
        }
    }

    public boolean isRotating() {
        return !currentTask.equals(RotationTask.IDLE);
    }

    public enum RotationTask {
        AIM,
        RESET,
        IDLE
    }
}
