package beame.components.modules.movement;

import beame.components.command.AbstractCommand;
import events.Event;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CPlayerPacket;
import net.minecraft.util.math.vector.Vector3d;
import beame.setting.SettingList.BooleanSetting;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ItemTP extends Module {
// leaked by itskekoff; discord.gg/sk3d X3ni5ufu
    private final Minecraft mc = Minecraft.getInstance();
    private ScheduledExecutorService executorService;
    private static final int SEARCH_INTERVAL = 350;
    private static final double SEARCH_RADIUS = 100.0;
    private final BooleanSetting tp = new BooleanSetting("Сразу", true);

    private final Set<Item> targetItems = new HashSet<>(Arrays.asList(
            Items.GOLDEN_APPLE,
            Items.NETHERITE_SWORD,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_BOOTS,
            Items.NETHERITE_LEGGINGS,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_BOOTS,
            Items.DIAMOND_LEGGINGS,
            Items.NETHERITE_PICKAXE,
            Items.DIAMOND_PICKAXE,
            Items.ELYTRA,
            Items.ENCHANTED_GOLDEN_APPLE,
            Items.PLAYER_HEAD,
            Items.TOTEM_OF_UNDYING,
            Items.SPLASH_POTION,
            Items.PLAYER_HEAD,
            Items.PHANTOM_MEMBRANE,
            Items.CONDUIT,
            Items.BONE_MEAL,
            Items.SHULKER_BOX
    ));

    private Vector3d initialPosition = null;

    public ItemTP() {
        super("ItemTP", Category.Movement, true, "Телепортирует вас к лежащим предметам, а потом возвращает обратно");
        addSettings(tp);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        startTeleportation();
        AbstractCommand.addMessage("Для корректной работы нужен /flyspeed");
        return;
    }

    @Override
    public void event(Event event) {

    }

    @Override
    public void onDisable() {
        super.onDisable();
        stopTeleportation();
        AbstractCommand.addMessage("Авто-TP выключен!");
    }

    private void startTeleportation() {
        executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(() -> {
            if (mc.world == null || mc.player == null) return;

            findNearestTargetItem().ifPresent(item -> {
                if (initialPosition == null) {
                    initialPosition = mc.player.getPositionVec();
                }

                Vector3d itemPos = item.getPositionVec();
                teleport(itemPos.getX(), itemPos.getY(), itemPos.getZ());

                if (tp.get()) {
                    executorService.schedule(this::teleportBack, 100, TimeUnit.MILLISECONDS);
                }
            });
        }, 0, SEARCH_INTERVAL, TimeUnit.MILLISECONDS);
    }

    private void teleport(double x, double y, double z) {
        mc.player.connection.sendPacket(new CPlayerPacket.PositionPacket(x, y, z, true));
        mc.player.setPosition(x, y, z);
    }

    private void teleportBack() {
        if (initialPosition != null) {
            teleport(initialPosition.getX(), initialPosition.getY(), initialPosition.getZ());
            initialPosition = null;
        }
    }

    private void stopTeleportation() {
        if (executorService != null) {
            executorService.shutdownNow();
            executorService = null;
        }
        initialPosition = null;
    }

    private Optional<ItemEntity> findNearestTargetItem() {
        return mc.world.getEntitiesWithinAABB(
                        ItemEntity.class,
                        mc.player.getBoundingBox().grow(SEARCH_RADIUS),
                        e -> targetItems.contains(e.getItem().getItem())
                ).stream()
                .min(Comparator.comparingDouble(e -> e.getDistanceSq(mc.player)));
    }
}

