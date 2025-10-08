package beame.components.baritone.api.event.events;

import beame.components.baritone.api.event.events.type.Cancellable;

/**
 * @author Brady
 * @since 8/1/2018
 */
public final class ChatEvent extends Cancellable {
// leaked by itskekoff; discord.gg/sk3d 8776jKsa

    /**
     * The message being sent
     */
    private final String message;

    public ChatEvent(String message) {
        this.message = message;
    }

    /**
     * @return The message being sent
     */
    public final String getMessage() {
        return this.message;
    }
}
