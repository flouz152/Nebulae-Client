package beame.components.modules.player;

import beame.components.command.AbstractCommand;
import beame.components.modules.combat.AuraHandlers.component.core.combat.Rotation;
import beame.components.modules.combat.AuraHandlers.component.core.combat.RotationComponent;
import beame.components.modules.misc.AutoBuyLogic.AutoBuyUtil;
import beame.util.math.MovementUtil;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemOnBlockPacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import java.util.Comparator;
import java.util.List;
import java.util.stream.IntStream;


public class AutoFarm extends Module {
// leaked by itskekoff; discord.gg/sk3d mfOk8tXL
    private final TimerUtil watchClose = new TimerUtil();
    private final TimerUtil watchOther = new TimerUtil();
    private final TimerUtil inventoryTimer = new TimerUtil();
    private final TimerUtil autoToggleTimer = new TimerUtil();
    private boolean autoRepair;
    private boolean autoToggle = true;
    
    public AutoFarm() {
        super("AutoFarm", Category.Misc, true, "Автоматически добывает и продаёт морковь/сладкие ягоды");
    }

    public void toggle() {
        RotationComponent.update(new Rotation(Rotation.cameraYaw(), Rotation.cameraPitch()), 360, 360, 90, 5);
        super.toggle();
        autoRepair = false;
        autoToggleTimer.reset();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (mc.player != null) {
            RotationComponent.getInstance().stopRotation();
        }
    }

