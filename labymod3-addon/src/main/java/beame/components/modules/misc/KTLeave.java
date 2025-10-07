package beame.components.modules.misc;

import events.Event;
import events.EventKey;
import events.impl.render.EventRender;
import events.impl.player.EventScreenOpen;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Slot;
import beame.setting.SettingList.BindSetting;

public class KTLeave extends Module {
// leaked by itskekoff; discord.gg/sk3d rKUJmGwd
    private final BindSetting leaveBind = new BindSetting("Кнопка лива", 0);
    private boolean hasClicked = false;
    private boolean shouldClick = false;

    public KTLeave() {
        super("KTLeave", Category.Misc, true, "Автоматический выход из арены");
        addSettings(leaveBind);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        hasClicked = false;
        shouldClick = false;
        if (mc.player != null) {
            mc.player.sendChatMessage("/darena");
        }
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventScreenOpen) {
            EventScreenOpen e = (EventScreenOpen) event;
            if (mc.currentScreen instanceof ContainerScreen) {
                e.setCancel(true);
                shouldClick = true;
            }
        }

        if (shouldClick && !hasClicked) {
            ContainerScreen<?> gui = (ContainerScreen<?>) mc.currentScreen;
            if (gui != null) {
                Slot slot = gui.getContainer().getSlot(24);
                if (slot != null) {
                    mc.playerController.windowClick(gui.getContainer().windowId, 24, 0, ClickType.PICKUP, mc.player);
                    hasClicked = true;
                    shouldClick = false;
                    mc.displayGuiScreen(null);
                    this.toggle();
                }
            }
        }

        if (event instanceof EventKey) {
            EventKey e = (EventKey) event;
            if (e.key == leaveBind.get() && e.key != 0 && !hasClicked) {
                shouldClick = true;
            }
        }
        if (event instanceof EventRender && mc.currentScreen instanceof ContainerScreen) {
            event.setCancel(true);
        }
    }
}
