package beame.components.baritone.api.cache;

import java.util.function.Consumer;

/**
 * @author Brady
 * @since 9/24/2018
 */
public interface IWorldProvider {
// leaked by itskekoff; discord.gg/sk3d aIA4btRj

    /**
     * Returns the data of the currently loaded world
     *
     * @return The current world data
     */
    IWorldData getCurrentWorld();

    default void ifWorldLoaded(Consumer<IWorldData> callback) {
        final IWorldData currentWorld = this.getCurrentWorld();
        if (currentWorld != null) {
            callback.accept(currentWorld);
        }
    }
}
