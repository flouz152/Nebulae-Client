package beame.labyaddon.feature;

import beame.Essence;
import beame.components.modules.player.FTHelper;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.SliderSetting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility wrapper that exposes the most important FTHelper options to external UIs.
 */
public class FTHelperBridge {

    private final AtomicBoolean queuedEnableState = new AtomicBoolean(false);
    private final AtomicBoolean hasQueuedState = new AtomicBoolean(false);

    public Optional<FTHelper> module() {
        Essence handler = Essence.getHandler();
        if (handler == null || handler.getModuleList() == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(handler.getModuleList().ftHelper);
    }

    public void refresh() {
        module().ifPresent(ftHelper -> {
            // Ensure internal slider visibility is up to date.
            if (ftHelper.options.get("Авто /event delay") != null) {
                ftHelper.eventDelayInterval.setVisible(() -> ftHelper.options.get("Авто /event delay").get());
            }
        });
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

    public boolean getOption(String key, boolean fallback) {
        return module().map(module -> {
            BooleanSetting setting = module.options.get(key);
            return setting != null ? setting.get() : fallback;
        }).orElse(fallback);
    }

    public void setOption(String key, boolean value) {
        module().ifPresent(module -> {
            BooleanSetting setting = module.options.get(key);
            if (setting != null) {
                setting.set(value);
            }
        });
    }

    public float getEventDelayMinutes() {
        return module().map(module -> module.eventDelayInterval.get()).orElse(1.0F);
    }

    public void setEventDelayMinutes(float value) {
        module().ifPresent(module -> {
            SliderSetting slider = module.eventDelayInterval;
            float clamped = MathHelper.clamp(value, slider.min, slider.max);
            float snapped = Math.round(clamped / slider.increment) * slider.increment;
            slider.set(snapped);
        });
    }
}
