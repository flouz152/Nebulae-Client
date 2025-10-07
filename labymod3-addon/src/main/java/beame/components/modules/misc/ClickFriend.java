package beame.components.modules.misc;

import beame.Essence;
import events.Event;
import events.EventKey;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.entity.LivingEntity;
import beame.setting.SettingList.BindSetting;

public class ClickFriend extends Module {
// leaked by itskekoff; discord.gg/sk3d 6iCCJhWW
    public ClickFriend() {
        super("ClickFriend", Category.Misc, true, "Позволяет добавлять друзей по кнопке");
        addSettings(bind);
    }

    public BindSetting bind = new BindSetting("Добавить в друзья", 0);

    @Override
    public void event(Event event) {
        if (event instanceof EventKey) {
            if (((EventKey) event).key == bind.get() && mc.pointedEntity instanceof LivingEntity) {
                String entityName = mc.pointedEntity.getName().getString();
                if (Essence.getHandler().getFriends().isFriend(entityName)) {
                    Essence.getHandler().getFriends().remFriend(entityName);
                  //  AbstractCommand.addMessage("Удалил " + entityName + " из друзей!");
                } else {
                    Essence.getHandler().getFriends().addFriend(entityName);
                  // AbstractCommand.addMessage("Добавил " + entityName + " в друзья!");
                }
            }
        }
    }
}
