package beame.components.command.impl.software;

import beame.Nebulae;
import beame.components.command.AbstractCommand;

import java.util.List;

@AbstractCommand.CommandInfo(name = "help", description = "Список доступных команд")
public class HelpCommand extends AbstractCommand {
// leaked by itskekoff; discord.gg/sk3d GTtzZgCn

    @Override
    public void run(String[] args) {
        out("[Список команд:]");
        Nebulae.getHandler().commandManager.commands.stream()
                .filter(cmd -> !cmd.command.equalsIgnoreCase("help"))
                .forEach(cmd -> out("." + cmd.command + " - " + cmd.description));
    }

    @Override
    public void error() {
        printMessage(List.of(
                "[Ошибка] Просто используйте:",
                ".help"
        ));
    }

    @Override
    public String name() {
        return "help";
    }

    @Override
    public String description() {
        return "Список доступных команд";
    }

    @Override
    public List<String> adviceMessage() {
        return List.of(".help");
    }

    @Override
    public List<String> aliases() {
        return List.of("commands", "?");
    }
}
