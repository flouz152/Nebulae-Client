package beame.components.baritone.command.manager;

import beame.components.baritone.Baritone;
import beame.components.baritone.api.IBaritone;
import beame.components.baritone.api.command.ICommand;
import beame.components.baritone.api.command.argument.ICommandArgument;
import beame.components.baritone.api.command.exception.CommandException;
import beame.components.baritone.api.command.exception.CommandUnhandledException;
import beame.components.baritone.api.command.exception.ICommandException;
import beame.components.baritone.api.command.helpers.TabCompleteHelper;
import beame.components.baritone.api.command.manager.ICommandManager;
import beame.components.baritone.api.command.registry.Registry;
import beame.components.baritone.command.argument.ArgConsumer;
import beame.components.baritone.command.argument.CommandArguments;
import beame.components.baritone.command.defaults.DefaultCommands;
import net.minecraft.util.Tuple;

import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;


/**
 * The default, internal implementation of {@link ICommandManager}
 *
 * @author Brady
 * @since 9/21/2019
 */
public class CommandManager implements ICommandManager {
// leaked by itskekoff; discord.gg/sk3d FUiZN5xV

    private final Registry<ICommand> registry = new Registry<>();
    private final Baritone baritone;

    public CommandManager(Baritone baritone) {
        this.baritone = baritone;
        DefaultCommands.createAll(baritone).forEach(this.registry::register);
    }

    @Override
    public IBaritone getBaritone() {
        return this.baritone;
    }

    @Override
    public Registry<ICommand> getRegistry() {
        return this.registry;
    }

    @Override
    public ICommand getCommand(String name) {
        for (ICommand command : this.registry.entries) {
            if (command.getNames().contains(name.toLowerCase(Locale.US))) {
                return command;
            }
        }
        return null;
    }

    @Override
    public boolean execute(String string) {
        return this.execute(expand(string));
    }

    @Override
    public boolean execute(Tuple<String, List<ICommandArgument>> expanded) {
        ExecutionWrapper execution = this.from(expanded);
        if (execution != null) {
            execution.execute();
        }
        return execution != null;
    }

    @Override
    public Stream<String> tabComplete(Tuple<String, List<ICommandArgument>> expanded) {
        ExecutionWrapper execution = this.from(expanded);
        return execution == null ? Stream.empty() : execution.tabComplete();
    }

    @Override
    public Stream<String> tabComplete(String prefix) {
        Tuple<String, List<ICommandArgument>> pair = expand(prefix, true);
        String label = pair.getA();
        List<ICommandArgument> args = pair.getB();
        if (args.isEmpty()) {
            return new TabCompleteHelper()
                    .addCommands(this.baritone.getCommandManager())
                    .filterPrefix(label)
                    .stream();
        } else {
            return tabComplete(pair);
        }
    }

    private ExecutionWrapper from(Tuple<String, List<ICommandArgument>> expanded) {
        String label = expanded.getA();
        ArgConsumer args = new ArgConsumer(this, expanded.getB());

        ICommand command = this.getCommand(label);
        return command == null ? null : new ExecutionWrapper(command, label, args);
    }

    private static Tuple<String, List<ICommandArgument>> expand(String string, boolean preserveEmptyLast) {
        String label = string.split("\\s", 2)[0];
        List<ICommandArgument> args = CommandArguments.from(string.substring(label.length()), preserveEmptyLast);
        return new Tuple<>(label, args);
    }

    public static Tuple<String, List<ICommandArgument>> expand(String string) {
        return expand(string, false);
    }

    private static final class ExecutionWrapper {

        private ICommand command;
        private String label;
        private ArgConsumer args;

        private ExecutionWrapper(ICommand command, String label, ArgConsumer args) {
            this.command = command;
            this.label = label;
            this.args = args;
        }

        private void execute() {
            try {
                this.command.execute(this.label, this.args);
            } catch(Throwable t) {
                // Create a handleable exception, wrap if needed
                ICommandException exception = t instanceof ICommandException
                        ? (ICommandException) t
                        : new CommandUnhandledException(t);

                exception.handle(command, args.getArgs());
            }
        }

        private Stream<String> tabComplete() {
            try {
                return this.command.tabComplete(this.label, this.args);
            } catch(CommandException ignored) {
                // NOP
            } catch(Throwable t) {
                t.printStackTrace();
            }
            return Stream.empty();
        }
    }
}
