package beame.components.modules.misc.AutoBuyLogic.GUI;

import beame.Nebulae;
import beame.components.modules.misc.AutoBuyLogic.Items.AutoBuyItemClass;
import beame.feature.notify.NotificationManager;
import beame.screens.api.NebulaeTextBox;
import beame.util.ClientHelper;
import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.render.ClientHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;

import static beame.util.IMinecraft.mc;
import static beame.util.color.ColorUtils.interpolateColor;

public class AutoBuyScreen extends Screen {
// leaked by itskekoff; discord.gg/sk3d rKZj1GeH
    public AutoBuyScreen(ITextComponent titleIn) {
        super(titleIn);
    }

    public float x = 0, y = 0, width = 445, height = 325;
    public int server = 1;
    public float animServer1 = 0, animServer2 = 0;
    public int selectedIndex = -1;
    private AutoBuyItemClass selectedItem = null;
    public boolean hoveredFT = false, hoveredST = false, hoveredSave = false, hoveredFound = false;

    public NebulaeTextBox input;
    private int prevSelectedIndex = -1;

    int counter = 0;

    @Override
    public void tick() {
        super.tick();

        width = 255;
        int N = Nebulae.getHandler().autoBuy.items.list.size();
        int rows = (int) Math.ceil(N / 11.0);
        height = 80 + rows * 22 + 60;
        x = (float) mc.getMainWindow().getScaledWidth() / 2 - width / 2;
        y = (float) mc.getMainWindow().getScaledHeight() / 2 - height / 2;

        if (input != null && selectedIndex != -1) {
            input.x = (int) (x + 14);
            input.y = (int) (y + height - 29);

            input.tick();

            if (Math.abs(input.y - (y + height - 29)) > 5) {
                String currentText = input.getText();
                boolean wasFocused = input.isFocused();
                buttons.remove(input);
                input = new NebulaeTextBox(mc.fontRenderer, (int) (x + 14), (int) (y + height - 29), 133, 15, ITextComponent.getTextComponentOrEmpty(currentText));
                if (wasFocused) {
                    input.setFocused2(true);
                }
                addButton(input);
            }
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        ClientHandler.drawSexyRect(x, y, width, height, 5, false);
        counter++;

        if (selectedIndex != -1) {
            ClientHandler.drawSexyRect(x + 8, y + height - 17 - 20 - 8, 145, 37, 3, false);
            Fonts.ESSENCE_ICONS.get(16).drawString("t", x + 15, y + height - 36.5f, ColorUtils.getColor(120));
            Fonts.SF_BOLD.get(14).drawString("Стоимость покупки:", x + 15 + 10, y + height - 37f, ColorUtils.getColor(120));

            int inputX = (int) (x + 14);
            int inputY = (int) (y + height - 29);
            int inputW = 133;
            int inputH = 15;

            if (input == null) {
                String initialText = formatNumber(String.valueOf(Nebulae.getHandler().autoBuy.items.list.get(selectedIndex).buyPrice));
                input = new NebulaeTextBox(mc.fontRenderer, inputX, inputY, inputW, inputH, ITextComponent.getTextComponentOrEmpty(initialText));
                addButton(input);
            } else if (selectedIndex != prevSelectedIndex) {
                String newText = formatNumber(String.valueOf(Nebulae.getHandler().autoBuy.items.list.get(selectedIndex).buyPrice));
                input.setText(newText);
            }
            prevSelectedIndex = selectedIndex;

            ClientHandler.drawRound(inputX, inputY, inputW, inputH, 3, ColorUtils.rgba(10, 10, 10, 50));
            if (buttons.isEmpty() && input != null) {
                addButton(input);
            }

            float textX = x + width - 90 + (90 / 2) - (Fonts.SF_BOLD.get(14).getWidth("Сохранить") / 2) - 10 + 4;
            ClientHandler.drawSexyRect(x + width - 90 - 8, y + height - 17 - 8, 90, 17, 3, true);
            hoveredSave = ClientHandler.isInRegion(mouseX, mouseY, (int) (x + width - 90 - 8), (int) (y + height - 17 - 8), 90, 17);
            Fonts.SF_BOLD.get(14).drawString("Сохранить", textX, y + height - 18, hoveredSave ? -1 : ColorUtils.getColor(120));
            Fonts.ESSENCE_ICONS.get(16).drawString("q", textX - 10, y + height - 17.5f, hoveredSave ? -1 : ColorUtils.getColor(120));

            textX = x + width - 90 + (90 / 2) - (Fonts.SF_BOLD.get(14).getWidth("Найти на /ah") / 2) - 10 + 4;
            ClientHandler.drawSexyRect(x + width - 90 - 8, y + height - 17 - 20 - 8, 90, 17, 3, true);
            hoveredFound = ClientHandler.isInRegion(mouseX, mouseY, (int) (x + width - 90 - 8), (int) (y + height - 20 - 17 - 8), 90, 17);
            Fonts.SF_BOLD.get(14).drawString("Найти на /ah", textX, y + height - 20 - 17.5f, hoveredFound ? -1 : ColorUtils.getColor(120));
            Fonts.ESSENCE_ICONS.get(16).drawString("g", textX - 10, y + height - 20 - 17.5f, hoveredFound ? -1 : ColorUtils.getColor(120));
        }

        float textPos = 12;
        String title = "Nebulae || AutoBuy";
        ClientHandler.drawSexyRect(x + textPos - 4, y + textPos - 4, Fonts.SF_BOLD.get(14).getStringWidth(title) + 23, 15, 3, false);
        Fonts.ESSENCE_ICONS.get(18).drawString("u", x + textPos, y + 2 + textPos, Nebulae.getHandler().themeManager.getThemeColor(0));
        Fonts.SF_BOLD.get(14).drawString(title, x + textPos + 12, y + 2 + textPos, ColorUtils.getColor(120));

        {
            float tWidth = Fonts.SF_BOLD.get(14).getStringWidth("SpookyTime");
            float tWidth2 = Fonts.SF_BOLD.get(14).getStringWidth("Тест");

            animServer1 = AnimationMath.fast(animServer1, server, 8);
            animServer2 = AnimationMath.fast(animServer2, server == 0 ? tWidth : tWidth2, 12);

            ClientHandler.drawSexyRect(x + width - 16 - tWidth - tWidth2 - 12 - 4, y + textPos - 4, tWidth2 + tWidth + 12 + 12, 15, 3, false);
            ClientHandler.drawSexyRect(x + width - 16 - tWidth - ((tWidth2 * animServer1) + (12 * animServer1)) - 2, y + textPos - 4 + 2, animServer2 + 12 - 4, 15 - 4, 2, true);

            Fonts.SF_BOLD.get(14).drawString("SpookyTime", x + width - 15 - tWidth, y + 2 + textPos, -1);
            Fonts.SF_BOLD.get(14).drawString("Тест", x + width - 15 - tWidth - tWidth2 - 12, y + 2 + textPos, -1);
            hoveredST = ClientHandler.isInRegion(mouseX, mouseY, (int) (x + width - 15 - tWidth), (int) (y + 2 + textPos), (int) (tWidth + 8), 15 - 4);
            hoveredFT = ClientHandler.isInRegion(mouseX, mouseY, (int) (x + width - 15 - tWidth - tWidth2 - 12), (int) (y + 2 + textPos), (int) (tWidth2 + 8), 15 - 4);
        }

        int index = 0;
        int yIndex = 0;
        for (AutoBuyItemClass item : Nebulae.getHandler().autoBuy.items.list) {
            int itemX = (int) (x + 10f + (index * 22f));
            int itemY = (int) (y + 35f + (yIndex * 22f));

            int fullIndex = index + (yIndex * 11);
            boolean selected = selectedIndex == fullIndex;
            item.selectAnimation = AnimationMath.fast(item.selectAnimation, selected ? 1 : 0, 8);

            boolean hasPrice = item.buyPrice > 0;
            boolean hasParsing = item.isParsingEnabled;

            item.priceAnimation = AnimationMath.fast(item.priceAnimation, hasPrice ? 1 : 0, 3);

            float parsingAnimation = AnimationMath.fast(item.color1Animation, hasParsing ? 1 : 0, 3);

            int clr = (int) (10 + (35 * item.hoverAnimation));

            int baseColor = ColorUtils.rgba(255, 0, 0, 255);

            int priceColor = ColorUtils.rgba(0, 255, 0, 255);
            

            int parsingColor = ColorUtils.rgba(128, 0, 255, 255);

            int finalColor = baseColor;
            
            if (hasPrice && hasParsing) {
                finalColor = interpolateColor(priceColor, parsingColor, 0.5f);
            } else if (hasPrice) {
                finalColor = priceColor;
            } else if (hasParsing) {
                finalColor = parsingColor;
            }
            
            item.color1Animation = interpolateColor((int) item.color1Animation, finalColor, 0.1f);
            int clr0 = (int) item.color1Animation;

            int color2 = selected ? ColorUtils.rgba(255, 255, 0, 255) : ColorUtils.rgba(clr, clr, clr, 255);
            item.color2Animation = interpolateColor((int) item.color2Animation, color2, item.selectAnimation);

            int clr2 = (int) item.color2Animation;
            ClientHandler.drawGradientRound(itemX - 2, itemY - 2, 20, 20, 3, clr0, clr2, clr2, clr2);
            item.render(itemX, itemY, mouseX, mouseY);

            index += 1;
            if (index % 11 == 0) {
                yIndex += 1;
                index = 0;
            }
        }

        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredFT) {
            server = 1;
        } else if (hoveredST) {
            server = 0;
        } else if (hoveredSave) {
            if (input != null) {
                Nebulae.getHandler().autoBuy.items.setPrice(Nebulae.getHandler().autoBuy.items.list.get(selectedIndex), input.text);
            }
        } else if (hoveredFound) {
            if (selectedIndex != -1) {
                String item = Nebulae.getHandler().autoBuy.items.list.get(selectedIndex).displayName;
                mc.player.sendChatMessage("/ah search " + item);
            }
        } else {
            int index = 0;
            int yIndex = 0;
            for (AutoBuyItemClass item : Nebulae.getHandler().autoBuy.items.list) {
                int itemX = (int) (x + 10f + (index * 22f));
                int itemY = (int) (y + 35f + (yIndex * 22f));

                boolean hovered = ClientHandler.isInRegion((int) mouseX, (int) mouseY, itemX - 2, itemY - 2, 20, 20);
                if (hovered) {
                    if (button == 0) {
                        selectedIndex = index + (yIndex * 11);
                        AutoBuyItemClass originalItem = Nebulae.getHandler().autoBuy.items.list.get(selectedIndex);
                        if (ClientHelper.isConnectedToServer("spookytime")) {
                            selectedItem = new AutoBuyItemClass(originalItem.buyPrice, originalItem.item, originalItem.spookyItemType, originalItem.texture);
                        } else {
                            selectedItem = new AutoBuyItemClass(originalItem.buyPrice, originalItem.item, originalItem.attributes);
                        }
                    } else if (button == 1) {
                        AutoBuyItemClass itemToReset = Nebulae.getHandler().autoBuy.items.list.get(index + (yIndex * 11));
                        itemToReset.buyPrice = 0;
                        Nebulae.getHandler().autoBuy.savePrices();
                        Nebulae.getHandler().notificationManager.pushNotify("Покупка " + itemToReset.displayName + " отменена.", NotificationManager.Type.Info);
                    } else if (button == 2) {
                        AutoBuyItemClass itemToToggle = Nebulae.getHandler().autoBuy.items.list.get(index + (yIndex * 11));
                        itemToToggle.isParsingEnabled = !itemToToggle.isParsingEnabled;
                        Nebulae.getHandler().autoBuy.savePrices();
                        String status = itemToToggle.isParsingEnabled ? "включен" : "отключен";
                        Nebulae.getHandler().notificationManager.pushNotify("Парсинг цен для " + itemToToggle.displayName + " " + status, NotificationManager.Type.Info);
                    }
                }

                index += 1;
                if (index % 11 == 0) {
                    yIndex += 1;
                    index = 0;
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public static String formatNumber(String input) {
        try {
            String clean = input.replaceAll("[^\\d]", "");
            if (clean.isEmpty()) return "";

            long parsed = Long.parseLong(clean);
            return String.format("%,d", parsed).replace(',', ',');
        } catch (Exception e) {
            return input;
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (input != null && input.isFocused()) {
            return input.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (input != null && input.isFocused()) {
            return input.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        if (input != null) {
            input.setFocused2(false);
        }
        super.onClose();
    }
}