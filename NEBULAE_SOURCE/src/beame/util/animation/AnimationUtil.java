package beame.util.animation;

import beame.util.math.MathUtil;
import net.optifine.util.MathUtils;

public class AnimationUtil {
// leaked by itskekoff; discord.gg/sk3d yvMFBFF5

    long mc;
    float anim;
    float anim2;
    public float to;
    public float speed;
    public AnimationUtil(float anim, float to, float speed){
        this.anim = anim;
        this.to = to;
        this.speed = speed;
        mc = System.currentTimeMillis();
    }
    public float getAnim() {
        int count = (int) ((System.currentTimeMillis() - mc) / 5);
        if (count > 0){
            mc = System.currentTimeMillis();
        }
        for (int i = 0; i < count; i++) {
            anim = MathUtil.lerp(anim, to, speed);
        }
        return anim;

    }

    public static float harp(float val, float current, float speed) {
        float emi = ((current - val) * (speed/2)) > 0 ? Math.max((speed), Math.min(current - val, ((current - val) * (speed/2)))) : Math.max(current - val, Math.min(-(speed/2), ((current - val) * (speed/2))));
        return val + emi;
    }

    public float getAnimHarp() {
        int count = (int) ((System.currentTimeMillis() - mc) / 5);
        if (count > 0){
            mc = System.currentTimeMillis();
        }
        for (int i = 0; i < count; i++) {
            anim = harp(anim, to, speed);
        }
        return anim;

    }
    public void reset(){
        anim = 0;
        to = 0;
        anim2 = 0;
        mc = System.currentTimeMillis();
    }


    public void setAnim(float anim) {
        this.anim = anim;
        this.anim2 = anim;
        mc = System.currentTimeMillis();
    }
}
