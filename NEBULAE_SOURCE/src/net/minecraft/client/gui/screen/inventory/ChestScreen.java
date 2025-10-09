package net.minecraft.client.gui.screen.inventory;

import beame.Nebulae;
import beame.components.modules.misc.AutoBuyLogic.AutoBuySystem;
import beame.components.modules.misc.AutoBuyLogic.Items.BuyedItem;
import beame.feature.notify.NotificationManager;
import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import events.impl.EventStartPriceParsing;
import events.impl.EventStopPriceParsing;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static beame.util.IMinecraft.mc;

public class ChestScreen extends ContainerScreen<ChestContainer> implements IHasContainer<ChestContainer> {
// leaked by itskekoff; discord.gg/sk3d ghQsNA7D
    /**
     * The ResourceLocation containing the chest GUI texture.
     */
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");

    /**
     * Window height is calculated with these values; the more rows, the higher
     */
    private final int inventoryRows;

    private int msX = 0;
    private int msY = 0;

    public ChestScreen(ChestContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.passEvents = false;
        int i = 222;
        int j = 114;
        this.inventoryRows = container.getNumRows();
        this.ySize = 114 + this.inventoryRows * 18;
        this.playerInventoryTitleY = this.ySize - 94;
    }

    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        msX = mouseX;
        msY = mouseY;

        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderHoveredTooltip(matrixStack, mouseX, mouseY);
    }

    private List<ITextComponent> getBetterToolTip(ItemStack stack, LocalDateTime buyTime) {
        List<ITextComponent> startTT = this.getTooltipFromItem(stack);
        List<ITextComponent> newTT = new ArrayList<>();

        for(ITextComponent ITC : startTT){
            if(ITC.getString().contains("                    ") || ITC.getString().contains("Нажми") || ITC.getString().contains("Истекает:") || ITC.getString().contains("NBT:") || ITC.getString().contains("minecraft:"))
                continue;

            newTT.add(ITC);
            if(ITC.getString().contains("Продавец"))
                newTT.add(ITextComponent.getTextComponentOrEmpty(TextFormatting.AQUA + "⌚ " + TextFormatting.WHITE + "Время покупки: " + TextFormatting.AQUA + buyTime.getHour() + ":" + buyTime.getMinute() + ":" + buyTime.getSecond()));
        }

        return newTT;
    }

    public Button allToMeButton;
    public Button allToChestButton;
    float Animator = 0f;
    float scroll = 0;
    Button abbutton = null;

    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(matrixStack, i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.blit(matrixStack, i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);

        if(title.getString().contains("Аукционы") || title.getString().contains("Поиск") && Nebulae.getHandler().getModuleList().autoBuy.isState())  {
            boolean state = Nebulae.getHandler().getModuleList().autoBuy.isState();
            if(state) Animator = AnimationMath.fast(Animator, 1, 8);

            int abbx = i - 180;
            int abby = j + 33;

            buttons.clear();

            this.blit(matrixStack, abbx, abby + this.inventoryRows * 2 + 23, 0, 126, this.xSize, 96);
            this.blit(matrixStack, abbx, abby, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
            mc.fontRenderer.drawString(matrixStack, TextFormatting.RED + "[☄]" + TextFormatting.GOLD + " Покупки " + TextFormatting.RED + "[Nebulae]", abbx + 8, abby + 6, -1);
            mc.fontRenderer.drawStringWithShadow(matrixStack, TextFormatting.WHITE + "        Список можно листать", abbx, abby + (17 + (this.inventoryRows * 18 + 0.5f) + 5) + 6, -1);
            mc.fontRenderer.drawStringWithShadow(matrixStack, "" + TextFormatting.BLUE + TextFormatting.BOLD + TextFormatting.ITALIC + "DELETE" + TextFormatting.WHITE + ": очистить список", abbx + 17, abby + (17 + (this.inventoryRows * 18 + 0.5f) + 5) + 6 + 12, -1);
            //mc.fontRenderer.drawStringWithShadow(matrixStack, "" + TextFormatting.BLUE + TextFormatting.BOLD + TextFormatting.ITALIC + "INSERT" + TextFormatting.WHITE + ": авто-перевыставление", abbx + 4, abby + (17 + (this.inventoryRows * 18 + 0.5f) + 5) + 6 + 24, -1);

            int index = 0;
            int yindex = 0;

            List<BuyedItem> allItems = Nebulae.getHandler().autoBuy.getBuyedItems();
            List<BuyedItem> items = new ArrayList<>();
            if(allItems != null && !allItems.isEmpty()) {
                for(BuyedItem item : allItems) {
                    if(item.buyed) {
                        items.add(item);
                    }
                }
            }
            
            if(items != null && !items.isEmpty()) {
                Scissor.push();
                Scissor.setFromComponentCoordinates((int)(abbx + 6.5f), (int)(abby + 17), (int)(xSize - 13), (int)(this.inventoryRows * 18 + 0.5f));
                for (BuyedItem ab : items) {
                    if (index > 53) continue;

                    int itemX = abbx + 8 + (index * 18);
                    int itemY = abby + (int)scroll + 18 + (yindex * 18);
                    int itemW = 17;
                    try {
                        ItemStack stack = ab.ahItem;
                        stack.setCount(ab.count);
                        mc.getItemRenderer().renderItemAndEffectIntoGUI(stack, itemX, itemY);
                        mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, stack, itemX, itemY);

                        int color = !ab.buyed ? ColorUtils.getColor(255, 0, 0, 150) : ClientHandler.isInRegion(msX, msY, itemX, itemY, itemW, itemW) ? -2130706433 : ColorUtils.getColor(0, 0, 0, 0);
                        ClientHandler.drawRound((float) itemX, (float) itemY, (float) 16, (float) 17, 0, color);
                    } catch (Exception ex) {
                    }

                    index += 1;
                    if (index == 9) {
                        yindex += 1;
                        index = 0;
                    }
                }
                Scissor.unset();
                Scissor.pop();

                index = 0;
                yindex = 0;
                for (BuyedItem ab : items) {
                    if (index > 53)
                        continue;

                    int itemX = abbx + 8 + (index * 18);
                    int itemY = abby + (int)scroll + 18 + (yindex * 18);
                    int itemW = 17;
                    try {
                        if (ClientHandler.isInRegion(msX, msY, itemX, itemY, itemW, itemW)) {
                            this.func_243308_b(matrixStack, this.getBetterToolTip(ab.ahItem, ab.buyTime), msX, msY);
                        }
                    } catch (Exception ex) {
                    }

                    index += 1;
                    if (index == 9) {
                        yindex += 1;
                        index = 0;
                    }
                }
            }

            abbutton = new Button(i + 35, j - 25, 110, 20, ITextComponent.getTextComponentOrEmpty((Nebulae.getHandler().autoBuy.isEnabled() ? TextFormatting.GREEN : TextFormatting.RED) + "Autobuy: " + (Nebulae.getHandler().autoBuy.isEnabled() ? "включен" : "выключен")), (drawAutobuyButton) -> {
                if (!Nebulae.getHandler().autoBuy.isEnabled()) {
                    Nebulae.getHandler().autoBuy.enable();
                } else {
                    Nebulae.getHandler().autoBuy.disable();
                }
            });
            if (!buttons.contains(abbutton))
                addButton(abbutton);

            Button parseButton = new Button(i + 180, j + 5, 110, 20, ITextComponent.getTextComponentOrEmpty(
                    (Nebulae.getHandler().autoBuy.priceParser.isParsing() ? TextFormatting.RED : TextFormatting.WHITE) +
                            (Nebulae.getHandler().autoBuy.priceParser.isParsing() ? "Выключить парсер" : "Спарсить цены")
            ), (parsePricesButton) -> {
                if (Nebulae.getHandler().autoBuy.priceParser.isParsing()) {
                    Nebulae.getHandler().getModuleList().autoBuy.event(new EventStopPriceParsing());
                    Nebulae.getHandler().getModuleList().autoBuy.parser.set(false);
                } else {
                    Nebulae.getHandler().getModuleList().autoBuy.event(new EventStartPriceParsing());
                    Nebulae.getHandler().getModuleList().autoBuy.parser.set(true);
                }
            });
            if (!buttons.contains(parseButton))
                addButton(parseButton);
        }
    }


    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        scroll += (float) (delta * 18);

        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(title.getString().contains("Аукционы") || title.getString().contains("Поиск")) {
            int insert = 260;
            int delete = 261;
            if (keyCode == delete) {
                System.out.println("Clear list");
                Nebulae.getHandler().autoBuy.clearBuyedItems();
            } else if (keyCode == insert) {
                // KomaruBuy.getHandler().notificationManager.pushNotify("Авто перевыставление " + (!KomaruBuy.getHandler().autobuy.enableReroll ? "включено" : "выключено"), KomaruBuy.getHandler().autobuy.enableReroll ? NotificationManager.Type.Off : NotificationManager.Type.On);
                // KomaruBuy.getHandler().autobuy.enableReroll = !KomaruBuy.getHandler().autobuy.enableReroll;
                //KomaruBuy.getHandler().autobuy.resetReroll();
            }
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

