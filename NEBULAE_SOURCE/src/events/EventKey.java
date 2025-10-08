package events;


import lombok.AllArgsConstructor;
import lombok.Data;

public class EventKey extends Event {
// leaked by itskekoff; discord.gg/sk3d 60YDkoBM

    public int key;
    public boolean released;

    public EventKey(int key, boolean released) {
        this.key = key;
        this.released = released;
    }

    public boolean isReleased() {
        return released;
    }
}
