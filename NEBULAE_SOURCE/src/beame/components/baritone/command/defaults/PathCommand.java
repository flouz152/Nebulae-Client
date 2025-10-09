package beame.components.baritone.command.defaults;

import beame.components.baritone.api.BaritoneAPI;
import beame.components.baritone.api.IBaritone;
import beame.components.baritone.api.command.Command;
import beame.components.baritone.api.command.argument.IArgConsumer;
import beame.components.baritone.api.command.exception.CommandException;
import beame.components.baritone.api.process.ICustomGoalProcess;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class PathCommand extends Command {
// leaked by itskekoff; discord.gg/sk3d lAiYvRs3

    public PathCommand(IBaritone baritone) {
        super(baritone, "path");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        ICustomGoalProcess customGoalProcess = baritone.getCustomGoalProcess();
        args.requireMax(0);
        BaritoneAPI.getProvider().getWorldScanner().repack(ctx);
        customGoalProcess.path();
        logDirect("Now pathing");
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        return Stream.empty();
    }

    @Override
    public String getShortDesc() {
        return "Start heading towards the goal";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The path command tells Baritone to head towards the current goal.",
                "",
                "Usage:",
                "> path - Start the pathing."
        );
    }
}
