package beame.components.baritone.api.command.exception;

public class CommandTooManyArgumentsException extends CommandErrorMessageException {
// leaked by itskekoff; discord.gg/sk3d 19VaIkUp

    public CommandTooManyArgumentsException(int maxArgs) {
        super(String.format("Too many arguments (expected at most %d)", maxArgs));
    }
}
