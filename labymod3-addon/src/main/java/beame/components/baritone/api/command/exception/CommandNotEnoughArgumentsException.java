package beame.components.baritone.api.command.exception;

public class CommandNotEnoughArgumentsException extends CommandErrorMessageException {
// leaked by itskekoff; discord.gg/sk3d YxI9oOPe

    public CommandNotEnoughArgumentsException(int minArgs) {
        super(String.format("Not enough arguments (expected at least %d)", minArgs));
    }
}
