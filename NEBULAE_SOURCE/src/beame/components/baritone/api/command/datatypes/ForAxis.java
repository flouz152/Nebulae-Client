package beame.components.baritone.api.command.datatypes;

import beame.components.baritone.api.command.exception.CommandException;
import beame.components.baritone.api.command.helpers.TabCompleteHelper;
import net.minecraft.util.Direction;

import java.util.Locale;
import java.util.stream.Stream;

public enum ForAxis implements IDatatypeFor<Direction.Axis> {
// leaked by itskekoff; discord.gg/sk3d eslXLzGz
    INSTANCE;

    @Override
    public Direction.Axis get(IDatatypeContext ctx) throws CommandException {
        return Direction.Axis.valueOf(ctx.getConsumer().getString().toUpperCase(Locale.US));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper()
                .append(Stream.of(Direction.Axis.values())
                        .map(Direction.Axis::getString).map(String::toLowerCase))
                .filterPrefix(ctx.getConsumer().getString())
                .stream();
    }
}
