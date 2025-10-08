package beame.components.modules.render;

import beame.components.modules.combat.AuraHandlers.other.FastRandom;
import beame.util.animationExcellent.Animation;
import beame.util.animationExcellent.util.Easings;
import beame.util.color.ColorUtils;
import beame.util.math.MathUtil;
import beame.util.math.TimerUtil;
import beame.util.player.PlayerUtil;
import beame.util.render.RenderUtil;
import beame.util.render.RenderUtil3D;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import events.Event;
import events.impl.player.EventAttack;
import events.impl.player.EventMotion;
import events.impl.player.WorldChangeEvent;
import events.impl.render.Render3DPosedEvent;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import beame.module.Category;
import beame.module.Module;
import net.minecraft.client.settings.PointOfView;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.gen.Heightmap;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.EnumSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.ArrayList;
import java.util.List;

public class Particles extends Module {
// leaked by itskekoff; discord.gg/sk3d MvhtpbAm

    private final EnumSetting typep = new EnumSetting("Спавнить при",
            new BooleanSetting("Бездействии", true),
            new BooleanSetting("Движении", true),
            new BooleanSetting("Крите", true),
            new BooleanSetting("Броске", false),
            new BooleanSetting("Тотеме", true)
    );

    public EnumSetting getTypep() {
        return typep;
    }

    private final SliderSetting countAFK = new SliderSetting("Кол-во при бездействии", 5, 1, 25, 1).setVisible(() -> typep.get("Бездействии").get());
    private final SliderSetting countAttack = new SliderSetting("Кол-во при крите", 2, 1, 25, 1).setVisible(() -> typep.get("Крите").get());
    private final SliderSetting countMove = new SliderSetting("Кол-во при движении", 2, 1, 25, 1).setVisible(() -> typep.get("Движении").get());
    private final SliderSetting size = new SliderSetting("Размер", 0.5F, 0.0F, 1F, 0.1F);
    private final SliderSetting range = new SliderSetting("Дистанция", 16, 4, 32, 1);
    private final SliderSetting duration = new SliderSetting("Время жизни", 3500, 500, 5000, 250);
    private final SliderSetting strength = new SliderSetting("Скорость движения", 1.0F, 0.1F, 2.0F, 0.1F);
    private final SliderSetting opacity = new SliderSetting("Прозрачность", 1.0F, 0.1F, 1.0F, 0.1F);
    
    private final SliderSetting critSize = new SliderSetting("Размер при крите", 0.5F, 0.0F, 1F, 0.1F).setVisible(() -> typep.get("Крите").get());
    private final SliderSetting critDuration = new SliderSetting("Время жизни при крите", 3500, 500, 5000, 250).setVisible(() -> typep.get("Крите").get());
    private final SliderSetting critStrength = new SliderSetting("Скорость движения при крите", 1.0F, 0.1F, 2.0F, 0.1F).setVisible(() -> typep.get("Крите").get());

    private static final float TOTEM_SIZE = 0.25F;
    private static final int TOTEM_COUNT = 75;
    private static final float TOTEM_STRENGTH = 4F;
    private static final long TOTEM_DURATION = 40000L;
    private static final int TOTEM_COLOR_GREEN = 0x00FF00;
    private static final int TOTEM_COLOR_YELLOW = 0xFFFF00;

    private final float THROW_SIZE = 0.4F;
    private final float THROW_STRENGTH = 2.0F;
    private final int THROW_COUNT = 2;

    private final BooleanSetting glowing = new BooleanSetting("Свечение", true);
    private final BooleanSetting onlyMove = new BooleanSetting("Только в движении", false);
    private final BooleanSetting ground = new BooleanSetting("Спавнить на земле", false);
    private final BooleanSetting physic = new BooleanSetting("Физика", false);
    private final RadioSetting colorMode = new RadioSetting("Режим цвета", "Клиентский", "Клиентский", "Радужный");
    private final RadioSetting particleMode = new RadioSetting("Тип частиц",
            "Бубенцы",
            "Бубенцы",
            "Крестики",
            "Короны",
            "Доллары",
            "Сердечки",
            "Снежинки",
            "Звездочки",
            "Черепа",
            "Рандом"
    );

    private static Particles instance;
    
    public Particles() {
        super("Particles", Category.Visuals, true, "Видимые партиклы с настройками");
        addSettings(particleMode, typep, countAFK, countAttack, countMove, size, critSize, range, duration, critDuration, 
                    strength, critStrength, opacity,
                    glowing, onlyMove, ground, physic, colorMode);

        instance = this;
    }

