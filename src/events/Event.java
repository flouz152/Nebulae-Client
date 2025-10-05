package events;

public class Event {
// leaked by itskekoff; discord.gg/sk3d LvTVr1bu

    public boolean isCancel;

    public boolean isCancel() {
        return isCancel;
    }

    public void open() {
        isCancel = false;
    }

    public void setCancel(boolean cancel) {
        this.isCancel = cancel;
    }

    public void cancel() {
        isCancel = true;
    }

    public String getName() {
        return this.getClass().getName();
    }

}