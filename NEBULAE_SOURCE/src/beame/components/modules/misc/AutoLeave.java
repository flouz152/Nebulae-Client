package beame.components.modules.misc;


import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.player.PlayerEntity;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;
import events.Event;
import events.impl.player.EventUpdate;
import net.minecraft.util.text.StringTextComponent;


public class AutoLeave extends Module {
// leaked by itskekoff; discord.gg/sk3d kLuaVZgh
    private final SliderSetting cost = new SliderSetting("Дистанция до игрока", 50, 1, 100, 1);
    public static final RadioSetting type = new RadioSetting("Тип лива", "Хаб", "Хаб", "Домой", "На спавн", "Отключение");
    private String lastPlayerName = "";
    private boolean hasLeft = false;


    public AutoLeave() {
        super("AutoLeave", Category.Misc, true, "Автоматически покидает мир/игру");
        addSettings(cost, type);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventUpdate) {
            if (mc.player == null || mc.world == null) return;

            if (hasLeft) return;

            PlayerEntity nearestPlayer = null;
            double nearestDistance = Double.MAX_VALUE;

            for (PlayerEntity player : mc.world.getPlayers()) {
                if (player == mc.player) continue;
                if (Nebulae.getHandler().friends.isFriend(player.getScoreboardName())) continue;
                if (!player.botEntity) continue;

                double dist = mc.player.getDistance(player);
                if (dist < nearestDistance) {
                    nearestDistance = dist;
                    nearestPlayer = player;
                    lastPlayerName = player.getName().getString();
                }
            }

            if (nearestPlayer != null && nearestDistance <= cost.get()) {
                leaveServer();
                hasLeft = true;
                toggle();
            }
        }
    }

    private void leaveServer() {
        switch (type.get()) {
            case "Хаб":
                AbstractCommand.addMessage("Ливнул от " + lastPlayerName);
                mc.player.sendChatMessage("/hub");
                break;
            case "Домой":
                AbstractCommand.addMessage(" от " + lastPlayerName);
                mc.player.sendChatMessage("/home");
                break;
            case "На спавн":
                AbstractCommand.addMessage("Ливнул от " + lastPlayerName);
                mc.player.sendChatMessage("/spawn");
                break;
            case "Отключение":
                AbstractCommand.addMessage("Ливнул от " + lastPlayerName);
                if (mc.world != null) {
                    mc.world.sendQuittingDisconnectingPacket();
                }
                mc.player.connection.getNetworkManager().closeChannel(new StringTextComponent("§c§lВы вышли с сервера!\n§f§lЛивнул от " + lastPlayerName));
                break;
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        hasLeft = false;
        lastPlayerName = "";
    }

    @Override
    public void onDisable() {
        super.onDisable();
        hasLeft = false;
        lastPlayerName = "";
    }
}