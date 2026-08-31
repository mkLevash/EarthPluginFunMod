package earthrp.tools.maps;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


import de.pauleff.jmcx.api.IChunk;
import de.pauleff.jnbt.api.ICompoundTag;
import de.pauleff.jnbt.api.IListTag;
import earthrp.Earth;
import org.bukkit.Bukkit;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;

public class WorldScanner {

    private final File regionFolder;

    public WorldScanner(File worldFolder) {
        this.regionFolder = new File(worldFolder, "region");
    }

    public CompletableFuture<RegionMap> scanWorldAsync() {
        return getAllGeneratedChunks().thenCompose(list -> {
            RegionMap regionMap = new RegionMap();
            String worldName = this.regionFolder.getParentFile().getName();
            World world = Bukkit.getWorlds().getFirst();

            // Ждем завершения всей цепочки рекурсивных вызовов
            return processChunksInBatches(world, list, regionMap, 0)
                    .thenApply(v -> {
                        Earth.getInstance().getLogger().info("Сканирование завершено, обработано: " + list.size());
                        return regionMap;
                    });
        });
    }

    public CompletableFuture<RegionMap> scanWorldAsyncInRadius(int startChunkX, int startChunkZ, int radius) {
        // Сначала асинхронно формируем список нужных чанков, чтобы не стопить основной поток
        return CompletableFuture.supplyAsync(() -> {
            List<ChunkCord> chunksToScan = new ArrayList<>();

            for (int x = startChunkX - radius; x <= startChunkX + radius; x++) {
                for (int z = startChunkZ - radius; z <= startChunkZ + radius; z++) {
                    // Математика для кругового радиуса
                    if ((x - startChunkX) * (x - startChunkX) + (z - startChunkZ) * (z - startChunkZ) <= radius * radius) {
                        chunksToScan.add(new ChunkCord(x, z));
                    }
                }
            }
            return chunksToScan;

        }).thenCompose(list -> {
            RegionMap regionMap = new RegionMap();
            String worldName = this.regionFolder.getParentFile().getName();
            World world = Bukkit.getWorlds().getFirst();

            // Защита на случай, если мир еще не прогружен сервером
            if (world == null) {
                Earth.getInstance().getLogger().severe("Ошибка сканирования: мир '" + worldName + "' не найден!");
                return CompletableFuture.completedFuture(regionMap);
            }

            // Запускаем рекурсивную обработку пачками (batches)
            return processChunksInBatches(world, list, regionMap, 0)
                    .thenApply(v -> {
                        Earth.getInstance().getLogger().info(
                                "Сканирование в радиусе " + radius + " завершено. Обработано чанков: " + list.size()
                        );
                        return regionMap;
                    });
        });
    }

