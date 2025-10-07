package beame.components.modules.combat;

import beame.Essence;
import beame.components.command.AbstractCommand;
import beame.util.player.InventoryUtility;
import beame.util.math.TimerUtil;
import events.Event;
import events.EventKey;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

public class AutoSwap extends Module {
// leaked by itskekoff; discord.gg/sk3d RzdhDe6L
    public RadioSetting swapFrom = new RadioSetting("Свапать с", "Сфера", "Сфера", "Талик", "Гепл", "Щит");
    public RadioSetting swapTo = new RadioSetting("Свапать на", "Сфера", "Сфера", "Талик", "Гепл", "Щит");
    public BindSetting swapBind = new BindSetting("Кнопка свапа", 0);
    public BooleanSetting swapOnEat = new BooleanSetting("Свапать при еде", true);
    public BooleanSetting swapOnLowHP = new BooleanSetting("Свапать при низком хп", false);
    public SliderSetting hpThreshold = new SliderSetting("Хп для свапа", 10, 1, 20, 0.5f).setVisible(() -> swapOnLowHP.get());
    
    private boolean wasEating = false;
    private ItemStack previousItem = null;
    private boolean wasLowHP = false;
    private boolean wasInAir = false;
    private boolean waitingForGround = false;

    public AutoSwap() {
        super("AutoSwap", Category.Combat,true, "Перемещает предмет в левую руку" );
        addSettings(swapFrom, swapTo, swapBind, swapOnEat, swapOnLowHP, hpThreshold);
    }

    private void swap(ItemStack stack) {
        boolean curgui = Essence.getHandler().getModuleList().guiMove.isState();
        boolean shouldDisableMove = Essence.getHandler().getModuleList().guiMove.funtime.get() ;
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

        if (shouldDisableMove && mc.player.isOnGround()) {
            Essence.getHandler().disableMove = true;
        }

        final TimerUtil timer = new TimerUtil();
        timer.reset();
        final Item swapItem = stack.getItem();

        new Thread(() -> {
            while (!timer.hasReached(50)) {
                try { Thread.sleep(1); } catch (InterruptedException e) {}
            }

            boolean successful = false;
            if (mc.currentScreen != null) {
                if (shouldDisableMove) Essence.getHandler().disableMove = false;
                return;
            }

            int slot = findSlotForItem(swapItem);
            
            if (slot >= 0) {
                InventoryUtility.moveItem(slot, 45);
                successful = true;
                String itemName = getItemName(swapItem);
                AbstractCommand.addMessage("Свапнул на " + itemName);
            } else {
                AbstractCommand.addMessage("Нету предмета");
            }

            timer.reset();
            while (!timer.hasReached(1)) {
                try { Thread.sleep(1); } catch (InterruptedException e) {}
            }

            for (KeyBinding keyBinding : pressedKeys) {
                boolean press = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
                keyBinding.setPressed(press);
            }
            
            if (curgui) Essence.getHandler().getModuleList().guiMove.setState(true);
            if (shouldDisableMove) Essence.getHandler().disableMove = false;
        }).start();
    }

