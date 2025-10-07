package beame.components.modules.player.AutoExpress;

import beame.util.math.TimerUtil;
import net.minecraft.client.gui.screen.inventory.ChestScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;

import static beame.util.IMinecraft.mc;

public class ExpressStealer {
// leaked by itskekoff; discord.gg/sk3d WEZC8Yru
    public ExpressStealer() {}

    TimerUtil zak = new TimerUtil();
    TimerUtil timerHelper = new TimerUtil();
    TimerUtil helper = new TimerUtil();

    public void onTick() {
        if (mc.world != null && mc.player != null) {
            for (TileEntity te : mc.world.loadedTileEntityList) {
                if (te instanceof ChestTileEntity) {
                    if (mc.currentScreen == null) {
                        BlockPos pos = te.getPos();
                        double distanceSq = mc.player.getPositionVec().distanceTo(Vector3d.copyCentered(pos));
                        if (distanceSq <= 20.25f/3) {
                            if (helper.hasTimeElapsed(200, true)) {
                                BlockRayTraceResult result = new BlockRayTraceResult(Vector3d.copyCentered(pos).add(0.2, 0.375, 0.2), Direction.UP, pos, false);
                                mc.playerController.func_217292_a(mc.player, mc.world, Hand.MAIN_HAND, result);
                                mc.player.swingArm(Hand.MAIN_HAND);
                                break;
                            }
                        }
                    }
                }
            }
            if (mc.currentScreen == null) {
                zak.reset();
                timerHelper.reset();
            }
            if (mc.currentScreen instanceof ChestScreen) {
                if (!mc.currentScreen.getTitle().getString().contains("Аукцион") && !mc.currentScreen.getTitle().getString().contains("Поиск:")) {
                    ChestContainer container = (ChestContainer) mc.player.openContainer;
                    boolean hasItems = false;
                    for (int index = 0; index < container.getLowerChestInventory().getSizeInventory(); index++) {
                        Item item = container.getLowerChestInventory().getStackInSlot(index).getItem();
                        if (item != Items.AIR) {
                            if (item != Items.LIGHT_BLUE_STAINED_GLASS_PANE && item != Items.BLUE_STAINED_GLASS_PANE) {
                                hasItems = true;
                                break;
                            }
                        }
                    }
                    if (!hasItems) {
                        if (zak.hasTimeElapsed(250, true)) {
                            mc.player.closeScreen();
                            zak.reset();
                        }
                    } else {
                        zak.reset();
                    }
                    int containerSize = container.getLowerChestInventory().getSizeInventory();
                    if (!isInventoryFull(mc.player)) {
                        for (int index = containerSize - 1; index >= 0; index--) {
                            ItemStack stack = container.getLowerChestInventory().getStackInSlot(index);
                            Item item = stack.getItem();
                            if (item != Items.AIR && !mc.player.getCooldownTracker().hasCooldown(item)) {
                                if (item != Items.LIGHT_BLUE_STAINED_GLASS_PANE && item != Items.BLUE_STAINED_GLASS_PANE) {
                                    mc.playerController.windowClick(container.windowId, index, 1, ClickType.PICKUP, mc.player);
                                    mc.playerController.windowClick(container.windowId, index, 1, ClickType.QUICK_MOVE, mc.player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isInventoryFull(PlayerEntity player) {
        for (ItemStack stack : player.inventory.mainInventory) {
            if (stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
