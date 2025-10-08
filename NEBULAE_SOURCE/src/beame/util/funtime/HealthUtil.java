package beame.util.funtime;

import beame.util.IMinecraft;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;

import java.util.Iterator;
import java.util.Map;

public class HealthUtil {
// leaked by itskekoff; discord.gg/sk3d 6WsUBC0u
    public static float getHealth(PlayerEntity entity) {
        Iterator<Map.Entry<ScoreObjective, Score>> iterator;
        if ((iterator = IMinecraft.mc.world.getScoreboard().getObjectivesForEntity(entity.getName().getString()).entrySet().iterator()).hasNext()) {
            Map.Entry<ScoreObjective, Score> entry = iterator.next();
            ScoreObjective objective = entry.getKey();
            Score score = entry.getValue();
            return score.getScorePoints();
        }
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public static float getHealthMonster(MobEntity entity) {
        Iterator<Map.Entry<ScoreObjective, Score>> iterator;
        if ((iterator = IMinecraft.mc.world.getScoreboard().getObjectivesForEntity(entity.getName().getString()).entrySet().iterator()).hasNext()) {
            Map.Entry<ScoreObjective, Score> entry = iterator.next();
            ScoreObjective objective = entry.getKey();
            Score score = entry.getValue();
            return score.getScorePoints();
        }
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

    public static float getHealthAnimal(AnimalEntity entity) {
        Iterator<Map.Entry<ScoreObjective, Score>> iterator;
        if ((iterator = IMinecraft.mc.world.getScoreboard().getObjectivesForEntity(entity.getName().getString()).entrySet().iterator()).hasNext()) {
            Map.Entry<ScoreObjective, Score> entry = iterator.next();
            ScoreObjective objective = entry.getKey();
            Score score = entry.getValue();
            return score.getScorePoints();
        }
        return entity.getHealth() + entity.getAbsorptionAmount();
    }

}
