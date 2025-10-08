package beame.util.animation;

public class SmoothFloat {
// leaked by itskekoff; discord.gg/sk3d 89d4M5u4
    private float currentValue;
    private float targetValue;
    private float speed;

    public SmoothFloat(float initialValue, float speed) {
        this.currentValue = initialValue;
        this.targetValue = initialValue;
        this.speed = speed;
    }

    public void setTargetValue(float targetValue) {
        this.targetValue = targetValue;
    }

    public void update() {
        float delta = targetValue - currentValue;

        if (Math.abs(delta) > 0.01f) {
            currentValue += delta * speed;
        }
    }

    public float getCurrentValue() {
        return currentValue;
    }
}
