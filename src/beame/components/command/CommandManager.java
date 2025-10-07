package beame.components.command;

import beame.Nebulae;
import beame.components.command.impl.game.GPSCommand;
import beame.components.command.impl.game.RCTCommand;
import beame.components.command.impl.game.StaffCommand;
import beame.components.command.impl.software.HelpCommand;
import beame.components.command.impl.software.configuration.ConfigCommand;
import beame.components.command.impl.software.configuration.FriendCommand;
import beame.components.command.impl.software.keybinds.BindsCommand;
import beame.components.command.impl.software.keybinds.MacroCommand;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import java.util.ArrayList;
import java.util.List;

import static beame.util.IMinecraft.mc;

public class CommandManager {
// leaked by itskekoff; discord.gg/sk3d 2iRONyN5
    public List<AbstractCommand> commands = new ArrayList<>();

    public CommandManager() {
        registerAll(
                new HelpCommand(),
                new RCTCommand(),
                new GPSCommand(),
                new FriendCommand(),
                new MacroCommand(),
                new StaffCommand(),
                new ConfigCommand(),
                new BindsCommand()
        );
    }

    private static void addMessage(Object message) {
        try {
            mc.ingameGUI.getChatGUI().printChatMessage(new StringTextComponent(TextFormatting.BLUE + "nebulae" + TextFormatting.GRAY + " > " + TextFormatting.RESET + message));
        } catch (Exception exc) { }
    }

    public void runCmds(String message) {
        if (Nebulae.getHandler().unhooked) {
            return;
        }

        if (message.startsWith(".")) {
            for (AbstractCommand command : commands) {
                if (!message.startsWith("." + command.command)) continue;
                try {
                    command.run(message.split(" "));
                } catch (Exception ex) {
                    command.error();
                    ex.printStackTrace();
                }
                return;
            }
            addMessage("Неизвестная комманда.");
            addMessage("Список комманд: .help");
        }
    }

    private void registerAll(AbstractCommand... commands) {
        this.commands.addAll(List.of(commands));
    }
}
