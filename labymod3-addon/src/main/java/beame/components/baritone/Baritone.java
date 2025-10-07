package beame.components.baritone;

import beame.components.baritone.api.BaritoneAPI;
import beame.components.baritone.api.IBaritone;
import beame.components.baritone.api.Settings;
import beame.components.baritone.api.behavior.IBehavior;
import beame.components.baritone.api.event.listener.IEventBus;
import beame.components.baritone.api.process.IBaritoneProcess;
import beame.components.baritone.api.utils.IPlayerContext;
import beame.components.baritone.behavior.InventoryBehavior;
import beame.components.baritone.behavior.LookBehavior;
import beame.components.baritone.behavior.PathingBehavior;
import beame.components.baritone.behavior.WaypointBehavior;
import beame.components.baritone.cache.WorldProvider;
import beame.components.baritone.command.manager.CommandManager;
import beame.components.baritone.event.GameEventHandler;
import beame.components.baritone.process.*;
import beame.components.baritone.process.*;
import beame.components.baritone.selection.SelectionManager;
import beame.components.baritone.utils.BlockStateInterface;
import beame.components.baritone.utils.GuiClick;
import beame.components.baritone.utils.InputOverrideHandler;
import beame.components.baritone.utils.PathingControlManager;
import beame.components.baritone.utils.player.BaritonePlayerContext;
import lombok.Getter;
import net.minecraft.client.Minecraft;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.Executor;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;


public class Baritone implements IBaritone {
// leaked by itskekoff; discord.gg/sk3d QajZuDUE

    private static final ThreadPoolExecutor threadPool;

    static {
        threadPool = new ThreadPoolExecutor(4, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<>());
    }

    private final Minecraft mc;
    @Getter
    private final Path directory;

    private final GameEventHandler gameEventHandler;

    private final PathingBehavior pathingBehavior;
    private final LookBehavior lookBehavior;
    @Getter
    private final InventoryBehavior inventoryBehavior;
    private final InputOverrideHandler inputOverrideHandler;

    private final FollowProcess followProcess;
    private final MineProcess mineProcess;
    private final GetToBlockProcess getToBlockProcess;
    private final CustomGoalProcess customGoalProcess;
    private final BuilderProcess builderProcess;
    private final ExploreProcess exploreProcess;
    private final FarmProcess farmProcess;
    @Getter
    private final InventoryPauserProcess inventoryPauserProcess;

    private final PathingControlManager pathingControlManager;
    private final SelectionManager selectionManager;
    private final CommandManager commandManager;

    private final IPlayerContext playerContext;
    private final WorldProvider worldProvider;

    public BlockStateInterface bsi;

    Baritone(Minecraft mc) {
        this.mc = mc;
        this.gameEventHandler = new GameEventHandler(this);

        this.directory = mc.gameDir.toPath().resolve("baritone");
        if (!Files.exists(this.directory)) {
            try {
                Files.createDirectories(this.directory);
            } catch(IOException ignored) {
            }
        }

        // Define this before behaviors try and get it, or else it will be null and the builds will fail!
        this.playerContext = new BaritonePlayerContext(this, mc);

        {
            this.lookBehavior = this.registerBehavior(LookBehavior::new);
            this.pathingBehavior = this.registerBehavior(PathingBehavior::new);
            this.inventoryBehavior = this.registerBehavior(InventoryBehavior::new);
            this.inputOverrideHandler = this.registerBehavior(InputOverrideHandler::new);
            this.registerBehavior(WaypointBehavior::new);
        }

        this.pathingControlManager = new PathingControlManager(this);
        {
            this.followProcess = this.registerProcess(FollowProcess::new);
            this.mineProcess = this.registerProcess(MineProcess::new);
            this.customGoalProcess = this.registerProcess(CustomGoalProcess::new); // very high iq
            this.getToBlockProcess = this.registerProcess(GetToBlockProcess::new);
            this.builderProcess = this.registerProcess(BuilderProcess::new);
            this.exploreProcess = this.registerProcess(ExploreProcess::new);
            this.farmProcess = this.registerProcess(FarmProcess::new);
            this.inventoryPauserProcess = this.registerProcess(InventoryPauserProcess::new);
            this.registerProcess(BackfillProcess::new);
        }

        this.worldProvider = new WorldProvider(this);
        this.selectionManager = new SelectionManager(this);
        this.commandManager = new CommandManager(this);
    }

    public void registerBehavior(IBehavior behavior) {
        this.gameEventHandler.registerEventListener(behavior);
    }

    public <T extends IBehavior> T registerBehavior(Function<Baritone, T> constructor) {
        final T behavior = constructor.apply(this);
        this.registerBehavior(behavior);
        return behavior;
    }

    public <T extends IBaritoneProcess> T registerProcess(Function<Baritone, T> constructor) {
        final T behavior = constructor.apply(this);
        this.pathingControlManager.registerProcess(behavior);
        return behavior;
    }

    @Override
    public PathingControlManager getPathingControlManager() {
        return this.pathingControlManager;
    }

    @Override
    public InputOverrideHandler getInputOverrideHandler() {
        return this.inputOverrideHandler;
    }

    @Override
    public CustomGoalProcess getCustomGoalProcess() {
        return this.customGoalProcess;
    }

    @Override
    public GetToBlockProcess getGetToBlockProcess() {
        return this.getToBlockProcess;
    }

    @Override
    public IPlayerContext getPlayerContext() {
        return this.playerContext;
    }

    @Override
    public FollowProcess getFollowProcess() {
        return this.followProcess;
    }

    @Override
    public BuilderProcess getBuilderProcess() {
        return this.builderProcess;
    }

    @Override
    public LookBehavior getLookBehavior() {
        return this.lookBehavior;
    }

    @Override
    public ExploreProcess getExploreProcess() {
        return this.exploreProcess;
    }

    @Override
    public MineProcess getMineProcess() {
        return this.mineProcess;
    }

    @Override
    public FarmProcess getFarmProcess() {
        return this.farmProcess;
    }

    @Override
    public PathingBehavior getPathingBehavior() {
        return this.pathingBehavior;
    }

    @Override
    public SelectionManager getSelectionManager() {
        return selectionManager;
    }

    @Override
    public WorldProvider getWorldProvider() {
        return this.worldProvider;
    }

    @Override
    public IEventBus getGameEventHandler() {
        return this.gameEventHandler;
    }

    @Override
    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    @Override
    public void openClick() {
        new Thread(() -> {
            try {
                Thread.sleep(100);
                mc.execute(() -> mc.displayGuiScreen(new GuiClick()));
            } catch(Exception ignored) {
            }
        }).start();
    }

    public static Settings settings() {
        return BaritoneAPI.getSettings();
    }

    public static Executor getExecutor() {
        return threadPool;
    }
}
