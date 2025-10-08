package beame.components.baritone.api.command.exception;

public abstract class CommandException extends Exception implements ICommandException {
// leaked by itskekoff; discord.gg/sk3d km3XToXy

    protected CommandException(String reason) {
        super(reason);
    }

    protected CommandException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
