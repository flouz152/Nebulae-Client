package events.impl.game;

public class CancelEvent {
// leaked by itskekoff; discord.gg/sk3d CJhOxg8c

    private boolean isCancel;

    public void cancel() {
        isCancel = true;
    }
    public void open() {
        isCancel = false;
    }
    public boolean isCancel() {
        return isCancel;
    }

}
