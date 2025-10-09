package beame.components.modules.render;

import beame.Nebulae;
import beame.util.autobuy.AuctionUtil;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.funtime.HealthUtil;
import beame.util.render.ClientHandler;
import beame.util.render.PlayerPositionTracker;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.TextFormatting;
import org.joml.Vector4d;
import org.lwjgl.opengl.GL11;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.RadioSetting;

import java.util.*;

public class ESP extends Module {
// leaked by itskekoff; discord.gg/sk3d zBiEuDxX
    private static final float TAG_PADDING = 5.5f;
    private static final int BACKGROUND_COLOR = ColorUtils.rgba(20, 20, 20, 125);
    private static final int FRIEND_COLOR = ColorUtils.rgba(20, 250, 20, 85);
    private static final int BOX_COLOR = ColorUtils.rgba(20, 20, 20, 150);
    private static final int MAX_NAME_PARTS = 2;

    private final Map<Class<? extends Entity>, Map<Vector4d, Entity>> entityMaps = new HashMap<>();
    private final Map<Entity, String> cachedNames = new HashMap<>();
    private final Map<ItemStack, String> cachedSphereInfo = new HashMap<>();

    public final EnumSetting shown = new EnumSetting("Отображать",
            new BooleanSetting("Игроков", true, 0),
            new BooleanSetting("Мобов", false, 0),
            new BooleanSetting("Животных", false, 0),
            new BooleanSetting("Предметы", true, 0),
            new BooleanSetting("Себя", true, 0)
    );

    public final EnumSetting elements = new EnumSetting("Отображать у энтити",
            new BooleanSetting("Боксы", true, 0),
            new BooleanSetting("Тэги", true, 0),
            new BooleanSetting("Броня и предметы", true, 0)
    ).setVisible(() -> shown.get("Игроков").get() || shown.get("Себя").get());

    public final EnumSetting item_elements = new EnumSetting("Отображать у предметов",
            new BooleanSetting("Боксы", true, 0),
            new BooleanSetting("Тэги", true, 0)
    ).setVisible(() -> shown.get("Предметы").get());

    public final RadioSetting boxType = new RadioSetting("Стиль боксов", "Обычный", "Обычный", "Корнер");
    public final BooleanSetting ignore = new BooleanSetting("Игнорировать голых", false);

    public ESP() {
        super("ESP", Category.Visuals, true, "Включает помощника в видимости игрока через стенки");
        addSettings(shown, elements, item_elements, boxType, ignore);
        initEntityMaps();
    }

    private void initEntityMaps() {
        entityMaps.put(PlayerEntity.class, new HashMap<>());
        entityMaps.put(MonsterEntity.class, new HashMap<>());
        entityMaps.put(AnimalEntity.class, new HashMap<>());
        entityMaps.put(ItemEntity.class, new HashMap<>());
    }

    public void updatePositions(float partialTicks) {
        entityMaps.values().forEach(Map::clear);
        cachedNames.clear();
        cachedSphereInfo.clear();

        if (mc.world == null) return;

        mc.world.getAllEntities().forEach(entity -> {
            if (!PlayerPositionTracker.isInView(entity)) return;

            Vector4d pos = PlayerPositionTracker.updatePlayerPositions(entity, partialTicks);
            if (pos == null) return;

            if (entity instanceof PlayerEntity player) {
                if (!shouldRenderPlayer(player)) return;
                entityMaps.get(PlayerEntity.class).put(pos, player);
            } else if (entity instanceof MonsterEntity) {
                entityMaps.get(MonsterEntity.class).put(pos, entity);
            } else if (entity instanceof AnimalEntity) {
                entityMaps.get(AnimalEntity.class).put(pos, entity);
            } else if (entity instanceof ItemEntity) {
                entityMaps.get(ItemEntity.class).put(pos, entity);
            }

        });

    }


    private boolean shouldRenderPlayer(PlayerEntity player) {
        if (!player.botEntity) return false;
        if (ignore.get() && isNaked(player)) return false;
        
        if (player == mc.player) {
            return !(mc.gameSettings.getPointOfView() == PointOfView.FIRST_PERSON || !shown.get("Себя").get());
        }
        return true;
    }

