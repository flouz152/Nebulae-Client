package beame.components.modules.player;

import events.Event;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import beame.util.math.TimerUtil;
import org.apache.commons.lang3.RandomStringUtils;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import java.util.concurrent.ThreadLocalRandom;

public class AntiAFK extends Module {
// leaked by itskekoff; discord.gg/sk3d lBgp7aVU
    public AntiAFK() {
        super("AntiAFK", Category.Player, true, "Прыгает, отправляет команды, использует предметы, чтобы сервер не отключил вас за АФК");
        addSettings(use);
    }

    public final EnumSetting use = new EnumSetting("Использовать",
            new BooleanSetting("Команды", true, 0),
            new BooleanSetting("Прыжки", true, 0)
    );

    private final TimerUtil commandTimer = new TimerUtil();
    private final TimerUtil jumpTimer = new TimerUtil();
    private boolean commandSent = false;

    @Override
    public void event(Event event) {
        if (event instanceof Render2DEvent) {
            if (mc.player == null) return;

            if (use.get("Команды").get()) {
                String[] commands = {"/shop", "/dmarket", "/donate", "/donatekits"};

                if (commandTimer.hasTimeElapsed(60000)) {
                    if (!commandSent) {
                        int randomIndex = (int) (Math.random() * commands.length);
                        mc.player.sendChatMessage(commands[randomIndex]);
                        commandSent = true;
                    } else {
                        if (mc.currentScreen != null) {
                            mc.displayGuiScreen(null);
                        }
                        commandSent = false;
                        commandTimer.reset();
                    }
                }
            }

            if (use.get("Прыжки").get()) {
                if (jumpTimer.hasTimeElapsed(30000)) {
                    mc.player.jump();
                    jumpTimer.reset();
                    if (mc.player.ticksExisted % 20 != 0) return;
                    mc.player.rotationYaw += ThreadLocalRandom.current().nextFloat(-10, 10);
                    mc.player.sendChatMessage("/" + RandomStringUtils.randomAlphabetic(5));
                }
        }
    }
    }
}