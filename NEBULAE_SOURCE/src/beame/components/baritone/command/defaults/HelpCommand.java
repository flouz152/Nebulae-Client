package beame.components.baritone.command.defaults;

import beame.components.baritone.api.IBaritone;
import beame.components.baritone.api.command.Command;
import beame.components.baritone.api.command.ICommand;
import beame.components.baritone.api.command.argument.IArgConsumer;
import beame.components.baritone.api.command.exception.CommandException;
import beame.components.baritone.api.command.exception.CommandNotFoundException;
import beame.components.baritone.api.command.helpers.Paginator;
import beame.components.baritone.api.command.helpers.TabCompleteHelper;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static beame.components.baritone.api.command.IBaritoneChatControl.FORCE_COMMAND_PREFIX;

public class HelpCommand extends Command {
// leaked by itskekoff; discord.gg/sk3d nArx6MGT

    public HelpCommand(IBaritone baritone) {
        super(baritone, "help", "?");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(1);
        if (!args.hasAny() || args.is(Integer.class)) {
            Paginator.paginate(
                    args, new Paginator<>(
                            this.baritone.getCommandManager().getRegistry().descendingStream()
                                    .filter(command -> !command.hiddenFromHelp())
                                    .collect(Collectors.toList())
                    ),
                    () -> logDirect("All Baritone commands (clickable):"),
                    command -> {
                        String names = String.join("/", command.getNames());
                        String name = command.getNames().get(0);
                        TextComponent shortDescComponent = new StringTextComponent(" - " + command.getShortDesc());
                        shortDescComponent.setStyle(shortDescComponent.getStyle().setFormatting(TextFormatting.DARK_GRAY));
                        TextComponent namesComponent = new StringTextComponent(names);
                        namesComponent.setStyle(namesComponent.getStyle().setFormatting(TextFormatting.WHITE));
                        TextComponent hoverComponent = new StringTextComponent("");
                        hoverComponent.setStyle(hoverComponent.getStyle().setFormatting(TextFormatting.GRAY));
                        hoverComponent.append(namesComponent);
                        hoverComponent.appendString("\n" + command.getShortDesc());
                        hoverComponent.appendString("\n\nClick to view full help");
                        String clickCommand = FORCE_COMMAND_PREFIX + String.format("%s %s", label, command.getNames().get(0));
                        TextComponent component = new StringTextComponent(name);
                        component.setStyle(component.getStyle().setFormatting(TextFormatting.GRAY));
                        component.append(shortDescComponent);
                        component.setStyle(component.getStyle()
                                .setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverComponent))
                                .setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand)));
                        return component;
                    },
                    FORCE_COMMAND_PREFIX + label
            );
        } else {
            String commandName = args.getString().toLowerCase();
            ICommand command = this.baritone.getCommandManager().getCommand(commandName);
            if (command == null) {
                throw new CommandNotFoundException(commandName);
            }
            logDirect(String.format("%s - %s", String.join(" / ", command.getNames()), command.getShortDesc()));
            logDirect("");
            command.getLongDesc().forEach(this::logDirect);
            logDirect("");
            TextComponent returnComponent = new StringTextComponent("Click to return to the help menu");
            returnComponent.setStyle(returnComponent.getStyle().setClickEvent(new ClickEvent(
                    ClickEvent.Action.RUN_COMMAND,
                    FORCE_COMMAND_PREFIX + label
            )));
            logDirect(returnComponent);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return new TabCompleteHelper()
                    .addCommands(this.baritone.getCommandManager())
                    .filterPrefix(args.getString())
                    .stream();
        }
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "View all commands or help on specific ones";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "Using this command, you can view detailed help information on how to use certain commands of Baritone.",
                "",
                "Usage:",
                "> help - Lists all commands and their short descriptions.",
                "> help <command> - Displays help information on a specific command."
        );
    }
}
