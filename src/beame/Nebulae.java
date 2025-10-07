package beame;

import beame.components.baritone.api.BaritoneAPI;
import beame.components.clickgui.DropDownGui;
import beame.components.command.CommandManager;
import beame.components.modules.combat.AuraHandlers.component.ComponentManager;
import beame.components.modules.combat.AutoTotem;
import beame.managers.macro.MacroManager;
import beame.components.modules.combat.AuraHandlers.other.AuraHelper;
import beame.components.modules.misc.AutoBuyLogic.AutoBuySystem;
import beame.components.modules.misc.AutoBuyLogic.GUI.AutoBuyScreen;
import beame.managers.configs.ConfigManager;
import beame.feature.gps.GPS;
import beame.feature.notify.NotificationManager;
import beame.feature.themes.StyleManager;
import beame.feature.themes.ThemeManager;
import beame.managers.alts.AltManager;
import beame.managers.friends.FriendManager;
import beame.managers.staff.StaffManager;
import beame.proxy.ProxyConfig;
import beame.util.IMinecraft;
import beame.util.drag.DraggableManager;
import beame.util.drag.Dragging;
import beame.util.other.ServerUtils;
import beame.util.shaderExcellent.ShaderManager;
import beame.util.telegram.BotUtil;
import beame.wavecapes.WaveyCapesBase;
import com.google.common.eventbus.EventBus;
import events.EventKey;
import events.EventManager;
import lombok.Getter;
import beame.module.Module;
import beame.module.ModuleList;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import beame.components.modules.misc.AutoBuyLogic.AutoSell;

@Getter
public class Nebulae {
// leaked by itskekoff; discord.gg/sk3d PnBU8glo
    private static final List<Module> modules = new CopyOnWriteArrayList<>();

    public static boolean isServer;

    public boolean isDevBuild = false;
    public boolean unhooked = false;

    public boolean disableMove = false;

    @Getter
    private static Nebulae handler;

    private final Path clientDir = Paths.get(Minecraft.getInstance().gameDir.getAbsolutePath(), "Nebulae");
    @Getter
    private final Path filesDir = Paths.get(Minecraft.getInstance().gameDir.getAbsolutePath(), "nebulae", "cfg");
    private MacroManager macroManager;
    private StaffManager staffManager;
    private ModuleList moduleList;
    private DropDownGui dropDownGui;
    private final EventBus eventBus = new EventBus();
    public ThemeManager themeManager;
    public static DraggableManager draggableManager;
    public static ConfigManager cfgManager;
    public BotUtil telegram;
    public GPS gps;
    public NotificationManager notificationManager;
    public CommandManager commandManager;
    public StyleManager styler;
    public FriendManager friends;
    public AltManager altManager;
    private WaveyCapesBase waveyCapesBase;

    public AutoBuySystem autoBuy;
    public AutoBuyScreen autoBuyGUI;
    public AutoTotem autoTotem;
    public ServerUtils serverUtils;
    public ComponentManager componentManager;

    public AuraHelper auraHelper;

    public BigDecimal balance = new BigDecimal(0);
    public String strBalance = "0";

    public float interpolateState = 0;
    public float needValueInterpolate = 1;

    private int userID;
    private String userName, formattedUserRole;

    public final AutoSell autoSell = new AutoSell();

    private void initialize() {
        this.userID = 52;
        this.userName = "nextozz1337";
        this.formattedUserRole = "Null";
        handler = this;

        if(Minecraft.getInstance().loaded) {
            createDirectory(clientDir);
            createDirectory(filesDir);

            moduleList = new ModuleList();
            auraHelper = new AuraHelper();

            moduleList.initialization();
            eventBus.register(this);

            //aura
            componentManager = new ComponentManager();
            this.componentManager.init();

            draggableManager = new DraggableManager();

            styler = new StyleManager();
            themeManager = new ThemeManager();

            friends = new FriendManager();
            macroManager = new MacroManager();
            staffManager = new StaffManager();
            altManager = new AltManager();
            waveyCapesBase = new WaveyCapesBase();

            dropDownGui = new DropDownGui(new StringTextComponent("NEBULAE"));
            autoTotem = new AutoTotem();
            autoBuy = new AutoBuySystem();
            autoBuy.loadPrices();

            autoBuyGUI = new AutoBuyScreen(new StringTextComponent("ZOV"));

            gps = new GPS();
            commandManager = new CommandManager();
            cfgManager = new ConfigManager();
            notificationManager = new NotificationManager();

            serverUtils = new ServerUtils();

            BaritoneAPI.init();
            BaritoneAPI.getProvider().getPrimaryBaritone();

        } else {
            unhooked = true;
        }
        cfgManager.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> cfgManager.saveAutoConfig()));

    }

    public Nebulae() {
        ShaderManager.loadShaders();
        ProxyConfig.loadConfig();
        initialize();

        try {
            altManager.loadFile();
            if (altManager.getSelectedNickname() != null) {
                String nickname = altManager.getSelectedNickname();
                if (nickname.length() >= 3 && nickname.length() <= 16) {
                    String uuid = java.util.UUID.randomUUID().toString();
                    IMinecraft.mc.session = new net.minecraft.util.Session(nickname, uuid, "", "mojang");
                }
            }
            macroManager.init();
            staffManager.init();
        } catch (Exception e) {
            e.printStackTrace();
        }

        DraggableManager.load();

    }

    public static Dragging createDraggable(String name, float x, float y) {
        DraggableManager.draggables.put(name, new Dragging(name, x, y));
        return DraggableManager.draggables.get(name);
    }


    public void keyPress(int key) {
        if(!unhooked) {
            EventManager.call(new EventKey(key, true));
            macroManager.onKeyPressed(key);
            if (key == Nebulae.getHandler().getModuleList().getClickGUI().clickGuiBind.get()) {
                Minecraft.getInstance().displayGuiScreen(dropDownGui);
            }
        }
    }

    private void createDirectory(Path dir) {
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}