    public CompletableFuture<List<ChunkCord>> getAllGeneratedChunks() {
        return CompletableFuture.supplyAsync(() -> {
            List<ChunkCord> cords = new ArrayList<>();
            File[] files = regionFolder.listFiles((dir, name) -> name.endsWith(".mca"));

            if (files != null) {
                for (File file : files) {
                    try {
                        String[] parts = file.getName().split("\\.");
                        int regionX = Integer.parseInt(parts[1]);
                        int regionZ = Integer.parseInt(parts[2]);

                        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
                            if (raf.length() < 4096) continue;

                            byte[] header = new byte[4096];
                            raf.read(header);

                            for (int i = 0; i < 1024; i++) {
                                int offset = i * 4;
                                // Проверка на непустой чанк
                                if (header[offset] != 0 || header[offset+1] != 0 ||
                                        header[offset+2] != 0 || header[offset+3] != 0) {

                                    int chunkX = (regionX << 5) + (i % 32);
                                    int chunkZ = (regionZ << 5) + (i / 32);
                                    cords.add(new ChunkCord(chunkX, chunkZ));
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Используйте ваш логгер здесь
                        Earth.getInstance().getLogger().warning("Ошибка при анализе региона " + file.getName());
                    }
                }
            }
            return cords;
        });
    }

    // Простой класс для хранения координат
    private record ChunkCord(int x, int z) {}



    private void scanChunkSnapshot(ChunkSnapshot snapshot, RegionMap regionMap){
        int seaLevelY = 61;

        // 3. Перебираем координаты внутри слепка
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {

                // Получаем тип блока (никаких NBT, палитр и сдвигов битов!)
                Material blockType = snapshot.getBlockType(x, seaLevelY, z);

                Biome biome = snapshot.getBiome(x, seaLevelY, z);
                // Переводим локальные координаты в мировые для вывода
                int worldX = (snapshot.getX() << 4) + x;
                int worldZ = (snapshot.getZ() << 4) + z;


                if (biome.translationKey().contains("ocean") || biome.translationKey().contains("peaks")) {
                    regionMap.setSea(worldX,worldZ);
                }
            }
        }
    }

    private CompletableFuture<Void> processChunksInBatches(World world, List<ChunkCord> allCoords, RegionMap map, int index) {
        if (index >= allCoords.size()) {
            return CompletableFuture.completedFuture(null);
        }

        int batchSize = 250;
        int end = Math.min(index + batchSize, allCoords.size());
        List<ChunkCord> batch = allCoords.subList(index, end);

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (ChunkCord cord : batch) {
            futures.add(world.getChunkAtAsync(cord.x, cord.z).thenAccept(chunk -> {
                scanChunkSnapshot(chunk.getChunkSnapshot(false,false,true), map);
            }));
        }

        // Возвращаем композицию: когда текущая пачка готова, запускаем следующую
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenCompose(v -> {
                    // Маленькая задержка для стабильности сервера
                    CompletableFuture<Void> delay = new CompletableFuture<>();
                    Bukkit.getScheduler().runTaskLater(Earth.getInstance(), () -> {
                        processChunksInBatches(world, allCoords, map, end).thenAccept(v2 -> delay.complete(null));
                    }, 1L);
                    return delay;
                });
    }


    public void scanChunk(IChunk chunk, RegionMap regionMap) throws IOException {
        int seaLevelY = 41;
        int sectionY = seaLevelY >> 4; // Равно 3
        int localY = seaLevelY & 15;   // Равно 14 (локальная высота внутри секции)

        // 1. Получаем сырые NBT данные чанка
        ICompoundTag nbt = chunk.getNBTData();
        if(nbt == null) return;
        if (!nbt.hasTag("sections")) return;
        IListTag sections = nbt.getList("sections");

        // 2. Ищем нужную секцию
        ICompoundTag targetSection = null;
        for (int i = 0; i < sections.size(); i++) {
            ICompoundTag sec = (ICompoundTag) sections.get(i);
            if (sec.getByte("Y") == sectionY) {
                targetSection = sec;
                break;
            }
        }

        if (targetSection == null) return; // Секция пуста (только воздух)

        ICompoundTag blockStates = targetSection.getCompound("block_states");
        ICompoundTag biomes = targetSection.getCompound("biomes");

        // 3. Супер-оптимизация: проверяем палитру до сложных вычислений.
        // Если в палитре секции вообще нет воды, пропускаем чанк моментально!
        if (!hasWaterInPalette(blockStates)) return;

        // 4. Перебираем все x и z
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                String blockName = getBlockAt(blockStates, x, localY, z);

                if (blockName.contains("water")) {
                    String biomeName = getBiomeAt(biomes, x, localY, z);

                    if (biomeName.contains("ocean")) {
                        regionMap.setSea(x,z);
                    }
                }
            }
        }
    }

    private boolean hasWaterInPalette(ICompoundTag blockStates) {
        if (blockStates == null || !blockStates.hasTag("palette")) return false;
        IListTag palette = blockStates.getList("palette");
        for (int i = 0; i < palette.size(); i++) {
            ICompoundTag block = (ICompoundTag) palette.get(i);
            if (block.getString("Name").contains("water")) return true;
        }
        return false;
    }

    private String getBlockAt(ICompoundTag blockStates, int x, int y, int z) {
        if (blockStates == null || !blockStates.hasTag("palette")) return "minecraft:air";

        IListTag palette = blockStates.getList("palette");

        // Если в секции всего 1 тип блока, Minecraft удаляет массив data
        if (palette.size() == 1) {
            return ((ICompoundTag) palette.get(0)).getString("Name");
        }

        long[] data = blockStates.getLongArray("data");
        if (data == null || data.length == 0) return "minecraft:air";

        // Математика распаковки (Bit-Packing)
        int blockIndex = (y * 16 + z) * 16 + x;
        int bitsPerBlock = Math.max(4, (int) Math.ceil(Math.log(palette.size()) / Math.log(2)));
        int blocksPerLong = 64 / bitsPerBlock;

        int longIndex = blockIndex / blocksPerLong;
        int bitOffset = (blockIndex % blocksPerLong) * bitsPerBlock;

        long mask = (1L << bitsPerBlock) - 1;
        int paletteId = (int) ((data[longIndex] >>> bitOffset) & mask);

        return ((ICompoundTag) palette.get(paletteId)).getString("Name");
    }

    private String getBiomeAt(ICompoundTag biomes, int x, int y, int z) {
        if (biomes == null || !biomes.hasTag("palette")) return "minecraft:plains";

        IListTag palette = biomes.getList("palette");
        // В палитре биомов лежат просто строки, а не CompoundTag
        if (palette.size() == 1) return palette.get(0).toString().replace("\"", "");

        long[] data = biomes.getLongArray("data");
        if (data == null || data.length == 0) return palette.get(0).toString().replace("\"", "");

        // Биомы хранятся в сетке 4x4x4 (сжимаем координаты)
        int bX = x / 4;
        int bY = y / 4;
        int bZ = z / 4;
        int biomeIndex = (bY * 4 + bZ) * 4 + bX;

        int bitsPerBiome = (int) Math.ceil(Math.log(palette.size()) / Math.log(2));
        if (bitsPerBiome == 0) bitsPerBiome = 1;
        int biomesPerLong = 64 / bitsPerBiome;

        int longIndex = biomeIndex / biomesPerLong;
        int bitOffset = (biomeIndex % biomesPerLong) * bitsPerBiome;

        long mask = (1L << bitsPerBiome) - 1;
        int paletteId = (int) ((data[longIndex] >>> bitOffset) & mask);

        return palette.get(paletteId).toString().replace("\"", "");
    }

    public void scanChunkSafely(World world, int chunkX, int chunkZ, RegionMap regionMap) {



        // 1. Асинхронно просим сервер загрузить чанк (не тормозит основной поток игры)
        world.getChunkAtAsync(chunkX, chunkZ).thenAccept(chunk -> {

            // 2. Делаем "слепок" чанка. Это мгновенная копия данных в оперативной памяти.
            // Она абсолютно безопасна для чтения и никогда не выдаст "Sector count mismatch".
            ChunkSnapshot snapshot = chunk.getChunkSnapshot();

            int seaLevelY = 41;

            // 3. Перебираем координаты внутри слепка
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {

                    // Получаем тип блока (никаких NBT, палитр и сдвигов битов!)
                    Material blockType = snapshot.getBlockType(x, seaLevelY, z);

                    if (blockType == Material.WATER) {
                        // Проверяем биом
                        Biome biome = snapshot.getBiome(x, seaLevelY, z);

                        if (biome.translationKey().contains("ocean")) {
                            // Переводим локальные координаты в мировые для вывода
                            int worldX = (chunkX << 4) + x;
                            int worldZ = (chunkZ << 4) + z;

                            regionMap.setSea(worldX,worldZ);
                        }
                    }
                }
            }
        });
    }
}