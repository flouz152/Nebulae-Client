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
import net.minecraft.block.BlockState;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
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
    private boolean pendingLoot;
    private boolean looting;
    private int lootClicks;
    private boolean autoLeaveTriggered;
    private long spawnCommandAt;
    private long lastRandomLocAt;
    private long lastZombieSeenAt;
    private BlockPos lootBlock;
    private int lootBaseline;

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
        pendingLoot = false;
        looting = false;
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
        lootBlock = null;
        lootBaseline = 0;
    }

    @Override
    protected void onDisable() {
        super.onDisable();
        cancelPathing();
        baritone = null;
        currentTarget = null;
        looting = false;
        pendingLoot = false;
        autoLeaveTriggered = false;
        wanderGoal = null;
        lootBlock = null;
        lootBaseline = 0;
    }

    @Override
    public void event(Event event) {
        if (!(event instanceof EventUpdate) || mc.player == null || mc.world == null) {
            return;
        }

        handleAutoLeave();
        handleRandomLocTimer();
        performLootClicks();

        if (autoLeaveTriggered) {
            cancelPathing();
            return;
        }

        if (!looting) {
            updateTarget();
        } else {
            cancelPathing();
        }

        if (looting) {
            cancelPathing();
            return;
        }

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
                    stopLooting();
                    pendingLoot = false;
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
            pendingLoot = true;
            currentTarget = null;
            lastGoal = null;
            wanderGoal = null;
            lootBlock = computeLootBlock();
        }

        AxisAlignedBB searchBox = mc.player.getBoundingBox().grow(48.0, 16.0, 48.0);
        List<ZombieEntity> zombies = mc.world.getEntitiesWithinAABB(ZombieEntity.class, searchBox,
                entity -> entity != null && entity.isAlive() && !entity.isInvisible());

        if (zombies.isEmpty()) {
            currentTarget = null;
            if (pendingLoot && !looting) {
                startLooting();
            }
            return;
        }

        pendingLoot = false;
        if (looting) {
            stopLooting();
        }

        zombies.sort(Comparator.comparingDouble(z -> z.getDistance(mc.player)));
        ZombieEntity nearest = zombies.get(0);
        if (currentTarget != nearest) {
            currentTarget = nearest;
            lastGoal = null;
            wanderGoal = null;
            wanderTimer.reset();
        }
        lastZombieSeenAt = System.currentTimeMillis();
    }

    private void engageTarget() {
        if (currentTarget == null || !currentTarget.isAlive()) {
            return;
        }

        lastZombieSeenAt = System.currentTimeMillis();
        double distance = mc.player.getDistance(currentTarget);
        if (distance > 6.0) {
            pathToTarget();
        } else {
            cancelPathing();
            maintainSpacing(distance);
            attackTarget();
        }
        if (distance > 2.1) {
            mc.player.setSprinting(true);
        }
    }

    private void pathToTarget() {
        if (baritone == null) {
            return;
        }
        if (!pathTimer.hasReached(250)) {
            return;
        }
        BlockPos pos = findApproachPosition(currentTarget);
        if (pos == null) {
            return;
        }
        boolean sameGoal = lastGoal != null && lastGoal.withinDistance(pos, 1.5);
        if (sameGoal && baritone.getCustomGoalProcess().isActive()) {
            return;
        }
        baritone.getCustomGoalProcess().setGoalAndPath(new GoalNear(pos, 2));
        lastGoal = pos;
        pathTimer.reset();
    }

    private BlockPos findApproachPosition(ZombieEntity target) {
        if (target == null || mc.world == null) {
            return mc.player.getPosition();
        }
        BlockPos base = target.getPosition();
        BlockPos best = null;
        double bestScore = Double.MAX_VALUE;
        for (int radius = 2; radius <= 5; radius++) {
            for (int i = 0; i < 12; i++) {
                double angle = (Math.PI * 2 / 12.0) * i;
                int x = base.getX() + MathHelper.floor(Math.cos(angle) * radius);
                int z = base.getZ() + MathHelper.floor(Math.sin(angle) * radius);
                int y = mc.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
                BlockPos candidate = new BlockPos(x, y, z);
                BlockPos min = candidate.add(-1, -1, -1);
                BlockPos max = candidate.add(1, 1, 1);
                if (!mc.world.isAreaLoaded(min, max)) {
                    continue;
                }
                if (!isOpenSurface(candidate)) {
                    continue;
                }
                double dist = candidate.distanceSq(mc.player.getPosX(), mc.player.getPosY(), mc.player.getPosZ(), true);
                if (dist < bestScore) {
                    bestScore = dist;
                    best = candidate;
                }
            }
            if (best != null) {
                return best;
            }
        }
        if (isOpenSurface(base)) {
            return base;
        }
        return mc.player.getPosition();
    }

    private boolean isOpenSurface(BlockPos pos) {
        if (mc.world == null) {
            return false;
        }
        if (!mc.world.isAirBlock(pos) || !mc.world.isAirBlock(pos.up())) {
            return false;
        }
        if (!mc.world.getFluidState(pos).isEmpty()) {
            return false;
        }
        BlockPos below = pos.down();
        BlockState belowState = mc.world.getBlockState(below);
        if (!belowState.getMaterial().isSolid()) {
            return false;
        }
        if (belowState.isIn(BlockTags.CLIMBABLE)) {
            return false;
        }
        if (!mc.world.canBlockSeeSky(pos)) {
            return false;
        }
        BlockState state = mc.world.getBlockState(pos);
        return !state.isIn(BlockTags.CLIMBABLE);
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

    private void maintainSpacing(double distance) {
        if (currentTarget == null) {
            return;
        }
        double desired = 2.0;
        Vector3d toTarget = currentTarget.getPositionVec().subtract(mc.player.getPositionVec());
        Vector3d planar = new Vector3d(toTarget.x, 0.0, toTarget.z);
        if (planar.lengthSquared() < 1.0E-4) {
            return;
        }
        Vector3d normalized = planar.normalize();
        Vector3d motion = mc.player.getMotion();
        if (distance > desired + 0.35) {
            double speed = MathHelper.clamp(distance - desired, 0.25, 0.85);
            mc.player.setMotion(normalized.x * speed, motion.y, normalized.z * speed);
        } else if (distance < desired - 0.3) {
            double speed = MathHelper.clamp(desired - distance, 0.25, 0.6);
            mc.player.setMotion(-normalized.x * speed, motion.y, -normalized.z * speed);
        }
    }

    private void performLootClicks() {
        if (!looting || mc.player == null || mc.world == null) {
            return;
        }
        if (getValuableCount() > lootBaseline) {
            stopLooting();
            pendingLoot = false;
            return;
        }
        if (lootBlock == null || !mc.world.isAreaLoaded(lootBlock, lootBlock)) {
            lootBlock = computeLootBlock();
            if (lootBlock == null) {
                stopLooting();
                pendingLoot = false;
                return;
            }
        }
        if (!lootTimer.hasReached(30)) {
            return;
        }
        BlockPos target = lootBlock;
        if (mc.world.isAirBlock(target)) {
            BlockPos below = target.down();
            if (mc.world.isAreaLoaded(below, below) && !mc.world.isAirBlock(below)) {
                target = below;
            }
        }
        Vector3d lookVec = new Vector3d(target.getX() + 0.5, target.getY() + 0.15, target.getZ() + 0.5);
        lookAt(lookVec);
        if (!mc.world.isAirBlock(target)) {
            mc.playerController.clickBlock(target, Direction.UP);
        }
        mc.player.swingArm(Hand.MAIN_HAND);
        lootClicks++;
        lootTimer.reset();
        if (getValuableCount() > lootBaseline || lootClicks > 80) {
            stopLooting();
            pendingLoot = false;
        }
    }

    private void startLooting() {
        if (mc.player == null || mc.world == null) {
            pendingLoot = false;
            return;
        }
        cancelPathing();
        looting = true;
        lootClicks = 0;
        lootBlock = computeLootBlock();
        lootTimer.reset();
        lootBaseline = getValuableCount();
    }

    private void stopLooting() {
        looting = false;
        lootClicks = 0;
        lootBlock = null;
        lootBaseline = 0;
    }

    private int getValuableCount() {
        if (mc.player == null) {
            return 0;
        }
        int total = 0;
        for (ItemStack stack : mc.player.inventory.mainInventory) {
            if (isValuable(stack)) {
                total += stack.getCount();
            }
        }
        for (ItemStack stack : mc.player.inventory.offHandInventory) {
            if (isValuable(stack)) {
                total += stack.getCount();
            }
        }
        return total;
    }

    private boolean isValuable(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        return stack.getItem() == Items.GOLD_INGOT
                || stack.getItem() == Items.GOLD_NUGGET
                || stack.getItem() == Items.IRON_INGOT
                || stack.getItem() == Items.EMERALD
                || stack.getItem() == Items.DIAMOND;
    }

    private BlockPos computeLootBlock() {
        if (mc.player == null || mc.world == null) {
            return null;
        }
        BlockPos front = mc.player.getPosition().offset(mc.player.getHorizontalFacing()).down();
        if (mc.world.isAreaLoaded(front, front)) {
            return front;
        }
        BlockPos fallback = mc.player.getPosition().down();
        if (mc.world.isAreaLoaded(fallback, fallback)) {
            return fallback;
        }
        return null;
    }

    private void sendRandomLoc() {
        sendCommand("/randomloc");
        lastRandomLocAt = System.currentTimeMillis();
        lastZombieSeenAt = lastRandomLocAt;
        cancelPathing();
        wanderGoal = null;
        stopLooting();
        pendingLoot = false;
        wanderTimer.setMs(900L);
        pathTimer.reset();
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
        if (baritone == null || autoLeaveTriggered || mc.player == null || looting) {
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
            BlockPos minCheck = candidate.add(-1, -1, -1);
            BlockPos maxCheck = candidate.add(1, 1, 1);
            if (!mc.world.isAreaLoaded(minCheck, maxCheck)) {
                continue;
            }
            if (!isOpenSurface(candidate)) {
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
