package beame.components.baritone.api.command.manager;

import beame.components.baritone.api.IBaritone;
import beame.components.baritone.api.command.ICommand;
import beame.components.baritone.api.command.argument.ICommandArgument;
import beame.components.baritone.api.command.registry.Registry;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.stream.Stream;

/**
 * @author Brady
 * @since 9/21/2019
 */
public interface ICommandManager {
// leaked by itskekoff; discord.gg/sk3d ykHQLrgs

    IBaritone getBaritone();

    Registry<ICommand> getRegistry();

    /**
     * @param name The command name to search for.
     * @return The command, if found.
     */
    ICommand getCommand(String name);

    boolean execute(String string);

    boolean execute(Tuple<String, List<ICommandArgument>> expanded);

    Stream<String> tabComplete(Tuple<String, List<ICommandArgument>> expanded);

    Stream<String> tabComplete(String prefix);
}
