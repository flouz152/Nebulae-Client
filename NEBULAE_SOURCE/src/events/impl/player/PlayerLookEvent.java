package events.impl.player;

import events.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector2f;

@Getter
@Setter
@AllArgsConstructor
public final class PlayerLookEvent extends Event {
// leaked by itskekoff; discord.gg/sk3d VhFBIsWJ
    private Vector2f rotation;
}