package beame.components.baritone.api.cache;

/**
 * @author Brady
 * @since 9/24/2018
 */
public interface IWorldData {
// leaked by itskekoff; discord.gg/sk3d jTPo4kkf

    /**
     * Returns the cached world for this world. A cached world is a simplified format
     * of a regular world, intended for use on multiplayer servers where chunks are not
     * traditionally stored to disk, allowing for long distance pathing with minimal disk usage.
     *
     * @return The cached world for this world
     */
    ICachedWorld getCachedWorld();

    /**
     * @return The waypoint collection for this world
     */
    IWaypointCollection getWaypoints();

}
