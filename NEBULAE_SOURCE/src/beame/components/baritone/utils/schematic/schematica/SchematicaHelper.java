package beame.components.baritone.utils.schematic.schematica;

import beame.components.baritone.api.schematic.IStaticSchematic;
import beame.components.baritone.schematica.Schematica;
import beame.components.baritone.schematica.proxy.ClientProxy;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

public enum SchematicaHelper {
// leaked by itskekoff; discord.gg/sk3d DYNZKjO0
    ;

    public static boolean isSchematicaPresent() {
        try {
            Class.forName(Schematica.class.getName());
            return true;
        } catch(ClassNotFoundException | NoClassDefFoundError ex) {
            return false;
        }
    }

    public static Optional<Tuple<IStaticSchematic, BlockPos>> getOpenSchematic() {
        return Optional.ofNullable(ClientProxy.schematic)
                .map(world -> new Tuple<>(new SchematicAdapter(world), world.position));
    }

}
