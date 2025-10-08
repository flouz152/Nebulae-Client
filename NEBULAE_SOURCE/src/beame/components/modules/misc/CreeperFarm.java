package beame.components.modules.misc;

import beame.util.math.MovementUtil;
import beame.util.math.TimerUtil2;
import events.Event;
import events.impl.player.EventInput;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.RayTraceContext;



public class CreeperFarm extends Module {
// leaked by itskekoff; discord.gg/sk3d bJRcgudO
    private Entity target;
    private final TimerUtil2 timerUtil = new TimerUtil2();
    private final TimerUtil2 hubTimer = new TimerUtil2();
    private Vector2f rotateVector = new Vector2f(0, 0);
    private boolean needRotate = false;
    private boolean isRunningAway = false;
    private Vector3d runAwayPosition = null;


    private Vector3d lastPosition;
    private long lastMoveTime;
    private boolean isStuck = false;
    private boolean strafingRight = false;
    private boolean sentHubCommand = false;
    private boolean hasExploded = false;

    public CreeperFarm() {
        super("CreeperFarm", Category.Misc, true, "Бот для автоматического убийства криперов");

    }

    @Override
    public void event(Event event) {
        if (event instanceof EventInput) {
            if (target != null && mc.player != null) {
                MovementUtil.fixMovement((EventInput) event, rotateVector.x);
            }
        }

        if (event instanceof EventUpdate) {
            if (isRunningAway) {
                runFromCreeper();
            } else {
                updateTarget();
                if (target != null) {
                    processRotationLogic();
                    moveToTarget();
                } else {
                    timerUtil.setLastMS(0);
                    reset();
                }
            }

            if (target instanceof CreeperEntity && !isRunningAway && !hasExploded) {
                if (mc.player.getDistance(target) <= 3.0) {
                    attack((CreeperEntity) target);
                }
            }
        }

        if (event instanceof EventMotion) {
            if (target == null && !isRunningAway) return;
            setPlayerRotation((EventMotion) event);
        }
    }

    private void moveToTarget() {
        if (target == null || mc.player == null) return;

        double deltaX = target.getPosX() - mc.player.getPosX();
        double deltaZ = target.getPosZ() - mc.player.getPosZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        if (target instanceof ItemEntity) {
            if (distance <= 1.5) {
                moveTowards(distance);
            } else {
                moveTowards(distance);
            }
            return;
        }

        if (target instanceof CreeperEntity creeper) {
            if (creeper.getCreeperState() > 2) {
                isRunningAway = true;
                runAwayPosition = getRunAwayPosition(creeper);
                return;
            }

            if (distance <= 6.0) {
                attack(creeper);
            }
        }

        moveTowards(distance);
    }

    private void moveTowards(double distance) {
        if (distance > 1.5) {
            checkStuck();


            if (!mc.player.isSprinting()) {
                mc.player.setSprinting(true);
            }
            mc.gameSettings.keyBindForward.setPressed(true);


            mc.gameSettings.keyBindJump.setPressed(true);

            if (isStuck) {
                if (strafingRight) {
                    mc.gameSettings.keyBindRight.setPressed(true);
                    mc.gameSettings.keyBindLeft.setPressed(false);
                    rotateVector = new Vector2f(mc.player.rotationYaw - 45, rotateVector.y);
                } else {
                    mc.gameSettings.keyBindLeft.setPressed(true);
                    mc.gameSettings.keyBindRight.setPressed(false);
                    rotateVector = new Vector2f(mc.player.rotationYaw + 45, rotateVector.y);
                }
                needRotate = true;
            } else {
                mc.gameSettings.keyBindRight.setPressed(false);
                mc.gameSettings.keyBindLeft.setPressed(false);
            }
        } else {
            resetKeys();
        }
    }

    private void runFromCreeper() {
        if (runAwayPosition == null || mc.player == null) {
            isRunningAway = false;
            return;
        }

        double deltaX = runAwayPosition.x - mc.player.getPosX();
        double deltaZ = runAwayPosition.z - mc.player.getPosZ();
        double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

        float yaw = (float) Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90F;
        rotateVector = new Vector2f(smoothRotation(mc.player.rotationYaw, yaw, 150), mc.player.rotationPitch);
        needRotate = true;


        mc.player.setSprinting(true);
        mc.gameSettings.keyBindJump.setPressed(true);

        if (target instanceof CreeperEntity && mc.player.getDistance(target) <= 3.0) {
            attack((CreeperEntity) target);
        }

        if (distance < 6.0) {
            isRunningAway = false;
            runAwayPosition = null;
            reset();
            return;
        }

        checkStuck();

        if (isStuck) {
            if (strafingRight) {
                mc.gameSettings.keyBindRight.setPressed(true);
                mc.gameSettings.keyBindLeft.setPressed(false);
            } else {
                mc.gameSettings.keyBindLeft.setPressed(true);
                mc.gameSettings.keyBindRight.setPressed(false);
            }
        } else {
            mc.gameSettings.keyBindRight.setPressed(false);
            mc.gameSettings.keyBindLeft.setPressed(false);
        }

        mc.gameSettings.keyBindForward.setPressed(true);
    }


    private void checkStuck() {
        if (mc.player == null) return;

        Vector3d currentPos = mc.player.getPositionVec();
        long currentTime = System.currentTimeMillis();

        if (lastPosition != null) {
            double deltaX = Math.abs(currentPos.x - lastPosition.x);
            double deltaZ = Math.abs(currentPos.z - lastPosition.z);

            if (currentTime - lastMoveTime <= 10) {
                if (deltaX < 0.01 && deltaZ < 0.01) {
                    if (strafingRight) {
                        rotateVector = new Vector2f(mc.player.rotationYaw - 45, rotateVector.y);
                    } else {
                        rotateVector = new Vector2f(mc.player.rotationYaw + 45, rotateVector.y);
                    }
                    strafingRight = !strafingRight;
                }
            } else {
                lastMoveTime = currentTime;
                lastPosition = currentPos;
            }
        } else {
            lastPosition = currentPos;
            lastMoveTime = currentTime;
        }
    }


