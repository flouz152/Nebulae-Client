package beame.components.modules.misc;

import beame.util.math.TimerUtil;
import beame.util.render.RenderUtil3D;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventMotion;
import events.impl.render.EventRender;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3d;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.concurrent.CopyOnWriteArrayList;

public final class Blink extends Module {
// leaked by itskekoff; discord.gg/sk3d lPwE2aHS

    private final CopyOnWriteArrayList<IPacket> packets = new CopyOnWriteArrayList<>();
    private final BooleanSetting delay = new BooleanSetting("Пульсации", false);
    private final SliderSetting delayS = new SliderSetting("Задержка", 100, 50, 1000, 50).setVisible(delay::get);
    private final TimerUtil timerUtils = new TimerUtil();
    private Vector3d lastPos = new Vector3d(0, 0, 0);
    private long started;

    public Blink() {
        super("Blink", Category.Misc, true, "Задерживает отправку пакетов на сервер");
        addSettings(delay, delayS);
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventRender eventRender) {
            if (eventRender.isRender3D()) {
                RenderSystem.pushMatrix();
                RenderSystem.enableBlend();
                RenderUtil3D.drawBox(AxisAlignedBB.fromVector(lastPos).expand(0, 1, 0).offset(-mc.getRenderManager().info.getProjectedView().x, -mc.getRenderManager().info.getProjectedView().y, -mc.getRenderManager().info.getProjectedView().z).offset(-0.5f, 0, -0.5f).grow(-0.2, 0, -0.2), -1);
                RenderSystem.disableBlend();
                RenderSystem.popMatrix();
            }
        }
        if (event instanceof EventPacket eventPacket) {
            if (mc.player != null && mc.world != null && !mc.isSingleplayer() && !mc.player.getShouldBeDead()) {
                if (eventPacket.isSendPacket()) {
                    packets.add(eventPacket.getPacket());
                    eventPacket.setCancel(true);
                }
            } else {
                toggle();
            }
        }
        if (event instanceof EventMotion) {
            if ((System.currentTimeMillis() - started) >= 29900) toggle();
            if (delay.get() && timerUtils.hasTimeElapsed(delayS.get().longValue())) {
                for (IPacket packet : packets) {
                    mc.player.connection.getNetworkManager().sendPacketWithoutEvent(packet);
                }
                packets.clear();
                started = System.currentTimeMillis();
                timerUtils.reset();
            }
        }
    }


    @Override
    public void onEnable() {
        super.onEnable();
        started = System.currentTimeMillis();
        lastPos = mc.player.getPositionVec();
    }


    @Override
    public void onDisable() {
        super.onDisable();
        for (IPacket packet : packets) {
            mc.player.connection.sendPacket(packet);
        }
        packets.clear();
    }
}
