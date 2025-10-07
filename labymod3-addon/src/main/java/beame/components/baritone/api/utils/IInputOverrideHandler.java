package beame.components.baritone.api.utils;

import beame.components.baritone.api.behavior.IBehavior;
import beame.components.baritone.api.utils.input.Input;

/**
 * @author Brady
 * @since 11/12/2018
 */
public interface IInputOverrideHandler extends IBehavior {
// leaked by itskekoff; discord.gg/sk3d cUxvuvEc

    boolean isInputForcedDown(Input input);

    void setInputForceState(Input input, boolean forced);

    void clearAllKeys();
}
