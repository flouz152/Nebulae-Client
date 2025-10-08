package beame.feature.ui;

import beame.util.Scissor;
import beame.util.animation.AnimationMath;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.MainWindow;
import net.minecraft.item.ItemStack;

import static beame.util.IMinecraft.mc;

public class ArmorDraw {
// leaked by itskekoff; discord.gg/sk3d eYMVLRS7
    public float animatedHeight = 0;
    public float animatedWidth = 0;

    public void render(MatrixStack stack) {
        MainWindow window = mc.getMainWindow();

        float x = window.getScaledWidth() / 2 +13.9f;
        float y = window.getScaledHeight() - 65;

        float armorCount = 0;

        for(ItemStack istack : mc.player.getArmorInventoryList()) {
            if(istack.isEmpty()) continue;
            armorCount+=1;
        }

        animatedWidth = AnimationMath.fast(animatedWidth, 70, 8);
        animatedHeight = AnimationMath.fast(animatedHeight, 16, 8);
        float width = animatedWidth;
        float height = animatedHeight;

        Scissor.push();
        Scissor.setFromComponentCoordinates(x - 2, y - 2, width + 4, height + 4);

        int posX = (int) x;
        int posY = (int) y;

        Scissor.unset();
        Scissor.setFromComponentCoordinates(x, y, width, height);

        for (ItemStack itemStack : mc.player.getArmorInventoryList()) {
            if(itemStack.isEmpty()) continue;

            mc.getItemRenderer().renderItemIntoGUI(itemStack, posX, posY);
            mc.getItemRenderer().renderItemOverlays(mc.fontRenderer, itemStack, posX, posY);

            posX += 18;
        }

        Scissor.unset();
        Scissor.pop();
    }
}
