package beame.components.baritone.api.command.datatypes;

import beame.components.baritone.api.command.argument.IArgConsumer;
import beame.components.baritone.api.command.exception.CommandException;
import beame.components.baritone.api.pathing.goals.GoalYLevel;
import beame.components.baritone.api.utils.BetterBlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.stream.Stream;

public enum RelativeGoalYLevel implements IDatatypePost<GoalYLevel, BetterBlockPos> {
// leaked by itskekoff; discord.gg/sk3d qkl2nany
    INSTANCE;

    @Override
    public GoalYLevel apply(IDatatypeContext ctx, BetterBlockPos origin) throws CommandException {
        if (origin == null) {
            origin = BetterBlockPos.ORIGIN;
        }

        return new GoalYLevel(
                MathHelper.floor(ctx.getConsumer().getDatatypePost(RelativeCoordinate.INSTANCE, (double) origin.y))
        );
    }

    @Override
    public Stream<String> tabComplete(IDatatypeContext ctx) {
        final IArgConsumer consumer = ctx.getConsumer();
        if (consumer.hasAtMost(1)) {
            return consumer.tabCompleteDatatype(RelativeCoordinate.INSTANCE);
        }
        return Stream.empty();
    }
}
