package net.minecraft.entity.projectile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.TridentItem;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public final class ProjectileHelper
{
// leaked by itskekoff; discord.gg/sk3d hRQyigmZ
    private static boolean aimAtPlayer = false;

    public static void setAimAtPlayer(boolean enabled) {
        aimAtPlayer = enabled;
    }
    

    public static boolean shouldPreventRelease(PlayerEntity player, ItemStack itemStack) {
        if (itemStack.isEmpty()) return false;
        Item item = itemStack.getItem();
        if (player.getCooldownTracker().hasCooldown(item)) {
            if (item instanceof TridentItem) {
                if (player.isHandActive() && 
                    player.getActiveItemStack().getItem() instanceof TridentItem) {
                    return true;
                }
            }
        }
        
        return false;
    }
    

    public static boolean isAimingAtPlayer() {
        return aimAtPlayer;
    }

    public static boolean aimAtNearestPlayer(LivingEntity shooter) {
        if (!aimAtPlayer || !(shooter instanceof PlayerEntity)) {
            return false;
        }
        
        PlayerEntity player = (PlayerEntity) shooter;
        PlayerEntity target = findNearestTarget(player);
        
        if (target != null) {
            float[] angles = calculateTargetAngles(player, target);
            player.rotationYaw = angles[0];
            player.rotationPitch = angles[1];
            
            return true;
        }
        
        return false;
    }

    public static double getBodyDistanceBetween(Entity entity1, Entity entity2) {
        Vector3d pos1 = entity1.getPositionVec().add(0, entity1.getEyeHeight(), 0);

        double targetHeight = entity2 instanceof PlayerEntity ? 0.4 : 0.5;
        Vector3d pos2 = entity2.getPositionVec().add(0, entity2.getHeight() * targetHeight, 0);

        return Math.sqrt(
            Math.pow(pos1.x - pos2.x, 2) +
            Math.pow(pos1.y - pos2.y, 2) +
            Math.pow(pos1.z - pos2.z, 2)
        );
    }
    public static double getHorizontalDistanceBetween(Entity entity1, Entity entity2) {
        Vector3d pos1 = entity1.getPositionVec();
        Vector3d pos2 = entity2.getPositionVec();
        
        return Math.sqrt(
            Math.pow(pos1.x - pos2.x, 2) +
            Math.pow(pos1.z - pos2.z, 2)
        );
    }

    private static PlayerEntity findNearestTarget(PlayerEntity player) {
        World world = player.world;
        double maxDistance = 30.0;

        List<PlayerEntity> players = world.getPlayers()
                .stream()
                .filter(target -> target != player && !target.isSpectator() && target.isAlive())
                .filter(target -> {

                    double bodyDistance = getBodyDistanceBetween(player, target);
                    double horizontalDistance = getHorizontalDistanceBetween(player, target);

                    if (horizontalDistance < 10.0) {

                        return bodyDistance <= maxDistance;
                    } else {

                        return horizontalDistance <= maxDistance && bodyDistance <= maxDistance + 5.0;
                    }
                })
                .collect(Collectors.toList());
        

        return players.stream()
                .min(Comparator
                    .comparingDouble((PlayerEntity target) -> getHorizontalDistanceBetween(player, target) * 1.2)
                    .thenComparingDouble(target -> getBodyDistanceBetween(player, target)))
                .orElse(null);
    }
    

    private static float[] calculateTargetAngles(Entity shooter, Entity target) {

        Vector3d shooterPos = shooter.getPositionVec().add(0, shooter.getEyeHeight(), 0);

        double horizontalDistanceRaw = getHorizontalDistanceBetween(shooter, target);

        double maxEffectiveDistance = 25.0;
        boolean isVeryDistant = horizontalDistanceRaw > maxEffectiveDistance;

        double targetHeightFactor;
        
        if (isVeryDistant) {
            targetHeightFactor = 0.3;
        } else if (target instanceof PlayerEntity) {
            targetHeightFactor = 0.4;
        } else {
            targetHeightFactor = 0.5;
        }

        Vector3d targetPos = target.getPositionVec().add(0, target.getHeight() * targetHeightFactor, 0);

        double verticalDifference = (targetPos.y - shooterPos.y);

        if (isVeryDistant && Math.abs(verticalDifference) > 8.0) {
            double sign = Math.signum(verticalDifference);
            verticalDifference = sign * Math.min(Math.abs(verticalDifference), 8.0);
            targetPos = new Vector3d(targetPos.x, shooterPos.y + verticalDifference, targetPos.z);
        }

        if (horizontalDistanceRaw > 5.0) {
            double baseLoweringFactor = Math.min((horizontalDistanceRaw - 5.0) / 15.0, 1.0);
            double baseHeightAdjustment = target.getHeight() * 0.25 * baseLoweringFactor;
            
            if (horizontalDistanceRaw > 20.0) {
                double farDistanceFactor = Math.min((horizontalDistanceRaw - 20.0) / 10.0, 1.0);
                baseHeightAdjustment += target.getHeight() * 0.25 * farDistanceFactor;
            }
            
            targetPos = targetPos.subtract(0, baseHeightAdjustment, 0);
            
            if (Math.abs(verticalDifference) > 1.0) {
                if (verticalDifference > 0) {
                    double heightDistanceFactor = Math.min(horizontalDistanceRaw / 20.0, 1.0);
                    double heightAdjustment = Math.min(
                        verticalDifference * 0.4 * heightDistanceFactor, 
                        target.getHeight() * 0.3
                    );
                    targetPos = targetPos.subtract(0, heightAdjustment, 0);
                } else {
                    if (horizontalDistanceRaw > 15.0) {
                        double heightUpAdjustment = Math.min(Math.abs(verticalDifference) * 0.1, 0.7) * 
                                                   Math.min((horizontalDistanceRaw - 15.0) / 10.0, 1.0);
                        targetPos = targetPos.add(0, heightUpAdjustment, 0);
                    }
                }
            }
        }

        if (isVeryDistant) {
            double deltaX = targetPos.x - shooterPos.x;
            double deltaY = targetPos.y - shooterPos.y;
            double deltaZ = targetPos.z - shooterPos.z;
            
            double yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0;
            
            double pitch;
            double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
            
            if (deltaY > 0) {
                double maxUpwardAngle = 20.0 * (maxEffectiveDistance / horizontalDistanceRaw);
                pitch = -Math.min(maxUpwardAngle, Math.toDegrees(Math.atan2(deltaY, horizontalDistance)));
            } else {
                double angleBase = Math.toDegrees(Math.atan2(-deltaY, horizontalDistance));
                pitch = -Math.min(5.0, angleBase);
            }
            
            pitch = MathHelper.clamp(pitch, -90.0, 90.0);
            
            return new float[] { (float) yaw, (float) pitch };
        }

        double deltaX = targetPos.x - shooterPos.x;
        double deltaY = targetPos.y - shooterPos.y;
        double deltaZ = targetPos.z - shooterPos.z;
        
        double yaw = Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0;
        
        double horizontalDistance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
        
        double gravity = 0.05;
        double distance = horizontalDistance;
        double initialVelocity = 1.6;
        
        double pitch;
        try {
            double discriminant = Math.pow(initialVelocity, 4) - gravity * (gravity * distance * distance + 2 * deltaY * initialVelocity * initialVelocity);
            
            if (discriminant < 0) {
                if (deltaY > 0) {
                    double maxAngle = Math.min(45.0, 15.0 + Math.toDegrees(Math.atan2(deltaY, horizontalDistance)));
                    if (horizontalDistance > 15.0) {
                        maxAngle = maxAngle * 15.0 / horizontalDistance;
                    }
                    pitch = -maxAngle;
                } else {
                    double angleBase = Math.toDegrees(Math.atan2(-deltaY, horizontalDistance));
                    if (horizontalDistance > 15.0) {
                        angleBase = angleBase * 15.0 / horizontalDistance;
                    }
                    pitch = -Math.min(60.0, angleBase);
                }
            } else {
                pitch = -Math.toDegrees(Math.atan((initialVelocity * initialVelocity - 
                        Math.sqrt(discriminant)) / 
                        (gravity * distance)));
                
                if (deltaY > 0) {
                    pitch = Math.max(pitch, -45.0);
                    
                    if (horizontalDistance > 15.0) {
                        double maxUpAngle = 45.0 * 15.0 / horizontalDistance;
                        pitch = Math.max(pitch, -maxUpAngle);
                    }
                }
            }
        } catch (Exception e) {
            pitch = -Math.toDegrees(Math.atan2(deltaY, horizontalDistance));
            
            if (deltaY > 0 && pitch < -45) {
                pitch = -45.0;
            }
        }
        
        pitch = MathHelper.clamp(pitch, -90.0, 90.0);
        
        return new float[] { (float) yaw, (float) pitch };
    }

    public static RayTraceResult func_234618_a_(Entity p_234618_0_, Predicate<Entity> p_234618_1_)
    {

        if (aimAtPlayer && p_234618_0_ instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) p_234618_0_;
            ItemStack mainHand = player.getHeldItemMainhand();
            ItemStack offHand = player.getHeldItemOffhand();

            boolean canUseMainHand = mainHand.getItem() instanceof TridentItem && 
                                   !player.getCooldownTracker().hasCooldown(mainHand.getItem());
            boolean canUseOffHand = offHand.getItem() instanceof TridentItem && 
                                  !player.getCooldownTracker().hasCooldown(offHand.getItem());
            
            if (canUseMainHand || canUseOffHand) {
                aimAtNearestPlayer(player);
            }
        }
        
        Vector3d vector3d = p_234618_0_.getMotion();
        World world = p_234618_0_.world;
        Vector3d vector3d1 = p_234618_0_.getPositionVec();
        Vector3d vector3d2 = vector3d1.add(vector3d);
        RayTraceResult raytraceresult = world.rayTraceBlocks(new RayTraceContext(vector3d1, vector3d2, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, p_234618_0_));

        if (raytraceresult.getType() != RayTraceResult.Type.MISS)
        {
            vector3d2 = raytraceresult.getHitVec();
        }

        RayTraceResult raytraceresult1 = rayTraceEntities(world, p_234618_0_, vector3d1, vector3d2, p_234618_0_.getBoundingBox().expand(p_234618_0_.getMotion()).grow(1.0D), p_234618_1_);

        if (raytraceresult1 != null)
        {
            raytraceresult = raytraceresult1;
        }

        return raytraceresult;
    }

    @Nullable

    /**
     * Gets the EntityRayTraceResult representing the entity hit
     */
    public static EntityRayTraceResult rayTraceEntities(Entity shooter, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter, double distance)
    {
        if (aimAtPlayer && shooter instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) shooter;
            if (player.getHeldItemMainhand().getItem() instanceof TridentItem || 
                player.getHeldItemOffhand().getItem() instanceof TridentItem) {
                aimAtNearestPlayer(player);
            }
        }
        
        World world = shooter.world;
        double d0 = distance;
        Entity entity = null;
        Vector3d vector3d = null;

        for (Entity entity1 : world.getEntitiesInAABBexcluding(shooter, boundingBox, filter))
        {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)entity1.getCollisionBorderSize());
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);

            if (axisalignedbb.contains(startVec))
            {
                if (d0 >= 0.0D)
                {
                    entity = entity1;
                    vector3d = optional.orElse(startVec);
                    d0 = 0.0D;
                }
            }
            else if (optional.isPresent())
            {
                Vector3d vector3d1 = optional.get();
                double d1 = startVec.squareDistanceTo(vector3d1);

                if (d1 < d0 || d0 == 0.0D)
                {
                    if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity())
                    {
                        if (d0 == 0.0D)
                        {
                            entity = entity1;
                            vector3d = vector3d1;
                        }
                    }
                    else
                    {
                        entity = entity1;
                        vector3d = vector3d1;
                        d0 = d1;
                    }
                }
            }
        }

        return entity == null ? null : new EntityRayTraceResult(entity, vector3d);
    }

    @Nullable

    /**
     * Gets the EntityRayTraceResult representing the entity hit
     */
    public static EntityRayTraceResult rayTraceEntities(World worldIn, Entity projectile, Vector3d startVec, Vector3d endVec, AxisAlignedBB boundingBox, Predicate<Entity> filter)
    {
        double d0 = Double.MAX_VALUE;
        Entity entity = null;

        for (Entity entity1 : worldIn.getEntitiesInAABBexcluding(projectile, boundingBox, filter))
        {
            AxisAlignedBB axisalignedbb = entity1.getBoundingBox().grow((double)0.3F);
            Optional<Vector3d> optional = axisalignedbb.rayTrace(startVec, endVec);

            if (optional.isPresent())
            {
                double d1 = startVec.squareDistanceTo(optional.get());

                if (d1 < d0)
                {
                    entity = entity1;
                    d0 = d1;
                }
            }
        }

        return entity == null ? null : new EntityRayTraceResult(entity);
    }

    public static final void rotateTowardsMovement(Entity projectile, float rotationSpeed)
    {
        Vector3d vector3d = projectile.getMotion();

        if (vector3d.lengthSquared() != 0.0D)
        {
            float f = MathHelper.sqrt(Entity.horizontalMag(vector3d));
            projectile.rotationYaw = (float)(MathHelper.atan2(vector3d.z, vector3d.x) * (double)(180F / (float)Math.PI)) + 90.0F;

            for (projectile.rotationPitch = (float)(MathHelper.atan2((double)f, vector3d.y) * (double)(180F / (float)Math.PI)) - 90.0F; projectile.rotationPitch - projectile.prevRotationPitch < -180.0F; projectile.prevRotationPitch -= 360.0F)
            {
            }

            while (projectile.rotationPitch - projectile.prevRotationPitch >= 180.0F)
            {
                projectile.prevRotationPitch += 360.0F;
            }

            while (projectile.rotationYaw - projectile.prevRotationYaw < -180.0F)
            {
                projectile.prevRotationYaw -= 360.0F;
            }

            while (projectile.rotationYaw - projectile.prevRotationYaw >= 180.0F)
            {
                projectile.prevRotationYaw += 360.0F;
            }

            projectile.rotationPitch = MathHelper.lerp(rotationSpeed, projectile.prevRotationPitch, projectile.rotationPitch);
            projectile.rotationYaw = MathHelper.lerp(rotationSpeed, projectile.prevRotationYaw, projectile.rotationYaw);
        }
    }

    public static Hand getHandWith(LivingEntity living, Item itemIn)
    {
        return living.getHeldItemMainhand().getItem() == itemIn ? Hand.MAIN_HAND : Hand.OFF_HAND;
    }

    public static AbstractArrowEntity fireArrow(LivingEntity shooter, ItemStack arrowStack, float distanceFactor)
    {
        ArrowItem arrowitem = (ArrowItem)(arrowStack.getItem() instanceof ArrowItem ? arrowStack.getItem() : Items.ARROW);
        AbstractArrowEntity abstractarrowentity = arrowitem.createArrow(shooter.world, arrowStack, shooter);
        abstractarrowentity.setEnchantmentEffectsFromEntity(shooter, distanceFactor);

        if (arrowStack.getItem() == Items.TIPPED_ARROW && abstractarrowentity instanceof ArrowEntity)
        {
            ((ArrowEntity)abstractarrowentity).setPotionEffect(arrowStack);
        }

        return abstractarrowentity;
    }
}
