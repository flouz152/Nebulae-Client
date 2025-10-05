package beame.module;

import beame.Essence;
import beame.components.modules.combat.*;
import beame.components.modules.misc.*;
import beame.components.modules.movement.*;
import beame.components.modules.player.*;
import beame.components.modules.render.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class ModuleList {
// leaked by itskekoff; discord.gg/sk3d fFzuBRTW
    private final List<Module> modules = new CopyOnWriteArrayList<>();
    //public AttackAura attackAura;
    public Aura aura;
    //public PlayMusic playMusic;
    public AntiBot antiBot;
    public TriggerBot triggerBot;
    //public Unhook unhook;
    public ClientSounds clientSounds;
    //public TelegramAPI telegramAPI;
    public beame.components.modules.render.ESP ESP;
    public Interface hud;
    public Visuality visuality;
    public AutoMyst autoMyst;
    public ZeroHitbox zeroHitbox;
    public Sprint sprint;
    public Speed speed;
  //  public CasinoBot casinoBot;
    public AutoEat autoEat;
    public MinecraftUtils mcUtils;
    public MiddleClickPearl middleClickPearl;
    public NameProtect nameProtect;
    public AspectRatio aspectRatio;
    //public Autobuy autobuy;
    public TapeMouse tapeMouse;
    public Arrows arrows;
    //public Viewmodel viewmodel;
    public AutoAccept autoAccept;
    public GuiMove guiMove;
    public AuctionHelper ahHelper;
    public FreeCamera freeCamera;
    public NoFriendDamage noFriendDamage;
    public ItemScroller itemScroller;
    public ElytraHelper elytraHelper;
    public FTHelper ftHelper;
    public AutoSwap autoSwap;
    public AutoTotem autoTotem;
    public SwingAnimations swingAnimations;
    public NoInteract noInteract;
    public NoPush noPush;
    public AutoTool autoTool;
    public Predictions pearlPrediction;
    public Ambience ambience;
    public Viewmodel viewmodel;
    public FieldOfView fieldOfView;
    public AntiAFK antiAFK;
    public TargetESP targetESP;
    public FastEXP fastEXP;
    //public BlockESP blockESP;
    public NoSlow noSlow;
    //public Criticals criticals;
    //public Strafe strafe;
    public Xray xRay;
   // public StaffKill staffKill;
    public CreeperFarm creeperFarm;
    public CustomFog customFog;
    public CollisionDisabler collisionDisabler;
    //public VulcanESP vulcanESP;
    public AutoBuy autoBuy;
    public CameraSettings cameraSettings;
    public AutoFarm autoFarm;
    public Particles particles;
    public AutoLeave autoLeave;
    public AutoInvisible autoInvisible;
    public LockSlot lockSlot;
    //public KTLeave ktLeave;
    public AutoDisorient autoDisorient;
    public CoordsSender coordsSender;
    public ProjectileHelper projectileHelper;
    //public ClanUpgrade clanUpgrade;
    public AutoFish autoFish;
    public ShaderESP shaderEsp;
    public Sneak sneak;
    public HighJump highJump;
    public FastBow fastBow;
    public Velocity velocity;
    public SeeInvisible seeInvisible;
    public ChunkAnimator chunkAnimator;
    public WaterSpeed waterSpeed;
    public AutoPotion autoPotion;
    public AutoDuel autoDuel;
    public RPSpoofer rpSpoofer;
    public ClickGUI clickGUI;
    public Spider spider;
    public TargetPearl targetPearl;
    public ShulkerView shulkerView;
   // public AutoDodge autoDodge;
    public HitBox hitBox;
    public BlockESP blockESP;
    //public TridentAim tridentAim;
    //public Timer timer;
   // public FreeLook freeLook;
    public ServerJoiner serverJoiner;
    public ClanInvest clanInvest;
    public ItemPhysic itemPhysic;
  //  public ItemTP itemTP;
    public AutoExplosion autoExplosion;
    public AutoDodge autoDodge;
    public Flight flight;
  //  public Strafe strafe;
   // public ElytraTarget elytraTarget;
    public AutoReg autoReg;
    public Crosshair crosshair;
    public NoServerRotation noServerRotation;
    public GlassHand glassHand;
    public Blink blink;
   // public ElytraRecast elytraRecast;
    public ClickFriend clickFriend;
    public UseTracker useTracker;

    public List<MModule> replacments = new ArrayList<>();

    public void initialization() {
        registerAll(
                aura = new Aura(),
                triggerBot = new TriggerBot(),
                arrows = new Arrows(),
                clientSounds = new ClientSounds(),
                autoLeave = new AutoLeave(),
               // autoDodge = new AutoDodge(),
                visuality = new Visuality(),
                autoInvisible = new AutoInvisible(),
               // playMusic = new PlayMusic(),
                clickGUI = new ClickGUI(),
                middleClickPearl = new MiddleClickPearl(),
                autoMyst = new AutoMyst(),
                nameProtect = new NameProtect(),
                autoPotion = new AutoPotion(),
                zeroHitbox = new ZeroHitbox(),
                autoEat = new AutoEat(),
                sprint = new Sprint(),
                autoDuel = new AutoDuel(),
                ESP = new ESP(),
                chunkAnimator = new ChunkAnimator(),
                seeInvisible = new SeeInvisible(),
                shaderEsp = new ShaderESP(),
                mcUtils = new MinecraftUtils(),
                aspectRatio = new AspectRatio(),
                antiBot = new AntiBot(),
                autoAccept = new AutoAccept(),
                guiMove = new GuiMove(),
                ahHelper = new AuctionHelper(),
                freeCamera = new FreeCamera(),
                elytraHelper = new ElytraHelper(),
                speed = new Speed(),
                itemScroller = new ItemScroller(),
                noFriendDamage = new NoFriendDamage(),
                ftHelper = new FTHelper(),
                autoSwap = new AutoSwap(),
                autoTotem = new AutoTotem(),
              //  staffKill = new StaffKill(),
                swingAnimations = new SwingAnimations(),
                noInteract = new NoInteract(),
                noPush = new NoPush(),
                autoTool = new AutoTool(),
                pearlPrediction = new Predictions(),
                ambience = new Ambience(),
                viewmodel = new Viewmodel(),
                targetESP = new TargetESP(),
                antiAFK = new AntiAFK(),
                hud = new Interface(),
                fastEXP = new FastEXP(),
                noSlow = new NoSlow(),
                collisionDisabler = new CollisionDisabler(),
                xRay = new Xray(),
                //ktLeave = new KTLeave(),
                creeperFarm = new CreeperFarm(),
                customFog = new CustomFog(),
                autoBuy = new AutoBuy(),
                cameraSettings = new CameraSettings(),
                autoFarm = new AutoFarm(),
                fieldOfView = new FieldOfView(),
                particles = new Particles(),
                lockSlot = new LockSlot(),
                autoDisorient = new AutoDisorient(),
                coordsSender = new CoordsSender(),
                projectileHelper = new ProjectileHelper(),
                //clanUpgrade = new ClanUpgrade(),
                autoFish = new AutoFish(),
                sneak = new Sneak(),
                highJump = new HighJump(),
                fastBow = new FastBow(),
                velocity = new Velocity(),
                waterSpeed = new WaterSpeed(),
                rpSpoofer = new RPSpoofer(),
                spider = new Spider(),
                targetPearl = new TargetPearl(),
                shulkerView = new ShulkerView(),
                hitBox = new HitBox(),
                blockESP = new BlockESP(),
                //tridentAim = new TridentAim()
                //timer = new Timer()
            //    freeLook = new FreeLook(),
                serverJoiner = new ServerJoiner(),
                clanInvest = new ClanInvest(),
                itemPhysic = new ItemPhysic(),
            //    itemTP = new ItemTP(),
                autoExplosion = new AutoExplosion(),
                autoDodge = new AutoDodge(),
                flight = new Flight(),
               // strafe = new Strafe(),
               // elytraTarget = new ElytraTarget(),
                autoReg = new AutoReg(),
                crosshair = new Crosshair(),
                noServerRotation = new NoServerRotation(),
               // glassHand = new GlassHand(),
                blink = new Blink(),
               clickFriend = new ClickFriend(),
               // elytraRecast = new ElytraRecast()
                useTracker = new UseTracker()



        );

        Essence.getHandler().getEventBus().register(this);
    }

    private void registerAll(Module... modules) {
        this.modules.addAll(List.of(modules));
        for (Module m : modules) this.replacments.add(new MModule(m.name, m));
    }


    public void keyPress(int key) {

        for (Module Module : modules) {
            if (Module.getBind() == key) {
                Module.toggle();
            }
        }
    }

    public class MModule {
        public String name;
        public Module module;

        public MModule(String name, Module module) {
            this.name = name;
            this.module = module;
        }
    }
}
