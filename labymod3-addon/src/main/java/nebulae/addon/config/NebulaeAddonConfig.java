package nebulae.addon.config;

import net.labymod.api.addon.AddonConfig;
import net.labymod.api.config.annotation.ConfigName;

@ConfigName("nebulae-addon")
public class NebulaeAddonConfig extends AddonConfig {

    @Override
    public boolean enabled() {
        return true;
    }
}
