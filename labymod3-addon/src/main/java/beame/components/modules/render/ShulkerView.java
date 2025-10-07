package beame.components.modules.render;

import beame.Essence;
import beame.util.math.MathUtil;
import beame.util.render.ClientHandler;
import beame.util.render.PlayerPositionTracker;
import beame.util.render.ProjectionUtil;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import events.Event;
import events.impl.render.Render2DEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import org.joml.Vector2f;
import beame.setting.SettingList.BooleanSetting;

import java.awt.*;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

public class ShulkerView extends Module {
// leaked by itskekoff; discord.gg/sk3d kQDo6kMO
    
    private final BooleanSetting showOnGround = new BooleanSetting("Показывать лежачие шалкера", false);
    
    public ShulkerView() {
        super("ShulkerView", Category.Visuals, true, "Показ предметов внутри шалкера");
        addSettings(showOnGround);
    }

    @Override
    public void event(Event event) {
        if (event instanceof Render2DEvent e) {
            if (showOnGround.get()) {
                StreamSupport.stream(mc.world.getAllEntities().spliterator(), false)
                        .filter(ItemEntity.class::isInstance)
                        .map(ItemEntity.class::cast)
                        .filter(PlayerPositionTracker::isInView)
                        .filter(itemEntity -> isShulkerBox(itemEntity.getItem()))
                        .forEach(ent -> viewShulker(e.getMatrix(), ent, ProjectionUtil.project2D(MathUtil.interpolate(new Vector3d(ent.lastTickPosX, ent.lastTickPosY, ent.lastTickPosZ), ent.getPositionVec()))));
            }
        }
    }

    public void viewShulker(MatrixStack matrix, ItemEntity item, Vector2f vec) {
        float width = 176;

        GlStateManager.pushMatrix();
        GlStateManager.translated(vec.x - width / 4, vec.y - 5, 0);
        GlStateManager.scalef(0.5F,0.5F,0.5F);
        ClientHandler.drawImage(matrix, new ResourceLocation("textures/gui/container/shulker_box-essence.png"), 0, -11.5f, 179, 75, Color.WHITE);

        int posX = 8;
        int posY = 7;

        int spacing = 18;

        for (ItemStack itemStack : Essence.getHandler().getModuleList().getShulkerView().getShulkerBoxItems(item.getItem())) {
            ESP.drawItemStack( itemStack, posX, posY, true, false, 1);
            posX += spacing;

            if (posX >= 170) {
                posX = 8;
                posY += spacing;
            }
        }

        GlStateManager.popMatrix();
    }

    public List<ItemStack> getShulkerBoxItems(ItemStack stack) {
        NonNullList<ItemStack> items = NonNullList.withSize(27, ItemStack.EMPTY);

        if (!(stack.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof ShulkerBoxBlock) || !stack.hasTag()) {
            return items;
        }

        CompoundNBT blockEntityTag = Objects.requireNonNull(stack.getTag()).getCompound("BlockEntityTag");

        if (blockEntityTag.contains("Items", 9)) {
            ItemStackHelper.loadAllItems(blockEntityTag, items);
        }

        return items;
    }

    private boolean isShulkerBox(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ShulkerBoxBlock;
    }
}

