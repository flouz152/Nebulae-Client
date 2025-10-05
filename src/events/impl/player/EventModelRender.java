package events.impl.player;

import events.Event;
import net.minecraft.client.renderer.entity.PlayerRenderer;

public class EventModelRender extends Event {
// leaked by itskekoff; discord.gg/sk3d ewZDSQP8

    public PlayerRenderer renderer;
    private Runnable entityRenderer;

    public EventModelRender(PlayerRenderer renderer, Runnable entityRenderer) {
        this.renderer = renderer;
        this.entityRenderer = entityRenderer;
    }

    public void render() {
        entityRenderer.run();
    }

}
