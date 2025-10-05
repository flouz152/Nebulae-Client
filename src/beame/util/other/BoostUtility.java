/*
package beame.util.other;

import beame.Essence;
import beame.components.modules.combat.Aura;
import beame.components.modules.combat.AuraHandlers.other.AuraUtil;
import beame.components.modules.combat.ElytraTarget;
import beame.util.IMinecraft;
import beame.util.animation.AnimationMath;
import beame.util.math.TimerUtil;
import events.Event;
import events.impl.packet.EventPacket;
import events.impl.player.EventMotion2;
import events.impl.player.EventMotion2;
import events.impl.player.EventUpdate;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.play.server.SPlayerPositionLookPacket;
import net.minecraft.util.math.MathHelper;

@UtilityClass
public class BoostUtility implements IMinecraft {
    float speed = 1.5f;
    public final TimerUtil clean = new TimerUtil();
    public double lastSpeed;
    public boolean up;
    public int flagFactor;
    private float prevYaw;
    private int direction;

    public double getBoost(int ticks) {
        boolean passive = true;
        float realBoostable = passive ? 1.5f : 1.67f;
        float countableSpeed = realBoostable;
       // ElytraTarget elytratarget = Essence.getHandler().getModuleList().elytraTarget;
        Aura aura = Essence.getHandler().getModuleList().aura;
        LivingEntity target = aura.getTarget();

      */
/*  if (
                elytratarget.desync.get("Ударе (Defensive)").get()
                        && target != null
                        && AuraUtil.getClosestTargetPoint(target) != null
                        && !elytratarget.isLeaving(target)
        )*//*
 {
            if (EventMotion2.LAST_PITCH > 0) {
                countableSpeed = 1.5f;
            } else {
                countableSpeed = 1.5f;
            }
        }  {
            // if (!rock.getModules().get(ElytraTarget.class).isDefensiveActive()) {

            int[] vectors = {-45, 45, 135, -135};
            int[] addVectors = {-90, 90, 180, -180, 0};
            int[] pitchVectors = {-45, 45};

            float lastYaw = EventMotion2.LAST_YAW;
            float lastPitch = EventMotion2.LAST_PITCH;

            int minDist = findClosestVector(lastYaw, vectors);
            float maxDist = Math.abs(MathHelper.wrapDegrees(lastYaw) - vectors[minDist]);

            int addMinDist = findClosestVector(lastYaw, addVectors);
            float addMaxDist = Math.abs(MathHelper.wrapDegrees(lastYaw) - addVectors[addMinDist]);

            countableSpeed = (minDist == -1) ? 1.5f : 2.06f - maxDist * 0.56F / 45F;

            if (addMaxDist < 10) {
                countableSpeed += 0.1f - 0.1f * addMaxDist / 10F;
            }

            int pitchMinDist = findClosestVector(lastPitch, pitchVectors);
            float pitchMaxDist = Math.abs(Math.abs(lastPitch) - Math.abs(pitchVectors[pitchMinDist]));

            if (pitchMaxDist < 26) {
                countableSpeed = Math.max(1.94f, countableSpeed);

                countableSpeed += 0.05f - pitchMaxDist * 0.05F / 26F;
            }

            countableSpeed = Math.min(2.045f, countableSpeed);

            prevYaw = EventMotion2.LAST_YAW;


            if (EventMotion2.LAST_PITCH > -55 && EventMotion2.LAST_PITCH < -19f) {
                countableSpeed = 1.91f;
            } else if (EventMotion2.LAST_PITCH < -55) {
                countableSpeed = 1.54f;
            }

            if (EventMotion2.LAST_PITCH > 19f && EventMotion2.LAST_PITCH < 55) {
                countableSpeed = 1.8f;
            } else if (EventMotion2.LAST_PITCH > 55) {
                countableSpeed = 1.54f;
            }


        }

        return countableSpeed;
    }

    private static int findClosestVector(float lastYaw, int[] vectors) {
        int index = 0;
        int minDistIndex = -1;
        float minDist = Float.MAX_VALUE;

        for (int vector : vectors) {
            float dist = Math.abs(MathHelper.wrapDegrees(lastYaw) - vector);
            if (dist < minDist) {
                minDist = dist;
                minDistIndex = index;
            }
            index++;
        }

        return minDistIndex;
    }

    public float adjustValue(float value) {
		*/
/*
    	int[] vectors = {
    			-45,
    			45,
    			135,
    			-135,
    	};

		int index = 0;
    	int minDist = -1;
    	float maxDist = Float.MAX_VALUE;

    	for (int vector : vectors) {
    		float dist = Math.abs(MathHelper.wrapDegrees(value) - vector);

    		if (dist < maxDist) {
    			maxDist = dist;
    			minDist = index;
    		}

    		index++;
    	}

    	if (clean.passed(800) && minDist != direction) {
    		direction = minDist;
			clean.reset();
		}

    	index = 0;

    	for (int vector : vectors) {
    		float dist = Math.abs(MathHelper.wrapDegrees(value) - vector);

    		if (index == direction) {
    			maxDist = MathHelper.wrapDegrees(value) - vector;
    			minDist = index;
    		}
    		index++;
    	}
    	value = MathHelper.wrapDegrees(value) - maxDist;
    	Debugger.overlay(direction + " = " + value + "");
*//*

		*/
/*
		if (clean.passed(600)) {
			if (value < 0 != up) {
				clean.reset();
			}
		//	Debugger.overlay(up + " - " + clean.getElapsed());
			up = value < 0;
		}
		pitch.animate(value, 150);

		if (!up) {
			if (value < 19) {
				pitch.animate(20, 150);
		        return pitch.get();
		    }
		} else {
			if (value > -19) {
				pitch.animate(-20, 150);
		        return pitch.get();
		    }
		}
	*//*


        return value;
    }

    public void Event(Event event) {
        if (event instanceof EventPacket e) {
            if (e.getPacket() instanceof SPlayerPositionLookPacket packet) {
                flagFactor++;
                float speed = 1.5f;
            }
        }

        if (event instanceof EventUpdate) {
            //Bypass.send(EventMotion2.LAST_YAW-180, 90);
            //if (Math.abs(prevYaw - EventMotion2.LAST_YAW) > 30) {
            //	flag = false;
            //	}
            //	Debugger.overlay(Move.getSpeed()+"");
            //	prevYaw = EventMotion2.LAST_YAW;
        }
    }

}
*/
// leaked by itskekoff; discord.gg/sk3d uOIHRihB
