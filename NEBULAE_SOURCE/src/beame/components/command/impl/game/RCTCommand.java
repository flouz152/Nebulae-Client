package beame.components.command.impl.game;

import beame.components.command.AbstractCommand;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

@AbstractCommand.CommandInfo(name = "rct", description = "Перезаход на анархию")
public class RCTCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d tWVZy7WH

    private String getAnarchyNumber() {
        if (mc.ingameGUI.getTabList().header == null) {
            return "";
        }
        String serverHeader = TextFormatting.getTextWithoutFormattingCodes(mc.ingameGUI.getTabList().header.getString());
        if (serverHeader != null && serverHeader.contains("Анархия-")) {
            return serverHeader.split("Анархия-")[1].trim();
        }
        return "";
    }

    @Override
    public void run(String[] args) {
        String anarchy = getAnarchyNumber();
        if (anarchy.isEmpty()) {
            error();
            return;
        }

        try {
            mc.player.sendChatMessage("/hub");
            Thread.sleep(1000);
            mc.player.sendChatMessage("/an" + anarchy);
            addMessage("Перезаход на Анархию-" + TextFormatting.BLUE + anarchy + TextFormatting.WHITE + " выполнен.");
        } catch (InterruptedException e) {
            addMessage(TextFormatting.RED + "Ошибка при выполнении перезахода.");
        }
    }

    @Override
    public void error() {
        printMessage(adviceMessage());
    }

    @Override
    public String name() {
        return "rct";
    }

    @Override
    public String description() {
        return "Перезаход на текущую анархию";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of(
                TextFormatting.RED + "[Ошибка] Не удалось определить номер анархии.",
                "Убедитесь, что вы находитесь на сервере с заголовком 'Анархия-X'."
        );
    }

    @Override
    public List<String> aliases() {
        return List.of("reconnect", "anarchy");
    }
}