package net.minecraft.client.gui.screen.inventory;

import beame.Essence;
import beame.util.IMinecraft;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DisplayEffectsScreen;
import net.minecraft.client.gui.recipebook.IRecipeShownListener;
import net.minecraft.client.gui.recipebook.RecipeBookGui;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ImageButton;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Random;

public class InventoryScreen extends DisplayEffectsScreen<PlayerContainer> implements IRecipeShownListener, IMinecraft {
// leaked by itskekoff; discord.gg/sk3d kzyQquJH
    private static final ResourceLocation RECIPE_BUTTON_TEXTURE = new ResourceLocation("textures/gui/recipe_button.png");

    /**
     * The old x position of the mouse pointer
     */
    private float oldMouseX;



    @Setter
    @Getter
    private boolean drop = false;

    /**
     * The old y position of the mouse pointer
     */
    private float oldMouseY;
    private final RecipeBookGui recipeBookGui = new RecipeBookGui();
    private boolean removeRecipeBookGui;
    private boolean widthTooNarrow;
    private boolean buttonClicked;

    public InventoryScreen(PlayerEntity player) {
        super(player.container, player.inventory, new TranslationTextComponent("container.crafting"));
        this.passEvents = true;
        this.titleX = 97;
    }

    public void tick() {
        if(isDrop()) dropItems();
        if (this.minecraft.playerController.isInCreativeMode()) {
            this.minecraft.displayGuiScreen(new CreativeScreen(this.minecraft.player));
        } else {
            this.recipeBookGui.tick();
        }
    }

