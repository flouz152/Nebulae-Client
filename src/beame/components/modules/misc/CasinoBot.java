/*
package beame.components.modules.misc;

import beame.Nebulae;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.server.SChatPacket;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.Random;

public class CasinoBot extends Module {
    public CasinoBot() { super("Casino bot", Category.Misc); addSettings(backMoney, onlyLose, losePercent, cost); }

    int gameCount = 0;

    private final BooleanSetting backMoney = new BooleanSetting("Return $ on incorrect", true, 0);
    private final BooleanSetting onlyLose = new BooleanSetting("Only lose", false, 0);
    private final SliderSetting losePercent = new SliderSetting("Lose %", 70, 50, 90, 10).setVisible(() -> !onlyLose.get());
    private final SliderSetting cost = new SliderSetting("Bet count", 5000, 5000, 15000, 1000);

    public boolean bet() {
        int botBet = new Random().nextInt(1, 100);
        return onlyLose.get() ? false : losePercent.get().intValue() < botBet;
    }

    private TimerUtil sayUtil = new TimerUtil();

    private TimerUtil reciveTimer = new TimerUtil();
    private TimerUtil sendTimer = new TimerUtil();

    @Override
    public void event(Event event) {
        if (event instanceof EventPacket) {
            EventPacket eventPacket = (EventPacket) event;

            if(eventPacket.getPacket() instanceof SChatPacket){
                SChatPacket packet = (SChatPacket) eventPacket.getPacket();
                String chatMessage = packet.getChatComponent().getString();

                processMoneySend(chatMessage);
            }
        }

        if(event instanceof EventRender) {
            if(sayUtil.hasTimeElapsed(20000)) {
                sendMessage();
                sayUtil.reset();
            }
        }
    }
    public void processMoneySend(String message) {
        if(message.startsWith("[$]") && message.contains("???????? ?? ??????")){
            String senderName = message.split("?????? ")[1];

            int needMoney = cost.get().intValue();
            String recivedMoneyStr = message.replace("[$] ", "").replace("$", "").split(" ")[0].replace(",", "");
            int recivedMoney = Integer.parseInt(recivedMoneyStr);
            if(recivedMoney != needMoney){
                mc.player.sendChatMessage("/pm " + senderName + " ?????? ?? ?????? ?????! ??????? ????? ??? ??????: " + (cost.get().toString()).replace(".0", "") + "$");

                if(backMoney.get()) {
                    mc.player.sendChatMessage("/pay " + senderName + " " + recivedMoneyStr);
                    if(sendTimer.hasTimeElapsed(300)) {
                        mc.player.sendChatMessage("/pay " + senderName + " " + recivedMoneyStr);
                        sendTimer.reset();
                    }
                }
                else
                    if(Nebulae.getHandler().telegram != null)
                        Nebulae.getHandler().telegram.sendMessage("?? ??????-???\n???????? " + recivedMoney + "$ \n???????: ???????????? ????? ??????");

                return;
            }

            if(reciveTimer.hasTimeElapsed(300)){
                boolean isWin = bet();
                gameCount += 1;
                if(isWin){
                    String winMoney = "" + (cost.get().intValue()*2);
                    mc.player.sendChatMessage("/pay " + senderName + " " + winMoney.replace(".0", ""));
                    if(sendTimer.hasTimeElapsed(300)) {
                        mc.player.sendChatMessage("/pay " + senderName + " " + winMoney.replace(".0", ""));
                        sendTimer.reset();
                    }
                    mc.player.sendChatMessage("/pm " + senderName + " ?? ????????!");
                    if(Nebulae.getHandler().telegram != null)
                        Nebulae.getHandler().telegram.sendMessage("?? ??????-???\n?????????? " + winMoney + "$ \n???????: ?? ?????????");
                }
                else{
                    mc.player.sendChatMessage("/pm " + senderName + " ???, ?? ?? ?????????! ?????? ? ????????? ???...");
                    if(Nebulae.getHandler().telegram != null)
                        Nebulae.getHandler().telegram.sendMessage("?? ??????-???\n???????? " + needMoney + "$ \n???????: ?? ????????");
                }
                reciveTimer.reset();
            }
        }
    }

    public void sendMessage() {
        mc.player.sendChatMessage("!??????! ? ????? ?????? ???. ??????? ??? " + (cost.get().toString()).replace(".0", "") + "$, ???? ?? ?????? 50%. ??? ?????? ?? ???????? ? 2 ???? ??????! ?????????? ???: " + gameCount);
    }
}
*/
// leaked by itskekoff; discord.gg/sk3d VRRT4x3D
