package earthrp.customObjects;

import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EarthItem;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BuildingData {

    @Getter
    @Setter
    private BuildingType type;

    public int pastureArea;

    @Getter
    @Setter
    private EarthItem item;

    @Getter
    @Setter
    private Material defaultItem;



    @Getter
    @Setter
    private boolean pastureMobSpawn = true;

    @Getter
    @Setter
    private boolean status = true;

    @Getter
    @Setter
    private UUID armyId = null;

    @Getter
    @Setter
    private double farmEfficiency;

    @Getter
    @Setter
    private double lumberEfficiency;

    @Getter
    @Setter
    private double mineEfficiency;


    private final Map<Long, Integer> chunkFarmlandCache = new HashMap<>();

    public void updateChunkCache(Chunk chunk, int count){
        chunkFarmlandCache.put(chunk.getChunkKey(), count);
    }



    public int countFarmland(Location center) {
        World world = center.getWorld();
        if (world == null) return 0;

        int bY = center.getBlockY() - 1;

        // Определяем крайние точки нашей фермы (радиус 13)
        int minX = center.getBlockX() - 4;
        int maxX = center.getBlockX() + 4;
        int minZ = center.getBlockZ() - 4;
        int maxZ = center.getBlockZ() + 4;

        // Переводим координаты блоков в координаты чанков (сдвиг вправо на 4 эквивалентен делению на 16)
        int minChunkX = minX >> 4;
        int maxChunkX = maxX >> 4;
        int minChunkZ = minZ >> 4;
        int maxChunkZ = maxZ >> 4;

        int totalFarmlandCount = 0;

        // Проходимся только по тем чанкам, которые задевает ферма
        for (int cx = minChunkX; cx <= maxChunkX; cx++) {
            for (int cz = minChunkZ; cz <= maxChunkZ; cz++) {

                // Генерируем уникальный ключ чанка для кэша
                long chunkKey = getChunkKey(cx, cz);

                if (world.isChunkLoaded(cx, cz)) {
                    // ЧАНК ЗАГРУЖЕН: считаем блоки в реальном времени и обновляем кэш

                    int chunkFarmlandCount = 0;

                    // Вычисляем границы пересечения нашей фермы и текущего чанка.
                    // Нам не нужно проверять весь чанк (16x16), а только ту его часть, где есть ферма.
                    int startX = Math.max(minX, cx << 4);
                    int endX = Math.min(maxX, (cx << 4) + 15);
                    int startZ = Math.max(minZ, cz << 4);
                    int endZ = Math.min(maxZ, (cz << 4) + 15);

                    for (int x = startX; x <= endX; x++) {
                        for (int z = startZ; z <= endZ; z++) {
                            if (world.getBlockAt(x, bY, z).getType() == Material.FARMLAND) {
                                chunkFarmlandCount++;
                            }
                        }
                    }

                    // Обновляем кэш свежими данными
                    chunkFarmlandCache.put(chunkKey, chunkFarmlandCount);
                    totalFarmlandCount += chunkFarmlandCount;

                } else {
                    // ЧАНК НЕ ЗАГРУЖЕН: достаем последнее известное значение из кэша
                    // Если чанк еще ни разу не проверялся (вернул null), прибавляем 0
                    totalFarmlandCount += chunkFarmlandCache.getOrDefault(chunkKey, 0);
                }
            }
        }

        return totalFarmlandCount;
    }

    /**
     * Быстрый метод для создания уникального Long-ключа из X и Z координат чанка.
     * Используется внутри ядра Minecraft.
     */
    private long getChunkKey(int x, int z) {
        return (long) x & 0xffffffffL | ((long) z & 0xffffffffL) << 32;
    }



}
