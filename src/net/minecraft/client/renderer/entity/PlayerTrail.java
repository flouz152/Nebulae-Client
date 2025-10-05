package net.minecraft.client.renderer.entity;

import lombok.Getter;
import net.minecraft.util.math.vector.Vector3d;

import java.util.LinkedList;
import java.util.Queue;

public class PlayerTrail {
// leaked by itskekoff; discord.gg/sk3d hJmPpios
    private final Queue<TrailPosition> trailPositions = new LinkedList<>();
    private final int trailLength;
    private final double modelDistance;

    public PlayerTrail(int trailLength, double modelDistance) {
        this.trailLength = trailLength;
        this.modelDistance = modelDistance;
    }

    public void addPosition(Vector3d position) {
        if (!trailPositions.isEmpty()) {
            Vector3d lastPosition = trailPositions.peek().position;
            if (lastPosition.distanceTo(position) < modelDistance) {
                return;
            }
        }
        trailPositions.add(new TrailPosition(position, System.currentTimeMillis(), 1.0f));
        if (trailPositions.size() > trailLength) {
            trailPositions.poll();
        }
    }

    public Queue<TrailPosition> getTrail() {
        return trailPositions;
    }

    static class TrailPosition {
        Vector3d position;
        long timestamp;
        float alpha;

        public TrailPosition(Vector3d position, long timestamp, float alpha) {
            this.position = position;
            this.timestamp = timestamp;
            this.alpha = alpha;
        }
    }
}
