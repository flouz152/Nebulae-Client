package beame.components.baritone.api.process;

import java.nio.file.Path;

public interface IExploreProcess extends IBaritoneProcess {
// leaked by itskekoff; discord.gg/sk3d fKzyDPDx

    void explore(int centerX, int centerZ);

    void applyJsonFilter(Path path, boolean invert) throws Exception;
}
