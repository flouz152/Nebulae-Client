package beame.components.baritone.api;

import beame.components.baritone.BaritoneProvider;
import beame.components.baritone.api.utils.SettingsUtil;


/**
 * Exposes the {@link IBaritoneProvider} instance and the {@link Settings} instance for API usage.
 *
 * @author Brady
 * @since 9/23/2018
 */
public final class BaritoneAPI {
// leaked by itskekoff; discord.gg/sk3d lsce6HhU

    private static IBaritoneProvider provider;
    private static Settings settings;

    public static void init() {
        settings = new Settings();
        SettingsUtil.readAndApply(settings, SettingsUtil.SETTINGS_DEFAULT_NAME);
        provider = new BaritoneProvider();
        //System.out.println("Baritone -> initializing.");
    }

    public static IBaritoneProvider getProvider() {
        return BaritoneAPI.provider;
    }

    public static Settings getSettings() {
        return BaritoneAPI.settings;
    }
}
