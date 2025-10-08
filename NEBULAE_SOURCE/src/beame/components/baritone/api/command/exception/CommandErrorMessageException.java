package beame.components.baritone.api.command.exception;

public abstract class CommandErrorMessageException extends CommandException {
// leaked by itskekoff; discord.gg/sk3d aCrm2l2e

    protected CommandErrorMessageException(String reason) {
        super(reason);
    }

    protected CommandErrorMessageException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
