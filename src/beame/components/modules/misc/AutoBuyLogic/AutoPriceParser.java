package beame.components.modules.misc.AutoBuyLogic;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.components.modules.misc.AutoBuyLogic.Items.AutoBuyItemClass;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.PotionUtils;
import beame.components.modules.misc.AutoBuyLogic.Items.PotionEffectMatcher;
import events.Event;
import events.Listener;
import events.impl.player.EventUpdate;
import events.impl.player.EventContainerUpdated;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static beame.util.IMinecraft.mc;

public class AutoPriceParser implements Listener<Event> {
// leaked by itskekoff; discord.gg/sk3d KmTq0hJI
    private boolean isParsingPrices = false;
    private int currentParsingIndex = 0;
    private List<AutoBuyItemClass> itemsToParse = new ArrayList<>();
    private TimerUtil parsingDelay = new TimerUtil();
    private TimerUtil pageUpdateTimer = new TimerUtil();
    private TimerUtil closeScreenTimer = new TimerUtil();
    private TimerUtil analysisDelay = new TimerUtil();
    private boolean waitingForResults = false;
    private boolean needsPageUpdate = false;

    public AutoPriceParser() {
        Event AutoPriceParser;
        events.EventManager.call(new Event());
    }

    @Override
    public void handle(Event event) {
        if (event instanceof EventUpdate) {
            if (!Nebulae.getHandler().getModuleList().autoBuy.parser.get() && isParsingPrices) {
                stopPriceParsing();
            }
        } else if (event instanceof EventContainerUpdated) {
            if (!Nebulae.getHandler().getModuleList().autoBuy.parser.get() && isParsingPrices) {
                stopPriceParsing();
            }
            if (isParsingPrices && isWaitingForResults()) {
                processCurrentSearch();
            } else if (isParsingPrices && beame.components.modules.misc.AutoBuyLogic.AutoBuyUtil.isAuctionOpened()) {
                checkParsingDelay();
            }
        }
    }

    public boolean isWaitingForResults() {
        return waitingForResults;
    }

    public void startPriceParsing() {
        if (!Nebulae.getHandler().getModuleList().autoBuy.parser.get()) {
       //     AbstractCommand.addMessage("${red}Автопарсер отключен! Включите настройку 'Автопарсер' в AutoBuyGUI.");
            return;
        }

        if (!AutoBuyUtil.isAuctionOpened()) {
      //      AbstractCommand.addMessage("${red}Аукцион не открыт!");
            return;
        }

        itemsToParse.clear();
        for (AutoBuyItemClass item : Nebulae.getHandler().autoBuy.items.list) {
            if (item.isParsingEnabled) {
                itemsToParse.add(item);
            }
        }

        if (itemsToParse.isEmpty()) {
            AbstractCommand.addMessage("Нет предметов для парсинга!");
            return;
        }

        isParsingPrices = true;
        currentParsingIndex = 0;
        parsingDelay.reset();
        closeScreenTimer.reset();
        waitingForResults = false;
        needsPageUpdate = false;
        pageUpdateTimer.reset();
        AbstractCommand.addMessage("Начинаю парсинг цен для ${green}" + itemsToParse.size() + "${white} предметов...");
    //    AbstractCommand.addMessage("${white}Автопарсер будет автоматически делить цену лота на количество предметов");

        startNextSearch();
    }

    public void stopPriceParsing() {
        if (isParsingPrices) {
            isParsingPrices = false;
            waitingForResults = false;
            needsPageUpdate = false;
            closeScreenTimer.reset();
            AbstractCommand.addMessage("Парсинг цен остановлен!");
        }
    }

    public boolean isParsing() {
        return isParsingPrices;
    }

    private void startNextSearch() {
        if (currentParsingIndex >= itemsToParse.size()) {
            isParsingPrices = false;
            waitingForResults = false;
            needsPageUpdate = false;
            AbstractCommand.addMessage("Парсинг цен завершен!");
            Nebulae.getHandler().autoBuy.savePrices();
            Nebulae.getHandler().getModuleList().autoBuy.parser.set(false);
            
            return;
        }

        AutoBuyItemClass currentItem = itemsToParse.get(currentParsingIndex);

        if (!parsingDelay.hasTimeElapsed(1550)) {
            return;
        }

        if (currentItem.displayName.equalsIgnoreCase("Элитры Крушителя")) {
            mc.player.sendChatMessage("/ah search Элитры");
        } else {
            mc.player.sendChatMessage("/ah search " + currentItem.displayName);
        }

        parsingDelay.reset();

        waitingForResults = true;
        needsPageUpdate = true;
        pageUpdateTimer.reset();
        analysisDelay.reset();
    }