    protected void init() {
        assert this.minecraft != null;
        assert this.minecraft.playerController != null;
        if (this.minecraft.playerController.isInCreativeMode()) {
            assert this.minecraft.player != null;
            this.minecraft.displayGuiScreen(new CreativeScreen(this.minecraft.player));
        } else {
            this.addButton(new Button((int) (width / 2f - 50),height / 2 - 105,100,20, new StringTextComponent("Выбросить всё"), button -> {
                if (mc.player != null && mc.playerController != null) {
                    setDrop(true);
                }
            }));
            super.init();
            this.widthTooNarrow = this.width < 379;
            this.recipeBookGui.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.container);
            this.removeRecipeBookGui = true;
            this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
            this.children.add(this.recipeBookGui);
            this.setFocusedDefault(this.recipeBookGui);
            this.addButton(new ImageButton(this.guiLeft + 104, this.height / 2 - 22, 20, 18, 0, 0, 19, RECIPE_BUTTON_TEXTURE, (button) ->
            {
                this.recipeBookGui.initSearchBar(this.widthTooNarrow);
                this.recipeBookGui.toggleVisibility();
                this.guiLeft = this.recipeBookGui.updateScreenPosition(this.widthTooNarrow, this.width, this.xSize);
                ((ImageButton) button).setPosition(this.guiLeft + 104, this.height / 2 - 22);
                this.buttonClicked = true;
            }));
        }
    }


    private long time = System.currentTimeMillis();

    public void dropItems() {
        java.util.List<Integer> list = new ArrayList<>();
        java.util.List<Integer> list2 = new ArrayList<>();
        java.util.List<Integer> list3 = new ArrayList<>();

        int totalSlots = this.container.getInventory().size();

        for (int index = 0; index < totalSlots && mc.currentScreen == this; ++index) {
            if (new Random().nextBoolean()) {
                list.add(index);
                continue;
            }

            if (container.getSlot((index)).getStack().getItem() != Item.getItemById(0) && (System.currentTimeMillis() - time) >= random(3.5f, 5)) {
                mc.playerController.windowClick(0, index, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, mc.player);
                time = System.currentTimeMillis();
            }
        }

        for (int i = 0; i < list.size(); i++) {
            if (new Random().nextBoolean()) {
                list2.add(i);
                continue;
            }

            if (container.getSlot(list.get(i)).getStack().getItem() != Item.getItemById(0) && (System.currentTimeMillis() - time) >= random(3.5f, 5)) {
                mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, mc.player);
                time = System.currentTimeMillis();
            }
        }

        for (int i = 0; i < list2.size(); i++) {
            if (new Random().nextBoolean()) {
                list3.add(i);
                continue;
            }

            if (container.getSlot(list2.get(i)).getStack().getItem() != Item.getItemById(0) && (System.currentTimeMillis() - time) >= random(3.5f, 5)) {
                mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, mc.player);
                time = System.currentTimeMillis();
            }
        }

        for (int i = 0; i < list3.size(); i++) {
            if (container.getSlot(list3.get(i)).getStack().getItem() != Item.getItemById(0) && (System.currentTimeMillis() - time) >= random(3.5f, 5)) {
                mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
                mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, mc.player);
                time = System.currentTimeMillis();
            }
        }

        setDrop(true);
    }

    public static float random(float min, float max) {
        return (float) (Math.random() * (max - min) + min);
    }

    protected void drawGuiContainerForegroundLayer(MatrixStack matrixStack, int x, int y) {
        this.font.func_243248_b(matrixStack, this.title, (float) this.titleX, (float) this.titleY, 4210752);
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);

        this.hasActivePotionEffects = !this.recipeBookGui.isVisible();

        if (this.recipeBookGui.isVisible() && this.widthTooNarrow) {
            this.drawGuiContainerBackgroundLayer(matrixStack, partialTicks, mouseX, mouseY);
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookGui.render(matrixStack, mouseX, mouseY, partialTicks);
            super.render(matrixStack, mouseX, mouseY, partialTicks);
            this.recipeBookGui.func_230477_a_(matrixStack, this.guiLeft, this.guiTop, false, partialTicks);
        }



        this.recipeBookGui.func_238924_c_(matrixStack, this.guiLeft, this.guiTop, mouseX, mouseY);
        this.oldMouseX = (float) mouseX;
        this.oldMouseY = (float) mouseY;

        if (hoveredSlot != null && hoveredSlot.getHasStack()) {
            ItemStack stack = hoveredSlot.getStack();
            if (!(stack.getItem() == Items.SHULKER_BOX || stack.getItem() == Items.BLUE_SHULKER_BOX || stack.getItem() == Items.BLACK_SHULKER_BOX || stack.getItem() == Items.YELLOW_SHULKER_BOX || stack.getItem() == Items.BROWN_SHULKER_BOX || stack.getItem() == Items.CYAN_SHULKER_BOX || stack.getItem() == Items.GRAY_SHULKER_BOX || stack.getItem() == Items.LIGHT_BLUE_SHULKER_BOX || stack.getItem() == Items.LIGHT_GRAY_SHULKER_BOX || stack.getItem() == Items.GREEN_SHULKER_BOX || stack.getItem() == Items.LIME_SHULKER_BOX || stack.getItem() == Items.MAGENTA_SHULKER_BOX || stack.getItem() == Items.ORANGE_SHULKER_BOX || stack.getItem() == Items.PINK_SHULKER_BOX || stack.getItem() == Items.PURPLE_SHULKER_BOX || stack.getItem() == Items.WHITE_SHULKER_BOX)) {
                this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
            } else {
                if (!Essence.getHandler().getModuleList().getShulkerView().isState()) {
                    this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
                }
            }
        }


    }

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        assert this.minecraft != null;
        this.minecraft.getTextureManager().bindTexture(INVENTORY_BACKGROUND);
        int i = this.guiLeft;
        int j = this.guiTop;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.ySize);
        drawEntityOnScreen(i + 51, j + 75, 30, (float) (i + 51) - this.oldMouseX, (float) (j + 75 - 50) - this.oldMouseY, this.minecraft.player);
    }

    public static void drawEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity livingEntity) {
        float f = (float) Math.atan((double) (mouseX / 40.0F));
        float f1 = (float) Math.atan((double) (mouseY / 40.0F));
        RenderSystem.pushMatrix();
        RenderSystem.translatef((float) posX, (float) posY, 1050.0F);
        RenderSystem.scalef(1.0F, 1.0F, -1.0F);
        MatrixStack matrixstack = new MatrixStack();
        matrixstack.translate(0.0D, 0.0D, 1000.0D);
        matrixstack.scale((float) scale, (float) scale, (float) scale);
        Quaternion quaternion = Vector3f.ZP.rotationDegrees(180.0F);
        Quaternion quaternion1 = Vector3f.XP.rotationDegrees(f1 * 20.0F);
        quaternion.multiply(quaternion1);
        matrixstack.rotate(quaternion);
        float f2 = livingEntity.renderYawOffset;
        float f3 = livingEntity.rotationYaw;
        float f4 = livingEntity.rotationPitch;
        float f5 = livingEntity.prevRotationYawHead;
        float f6 = livingEntity.rotationYawHead;
        livingEntity.renderYawOffset = 180.0F + f * 20.0F;
        livingEntity.rotationYaw = 180.0F + f * 40.0F;
        livingEntity.rotationPitch = -f1 * 20.0F;
        livingEntity.rotationYawHead = livingEntity.rotationYaw;
        livingEntity.prevRotationYawHead = livingEntity.rotationYaw;
        EntityRendererManager entityrenderermanager = Minecraft.getInstance().getRenderManager();
        quaternion1.conjugate();
        entityrenderermanager.setCameraOrientation(quaternion1);
        entityrenderermanager.setRenderShadow(false);
        IRenderTypeBuffer.Impl irendertypebuffer$impl = Minecraft.getInstance().getRenderTypeBuffers().getBufferSource();
        RenderSystem.runAsFancy(() ->
        {
            entityrenderermanager.renderEntityStatic(livingEntity, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, matrixstack, irendertypebuffer$impl, 15728880);
        });
        irendertypebuffer$impl.finish();
        entityrenderermanager.setRenderShadow(true);
        livingEntity.renderYawOffset = f2;
        livingEntity.rotationYaw = f3;
        livingEntity.rotationPitch = f4;
        livingEntity.prevRotationYawHead = f5;
        livingEntity.rotationYawHead = f6;
        RenderSystem.popMatrix();
    }

    protected boolean isPointInRegion(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookGui.isVisible()) && super.isPointInRegion(x, y, width, height, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.recipeBookGui.mouseClicked(mouseX, mouseY, button)) {
            this.setListener(this.recipeBookGui);
            return true;
        } else {
            return this.widthTooNarrow && this.recipeBookGui.isVisible() ? false : super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (this.buttonClicked) {
            this.buttonClicked = false;
            return true;
        } else {
            return super.mouseReleased(mouseX, mouseY, button);
        }
    }

    protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeftIn, int guiTopIn, int mouseButton) {
        boolean flag = mouseX < (double) guiLeftIn || mouseY < (double) guiTopIn || mouseX >= (double) (guiLeftIn + this.xSize) || mouseY >= (double) (guiTopIn + this.ySize);
        return this.recipeBookGui.func_195604_a(mouseX, mouseY, this.guiLeft, this.guiTop, this.xSize, this.ySize, mouseButton) && flag;
    }

    /**
     * Called when the mouse is clicked over a slot or outside the gui.
     */
    protected void handleMouseClick(Slot slotIn, int slotId, int mouseButton, ClickType type) {
        super.handleMouseClick(slotIn, slotId, mouseButton, type);
        this.recipeBookGui.slotClicked(slotIn);
    }

    public void recipesUpdated() {
        this.recipeBookGui.recipesUpdated();
    }

    public void onClose() {
        if (this.removeRecipeBookGui) {
            this.recipeBookGui.removed();
        }

        super.onClose();
    }

    public RecipeBookGui getRecipeGui() {
        return this.recipeBookGui;
    }
}
