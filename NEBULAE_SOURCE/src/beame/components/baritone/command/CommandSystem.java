package beame.components.baritone.command;

import beame.components.baritone.api.command.ICommandSystem;
import beame.components.baritone.api.command.argparser.IArgParserManager;
import beame.components.baritone.command.argparser.ArgParserManager;

/**
 * @author Brady
 * @since 10/4/2019
 */
public enum CommandSystem implements ICommandSystem {
// leaked by itskekoff; discord.gg/sk3d 7C1y7dG1
    INSTANCE;

    @Override
    public IArgParserManager getParserManager() {
        return ArgParserManager.INSTANCE;
    }
}
