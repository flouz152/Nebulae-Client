package beame.components.baritone.api.command.exception;

import beame.components.baritone.api.command.ICommand;
import beame.components.baritone.api.command.argument.ICommandArgument;

import java.util.List;

import static beame.components.baritone.api.utils.Helper.HELPER;

public class CommandNotFoundException extends CommandException {
// leaked by itskekoff; discord.gg/sk3d 4zHHDeTi

    public final String command;

    public CommandNotFoundException(String command) {
        super(String.format("Command not found: %s", command));
        this.command = command;
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
        HELPER.logDirect(getMessage());
    }
}
