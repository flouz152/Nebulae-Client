package beame.components.modules.combat;

import events.Event;
import events.impl.player.EntityHitBoxEvent;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import beame.setting.SettingList.SliderSetting;

public class HitBox extends Module {
// leaked by itskekoff; discord.gg/sk3d GF0gRtky

    private final SliderSetting size = new SliderSetting("Размер", 0.3F, 0F, 2F, 0.05F);

    public HitBox() {
        super("HitBox", Category.Combat, true, "Расширяет области удара энтити");
        addSettings(size);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EntityHitBoxEvent e) {
            if (!(e.getEntity() instanceof LivingEntity)) return;
            if (e.getEntity() instanceof PlayerEntity)
                e.setSize(size.get());
            if (e.getEntity() instanceof MobEntity) e.setSize(size.get());
        }
    }
}