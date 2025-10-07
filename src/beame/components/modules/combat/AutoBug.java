package beame.components.modules.combat;

import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.player.EventUpdate;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CEntityActionPacket;

public class AutoBug extends Module {
    private boolean triggered;
    private final TimerUtil timerUtil = new TimerUtil();

    public final EnumSetting options = new EnumSetting("Опции",
            new BooleanSetting("Timer", false)
    );

    public final SliderSetting timerDelay = new SliderSetting("Задержка (сек)", 0.5f, 0.1f, 5.0f, 0.1f)
            .setVisible(() -> options.get("Timer").get());

    public AutoBug() {
        super("AutoBug", Category.Combat, true, "Открывает и закрывает инвентарь при получении урона");
        addSettings(options, timerDelay);
    }

    @Override
    protected void onDisable() {
        triggered = false;
        timerUtil.reset();
    }

    @Override
    protected void onEnable() {
        timerUtil.reset();
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player == null || mc.player.connection == null) {
                return;
            }

            if (options.get("Timer").get()) {
                float seconds = timerDelay.get();
                long delayMillis = (long) (seconds * 1000L);

                if (timerUtil.hasTimeElapsed(delayMillis)) {
                    sendInventoryPackets();
                    timerUtil.reset();
                }
                return;
            }

            boolean hurt = mc.player.hurtTime > 0;
            if (hurt && !triggered) {
                triggered = true;
                sendInventoryPackets();
            } else if (!hurt) {
                triggered = false;
            }
        }
    }

    private void sendInventoryPackets() {
        mc.player.connection.sendPacket(new CEntityActionPacket(mc.player, CEntityActionPacket.Action.OPEN_INVENTORY));
        mc.player.connection.sendPacket(new CCloseWindowPacket(0));
    }
}
