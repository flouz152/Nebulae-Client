package beame.components.baritone.utils;

import beame.components.baritone.Baritone;
import beame.components.baritone.api.process.IBaritoneProcess;
import beame.components.baritone.api.utils.Helper;
import beame.components.baritone.api.utils.IPlayerContext;

public abstract class BaritoneProcessHelper implements IBaritoneProcess, Helper {
// leaked by itskekoff; discord.gg/sk3d hQ9NvPnO

    protected final Baritone baritone;
    protected final IPlayerContext ctx;

    public BaritoneProcessHelper(Baritone baritone) {
        this.baritone = baritone;
        this.ctx = baritone.getPlayerContext();
    }

    @Override
    public boolean isTemporary() {
        return false;
    }
}
