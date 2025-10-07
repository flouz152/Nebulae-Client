package beame.module;

import beame.Essence;
import beame.util.IMinecraft;
import beame.util.other.SoundUtil;
import events.Event;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import beame.setting.ConfigSetting;

import java.util.List;

@Getter
public abstract class Module implements IMinecraft {
// leaked by itskekoff; discord.gg/sk3d kcq66rXC

    final String name;
    final Category category;
    boolean hideNotification = false;

    String suffix = "";

    public boolean state;
    public boolean stateDescription;
    public String description;
    @Setter
    int bind;
    final List<ConfigSetting<?>> configSettings = new ObjectArrayList<>();

    protected Module(String name, Category category) {
        this.name = name;
        this.category = category;
    }

    protected Module(String name, Category category, String suffix) {
        this.name = name;
        this.category = category;
        this.suffix = suffix;
    }

    protected Module(String name, Category category, boolean hideNotification) {
        this.name = name;
        this.category = category;
        this.hideNotification = hideNotification;
    }

    protected Module(String name, Category category, boolean stateDescription, String description) {
        this.name = name;
        this.category = category;
        this.stateDescription = stateDescription;
        this.description = description;
    }

    private boolean visible = true;

    public void addSettings(ConfigSetting<?>... configSettings) {
        this.configSettings.addAll(List.of(configSettings));
    }

    public Module getThis() {
        return this;
    }

    public void setState(final boolean enabled) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (this.state == enabled) return;

        this.state = enabled;

        if (enabled) {
            this.onEnable();
        } else {
            this.onDisable();
        }

        if (!hideNotification) {
            Essence.getHandler().notificationManager.pushNotify(this, state);

            if (Essence.getHandler().getModuleList().getClientSounds().isState() && Essence.getHandler().getModuleList().getClientSounds().soundActive.get(0).get()) {
                SoundUtil.playSound(state ? "enable.wav" : "disable.wav", Essence.getHandler().getModuleList().getClientSounds().volume.get(), false);
            }
        }
        Essence.cfgManager.saveAutoConfig();

    }

    public void markState(final boolean enabled) {
        this.state = enabled;

        if (enabled) {
            this.onEnable();
        }
    }


    public void toggle() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        this.state = !state;
        if (!state) {
            onDisable();
        } else {
            onEnable();
        }

        if (!hideNotification) {
            Essence.getHandler().notificationManager.pushNotify(this, state);

            if (Essence.getHandler().getModuleList().getClientSounds().isState() && Essence.getHandler().getModuleList().getClientSounds().soundActive.get(0).get()) {
                SoundUtil.playSound(state ? "enable.wav" : "disable.wav", Essence.getHandler().getModuleList().getClientSounds().volume.get(), false);
            }
        }
        Essence.cfgManager.saveAutoConfig();
    }

    protected void onDisable() {
    }

    protected void onEnable() {
    }


    public abstract void event(final Event event);
}