    private String getEntityName(Entity entity) {
        return cachedNames.computeIfAbsent(entity, e -> {
            if (e instanceof PlayerEntity) {
                return e.getDisplayName().getString();
            } else {
                String name = e.getScoreboardName().replace("⚡ ", "");
                String[] parts = name.split("[  ]");
                if (parts.length > MAX_NAME_PARTS) {
                    name = name.replace((" " + parts[MAX_NAME_PARTS]), "");
                }
                return name;
            }
        });
    }

    private String getDonatPrefix(PlayerEntity player) {
        String displayName = player.getDisplayName().getString();
        String plainName = player.getGameProfile().getName();

        if (displayName.contains("§") && !displayName.equals(plainName)) {
            int nameIndex = displayName.indexOf(plainName);
            if (nameIndex > 0) {
                return displayName.substring(0, nameIndex);
            }

        }


        return "";
    }

    private String getItemName(Entity entity) {
        return cachedNames.computeIfAbsent(entity, e -> e.getDisplayName().getString());
    }

    private String getSphereInfo(ItemStack sphere) {
        if (sphere == null || sphere.isEmpty() || sphere.getItem() != Items.PLAYER_HEAD) return "";

        return cachedSphereInfo.computeIfAbsent(sphere, s -> {
            int level = AuctionUtil.getSphereLVL(s);
            if (level == -1) return "";
            return TextFormatting.DARK_GRAY + " [" + TextFormatting.RED +
                    AuctionUtil.getSphereName(s) + " " + TextFormatting.YELLOW +
                    level + TextFormatting.DARK_GRAY + "]";
        });
    }
    public boolean isNaked(LivingEntity entity) {
        return entity.getItemStackFromSlot(EquipmentSlotType.HEAD).isEmpty()
                && entity.getItemStackFromSlot(EquipmentSlotType.CHEST).isEmpty()
                && entity.getItemStackFromSlot(EquipmentSlotType.LEGS).isEmpty()
                && entity.getItemStackFromSlot(EquipmentSlotType.FEET).isEmpty();
    }

    private void renderEntities(MatrixStack matrixStack, Class<? extends Entity> entityClass) {
        Map<Vector4d, Entity> entities = entityMaps.get(entityClass);
        if (entities.isEmpty()) return;

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        List<Map.Entry<Vector4d, Entity>> sortedEntities = new ArrayList<>(entities.entrySet());
        sortedEntities.sort((e1, e2) -> Double.compare(e2.getKey().y, e1.getKey().y));

        for (Map.Entry<Vector4d, Entity> entry : sortedEntities) {
            Entity entity = entry.getValue();
            String name = getEntityName(entity);
            String health = "";
            String sphereText = "";
            String donatPrefix = "";

            if (entity instanceof LivingEntity living) {
                health = String.valueOf(Math.round(entity instanceof PlayerEntity ?
                        HealthUtil.getHealth((PlayerEntity)living) :
                        HealthUtil.getHealthMonster((MobEntity) living)));
                sphereText = getSphereInfo(living.getHeldItemOffhand());
                if (entity instanceof PlayerEntity player) {
                    donatPrefix = getDonatPrefix(player);
                }
            }

            renderEntityInfo(matrixStack, entity, entry.getKey(), name, health, sphereText, donatPrefix,
                    entity instanceof PlayerEntity);
        }
    }

    private void renderItems(MatrixStack matrixStack, Class<? extends Entity> entityClass) {
        Map<Vector4d, Entity> entities = entityMaps.get(entityClass);
        if (entities.isEmpty()) return;

        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

        List<Map.Entry<Vector4d, Entity>> sortedEntities = new ArrayList<>(entities.entrySet());
        sortedEntities.sort((e1, e2) -> Double.compare(e2.getKey().y, e1.getKey().y));

        for (Map.Entry<Vector4d, Entity> entry : sortedEntities) {
            Entity entity = entry.getValue();
            String name = getItemName(entity);

            renderEntityInfo(matrixStack, entity, entry.getKey(), name, "", "", "",
                    false);
        }
    }

