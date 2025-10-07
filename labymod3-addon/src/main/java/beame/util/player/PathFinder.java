package beame.util.player;


import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import java.util.*;

public class PathFinder {
// leaked by itskekoff; discord.gg/sk3d hu66WHMh
    private static final Minecraft mc = Minecraft.getInstance();
    private static final int MAX_ITERATIONS = 1000;
    private static final int MAX_DISTANCE = 100;

    private static class Node implements Comparable<Node> {
        BlockPos pos;
        Node parent;
        double g; // Стоимость от старта до текущей точки
        double h; // Эвристическая оценка до цели
        double f; // Общая стоимость (g + h)

        Node(BlockPos pos, Node parent, double g, double h) {
            this.pos = pos;
            this.parent = parent;
            this.g = g;
            this.h = h;
            this.f = g + h;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.f, other.f);
        }
    }

    public List<BlockPos> findPath(BlockPos start, BlockPos end) {
        if (start.manhattanDistance(end) > MAX_DISTANCE) {
            return null; // Цель слишком далеко
        }

        PriorityQueue<Node> openSet = new PriorityQueue<>();
        Set<BlockPos> closedSet = new HashSet<>();

        Node startNode = new Node(start, null, 0, heuristic(start, end));
        openSet.add(startNode);

        int iterations = 0;
        while (!openSet.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;
            Node current = openSet.poll();

            if (current.pos.equals(end)) {
                return reconstructPath(current);
            }

            closedSet.add(current.pos);

            for (BlockPos neighbor : getNeighbors(current.pos)) {
                if (closedSet.contains(neighbor)) {
                    continue;
                }

                double tentativeG = current.g + getMovementCost(current.pos, neighbor);

                Node neighborNode = new Node(neighbor, current, tentativeG, heuristic(neighbor, end));

                if (!openSet.contains(neighborNode) || tentativeG < neighborNode.g) {
                    openSet.add(neighborNode);
                }
            }
        }

        return null; // Путь не найден
    }

    private double heuristic(BlockPos start, BlockPos end) {
        // Используем расстояние по прямой как эвристику
        return start.distanceSq(end);
    }

    private List<BlockPos> getNeighbors(BlockPos pos) {
        List<BlockPos> neighbors = new ArrayList<>();

        // Проверяем соседние блоки (включая диагональные)
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 1; y++) {
                    if (x == 0 && y == 0 && z == 0) continue;

                    BlockPos neighbor = pos.add(x, y, z);
                    if (isValidPosition(neighbor)) {
                        neighbors.add(neighbor);
                    }
                }
            }
        }

        return neighbors;
    }

    private boolean isValidPosition(BlockPos pos) {
        // Проверяем, можно ли ходить по этой позиции
        if (mc.world == null) return false;

        BlockPos up = pos.up();
        BlockPos down = pos.down();

        // Проверяем, что блок проходим
        boolean isPassable = mc.world.getBlockState(pos).getMaterial().isReplaceable() &&
                mc.world.getBlockState(up).getMaterial().isReplaceable();

        // Проверяем, что есть на чем стоять
        boolean hasGround = !mc.world.getBlockState(down).getMaterial().isReplaceable();

        // Проверяем опасные блоки
        boolean isDangerous = mc.world.getBlockState(pos).getMaterial().isLiquid() ||
                mc.world.getBlockState(pos).getMaterial() == Material.FIRE;

        return isPassable && hasGround && !isDangerous;
    }

    private double getMovementCost(BlockPos from, BlockPos to) {
        // Базовая стоимость движения
        double cost = from.distanceSq(to);

        // Увеличиваем стоимость для вертикального движения
        if (from.getY() != to.getY()) {
            cost *= 1.5;
        }

        // Увеличиваем стоимость для диагонального движения
        if (from.getX() != to.getX() && from.getZ() != to.getZ()) {
            cost *= 1.4;
        }

        // Проверяем наличие препятствий
        if (hasObstacles(from, to)) {
            cost *= 2;
        }

        return cost;
    }

    private boolean hasObstacles(BlockPos from, BlockPos to) {
        // Проверяем наличие препятствий между двумя точками
        BlockPos diff = to.subtract(from);
        int steps = Math.max(Math.abs(diff.getX()), Math.max(Math.abs(diff.getY()), Math.abs(diff.getZ())));

        for (int i = 1; i < steps; i++) {
            BlockPos pos = from.add(
                    diff.getX() * i / steps,
                    diff.getY() * i / steps,
                    diff.getZ() * i / steps
            );

            if (!mc.world.getBlockState(pos).getMaterial().isReplaceable()) {
                return true;
            }
        }

        return false;
    }

    private List<BlockPos> reconstructPath(Node endNode) {
        List<BlockPos> path = new ArrayList<>();
        Node current = endNode;

        while (current != null) {
            path.add(0, current.pos);
            current = current.parent;
        }

        // Оптимизируем путь, удаляя лишние точки
        return optimizePath(path);
    }

    private List<BlockPos> optimizePath(List<BlockPos> path) {
        if (path.size() <= 2) return path;

        List<BlockPos> optimizedPath = new ArrayList<>();
        optimizedPath.add(path.get(0));

        for (int i = 1; i < path.size() - 1; i++) {
            BlockPos prev = path.get(i - 1);
            BlockPos current = path.get(i);
            BlockPos next = path.get(i + 1);

            // Если точки не на одной линии, добавляем текущую точку
            if (!isInLine(prev, current, next)) {
                optimizedPath.add(current);
            }
        }

        optimizedPath.add(path.get(path.size() - 1));
        return optimizedPath;
    }

    private boolean isInLine(BlockPos a, BlockPos b, BlockPos c) {
        // Проверяем, находятся ли три точки на одной линии
        return (b.getX() - a.getX()) * (c.getZ() - a.getZ()) ==
                (c.getX() - a.getX()) * (b.getZ() - a.getZ()) &&
                (b.getX() - a.getX()) * (c.getY() - a.getY()) ==
                        (c.getX() - a.getX()) * (b.getY() - a.getY());
    }
}