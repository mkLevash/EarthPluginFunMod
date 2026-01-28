//package earthrp;
//
//import earthrp.Earth.ChunkPosition;
//import earthrp.database.ServerDatabase;
//import org.bukkit.Bukkit;
//import org.bukkit.World;
//import org.dynmap.DynmapAPI;
//import org.dynmap.markers.AreaMarker;
//import org.dynmap.markers.Marker;
//import org.dynmap.markers.MarkerAPI;
//import org.dynmap.markers.MarkerSet;
//
//import java.sql.SQLException;
//import java.util.*;
//import java.awt.geom.Point2D;
//import java.util.*;
//import java.util.stream.Collectors;
//
//public class PolygonUtils {
//
//    public static List<int[]> traceOuterBorder(Set<ChunkPosition> chunks) {
//        // Переводим все чанки в булевую сетку
//        if (chunks.isEmpty()) return Collections.emptyList();
//
//        int minX = chunks.stream().mapToInt(ChunkPosition::x).min().getAsInt();
//        int maxX = chunks.stream().mapToInt(ChunkPosition::x).max().getAsInt();
//        int minZ = chunks.stream().mapToInt(ChunkPosition::z).min().getAsInt();
//        int maxZ = chunks.stream().mapToInt(ChunkPosition::z).max().getAsInt();
//
//        int width = maxX - minX + 3; // +2 для рамки, +1 потому что включительно
//        int height = maxZ - minZ + 3;
//
//        boolean[][] grid = new boolean[width][height];
//
//        // Заполняем сетку чанков, со смещением на 1 (рамка)
//        for (ChunkPosition pos : chunks) {
//            int x = pos.x() - minX + 1;
//            int z = pos.z() - minZ + 1;
//            grid[x][z] = true;
//        }
//
//        // Directions: вверх, вправо, вниз, влево
//        int[][] DIR = {
//                {0, -1}, {1, 0}, {0, 1}, {-1, 0}
//        };
//
//        List<int[]> border = new ArrayList<>();
//
//        // Находим стартовую точку (первый наружный угол)
//        int sx = -1, sz = -1;
//        outer:
//        for (int x = 0; x < width; x++) {
//            for (int z = 0; z < height; z++) {
//                if (grid[x][z]) {
//                    sx = x;
//                    sz = z;
//                    break outer;
//                }
//            }
//        }
//
//        int x = sx;
//        int z = sz;
//        int dir = 0; // Начинаем с направления вверх
//        boolean first = true;
//
//        int iterCount = 0;
//        int maxIter = 10000;
//
//        do {
//            border.add(new int[]{x + minX - 1, z + minZ - 1});
//
//            // Поворачиваем налево
//            for (int i = 0; i < 4; i++) {
//                int newDir = (dir + 3 + i) % 4;
//                int nx = x + DIR[newDir][0];
//                int nz = z + DIR[newDir][1];
//
//                if (grid[nx][nz]) {
//                    x = nx;
//                    z = nz;
//                    dir = newDir;
//                    break;
//                }
//            }
//
//            if (!first && x == sx && z == sz && dir == 0) break;
//            first = false;
//
//            iterCount++;
//            if (iterCount > maxIter) {
//                System.out.println("[Earth]Прерывание: превышено число итераций");
//                break;
//            }
//        } while (true);
//
//        return border;
//    }
//
//
//    private static List<Set<ChunkPosition>> groupConnectedChunks(Set<ChunkPosition> allChunks) {
//        List<Set<ChunkPosition>> groups = new ArrayList<>();
//        Set<ChunkPosition> visited = new HashSet<>();
//
//        for (ChunkPosition start : allChunks) {
//            if (visited.contains(start)) continue;
//
//            Set<ChunkPosition> group = new HashSet<>();
//            Queue<ChunkPosition> queue = new ArrayDeque<>();
//            queue.add(start);
//
//            while (!queue.isEmpty()) {
//                ChunkPosition current = queue.poll();
//                if (!visited.add(current)) continue;
//                group.add(current);
//
//                for (int dx = -1; dx <= 1; dx++) {
//                    for (int dz = -1; dz <= 1; dz++) {
//                        if (Math.abs(dx) + Math.abs(dz) != 1) continue; // 4 направления
//
//                        ChunkPosition neighbor = new ChunkPosition(current.x() + dx, current.z() + dz, current.world());
//                        if (allChunks.contains(neighbor) && !visited.contains(neighbor)) {
//                            queue.add(neighbor);
//                        }
//                    }
//                }
//            }
//
//            groups.add(group);
//        }
//
//        return groups;
//    }
//
//
//
//
//    public static List<ChunkPosition> findOrderedBorderChunks(Set<ChunkPosition> allChunks) {
//        Set<ChunkPosition> borderChunks = findBorderChunks(allChunks);
//        return sortBorderChunksClockwise(borderChunks);
//    }
//
//    /**
//     * Находит граничные чанки (без порядка).
//     */
//    private static Set<ChunkPosition> findBorderChunks(Set<ChunkPosition> allChunks) {
//        Set<ChunkPosition> borderChunks = new HashSet<>();
//        for (ChunkPosition chunk : allChunks) {
//            int x = chunk.x();
//            int z = chunk.z();
//            String world = chunk.world();
//            boolean isBorder = !allChunks.contains(new ChunkPosition(x - 1, z, world)) ||
//                    !allChunks.contains(new ChunkPosition(x + 1, z, world)) ||
//                    !allChunks.contains(new ChunkPosition(x, z - 1, world)) ||
//                    !allChunks.contains(new ChunkPosition(x, z + 1, world));
//            if (isBorder) {
//                borderChunks.add(chunk);
//            }
//        }
//        return borderChunks;
//    }
//
//    /**
//     * Сортирует граничные чанки по часовой стрелке.
//     */
//    private static List<ChunkPosition> sortBorderChunksClockwise(Set<ChunkPosition> borderChunks) {
//        if (borderChunks.isEmpty()) {
//            return new ArrayList<>();
//        }
//
//        // Находим "центр" области (средние координаты)
//        double centerX = borderChunks.stream().mapToInt(ChunkPosition::x).average().orElse(0);
//        double centerZ = borderChunks.stream().mapToInt(ChunkPosition::z).average().orElse(0);
//
//        // Сортируем по углу относительно центра
//        return borderChunks.stream()
//                .sorted((a, b) -> {
//                    double angleA = Math.atan2(a.z() - centerZ, a.x() - centerX);
//                    double angleB = Math.atan2(b.z() - centerZ, b.x() - centerX);
//                    return Double.compare(angleA, angleB);
//                })
//                .collect(Collectors.toList());
//    }
//
//
//    public static record Edge(int x1, int z1, int x2, int z2) {}
//
//    // Получаем внешние рёбра чанков
//    public static List<Edge> getOuterEdges(Set<ChunkPosition> chunks) {
//        Set<String> chunkSet = new HashSet<>();
//        for (ChunkPosition p : chunks) {
//            chunkSet.add(p.x() + "," + p.z());
//        }
//
//        Map<String, Edge> edges = new HashMap<>();
//
//        for (ChunkPosition chunk : chunks) {
//            int x = chunk.x();
//            int z = chunk.z();
//
//            // Северная сторона: ребро между (x,z) и (x+1,z)
//            if (!chunkSet.contains(x + "," + (z - 1))) {
//                Edge e = new Edge(x, z, x + 1, z);
//                edges.put(edgeKey(e), e);
//            }
//            // Восточная сторона: ребро между (x+1,z) и (x+1,z+1)
//            if (!chunkSet.contains((x + 1) + "," + z)) {
//                Edge e = new Edge(x + 1, z, x + 1, z + 1);
//                edges.put(edgeKey(e), e);
//            }
//            // Южная сторона: ребро между (x,z+1) и (x+1,z+1)
//            if (!chunkSet.contains(x + "," + (z + 1))) {
//                Edge e = new Edge(x, z + 1, x + 1, z + 1);
//                edges.put(edgeKey(e), e);
//            }
//            // Западная сторона: ребро между (x,z) и (x,z+1)
//            if (!chunkSet.contains((x - 1) + "," + z)) {
//                Edge e = new Edge(x, z, x, z + 1);
//                edges.put(edgeKey(e), e);
//            }
//        }
//
//        return new ArrayList<>(edges.values());
//    }
//
//    // Формируем уникальный ключ ребра, чтобы избежать дубликатов в обратном порядке
//    private static String edgeKey(Edge e) {
//        if (e.x1 < e.x2 || (e.x1 == e.x2 && e.z1 < e.z2)) {
//            return e.x1 + "," + e.z1 + "-" + e.x2 + "," + e.z2;
//        } else {
//            return e.x2 + "," + e.z2 + "-" + e.x1 + "," + e.z1;
//        }
//    }
//
//    // Строим связанный контур из рёбер
//    public static List<double[]> buildPolygonFromEdges(List<Edge> edges) {
//        if (edges.isEmpty()) return Collections.emptyList();
//
//        // Создаем карту: ключ — начальная точка ребра, значение — список ребер с такой точкой
//        Map<String, List<Edge>> edgesFromPoint = new HashMap<>();
//        for (Edge e : edges) {
//            String key = pointKey(e.x1, e.z1);
//            edgesFromPoint.computeIfAbsent(key, k -> new ArrayList<>()).add(e);
//        }
//
//        List<double[]> polygon = new ArrayList<>();
//
//        // Начинаем с первого ребра
//        Edge currentEdge = edges.get(0);
//        polygon.add(new double[]{currentEdge.x1 * 16.0, currentEdge.z1 * 16.0});
//        polygon.add(new double[]{currentEdge.x2 * 16.0, currentEdge.z2 * 16.0});
//
//        String currentPoint = pointKey(currentEdge.x2, currentEdge.z2);
//
//        Set<String> visitedEdges = new HashSet<>();
//        visitedEdges.add(edgeKey(currentEdge));
//
//        while (true) {
//            List<Edge> nextEdges = edgesFromPoint.getOrDefault(currentPoint, Collections.emptyList());
//            Edge nextEdge = null;
//
//            for (Edge e : nextEdges) {
//                if (!visitedEdges.contains(edgeKey(e))) {
//                    nextEdge = e;
//                    break;
//                }
//            }
//
//            if (nextEdge == null) {
//                // Конец маршрута — замкнули контур
//                break;
//            }
//
//            visitedEdges.add(edgeKey(nextEdge));
//
//            // Добавляем новую точку (вторую точку ребра)
//            polygon.add(new double[]{nextEdge.x2 * 16.0, nextEdge.z2 * 16.0});
//            currentPoint = pointKey(nextEdge.x2, nextEdge.z2);
//        }
//
//        return polygon;
//    }
//
//    private static String pointKey(int x, int z) {
//        return x + "," + z;
//    }
//
//
//    public static List<int[]> traceBorder(Set<ChunkPosition> chunks) {
//        if (chunks.isEmpty()) return List.of();
//
//        Set<String> chunkSet = new HashSet<>();
//        for (ChunkPosition p : chunks) {
//            chunkSet.add(p.x() + "," + p.z());
//        }
//
//        int[][] DIRECTIONS = {
//                {1, 0},   // Восток
//                {0, 1},   // Юг
//                {-1, 0},  // Запад
//                {0, -1}   // Север
//        };
//
//        ChunkPosition start = chunks.stream()
//                .min(Comparator.comparingInt(ChunkPosition::z)
//                        .thenComparingInt(ChunkPosition::x))
//                .orElseThrow();
//
//        List<int[]> border = new ArrayList<>();
//        int x = start.x();
//        int z = start.z();
//        int dir = 0;
//        int startX = x;
//        int startZ = z;
//        int startDir = dir;
//
//        boolean completed = false;
//        boolean firstStep = true;
//        int iterations = 0;
//        int maxIterations = 10000;
//
//        while (!completed && iterations++ < maxIterations) {
//            border.add(new int[]{x, z});
//            boolean moved = false;
//
//            for (int i = 0; i < 4; i++) {
//                int newDir = (dir + 3 + i) % 4;
//                int nx = x + DIRECTIONS[newDir][0];
//                int nz = z + DIRECTIONS[newDir][1];
//
//                int cx = x + (DIRECTIONS[newDir][0] + DIRECTIONS[dir][0]) / 2;
//                int cz = z + (DIRECTIONS[newDir][1] + DIRECTIONS[dir][1]) / 2;
//
//                if (!chunkSet.contains(cx + "," + cz)) {
//                    x = nx;
//                    z = nz;
//                    dir = newDir;
//                    moved = true;
//                    break;
//                }
//            }
//
//            if (!moved) {
//                x += DIRECTIONS[dir][0];
//                z += DIRECTIONS[dir][1];
//            }
//
//            if (!firstStep && x == startX && z == startZ && dir == startDir) {
//                completed = true;
//            }
//            firstStep = false;
//        }
//
//        return border;
//    }
//
//
//
//    public static List<int[]> traceBorderPrecise(Set<ChunkPosition> chunks) {
//        if (chunks.isEmpty()) return List.of();
//
//        Set<ChunkPosition> chunkSet = new HashSet<>(chunks);
//
//        List<int[]> border = new ArrayList<>();
//        ChunkPosition start = chunks.stream()
//                .min(Comparator.comparingInt(ChunkPosition::z).thenComparingInt(ChunkPosition::x))
//                .orElseThrow();
//
//        int x = start.x();
//        int z = start.z();
//        int dir = 0; // направление: 0=восток, 1=юг, 2=запад, 3=север
//
//        int[][] directions = {
//                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
//        };
//
//        boolean firstStep = true;
//
//        int startX = x;
//        int startZ = z;
//        int startDir = dir;
//
//        int maxIterations = chunkSet.size() * 4;  // максимум итераций, чтобы не застрять в цикле
//        int iterations = 0;
//
//        do {
//            border.add(new int[]{x, z});
//
//            // Проверяем условие выхода **только если это не первый шаг**
//            if (!firstStep && x == startX && z == startZ && dir == startDir) {
//                break; // Завершаем обход
//            }
//
//            if (iterations++ > maxIterations) {
//                System.err.println("traceBorderPrecise: Обнаружено возможное зацикливание — превышен лимит итераций " + maxIterations);
//                break; // Прерываем цикл по лимиту
//            }
//
//            boolean turned = false;
//            for (int i = 0; i < 4; i++) {
//                int newDir = (dir + 3 + i) % 4;
//                int nx = x + directions[newDir][0];
//                int nz = z + directions[newDir][1];
//                ChunkPosition np = new ChunkPosition(nx, nz, start.world());
//
//                if (!chunkSet.contains(np)) {
//                    dir = newDir;
//                    x += directions[dir][0];
//                    z += directions[dir][1];
//                    turned = true;
//                    break;
//                }
//            }
//
//            if (!turned) {
//                // Если не смогли повернуть — движемся вперед
//                x += directions[dir][0];
//                z += directions[dir][1];
//            }
//
//            firstStep = false;
//
//        } while (true);
//
//        return border;
//    }
//
//
//
//
//    public static List<List<int[]>> traceBorderSmooth(Set<ChunkPosition> allChunks) {
//        List<Set<ChunkPosition>> groups = groupConnectedChunks(allChunks);
//        List<List<int[]>> allBorders = new ArrayList<>();
//
//        for (Set<ChunkPosition> group : groups) {
//            Set<ChunkPosition> borderChunks = findBorderChunks(group);
//            List<ChunkPosition> sortedBorder = sortBorderChunksClockwise(borderChunks);
//
//            List<int[]> coords = new ArrayList<>();
//            for (ChunkPosition chunk : sortedBorder) {
//                coords.add(new int[]{chunk.x() * 16 + 8, chunk.z() * 16 + 8});
//            }
//
//            allBorders.add(coords);
//        }
//
//        return allBorders;
//    }
//
//
//
//    private static List<Point2D.Double> chaikinSmooth(List<Point2D.Double> points) {
//        List<Point2D.Double> result = new ArrayList<>();
//        int size = points.size();
//        boolean closed = points.get(0).equals(points.get(size - 1));
//
//        for (int i = 0; i < size - 1; i++) {
//            Point2D.Double p0 = points.get(i);
//            Point2D.Double p1 = points.get(i + 1);
//
//            Point2D.Double q = new Point2D.Double(
//                    0.75 * p0.x + 0.25 * p1.x,
//                    0.75 * p0.y + 0.25 * p1.y
//            );
//
//            Point2D.Double r = new Point2D.Double(
//                    0.25 * p0.x + 0.75 * p1.x,
//                    0.25 * p0.y + 0.75 * p1.y
//            );
//
//            result.add(q);
//            result.add(r);
//        }
//
//        // Если контур замкнут — обработай последнюю пару
//        if (closed) {
//            Point2D.Double p0 = points.get(size - 1);
//            Point2D.Double p1 = points.get(0);
//
//            Point2D.Double q = new Point2D.Double(
//                    0.75 * p0.x + 0.25 * p1.x,
//                    0.75 * p0.y + 0.25 * p1.y
//            );
//
//            Point2D.Double r = new Point2D.Double(
//                    0.25 * p0.x + 0.75 * p1.x,
//                    0.25 * p0.y + 0.75 * p1.y
//            );
//
//            result.add(q);
//            result.add(r);
//        }
//
//        return result;
//    }
//
//    // Не забудь сюда добавить traceBorder(Set<ChunkPosition>), если он не в другом классе
//
//    // Метод для сглаживания координат
//    public static List<double[]> chaikinSmoothing(double[] x, double[] z, int iterations) {
//        List<double[]> points = new ArrayList<>();
//        for (int i = 0; i < x.length; i++) {
//            points.add(new double[]{x[i], z[i]});
//        }
//
//        for (int it = 0; it < iterations; it++) {
//            List<double[]> newPoints = new ArrayList<>();
//            for (int i = 0; i < points.size(); i++) {
//                double[] p0 = points.get(i);
//                double[] p1 = points.get((i + 1) % points.size()); // циклический замкнутый контур
//
//                double[] q = {
//                        0.75 * p0[0] + 0.25 * p1[0],
//                        0.75 * p0[1] + 0.25 * p1[1]
//                };
//                double[] r = {
//                        0.25 * p0[0] + 0.75 * p1[0],
//                        0.25 * p0[1] + 0.75 * p1[1]
//                };
//
//                newPoints.add(q);
//                newPoints.add(r);
//            }
//            points = newPoints;
//        }
//
//        return points;
//    }
//
//    public static List<int[]> traceBorderWithAlphaShape(Set<ChunkPosition> chunks, double alpha) {
//        if (chunks.isEmpty()) return List.of();
//
//        // 1. Собираем граничные точки
//        Set<Point2D.Double> edgePoints = new HashSet<>();
//
//        for (ChunkPosition chunk : chunks) {
//            int chunkX = chunk.x();
//            int chunkZ = chunk.z();
//            String world = chunk.world();
//
//            // Проверяем 4 направления
//            if (!chunks.contains(new ChunkPosition(chunkX + 1, chunkZ,world))) {
//                edgePoints.add(new Point2D.Double(chunkX * 16 + 16, chunkZ * 16 + 8));
//            }
//            if (!chunks.contains(new ChunkPosition(chunkX - 1, chunkZ,world))) {
//                edgePoints.add(new Point2D.Double(chunkX * 16, chunkZ * 16 + 8));
//            }
//            if (!chunks.contains(new ChunkPosition(chunkX, chunkZ + 1,world))) {
//                edgePoints.add(new Point2D.Double(chunkX * 16 + 8, chunkZ * 16 + 16));
//            }
//            if (!chunks.contains(new ChunkPosition(chunkX, chunkZ - 1,world))) {
//                edgePoints.add(new Point2D.Double(chunkX * 16 + 8, chunkZ * 16));
//            }
//        }
//
//        // 2. Вычисляем центроид
//        Point2D.Double centroid = calculateCentroid(edgePoints);
//
//        // 3. Alpha Shape алгоритм
//        List<Point2D.Double> alphaShape = computeAlphaShape(edgePoints, alpha, centroid);
//
//        // 4. Конвертируем в массив int[]
//        return alphaShape.stream()
//                .map(p -> new int[]{(int) Math.round(p.x), (int) Math.round(p.y)})
//                .collect(Collectors.toList());
//    }
//
//    private static List<Point2D.Double> computeAlphaShape(Set<Point2D.Double> points,
//                                                          double alpha,
//                                                          Point2D.Double centroid) {
//        List<Point2D.Double> boundary = new ArrayList<>();
//
//        // Для каждой точки проверяем пустоту альфа-окрестности
//        for (Point2D.Double p : points) {
//            boolean isBoundary = false;
//
//            for (Point2D.Double q : points) {
//                if (p.equals(q)) continue;
//
//                // Вычисляем диаметральную окружность
//                double dx = p.x - q.x;
//                double dy = p.y - q.y;
//                double distSq = dx*dx + dy*dy;
//
//                if (distSq < 4*alpha*alpha) {
//                    Point2D.Double mid = new Point2D.Double(
//                            (p.x + q.x)/2,
//                            (p.y + q.y)/2
//                    );
//
//                    double radius = Math.sqrt(distSq)/2;
//                    boolean isEmpty = true;
//
//                    // Проверяем, есть ли точки внутри окружности
//                    for (Point2D.Double r : points) {
//                        if (r.equals(p) || r.equals(q)) continue;
//
//                        double distToCenterSq = Math.pow(r.x - mid.x, 2) +
//                                Math.pow(r.y - mid.y, 2);
//                        if (distToCenterSq < radius*radius) {
//                            isEmpty = false;
//                            break;
//                        }
//                    }
//
//                    if (isEmpty) {
//                        isBoundary = true;
//                        break;
//                    }
//                }
//            }
//
//            if (isBoundary) {
//                boundary.add(p);
//            }
//        }
//
//        // Сортируем точки по углу относительно центроида
//        boundary.sort((a, b) -> {
//            double angleA = Math.atan2(a.y - centroid.y, a.x - centroid.x);
//            double angleB = Math.atan2(b.y - centroid.y, b.x - centroid.x);
//            return Double.compare(angleA, angleB);
//        });
//
//        return boundary;
//    }
//
//    private static Point2D.Double calculateCentroid(Collection<Point2D.Double> points) {
//        double sumX = 0, sumY = 0;
//        int count = 0;
//
//        for (Point2D.Double p : points) {
//            sumX += p.x;
//            sumY += p.y;
//            count++;
//        }
//
//        return new Point2D.Double(sumX/count, sumY/count);
//    }
//
//
//
//
//
//
//}
