package earthrp.tools.maps;

import earthrp.Earth;

import java.util.*;

public class CityBoundaryCalculator {

    private final RegionMap mapData;
    private static final int PLOT_SIZE = 16;

    public CityBoundaryCalculator(RegionMap mapData) {
        this.mapData = mapData;
    }

    /**
     * Принимает сетку чанков города (координаты чанков: 0, 1, 2...).
     * Возвращает упорядоченный список мировых координат конкретных блоков границы.
     */
    public static List<chunkPoint> getBoundary(Set<chunkPoint> cityArea) {
        Set<chunkPoint> boundaryBlocks = new HashSet<>();

        for (chunkPoint p : cityArea) {
            // Переводим координату чанка в мировую координату начала этого чанка (в блоках)
            int blockX = p.x * PLOT_SIZE;
            int blockZ = p.z * PLOT_SIZE;

            // Проверяем 4 соседних ЧАНКА (смещение на 1)

            // Нет соседа на Востоке (X + 1) -> добавляем самую восточную линию блоков чанка
            if (!cityArea.contains(new chunkPoint(p.x + 1, p.z))) {
                for (int i = 0; i < PLOT_SIZE; i++) {
                    boundaryBlocks.add(new chunkPoint(blockX + PLOT_SIZE - 1, blockZ + i));
                }
            }

            // Нет соседа на Западе (X - 1) -> добавляем самую западную линию блоков чанка
            if (!cityArea.contains(new chunkPoint(p.x - 1, p.z))) {
                for (int i = 0; i < PLOT_SIZE; i++) {
                    boundaryBlocks.add(new chunkPoint(blockX, blockZ + i));
                }
            }

            // Нет соседа на Юге (Z + 1) -> добавляем самую южную линию блоков чанка
            if (!cityArea.contains(new chunkPoint(p.x, p.z + 1))) {
                for (int i = 0; i < PLOT_SIZE; i++) {
                    boundaryBlocks.add(new chunkPoint(blockX + i, blockZ + PLOT_SIZE - 1));
                }
            }

            // Нет соседа на Севере (Z - 1) -> добавляем самую северную линию блоков чанка
            if (!cityArea.contains(new chunkPoint(p.x, p.z - 1))) {
                for (int i = 0; i < PLOT_SIZE; i++) {
                    boundaryBlocks.add(new chunkPoint(blockX + i, blockZ));
                }
            }
        }

        // Сортируем получившиеся блоки по периметру (друг за другом)
        return sortBoundaryPoints(boundaryBlocks);
    }

    /**
     * Вычисляет площадь города в координатах чанков.
     */
    public Set<chunkPoint> calculateCityArea(int startChunkX, int startChunkZ, int maxPlots) {
        Queue<chunkPoint> queue = new LinkedList<>();
        Set<chunkPoint> cityArea = new HashSet<>();

        chunkPoint startPoint = new chunkPoint(startChunkX, startChunkZ);
        queue.add(startPoint);
        cityArea.add(startPoint);

        while (!queue.isEmpty() && cityArea.size() < maxPlots) {
            chunkPoint current = queue.poll();

            // Соседние чанки (смещение на 1)
            chunkPoint[] neighbors = {
                    new chunkPoint(current.x + 1, current.z),
                    new chunkPoint(current.x - 1, current.z),
                    new chunkPoint(current.x, current.z + 1),
                    new chunkPoint(current.x, current.z - 1)
            };

            for (chunkPoint neighbor : neighbors) {
                if (!cityArea.contains(neighbor) && isPlotValid(neighbor.x, neighbor.z)) {
                    cityArea.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        return cityArea;
    }

    /**
     * Проверяет чанк на пригодность (принимает координаты чанка).
     */
    private boolean isPlotValid(int chunkX, int chunkZ) {
        if(Earth.getInstance().getDatabase().getTownAtChunk(chunkX,chunkZ)!=null) return false;
        // Переводим координаты чанка в начальные координаты блоков для работы с RegionMap
        int startX = chunkX * PLOT_SIZE;
        int startZ = chunkZ * PLOT_SIZE;

        int totalBlocks = PLOT_SIZE * PLOT_SIZE; // Общая площадь участка
        int landBlocksCount = 0;                 // Счетчик блоков суши

        for (int x = startX; x < startX + PLOT_SIZE; x += 1) {
            for (int z = startZ; z < startZ + PLOT_SIZE; z += 1) {
                if (!mapData.isSea(x, z)) {
                    // Если это НЕ море, значит это суша
                    landBlocksCount++;
                }
            }
        }

// 30% от общей площади это: totalBlocks * 0.3
// Если суши меньше 30%, то участок нам не подходит
        return (landBlocksCount > totalBlocks * 0.3);
    }

    public record chunkPoint(int x, int z) {}

    public static List<chunkPoint> sortBoundaryPoints(Set<chunkPoint> boundarySet) {
        List<chunkPoint> sortedList = new ArrayList<>();
        if (boundarySet.isEmpty()) return sortedList;

        chunkPoint current = boundarySet.iterator().next();
        sortedList.add(current);
        boundarySet.remove(current);

        while (!boundarySet.isEmpty()) {
            chunkPoint nextPoint = null;
            double minDistance = Double.MAX_VALUE;

            for (chunkPoint p : boundarySet) {
                // Вычисляем расстояние между блоками (оно будет равно 1 или sqrt(2) для соседних)
                double dist = Math.sqrt(Math.pow(p.x - current.x, 2) + Math.pow(p.z - current.z, 2));
                if (dist < minDistance) {
                    minDistance = dist;
                    nextPoint = p;
                }
            }

            sortedList.add(nextPoint);
            boundarySet.remove(nextPoint);
            current = nextPoint;
        }

        return sortedList;
    }
}