    private final List<Particle> particles = new ArrayList<>();
    private final List<ParticleAttack> targetParticles = new ArrayList<>();
    private final List<ParticleAttack> flameParticles = new ArrayList<>();
    private final List<ParticleAttack> thrownParticles = new ArrayList<>();
    private final List<ParticleAttack> totemParticles = new ArrayList<>();

    private void clear() {
        particles.clear();
        targetParticles.clear();
        flameParticles.clear();
        thrownParticles.clear();
        totemParticles.clear();
    }

    @Override
    public void toggle() {
        super.toggle();
        clear();
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventAttack eventAttack) {
            if (!typep.get("Крите").get()) return;
            if (mc.player.fallDistance == 0) return;

            Entity target = eventAttack.getTarget();
            float motion = strength.get();

            for (int i = 0; i < countAttack.get().intValue(); i++) {
                spawnParticleAttack(targetParticles,
                        new Vector3d(target.getPosX(), target.getPosY() + MathUtil.randomValue(0, target.getHeight()), target.getPosZ()),
                        new Vector3d(MathUtil.randomValue(-motion, motion), MathUtil.randomValue(-motion, motion / 4F), MathUtil.randomValue(-motion, motion))
                );
            }
        }

        if (event instanceof EventMotion eventMotion) {

            if (typep.get("Движении").get() && hasPlayerMoved()) {
                if (!mc.gameSettings.getPointOfView().equals(PointOfView.FIRST_PERSON)) {
                    for (int i = 0; i < countMove.get().intValue(); i++) {
                        spawnParticleAttack(flameParticles,
                                new Vector3d(mc.player.getPosX() + MathUtil.randomValue(-0.5, 0.5),
                                        mc.player.getPosY() + MathUtil.randomValue(0, mc.player.getHeight()),
                                        mc.player.getPosZ() + MathUtil.randomValue(-0.5, 0.5)),
                                new Vector3d(mc.player.motion.x + MathUtil.randomValue(-0.25, 0.25),
                                        MathUtil.randomValue(-0.15, 0.15),
                                        mc.player.motion.z + MathUtil.randomValue(-0.25, 0.25)).mul(strength.get())
                        );
                    }
                }
            }


            if (typep.get("Бездействии").get()) {
                if (!onlyMove.get() || hasPlayerMoved()) {
                    int r = range.get().intValue();
                    for (int i = 0; i < countAFK.get().intValue(); i++) {
                        Vector3d additional = mc.player.getPositionVec().add(
                                MathUtil.randomValue(-r, r), 0, MathUtil.randomValue(-r, r)
                        );
                        BlockPos pos = mc.world.getHeight(Heightmap.Type.MOTION_BLOCKING, new BlockPos(additional));
                        spawnParticle(
                                new Vector3d(
                                        pos.getX() + MathUtil.randomValue(0, 1),
                                        ground.get() ? pos.getY() : mc.player.getPosY() + MathUtil.randomValue(mc.player.getHeight(), r),
                                        pos.getZ() + MathUtil.randomValue(0, 1)
                                ),
                                new Vector3d(0, MathUtil.randomValue(0.0, strength.get()) * (ground.get() ? 1 : -1), 0)
                        );
                    }
                }
            }
            if (typep.get("Броске").get()) {
                for (Entity entity : mc.world.getAllEntities()) {
                    if (entity instanceof net.minecraft.entity.item.EnderPearlEntity || 
                        entity instanceof net.minecraft.entity.projectile.ArrowEntity || 
                        entity instanceof net.minecraft.entity.projectile.TridentEntity) {
                        
                        if (entity instanceof net.minecraft.entity.projectile.TridentEntity) {
                            TridentEntity trident = (TridentEntity) entity;
                            if (trident.func_234616_v_() != null && trident.dealtDamage) {
                                continue;
                            }
                        }
                        
                        boolean isMoving = entity.prevPosX != entity.getPosX() || 
                                           entity.prevPosY != entity.getPosY() || 
                                           entity.prevPosZ != entity.getPosZ();
                        if (!isMoving) {
                            continue;
                        }
                        Vector3d pos = entity.getPositionVec();
                        for (int i = 0; i < THROW_COUNT; i++) {
                            spawnParticleThrown(
                                new Vector3d(
                                    pos.x + MathUtil.randomValue(-0.2, 0.2),
                                    pos.y + MathUtil.randomValue(-0.2, 0.2),
                                    pos.z + MathUtil.randomValue(-0.2, 0.2)
                                ),
                                new Vector3d(
                                    entity.getMotion().x * 0.1 + MathUtil.randomValue(-0.1, 0.1),
                                    entity.getMotion().y * 0.1 + MathUtil.randomValue(-0.1, 0.1),
                                    entity.getMotion().z * 0.1 + MathUtil.randomValue(-0.1, 0.1)
                                )
                            );
                        }
                    }
                }
            }

            removeExpiredParticlesAttack(targetParticles, 5000);
            removeExpiredParticlesAttack(flameParticles, 3500);
            removeExpiredParticlesAttack(thrownParticles, 3500);
            removeExpiredParticlesAttack(totemParticles, TOTEM_DURATION);
        }


