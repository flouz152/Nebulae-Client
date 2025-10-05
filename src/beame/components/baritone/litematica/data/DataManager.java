package beame.components.baritone.litematica.data;


import beame.components.baritone.litematica.placement.SchematicPlacementManager;

public class DataManager {
// leaked by itskekoff; discord.gg/sk3d uVXHQE3D
    public static final DataManager INSTANCE = new DataManager();
    private final SchematicPlacementManager schematicPlacementManager = new SchematicPlacementManager();

    private static DataManager getInstance() {
        return INSTANCE;
    }

    public static SchematicPlacementManager getSchematicPlacementManager() {
        return getInstance().schematicPlacementManager;
    }
}