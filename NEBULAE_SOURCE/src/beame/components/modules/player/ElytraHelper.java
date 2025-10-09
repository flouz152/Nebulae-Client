package beame.components.modules.player;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import events.Event;
import events.EventKey;
import events.impl.packet.EventPacket;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;

public class ElytraHelper extends Module {
// leaked by itskekoff; discord.gg/sk3d nb5cpgP3
    private final BindSetting swapChestKey = new BindSetting("Элитра свап", -1);
    private final BindSetting fireWorkKey = new BindSetting("Фейерверк", -1);
    private final BooleanSetting bypas = new BooleanSetting("Обход надевания в кт", true);
    private final InventoryUtility.Hand handUtil = new InventoryUtility.Hand();
    private ItemStack currentStack = ItemStack.EMPTY;
    private long delay;
    private boolean fireworkUsed;
    public static TimerUtil timerUtility = new TimerUtil();
    public static TimerUtil fireWorkTimerUtility = new TimerUtil();
    public TimerUtil wait = new TimerUtil();

    public ElytraHelper() {
        super("ElytraHelper", Category.Player, true, "Помощник в использовании элитры игроком");
        this.addSettings(this.swapChestKey, this.fireWorkKey, bypas);
    }

    @Override
    public void event(Event event) {
        if(event instanceof EventKey) {
            EventKey e = (EventKey) event;
            if (e.key == (this.swapChestKey.get()) && timerUtility.hasReached(200L)) {
                this.changeChestPlate(this.currentStack);
                timerUtility.reset();
            }
            if (e.key == this.fireWorkKey.get() && mc.player.isElytraFlying()) {
                InventoryUtility.inventorySwapClick(Items.FIREWORK_ROCKET, false);
            }
        }
        if(event instanceof Render2DEvent) {
            this.currentStack = mc.player.getItemStackFromSlot(EquipmentSlotType.CHEST);
            this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
        }
        if(event instanceof EventPacket) {
            handUtil.onEventPacket((EventPacket) event);
        }
    }
    
    public void changeChestPlate(ItemStack stack) {
        if (bypas.get()) {
            if (stack == null || stack.getItem() != Items.ELYTRA) {
                int elytraSlot = getItemSlot(Items.ELYTRA);
                if (elytraSlot == -1) {
                    AbstractCommand.addMessage("Нету: Элитра");
                    return;
                }
                mc.playerController.windowClick(0, elytraSlot, 38, ClickType.SWAP, mc.player);
                AbstractCommand.addMessage("Свап -> Элитра");
            } else {
                int chestSlot = getChestPlateSlot();
                if (chestSlot == -1) {
                    AbstractCommand.addMessage("Нету: Нагрудник");
                    return;
                }
                mc.playerController.windowClick(0, chestSlot, 38, ClickType.SWAP, mc.player);
                AbstractCommand.addMessage("Свап -> Нагрудник (байпас)");
            }
            return;
        }
        boolean shouldDisableMove = Nebulae.getHandler().getModuleList().guiMove.funtime.get();

        KeyBinding[] pressedKeys = {
                mc.gameSettings.keyBindForward,
                mc.gameSettings.keyBindBack,
                mc.gameSettings.keyBindLeft,
                mc.gameSettings.keyBindRight,
                mc.gameSettings.keyBindJump,
                mc.gameSettings.keyBindSprint
        };

        for (KeyBinding keyBinding : pressedKeys) {
            keyBinding.setPressed(false);
        }

        if (shouldDisableMove) {
            Nebulae.getHandler().disableMove = true;
        }

        new Thread(() -> {
            boolean sss = false;

            try {
                Thread.sleep(150);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }

            int armorSlot;
            if (mc.currentScreen != null) {
                if (shouldDisableMove) {
                    Nebulae.getHandler().disableMove = false;
                }
                return;
            }
            if (stack != null && stack.getItem() != Items.ELYTRA) {
                int elytraSlot = this.getItemSlot(Items.ELYTRA);
                if (elytraSlot >= 0) {
                    InventoryUtility.moveItem(elytraSlot, 6);
                    sss = true;
                    AbstractCommand.addMessage("Свап -> Элитра");
                }
                if(!sss) AbstractCommand.addMessage("Нету: Элитра");
            }
            if(!sss) {
                if ((armorSlot = this.getChestPlateSlot()) >= 0) {
                    InventoryUtility.moveItem(armorSlot, 6);
                    AbstractCommand.addMessage("Свап -> Нагрудник");
                } else {
                    AbstractCommand.addMessage("Нету: Нагрудник");
                }
            }

            for (KeyBinding keyBinding : pressedKeys) {
                boolean press = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                keyBinding.setPressed(press);
            }
            if (shouldDisableMove) {
                Nebulae.getHandler().disableMove = false;
            }
            sss = false;
        }).start();
    }

    public void swapChestByBypas() {
        if (!bypas.get()) return;
        int dalbaev = 0;
        int chestSlot = getChestPlateSlot();
        if (chestSlot == -1) return;
        InventoryUtility.moveItem(chestSlot, dalbaev);
        mc.playerController.windowClick(0, dalbaev < 9 ? dalbaev + 36 : dalbaev, 38, ClickType.SWAP, mc.player);
    }


    private int getChestPlateSlot() {
        Item[] items;
        for (Item item : items = new Item[]{Items.NETHERITE_CHESTPLATE, Items.DIAMOND_CHESTPLATE, Items.LEATHER_CHESTPLATE}) {
            for (int i = 0; i < 36; ++i) {
                Item stack = mc.player.inventory.getStackInSlot(i).getItem();
                if (stack != item) continue;
                if (i < 9) {
                    i += 36;
                }
                return i;
            }
        }
        return -1;
    }

    @Override
    public void onDisable() {
        timerUtility.reset();
        super.onDisable();
    }

    private int getItemSlot(Item input) {
        int slot = -1;
        for (int i = 0; i < 36; ++i) {
            ItemStack s = mc.player.inventory.getStackInSlot(i);
            if (s.getItem() != input) continue;
            slot = i;
            break;
        }
        if (slot < 9 && slot != -1) {
            slot += 36;
        }
        return slot;
    }
}
