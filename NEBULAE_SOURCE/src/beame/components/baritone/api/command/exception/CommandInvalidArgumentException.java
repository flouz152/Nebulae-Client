package beame.components.baritone.api.command.exception;

import beame.components.baritone.api.command.argument.ICommandArgument;

public abstract class CommandInvalidArgumentException extends CommandErrorMessageException {
// leaked by itskekoff; discord.gg/sk3d Snhw4sZ4

    public final ICommandArgument arg;

    protected CommandInvalidArgumentException(ICommandArgument arg, String message) {
        super(formatMessage(arg, message));
        this.arg = arg;
    }

    protected CommandInvalidArgumentException(ICommandArgument arg, String message, Throwable cause) {
        super(formatMessage(arg, message), cause);
        this.arg = arg;
    }

    private static String formatMessage(ICommandArgument arg, String message) {
        return String.format(
                "Error at argument #%s: %s",
                arg.getIndex() == -1 ? "<unknown>" : Integer.toString(arg.getIndex() + 1),
                message
        );
    }
}
