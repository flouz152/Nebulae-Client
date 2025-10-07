package beame.components.baritone.api.command.exception;

import beame.components.baritone.api.command.ICommand;
import beame.components.baritone.api.command.argument.ICommandArgument;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

import static beame.components.baritone.api.utils.Helper.HELPER;

public class CommandUnhandledException extends RuntimeException implements ICommandException {
// leaked by itskekoff; discord.gg/sk3d IOZtiuWd

    public CommandUnhandledException(String message) {
        super(message);
    }

    public CommandUnhandledException(Throwable cause) {
        super(cause);
    }

    @Override
    public void handle(ICommand command, List<ICommandArgument> args) {
        HELPER.logDirect("An unhandled exception occurred. " +
                        "The error is in your game's log, please report this at https://github.com/cabaletta/baritone/issues",
                TextFormatting.RED);

        this.printStackTrace();
    }
}
