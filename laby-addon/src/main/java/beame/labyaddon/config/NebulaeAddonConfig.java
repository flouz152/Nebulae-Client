package beame.labyaddon.config;

import net.labymod.api.configuration.loader.property.type.BooleanProperty;
import net.labymod.api.configuration.loader.property.type.StringProperty;
import net.labymod.api.configuration.loader.property.type.DoubleProperty;
import net.labymod.api.configuration.loader.property.ConfigPropertyHolder;
import net.labymod.api.configuration.loader.annotation.Config;
import net.labymod.api.configuration.loader.annotation.Option;
import net.labymod.api.configuration.loader.annotation.Section;

/**
 * Simple persisted configuration mirroring the settings exposed in the GUI.
 */
@Config(name = "nebulae")
public class NebulaeAddonConfig implements ConfigPropertyHolder {

    @Section(value = "target_esp")
    public final BooleanProperty targetEspEnabled = new BooleanProperty(false);
    @Option(label = "Тип")
    public final StringProperty targetEspType = new StringProperty("Призраки");
    @Option(label = "Краснеть при ударе")
    public final BooleanProperty redOnHurt = new BooleanProperty(true);
    @Option(label = "Скорость призраков")
    public final DoubleProperty ghostsSpeed = new DoubleProperty(33.0D);
    @Option(label = "Длина призраков")
    public final DoubleProperty ghostsLength = new DoubleProperty(24.0D);
    @Option(label = "Ширина призраков")
    public final DoubleProperty ghostsWidth = new DoubleProperty(0.4D);
    @Option(label = "Угол вращения призраков")
    public final DoubleProperty ghostsAngle = new DoubleProperty(0.18D);
    @Option(label = "Скорость круга")
    public final DoubleProperty speedCircle = new DoubleProperty(2000.0D);
}