    @Override
    public void event(Event e) {
        if (e instanceof EventUpdate) {
            if (autoToggleTimer.finished(30000) && isState()) {
                watchOther.reset();
                autoToggleTimer.reset();
                RotationComponent.update(new Rotation(Rotation.cameraYaw(), Rotation.cameraPitch()), 360, 360, 90, 5);
                AbstractCommand.addMessage("Починил ферму");
                return;
            }
            
            if (!isState()) return;
            
            if (AutoInvisible.isDrinking()) {
                return;
            }

            
            List<Item> hoeItems = List.of(Items.NETHERITE_HOE, Items.DIAMOND_HOE);
            List<Item> plantsItems = List.of(Items.CARROT, Items.POTATO, Items.SWEET_BERRIES);
            Slot expSlot = InventoryUtility.getInstance().getInventorySlot(Items.EXPERIENCE_BOTTLE);
            Slot plantSlot = InventoryUtility.getInstance().getInventorySlot(plantsItems);
            Slot hoeSlot = InventoryUtility.getInstance().getInventorySlot(hoeItems);
            assert mc.player != null;
            Item mainHandItem = mc.player.getHeldItemMainhand().getItem();
            Item offHandItem = mc.player.getHeldItemOffhand().getItem();
            if (hoeSlot == null || MovementUtil.isMoving() || !watchClose.finished(400)) return;
            float itemStrength = 1 - MathHelper.clamp((float) hoeSlot.getStack().getDamage() / (float) hoeSlot.getStack().getMaxDamage(), 0, 1);
            autoRepair = itemStrength < 0.05 || itemStrength != 1 && autoRepair;

            if (mc.player.inventory.getFirstEmptyStack() == -1) {
                if (!plantsItems.contains(offHandItem) && !containerScreen()) {
                    InventoryUtility.swapHand(plantSlot, Hand.OFF_HAND, false);
                }
                if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                    if (screen.getTitle().getString().equals("● Выберите секцию")) {
                        InventoryUtility.clickSlotId(21, 0, ClickType.PICKUP, true);
                        return;
                    }
                    if (screen.getTitle().getString().equals("Скупщик еды")) {
                        int slotId;
                        if (offHandItem.equals(Items.SWEET_BERRIES)) {
                            slotId = 13;
                        } else if (offHandItem.equals(Items.CARROT)) {
                            slotId = 10;
                        } else {
                            slotId = 11;
                        }
                        InventoryUtility.clickSlotId(slotId, 0, ClickType.PICKUP, true);
                        return;
                    }
                }
                if (watchOther.every(1000)) mc.player.sendChatMessage("/buyer");
            } else if (autoRepair) {
                if (InventoryUtility.getInventoryCount(Items.EXPERIENCE_BOTTLE) > hoeSlot.getStack().getDamage() / 6) {
                    if (containerScreen()) return;
                    if (!offHandItem.equals(Items.EXPERIENCE_BOTTLE)) InventoryUtility.swapHand(expSlot, Hand.OFF_HAND, false);
                    if (!hoeItems.contains(mainHandItem)) InventoryUtility.swapHand(hoeSlot, Hand.MAIN_HAND, false);
                    BlockPos pos = mc.player.getPosition();
                    Vector3d targetPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                    double diffX = targetPos.x - mc.player.getPosX();
                    double diffY = targetPos.y - (mc.player.getPosY() + mc.player.getEyeHeight());
                    double diffZ = targetPos.z - mc.player.getPosZ();
                    double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

                    float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
                    float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

                    RotationComponent.update(new Rotation(yaw, pitch), 360, 360, 90, 5);

                    mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.OFF_HAND));
                    watchOther.setMs(500);
                } else if (watchOther.finished(1000)) {
                    if (mc.currentScreen instanceof ContainerScreen<?> screen) {
                        if (screen.getTitle().getString().contains("Пузырек опыта")) {
                            mc.player.openContainer.inventorySlots.stream().filter(s -> s.getStack().getTag() != null && s.slotNumber < 45)
                                    .min(Comparator.comparingInt(s -> AutoBuyUtil.getPrice(s.getStack()) / s.getStack().getCount()))
                                    .ifPresent(s -> InventoryUtility.clickSlot(s, 0, ClickType.QUICK_MOVE, true));
                            watchOther.setMs(500);
                            return;
                        } else if (screen.getTitle().getString().contains("Подозрительная цена")) {
                            InventoryUtility.clickSlotId(0, 0, ClickType.QUICK_MOVE, true);
                            watchOther.setMs(500);
                            return;
                        } else if (screen.getTitle().getString().contains("Аукцион")) {
                            InventoryUtility.clickSlotId(13, 0, ClickType.QUICK_MOVE, true);
                            watchOther.setMs(500);
                            return;
                        }
                    }
                    mc.player.sendChatMessage("/ah search Пузырёк Опыта");
                    watchOther.reset();
                }
            } else if (watchOther.finished(500)) {
                BlockPos pos = mc.player.getPosition();
                assert mc.world != null;

                if (inventoryTimer.finished(30000)) {
                    mc.player.sendChatMessage("/invsee " + mc.player.getName().getString());
                    inventoryTimer.reset();
                    return;
                }

                if (offHandItem.equals(Items.SWEET_BERRIES)) {
                    Vector3d targetPos = new Vector3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
                    double diffX = targetPos.x - mc.player.getPosX();
                    double diffY = targetPos.y - (mc.player.getPosY() + mc.player.getEyeHeight());
                    double diffZ = targetPos.z - mc.player.getPosZ();
                    double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

                    float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
                    float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

                    RotationComponent.update(new Rotation(yaw, pitch), 360, 360, 90, 5);
                    
                    if (mc.world.getBlockState(pos).getBlock().equals(Blocks.SWEET_BERRY_BUSH)) {
                        mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.OFF_HAND, new BlockRayTraceResult(targetPos, Direction.UP, pos, false)));
                    }
                    watchOther.setMs(500);
                    return;
                }

                if (mc.world.getBlockState(pos).getBlock().equals(Blocks.FARMLAND)) {
                    if (hoeItems.contains(mainHandItem) && plantsItems.contains(offHandItem)) {
                        Vector3d targetPos = new Vector3d(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                        double diffX = targetPos.x - mc.player.getPosX();
                        double diffY = targetPos.y - (mc.player.getPosY() + mc.player.getEyeHeight());
                        double diffZ = targetPos.z - mc.player.getPosZ();
                        double dist = Math.sqrt(diffX * diffX + diffZ * diffZ);

                        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0F;
                        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, dist));

                        RotationComponent.update(new Rotation(yaw, pitch), 360, 360, 90, 5);

                        if (!offHandItem.equals(Items.SWEET_BERRIES)) {
                            mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.OFF_HAND, new BlockRayTraceResult(targetPos, Direction.UP, pos, false)));
                            IntStream.range(0, 3).forEach(i -> mc.player.connection.sendPacket(new CPlayerTryUseItemOnBlockPacket(Hand.MAIN_HAND, new BlockRayTraceResult(targetPos, Direction.UP, pos.up(), false))));
                        }

                        if (!mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.SWEET_BERRY_BUSH)) {
                            mc.player.connection.sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.START_DESTROY_BLOCK, pos.up(), Direction.UP));
                        }
                    } else if (!containerScreen()) {
                        if (!plantsItems.contains(offHandItem) || !offHandItem.equals(plantSlot.getStack().getItem())) {
                            InventoryUtility.swapHand(plantSlot, Hand.OFF_HAND, false);
                        }
                        if (!hoeItems.contains(mainHandItem)) InventoryUtility.swapHand(hoeSlot, Hand.MAIN_HAND, false);
                    }
                }
            }
        }
    }

    public boolean containerScreen() {
        if (mc.currentScreen instanceof ContainerScreen<?>) {
            assert mc.player != null;
            mc.player.closeScreen();
            watchClose.reset();
            return true;
        }
        return false;
    }
}