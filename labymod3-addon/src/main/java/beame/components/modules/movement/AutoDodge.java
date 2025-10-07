package beame.components.modules.movement;

import beame.Essence;
import beame.components.command.AbstractCommand;
import beame.feature.notify.NotificationManager;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import events.Event;
import events.impl.player.EventMotion;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.List;

public class AutoDodge extends Module {
// leaked by itskekoff; discord.gg/sk3d AwrMX5YK
    private final EnumSetting dodgeType = new EnumSetting("Доджить",
            new BooleanSetting("Зелья", false),
            new BooleanSetting("Трезубец", true)
    );

    private final SliderSetting dodgeDistance = new SliderSetting("Дистанция уклонения", 5.0f, 3.0f, 50.0f, 0.5f).setVisible(() -> dodgeType.get("Трезубец").get());;
    private final SliderSetting dodgeSpeed = new SliderSetting("Скорость уклонения", 1.0f, 0.5f, 5.0f, 0.1f).setVisible(() -> dodgeType.get("Трезубец").get());;

    private boolean isDodging = false;
    private boolean isLookingAtThrower = false;
    private PotionEntity dangerousPotion = null;
    private Boolean lastDodgeRight = null;
    private TridentEntity lastDangerousTrident = null;
    private float dodgeYaw = 0;
    private float dodgePitch = 0;
    private Entity potionThrower = null;
    private final InventoryUtility.Hand handUtil = new InventoryUtility.Hand();
    private boolean plastThrow;
    private long delay;
    private final TimerUtil timer = new TimerUtil();
    private final TimerUtil debugTimer = new TimerUtil();
    private Vector3d lastDodgeDirection = null;
    private boolean isDodgingSideways = false;

    private double initialRotatedX;
    private double initialRotatedZ;

    private SliderSetting safeDistance = new SliderSetting("Безопасная дистанция", 6.0f, 3.0f, 10.0f, 0.5f).setVisible(() -> dodgeType.get("Трезубец").get());

    public AutoDodge() {
        super("AutoDodge", Category.Movement, true, "Уклоняется от зелий и трезубцев");
        addSettings(dodgeType, dodgeDistance, dodgeSpeed, safeDistance);
    }

    @Override
    public void event(Event event) {
        if (mc.player == null || mc.world == null) return;

        if (debugTimer.hasReached(5000)) {
            String currentMode = dodgeType.get("Зелья").get() ? "Зелья" :
                    dodgeType.get("Трезубец").get() ? "Трезубец" : "Не выбран";
            debugTimer.reset();
        }

        if (event instanceof EventUpdate) {
            onUpdate();
        } else if (event instanceof EventMotion) {
            onMotion((EventMotion) event);
        }
    }

    private void onUpdate() {
        if (isDodging) {
            resetKeys();
        }

        if (dodgeType.get("Зелья").get()) {
            handlePotionDodge();
        }
        if (dodgeType.get("Трезубец").get()) {
            handleTridentDodge();
        }
    }

    private void handlePotionDodge() {
        dangerousPotion = findDangerousPotion();

        if (dangerousPotion != null) {
            if (!isDodging) {
            plastThrow = true;
            potionThrower = findNearestPlayer();

            if (potionThrower != null && !isLookingAtThrower) {
                Vector3d throwerPos = potionThrower.getPositionVec().add(0, potionThrower.getEyeHeight(), 0);
                Vector3d playerPos = mc.player.getPositionVec().add(0, mc.player.getEyeHeight(), 0);
                Vector3d direction = throwerPos.subtract(playerPos).normalize();

                dodgeYaw = (float) Math.toDegrees(Math.atan2(-direction.x, direction.z));
                dodgePitch = (float) Math.toDegrees(Math.atan2(-direction.y,
                        Math.sqrt(direction.x * direction.x + direction.z * direction.z)));

                isLookingAtThrower = true;
                    if (timer.hasReached(2000)) {
                      //  AbstractCommand.addMessage("§dAutoDodge §7| §fОбнаружено опасное зелье!");
                        timer.reset();
                    }
                return;
            }

            if (isLookingAtThrower) {
                isDodging = true;
                isLookingAtThrower = false;
            }
            }

            if (plastThrow) {
                throwKelp();
            }
        } else {
            isDodging = false;
            isLookingAtThrower = false;
            potionThrower = null;
        }
    }