    private int findSlotForItem(Item targetItem) {
        if (targetItem == Items.TOTEM_OF_UNDYING || targetItem == Items.PLAYER_HEAD) {
            for (int i = 0; i < 36; ++i) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack.getItem() != targetItem) continue;
                int slot = (i < 9) ? i + 36 : i;
                
                if (isDonateItem(stack)) {
                    return slot;
                }
            }
        }
        
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != targetItem) continue;
            return (i < 9) ? i + 36 : i;
        }
        
        return -1;
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventKey) {
            EventKey e = (EventKey) event;
            if (e.key == swapBind.get()) {
                waitingForGround = true;
            }
        } else if (event instanceof EventUpdate) {
            if (waitingForGround && mc.player.onGround) {
                waitingForGround = false;
                mc.player.setMotion(mc.player.getMotion().x * 0.6, mc.player.getMotion().y, mc.player.getMotion().z * 0.6);
                performSwap();
            }
            
            handleAutoSwap();
        }
    }
    
    private void performSwap() {
        Item currentOffhandItem = mc.player.getHeldItemOffhand().getItem();
        
        Item fromItem = getItemFromSetting(swapFrom);
        Item toItem = getItemFromSetting(swapTo);
        
        Item targetItem;
        if (currentOffhandItem == fromItem) {
            targetItem = toItem;
        } else if (currentOffhandItem == toItem) {
            targetItem = fromItem;
        } else {
            targetItem = toItem;
        }
        
        boolean checkForDifferentItem = currentOffhandItem == targetItem;
        
        ItemStack itemToSwap = findItemForSwap(targetItem, checkForDifferentItem);
        if (itemToSwap != null) {
            swap(itemToSwap);
        }
    }
    
    private void handleAutoSwap() {
        if (swapOnEat.get()) {
            boolean isEating = mc.player.isHandActive() && mc.player.getActiveItemStack().isFood();
            
            if (isEating && !wasEating) {
                previousItem = mc.player.getHeldItemOffhand();
                Item targetItem = getItemFromSetting(swapTo);
                
                boolean shouldSwap = !hasImportantAttributes(mc.player.getHeldItemOffhand(), targetItem);
                
                if (shouldSwap && targetItem != null) {
                    ItemStack targetStack = findItemForSwap(targetItem, false);
                    if (targetStack != null) {
                        swap(targetStack);
                    }
                }
            } else if (!isEating && wasEating && previousItem != null) {
                swap(previousItem);
                previousItem = null;
            }
            
            wasEating = isEating;
        }
        
        if (swapOnLowHP.get()) {
            boolean isLowHP = mc.player.getHealth() <= hpThreshold.get();
            
            if (isLowHP && !wasLowHP) {
                previousItem = mc.player.getHeldItemOffhand();
                Item targetItem = getItemFromSetting(swapTo);
                
                boolean shouldSwap = !hasImportantAttributes(mc.player.getHeldItemOffhand(), targetItem);
                
                if (shouldSwap && targetItem != null) {
                    ItemStack targetStack = findItemForSwap(targetItem, false);
                    if (targetStack != null) {
                        swap(targetStack);
                    }
                }
            } else if (!isLowHP && wasLowHP && previousItem != null) {
                swap(previousItem);
                previousItem = null;
            }
            
            wasLowHP = isLowHP;
        }
    }
    
    private boolean hasImportantAttributes(ItemStack currentItem, Item targetItem) {
        if (currentItem == null || currentItem.isEmpty()) return false;
        
        if (currentItem.getItem() != Items.TOTEM_OF_UNDYING && currentItem.getItem() != Items.PLAYER_HEAD) return false;
        
        if (!hasArmorOrHealthAttribute(currentItem)) return false;
        
        ItemStack bestCandidate = findItemForSwap(targetItem, false);
        if (bestCandidate == null) return true;
        
        return !hasItemBetterArmorOrHealth(currentItem, bestCandidate);
    }
    
    private ItemStack findItemForSwap(Item targetItem, boolean findDifferent) {
        int currentItemHashCode = -1;
        if (findDifferent) {
            ItemStack currentOffhand = mc.player.getHeldItemOffhand();
            CompoundNBT tag = currentOffhand.getTag();
            if (tag != null) {
                currentItemHashCode = tag.hashCode();
            }
        }
        
        if (targetItem == Items.TOTEM_OF_UNDYING || targetItem == Items.PLAYER_HEAD) {
            for (int i = 0; i < 36; ++i) {
                ItemStack stack = mc.player.inventory.getStackInSlot(i);
                if (stack.getItem() != targetItem) continue;
                
                if (findDifferent) {
                    CompoundNBT tag = stack.getTag();
                    if (tag != null && tag.hashCode() == currentItemHashCode) continue;
                }
                
                if (isDonateItem(stack)) return stack;
            }
        }
        
        for (int i = 0; i < 36; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() != targetItem) continue;
            
            if (findDifferent) {
                CompoundNBT tag = stack.getTag();
                if (tag != null && currentItemHashCode != -1 && tag.hashCode() == currentItemHashCode) continue;
            }
            
            return stack;
        }
        
        return null;
    }
    
    private Item getItemFromSetting(RadioSetting setting) {
        if (setting.is("Талик")) return Items.TOTEM_OF_UNDYING;
        if (setting.is("Сфера")) return Items.PLAYER_HEAD;
        if (setting.is("Гепл")) return Items.GOLDEN_APPLE;
        if (setting.is("Щит")) return Items.SHIELD;
        return null;
    }
    
    private boolean isDonateItem(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        
        CompoundNBT tag = stack.getTag();
        if (tag == null) return false;

        if (tag.contains("don-item")) return true;
        if (tag.contains("donate")) return true;
        if (tag.contains("donItem")) return true;
        if (tag.contains("don_item")) return true;
        if (tag.contains("display") && tag.getCompound("display").contains("donate")) return true;
        
        return false;
    }
    
    private net.minecraft.nbt.ListNBT getAttributeModifiers(ItemStack itemStack) {
        if (itemStack == null || itemStack.isEmpty()) return null;
        
        CompoundNBT tag = itemStack.getTag();
        if (tag == null) return null;
        
        if (tag.contains("AttributeModifiers")) {
            return tag.getList("AttributeModifiers", 10);
        }
        
        return null;
    }

    private boolean hasArmorOrHealthAttribute(ItemStack itemStack) {
        net.minecraft.nbt.ListNBT attributes = getAttributeModifiers(itemStack);
        if (attributes == null) return false;
        
        for (int i = 0; i < attributes.size(); i++) {
            CompoundNBT attr = attributes.getCompound(i);
            String attrName = attr.getString("AttributeName");
            double amount = attr.getDouble("Amount");
            
            if ((attrName.contains("generic.armor") || 
                 attrName.contains("generic.maxHealth") || 
                 attrName.contains("generic.armorToughness")) && amount > 0) {
                return true;
            }
        }
        
        return false;
    }

    private boolean hasItemBetterArmorOrHealth(ItemStack current, ItemStack candidate) {
        if (current == null || current.isEmpty()) return true;
        
        boolean currentHasAttributes = hasArmorOrHealthAttribute(current);
        boolean candidateHasAttributes = hasArmorOrHealthAttribute(candidate);
        
        if (currentHasAttributes && !candidateHasAttributes) return false;
        
        if (!currentHasAttributes && candidateHasAttributes) return true;
        
        if (currentHasAttributes && candidateHasAttributes) {
            double currentTotal = getAttributeValue(current, "generic.armor") + 
                                 getAttributeValue(current, "generic.maxHealth") + 
                                 getAttributeValue(current, "generic.armorToughness");
            
            double candidateTotal = getAttributeValue(candidate, "generic.armor") + 
                                   getAttributeValue(candidate, "generic.maxHealth") + 
                                   getAttributeValue(candidate, "generic.armorToughness");
            
            return candidateTotal > currentTotal;
        }
        
        if (current.getItem() == Items.TOTEM_OF_UNDYING && candidate.getItem() != Items.TOTEM_OF_UNDYING) {
            return false;
        }
        
        if (current.getItem() != Items.TOTEM_OF_UNDYING && candidate.getItem() == Items.TOTEM_OF_UNDYING) {
            return true;
        }
        
        return false;
    }
    
    private double getAttributeValue(ItemStack item, String attributeName) {
        if (item == null || item.isEmpty()) return 0;
        
        net.minecraft.nbt.ListNBT attributes = getAttributeModifiers(item);
        if (attributes == null) return 0;
        
        double totalValue = 0;
        
        for (int i = 0; i < attributes.size(); i++) {
            CompoundNBT attr = attributes.getCompound(i);
            String attrName = attr.getString("AttributeName");
            double amount = attr.getDouble("Amount");
            
            if (attrName.contains(attributeName)) {
                totalValue += amount;
            }
        }
        
        return totalValue;
    }

    private String getItemName(Item item) {
        if (item == Items.TOTEM_OF_UNDYING) return "Талик";
        if (item == Items.PLAYER_HEAD) return "Сфера";
        if (item == Items.GOLDEN_APPLE) return "Гепл";
        if (item == Items.SHIELD) return "Щит";
        return item.toString();
    }
}
