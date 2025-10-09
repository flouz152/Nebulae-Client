package beame.components.modules.render;

import beame.util.color.ColorUtils;
import events.Event;
import events.impl.game.WorldEvent;
import events.impl.render.Render3DLastEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.optifine.render.RenderUtils;
import org.lwjgl.opengl.GL11;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.function.Predicate;

public class BlockESP extends Module {
// leaked by itskekoff; discord.gg/sk3d ES4JlXvc
    private final EnumSetting blocks = new EnumSetting("Отображать",
            new BooleanSetting("Сундук", true, 0),
            new BooleanSetting("Спавнер", true, 0),
            new BooleanSetting("Эндер-Сундук", false, 0),
            new BooleanSetting("Шалкер", false, 0),
            new BooleanSetting("Бочка", false, 0),
            new BooleanSetting("Воронка", false, 0),
            new BooleanSetting("Печка", false, 0),
            new BooleanSetting("Изумрудная руда", false, 0),
            new BooleanSetting("Алмазная руда", false, 0),
            new BooleanSetting("Золотая руда", false, 0),
            new BooleanSetting("Железная руда", false, 0),
            new BooleanSetting("Угольная руда", false, 0),
            new BooleanSetting("Редстоуновая руда", false, 0),
            new BooleanSetting("Лазуритовая руда", false, 0)
    );
    public final SliderSetting oreUpdateInterval = new SliderSetting("Интернал обновления", 5000f, 1000f, 25000f, 100f).setVisible(() -> blocks.get("Изумрудная руда").get() || blocks.get("Алмазная руда").get() || blocks.get("Золотая руда").get() || blocks.get("Железная руда").get() || blocks.get("Угольная руда").get() || blocks.get("Редстоуновая руда").get() || blocks.get("Лазуритовая руда").get());
    public final SliderSetting oreUpdateDistance = new SliderSetting("Дистанция обновления", 5f, 1f, 100f, 1f).setVisible(() -> blocks.get("Изумрудная руда").get() || blocks.get("Алмазная руда").get() || blocks.get("Золотая руда").get() || blocks.get("Железная руда").get() || blocks.get("Угольная руда").get() || blocks.get("Редстоуновая руда").get() || blocks.get("Лазуритовая руда").get());
    public final SliderSetting oreUpdateRadius = new SliderSetting("Радиус обновления", 20f, 1f, 250f, 5f).setVisible(() -> blocks.get("Изумрудная руда").get() || blocks.get("Алмазная руда").get() || blocks.get("Золотая руда").get() || blocks.get("Железная руда").get() || blocks.get("Угольная руда").get() || blocks.get("Редстоуновая руда").get() || blocks.get("Лазуритовая руда").get());

    private final ConcurrentHashMap<BlockPos, Integer> tileEntityRenderMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<BlockPos, Integer> oreRenderMap = new ConcurrentHashMap<>();

    private final Map<String, Predicate<TileEntity>> tileEntityPredicates = new HashMap<>() {{
        put("Сундук", entity -> entity instanceof ChestTileEntity);
        put("Спавнер", entity -> entity instanceof MobSpawnerTileEntity);
        put("Эндер-Сундук", entity -> entity instanceof EnderChestTileEntity);
        put("Шалкер", entity -> entity instanceof ShulkerBoxTileEntity);
        put("Бочка", entity -> entity instanceof BarrelTileEntity);
        put("Воронка", entity -> entity instanceof HopperTileEntity);
        put("Печка", entity -> entity instanceof FurnaceTileEntity);
    }};

    private final Map<String, Block> oreBlocks = new HashMap<>() {{
        put("Изумрудная руда", Blocks.EMERALD_ORE);
        put("Алмазная руда", Blocks.DIAMOND_ORE);
        put("Золотая руда", Blocks.GOLD_ORE);
        put("Железная руда", Blocks.IRON_ORE);
        put("Угольная руда", Blocks.COAL_ORE);
        put("Редстоуновая руда", Blocks.REDSTONE_ORE);
        put("Лазуритовая руда", Blocks.LAPIS_ORE);
    }};

    private final Map<String, Integer> colorMap = new HashMap<>() {{
        put("Сундук", ColorUtils.rgb(255, 179, 39));
        put("Спавнер", ColorUtils.rgb(147, 255, 0));
        put("Эндер-Сундук", ColorUtils.rgb(148, 86, 255));
        put("Шалкер", ColorUtils.rgb(241, 34, 137));
        put("Бочка", ColorUtils.rgb(220, 128, 36));
        put("Воронка", ColorUtils.rgb(71, 72, 77));
        put("Печка", ColorUtils.rgb(24, 24, 24));
        put("Изумрудная руда", ColorUtils.rgb(147, 255, 0));
        put("Алмазная руда", ColorUtils.rgb(92, 219, 213));
        put("Золотая руда", ColorUtils.rgb(255, 215, 0));
        put("Железная руда", ColorUtils.rgb(220, 220, 220));
        put("Угольная руда", ColorUtils.rgb(0, 0, 0));
        put("Редстоуновая руда", ColorUtils.rgb(255, 0, 0));
        put("Лазуритовая руда", ColorUtils.rgb(0, 0, 255));
    }};

