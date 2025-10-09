package beame.components.modules.combat;

import events.Event;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class ZeroHitbox extends Module {
// leaked by itskekoff; discord.gg/sk3d aQ5pGvEC
    public ZeroHitbox() {
        super("NoEntityTrace", Category.Combat, true, "Убирает хитбокс у энтити");
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventRender) {
            adjustBoundingBoxesForPlayers();
        }
    }

    @Override
    public void onDisable() {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (shouldSkipPlayer(player))
                continue;

            setBoundingBox(player, 0.3f);
        }
    }

    private void adjustBoundingBoxesForPlayers() {
        for (PlayerEntity player : mc.world.getPlayers()) {
            if (shouldSkipPlayer(player))
                continue;

            setBoundingBox(player, 0);
        }
    }

    private boolean shouldSkipPlayer(PlayerEntity player) {
        return player == mc.player || !player.isAlive();
    }

    private void setBoundingBox(Entity entity, float size) {
        AxisAlignedBB newBoundingBox = calculateBoundingBox(entity, size);
        entity.setBoundingBox(newBoundingBox);
    }

    private AxisAlignedBB calculateBoundingBox(Entity entity, float size) {
        double minX = entity.getPosX() - size;
        double minY = entity.getBoundingBox().minY;
        double minZ = entity.getPosZ() - size;
        double maxX = entity.getPosX() + size;
        double maxY = entity.getBoundingBox().maxY;
        double maxZ = entity.getPosZ() + size;
        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }
}
