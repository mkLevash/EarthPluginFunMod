package earthrp.tools.maps;

import java.io.*;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class RegionMap implements Serializable {
    private static final long serialVersionUID = 1L;

    // Ключ - координаты региона, Значение - битовая маска 512x512 блоков
    private final Map<Long, BitSet> regions = new HashMap<>();

    /**
     * Помечает блок как море при первичном сканировании карты.
     */
    public void setSea(int x, int z) {
        long regionKey = getRegionKey(x, z);
        BitSet regionData = regions.computeIfAbsent(regionKey, k -> new BitSet(512 * 512));

        int localIndex = getLocalIndex(x, z);
        regionData.set(localIndex, true);
    }

    /**
     * Проверяет, является ли блок морем.
     * Именно этот метод будет использоваться при постройке города.
     */
    public boolean isSea(int x, int z) {
        long regionKey = getRegionKey(x, z);
        BitSet regionData = regions.get(regionKey);

        // Если данных о регионе нет, считаем, что это суша (или море, в зависимости от вашей логики)
        if (regionData == null) return false;

        return regionData.get(getLocalIndex(x, z));
    }

    // --- Служебные методы для математики координат ---

    private long getRegionKey(int x, int z) {
        int regionX = Math.floorDiv(x, 512);
        int regionZ = Math.floorDiv(z, 512);
        return ((long) regionX << 32) | (regionZ & 0xFFFFFFFFL);
    }

    private int getLocalIndex(int x, int z) {
        int localX = x & 511; // Быстрый остаток от деления на 512 (для положительных и отрицательных)
        int localZ = z & 511;
        return localZ * 512 + localX;
    }

    // --- Сохранение и загрузка ---

    public void saveToFile(File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(this);
        }
    }

    public static RegionMap loadFromFile(File file) throws IOException, ClassNotFoundException {
        if (!file.exists()) return new RegionMap();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (RegionMap) ois.readObject();
        }
    }
}
