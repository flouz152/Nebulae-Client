package beame.components.modules.player;

import beame.Nebulae;
import beame.components.command.AbstractCommand;
import beame.util.player.InventoryUtility;
import events.Event;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.item.Items;

public class AutoDisorient extends Module {
// leaked by itskekoff; discord.gg/sk3d H3KDKZ2J
    private long lastDisorientUse = 0;
    private static final long DISORIENT_COOLDOWN = 60000;
    private int failedAttempts = 0;
    private static final int MAX_FAILED_ATTEMPTS = 3;
    public AutoDisorient() {
        super("AutoDisorient", Category.Player, true, "Автоматически использует дезориентацию");
    }

    private boolean isPlayerNearby(double radius) {
        if (mc.world == null || mc.player == null) return false;
        return mc.world.getPlayers().stream()
                .anyMatch(player -> player != mc.player &&
                        !player.isSpectator() && !Nebulae.getHandler().friends.isFriend(player.getGameProfile().getName()) &&
                        player.getDistanceSq(mc.player) <= radius * radius);
    }

    @Override
    public void event(Event event) {
        if (mc.player == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDisorientUse >= DISORIENT_COOLDOWN) {
            if (isPlayerNearby(11)) {
                boolean found = InventoryUtility.inventorySwapClick(Items.ENDER_EYE, "don-item", "desorientation", false);
                if (found) {
                    AbstractCommand.addMessage("Использовал дезориентацию!");
                    lastDisorientUse = currentTime;
                    failedAttempts = 0;
                } else {
                    failedAttempts++;
                    if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
                        AbstractCommand.addMessage("Дезориентация не найдена! Модуль выключен.");
                        toggle();
                    } else {
                    }
                }
            }
        }
    }

    @Override
    public void onEnable() {
        lastDisorientUse = 0;
        failedAttempts = 0;
        AbstractCommand.addMessage("Дезориентация будет использована если игрок будет в радиусе 10 блоков");
    }
}