        if (event instanceof WorldChangeEvent) {
            clear();
        }


        if (event instanceof Render3DPosedEvent render3DPosedEvent) {
            MatrixStack matrix = render3DPosedEvent.getMatrix();

            setupRenderState();

            if (typep.get("Бездействии").get()) {
                renderParticles(matrix, particles, duration.get().doubleValue(), duration.min);
            }

            if (typep.get("Крите").get() || typep.get("Движении").get() || typep.get("Броске").get() || typep.get("Тотеме").get()) {
                renderParticlesAttack(matrix, targetParticles, 500, 2000);
                renderParticlesAttack(matrix, flameParticles, 500, 3000);
                renderParticlesAttack(matrix, thrownParticles, 500, 3000);
                renderParticlesAttack(matrix, totemParticles, 1000, TOTEM_DURATION - 1000);
            }

            resetRenderState();
        }
    }

    private void renderParticles(MatrixStack matrix, List<Particle> particles, double lifetime, double duration) {
        removeExpiredParticles(particles, lifetime + duration);
        if (particles.isEmpty()) return;

        matrix.push();
        for (Particle particle : particles) {
            particle.update(physic.get());
            Animation animation = particle.animation();
            animation.update();
            float alpha = animation.get();

            if (alpha != opacity.get() && !particle.time().finished(duration)) {
                animation.run(opacity.get(), (duration / 1000), Easings.CUBIC_OUT, true);
            }
            if (alpha != 0.0F && particle.time().finished(lifetime)) {
                animation.run(0.0F, (duration / 1000), Easings.CUBIC_OUT, true);
            }

            int color = ColorUtils.multAlpha(ColorUtils.replAlpha(particle.color(), alpha), (float) ((Math.sin((System.currentTimeMillis() - particle.spawnTime()) / 200D) + 1F) / 2F));
            Vector3d vec = particle.position();
            float x = (float) vec.x;
            float y = (float) vec.y;
            float z = (float) vec.z;

            renderParticle(matrix, particle, x, y, z, color);
        }
        matrix.pop();
    }

    private void removeExpiredParticles(List<Particle> particles, double lifespan) {
        particles.removeIf(particle -> !PlayerUtil.isInView(particle.box));
        particles.removeIf(particle -> particle.time().finished(lifespan));
    }

    private void removeExpiredParticlesAttack(List<ParticleAttack> particles, long lifespan) {
        if (particles == totemParticles) {
            particles.removeIf(particle -> particle.time().finished(lifespan));
        } else {
            particles.removeIf(particle -> !PlayerUtil.isInView(particle.box));
            particles.removeIf(particle -> particle.time().finished(lifespan));
        }
    }

    private void renderParticle(MatrixStack matrix, Particle particle, float x, float y, float z, int color) {
        float pos = particle.size;
        matrix.push();
        RenderUtil3D.setupOrientationMatrix(matrix, x, y, z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        matrix.push();
        matrix.rotate(Vector3f.ZP.rotationDegrees(180F));
        if (particle.type().rotatable()) matrix.rotate(Vector3f.ZP.rotationDegrees(particle.rotate()));
        matrix.push();
        matrix.translate(0, -pos, -pos);
        if (glowing.get()) {
            RenderUtil.bindTexture(ParticleType.bubenci.texture());
            RenderUtil.drawRect(matrix, -pos * 4, -pos * 4, pos * 8, pos * 8, ColorUtils.multAlpha(color, 0.1F), true, true);
        }
        RenderUtil.bindTexture(particle.type().texture());
        RenderUtil.drawRect(matrix, -pos, -pos, pos * 2, pos * 2, color, true, true);
        if (particle.type.equals(ParticleType.bubenci)) {
            RenderUtil.drawRect(matrix, -pos / 2, -pos / 2, pos, pos, color, true, true);
        }
        matrix.pop();
        matrix.pop();
        matrix.pop();
    }

    private void setupRenderState() {
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
    }

    private void resetRenderState() {
        RenderSystem.blendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );
        RenderSystem.clearCurrentColor();
        RenderSystem.enableCull();
        RenderSystem.depthMask(true);
        RenderSystem.enableAlphaTest();
    }

    private void renderParticlesAttack(MatrixStack matrix, List<ParticleAttack> particles, long fadeInTime, long fadeOutTime) {
        if (particles.isEmpty()) return;

        matrix.push();
        for (ParticleAttack particle : particles) {
            boolean usePhysics = physic.get() || particles == totemParticles;
            particle.update(usePhysics);
            particle.animation.update();

            long lifetime = typep.get("Крите").get() ? critDuration.get().longValue() : fadeOutTime;

            if (particle.animation().getValue() != opacity.get() && !particle.time().finished(fadeInTime)) {
                particle.animation().run(opacity.get(), 0.5, Easings.CUBIC_OUT, true);
            }
            if (particle.animation().getValue() != 0 && particle.time().finished(lifetime)) {
                particle.animation().run(0, 0.5, Easings.CUBIC_OUT, true);
            }

            int color = ColorUtils.replAlpha(particle.color(), particle.animation.get());
            Vector3d vec = particle.position();
            float x = (float) vec.x;
            float y = (float) vec.y;
            float z = (float) vec.z;

            renderParticleAttack(matrix, particle, x, y, z, particle.size, color);
        }
        matrix.pop();
    }

    private void renderParticleAttack(MatrixStack matrix, ParticleAttack particle, float x, float y, float z, float pos, int color) {
        matrix.push();
        RenderUtil3D.setupOrientationMatrix(matrix, x, y, z);
        matrix.rotate(mc.getRenderManager().getCameraOrientation());
        matrix.push();
        matrix.rotate(Vector3f.ZP.rotationDegrees(180F));
        if (particle.type().rotatable()) matrix.rotate(Vector3f.ZP.rotationDegrees(particle.rotate()));
        matrix.push();
        matrix.translate(0, -pos, 0);
        if (glowing.get()) {
            RenderUtil.bindTexture(ParticleType.bubenci.texture());
            RenderUtil.drawRect(matrix, -pos * 4, -pos * 4, pos * 8, pos * 8, ColorUtils.multAlpha(color, 0.1F), true, true);
        }
        RenderUtil.bindTexture(particle.type().texture());
        RenderUtil.drawRect(matrix, -pos, -pos, pos * 2, pos * 2, color, color, color, color, true, true);
        if (particle.type.equals(ParticleType.bubenci)) {
            RenderUtil.drawRect(matrix, -pos / 2, -pos / 2, pos, pos, color, true, true);
        }
        matrix.pop();
        matrix.pop();
        matrix.pop();
    }

    private void spawnParticle(Vector3d position, Vector3d velocity) {
        float size = 0.05F + (this.size.get() * 0.2F);
        int color = switch (this.colorMode.get()) {
            case "Клиентский" -> ColorUtils.fade(particles.size() * 100);
            case "Радужный" -> ColorUtils.rainbow(4, particles.size() * 100, 0.5F, 1F, 1F);
            default -> -1;
        };

        ParticleType type = switch (this.particleMode.get()) {
            case "Бубенцы" -> ParticleType.bubenci;
            case "Крестики" -> ParticleType.cross;
            case "Короны" -> ParticleType.crown;
            case "Доллары" -> ParticleType.dollar;
            case "Сердечки" -> ParticleType.heart;
            case "Снежинки" -> ParticleType.snowflake;
            case "Звездочки" -> ParticleType.star;
            case "Черепа" -> ParticleType.skull;
            default -> ParticleType.getRandom();
        };

        particles.add(new Particle(type,
                position.add(0, size, 0),
                velocity,
                particles.size(),
                color,
                size,
                (int) MathUtil.step(MathUtil.randomValue(0, 360), 15))
        );
    }

    private void spawnParticleAttack(List<ParticleAttack> particles, Vector3d position, Vector3d velocity) {
        float size = 0.05F + (typep.get("Крите").get() ? (this.critSize.get() * 0.2F) : (this.size.get() * 0.2F));
        int color = switch (this.colorMode.get()) {
            case "Клиентский" -> ColorUtils.fade(particles.size() * 100);
            case "Радужный" -> ColorUtils.rainbow(4, particles.size() * 100, 0.5F, 1F, 1F);
            default -> -1;
        };

        ParticleType type = switch (this.particleMode.get()) {
            case "Бубенцы" -> ParticleType.bubenci;
            case "Крестики" -> ParticleType.cross;
            case "Короны" -> ParticleType.crown;
            case "Доллары" -> ParticleType.dollar;
            case "Сердечки" -> ParticleType.heart;
            case "Снежинки" -> ParticleType.snowflake;
            case "Звездочки" -> ParticleType.star;
            case "Черепа" -> ParticleType.skull;
            default -> ParticleType.getRandom();
        };

        Vector3d motion = velocity.mul(typep.get("Крите").get() ? this.critStrength.get() : this.strength.get());

        particles.add(new ParticleAttack(type,
                position.add(0, size, 0),
                motion,
                particles.size(),
                (int) MathUtil.step(MathUtil.randomValue(0, 360), 15),
                color,
                size)
        );
    }

    private void spawnParticleThrown(Vector3d position, Vector3d velocity) {
        float size = 0.05F + (THROW_SIZE * 0.2F);
        int color = switch (this.colorMode.get()) {
            case "Клиентский" -> ColorUtils.fade(thrownParticles.size() * 100);
            case "Радужный" -> ColorUtils.rainbow(4, thrownParticles.size() * 100, 0.5F, 1F, 1F);
            default -> -1;
        };

        ParticleType type = switch (this.particleMode.get()) {
            case "Бубенцы" -> ParticleType.bubenci;
            case "Крестики" -> ParticleType.cross;
            case "Короны" -> ParticleType.crown;
            case "Доллары" -> ParticleType.dollar;
            case "Сердечки" -> ParticleType.heart;
            case "Снежинки" -> ParticleType.snowflake;
            case "Звездочки" -> ParticleType.star;
            case "Черепа" -> ParticleType.skull;
            default -> ParticleType.getRandom();
        };
        Vector3d motion = velocity.mul(THROW_STRENGTH);

        thrownParticles.add(new ParticleAttack(type,
                position.add(0, size, 0),
                motion,
                thrownParticles.size(),
                (int) MathUtil.step(MathUtil.randomValue(0, 360), 15),
                color,
                size)
        );
    }

    private void spawnTotemParticle(Vector3d position, Vector3d velocity, int color) {
        ParticleType type = switch (this.particleMode.get()) {
            case "Бубенцы" -> ParticleType.bubenci;
            case "Крестики" -> ParticleType.cross;
            case "Короны" -> ParticleType.crown;
            case "Доллары" -> ParticleType.dollar;
            case "Сердечки" -> ParticleType.heart;
            case "Снежинки" -> ParticleType.snowflake;
            case "Звездочки" -> ParticleType.star;
            case "Черепа" -> ParticleType.skull;
            default -> ParticleType.getRandom();
        };
        
        float size = TOTEM_SIZE;
        if (type == ParticleType.star || type == ParticleType.crown || 
            type == ParticleType.skull || type == ParticleType.heart) {
            size = 0.1F;
        }

        totemParticles.add(new ParticleAttack(type,
                position.add(MathUtil.randomValue(-0.3, 0.3), MathUtil.randomValue(-0.3, 0.3), MathUtil.randomValue(-0.3, 0.3)),
                velocity.mul(TOTEM_STRENGTH),
                totemParticles.size(),
                (int) MathUtil.step(MathUtil.randomValue(0, 360), 15),
                color,
                size)
        );
    }

    private boolean hasPlayerMoved() {
        return mc.player.lastTickPosX != mc.player.getPosX()
                || mc.player.lastTickPosY != mc.player.getPosY()
                || mc.player.lastTickPosZ != mc.player.getPosZ();
    }

    private Vector3d getNextMotion(net.minecraft.entity.projectile.ThrowableEntity throwable, Vector3d motion) {
        Vector3d nextMotion = motion;
        
        if (throwable.isInWater()) {
            nextMotion = nextMotion.scale(0.8);
        } else {
            nextMotion = nextMotion.scale(0.99);
        }
        
        if (!throwable.hasNoGravity()) {
            nextMotion = new Vector3d(nextMotion.x, nextMotion.y - throwable.getGravityVelocity(), nextMotion.z);
        }
        
        return nextMotion;
    }

    @Getter
    @Accessors(fluent = true)
    public enum ParticleType {
        bubenci("bloom", false),
        cross("cross", false),
        crown("crown", false),
        dollar("dollar", false),
        heart("heart", false),
        point("point", false),
        snowflake("snowflake", false),
        star("star", false),
        skull("skull",false);

        private final ResourceLocation texture;
        private final boolean rotatable;

        ParticleType(String name, boolean rotatable) {
            texture = new ResourceLocation("night/image/particles/" + name + ".png");
            this.rotatable = rotatable;
        }

        public static ParticleType getRandom() {
            ParticleType[] values = ParticleType.values();
            return values[new FastRandom().nextInt(values.length)];
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class Particle {
        private final long spawnTime = System.currentTimeMillis();
        private final ParticleType type;
        private final AxisAlignedBB box;
        private Vector3d position;
        private Vector3d velocity;
        private final int rotate;
        private final int index;
        private final int color;
        private final float size;

        private final TimerUtil time = new TimerUtil();
        private final Animation animation = new Animation();

        public Particle(ParticleType type, final Vector3d position, final Vector3d velocity, final int index, int color, float size, int rotate) {
            this.type = type;
            this.rotate = rotate;
            this.box = new AxisAlignedBB(position, position).grow(size);
            this.position = position;
            this.velocity = velocity.mul(0.01F);
            this.index = index;
            this.color = color;
            this.size = size;
            this.time.reset();
        }

        public void update(boolean physic) {
            if (physic) {
                if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                    this.velocity = this.velocity.mul(1, 1, -0.8);
                }
                if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                    this.velocity = this.velocity.mul(0.999, -0.6, 0.999);
                }
                if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                    this.velocity = this.velocity.mul(-0.8, 1, 1);
                }
                this.velocity = this.velocity.mul(0.999999).subtract(new Vector3d(0, 0.00005, 0));
            }
            this.position = this.position.add(this.velocity);
        }
    }

    @Getter
    @Accessors(fluent = true)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class ParticleAttack {
        AxisAlignedBB box;
        ParticleType type;
        @NonFinal
        Vector3d position;
        @NonFinal
        Vector3d velocity;
        int index;
        int rotate;
        int color;

        float size;

        TimerUtil time = new TimerUtil();
        Animation animation = new Animation();

        public ParticleAttack(ParticleType type, final Vector3d position, final Vector3d velocity, final int index, int rotate, int color, float size) {
            this.box = new AxisAlignedBB(position, position).grow(size);
            this.type = type;
            this.position = position;
            this.velocity = velocity.mul(0.01F);
            this.index = index;
            this.rotate = rotate;
            this.color = color;
            this.size = size;
            this.time.reset();
        }

        public void update(boolean physic) {
            if (physic) {
                if (PlayerUtil.isBlockSolid(this.position.x, this.position.y, this.position.z + this.velocity.z)) {
                    this.velocity = this.velocity.mul(1, 1, -0.8);
                }
                if (PlayerUtil.isBlockSolid(this.position.x, this.position.y + this.velocity.y, this.position.z)) {
                    this.velocity = this.velocity.mul(0.999, -0.6, 0.999);
                }
                if (PlayerUtil.isBlockSolid(this.position.x + this.velocity.x, this.position.y, this.position.z)) {
                    this.velocity = this.velocity.mul(-0.8, 1, 1);
                }
                this.velocity = this.velocity.mul(0.999999).subtract(new Vector3d(0, 0.00005, 0));
            }
            this.position = this.position.add(this.velocity);
        }
    }
    public static void onEntityStatus(byte id, Entity entity) {
        if (id == 35 && entity instanceof LivingEntity) {
            Particles particles = instance;
            if (particles != null && particles.isState() && particles.typep.get("Тотеме").get()) {
                particles.createTotemEffect(entity.getPosX(), entity.getPosY() + entity.getHeight() / 2, entity.getPosZ());
            }
        }
    }

    public void createTotemEffect(double x, double y, double z) {
        if (!isState() || !typep.get("Тотеме").get()) return;

        for (int i = 0; i < TOTEM_COUNT; i++) {

            double angleXZ = Math.random() * Math.PI * 2;
            double angleY = Math.random() * Math.PI;
            double strength = 0.5 + Math.random() * 0.5;
            
            Vector3d velocity = new Vector3d(
                Math.sin(angleXZ) * Math.sin(angleY) * strength,
                Math.cos(angleY) * strength,
                Math.cos(angleXZ) * Math.sin(angleY) * strength
            );
            

            int color = Math.random() < 0.7 ? TOTEM_COLOR_GREEN : TOTEM_COLOR_YELLOW;

            spawnTotemParticle(new Vector3d(x, y, z), velocity, color);
        }
    }
}
