package beame.components.baritone.utils.schematic;

import beame.components.baritone.api.command.registry.Registry;
import beame.components.baritone.api.schematic.ISchematicSystem;
import beame.components.baritone.api.schematic.format.ISchematicFormat;
import beame.components.baritone.utils.schematic.format.DefaultSchematicFormats;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author Brady
 * @since 12/24/2019
 */
public enum SchematicSystem implements ISchematicSystem {
// leaked by itskekoff; discord.gg/sk3d Jox8C5RY
    INSTANCE;

    private final Registry<ISchematicFormat> registry = new Registry<>();

    SchematicSystem() {
        Arrays.stream(DefaultSchematicFormats.values()).forEach(this.registry::register);
    }

    @Override
    public Registry<ISchematicFormat> getRegistry() {
        return this.registry;
    }

    @Override
    public Optional<ISchematicFormat> getByFile(File file) {
        return this.registry.stream().filter(format -> format.isFileType(file)).findFirst();
    }
}
