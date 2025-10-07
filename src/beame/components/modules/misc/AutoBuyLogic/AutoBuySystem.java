package beame.components.modules.misc.AutoBuyLogic;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.components.modules.misc.AutoBuyLogic.AutoBuyUtil.MessageType;
import beame.components.modules.misc.AutoBuyLogic.Items.AutoBuyItemClass;
import beame.components.modules.misc.AutoBuyLogic.Items.AutoBuyItems;
import beame.components.modules.misc.AutoBuyLogic.Items.BuyedItem;
import beame.components.modules.misc.AutoBuyLogic.Items.PotionEffectMatcher;
import beame.util.IMinecraft;
import beame.util.math.TimerUtil;
import beame.util.player.InventoryUtility;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import events.impl.player.EventUpdate;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;

public class AutoBuySystem {
// leaked by itskekoff; discord.gg/sk3d NK1zUJIN
    public int curping = 0;
    public AutoBuyItems items = new AutoBuyItems();
    public final List<BuyedItem> rlyBuyedItems = new ArrayList();
    private final Object buyedItemsLock = new Object();
    public BuyedItem purchasingItem = null;
    private int lastAttemptedPrice = -1;
    private String lastAttemptedItemName = "";
    public TimerUtil updater = new TimerUtil();
    public TimerUtil sleep = new TimerUtil();
    public TimerUtil buy = new TimerUtil();
    public TimerUtil ping = new TimerUtil();
    private final Path priceFile = Nebulae.getHandler().getClientDir().resolve("autobuy.json");
    private final Gson gson = new Gson();
    private boolean isParsingPrices = false;
    private int currentParsingIndex = 0;
    private TimerUtil parsingDelay = new TimerUtil();
    private List<AutoBuyItemClass> itemsToParse = new ArrayList();
    private boolean enabled = false;
    public AutoPriceParser priceParser = new AutoPriceParser();

    public AutoBuySystem() {
    }

    public void enable() {
        if (!this.enabled) {
            this.enabled = true;
        }

    }

