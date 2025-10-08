package beame.components.modules.player;

import events.Event;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockRayTraceResult;

public class AutoTool extends Module {
// leaked by itskekoff; discord.gg/sk3d bREQNhmX
    public AutoTool() {
        super("AutoTool", Category.Player, true, "Автоматически побирает предмет по ломающийся блок");
    }

    int itemIndex = -1;
    int oldSlot = -1;
    boolean status;

    private int findBestToolSlotInHotBar() {
        Object object = mc.objectMouseOver;
        if (object instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockRayTraceResult = (BlockRayTraceResult)object;
            object = mc.world.getBlockState(blockRayTraceResult.getPos()).getBlock();
            int n = -1;
            float f = 1.0f;
            for (int i = 0; i < 9; ++i) {
                float f2 = mc.player.inventory.getStackInSlot(i).getDestroySpeed(((Block)object).getDefaultState());
                if (!(f2 > f)) continue;
                f = f2;
                n = i;
            }
            return n;
        }
        return 1;
    }

    private boolean isMousePressed() {
        return mc.objectMouseOver != null && mc.gameSettings.keyBindAttack.isKeyDown();
    }

    @Override
    public void event(Event event) {
        if(event instanceof Render2DEvent) {
            if (mc.player == null || mc.player.isCreative()) {
                itemIndex = -1;
                return;
            }
            if (isMousePressed()) {
                itemIndex = findBestToolSlotInHotBar();
                if (itemIndex != -1) {
                    status = true;
                    if (oldSlot == -1) {
                        oldSlot = mc.player.inventory.currentItem;
                    }
                    mc.player.inventory.currentItem = itemIndex;
                }
            } else if (status && oldSlot != -1) {
                mc.player.inventory.currentItem = oldSlot;
                itemIndex = oldSlot;
                status = false;
                oldSlot = -1;
            }
        }
    }

    @Override
    public void onDisable() {
        status = false;
        itemIndex = -1;
        oldSlot = -1;
        super.onDisable();
    }
}
