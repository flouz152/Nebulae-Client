package beame.components.modules.player;
import beame.Essence;
import beame.feature.notify.NotificationManager;
import events.impl.player.EventUpdate;
import net.minecraft.client.gui.CommandSuggestionHelper;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.concurrent.CompletableFuture;

import beame.components.command.AbstractCommand;
import beame.util.player.InventoryUtility;
import events.Event;
import events.EventKey;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.play.client.CHeldItemChangePacket;
import net.minecraft.network.play.client.CPlayerTryUseItemPacket;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.network.play.server.SJoinGamePacket;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import beame.setting.SettingList.BindSetting;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;
import beame.setting.SettingList.EnumSetting;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FTHelper extends Module {
// leaked by itskekoff; discord.gg/sk3d MqxLHFy0

    public FTHelper() {
        super("FTHelper", Category.Player, true, "Помощник на сервере play.funtime.su");
        addSettings(options, eventDelayInterval, ah, dezori,
                dezor, yavkai, yavka, trapi, trap, plast, bojka, snejok, serka, otrigka, killera, medik, pobedka, agenta);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        random = new Random();
    }

    private boolean isProcessingEvent = false;
    private String lastNumber = "";
    private String lastFormatted = "";
    private boolean isProcessingNumberFormat = false;
    private boolean waitingForClanBalance = false;
    private boolean waitingForPlayerBalance = false;
    private String clanWithdrawAction = null;
    private String clanInvestAction = null;
    private int clanBalance = 0;
    private int playerBalance = 0;
    private final Random random;

    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public final EnumSetting options = new EnumSetting("Опции",
            new BooleanSetting("Авто GPS", true),
            new BooleanSetting("Конвертировать время", true),
            new BooleanSetting("Перевыставление предметов", false),
            new BooleanSetting("Раскрывать баны", true),
            new BooleanSetting("Авто /event delay", true),
            new BooleanSetting("Улучшать команды", false));

    public final SliderSetting eventDelayInterval = new SliderSetting("Интервал (минуты)", 1, 1, 10, 1).setVisible(() -> options.get("Авто /event delay").get());
    public BindSetting ah = new BindSetting("Поиск предмета по '/ah' ", 0);

    private long lastStorageClick = -1;
    private static final long STORAGE_CLICK_DELAY = 60100;
    private boolean waitingForStorage = false;

    private long delay;
    public boolean disorientationThrow, trapThrow, blatantThrow, serkaThrow, otrigaThrow,bojkaThrow, plastThrow, snejoktrow;
    InventoryUtility.Hand handUtil = new InventoryUtility.Hand();
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> eventDelayTask;
    public final BooleanSetting dezori = new BooleanSetting("Рядом с игроком", false);
    public BindSetting dezor = new BindSetting("Дезориентация", 0);
    public final BooleanSetting yavkai = new BooleanSetting("Рядом с игроком", false);
    public BindSetting yavka = new BindSetting("Явная пыль", 0);
    public final BooleanSetting trapi = new BooleanSetting("Рядом с игроком", false);
    public BindSetting trap = new BindSetting("Трапка", 0);
    public BindSetting plast = new BindSetting("Пласт", 0);
    public BindSetting bojka = new BindSetting("Божья аура", 0);
    public BindSetting snejok = new BindSetting("Снежок заморозка", 0);

    public BindSetting serka = new BindSetting("Серная кислота", 0);
    public BindSetting otrigka = new BindSetting("Зелье Отрыжки", 0);
    public BindSetting killera = new BindSetting("Зелье Киллера", 0);
    public BindSetting medik = new BindSetting("Зелье Медика", 0);
    public BindSetting pobedka = new BindSetting("Зелье Победителя", 0);
    public BindSetting agenta = new BindSetting("Зелье Агента", 0);

    private boolean isPayCommandProcessing = false;
    private String targetPlayerForPay = null;

    private void getBalance(String playerName) {
        try {
            if (mc.world == null || mc.player == null) {

                return;
            }

            targetPlayerForPay = playerName;

            mc.player.sendChatMessage("/bal");
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

    private String formatNumberWithCommas(String number) {
        if (number == null || number.isEmpty()) return number;

        number = number.replaceAll("[,\\s]", "");

        try {
            Long.parseLong(number);

            StringBuilder result = new StringBuilder();
            int len = number.length();

            for (int i = 0; i < len; i++) {
                if (i > 0 && (len - i) % 3 == 0) {
                    result.append(",");
                }
                result.append(number.charAt(i));
            }

            return result.toString();
        } catch (NumberFormatException e) {
            return number;
        }
    }

    private boolean isPlayerNearby(double radius) {
        if (mc.world == null || mc.player == null) return false;
        return mc.world.getPlayers().stream()
                .anyMatch(player -> player != mc.player &&
                        !player.isSpectator() &&
                        !Essence.getHandler().friends.isFriend(player.getGameProfile().getName()) &&
                        player.getDistanceSq(mc.player) <= radius * radius);
    }

    private String getItemName(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;
        try {
            return stack.getDisplayName().getString().replaceAll("§[0-9a-fk-or]", "");
        } catch (Exception e) {
            return null;
        }
    }

    private void startEventDelayTask() {
        stopEventDelayTask();

        if (options.get("Авто /event delay").get()) {
            double intervalValue = eventDelayInterval.get();
            try {
                eventDelayTask = scheduler.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (mc.player != null && options.get("Авто /event delay").get()) {
                                mc.player.sendChatMessage("/event delay");
                            }
                        } catch (Exception e) {
                        }
                    }
                }, 0, (long) intervalValue, TimeUnit.MINUTES);
            } catch (Exception e) {
            }
        }
    }

    private void stopEventDelayTask() {
        if (eventDelayTask != null && !eventDelayTask.isCancelled()) {
            eventDelayTask.cancel(false);
            eventDelayTask = null;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        startEventDelayTask();
        lastStorageClick = -1;
        waitingForStorage = true;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        disorientationThrow = false;
        trapThrow = false;
        blatantThrow = false;
        plastThrow = false;
        bojkaThrow = false;
        delay = 0;
        stopEventDelayTask();
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }

    private String processBanMessage(String message, ITextComponent originalComponent, SChatPacket packet) {
        if (!options.get("Раскрывать баны").get() || !message.contains("забанен") || !message.contains("[Подробнее]"))
            return message;

        boolean isAnticheatBan = false;
        String playerName = "";
        String reason = "";
        Style hoverStyle = null;
        Style mainStyle = null;

        for (ITextComponent sibling : originalComponent.getSiblings()) {
            if (sibling.getString().contains("[Подробнее]")) {
                hoverStyle = sibling.getStyle();
                if (hoverStyle != null && hoverStyle.getHoverEvent() != null) {
                    ITextComponent hoverText = hoverStyle.getHoverEvent().getParameter(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT);
                    if (hoverText != null) {
                        String fullText = hoverText.getString().toLowerCase();
                        if (fullText.contains("[funac]") || fullText.contains("funac") ||
                                fullText.contains("anticheat") || fullText.contains("античит") ||
                                fullText.contains("анти-чит") || fullText.contains("чит") ||
                                fullText.contains("взлом") || fullText.contains("hack") ||
                                (fullText.contains("автомат") && fullText.contains("ban")) ||
                                (fullText.contains("automat") && fullText.contains("ban"))) {
                            isAnticheatBan = true;
                        }
                    }
                }
            } else if (!sibling.getString().contains("♨") && !sibling.getString().contains("[Подробнее]")) {
                mainStyle = sibling.getStyle();
            }
        }

        try {
            if (!isAnticheatBan) {
                Essence.getHandler().notificationManager.pushNotify("Игрок был забанен администратором!", NotificationManager.Type.Staff);
            }

            playerName = message.substring(message.indexOf("]") + 1, message.indexOf("забанен")).trim();

            for (ITextComponent sibling : originalComponent.getSiblings()) {
                if (sibling.getString().contains("[Подробнее]")) {
                    hoverStyle = sibling.getStyle();
                    if (hoverStyle != null && hoverStyle.getHoverEvent() != null) {
                        ITextComponent hoverText = hoverStyle.getHoverEvent().getParameter(net.minecraft.util.text.event.HoverEvent.Action.SHOW_TEXT);
                        if (hoverText != null) {
                            String fullText = hoverText.getString();
                            int startIndex = fullText.indexOf("Причина:") + 8;
                            int endIndex = fullText.indexOf("\n", startIndex);
                            if (endIndex == -1) endIndex = fullText.length();

                            reason = fullText.substring(startIndex, endIndex).trim();
                        }
                    }
                }
            }

            if (!reason.isEmpty() && hoverStyle != null) {
                StringTextComponent prefix = null;
                for (ITextComponent sibling : originalComponent.getSiblings()) {
                    if (sibling.getString().contains("♨")) {
                        prefix = new StringTextComponent(sibling.getString() + " ");
                        prefix.setStyle(sibling.getStyle());
                        break;
                    }
                }

                StringTextComponent nameAndReason = new StringTextComponent(playerName + " забанен за " + reason + " ");
                nameAndReason.setStyle(mainStyle != null ? mainStyle : originalComponent.getStyle());

                StringTextComponent details = new StringTextComponent("[Подробнее]");
                details.setStyle(hoverStyle);

                StringTextComponent newComponent = new StringTextComponent("");
                if (prefix != null) newComponent.append(prefix);
                newComponent.append(nameAndReason);
                newComponent.append(details);

                try {
                    java.lang.reflect.Field field = SChatPacket.class.getDeclaredField("chatComponent");
                    field.setAccessible(true);
                    field.set(packet, newComponent);
                } catch (Exception ex) {
                    AbstractCommand.addMessage("Ошибка при обработке бана: " + ex.getMessage());
                }
                return newComponent.getString();
            }
        } catch (Exception e) {
            AbstractCommand.addMessage("Ошибка при обработке сообщения о бане: " + e.getMessage());
        }
        return message;
    }

    private String generateRandomClanName() {
        int length = 3 + random.nextInt(3);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(index));
        }
        return sb.toString();
    }

    private String convertTimeInMessage(String message) {
        if (!message.contains("сек")) return message;

        try {
            String[] parts = message.split(" ");
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].matches("\\d+") && i + 1 < parts.length && parts[i + 1].contains("сек")) {
                    int seconds = Integer.parseInt(parts[i]);
                    int minutes = seconds / 60;
                    int remainingSeconds = seconds % 60;

                    if (minutes > 0) {
                        String newTime;
                        if (remainingSeconds > 0) {
                            newTime = minutes + " мин " + remainingSeconds + " сек";
                        } else {
                            newTime = minutes + " мин";
                        }
                        parts[i] = newTime;
                        parts[i + 1] = "";
                    }
                }
            }

            StringBuilder result = new StringBuilder();
            for (String part : parts) {
                if (!part.isEmpty()) {
                    if (result.length() > 0) result.append(" ");
                    result.append(part);
                }
            }
            return result.toString();
        } catch (Exception ignored) {
            return message;
        }
    }

    private void processCoordinates(String message) {
        if (!options.get("Авто GPS").get()) return;

        if (message.contains("Появился на координатах")) //PushUtils.sendPush("Essence", "На твоей анархии появился ивент");
            if (message.contains("Координаты:")) {
                Pattern pattern = Pattern.compile("(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)");
                Matcher matcher = pattern.matcher(message);

                if (matcher.find()) {
                    String x = matcher.group(1);
                    String y = matcher.group(2);
                    String z = matcher.group(3);

                    mc.player.sendChatMessage(".gps " + x + " " + z);
                }
            }
    }

    @Override
    public void event(Event event) {
        if (isProcessingEvent) {
            return;
        }

        try {
            isProcessingEvent = true;

            if (mc.currentScreen instanceof ChatScreen && !isProcessingNumberFormat) {
                ChatScreen chatScreen = (ChatScreen) mc.currentScreen;
                try {
                    Field inputField = ChatScreen.class.getDeclaredField("field_146415_a");
                    inputField.setAccessible(true);
                    TextFieldWidget textField = (TextFieldWidget) inputField.get(chatScreen);

                    String currentInput = textField.getText();
                    Pattern pattern = Pattern.compile("\\b\\d+\\b");
                    Matcher matcher = pattern.matcher(currentInput);

                    if (matcher.find()) {
                        String number = matcher.group();
                        if (!number.equals(lastNumber)) {
                            lastNumber = number;
                            lastFormatted = formatNumberWithCommas(number);


                            Field suggestionField = ChatScreen.class.getDeclaredField("field_146417_i");
                            suggestionField.setAccessible(true);
                            CommandSuggestionHelper suggestions = (CommandSuggestionHelper) suggestionField.get(chatScreen);


                            SuggestionsBuilder builder = new SuggestionsBuilder(currentInput, 0);
                            builder.suggest(lastFormatted);
                            CompletableFuture<Suggestions> future = CompletableFuture.completedFuture(builder.build());


                            Field suggestionsField = CommandSuggestionHelper.class.getDeclaredField("field_228116_b_");
                            suggestionsField.setAccessible(true);
                            suggestionsField.set(suggestions, future);


                            suggestions.shouldAutoSuggest(true);
                            suggestions.init();
                        }
                    } else {
                        lastNumber = "";
                        lastFormatted = "";
                    }
                } catch (Exception e) {

                }
            }

            if(event instanceof EventUpdate) {
                if (disorientationThrow) {
                    this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
                    //    InventoryUtility.getSlotIDFromItem()

                    int hbSlot =  getItem(Items.ENDER_EYE,true);
                    int invSlot = getItem(Items.ENDER_EYE,false);

                    if (invSlot == -1 && hbSlot == -1) {
                        disorientationThrow = false;
                        AbstractCommand.addMessage("Дезориентация не найдена!");
                        Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.ENDER_EYE)) {
                        int slot = findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }
                        AbstractCommand.addMessage("Дезориентация использована!");
                    } else  AbstractCommand.addMessage("Дезориентация в КД!");
                    disorientationThrow = false;
                }

                if(bojkaThrow) {

                    int hbSlot =  getItem(Items.PHANTOM_MEMBRANE,true);
                    int invSlot = getItem(Items.PHANTOM_MEMBRANE,false);

                    if (invSlot == -1 && hbSlot == -1) {
                        bojkaThrow = false;
                        AbstractCommand.addMessage("Божья аура не найдена!");
                        Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.PHANTOM_MEMBRANE)) {
                        int old = mc.player.inventory.currentItem;

                        int slot = findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }
                        if (InventoryUtility.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                        AbstractCommand.addMessage("Божья аура использована!");
                    } else  AbstractCommand.addMessage("Божья аура в КД!");
                    bojkaThrow = false;
                }

                if(snejoktrow) {

                    int hbSlot =  getItem(Items.SNOWBALL,true);
                    int invSlot = getItem(Items.SNOWBALL,false);

                    if (invSlot == -1 && hbSlot == -1) {
                        snejoktrow = false;
                        AbstractCommand.addMessage("Снежок заморозка не найден!");
                        Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SNOWBALL)) {
                        int old = mc.player.inventory.currentItem;

                        int slot = findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }
                        if (InventoryUtility.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                        AbstractCommand.addMessage("Снежок заморозка использована!");
                    } else  AbstractCommand.addMessage("Снежок заморозка в КД!");
                    snejoktrow = false;
                }

                if (trapThrow) {
                    int hbSlot =  getItem(Items.NETHERITE_SCRAP,true);
                    int invSlot = getItem(Items.NETHERITE_SCRAP,false);

                    if (invSlot == -1 && hbSlot == -1) {
                        trapThrow = false;
                        AbstractCommand.addMessage("Трапка не найдена!");
                        Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.NETHERITE_SCRAP)) {
                        int old = mc.player.inventory.currentItem;

                        int slot = findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }
                        if (InventoryUtility.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                        AbstractCommand.addMessage("Трапка использована!");
                    } else  AbstractCommand.addMessage("Трапка в КД!");
                    trapThrow = false;
                }




                if (plastThrow) {
                    int hbSlot =  getItem(Items.DRIED_KELP,true);
                    int invSlot = getItem(Items.DRIED_KELP,false);

                    if (invSlot == -1 && hbSlot == -1) {
                        plastThrow = false;
                        AbstractCommand.addMessage("Пласт не найден!");
                        Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.DRIED_KELP)) {
                        int old = mc.player.inventory.currentItem;

                        int slot = findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }
                        if (InventoryUtility.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                        AbstractCommand.addMessage("Пласт использован!");
                    } else  AbstractCommand.addMessage("Пласт в КД!");
                    plastThrow = false;
                }

                if (blatantThrow) {

                    int hbSlot =  getItem(Items.SUGAR,true);
                    int invSlot = getItem(Items.SUGAR,false);

                    if (invSlot == -1 && hbSlot == -1) {
                        blatantThrow = false;
                        AbstractCommand.addMessage("Явная пыль не найдена!");
                        Essence.getHandler().notificationManager.pushNotify("Нет предмета для использования!", NotificationManager.Type.Info);
                        return;
                    }

                    if (!mc.player.getCooldownTracker().hasCooldown(Items.SUGAR)) {
                        int old = mc.player.inventory.currentItem;

                        int slot = findAndTrowItem(hbSlot, invSlot);
                        if (slot > 8) {
                            mc.playerController.pickItem(slot);
                        }
                        if (InventoryUtility.findEmptySlot(true) != -1 && mc.player.inventory.currentItem != old) {
                            mc.player.inventory.currentItem = old;
                        }
                        AbstractCommand.addMessage("Явная пыль использована!");
                    } else AbstractCommand.addMessage("Явная пыль в КД!");
                    blatantThrow = false;
                }
                this.handUtil.handleItemChange(System.currentTimeMillis() - this.delay > 200L);
            }

            if (event instanceof EventPacket) {
                EventPacket e = (EventPacket) event;
                this.handUtil.onEventPacket(e);

                if (options.get("Улучшать команды").get() && e.getPacket() instanceof net.minecraft.network.play.client.CChatMessagePacket) {
                    net.minecraft.network.play.client.CChatMessagePacket packet = (net.minecraft.network.play.client.CChatMessagePacket) e.getPacket();
                    String message = packet.getMessage();

                    if (message.toLowerCase().equals("/clan create")) {
                        try {
                            String randomName = generateRandomClanName();
                            Field messageField = packet.getClass().getDeclaredField("message");
                            messageField.setAccessible(true);
                            messageField.set(packet, "/clan create " + randomName);
                            AbstractCommand.addMessage("Создание клана с случайным названием: " + randomName);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    else if (message.toLowerCase().equals("/clan invest full") ||
                            message.toLowerCase().equals("/clan invest all") ||
                            message.toLowerCase().equals("/clan invest max")) {
                        try {
                            Field messageField = packet.getClass().getDeclaredField("message");
                            messageField.setAccessible(true);
                            messageField.set(packet, "/bal");

                            clanInvestAction = "all";
                            waitingForPlayerBalance = true;

                            e.setCancel(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    else if (message.toLowerCase().equals("/clan withdraw full") ||
                            message.toLowerCase().equals("/clan withdraw all") ||
                            message.toLowerCase().equals("/clan withdraw max")) {
                        try {
                            Field messageField = packet.getClass().getDeclaredField("message");
                            messageField.setAccessible(true);
                            messageField.set(packet, "/clan money");

                            clanWithdrawAction = "all";
                            waitingForClanBalance = true;

                            e.setCancel(false);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }

                    if (message.toLowerCase().startsWith("/pay ")) {
                        String[] parts = message.split(" ");
                        if (parts.length == 3) {
                            String lastPart = parts[2].toLowerCase();
                            if (lastPart.equals("full") || lastPart.equals("all") || lastPart.equals("max")) {
                                String playerName = parts[1];


                                try {
                                    Field messageField = packet.getClass().getDeclaredField("message");
                                    messageField.setAccessible(true);
                                    messageField.set(packet, "/bal");

                                    targetPlayerForPay = playerName;
                                } catch (Exception ex) {

                                    ex.printStackTrace();
                                }
                            }
                        }
                    }

                    if (message.toLowerCase().equals("/ah me") || message.toLowerCase().startsWith("/ah me ") ||
                            message.toLowerCase().equals("/ah my") || message.toLowerCase().startsWith("/ah my ")) {
                        try {
                            String realPlayerName = mc.getSession().getUsername();
                            String newCommand = "/ah " + realPlayerName;

                            if (message.toLowerCase().startsWith("/ah me ") || message.toLowerCase().startsWith("/ah my ")) {
                                String extraParams = message.substring(message.toLowerCase().startsWith("/ah me ") ? "/ah me ".length() : "/ah my ".length());
                                newCommand = "/ah " + realPlayerName + " " + extraParams;
                            }

                            Field messageField = packet.getClass().getDeclaredField("message");
                            messageField.setAccessible(true);
                            messageField.set(packet, newCommand);


                        } catch (Exception ex) {

                            ex.printStackTrace();
                        }
                    }
                }

                if (e.getPacket() instanceof SJoinGamePacket) {
                    startEventDelayTask();
                    lastStorageClick = -1;
                    waitingForStorage = true;
                    return;
                }
                if (e.getPacket() instanceof SChatPacket) {
                    SChatPacket packet = (SChatPacket) e.getPacket();
                    ITextComponent originalComponent = packet.getChatComponent();
                    String message = originalComponent.getString();


                    if (targetPlayerForPay != null) {
                        boolean containsBalance = message.toLowerCase().contains("баланс");
                        boolean containsBalanceEng = message.toLowerCase().contains("balance");
                        boolean containsMoney = message.toLowerCase().contains("денег") || message.toLowerCase().contains("money");
                        boolean containsCoins = message.toLowerCase().contains("монет") || message.toLowerCase().contains("coins");

                        if (containsBalance || containsBalanceEng || containsMoney || containsCoins) {
                            Pattern pattern = Pattern.compile("\\d+[,\\s]?\\d*[,\\s]?\\d*");
                            Matcher matcher = pattern.matcher(message);

                            boolean foundNumber = false;
                            while (matcher.find()) {
                                String foundAmount = matcher.group();
                                foundAmount = foundAmount.replaceAll("[,\\s]", "");

                                try {
                                    int balance = Integer.parseInt(foundAmount);
                                    foundNumber = true;

                                    final int finalBalance = balance;
                                    scheduler.schedule(() -> {
                                        try {
                                            String command = "/pay " + targetPlayerForPay + " " + finalBalance;

                                            mc.player.sendChatMessage(command);
                                        } catch (Exception ex) {

                                        } finally {
                                            targetPlayerForPay = null;
                                        }
                                    }, 50, TimeUnit.MILLISECONDS);

                                    break;
                                } catch (NumberFormatException ex) {

                                }
                            }
                        }
                    }

                    if (waitingForPlayerBalance && clanInvestAction != null) {
                        boolean containsBalance = message.toLowerCase().contains("баланс");
                        boolean containsBalanceEng = message.toLowerCase().contains("balance");
                        boolean containsMoney = message.toLowerCase().contains("денег") || message.toLowerCase().contains("money");
                        boolean containsCoins = message.toLowerCase().contains("монет") || message.toLowerCase().contains("coins");

                        if (containsBalance || containsBalanceEng || containsMoney || containsCoins) {
                            Pattern pattern = Pattern.compile("\\d+[,\\s]?\\d*[,\\s]?\\d*");
                            Matcher matcher = pattern.matcher(message);

                            while (matcher.find()) {
                                String foundAmount = matcher.group();
                                foundAmount = foundAmount.replaceAll("[,\\s]", "");

                                try {
                                    playerBalance = Integer.parseInt(foundAmount);

                                    scheduler.schedule(() -> {
                                        try {
                                            if (playerBalance > 0) {
                                                String command = "/clan invest " + playerBalance;
                                                mc.player.sendChatMessage(command);
                                                AbstractCommand.addMessage("Инвестирование в клан: " + playerBalance);
                                            } else {
                                                AbstractCommand.addMessage("У вас нет денег для инвестирования в клан");
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        } finally {
                                            waitingForPlayerBalance = false;
                                            clanInvestAction = null;
                                        }
                                    }, 50, TimeUnit.MILLISECONDS);

                                    break;
                                } catch (NumberFormatException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                    if (waitingForClanBalance && clanWithdrawAction != null) {
                        if (message.contains("[⚔] Баланс клана:")) {
                            try {
                                // Ищем число с запятыми
                                Pattern pattern = Pattern.compile("\\d+(?:,\\d+)*");
                                Matcher matcher = pattern.matcher(message);

                                if (matcher.find()) {
                                    // Удаляем все запятые из числа перед парсингом
                                    String balanceStr = matcher.group().replace(",", "");
                                    clanBalance = Integer.parseInt(balanceStr);

                                    scheduler.schedule(() -> {
                                        try {
                                            if (clanBalance > 0) {
                                                String command = "/clan withdraw " + clanBalance;
                                                mc.player.sendChatMessage(command);
                                                AbstractCommand.addMessage("Снятие средств из клана: " + clanBalance);
                                            } else {
                                                AbstractCommand.addMessage("В клане нет средств для снятия");
                                            }
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        } finally {
                                            waitingForClanBalance = false;
                                            clanWithdrawAction = null;
                                        }
                                    }, 50, TimeUnit.MILLISECONDS);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }

                    if (options.get("Авто GPS").get()) {
                        processCoordinates(message);
                    }

                    if (options.get("Конвертировать время").get() || options.get("Раскрывать баны").get()) {
                        message = processBanMessage(message, originalComponent, packet);
                        String newMessage = convertTimeInMessage(message);

                        if (!message.equals(newMessage)) {
                            try {
                                StringTextComponent rootComponent = new StringTextComponent("");
                                List<ITextComponent> siblings = originalComponent.getSiblings();

                                if (!siblings.isEmpty()) {
                                    for (ITextComponent sibling : siblings) {
                                        String siblingText = sibling.getString();
                                        if (siblingText.matches(".*\\d+.*")) {
                                            String newText = convertTimeInMessage(siblingText);
                                            StringTextComponent newSibling = new StringTextComponent(newText);
                                            newSibling.setStyle(sibling.getStyle());
                                            rootComponent.append(newSibling);
                                        } else {
                                            StringTextComponent unchangedSibling = new StringTextComponent(siblingText);
                                            unchangedSibling.setStyle(sibling.getStyle());
                                            rootComponent.append(unchangedSibling);
                                        }
                                    }

                                    java.lang.reflect.Field field = SChatPacket.class.getDeclaredField("chatComponent");
                                    field.setAccessible(true);
                                    field.set(packet, rootComponent);
                                }
                            } catch (Exception ex) {

                            }
                        }
                    }
                }
            }

            if (mc.currentScreen instanceof ContainerScreen && options.get("Перевыставление предметов").get()) {
                ContainerScreen<?> screen = (ContainerScreen<?>) mc.currentScreen;
                String title = screen.getTitle().getString().toLowerCase();

                if (!title.contains("хранилище")) {
                    return;
                }

                long currentTime = System.currentTimeMillis();
                if (lastStorageClick == -1 || currentTime - lastStorageClick >= STORAGE_CLICK_DELAY) {
                    if (screen.getContainer() != null && screen.getContainer().inventorySlots.size() > 52) {
                        try {
                            Thread.sleep(70);
                            mc.playerController.windowClick(screen.getContainer().windowId, 52, 0, ClickType.QUICK_MOVE, mc.player);
                            lastStorageClick = currentTime;
                            waitingForStorage = false;
                        } catch (InterruptedException e) {
                        }
                    }
                }
            }
            if (event instanceof EventKey) {
                EventKey k = (EventKey) event;

                if (k.key == ah.get() && k.key != 0) {
                    try {
                        ItemStack itemToSearch = null;

                        if (mc.currentScreen instanceof ContainerScreen) {
                            ContainerScreen<?> screen = (ContainerScreen<?>) mc.currentScreen;
                            if (screen.getContainer() != null) {
                                for (Slot slot : screen.getContainer().inventorySlots) {
                                    if (slot.xPos < mc.mouseHelper.getMouseX() && slot.xPos + 16 > mc.mouseHelper.getMouseX() &&
                                            slot.yPos < mc.mouseHelper.getMouseY() && slot.yPos + 16 > mc.mouseHelper.getMouseY()) {
                                        if (slot.getStack() != null && !slot.getStack().isEmpty()) {
                                            itemToSearch = slot.getStack();
                                            break;
                                        }
                                    }
                                }
                            }
                        }

                        if (itemToSearch == null || itemToSearch.isEmpty()) {
                            itemToSearch = mc.player.getHeldItemMainhand();
                        }

                        if (itemToSearch != null && !itemToSearch.isEmpty()) {
                            String itemName = getItemName(itemToSearch);
                            if (itemName != null && !itemName.isEmpty()) {
                                itemName = itemName.replaceAll("\\[★\\]", "")
                                        .replaceAll("xxx", "")
                                        .replaceAll("123", "")
                                        .trim()
                                        .replaceAll("\\s+", " ");

                                if (itemName.endsWith(" I")) {
                                    itemName = itemName.substring(0, itemName.length() - 2).trim();
                                } else if (itemName.endsWith(" II")) {
                                    itemName = itemName.substring(0, itemName.length() - 3).trim();
                                }

                                boolean hasMaxAtEnd = itemName.endsWith("MAX");
                                String level = "";

                                if (hasMaxAtEnd) {
                                    itemName = itemName.substring(0, itemName.length() - 3).trim();
                                    level = "3";
                                }

                                if (itemName.contains("Сфера Пандоры")) {
                                    itemName = "Сфера Пандора";
                                }

                                if (!hasMaxAtEnd && itemToSearch.hasTag() && itemToSearch.getTag().contains("display")) {
                                    CompoundNBT display = itemToSearch.getTag().getCompound("display");
                                    if (display.contains("Lore", 9)) {
                                        ListNBT lore = display.getList("Lore", 8);
                                        for (int i = 0; i < lore.size(); i++) {
                                            String loreLine = lore.getString(i);
                                            if (loreLine.contains("Уровень")) {
                                                if (loreLine.contains("MAX")) {
                                                    level = "3";
                                                    break;
                                                }

                                                if (loreLine.contains("I/")) {
                                                    level = "1";
                                                } else if (loreLine.contains("II/")) {
                                                    level = "2";
                                                } else if (loreLine.contains("III/")) {
                                                    level = "3";
                                                } else {
                                                    if (loreLine.contains("1/")) {
                                                        level = "1";
                                                    } else if (loreLine.contains("2/")) {
                                                        level = "2";
                                                    } else if (loreLine.contains("3/")) {
                                                        level = "3";
                                                    }
                                                }
                                                break;
                                            }
                                        }
                                    }
                                }

                                String searchCommand = "/ah search " + itemName;
                                if (!level.isEmpty()) {
                                    searchCommand += " " + level;
                                }

                                mc.player.sendChatMessage(searchCommand);
                                AbstractCommand.addMessage("Поиск предмета: " + itemName + (level.isEmpty() ? "" : " " + level));
                            } else {
                                AbstractCommand.addMessage("Не удалось получить название предмета");
                            }
                        } else {
                            AbstractCommand.addMessage("Возьмите предмет в руку");
                        }
                    } catch (Exception e) {
                        AbstractCommand.addMessage("Ошибка при поиске предмета: " + e.getMessage());
                        e.printStackTrace();
                    }
                    return;
                }

                if (k.key == dezor.get()) {
                    if (!dezori.get() && isPlayerNearby(10)) {
                        InventoryUtility.swapAndUse(Items.ENDER_EYE);
                    } else {
                        AbstractCommand.addMessage("Рядом нет игроков в радиусе 10 блоков!");
                    }
                } else if (k.key == trap.get()) {
                    if (!trapi.get() && isPlayerNearby(2)) {
                        InventoryUtility.swapAndUse(Items.NETHERITE_SCRAP);
                    } else {
                        AbstractCommand.addMessage("Рядом нет игроков в радиусе 2 блоков!");
                    }
                } else if (k.key == plast.get()) {
                    InventoryUtility.swapAndUse(Items.DRIED_KELP);
                } else if (k.key == yavka.get()) {
                    if(!yavkai.get() && isPlayerNearby(10))
                        InventoryUtility.swapAndUse(Items.SUGAR);
                    else AbstractCommand.addMessage("Рядом нет игроков в радиусе 10 блоков!");
                } else if (k.key == bojka.get()) {
                    InventoryUtility.swapAndUse(Items.PHANTOM_MEMBRANE);
                } else if (k.key == snejok.get()) {
                    InventoryUtility.swapAndUse(Items.SNOWBALL);

                } else if (k.key == serka.get()) {
                    if (InventoryUtility.inventorySwapClick(Items.SPLASH_POTION, "don-item", "potion-acid", false)) {
                        AbstractCommand.addMessage("Серная кислота использована!");
                    } else {
                        AbstractCommand.addMessage("Серная кислота не найдена!");
                    }
                } else if (k.key == otrigka.get()) {
                    if (InventoryUtility.inventorySwapClick(Items.SPLASH_POTION, "don-item", "potion-burp", false)) {
                        AbstractCommand.addMessage("Отрыжка использована!");
                    } else {
                        AbstractCommand.addMessage("Отрыжка не найдена!");
                    }
                } else if (k.key == killera.get()) {
                    if (InventoryUtility.inventorySwapClick(Items.SPLASH_POTION, "don-item", "potion-killer", false)) {
                        AbstractCommand.addMessage("Зелье киллера использовано!");
                    } else {
                        AbstractCommand.addMessage("Зелье киллера не найдено!");
                    }
                } else if (k.key == medik.get()) {
                    if (InventoryUtility.inventorySwapClick(Items.SPLASH_POTION, "don-item", "potion-medic", false)) {
                        AbstractCommand.addMessage("Зелье медика использовано!");
                    } else {
                        AbstractCommand.addMessage("Зелье медика не найдено!");
                    }
                } else if (k.key == pobedka.get()) {
                    if (InventoryUtility.inventorySwapClick(Items.SPLASH_POTION, "don-item", "potion-winner", false)) {
                        AbstractCommand.addMessage("Победилка использована!");
                    } else {
                        AbstractCommand.addMessage("Победилка не найдена!");
                    }
                } else if (k.key == agenta.get()) {
                    if (InventoryUtility.inventorySwapClick(Items.SPLASH_POTION, "don-item", "potion-agent", false)) {
                        AbstractCommand.addMessage("Зелье агента использовано!");
                    } else {
                        AbstractCommand.addMessage("Зелье агента не найдено!");
                    }
                }
            }
        } finally {
            isProcessingEvent = false;
        }

    }


    private int findAndTrowItem(int hbSlot, int invSlot) {
        if (hbSlot != -1) {
            this.handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.player.connection.sendPacket(new CHeldItemChangePacket(hbSlot));
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return hbSlot;
        }
        if (invSlot != -1) {
            handUtil.setOriginalSlot(mc.player.inventory.currentItem);
            mc.playerController.pickItem(invSlot);
            mc.player.connection.sendPacket(new CPlayerTryUseItemPacket(Hand.MAIN_HAND));
            mc.player.swingArm(Hand.MAIN_HAND);
            this.delay = System.currentTimeMillis();
            return invSlot;
        }
        return -1;
    }

    private int getItem(Item input, boolean inHotBar) {
        int firstSlot = inHotBar ? 0 : 9;
        int lastSlot = inHotBar ? 9 : 36;
        for (int i = firstSlot; i < lastSlot; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);

            if (itemStack.getItem() instanceof AirItem) {
                continue;
            }
            if (itemStack.getItem() == input) {
                return i;
            }
        }
        return -1;

    }
}