    private void handleTridentDodge() {
        if (!dodgeType.get("Трезубец").get()) {
            return;
        }

        TridentEntity dangerousTrident = findDangerousTrident();

        if (dangerousTrident != null) {
            Vector3d tridentMotion = dangerousTrident.getMotion();
            double tridentSpeed = Math.sqrt(tridentMotion.x * tridentMotion.x + tridentMotion.z * tridentMotion.z);

            if (tridentSpeed > 1.5) {
                Vector3d playerPos = mc.player.getPositionVec();
                Vector3d landingPos = predictTridentLanding(dangerousTrident);

                if (landingPos != null) {
                    Vector3d tridentDirection = new Vector3d(tridentMotion.x, 0, tridentMotion.z).normalize();
                    Vector3d toImpact = landingPos.subtract(playerPos);
                    double dx = toImpact.x;
                    double dz = toImpact.z;

                    float playerYaw = (float) Math.toRadians(mc.player.rotationYaw);

                    double rotatedX = dx * Math.cos(playerYaw) + dz * Math.sin(playerYaw);
                    double rotatedZ = -dx * Math.sin(playerYaw) + dz * Math.cos(playerYaw);

                    double distanceToImpact = Math.sqrt(dx * dx + dz * dz);

                    if (distanceToImpact < dodgeDistance.get() || isDodging) {
                        if (lastDangerousTrident == null || dangerousTrident != lastDangerousTrident || lastDodgeRight == null) {


                            Vector3d perpRight = new Vector3d(-tridentDirection.z, 0, tridentDirection.x);
                            Vector3d perpLeft = new Vector3d(tridentDirection.z, 0, -tridentDirection.x);


                            Vector3d dodgeRight = playerPos.add(perpRight.scale(3));
                            Vector3d dodgeLeft = playerPos.add(perpLeft.scale(3));


                            double distanceRight = Math.abs(perpLeft.dotProduct(dodgeRight.subtract(landingPos)));
                            double distanceLeft = Math.abs(perpRight.dotProduct(dodgeLeft.subtract(landingPos)));


                            lastDodgeRight = distanceRight > distanceLeft;


                            lastDangerousTrident = dangerousTrident;


                            Vector3d dodgeDir = lastDodgeRight ? perpRight : perpLeft;


                            double dotProduct = tridentDirection.dotProduct(toImpact.normalize());
                            if (Math.abs(dotProduct) > 0.9) {
                                dodgeDir = dodgeDir.add(new Vector3d(-tridentDirection.x, 0, -tridentDirection.z));
                            }

                            dodgeDir = dodgeDir.normalize();
                            initialRotatedX = dodgeDir.x * Math.cos(playerYaw) + dodgeDir.z * Math.sin(playerYaw);
                            initialRotatedZ = -dodgeDir.x * Math.sin(playerYaw) + dodgeDir.z * Math.cos(playerYaw);
                        }

                        if (!isDodging) {
                            resetKeys();
                        }
                        isDodging = true;

                        double dodgeX = initialRotatedX;
                        double dodgeZ = initialRotatedZ;
                        if (distanceToImpact < safeDistance.get()) {
                            if (Math.abs(dodgeX) > 0.2) {
                                if (dodgeX < 0) {
                                    mc.gameSettings.keyBindLeft.setPressed(true);
                                } else {
                                    mc.gameSettings.keyBindRight.setPressed(true);
                                }
                            }

                            if (Math.abs(dodgeZ) > 0.2) {
                                if (dodgeZ < 0) {
                                    mc.gameSettings.keyBindBack.setPressed(true);
                                } else {
                                    mc.gameSettings.keyBindForward.setPressed(true);
                                }
                            }

                            if (timer.hasReached(0)) {
                                StringBuilder movement = new StringBuilder();
                                if (Math.abs(dodgeX) > 0.2) {
                                    movement.append(dodgeX < 0 ? "влево" : "вправо");
                                }
                                if (Math.abs(dodgeZ) > 0.2) {
                                    if (movement.length() > 0) movement.append(" + ");
                                    movement.append(dodgeZ < 0 ? "назад" : "вперед");
                                }


                                timer.reset();
                            }
                        } else {
                            resetKeys();
                            isDodging = false;
                            lastDodgeRight = null;
                            lastDangerousTrident = null;
                        }
                    } else {
                        if (isDodging) {
                            resetKeys();
                        }
                        isDodging = false;
                        lastDodgeRight = null;
                        lastDangerousTrident = null;
                    }
                }
            } else {
                if (isDodging) {
                    resetKeys();
                }
                isDodging = false;
                lastDodgeRight = null;
                lastDangerousTrident = null;
            }
        } else {
            if (isDodging) {
                resetKeys();
            }
            isDodging = false;
            lastDodgeRight = null;
            lastDangerousTrident = null;
        }
    }

