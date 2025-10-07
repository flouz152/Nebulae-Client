package beame.labyaddon.feature;

import beame.Essence;
import beame.components.modules.render.TargetESP;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Wrapper around the TargetESP module that exposes a user-friendly API for GUI controls.
 */
public class TargetESPBridge {

    private final AtomicBoolean queuedEnableState = new AtomicBoolean(false);
    private final AtomicBoolean hasQueuedState = new AtomicBoolean(false);

    public Optional<TargetESP> module() {
        Essence handler = Essence.getHandler();
        if (handler == null || handler.getModuleList() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(handler.getModuleList().targetESP);
    }

    public void refresh() {
        // Nothing to refresh at the moment, kept for parity with FTHelperBridge.
    }

    public void setEnabled(boolean enabled) {
        module().ifPresentOrElse(module -> {
            if (Minecraft.getInstance().player == null || Minecraft.getInstance().world == null) {
                queueState(enabled);
                return;
            }
            if (module.state != enabled) {
                module.setState(enabled);
            }
        }, () -> queueState(enabled));
    }

    private void queueState(boolean enabled) {
        this.queuedEnableState.set(enabled);
        this.hasQueuedState.set(true);
    }

    public void flushQueuedState() {
        if (!hasQueuedState.getAndSet(false)) {
            return;
        }
        boolean enabled = queuedEnableState.get();
        module().ifPresent(module -> {
            if (module.state != enabled) {
                module.setState(enabled);
            }
        });
    }

    public boolean isEnabled() {
        return module().map(module -> module.state).orElse(false);
    }

    public String[] getAvailableTypes() {
        return module().map(module -> module.type.strings).orElse(new String[]{"Призраки"});
    }

    public String getType() {
        return module().map(module -> module.type.get()).orElse("Призраки");
    }

    public void setType(String value) {
        module().ifPresent(module -> {
            RadioSetting setting = module.type;
            for (String candidate : setting.strings) {
                if (candidate.equalsIgnoreCase(value)) {
                    setting.set(candidate);
                    return;
                }
            }
            setting.set(setting.strings[0]);
        });
    }

    public boolean isRedOnHurt() {
        return module().map(module -> module.redOnHurt.get()).orElse(true);
    }

    public void setRedOnHurt(boolean value) {
        module().ifPresent(module -> module.redOnHurt.set(value));
    }

    private float clampSlider(SliderSetting setting, float value) {
        float clamped = MathHelper.clamp(value, setting.min, setting.max);
        return Math.round(clamped / setting.increment) * setting.increment;
    }

    public float getGhostsSpeed() {
        return module().map(module -> module.ghostsSpeed.get()).orElse(33.0F);
    }

    public void setGhostsSpeed(float value) {
        module().ifPresent(module -> module.ghostsSpeed.set(clampSlider(module.ghostsSpeed, value)));
    }

    public float getGhostsLength() {
        return module().map(module -> module.ghostsLength.get()).orElse(24.0F);
    }

    public void setGhostsLength(float value) {
        module().ifPresent(module -> module.ghostsLength.set(clampSlider(module.ghostsLength, value)));
    }

    public float getGhostsWidth() {
        return module().map(module -> module.ghostsWidth.get()).orElse(0.4F);
    }

    public void setGhostsWidth(float value) {
        module().ifPresent(module -> module.ghostsWidth.set(clampSlider(module.ghostsWidth, value)));
    }

    public float getGhostsAngle() {
        return module().map(module -> module.ghostsAngle.get()).orElse(0.18F);
    }

    public void setGhostsAngle(float value) {
        module().ifPresent(module -> module.ghostsAngle.set(clampSlider(module.ghostsAngle, value)));
    }

    public float getCircleSpeed() {
        return module().map(module -> module.speedcircle.get()).orElse(2000.0F);
    }

    public void setCircleSpeed(float value) {
        module().ifPresent(module -> module.speedcircle.set(clampSlider(module.speedcircle, value)));
    }
}
