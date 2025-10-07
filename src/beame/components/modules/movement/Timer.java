/*
package beame.feature.features.Movement;

import beame.Nebulae;
import beame.command.api.CMD;
import beame.util.math.MathUtil;
import events.Event;
import events.impl.player.EventMotion;
import beame.module.Category;
import beame.module.Module;
import beame.setting.SettingList.BooleanSetting;
import beame.setting.SettingList.RadioSetting;
import beame.setting.SettingList.SliderSetting;

import java.util.Random;

public class Timer extends Module {
    
    private final RadioSetting mode = new RadioSetting("Режим", "Пульс", "Обычный", "Пульс", "Ускорение");
    
    // Настройки для режима "Обычный"
    private final SliderSetting speed = new SliderSetting("Скорость", 2.0f, 0.1f, 10.0f, 0.1f).setVisible(() -> mode.get().equals("Обычный"));
    
    // Настройки для режима "Пульс"
    private final SliderSetting normalSpeed = new SliderSetting("Нормальная скорость", 0.42f, 0.4f, 0.5f, 0.01f).setVisible(() -> mode.get().equals("Пульс"));
    private final SliderSetting normalSpeedTicks = new SliderSetting("Тики норм. скорости", 1, 1, 500, 1).setVisible(() -> mode.get().equals("Пульс"));
    private final SliderSetting boostSpeed = new SliderSetting("Ускоренная скорость", 1.22f, 0.1f, 3.0f, 0.01f).setVisible(() -> mode.get().equals("Пульс"));
    private final SliderSetting boostSpeedTicks = new SliderSetting("Тики ускорения", 8, 7, 9, 1).setVisible(() -> mode.get().equals("Пульс"));
    private final BooleanSetting onMove = new BooleanSetting("Только при движении", false).setVisible(() -> mode.get().equals("Пульс"));
    
    // Настройки для режима "Ускорение"
    private final SliderSetting boost = new SliderSetting("Ускорение", 1.3f, 0.1f, 10.0f, 0.1f).setVisible(() -> mode.get().equals("Ускорение"));
    private final SliderSetting slow = new SliderSetting("Замедление", 0.6f, 0.1f, 10.0f, 0.1f).setVisible(() -> mode.get().equals("Ускорение"));
    private final SliderSetting timeBoostTicks = new SliderSetting("Тики ускорения", 12, 1, 60, 1).setVisible(() -> mode.get().equals("Ускорение"));
    private final BooleanSetting accountTimerValue = new BooleanSetting("Учитывать значения таймера", true).setVisible(() -> mode.get().equals("Ускорение"));
    private final BooleanSetting normalizeDuringCombat = new BooleanSetting("Нормализация в бою", true).setVisible(() -> mode.get().equals("Ускорение"));
    private final BooleanSetting allowNegative = new BooleanSetting("Разрешить отрицательные", false).setVisible(() -> mode.get().equals("Ускорение"));

    // Переменные для режима "Пульс"
    private enum TimerState { NORMAL_SPEED, BOOST_SPEED }
    private TimerState currentState = TimerState.NORMAL_SPEED;
    private int tickCounter = 0;
    private int randomTickValue = 8; // Случайное значение от 8 до 16
    private final Random random = new Random();
    
    // Переменные для режима "Ускорение"
    private int boostCapable = 0;
    private int waitTicks = 0;

    public Timer() {
        super("Timer", Category.Movement, true, "Ускоряет время игры");
        addSettings(mode, speed, normalSpeed, normalSpeedTicks, boostSpeed, boostSpeedTicks, onMove, 
                    boost, slow, timeBoostTicks, accountTimerValue, normalizeDuringCombat, allowNegative);
    }

    @Override
    public void onEnable() {
        super.onEnable();
        currentState = TimerState.NORMAL_SPEED;
        tickCounter = 0;
        boostCapable = 0;
        waitTicks = 0;
        updateRandomValue();
    }

    @Override
    public void onDisable() {
        super.onDisable();
        mc.timer.timerSpeed = 1.0f;
    }

    private void updateRandomValue() {
        randomTickValue = (int) (random.nextInt(9) + 3.7); // Случайное значение от 8 до 16
    }

    @Override
    public void event(Event event) {
        if (event instanceof EventMotion) {
            if (waitTicks > 0) {
                waitTicks--;
                return;
            }

            if (mode.get().equals("Обычный")) {
                handleClassicMode();
            } else if (mode.get().equals("Пульс")) {
                handlePulseMode();
            } else if (mode.get().equals("Ускорение")) {
                handleBoostMode();
            }
        }
    }

    private void handleClassicMode() {
        mc.timer.timerSpeed = speed.get().floatValue();
    }

    private void handlePulseMode() {
        boolean isMoving = mc.player.moveForward != 0 || mc.player.moveStrafing != 0;
        
        if (onMove.get() && !isMoving) {
            return;
        }
        
        tickCounter++;
        
        if (currentState == TimerState.NORMAL_SPEED && tickCounter >= normalSpeedTicks.get().intValue()) {
            currentState = TimerState.BOOST_SPEED;
            tickCounter = 0;
            mc.timer.timerSpeed = boostSpeed.get().floatValue();
            updateRandomValue();
        } else if (currentState == TimerState.BOOST_SPEED && tickCounter >= randomTickValue) {
            currentState = TimerState.NORMAL_SPEED;
            tickCounter = 0;
            mc.timer.timerSpeed = normalSpeed.get().floatValue();
            updateRandomValue();
        } else if (currentState == TimerState.NORMAL_SPEED) {
            mc.timer.timerSpeed = normalSpeed.get().floatValue();
        } else if (currentState == TimerState.BOOST_SPEED) {
            mc.timer.timerSpeed = boostSpeed.get().floatValue();
        }
    }

    private void handleBoostMode() {
        boolean isInCombat = Nebulae.getHandler().auraHelper != null && Nebulae.getHandler().getModuleList().aura.isState() && Nebulae.getHandler().getModuleList().aura.player != null;
        boolean isMoving = mc.player.moveForward != 0 || mc.player.moveStrafing != 0;
        
        if (normalizeDuringCombat.get() && isInCombat) {
            mc.timer.timerSpeed = 1.0f;
            return;
        }
        
        if (boostCapable < 0) {
            int ticks = Math.abs(boostCapable);
            mc.timer.timerSpeed = slow.get().floatValue();
            CMD.addMessage("Замедление на " + ticks + " тиков");
            boostCapable = 0;
            waitTicks = ticks;
            return;
        }
        
        if (!isMoving) {
            if (mc.currentScreen != null) {
                boostCapable = 0;
                return;
            }
            
            mc.timer.timerSpeed = slow.get().floatValue();
            
            int addition = accountTimerValue.get() ? (int)(1 / slow.get().floatValue()) : 1;
            boostCapable = Math.min(boostCapable + addition, timeBoostTicks.get().intValue());
        } else {
            boolean speedUp = boostCapable > 0 || (allowNegative.get() && isInCombat);
            
            if (!speedUp) {
                return;
            }
            
            int ticks = boostCapable > 0 ? boostCapable : timeBoostTicks.get().intValue();
            int speedUpTicks = accountTimerValue.get() ? (int)Math.ceil(ticks / boost.get().floatValue()) : ticks;
            
            if (speedUpTicks == 0) {
                return;
            }
            
            mc.timer.timerSpeed = boost.get().floatValue();
            CMD.addMessage("Ускорение на " + speedUpTicks + " тиков");
            boostCapable -= ticks;
            waitTicks = speedUpTicks;
        }
    }
}*/
// leaked by itskekoff; discord.gg/sk3d ske2DMWT
