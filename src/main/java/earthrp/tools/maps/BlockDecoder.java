//package earthrp.tools.maps;
//
//import net.querz.mca.Section;
//import net.querz.nbt.tag.CompoundTag;
//import net.querz.nbt.tag.ListTag;
//
//public class BlockDecoder {
//
//    /**
//     * Получает имя блока из секции чанка по локальным координатам.
//     * @param section Тег секции чанка (CompoundTag)
//     * @param localX Координата X внутри чанка (0-15)
//     * @param localY Координата Y внутри секции (0-15)
//     * @param localZ Координата Z внутри чанка (0-15)
//     * @return Название блока, например "minecraft:water"
//     */
//    public String getBlockName(Section section, int localX, int localY, int localZ) {
//        // 1. Используем встроенный метод библиотеки
//        // Он уже знает, как парсить block_states и как обращаться к palette
//        CompoundTag blockState = section.getBlockStateAt(localX, localY, localZ);
//
//        if (blockState == null) {
//            return "minecraft:air";
//        }
//
//        // 2. Просто достаем имя блока из полученного CompoundTag
//        return blockState.getString("Name");
//    }
//}