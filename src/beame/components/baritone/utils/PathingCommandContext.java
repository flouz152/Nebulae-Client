package beame.components.baritone.utils;

import beame.components.baritone.api.pathing.goals.Goal;
import beame.components.baritone.api.process.PathingCommand;
import beame.components.baritone.api.process.PathingCommandType;
import beame.components.baritone.pathing.movement.CalculationContext;

public class PathingCommandContext extends PathingCommand {
// leaked by itskekoff; discord.gg/sk3d 9b5cYw74

    public final CalculationContext desiredCalcContext;

    public PathingCommandContext(Goal goal, PathingCommandType commandType, CalculationContext context) {
        super(goal, commandType);
        this.desiredCalcContext = context;
    }
}