    public void disable() {
        if (this.enabled) {
            this.enabled = false;
        }

    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public List<BuyedItem> getBuyedItems() {
        synchronized(this.buyedItemsLock) {
            return new ArrayList(this.rlyBuyedItems);
        }
    }

    private void addBuyedItem(BuyedItem item) {
        if (item != null) {
            synchronized(this.buyedItemsLock) {
                if (!this.rlyBuyedItems.isEmpty() && ((BuyedItem)this.rlyBuyedItems.get(0)).equals(item)) {
                    System.out.println("[AutoBuy] Не добавлен (уже есть в истории): " + item.abItem.displayName);
                } else {
                    item.buyed = false;
                    this.rlyBuyedItems.add(0, item);
                    System.out.println("[AutoBuy] Добавлен в историю: " + item.abItem.displayName + ", цена: " + item.price);
                }

            }
        }
    }

    public void clearBuyedItems() {
        synchronized(this.buyedItemsLock) {
            this.rlyBuyedItems.clear();
        }

        AbstractCommand.addMessage("История покупок очищена.");
    }

    public void onUpdate(EventUpdate event) {
        if (this.enabled) {
            long delay = (long)Math.min(350.0F, (float)AutoBuyUtil.calculateDelay() * (Nebulae.getHandler().autoBuyGUI.server == 0 ? 2.0F : 6.0F));
            if (this.updater.hasTimeElapsed(delay)) {
                this.pushUpdatePage();
                this.updater.reset();
            }

            if (this.ping.hasTimeElapsed(15000L)) {
                IMinecraft.mc.player.sendChatMessage("/ping");
                this.ping.reset();
            }

            Nebulae.getHandler().autoSell.onUpdate();
        }
    }

    public boolean onChatMessage(String text) {
        if (!this.enabled) {
            return false;
        } else {
            Nebulae.getHandler().autoSell.onChatMessage(text);
            AutoBuyUtil.MessageType type = AutoBuyUtil.getMessageType(text);
            if (type == MessageType.No) {
                if (text.startsWith("Ваш пинг: ")) {
                    try {
                        this.curping = Integer.parseInt(text.replace("Ваш пинг: ", ""));
                    } catch (Exception var9) {
                    }

                    return true;
                } else {
                    return false;
                }
            } else if (type == MessageType.Buy) {
                String info = AutoBuyUtil.extractBuyInformation(text);
                if (info.isEmpty()) {
                    return false;
                } else {
                    int price = Integer.parseInt(info.split("[|]")[0].replace(",", ""));
                    String item = info.split("[|]")[1];
                    System.out.println("[AutoBuy] Покупка: из чата цена=" + price + ", lastAttemptedPrice=" + this.lastAttemptedPrice + ", предмет: " + item);
                    if (price == this.lastAttemptedPrice && !this.rlyBuyedItems.isEmpty()) {
                        synchronized(this.buyedItemsLock) {
                            BuyedItem lastItem = (BuyedItem)this.rlyBuyedItems.get(0);
                            lastItem.buyed = true;
                            System.out.println("[AutoBuy] buyed=true для: " + lastItem.abItem.displayName + ", цена: " + lastItem.price);
                        }
                    } else {
                        System.out.println("[AutoBuy] Не совпала цена или история пуста");
                    }

                    String message = "Куплен предмет \"" + TextFormatting.GREEN + item + TextFormatting.WHITE + "\" за " + TextFormatting.YELLOW + price + " монет" + TextFormatting.WHITE + ".";
                    AbstractCommand.addMessage(message);
                    return true;
                }
            } else {
                if (type == MessageType.Wait) {
                    this.sleep.reset();
                } else {
                    if (type == MessageType.NoMoney) {
                        String message = " Не хватило денег на покупку товара";
                        AbstractCommand.addMessage(message);
                        this.pushTGMessage("Не хватило денег для покупки товара, проверьте баланс аккаунта!");
                        return true;
                    }

                    if (type == MessageType.Purchased) {
                        String message = " К сожалению, товар уже куплен";
                        AbstractCommand.addMessage(message);
                        return true;
                    }
                }

                return false;
            }
        }
    }

    public void processBuy() {
        if (this.enabled) {
            if (AutoBuyUtil.isAuctionOpened()) {
                boolean canBuy = this.sleep.hasTimeElapsed(700L);
                if (canBuy) {
                    ChestContainer ah = (ChestContainer)IMinecraft.mc.player.openContainer;
                    boolean scaryPrice = IMinecraft.mc.currentScreen.getTitle().getString().toLowerCase().contains("подозрительная цена") || IMinecraft.mc.currentScreen.getTitle().getString().toLowerCase().contains("подтверждение покупки");
                    if (scaryPrice) {
                        AbstractCommand.addMessage("Подтверждение покупки...");
                        InventoryUtility.clickSlotId(0, 0, ClickType.QUICK_MOVE, false);
                    } else {
                        for(Slot slot : ah.inventorySlots) {
                            if (slot.slotNumber <= 44 && slot.getHasStack() && !slot.getStack().isEmpty()) {
                                int price = AutoBuyUtil.getPrice(slot.getStack());
                                if (price != -1) {
                                    int finalPrice = price / slot.getStack().getCount();
                                    AutoBuyItemClass itemToBuy = null;
                                    if (slot.getStack().getItem() == Items.ENCHANTED_BOOK) {
                                        itemToBuy = Nebulae.getHandler().autoBuy.items.isNeedToBuyEnchanted(slot.getStack());
                                    } else {
                                        itemToBuy = Nebulae.getHandler().autoBuy.items.isNeedToBuy(slot.getStack());
                                    }

                                    if (itemToBuy == null && slot.getStack().isEnchanted()) {
                                        itemToBuy = Nebulae.getHandler().autoBuy.items.isNeedToBuyEnchanted(slot.getStack());
                                    }

                                    if (itemToBuy == null) {
                                        String itemType = AutoBuyUtil.getSpookyItemType(slot.getStack());
                                        if (itemType != null) {
                                            itemToBuy = Nebulae.getHandler().autoBuy.items.isNeedToBuy(slot.getStack(), itemType);
                                        }
                                    }

                                    if (itemToBuy == null) {
                                        HashMap<Attribute, Map.Entry<Float, AttributeModifier.Operation>> attributes = AutoBuyUtil.getAttributes(slot.getStack());
                                        if (attributes != null && !attributes.isEmpty()) {
                                            itemToBuy = Nebulae.getHandler().autoBuy.items.isNeedToBuy(slot.getStack(), attributes);
                                        }
                                    }

                                    if (itemToBuy == null) {
                                        itemToBuy = Nebulae.getHandler().autoBuy.items.isNeedToBuyPotion(slot.getStack());
                                    }

                                    if (itemToBuy != null) {
                                        int limitPrice = itemToBuy.buyPrice;
                                        if (finalPrice <= Math.max(10, limitPrice)) {
                                            this.lastAttemptedPrice = price;
                                            this.lastAttemptedItemName = slot.getStack().getDisplayName().getString();
                                            BuyedItem buyedItem = new BuyedItem(slot.getStack(), slot.getStack().copy(), price, slot.getStack().getCount(), itemToBuy, false, LocalDateTime.now());
                                            this.addBuyedItem(buyedItem);
                                            this.purchasingItem = buyedItem;
                                            InventoryUtility.clickSlotId(slot.slotNumber, 0, ClickType.QUICK_MOVE, false);
                                            return;
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    private void pushTGMessage(String content) {
        if (Nebulae.getHandler().telegram != null) {
            if (Nebulae.getHandler().telegram.inited) {
                Nebulae.getHandler().telegram.sendMessage(content);
            }

        }
    }

    private void pushUpdatePage() {
        if (AutoBuyUtil.isAuctionOpened()) {
            InventoryUtility.clickSlotId(49, 0, ClickType.PICKUP, true);
        }
    }

    public void savePrices() {
        try {
            List<SimpleAutoBuyData> data = new ArrayList();
            for(int i = 0; i < this.items.list.size(); i++) {
                AutoBuyItemClass item = this.items.list.get(i);
                if (item.buyPrice > 0) {
                    data.add(new SimpleAutoBuyData(i, item.buyPrice));
                }
            }
            Files.createDirectories(this.priceFile.getParent());
            String json = this.gson.toJson(data);
            Files.write(this.priceFile, json.getBytes(), new OpenOption[]{StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadPrices() {
        try {
            if (!Files.exists(priceFile)) {
                return;
            }
            String json = new String(Files.readAllBytes(priceFile));
            Type type = new TypeToken<List<SimpleAutoBuyData>>() {}.getType();
            List<SimpleAutoBuyData> data = gson.fromJson(json, type);
            for (SimpleAutoBuyData entry : data) {
                if (entry.itemIndex >= 0 && entry.itemIndex < items.list.size()) {
                    AutoBuyItemClass item = items.list.get(entry.itemIndex);
                    item.buyPrice = entry.price;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startPriceParsing() {
        this.priceParser.startPriceParsing();
    }

    public void stopPriceParsing() {
        this.priceParser.stopPriceParsing();
    }

    public boolean isParsingPrices() {
        return this.priceParser.isParsing();
    }

    public void checkParsingDelay() {
        this.priceParser.checkParsingDelay();
    }

    public void up() {
        this.priceParser.up();
    }


    private static class SimpleAutoBuyData {
        public int itemIndex;
        public int price;
        public SimpleAutoBuyData() {}
        public SimpleAutoBuyData(int itemIndex, int price) {
            this.itemIndex = itemIndex;
            this.price = price;
        }
    }
}
