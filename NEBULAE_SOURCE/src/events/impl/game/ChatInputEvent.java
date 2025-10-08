package events.impl.game;

import events.impl.player.CancellableEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ChatInputEvent extends CancellableEvent {
// leaked by itskekoff; discord.gg/sk3d FKNMYoBU
    private String message;
}
