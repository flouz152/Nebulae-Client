package net.minecraft.resources;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.Unit;

public interface IResourceManagerReloadListener extends IFutureReloadListener
{
// leaked by itskekoff; discord.gg/sk3d VUHM274V
default CompletableFuture<Void> reload(IFutureReloadListener.IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler, IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor)
    {
        return stage.markCompleteAwaitingOthers(Unit.INSTANCE).thenRunAsync(() ->
        {
            reloadProfiler.startTick();
            reloadProfiler.startSection("listener");
            this.onResourceManagerReload(resourceManager);
            reloadProfiler.endSection();
            reloadProfiler.endTick();
        }, gameExecutor);
    }

    void onResourceManagerReload(IResourceManager resourceManager);
}