    private Vector3d getRunAwayPosition(CreeperEntity creeper) {
        double deltaX = mc.player.getPosX() - creeper.getPosX();
        double deltaZ = mc.player.getPosZ() - creeper.getPosZ();

        double length = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        if (length > 0) {
            deltaX /= length;
            deltaZ /= length;
        }

        double targetX = creeper.getPosX() + (deltaX * 15);
        double targetZ = creeper.getPosZ() + (deltaZ * 15);

        return new Vector3d(targetX, mc.player.getPosY(), targetZ);
    }

    private void updateTarget() {
        if (isRunningAway) return;

        Entity bestTarget = null;
        double bestDistance = Double.MAX_VALUE;

        for (Entity entity : mc.world.getAllEntities()) {
            if (!(entity instanceof CreeperEntity) && !(entity instanceof ItemEntity)) continue;
            if (entity instanceof ItemEntity && !((ItemEntity) entity).getItem().getItem().equals(Items.GUNPOWDER)) continue;

            double distance = mc.player.getDistance(entity);


            if (entity instanceof CreeperEntity creeper) {
                double heightDifference = creeper.getPosY() - mc.player.getPosY();
                if (heightDifference < -3 || heightDifference > 3) {
                    continue;
                }


                if (!isPathClear(creeper)) {
                    continue;
                }
            }

            if (distance < bestDistance) {
                bestDistance = distance;
                bestTarget = entity;
            }
        }

        target = bestTarget;
    }

    private boolean isPathClear(Entity target) {

        Vector3d playerPos = new Vector3d(mc.player.getPosX(), mc.player.getPosY() + mc.player.getEyeHeight(), mc.player.getPosZ());
        Vector3d targetPos = new Vector3d(target.getPosX(), target.getPosY() + target.getHeight() * 0.5, target.getPosZ());


        Vector3d direction = targetPos.subtract(playerPos);
        double distance = direction.length();
        direction = direction.normalize();


        RayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(
                playerPos,
                targetPos,
                RayTraceContext.BlockMode.COLLIDER,
                RayTraceContext.FluidMode.NONE,
                mc.player
        ));


        return result == null || result.getType() == RayTraceResult.Type.MISS;
    }

    private void processRotationLogic() {
        if (target == null || mc.player == null) return;

        Vector3d vec = target.getPositionVec()
                .add(0, target.getHeight() * 0.5, 0)
                .subtract(mc.player.getEyePosition(mc.getRenderPartialTicks()));

        double distance = vec.length();
        vec = vec.normalize();

        float targetYaw = (float) Math.toDegrees(Math.atan2(-vec.x, vec.z));
        float targetPitch = (float) MathHelper.clamp(-Math.toDegrees(Math.atan2(vec.y, Math.sqrt(vec.x * vec.x + vec.z * vec.z))), -90, 90);

        rotateVector = new Vector2f(
                smoothRotation(mc.player.rotationYaw, targetYaw, 150),
                smoothRotation(mc.player.rotationPitch, targetPitch, 150)
        );
        needRotate = true;

        if (target instanceof CreeperEntity && distance < 3.0) {
            attack((CreeperEntity) target);
        }
    }

    private void setPlayerRotation(EventMotion event) {
        if (!needRotate) return;

        event.setYaw(rotateVector.x);
        event.setPitch(rotateVector.y);

        mc.player.rotationYaw = rotateVector.x;
        mc.player.rotationPitch = rotateVector.y;
        mc.player.rotationYawHead = rotateVector.x;
        mc.player.renderYawOffset = rotateVector.x;
        mc.player.rotationPitchHead = rotateVector.y;
    }

    private float smoothRotation(float current, float target, float maxSpeed) {
        float speed = Math.min(maxSpeed, Math.abs(target - current) * 2);
        float delta = MathHelper.wrapDegrees(target - current);
        return MathHelper.wrapDegrees(current + MathHelper.clamp(delta, -speed, speed));
    }

    private void attack(CreeperEntity creeper) {
        if (creeper.getCreeperState() > 0) {
            isRunningAway = true;
            runAwayPosition = getRunAwayPosition(creeper);
            return;
        }

        if (timerUtil.hasTimeElapsed()) {
            mc.playerController.attackEntity(mc.player, creeper);
            mc.player.swingArm(Hand.MAIN_HAND);
            timerUtil.setLastMS(505);
        }
    }

    private void resetKeys() {
        mc.gameSettings.keyBindForward.setPressed(false);
        mc.gameSettings.keyBindRight.setPressed(false);
        mc.gameSettings.keyBindLeft.setPressed(false);
        mc.gameSettings.keyBindJump.setPressed(false);
        mc.player.setSprinting(false);
    }

    private void reset() {
        if (mc.player != null) {
            resetKeys();
        }
        needRotate = false;
        rotateVector = new Vector2f(mc.player != null ? mc.player.rotationYaw : 0, mc.player != null ? mc.player.rotationPitch : 0);
    }

    private boolean isChangingItem;
    private int originalSlot = -1;

    @Override
    public void onEnable() {
        super.onEnable();
        if (mc.player != null) {
            reset();
            target = null;
            isRunningAway = false;
            runAwayPosition = null;
            lastPosition = null;
            isStuck = false;
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
        reset();
        timerUtil.setLastMS(0);
        target = null;
        isRunningAway = false;
        runAwayPosition = null;
        lastPosition = null;
        isStuck = false;
    }
}