    private void renderEntityInfo(MatrixStack matrixStack, Entity entity, Vector4d pos,
                                  String name, String health, String sphereText, String donatPrefix, boolean isPlayer) {
        if (pos == null) return;

        double x = pos.x;
        double y = pos.y;
        double endX = pos.z;
        double endY = pos.w;

        boolean isFriend = false;
        if (isPlayer && entity instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) entity;
            isFriend = Nebulae.getHandler().friends.isFriend(player.getGameProfile().getName());
        }
        boolean showElements = isPlayer ? elements.get(0).get() : item_elements.get(0).get();

        if (showElements) {
            drawBox(boxType.getIndex(), x, y, endX, endY, isFriend);
        }

        String tagText = formatTagText(name, health, sphereText, donatPrefix, isPlayer);

        if ((isPlayer && elements.get(1).get()) || (!isPlayer && item_elements.get(1).get())) {
            renderTag(matrixStack, tagText, x, y, endX, isFriend);
        }

        if (isPlayer && elements.get(2).get() && entity instanceof PlayerEntity) {
            renderPlayerItems(matrixStack, (PlayerEntity)entity, x, y, endX);
        }
    }

    private String formatTagText(String name, String health, String sphereText, String donatPrefix, boolean isPlayer) {
        if (!isPlayer) return name;

        String displayName = name.contains(mc.player.getScoreboardName()) ?
                TextFormatting.WHITE + mc.getSession().getUsername() : name;

        return displayName +
                TextFormatting.DARK_GRAY + " [" + TextFormatting.RED + health +
                TextFormatting.DARK_GRAY + "]" + sphereText;
    }

    public static void drawItemStack(ItemStack stack, float x, float y, boolean overlay, boolean scale, float scaleValue) {
        RenderSystem.enableDepthTest();
        RenderSystem.pushMatrix();
        RenderSystem.enableBlend();

        RenderSystem.translatef(x, y, 0);
        if (scale) GL11.glScaled(scaleValue, scaleValue, scaleValue);

        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, 0, 0);
        if (overlay) mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, 0, 0);

        RenderSystem.disableBlend();
        RenderSystem.popMatrix();
        RenderSystem.disableDepthTest();
    }

    private void renderPlayerItems(MatrixStack matrixStack, PlayerEntity player, double x, double y, double endX) {
        ItemStack mainHand = player.getHeldItemMainhand();
        ItemStack offHand = player.getHeldItemOffhand();
        float yPos = (float)(y - (elements.getValueByName("Тэги").get() ? 22 : 11));

        if (!mainHand.isEmpty()) {
            drawItemStack(mainHand, (float)((x + (endX - x) / 2) - 25), yPos, true, true, 0.45f);
        }
        if (!offHand.isEmpty()) {
            drawItemStack(offHand, (float)((x + (endX - x) / 2 - 15)), yPos, true, true, 0.45f);
        }

        float itemX = (float)((x + (endX - x) / 2 - 5));
        for (ItemStack armor : player.getArmorInventoryList()) {
            if (!armor.isEmpty()) {
                drawItemStack(armor, itemX, yPos, true, true, 0.45f);
                itemX += 10;
            }
        }
    }

    private void renderTag(MatrixStack matrixStack, String text, double x, double y, double endX, boolean isFriend) {
        float textWidth = Fonts.SF_BOLD.get(12).getStringWidth(text);
        float centerX = (float)(x + (endX - x) / 2.0f);
        float textX = centerX - (textWidth / 2.0f);
        float rectStartX = textX - TAG_PADDING;
        float rectEndX = textX + textWidth + TAG_PADDING;
        float finalY = (float)y - 5;

        if(isFriend) {
            ClientHandler.drawRect(rectStartX - 2, finalY - 8, rectEndX + 2, finalY + 2, FRIEND_COLOR);
        } else {
            ClientHandler.drawRect(rectStartX + 3.5f, finalY - 8, rectEndX - 1, finalY + 1, BACKGROUND_COLOR);
        }

        matrixStack.push();
        matrixStack.translate(0, 0, 1);
        Fonts.SF_BOLD.get(12).drawString(matrixStack, text, textX, finalY - 4.5f, -1);
        matrixStack.pop();
    }

    private void drawBox(int type, double x, double y, double endX, double endY, boolean isFriend) {
        int boxColor = BOX_COLOR;
        int rectColor = Nebulae.getHandler().themeManager.getColor(0);

        if (type == 0) {
            drawSimpleBox(x, y, endX, endY, boxColor, rectColor);
        } else if (type == 1) {
            drawCornerBox(x, y, endX, endY, boxColor, rectColor);
        }
    }

    private void drawSimpleBox(double x, double y, double endX, double endY, int boxColor, int rectColor) {
        ClientHandler.drawRect(x + 0.5f, y + 0.5f, endX - 0.5f, y + 2, boxColor);
        ClientHandler.drawRect(x + 0.5f, endY - 2, endX - 0.5f, endY - 0.5f, boxColor);
        ClientHandler.drawRect(x + 0.5f, y + 2, x + 2, endY - 2, boxColor);
        ClientHandler.drawRect(endX - 2, y + 2, endX - 0.5f, endY - 2, boxColor);

        ClientHandler.drawRect(x + 1, y + 1, endX - 1, y + 1.5f, rectColor);
        ClientHandler.drawRect(x + 1, endY - 1.5f, endX - 1, endY - 1, rectColor);
        ClientHandler.drawRect(x + 1, y + 1, x + 1.5f, endY - 1, rectColor);
        ClientHandler.drawRect(endX - 1.5f, y + 1.5f, endX - 1, endY - 1, rectColor);
    }

    private void drawCornerBox(double x, double y, double endX, double endY, int boxColor, int rectColor) {
        double cornerSize = Math.min(Math.abs(endX - x) * 0.25, 15);
        float accentWidth = 1.5f;


        drawCorner(x, y, cornerSize, boxColor, rectColor, 0);
        drawCorner(endX - cornerSize, y, cornerSize, boxColor, rectColor, 1);
        drawCorner(x, endY - cornerSize, cornerSize, boxColor, rectColor, 2);
        drawCorner(endX - cornerSize, endY - cornerSize, cornerSize, boxColor, rectColor, 3);
    }

    private void drawCorner(double x, double y, double size, int boxColor, int rectColor, int corner) {
        float accentWidth = 1.5f;
        boolean isRight = corner == 1 || corner == 3;
        boolean isBottom = corner == 2 || corner == 3;

        if (!isBottom) {
            ClientHandler.drawRect(x, y, x + size, y + 2, boxColor);
            ClientHandler.drawRect(x + 1, y + 1, x + size - 1, y + accentWidth, rectColor);
        } else {
            ClientHandler.drawRect(x, y + size - 2, x + size, y + size, boxColor);
            ClientHandler.drawRect(x + 1, y + size - accentWidth, x + size - 1, y + size - 1, rectColor);
        }

        if (!isRight) {
            ClientHandler.drawRect(x, y, x + 2, y + size, boxColor);
            ClientHandler.drawRect(x + 1, y + 1, x + accentWidth, y + size - 1, rectColor);
        } else {
            ClientHandler.drawRect(x + size - 2, y, x + size, y + size, boxColor);
            ClientHandler.drawRect(x + size - accentWidth, y + 1, x + size - 1, y + size - 1, rectColor);
        }
    }

    @Override
    public void event(Event event) {
        if (!(event instanceof EventRender render)) return;

        if (render.isRender3D()) {
            updatePositions(render.partialTicks);
        }

        if (render.isRender2D()) {
            if (shown.get("Игроков").get() || shown.get("Себя").get()) {
                renderEntities(render.matrixStack, PlayerEntity.class);
            }
            if (shown.get("Предметы").get()) {
                renderItems(render.matrixStack, ItemEntity.class);
            }
            if (shown.get("Мобов").get()) {
                renderItems(render.matrixStack, MonsterEntity.class);
            }
            if (shown.get("Животных").get()) {
                renderItems(render.matrixStack, AnimalEntity.class);
            }
        }
    }
}

