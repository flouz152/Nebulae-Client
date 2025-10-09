package beame.feature.ui;

import beame.Nebulae;
import beame.util.animation.AnimationMath;
import beame.util.drag.Dragging;
import com.mojang.blaze3d.matrix.MatrixStack;

import java.util.ArrayList;

import static beame.util.IMinecraft.mc;

public class MotionGraphDraw {
// leaked by itskekoff; discord.gg/sk3d SJtsy3bx
    public Dragging motionGraph = Nebulae.getHandler().createDraggable("mgraph", 240, 80);

    ArrayList<Float> motiongraph = new ArrayList<>();
    float motionBps = 0;

    public void motion() {
        float bps = (float) (Math.hypot(mc.player.getPosX() - mc.player.prevPosX, mc.player.getPosZ() - mc.player.prevPosZ) * 20.0D);
        this.motionBps = AnimationMath.fast(this.motionBps,bps,100);
        motiongraph.add(this.motionBps);
        if (motiongraph.size() >= 100) {
            motiongraph.remove(0);
        }
    }

    public void render(MatrixStack stack) {

    }
}
