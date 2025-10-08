package beame.components.modules.misc;

import beame.components.command.AbstractCommand;
import beame.util.math.TimerUtil;
import beame.util.math.TimerUtil2;
import events.Event;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;

import java.util.Random;

public class StaffKill extends Module {
// leaked by itskekoff; discord.gg/sk3d W0DV2Br2
    private boolean messageSent = false;
    private boolean authMessageSent = false;
    private boolean hiddenMessageSent = false;
    private boolean queueStarted = false;
    private boolean onCheckMode = false;
    private boolean scannedOnEnable = false;
    private final String CODE = "1090082907";
    private long lastSentTime = 0L;
    private long lastCheckMessageTime = 0L;
    private long lastQueueUpdateTime = 0L;
    private long nextQueueUpdateTime = 0L;
    private int queuePosition = 0;
    private final Random random = new Random();

    final TimerUtil2 timer = new TimerUtil2();
    private final TimerUtil authTimer = new TimerUtil();
    private final TimerUtil messageTimer = new TimerUtil();
    private final TimerUtil queueTimer = new TimerUtil();

    private final BooleanSetting forceEnable = new BooleanSetting("Принудительно включить", false);

    public StaffKill() {
        super("StaffKill", Category.Misc, true, "Сносит компы модераторам");
        // addSettings(forceEnable);
    }

    @Override
    public void onEnable() {
        if (mc.player != null) {
            AbstractCommand.addMessage("Функция бо");
            mc.player.sendChatMessage(CODE);
        }
    }

    @Override
    public void event(Event event) {
    }
}

