package beame.components.modules.misc;

import events.Event;
import events.impl.packet.EventPacket;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.play.server.SChatPacket;
import beame.setting.SettingList.InputFieldSetting;

public class AutoReg extends Module {
// leaked by itskekoff; discord.gg/sk3d ZowPBBMc
    public final InputFieldSetting password = new InputFieldSetting("Пароль", "bee1892", "Пароль");

    public AutoReg() {
        super("AutoReg", Category.Misc, true, "Автоматическая регистрация на сервере");
        addSettings(password);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventPacket) {
            EventPacket packetEvent = (EventPacket) event;
            if (packetEvent.getPacket() instanceof SChatPacket chatPacket) {
                String chatMessage = chatPacket.getChatComponent().getString();
                if (chatMessage.contains("Зарегистрируйтесь") || chatMessage.contains("/reg")) {
                    String pass = password.get();
                    if (pass != null && pass.length() >= 4) {
                        mc.player.sendChatMessage("/reg " + pass + " " + pass);
                    }
                }
            }
        }
    }
}
