package beame.components.modules.render;

import beame.util.render.RenderUtil;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.settings.PointOfView;

import java.awt.*;

public class Crosshair extends Module {
// leaked by itskekoff; discord.gg/sk3d HvYGejoZ

    public Crosshair() {
        super("Crosshair", Category.Visuals, true, "Добавляет кастомный прицел");
    }
    @Override
    public void event(events.Event event) {
        float x = mc.getMainWindow().getScaledWidth() / 2f;
        float y = mc.getMainWindow().getScaledHeight() / 2f;

        if (event instanceof EventRender eventRender && eventRender.isRender2D() && mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON) {
            float cooldown = 1 - mc.player.getCooledAttackStrength(0);
            float thickness = 1;
            float length = 3;
            float gap = 2 + 8 * cooldown;

            int color = mc.pointedEntity != null ? Color.RED.getRGB() : -1;

            drawOutlined(x - thickness / 2, y - gap - length, thickness, length, color);
            drawOutlined(x - thickness / 2, y + gap, thickness, length, color);

            drawOutlined(x - gap - length, y - thickness / 2, length, thickness, color);
            drawOutlined(x + gap, y - thickness / 2, length, thickness, color);
        }
    }

    private void drawOutlined(float x, float y, float w, float h, int hex) {
        RenderUtil.drawRect2(x - 0.5f, y - 0.5f, w + 1, h + 1, Color.BLACK.getRGB());
        RenderUtil.drawRect2(x, y, w, h, hex);
    }

}
