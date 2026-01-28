//package earthrp;
//
//import earthrp.customObjects.Town;
//import org.bukkit.*;
//import org.bukkit.block.Biome;
//
//import java.util.*;
//
//import earthrp.Earth.ChunkPosition;
//
//public class Pathfinding {
//
//    static final Set<Biome> waterBiomes = Set.of(
//            Biome.OCEAN,
//            Biome.COLD_OCEAN,
//            Biome.DEEP_OCEAN,
//            Biome.FROZEN_OCEAN,
//            Biome.WARM_OCEAN,
//            Biome.LUKEWARM_OCEAN,
//            Biome.DEEP_COLD_OCEAN,
//            Biome.DEEP_FROZEN_OCEAN,
//            Biome.DEEP_LUKEWARM_OCEAN,
//            Biome.RIVER,
//            Biome.FROZEN_RIVER
//    );
//
//    public static boolean isWaterChunk(Chunk chunk) {
//        for (Biome biome : waterBiomes) {
//            if (chunk.contains(biome)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public static boolean isChunkClaimedByOthers(int chunkX, int chunkZ, World world, Set<ChunkPosition> claimedChunks) {
//
//        return claimedChunks.contains(new ChunkPosition(chunkX,chunkZ,world.getName())); // Пример
//    }
//
//
//
//    public enum Direction {
//        EAST, SOUTHEAST, SOUTH, SOUTHWEST, WEST, NORTHWEST, NORTH, NORTHEAST;
//
//        public static Direction fromAngle(double angleDegrees) {
//            if (angleDegrees >= 337.5 || angleDegrees < 22.5) return EAST;
//            if (angleDegrees < 67.5) return SOUTHEAST;
//            if (angleDegrees < 112.5) return SOUTH;
//            if (angleDegrees < 157.5) return SOUTHWEST;
//            if (angleDegrees < 202.5) return WEST;
//            if (angleDegrees < 247.5) return NORTHWEST;
//            if (angleDegrees < 292.5) return NORTH;
//            return NORTHEAST;
//        }
//
//    }
//
//    public static Direction getDirectionTo(int ax, int az, int bx, int bz) {
//        int dx = bx - ax;
//        int dz = bz - az;
//
//        double angleRadians = Math.atan2(dz, dx);
//        double angleDegrees = Math.toDegrees(angleRadians);
//        angleDegrees = (angleDegrees + 360) % 360;
//
//        return Direction.fromAngle(angleDegrees);
//    }
//
//
//    public static Map<Direction, TownDistancePair> findClosestMarketTownsByDirection(Town sourceCity, List<Town> allCities) {
//
//
//        Map<Direction, TownDistancePair> closestTowns = new EnumMap<>(Direction.class);
//        double MAX_DISTANCE = 1000.0;
//
//        int ax = sourceCity.getChunkX();
//        int az = sourceCity.getChunkZ();
//
//        // Инициализируем все направления
//        for (Direction dir : Direction.values()) {
//            closestTowns.put(dir, new TownDistancePair(null, Double.POSITIVE_INFINITY));
//        }
//
//        for (Town target : allCities) {
//            if (target == sourceCity || (target.getChunkX() == ax && target.getChunkZ() == az)) {
//                continue;
//            }
//            //if(target.getLandHubId() == null) continue;
//
//
//            int bx = target.getChunkX();
//            int bz = target.getChunkZ();
//
//            Direction direction = getDirectionTo(ax, az, bx, bz);
//            double distance = Math.sqrt(Math.pow(ax - bx, 2) + Math.pow(az - bz, 2));
//            if (distance > MAX_DISTANCE) continue;
//            TownDistancePair current = closestTowns.get(direction);
//            if (distance < current.distance) {
//                closestTowns.put(direction, new TownDistancePair(target, distance));
//            }
//        }
//
//        return closestTowns;
//    }
//
//    // Вспомогательный класс для хранения города и расстояния
//    public static class TownDistancePair {
//        public final Town town;
//        public final double distance;
//
//        public TownDistancePair(Town town, double distance) {
//            this.town = town;
//            this.distance = distance;
//        }
//
//        @Override
//        public String toString() {
//            return town != null ?
//                    town.getName() + " (" + String.format("%.1f", distance) + " чанков)" :
//                    "Нет города";
//        }
//    }
//    public static boolean isLandPathBetween(int startChunkX, int startChunkZ, int endChunkX, int endChunkZ, int maxDistance, Set<ChunkPosition> chunks, String world) {
//
//        class PathNode {
//            int x, z, distance;
//            PathNode(int x, int z, int distance) {
//                this.x = x;
//                this.z = z;
//                this.distance = distance;
//            }
//        }
//        Queue<PathNode> queue = new LinkedList<>();
//        Set<String> visited = new HashSet<>();
//        queue.add(new PathNode(startChunkX, startChunkZ, 0));
//        visited.add(startChunkX + "," + startChunkZ);
//
//        int[][] directions = {
//                {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // Вперед/назад/влево/вправо
//                {1, 1}, {-1, -1}, {1, -1}, {-1, 1} // Диагонали
//        };
//
//        while (!queue.isEmpty()) {
//            PathNode current = queue.poll();
//            if (current.x == endChunkX && current.z == endChunkZ) return true;
//            if (current.distance >= maxDistance) continue;
//
//
//            for (int[] dir : directions) {
//                int nx = current.x + dir[0];
//                int nz = current.z + dir[1];
//                String key = nx + "," + nz;
//
//                if (!visited.contains(key)) {
//                    visited.add(key);
//                    if (chunks != null && isChunkClaimedByOthers(nx, nz,Bukkit.getWorld(world), chunks)) continue;
//
//                    Chunk chunk = Bukkit.getWorld(world).getChunkAt(nx, nz);
//                    if (!isWaterChunk(chunk)) {
//                        queue.add(new PathNode(nx, nz, current.distance + 1));
//                    }
//                }
//            }
//        }
//
//        return false; // Не найден путь по суше
//    }
//
//
//}
