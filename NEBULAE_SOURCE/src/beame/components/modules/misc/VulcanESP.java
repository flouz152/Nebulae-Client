/*
package beame.feature.features.Misc;

import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import beame.util.render.W2S;
import events.Event;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2d;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;

public class VulcanESP extends Module {
    record ItemPoint(Vector3d position, ItemStack stack, long spawnTime, Vector3d velocity, boolean isOnGround) {}
    final List<ItemPoint> itemPoints = new ArrayList<>();

    public VulcanESP() {
        super("VulcanESP", Category.Misc);
    }

    @Override
    public void event(Event event) {
        if(event instanceof EventRender render) {
            if(render.isRender2D()) {
                for (ItemPoint itemPoint : itemPoints) {
                    Vector3d pos = itemPoint.position;
                    ItemStack stack = itemPoint.stack;

                    Vector2d penis = W2S.project(pos.x, pos.y - 0.3F, pos.z);
                    if(penis == null) continue;

                    Vector2f projection = new Vector2f((float)penis.x, (float)penis.y);

                    if (projection.equals(new Vector2f(Float.MAX_VALUE, Float.MAX_VALUE))) {
                        continue;
                    }

                    String itemName = stack.getDisplayName().getString();
                    String levelText = getItemLevel(stack);
                    String text = itemName + " " + levelText;

                    float width = Fonts.SF_BOLD.get(14).getWidth(text);
                    float textWidth = width + 11 + 11;

                    float posX = projection.x;
                    float posY = projection.y - 40;

                    int textColor = getTextColor(stack);

                    ClientHandler.drawRound(posX + 3, posY + 2 - 3, textWidth - 4, 16 - 3, 2, 0);
                    Fonts.SF_BOLD.get(14).drawCenteredString(text, posX, posY + 3f, textColor);
                }
            } else if(render.isRender3D()) {
                glPushMatrix();

                glDisable(GL_TEXTURE_2D);
                glDisable(GL_DEPTH_TEST);

                glEnable(GL_BLEND);
                glEnable(GL_LINE_SMOOTH);

                Vector3d renderOffset = mc.getRenderManager().info.getProjectedView();
                glTranslated(-renderOffset.x, -renderOffset.y, -renderOffset.z);

                glLineWidth(2);

                BufferBuilder buffer = Tessellator.getInstance().getBuffer();
                buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);

                itemPoints.clear();
                for (Entity entity : mc.world.getAllEntities()) {
                    if (entity instanceof ItemEntity itemEntity) {
                        ItemStack stack = itemEntity.getItem();
                        if (isSpecificItem(stack)) {
                            Vector3d pos = itemEntity.getPositionVec();
                            Vector3d velocity = itemEntity.getMotion();
                            long spawnTime = System.currentTimeMillis();
                            boolean isOnGround = isItemOnBlock(pos);

                            if (!isOnGround) {
                                // Новый расчёт линии предикта
                                Vector3d currentPosition = pos;
                                Vector3d currentVelocity = velocity;
                                double gravity = 0.08; // Гравитация для предметов
                                int maxSteps = 300; // Максимальное количество шагов предсказания
                                double timeStep = 0.05; // Шаг по времени для предсказания
                                boolean hitGround = false;

                                for (int i = 0; i < maxSteps && !hitGround; i++) {
                                    Vector3d nextPosition = currentPosition.add(currentVelocity.scale(timeStep));
                                    Vector3d nextVelocity = currentVelocity.add(0, -gravity * timeStep, 0);

                                    BlockRayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(
                                            currentPosition, nextPosition, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player));

                                    // Проверяем столкновение с землёй
                                    if (result.getType() != RayTraceResult.Type.MISS) {
                                        nextPosition = result.getHitVec();
                                        hitGround = true;
                                    }

                                    // Рисуем линию от текущей позиции до следующей
                                    buffer.pos(currentPosition.x, currentPosition.y, currentPosition.z).color(255, 255, 255, 255).endVertex();
                                    buffer.pos(nextPosition.x, nextPosition.y, nextPosition.z).color(255, 255, 255, 255).endVertex();

                                    // Обновляем текущую позицию и скорость
                                    currentPosition = nextPosition;
                                    currentVelocity = nextVelocity;
                                }
                            }

                            itemPoints.add(new ItemPoint(pos, stack, spawnTime, velocity, isOnGround));
                        }
                    }
                }

                Tessellator.getInstance().draw();

                glDisable(GL_BLEND);
                glDisable(GL_LINE_SMOOTH);

                glEnable(GL_TEXTURE_2D);
                glEnable(GL_DEPTH_TEST);

                glPopMatrix();
            }
        }
    }

    private double calculateTimeToFall(double y0, double vy0, double gravity) {
        double a = -0.5 * gravity;
        double b = vy0;
        double c = y0;

        double discriminant = b * b - 4 * a * c;
        if (discriminant < 0) {
            return 0;
        }

        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);

        return Math.max(t1, t2);
    }

    private Vector3d getPositionAtTime(Vector3d position, Vector3d velocity, double time, double gravity) {
        double x = position.x + velocity.x * time;
        double y = position.y + velocity.y * time - 0.5 * gravity * time * time;
        double z = position.z + velocity.z * time;

        return new Vector3d(x, y, z);
    }

    private boolean isSpecificItem(ItemStack stack) {
        return stack.getItem() == Items.TOTEM_OF_UNDYING ||
                stack.getItem() == Items.PLAYER_HEAD ||
                stack.getItem() == Items.SPLASH_POTION ||
                stack.getItem() == Items.IRON_NUGGET ||
                stack.getItem() == Items.SOUL_LANTERN ||
                stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE ||
                stack.getItem() == Items.NETHERITE_PICKAXE ||
                stack.getItem() == Items.NETHERITE_SWORD ||
                stack.getItem() == Items.GOLDEN_APPLE ||
                stack.getItem() == Items.ELYTRA ||
                stack.getItem() == Items.TRIPWIRE_HOOK;
    }

    private boolean isItemOnBlock(Vector3d pos) {
        BlockRayTraceResult result = mc.world.rayTraceBlocks(new RayTraceContext(pos, new Vector3d(pos.x, pos.y - 1, pos.z), RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, mc.player));

        return result.getType() != RayTraceResult.Type.MISS;
    }

    private String getItemLevel(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("display")) {
            CompoundNBT displayTag = stack.getTag().getCompound("display");

            if (displayTag.contains("Lore", 9)) {
                ListNBT loreList = displayTag.getList("Lore", 8);
                for (int i = 0; i < loreList.size(); i++) {
                    String loreLine = loreList.getString(i);
                    if (loreLine.contains("1/3")) return "1/3";
                    if (loreLine.contains("2/3")) return "2/3";
                    if (loreLine.contains("MAX")) return "MAX";
                }
            }
        }
        return "?/3";
    }

    private int getTextColor(ItemStack stack) {
        if (stack.getItem() == Items.TOTEM_OF_UNDYING) {
            return ColorUtils.rgb(255, 0, 0);
        }
        if (stack.getItem() == Items.PLAYER_HEAD) {
            return ColorUtils.rgb(250, 14, 148);
        }
        if (stack.getItem() == Items.SPLASH_POTION) {
            return ColorUtils.rgb(1, 253, 35);
        }
        if (stack.getItem() == Items.IRON_NUGGET) {
            return ColorUtils.rgb(0, 255, 205);
        }
        if (stack.getItem() == Items.SOUL_LANTERN) {
            return ColorUtils.rgb(8, 250, 8);
        }
        if (stack.getItem() == Items.ENCHANTED_GOLDEN_APPLE) {
            return ColorUtils.rgb(255, 165, 0); // Оранжевый
        }
        if (stack.getItem() == Items.NETHERITE_PICKAXE || stack.getItem() == Items.NETHERITE_SWORD) {
            return ColorUtils.rgb(128, 0, 128); // Фиолетовый
        }
        if (stack.getItem() == Items.GOLDEN_APPLE) {
            return ColorUtils.rgb(255, 215, 0); // Золотой
        }
        if (stack.getItem() == Items.ELYTRA) {
            return ColorUtils.rgb(135, 206, 250); // Светло-голубой
        }
        if (stack.getItem() == Items.TRIPWIRE_HOOK) {
            return ColorUtils.rgb(53, 87, 255); // Синий
        }

        return ColorUtils.rgb(255, 255, 255); // Белый по умолчанию
    }
}
*/
// leaked by itskekoff; discord.gg/sk3d OpmAyrQO