    public void updatePriceParsing() {
        if (!isParsingPrices) return;

        if (!Nebulae.getHandler().getModuleList().autoBuy.parser.get()) {
            return;
        }


        if (needsPageUpdate && pageUpdateTimer.hasTimeElapsed(50)) {
            if (AutoBuyUtil.isAuctionOpened()) {
                InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, true);
        //        AbstractCommand.addMessage("${white}Обновляю страницу...");
            }
            needsPageUpdate = false;
        }
    }

    public void processCurrentSearch() {
        if (!isParsingPrices || !waitingForResults) return;

        if (!analysisDelay.hasTimeElapsed(100)) {
            return;
        }

        waitingForResults = false;

        ChestContainer ah = (ChestContainer) mc.player.openContainer;
        if (ah == null) {
            return;
        }

        AutoBuyItemClass currentItem = itemsToParse.get(currentParsingIndex);
        String title = mc.currentScreen.getTitle().getString();
        String expectedTitle = "Поиск: " + currentItem.displayName;
        if (!title.startsWith(expectedTitle)) {
            mc.player.sendChatMessage("/ah search " + currentItem.displayName);
            waitingForResults = true;
            return;
        }

        int lowestPrice = Integer.MAX_VALUE;
        int itemsFound = 0;
        int itemsWithPrice = 0;
        int totalItemsInLots = 0;

        boolean isPotion = currentItem.potionEffects != null && !currentItem.potionEffects.isEmpty();
        boolean isKrushElytra = currentItem.displayName.equalsIgnoreCase("Элитры Крушителя");

        for (Slot slot : ah.inventorySlots) {
            if (slot.slotNumber > 44) continue;
            if (!slot.getHasStack()) continue;
            if (slot.getStack().isEmpty()) continue;

            itemsFound++;
            boolean isCorrectItem = false;

            if (isPotion) {
                if (slot.getStack().getItem() == currentItem.item) {
                    List<EffectInstance> itemEffects = PotionUtils.getFullEffectsFromItem(slot.getStack());
                    if (itemEffects.size() == currentItem.potionEffects.size()) {
                        boolean allEffectsMatch = true;
                        for (PotionEffectMatcher requiredEffect : currentItem.potionEffects) {
                            boolean foundMatch = false;
                            for (EffectInstance itemEffect : itemEffects) {
                                int id = Effect.getId(itemEffect.getPotion());
                                int amplifier = itemEffect.getAmplifier();
                                int duration = itemEffect.getDuration() / 20;
                                if (id == requiredEffect.id && amplifier == requiredEffect.amplifier) {
                                    if (requiredEffect.duration != -1) {
                                        if (duration == requiredEffect.duration) {
                                            foundMatch = true;
                                            break;
                                        }
                                    } else {
                                        foundMatch = true;
                                        break;
                                    }
                                }
                            }
                            if (!foundMatch) {
                                allEffectsMatch = false;
                                break;
                            }
                        }
                        if (allEffectsMatch) isCorrectItem = true;
                    }
                }
            } else if (isKrushElytra) {
                if (slot.getStack().getItem() == currentItem.item) {
                    boolean allEnchantsMatch = true;
                    if (currentItem.enchants != null && !currentItem.enchants.isEmpty()) {
                        for (beame.components.modules.misc.AutoBuyLogic.Items.Enchant requiredEnchant : currentItem.enchants) {
                            if (!requiredEnchant.has(slot.getStack())) {
                                allEnchantsMatch = false;
                                break;
                            }
                        }
                    }
                    if (allEnchantsMatch) isCorrectItem = true;
                }
            } else {
                if (slot.getStack().getItem() == currentItem.item) {
                    isCorrectItem = true;
                }
            }

            if (isCorrectItem) {
                int price = beame.components.modules.misc.AutoBuyLogic.AutoBuyUtil.getPrice(slot.getStack());
                int itemCount = slot.getStack().getCount();
                if (price > 0 && itemCount > 0) {
                    itemsWithPrice++;
                    int finalPrice = price / itemCount;
                    if (finalPrice < 1) {
                        continue;
                    }
                    if (finalPrice < lowestPrice) {
                        lowestPrice = finalPrice;
                    }
                }
            }
        }

        if (lowestPrice != Integer.MAX_VALUE) {
            float discountPercent = Nebulae.getHandler().getModuleList().autoBuy.parser2.get();
            int newPrice = (int) (lowestPrice * (1 - discountPercent / 100.0));
            currentItem.buyPrice = newPrice;
            AbstractCommand.addMessage("${white}Обновлена цена для ${green}" + currentItem.displayName + "${white}: ${yellow}" + lowestPrice + "${white} → ${green}" + newPrice + "${white} (скидка ${red}" + discountPercent + "%${white})");
        }

        closeScreenTimer.reset();

        currentParsingIndex++;
        waitingForResults = false;
        parsingDelay.reset();
    }

    public void checkParsingDelay() {
        if (isParsingPrices && !waitingForResults && parsingDelay.hasTimeElapsed(1000) && closeScreenTimer.hasTimeElapsed(100)) {
            if (Nebulae.getHandler().getModuleList().autoBuy.parser.get()) {
                startNextSearch();
            }
        }
    }

    public void up() {
        if (!Nebulae.getHandler().getModuleList().autoBuy.parser.get() && isParsingPrices) {
            stopPriceParsing();
            return;
        }
        if (isParsingPrices) {
            updatePriceParsing();
            checkParsingDelay();
        }
    }

    public void togglePriceParsing() {
        if (isParsingPrices) {
            stopPriceParsing();
        } else {
            startPriceParsing();
        }
    }
}