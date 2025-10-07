package beame.components.baritone.api.command.datatypes;

import beame.components.baritone.api.command.exception.CommandException;
import beame.components.baritone.api.command.helpers.TabCompleteHelper;
import net.minecraft.util.Direction;

import java.util.Locale;
import java.util.stream.Stream;

public enum ForDirection implements IDatatypeFor<Direction> {
// leaked by itskekoff; discord.gg/sk3d 68we4cHt
    INSTANCE;

    @Override
    public Direction get(IDatatypeContext ctx) throws CommandException {
        return Direction.valueOf(ctx.getConsumer().getString().toUpperCase(Locale.US));
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) throws CommandException {
        return new TabCompleteHelper()
                .append(Stream.of(Direction.values())
                        .map(Direction::getName2).map(String::toLowerCase))
                .filterPrefix(ctx.getConsumer().getString())
                .stream();
    }
}
