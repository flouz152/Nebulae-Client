package beame.components.modules.misc;


import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;


public class RenderHook {
// leaked by itskekoff; discord.gg/sk3d lIsQrZ5H
    

    private static boolean disablePlayerGlowing = false;
    

    public static void setDisablePlayerGlowing(boolean disable) {
        disablePlayerGlowing = disable;
    }
    

    public static boolean isPlayerGlowingDisabled() {
        return disablePlayerGlowing;
    }

    public static boolean shouldRenderEntityGlowing(Entity entity) {
        if (disablePlayerGlowing && entity instanceof PlayerEntity) {
            return false;
        }

        return entity.isGlowing();
    }
    

    public static boolean shouldUseOutlineRenderType(Entity entity, boolean originalResult) {
        if (disablePlayerGlowing && entity instanceof PlayerEntity) {
            return false;
        }
        
        return originalResult;
    }
} 