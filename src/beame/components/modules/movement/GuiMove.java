package beame.components.modules.movement;

import beame.Nebulae;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventInventoryClose;
import events.impl.player.EventUpdate;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.client.CClickWindowPacket;
import beame.setting.SettingList.BooleanSetting;

import java.util.ArrayList;
import java.util.List;

public class GuiMove extends Module {
// leaked by itskekoff; discord.gg/sk3d vLe5ooBD
    private final TimerUtil timerUtil = new TimerUtil();
    private final List<IPacket<?>> packet = new ArrayList<>();
    int ticks = 0;

    public static KeyBinding[] pressedKeys = {
            mc.gameSettings.keyBindForward,
            mc.gameSettings.keyBindBack,
            mc.gameSettings.keyBindLeft,
            mc.gameSettings.keyBindRight,
            mc.gameSettings.keyBindJump,
            mc.gameSettings.keyBindSprint
    };

    public GuiMove() {
        super("GUIMove", Category.Movement, true, "Позволяет ходить с открытым контейнером");
        addSettings(funtime);
    }

    public final BooleanSetting funtime = new BooleanSetting("Обход фантайма", true, 0);

    public boolean isMoving() {
        return mc.player.movementInput.moveForward != 0f || mc.player.movementInput.moveStrafe != 0f;
    }

    @Override
    public void event(Event event) {
        if(Nebulae.getHandler().disableMove && funtime.get() && mc.player.isOnGround() ) return;
        if(event instanceof EventPacket) {
            EventPacket e = (EventPacket)event;
            if (e.getPacket() instanceof CClickWindowPacket p && isMoving()) {
                if (mc.currentScreen instanceof InventoryScreen) {
                    if (funtime.get()) {
                        packet.add(p);
                        e.setCancel(true);
                    }
                }
            }
        }
        if(event instanceof EventUpdate) {
            if (mc.player != null) {
                if (!timerUtil.hasReached(100)) {
                    if (funtime.get()) {
                        for (KeyBinding keyBinding : pressedKeys) {
                            keyBinding.setPressed(false);
                        }
                    }
                    return;
                }

                if (!(mc.currentScreen instanceof ChatScreen)) updateKeyBindingState(pressedKeys);
            }
        }
        if(event instanceof EventInventoryClose) {
            Event e = (EventInventoryClose)event;
            if (!packet.isEmpty() && isMoving()) {
                new Thread(() -> {
                    timerUtil.reset();
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    for (IPacket<?> p : packet) {
                        mc.player.connection.sendPacket(p);
                    }
                    packet.clear();
                }).start();
                e.setCancel(true);
            }
        }
    }

    private void updateKeyBindingState(KeyBinding[] keyBindings) {
        for (KeyBinding keyBinding : keyBindings) {
            boolean isKeyPressed = InputMappings.isKeyDown(mc.getMainWindow().getHandle(), keyBinding.getDefault().getKeyCode());
            keyBinding.setPressed(isKeyPressed);
        }
    }
}
