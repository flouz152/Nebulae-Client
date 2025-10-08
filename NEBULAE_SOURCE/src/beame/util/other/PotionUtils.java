package beame.util.other;

import beame.util.math.MathUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.MathHelper;

public class PotionUtils {
// leaked by itskekoff; discord.gg/sk3d d9rhmejF
    public String getAmplifer(EffectInstance f) {
        String amplifer = "";

        if (f.getAmplifier() == 1) {
            amplifer = "2";
        }
        if (f.getAmplifier() == 2) {
            amplifer = "3";
        }
        if (f.getAmplifier() == 3) {
            amplifer = "4";
        }
        if (f.getAmplifier() == 4) {
            amplifer = "5";
        }
        if (f.getAmplifier() == 5) {
            amplifer = "6";
        }
        if (f.getAmplifier() == 6) {
            amplifer = "7";
        }
        if (f.getAmplifier() == 7) {
            amplifer = "8";
        }
        if (f.getAmplifier() == 8) {
            amplifer = "9";
        }
        if (f.getAmplifier() == 9) {
            amplifer = "10";
        }

        return amplifer;
    }

    public static String getPotionDurationString(EffectInstance effect, float durationFactor)
    {
        if (effect.getIsPotionDurationMax())
        {
            return "**:**";
        }
        else
        {
            int i = MathHelper.floor((float)effect.getDuration() * durationFactor);
            return StringUtils.ticksToElapsedTime(i);
        }
    }

    public static boolean hasMiningSpeedup(LivingEntity entity)
    {
        return entity.isPotionActive(Effects.HASTE) || entity.isPotionActive(Effects.CONDUIT_POWER);
    }

    public static int getMiningSpeedup(LivingEntity entity)
    {
        int i = 0;
        int j = 0;

        if (entity.isPotionActive(Effects.HASTE))
        {
            i = entity.getActivePotionEffect(Effects.HASTE).getAmplifier();
        }

        if (entity.isPotionActive(Effects.CONDUIT_POWER))
        {
            j = entity.getActivePotionEffect(Effects.CONDUIT_POWER).getAmplifier();
        }

        return Math.max(i, j);
    }

    public static boolean canBreatheUnderwater(LivingEntity entity)
    {
        return entity.isPotionActive(Effects.WATER_BREATHING) || entity.isPotionActive(Effects.CONDUIT_POWER);
    }
}
