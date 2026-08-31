//package earthrp.tools.maps;
//
//import net.querz.nbt.tag.CompoundTag;
//import net.querz.nbt.tag.ListTag;
//import net.querz.nbt.tag.StringTag;
//import java.lang.reflect.Field;
//
//public class BiomeParser {
//
//    private static Field dataField;
//
//    static {
//        try {
//            // Достаем доступ к приватному полю data в классе Section
//            dataField = net.querz.mca.Section.class.getDeclaredField("data");
//            dataField.setAccessible(true);
//        } catch (NoSuchFieldException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static String getBiomeName(net.querz.mca.Section section, int x, int y, int z) {
//        try {
//            // 1. Получаем CompoundTag data через рефлексию
//            CompoundTag root = (CompoundTag) dataField.get(section);
//
//            // 2. Ищем тег "biomes"
//            CompoundTag biomesTag = root.getCompoundTag("biomes");
//            if (biomesTag == null) return "minecraft:plains";
//
//            ListTag<StringTag> palette = biomesTag.getListTag("palette").asStringTagList();
//            long[] data = biomesTag.getLongArray("data");
//
//            // 3. Вычисляем индекс (стандарт сетки 4x4x4)
//            int bx = (x & 15) >> 2;
//            int by = (y & 15) >> 2;
//            int bz = (z & 15) >> 2;
//            int index = (by * 4 + bz) * 4 + bx;
//
//            // 4. Логика битового сдвига
//            int bits = Math.max(1, (int) Math.ceil(Math.log(palette.size()) / Math.log(2)));
//            int indicesPerLong = 64 / bits;
//            int longIndex = index / indicesPerLong;
//            int bitOffset = (index % indicesPerLong) * bits;
//
//            long longValue = data[longIndex];
//            int paletteIndex = (int) ((longValue >>> bitOffset) & ((1L << bits) - 1));
//
//            return palette.get(paletteIndex).getValue();
//
//        } catch (Exception e) {
//            return "minecraft:plains";
//        }
//    }
//}