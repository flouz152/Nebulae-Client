package beame.components.modules.misc;

import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class AutoMyst extends Module {
// leaked by itskekoff; discord.gg/sk3d BQNbQY3R
    public AutoMyst() {
        super("AutoMyst", Category.Player, true, "Автоматически забирает предметы сундуков на событиях");
        /*addSettings(mode);*/
    }

    //public ModeSetting mode = new ModeSetting("Тип", "Обычный", "Обычный", "Экспресс");

    private TimerUtil timer = new TimerUtil();

    private void moveItemFromChest(ChestContainer container, int slotIndex) {
        String title = mc.currentScreen.getTitle().getString().toLowerCase();
        if (title.contains("аукционы") || title.contains("поиск:") || title.contains("хранилище") || title.contains("эндер-сундук")) {
            return;
        }
        mc.playerController.windowClick(container.windowId, slotIndex, 0, ClickType.QUICK_MOVE, mc.player);
    }

    //public ChestRotation chestRotation = new ChestRotation();
    //public ExpressStealer expressStealer = new ExpressStealer();

    @Override
    public void event(Event event) {
        if(event instanceof EventUpdate){
            //if(!mode.is("Экспресс")) {
            Container var2 = mc.player.openContainer;
            if (var2 instanceof ChestContainer container) {
                int size = container.getLowerChestInventory().getSizeInventory();
                List<Integer> prioritySlots = Arrays.asList(53, 51, 49, 47, 45, 43, 41, 39, 37, 35, 33, 31, 29, 27, 25, 23, 21, 19, 17, 15, 13, 11, 9, 7, 5, 3, 1, 52, 50, 48, 46, 44, 42, 40, 38, 36, 34, 32, 30, 28, 26, 24, 22, 20, 18, 16, 14, 12, 10, 8, 6, 4, 2, 0);
                Iterator var6 = prioritySlots.iterator();

                int slotIndex;
                ItemStack stackInSlot;
                while (var6.hasNext()) {
                    slotIndex = (Integer) var6.next();
                    if (slotIndex < size) {
                        stackInSlot = container.getLowerChestInventory().getStackInSlot(slotIndex);
                        if (!stackInSlot.isEmpty() && this.timer.hasTimeElapsed(55)) {
                            this.moveItemFromChest(container, slotIndex);
                            this.timer.reset();
                        }
                    }
                }
            }
            /*} else if(mode.is("Экспресс")) {
                expressStealer.onTick();
            }*/
        }
        /*if(event instanceof EventMotion motion) {
            if(mode.is("Экспресс")) {
                chestRotation.rotate(motion);
            }
        }*/
    }
}
