package beame.util.render;

import beame.util.animation.AnimationMath;
import beame.util.color.ColorUtils;
import beame.util.fonts.Fonts;
import beame.util.math.TimerUtil;
import com.mojang.blaze3d.matrix.MatrixStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static beame.util.IMinecraft.mc;

public class SnowflakeSystem {
// leaked by itskekoff; discord.gg/sk3d l3Y1oqhs
    public SnowflakeSystem() {}

    class Snowflake {
        public float x, y, size;
        public Snowflake(float x, float y, float size) { this.x = x; this.y = y; this.size = size; }
    }

    int snowflake_count = 599;
    public List<Snowflake> sfs = new ArrayList<>();

    public float mouseX = 0;

    private void renderSnowflake(MatrixStack matrixStack, Snowflake snowflake) {
        Fonts.SUISSEINTL.get(snowflake.size).drawString(matrixStack, "*", snowflake.x, snowflake.y, ColorUtils.rgba(255, 255, 255, 255));
    }

    private void createSnowflakeList() {
        if(sfs.size() > snowflake_count) return;

        sfs.add(new Snowflake(new Random().nextInt(-150, mc.getMainWindow().scaledWidth()), new Random().nextInt(-482, 33), new Random().nextInt(15, 22)));
    }

    private void snowflakeLogic(MatrixStack matrixStack) {
        if(sfs.isEmpty()) return;

        float move = (mouseX * 2) / mc.getMainWindow().getWidth();
        if((mouseX * 2) < (mc.getMainWindow().getWidth() / 2)) { move = ((mouseX * 2) - (mc.getMainWindow().getWidth() / 2)) / (mc.getMainWindow().getWidth() / 2); };

        for (Snowflake snow : sfs) {
            snow.y = AnimationMath.fast(snow.y, mc.getMainWindow().getScaledHeight() + 150, 0.2f);
            snow.x = AnimationMath.fast(snow.x, snow.x + (150 * move), 0.2f);
        }

        for(int index = 0;index< sfs.size();index++){
            Snowflake snow = sfs.get(index);

            if (snow.y >= (mc.getMainWindow().getScaledHeight() + 10) || snow.x > (mc.getMainWindow().getScaledWidth() + 10) || snow.x < -10) sfs.remove(snow);
            renderSnowflake(matrixStack, snow);
        }
    }

    public void renderSnow(MatrixStack matrixStack) {
        createSnowflakeList();
        snowflakeLogic(matrixStack);
    }
}