    private Vector3d predictIntersection(Vector3d pos1, Vector3d vel1, Vector3d pos2, Vector3d vel2) {


        double a = vel1.x - vel2.x;
        double b = vel1.z - vel2.z;
        double c = pos2.x - pos1.x;
        double d = pos2.z - pos1.z;


        double t;
        if (Math.abs(a) > Math.abs(b)) {
            t = c / a;
        } else {
            t = d / b;
        }

        if (t < 0 || t > 20) {
            return null;
        }

        return pos1.add(vel1.scale(t));
    }

    private void throwKelp() {
            int hbSlot = getItem(Items.DRIED_KELP, true);
            int invSlot = getItem(Items.DRIED_KELP, false);

            if (invSlot == -1 && hbSlot == -1) {
                plastThrow = false;
            AbstractCommand.addMessage("§cПласт не найден!");
                Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
                return;
            }

            if (!mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP)) {
            int oldSlot = mc.player.inventory.currentItem;

                int slot = findAndTrowItem(hbSlot, invSlot);
                if (slot > 8) {
                    mc.playerController.pickItem(slot);
                }
            if (InventoryUtility.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != oldSlot) {
                mc.player.inventory.currentItem = oldSlot;
            }
            AbstractCommand.addMessage("§aПласт использован!");
        } else {
            AbstractCommand.addMessage("§eПласт в КД!");
            }
            plastThrow = false;
    }

    private void onMotion(EventMotion event) {
        if ((isDodging || isLookingAtThrower) && dangerousPotion != null) {
            event.setYaw(dodgeYaw);
            event.setPitch(dodgePitch);
        }
    }

    private void resetKeys() {
        if (mc.gameSettings != null) {
            mc.gameSettings.keyBindForward.setPressed(false);
            mc.gameSettings.keyBindBack.setPressed(false);
            mc.gameSettings.keyBindLeft.setPressed(false);
            mc.gameSettings.keyBindRight.setPressed(false);
            mc.gameSettings.keyBindJump.setPressed(false);
        }
    }

    @Override
    public void onEnable() {
        resetKeys();
        isDodging = false;
        isLookingAtThrower = false;
        dangerousPotion = null;
        potionThrower = null;
        lastDodgeDirection = null;
      //  AbstractCommand.addMessage("§dAutoDodge §aвключен");
    }

    @Override
    public void onDisable() {
        resetKeys();
        isDodging = false;
        isLookingAtThrower = false;
        dangerousPotion = null;
        potionThrower = null;
        lastDodgeRight = null;
        lastDangerousTrident = null;
     //   AbstractCommand.addMessage("§dAutoDodge §cвыключен");
    }

    private int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            delay = System.currentTimeMillis();
            return hbSlot;
        }
        if (invSlot != -1) {
            handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.playerController.pickItem(invSlot);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }

    private int getItem(Item input, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (itemStack.getItem() instanceof AirItem) continue;
            if (itemStack.getItem() == input) return i;
        }
        return -1;
    }

    private Entity findNearestPlayer() {
        Entity nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PlayerEntity && entity != mc.player) {
                double distance = entity.getDistanceSq(dangerousPotion);
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = entity;
                }
            }
        }
        return nearest;
    }

    private PotionEntity findDangerousPotion() {
        double maxDistance = dodgeDistance.get();
        PotionEntity nearestDangerousPotion = null;
        double nearestDistance = maxDistance;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof PotionEntity) {
                PotionEntity potionEntity = (PotionEntity) entity;

                if (potionEntity.func_234616_v_() == mc.player) {
                    continue;
                }
                double distance = mc.player.getDistance(entity);

                if (distance < nearestDistance) {
                    List<EffectInstance> effects = PotionUtils.getEffectsFromStack(potionEntity.getItem());
                    boolean hasDangerousEffect = false;

                    for (EffectInstance effect : effects) {
                        if (isNegativeEffect(effect)) {
                            hasDangerousEffect = true;
                            break;
                        }
                    }

                    if (hasDangerousEffect) {
                        nearestDangerousPotion = potionEntity;
                        nearestDistance = distance;
                    }
                }
            }
        }
        return nearestDangerousPotion;
    }

    private boolean isNegativeEffect(EffectInstance effect) {
        Effect potionEffect = effect.getPotion();
        return potionEffect == Effects.POISON ||
                potionEffect == Effects.SLOWNESS ||
                potionEffect == Effects.WEAKNESS ||
                potionEffect == Effects.HUNGER ||
                potionEffect == Effects.NAUSEA ||
                potionEffect == Effects.BLINDNESS ||
                potionEffect == Effects.WITHER ||
                potionEffect == Effects.INSTANT_DAMAGE ||
                potionEffect == Effects.JUMP_BOOST ||
                potionEffect == Effects.LEVITATION ||
                potionEffect == Effects.MINING_FATIGUE ||
                potionEffect == Effects.BAD_OMEN ||
                potionEffect == Effects.UNLUCK ||
                potionEffect == Effects.SLOW_FALLING;
    }

    private TridentEntity findDangerousTrident() {
        TridentEntity closestTrident = null;
        double closestDistance = Double.MAX_VALUE;

        for (Entity entity : mc.world.getAllEntities()) {
            if (entity instanceof TridentEntity) {
                TridentEntity trident = (TridentEntity) entity;

                if (trident.func_234616_v_() != null && trident.dealtDamage) {
                    continue;
                }


                if (trident.func_234616_v_() == mc.player) {
                    continue;
                }

                Vector3d tridentPos = trident.getPositionVec();
                double distance = mc.player.getPositionVec().distanceTo(tridentPos);

                if (distance < closestDistance && distance < dodgeDistance.get()) {
                    Vector3d motion = trident.getMotion();
                    if (motion.lengthSquared() > 0.1) {
                        closestTrident = trident;
                        closestDistance = distance;
                    }
                }
            }
        }

        return closestTrident;
    }

    private Vector3d predictTridentLanding(TridentEntity trident) {
        Vector3d tridentPos = trident.getPositionVec();
        Vector3d tridentMotion = trident.getMotion();
        Vector3d lastPos = tridentPos;

        double drag = 0.995;
        double gravity = 0.03;
        double stepSize = 0.1;

        for (int i = 0; i < 300; i++) {

            lastPos = tridentPos;


            tridentPos = tridentPos.add(
                    tridentMotion.x * stepSize,
                    tridentMotion.y * stepSize,
                    tridentMotion.z * stepSize
            );


            tridentMotion = new Vector3d(
                    tridentMotion.x * drag,
                    (tridentMotion.y - gravity) * drag,
                    tridentMotion.z * drag
            );


            RayTraceContext context = new RayTraceContext(
                    lastPos,
                    tridentPos,
                    RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE,
                    trident
            );

            BlockRayTraceResult result = mc.world.rayTraceBlocks(context);
            if (result.getType() != RayTraceResult.Type.MISS) {
                return result.getHitVec();
            }

            if (tridentPos.y <= 0) {
                return new Vector3d(tridentPos.x, 0, tridentPos.z);
            }
        }

        return tridentPos;
    }
}