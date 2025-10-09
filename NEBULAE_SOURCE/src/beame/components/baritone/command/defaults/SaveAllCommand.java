package beame.components.baritone.command.defaults;

import beame.components.baritone.api.IBaritone;
import beame.components.baritone.api.command.Command;
import beame.components.baritone.api.command.argument.IArgConsumer;
import beame.components.baritone.api.command.exception.CommandException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SaveAllCommand extends Command {
// leaked by itskekoff; discord.gg/sk3d 8asI9dQY

    public SaveAllCommand(IBaritone baritone) {
        super(baritone, "saveall");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMax(0);
        ctx.worldData().getCachedWorld().save();
        logDirect("Saved");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Saves Baritone's cache for this world";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The saveall command saves Baritone's world cache.",
                "",
                "Usage:",
                "> saveall"
        );
    }
}
