package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Unit;

public interface IAsyncReloader
{
// leaked by itskekoff; discord.gg/sk3d Dlnh7Y1f
    CompletableFuture<Unit> onceDone();

    float estimateExecutionSpeed();

    boolean asyncPartDone();

    boolean fullyDone();

    void join();
}
