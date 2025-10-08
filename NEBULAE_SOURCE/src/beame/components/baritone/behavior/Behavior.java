package beame.components.baritone.behavior;

import beame.components.baritone.Baritone;
import beame.components.baritone.api.behavior.IBehavior;
import beame.components.baritone.api.utils.IPlayerContext;

/**
 * A type of game event listener that is given {@link Baritone} instance context.
 *
 * @author Brady
 * @since 8/1/2018
 */
public class Behavior implements IBehavior {
// leaked by itskekoff; discord.gg/sk3d pI3wqY7L

    public final Baritone baritone;
    public final IPlayerContext ctx;

    protected Behavior(Baritone baritone) {
        this.baritone = baritone;
        this.ctx = baritone.getPlayerContext();
    }
}
