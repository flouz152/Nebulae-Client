package beame.components.baritone.api.command.exception;

public class CommandNoParserForTypeException extends CommandUnhandledException {
// leaked by itskekoff; discord.gg/sk3d yfbvxLGe

    public CommandNoParserForTypeException(Class<?> klass) {
        super(String.format("Could not find a handler for type %s", klass.getSimpleName()));
    }
}
