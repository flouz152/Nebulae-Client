package beame.components.baritone.api.command.datatypes;

import beame.components.baritone.api.command.exception.CommandException;

/**
 * @author Brady
 * @since 9/26/2019
 */
public interface IDatatypePostFunction<T, O> {
// leaked by itskekoff; discord.gg/sk3d qYZOI9bj

    T apply(O original) throws CommandException;
}
