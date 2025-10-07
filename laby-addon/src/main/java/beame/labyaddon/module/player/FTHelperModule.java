package beame.labyaddon.module.player;

import beame.labyaddon.config.NebulaeAddonConfig;
import beame.labyaddon.core.AddonModule;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Standalone recreation of the FTHelper module.
 * <p>
 * The original implementation is tightly coupled to the Essence client.  For
 * the Laby add-on we replicate the core quality-of-life features that are used
 * on play.funtime.su: automatic event reminders, coordinate helpers and chat
 * message formatting improvements.
 */
public class FTHelperModule extends AddonModule {

    private final Map<String, Boolean> options = new LinkedHashMap<>();

    private double eventDelayMinutes = 1.0D;
    private ScheduledExecutorService scheduler;
    private ScheduledFuture<?> eventDelayTask;

    private static final Pattern COORDINATE_PATTERN = Pattern.compile("(-?\\d+)\\s+(-?\\d+)\\s+(-?\\d+)");

    public FTHelperModule() {
        super("FTHelper");

        options.put("Авто GPS", true);
        options.put("Конвертировать время", true);
        options.put("Перевыставление предметов", false);
        options.put("Раскрывать баны", true);
        options.put("Авто /event delay", true);
        options.put("Улучшать команды", false);
    }

    @Override
    protected void onEnable() {
        if (scheduler == null || scheduler.isShutdown()) {
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread thread = new Thread(r, "Nebulae-FTHelper");
                thread.setDaemon(true);
                return thread;
            });
        }
        restartEventDelayTask();
    }

    @Override
    protected void onDisable() {
        cancelEventDelayTask();
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
    }

    @Override
    public void onTick() {
        if (!isEnabled()) {
            return;
        }

        if (options.getOrDefault("Авто /event delay", true)) {
            if (eventDelayTask == null || eventDelayTask.isCancelled()) {
                restartEventDelayTask();
            }
        } else if (eventDelayTask != null) {
            cancelEventDelayTask();
        }
    }

    @Override
    public void onChatMessage(ITextComponent message) {
        String text = message.getString();

        if (options.getOrDefault("Конвертировать время", true) && text.contains("сек")) {
            String converted = convertSeconds(text);
            if (!converted.equals(text)) {
                printClientMessage(new StringTextComponent("§e[FT] §7" + converted));
            }
        }

        if (options.getOrDefault("Раскрывать баны", true) && text.contains("забанен") && text.contains("Подробнее")) {
            revealBanReason(text);
        }

        if (options.getOrDefault("Авто GPS", true) && text.contains("Координаты")) {
            sendGpsFromMessage(text);
        }
    }

    private void restartEventDelayTask() {
        cancelEventDelayTask();

        if (!options.getOrDefault("Авто /event delay", true)) {
            return;
        }

        if (mc.player == null) {
            return;
        }

        if (scheduler != null && !scheduler.isShutdown()) {
            long delayMinutes = Math.max(1L, Math.round(eventDelayMinutes));
            eventDelayTask = scheduler.scheduleAtFixedRate(() -> {
                if (mc.player != null && mc.world != null) {
                    mc.player.sendChatMessage("/event delay");
                }
            }, delayMinutes, delayMinutes, TimeUnit.MINUTES);
        }
    }

    private void cancelEventDelayTask() {
        if (eventDelayTask != null) {
            eventDelayTask.cancel(false);
            eventDelayTask = null;
        }
    }

    private void sendGpsFromMessage(String message) {
        Matcher matcher = COORDINATE_PATTERN.matcher(message);
        if (matcher.find() && mc.player != null) {
            String x = matcher.group(1);
            String z = matcher.group(3);
            mc.player.sendChatMessage(".gps " + x + " " + z);
        }
    }

    private void revealBanReason(String message) {
        int reasonIndex = message.indexOf("Причина:");
        if (reasonIndex == -1) {
            return;
        }

        String reason = message.substring(reasonIndex).replace("Причина:", "").trim();
        if (!reason.isEmpty()) {
            printClientMessage(new StringTextComponent("§c[Ban] §7" + reason));
        }
    }

    private String convertSeconds(String message) {
        String[] parts = message.split(" ");
        boolean changed = false;

        for (int i = 0; i < parts.length; i++) {
            if (parts[i].matches("\\d+") && i + 1 < parts.length && parts[i + 1].contains("сек")) {
                int seconds = Integer.parseInt(parts[i]);
                int minutes = seconds / 60;
                int rest = seconds % 60;
                if (minutes > 0) {
                    String replacement = minutes + " мин" + (rest > 0 ? " " + rest + " сек" : "");
                    parts[i] = replacement;
                    parts[i + 1] = "";
                    changed = true;
                }
            }
        }

        if (!changed) {
            return message;
        }

        StringBuilder builder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                if (builder.length() > 0) {
                    builder.append(' ');
                }
                builder.append(part);
            }
        }
        return builder.toString();
    }

    private void printClientMessage(ITextComponent component) {
        if (mc.ingameGUI == null) {
            return;
        }
        NewChatGui chat = mc.ingameGUI.getChatGUI();
        if (chat != null) {
            chat.printChatMessageWithOptionalDeletion(component, component.getString().hashCode());
        }
    }

    public boolean getOption(String name, boolean defaultValue) {
        return options.getOrDefault(name, defaultValue);
    }

    public void setOption(String name, boolean value) {
        options.put(name, value);
        if ("Авто /event delay".equals(name)) {
            restartEventDelayTask();
        }
    }

    public float getEventDelayMinutes() {
        return (float) eventDelayMinutes;
    }

    public void setEventDelayMinutes(float value) {
        this.eventDelayMinutes = Math.max(1.0F, Math.min(10.0F, value));
        restartEventDelayTask();
    }

    public void applyConfig(NebulaeAddonConfig config) {
        setEnabled(config.ftHelperEnabled.get());
        setEventDelayMinutes(config.eventDelayInterval.get().floatValue());
        setOption("Авто GPS", config.autoGps.get());
        setOption("Конвертировать время", config.convertTime.get());
        setOption("Авто /event delay", config.autoEventDelay.get());
    }

    public void exportConfig(NebulaeAddonConfig config) {
        config.ftHelperEnabled.set(isEnabled());
        config.eventDelayInterval.set((double) getEventDelayMinutes());
        config.autoGps.set(getOption("Авто GPS", true));
        config.convertTime.set(getOption("Конвертировать время", true));
        config.autoEventDelay.set(getOption("Авто /event delay", true));
    }
}
