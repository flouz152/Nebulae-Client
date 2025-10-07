package beame.components.modules.player;

import beame.components.command.AbstractCommand;
import events.Event;
import events.impl.player.EventUpdate;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.server.SChatPacket;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import beame.setting.SettingList.InputFieldSetting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClanInvest extends Module {
// leaked by itskekoff; discord.gg/sk3d INmApp6h
    private final InputFieldSetting investAmount = new InputFieldSetting("Сумма депа", "100", "Количество денег для инвестирования");
    private final InputFieldSetting minBalanceToInvest = new InputFieldSetting("Необходимый баланс", "1000", "Минимальный баланс для инвестирования");
    
    private int lastInvestTime = 0;
    private int checkTimer = 0;
    private int playerBalance = 0;
    private boolean waitingForBalanceResponse = false;
    private long lastBalanceRequestTime = 0;

    public ClanInvest() {
        super("ClanInvest", Category.Misc, true, "Автоматическое инвестирование в клан");
        addSettings(investAmount, minBalanceToInvest);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        lastInvestTime = 0;
        checkTimer = 0;
        playerBalance = 0;
        AbstractCommand.addMessage("ClanInvest включен. Сумма: " + investAmount.get());
    }

    private List<String> getSidebarText() {
        List<String> lines = new ArrayList<>();

        if (mc.world == null) return lines;

        Scoreboard scoreboard = mc.world.getScoreboard();
        if (scoreboard == null) return lines;

        ScoreObjective objective = scoreboard.getObjectiveInDisplaySlot(1);
        if (objective == null) return lines;

        Collection<Score> scores = scoreboard.getSortedScores(objective);

        for (Score score : scores) {
            String playerName = score.getPlayerName();
            if (playerName != null && !playerName.startsWith("#")) {
                String scoreText = getScoreDisplayForString(playerName);
                if (!scoreText.isEmpty()) {
                    lines.add(scoreText);
                }
            }
        }

        return lines;
    }

    private String getScoreDisplayForString(String name) {
        return name.replaceAll("§[0-9a-fk-or]", "");
    }

    public void onChatMessage(String message) {
        if (!waitingForBalanceResponse) return;

        String cleanMessage = stripColorCodes(message);

        if (cleanMessage.contains("баланс") || cleanMessage.contains("balance") || cleanMessage.contains("$")) {
            try {
                Pattern currencyPattern = Pattern.compile("\\$\\s*([\\d,\\s]+(?:\\.\\d+)?)");
                Matcher currencyMatcher = currencyPattern.matcher(cleanMessage);
                
                if (currencyMatcher.find()) {
                    String fullValue = currencyMatcher.group(1);
                    String cleanValue = fullValue.replaceAll("[,\\s]", "");
                    
                    if (cleanValue.contains(".")) {
                        String integerPart = cleanValue.split("\\.")[0];
                        playerBalance = Integer.parseInt(integerPart);
                    } else {
                        playerBalance = Integer.parseInt(cleanValue);
                    }
                    
                    waitingForBalanceResponse = false;
                    return;
                } else if (cleanMessage.matches(".*\\d+.*")) {
                    String digitsOnly = cleanMessage.replaceAll("[^0-9]", "");
                    playerBalance = Integer.parseInt(digitsOnly);
                    waitingForBalanceResponse = false;
                    return;
                }
            } catch (Exception e) {
                AbstractCommand.addMessage("Ошибка при обработке баланса: " + e.getMessage());
            }
            
            waitingForBalanceResponse = false;
        }
    }
    
    private String stripColorCodes(String text) {
        if (text == null) return "";
        return text.replaceAll("§[0-9a-fk-or]", "");
    }

    private void sendInvestCommand() {
        if (mc.player == null) return;

        String amountStr = investAmount.get();
        int amount;

        try {
            int minBalance = 0;
            try {
                minBalance = Integer.parseInt(minBalanceToInvest.get());
            } catch (NumberFormatException e) {
            }

            if (playerBalance < minBalance) {
                return;
            }

            amount = Integer.parseInt(amountStr);

            mc.player.sendChatMessage("/clan invest " + amount);
            AbstractCommand.addMessage("Инвестировано " + amount + " монет в клан");
            lastInvestTime = (int) (System.currentTimeMillis() / 1000);
        } catch (NumberFormatException e) {
        }
    }

    private void requestPlayerBalance() {
        if (mc.player == null) return;
        
        if (waitingForBalanceResponse) {
            if (System.currentTimeMillis() - lastBalanceRequestTime > 10000) {
                waitingForBalanceResponse = false;
            } else {
                return;
            }
        }
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBalanceRequestTime < 2000) {
            return;
        }
        
        mc.player.sendChatMessage("/bal");
        waitingForBalanceResponse = true;
        lastBalanceRequestTime = currentTime;
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            int currentTime = (int) (System.currentTimeMillis() / 1000);
            
            if (currentTime % 5 == 0 && checkTimer != currentTime) {
                checkTimer = currentTime;
                
                if (!waitingForBalanceResponse && currentTime - lastInvestTime >= 5) {
                    requestPlayerBalance();
                }
            }
        } else if (event instanceof EventPacket) {
            EventPacket e = (EventPacket) event;
            
            if (!e.isSendPacket() && e.getPacket() instanceof SChatPacket && waitingForBalanceResponse) {
                SChatPacket packet = (SChatPacket) e.getPacket();
                String message = packet.getChatComponent().getString();
                onChatMessage(message);
                
                if (!waitingForBalanceResponse) {
                    int currentTime = (int) (System.currentTimeMillis() / 1000);
                    if (currentTime - lastInvestTime >= 5) {
                        sendInvestCommand();
                    }
                }
            }
        }
    }
}