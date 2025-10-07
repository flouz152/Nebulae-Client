package beame.components.modules.combat;

import beame.components.baritone.api.BaritoneAPI;
import beame.components.baritone.api.IBaritone;
import beame.components.baritone.api.pathing.goals.GoalNear;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventUpdate;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.gen.Heightmap;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ZombieFarm extends Module {

    private final SliderSetting rtpInterval = new SliderSetting("RTP", 7f, 1f, 20f, 1f);
    private final BooleanSetting autoLeave = new BooleanSetting("AutoLeave", false);
    private final SliderSetting autoLeaveDistance = new SliderSetting("AutoLeave радиус", 25f, 1f, 100f, 1f)
            .setVisible(() -> autoLeave.get());

    private final TimerUtil pathTimer = new TimerUtil();
    private final TimerUtil commandTimer = new TimerUtil();
    private final TimerUtil lootTimer = new TimerUtil();
    private final TimerUtil wanderTimer = new TimerUtil();

    private ZombieEntity currentTarget;
    private BlockPos lastGoal;
    private BlockPos wanderGoal;
    private IBaritone baritone;
    private int killCounter;
    private boolean lootSequence;
    private int lootClicks;
    private boolean autoLeaveTriggered;
    private long spawnCommandAt;
    private long lastRandomLocAt;
    private long lastZombieSeenAt;

    public ZombieFarm() {
        super("ZombieFarm", Category.Combat, true, "Автофарм зомби с использованием Baritone");
        addSettings(rtpInterval, autoLeave, autoLeaveDistance);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        if (mc.player == null) {
            setState(false);
            return;
        }
        baritone = BaritoneAPI.getProvider().getPrimaryBaritone();
        killCounter = 0;
        lootSequence = false;
        lootClicks = 0;
        autoLeaveTriggered = false;
        lastGoal = null;
        wanderGoal = null;
        currentTarget = null;
        lastZombieSeenAt = System.currentTimeMillis();
        pathTimer.reset();
        lootTimer.reset();
        wanderTimer.reset();
        commandTimer.setMs(400L);
        sendRandomLoc();
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        cancelPathing();
        baritone = null;
        currentTarget = null;
        lootSequence = false;
        killCounter = 0;
        autoLeaveTriggered = false;
        wanderGoal = null;
    }

    @Override
    public void event(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return;
        }

        handleAutoLeave();
        handleRandomLocTimer();
        performLootClicks();

        if (autoLeaveTriggered || lootSequence) {
            cancelPathing();
            return;
        }

        updateTarget();
        if (currentTarget != null) {
            engageTarget();
        } else {
            handleWander();
        }
    }

    private void handleAutoLeave() {
        if (!autoLeave.get()) {
            autoLeaveTriggered = false;
            return;
        }
        if (!autoLeaveTriggered) {
            double radius = autoLeaveDistance.get();
            List<? extends PlayerEntity> players = mc.world.getPlayers();
            for (PlayerEntity player : players) {
                if (player == mc.player) {
                    continue;
                }
                if (player.isSpectator()) {
                    continue;
                }
                if (player.getDistance(mc.player) <= radius) {
                    sendCommand("/spawn", true);
                    autoLeaveTriggered = true;
                    spawnCommandAt = System.currentTimeMillis();
                    cancelPathing();
                    wanderGoal = null;
                    break;
                }
            }
        } else if (System.currentTimeMillis() - spawnCommandAt >= 10_000L) {
            sendRandomLoc();
            autoLeaveTriggered = false;
            wanderTimer.reset();
        }
    }

    private void handleRandomLocTimer() {
        long now = System.currentTimeMillis();
        float minutes = rtpInterval.get();
        long timeout = (long) (minutes * 60_000L);
        if (now - lastRandomLocAt >= timeout || now - lastZombieSeenAt >= timeout) {
            sendRandomLoc();
        }
    }

    private void updateTarget() {
        if (currentTarget != null && (!currentTarget.isAlive() || currentTarget.removed)) {
            killCounter++;
            currentTarget = null;
            lastGoal = null;
            lootSequence = killCounter >= 15;
            wanderGoal = null;
            if (lootSequence) {
                lootClicks = 0;
                lootTimer.reset();
                killCounter = 0;
            }
        }

        if (currentTarget != null) {
            lastZombieSeenAt = System.currentTimeMillis();
            return;
        }

        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(48.0, 16.0, 48.0);
        List<ZombieEntity> zombies = mc.world.getEntitiesWithinAABB(ZombieEntity.class, searchBox,
                entity -> entity != null && entity.isAlive() && !entity.isInvisible());

        if (zombies.isEmpty()) {
            return;
        }

        zombies.sort(Comparator.comparingDouble(z -> z.getDistance(mc.player)));
        currentTarget = zombies.get(0);
        lastZombieSeenAt = System.currentTimeMillis();
        lastGoal = null;
        wanderGoal = null;
        wanderTimer.reset();
    }

    private void engageTarget() {
        if (currentTarget == null || !currentTarget.isAlive()) {
            return;
        }

        lastZombieSeenAt = System.currentTimeMillis();
        double distance = mc.player.getDistance(currentTarget);
        if (distance > 4.0) {
            pathToTarget();
        } else {
            cancelPathing();
            attackTarget();
        }
        mc.player.setSprinting(true);
    }

    private void pathToTarget() {
        if (baritone == null) {
            return;
        }
        if (!pathTimer.hasReached(250)) {
            return;
        }
        BlockPos pos = currentTarget.getPosition();
        boolean sameGoal = lastGoal != null && lastGoal.withinDistance(pos, 1.5);
        if (sameGoal && baritone.getCustomGoalProcess().isActive()) {
            return;
        }
        baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(pos, 2));
        lastGoal = pos;
        pathTimer.reset();
    }

    private void attackTarget() {
        if (currentTarget == null) {
            return;
        }
        lookAt(currentTarget.getPositionVec().add(0.0, currentTarget.getEyeHeight() * 0.85, 0.0));
        if (mc.player.getCooledAttackStrength(0.0f) >= 0.9f) {
            mc.playerController.attackEntity(mc.player, currentTarget);
            mc.player.swingArm(Hand.MAIN_HAND);
        }
    }

    private void performLootClicks() {
        if (!lootSequence) {
            return;
        }
        if (!lootTimer.hasReached(60)) {
            return;
        }
        BlockPos floor = new BlockPos(mc.player.getPosX(), mc.player.getPosY() - 1, mc.player.getPosZ());
        if (!mc.world.isAirBlock(floor)) {
            mc.playerController.clickBlock(floor, Direction.UP);
        }
        mc.player.swingArm(Hand.MAIN_HAND);
        lootClicks++;
        lootTimer.reset();
        if (lootClicks >= 10) {
            lootSequence = false;
            lootClicks = 0;
        }
    }

    private void sendRandomLoc() {
        sendCommand("/randomloc");
        lastRandomLocAt = System.currentTimeMillis();
        lastZombieSeenAt = lastRandomLocAt;
        cancelPathing();
        wanderGoal = null;
        wanderTimer.reset();
    }

    private void sendCommand(String command) {
        sendCommand(command, false);
    }

    private void sendCommand(String command, boolean force) {
        if (mc.player == null) {
            return;
        }
        if (!force && !commandTimer.hasReached(300)) {
            return;
        }
        mc.player.sendChatMessage(command);
        commandTimer.reset();
    }

    private void cancelPathing() {
        if (baritone != null && baritone.getPathingBehavior().isPathing()) {
            baritone.getPathingBehavior().forceCancel();
        }
        lastGoal = null;
        wanderGoal = null;
    }

    private void lookAt(Vector3d vec) {
        Vector3d eye = mc.player.getEyePosition(1.0F);
        double diffX = vec.x - eye.x;
        double diffY = vec.y - eye.y;
        double diffZ = vec.z - eye.z;
        double distHorizontal = MathHelper.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) (MathHelper.atan2(diffZ, diffX) * (180.0F / Math.PI)) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(diffY, distHorizontal) * (180.0F / Math.PI)));
        mc.player.rotationYaw = yaw;
        mc.player.rotationYawHead = yaw;
        mc.player.renderYawOffset = yaw;
        mc.player.rotationPitch = pitch;
    }

    private void handleWander() {
        if (baritone == null || autoLeaveTriggered || mc.player == null) {
            return;
        }
        if (currentTarget != null) {
            return;
        }
        boolean isPathing = baritone.getPathingBehavior().isPathing();
        if (isPathing && wanderGoal != null) {
            double distSq = mc.player.getDistanceSq(wanderGoal.getX() + 0.5, wanderGoal.getY(), wanderGoal.getZ() + 0.5);
            if (distSq > 9.0) {
                return;
            }
        }
        if (!isPathing && !wanderTimer.hasReached(700)) {
            return;
        }
        if (isPathing && !wanderTimer.hasReached(1200)) {
            return;
        }
        startWander();
    }

    private void startWander() {
        if (baritone == null || mc.player == null || mc.world == null) {
            return;
        }
        BlockPos origin = mc.player.getPosition();
        for (int attempt = 0; attempt < 6; attempt++) {
            int radius = ThreadLocalRandom.current().nextInt(16, 41);
            double angle = ThreadLocalRandom.current().nextDouble(Math.PI * 2);
            int x = origin.getX() + MathHelper.floor(Math.cos(angle) * radius);
            int z = origin.getZ() + MathHelper.floor(Math.sin(angle) * radius);
            int y = mc.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos candidate = new BlockPos(x, y, z);
            BlockPos below = candidate.down();
            BlockPos minCheck = candidate.add(-1, -1, -1);
            BlockPos maxCheck = candidate.add(1, 1, 1);
            if (!mc.world.isAreaLoaded(minCheck, maxCheck)) {
                continue;
            }
            if (!mc.world.getBlockState(below).getMaterial().isSolid()) {
                continue;
            }
            if (!mc.world.getFluidState(candidate).isEmpty()) {
                continue;
            }
            wanderGoal = candidate;
            baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(candidate, 2));
            lastGoal = candidate;
            pathTimer.reset();
            wanderTimer.reset();
            return;
        }
    }
}