    private Future<?> collectionTask;
    private BlockPos lastPlayerPos = null;
    private long lastOreUpdateTime = 0;

    public BlockESP() {
        super("BlockESP", Category.Visuals, true, "Отображение блоков через стены");
        addSettings(blocks, oreUpdateInterval, oreUpdateDistance, oreUpdateRadius);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        scheduleCollectionTask();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (collectionTask != null) {
            collectionTask.cancel(true);
        }
        tileEntityRenderMap.clear();
        oreRenderMap.clear();
    }

    @Override
    public void event(Event event) {
        if (event instanceof Render3DLastEvent) {
            scheduleCollectionTask();
        } else if (event instanceof WorldEvent) {
            renderBlocks();
        }
    }

    private void scheduleCollectionTask() {
        if (collectionTask != null && !collectionTask.isDone()) {
            collectionTask.cancel(true);
        }
//        collectionTask = Nebulae.getHandler().getThreadingSystem().moduleTasks().submit(this::collectData);
    }

    private void collectData() {
        while (!Thread.currentThread().isInterrupted()) {
            if (mc.world == null || mc.player == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                continue;
            }

            updateTileEntities();
            updateOres();

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateTileEntities() {
        if (mc.world == null || mc.player == null) return;

        HashMap<BlockPos, Integer> localTileEntityMap = new HashMap<>();
        mc.world.loadedTileEntityList.stream()
                .filter(entity -> tileEntityPredicates.entrySet().stream()
                        .anyMatch(entry -> blocks.get(entry.getKey()).get() && entry.getValue().test(entity)))
                .forEach(entity -> tileEntityPredicates.entrySet().stream()
                        .filter(entry -> entry.getValue().test(entity))
                        .map(Map.Entry::getKey)
                        .findFirst().ifPresent(key -> localTileEntityMap.put(entity.getPos(), colorMap.get(key))));

        tileEntityRenderMap.keySet().removeIf(pos -> !localTileEntityMap.containsKey(pos));
        localTileEntityMap.forEach(tileEntityRenderMap::putIfAbsent);
    }

    private void updateOres() {
        if (mc.player == null || mc.world == null) return;

        boolean needUpdateOres = false;
        if (lastPlayerPos == null || System.currentTimeMillis() - lastOreUpdateTime > oreUpdateInterval.get()) {
            needUpdateOres = true;
        } else {
            double distance = mc.player.getPositionVec().distanceTo(new Vector3d(lastPlayerPos.getX(), lastPlayerPos.getY(), lastPlayerPos.getZ()));
            if (distance > oreUpdateDistance.get()) {
                needUpdateOres = true;
            }
        }

        if (needUpdateOres) {
            lastPlayerPos = mc.player.getPosition();
            lastOreUpdateTime = System.currentTimeMillis();
            HashMap<BlockPos, Integer> localOreRenderMap = new HashMap<>();
            if (oreBlocks.keySet().stream().anyMatch(key -> blocks.get(key).get())) {
                int radius = 20;
                BlockPos playerPos = mc.player.getPosition();
                for (int x = -radius; x <= radius; x++)
                    for (int y = -radius; y <= radius; y++)
                        for (int z = -radius; z <= radius; z++) {
                            BlockPos pos = playerPos.add(x, y, z);
                            Block block = mc.world.getBlockState(pos).getBlock();
                            oreBlocks.entrySet().stream()
                                    .filter(entry -> blocks.get(entry.getKey()).get() && entry.getValue() == block)
                                    .map(Map.Entry::getKey)
                                    .findFirst().ifPresent(key -> localOreRenderMap.put(pos, colorMap.get(key)));
                        }
            }

            oreRenderMap.keySet().removeIf(pos -> !localOreRenderMap.containsKey(pos));
            localOreRenderMap.forEach(oreRenderMap::putIfAbsent);
        }
    }

    private void renderBlocks() {
        Vector3d renderPos = mc.getRenderManager().info.getProjectedView();
        GL11.glPushMatrix();
        GL11.glTranslated(-renderPos.x, -renderPos.y, -renderPos.z);
        tileEntityRenderMap.forEach(RenderUtils::drawBlockBox);
        oreRenderMap.forEach(RenderUtils::drawBlockBox);
        GL11.glPopMatrix();
    }
}