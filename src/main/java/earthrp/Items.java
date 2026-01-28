package earthrp;

import earthrp.files.CustomConfig;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class Items {

    public static ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);



        return item;
    }


    public static ArrayList<ItemStack> getWoods(){
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(new ItemStack(Material.OAK_LOG));
        result.add(new ItemStack(Material.OAK_WOOD));

        result.add(new ItemStack(Material.DARK_OAK_LOG));
        result.add(new ItemStack(Material.DARK_OAK_WOOD));

        result.add(new ItemStack(Material.BIRCH_LOG));
        result.add(new ItemStack(Material.BIRCH_WOOD));

        result.add(new ItemStack(Material.SPRUCE_LOG));
        result.add(new ItemStack(Material.SPRUCE_WOOD));

        result.add(new ItemStack(Material.JUNGLE_LOG));
        result.add(new ItemStack(Material.JUNGLE_WOOD));

        result.add(new ItemStack(Material.CHERRY_LOG));
        result.add(new ItemStack(Material.CHERRY_WOOD));

        result.add(new ItemStack(Material.ACACIA_LOG));
        result.add(new ItemStack(Material.ACACIA_WOOD));

        result.add(new ItemStack(Material.MANGROVE_LOG));
        result.add(new ItemStack(Material.MANGROVE_WOOD));

        result.add(new ItemStack(Material.PALE_OAK_LOG));
        result.add(new ItemStack(Material.PALE_OAK_WOOD));
        return result;
    }

    public static ArrayList<ItemStack> getMineV1Items() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(Material.COBBLESTONE, "Булыжник " + Earth.getInstance().getConfig().getInt("tradeItems.COBBLESTONE") + "$"));
        result.add(makeItem(Material.RAW_COPPER,"Сырая медь " + Earth.getInstance().getConfig().getInt("tradeItems.RAW_COPPER") + "$"));
        result.add(makeItem(Material.COAL, "Уголь " + Earth.getInstance().getConfig().getInt("tradeItems.COAL") + "$"));


        result.add(new ItemStack(Material.GRANITE));
        result.add(new ItemStack(Material.DIORITE));
        result.add(new ItemStack(Material.ANDESITE));
        result.add(new ItemStack(Material.COBBLED_DEEPSLATE));
        result.add(new ItemStack(Material.TUFF));

        return result;
    }

    public static ArrayList<ItemStack> getMineV2Items() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(Material.GOLD_INGOT,"Золото "+ Earth.getInstance().getConfig().getInt("tradeItems.GOLD_INGOT") + "$"));
        result.add(makeItem(Material.RAW_IRON,"Сырое железо " + Earth.getInstance().getConfig().getInt("tradeItems.RAW_IRON") + "$"));
        result.add(makeItem(Material.RAW_COPPER,"Сырая медь " + Earth.getInstance().getConfig().getInt("tradeItems.RAW_COPPER") + "$"));
        result.add(makeItem(Material.COAL, "Уголь " + Earth.getInstance().getConfig().getInt("tradeItems.COAL") + "$"));
        result.add(makeItem(Material.COBBLESTONE, "Булыжник " + Earth.getInstance().getConfig().getInt("tradeItems.COBBLESTONE") + "$"));

        result.add(new ItemStack(Material.STONE));
        result.add(new ItemStack(Material.GRANITE));
        result.add(new ItemStack(Material.POLISHED_GRANITE));
        result.add(new ItemStack(Material.DIORITE));
        result.add(new ItemStack(Material.POLISHED_DIORITE));
        result.add(new ItemStack(Material.ANDESITE));
        result.add(new ItemStack(Material.POLISHED_ANDESITE));
        result.add(new ItemStack(Material.COBBLED_DEEPSLATE));
        result.add(new ItemStack(Material.DEEPSLATE));
        result.add(new ItemStack(Material.POLISHED_DEEPSLATE));
        result.add(new ItemStack(Material.CHISELED_DEEPSLATE));
        result.add(new ItemStack(Material.TUFF));
        result.add(new ItemStack(Material.POLISHED_TUFF));
        result.add(new ItemStack(Material.CHISELED_TUFF));

        return result;
    }

    public static ArrayList<ItemStack> getCareerItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(Material.DIAMOND, "Алмаз " + Earth.getInstance().getConfig().getInt("tradeItems.DIAMOND") + "$"));
        result.add(makeItem(Material.AMETHYST_SHARD, "Аметист " + Earth.getInstance().getConfig().getInt("tradeItems.AMETHYST_SHARD") + "$"));
        result.add(makeItem(Material.GOLD_INGOT,"Золото "+ Earth.getInstance().getConfig().getInt("tradeItems.GOLD_INGOT") + "$"));
        result.add(makeItem(Material.IRON_INGOT,"Обработанное железо " + Earth.getInstance().getConfig().getInt("tradeItems.IRON_INGOT") + "$"));
        result.add(makeItem(Material.COPPER_INGOT,"Обработанная медь " + Earth.getInstance().getConfig().getInt("tradeItems.COPPER_INGOT") + "$"));
        result.add(makeItem(Material.COAL, "Уголь " + Earth.getInstance().getConfig().getInt("tradeItems.COAL") + "$"));
        result.add(makeItem(Material.COBBLESTONE, "Булыжник " + Earth.getInstance().getConfig().getInt("tradeItems.COBBLESTONE") + "$"));

        result.add(new ItemStack(Material.STONE));
        result.add(new ItemStack(Material.GRANITE));
        result.add(new ItemStack(Material.POLISHED_GRANITE));
        result.add(new ItemStack(Material.DIORITE));
        result.add(new ItemStack(Material.POLISHED_DIORITE));
        result.add(new ItemStack(Material.ANDESITE));
        result.add(new ItemStack(Material.POLISHED_ANDESITE));
        result.add(new ItemStack(Material.COBBLED_DEEPSLATE));
        result.add(new ItemStack(Material.DEEPSLATE));
        result.add(new ItemStack(Material.POLISHED_DEEPSLATE));
        result.add(new ItemStack(Material.CHISELED_DEEPSLATE));
        result.add(new ItemStack(Material.TUFF));
        result.add(new ItemStack(Material.POLISHED_TUFF));
        result.add(new ItemStack(Material.CHISELED_TUFF));

        return result;
    }

    public static ArrayList<ItemStack> getPastureItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(Material.LEATHER, "Кожа " + Earth.getInstance().getConfig().getInt("tradeItems.LEATHER") + "$"));
        result.add(makeItem(Material.FEATHER, "Перо " + Earth.getInstance().getConfig().getInt("tradeItems.FEATHER") + "$"));
        result.add(makeItem(Material.WHITE_WOOL, "Шерсть " + Earth.getInstance().getConfig().getInt("tradeItems.WHITE_WOOL") + "$"));


        return result;
    }
    public static ArrayList<ItemStack> getFarmItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(Material.SUGAR_CANE, "Тростник " + Earth.getInstance().getConfig().getInt("tradeItems.SUGAR_CANE") + "$"));
        result.add(makeItem(Material.COCOA_BEANS, "Какао-бобы " + Earth.getInstance().getConfig().getInt("tradeItems.COCOA_BEANS") + "$"));
        result.add(makeItem(Material.GLOWSTONE_DUST,"Специи " + Earth.getInstance().getConfig().getInt("tradeItems.GLOWSTONE_DUST") + "$"));
        result.add(makeItem(Material.MELON, "Арбуз " + Earth.getInstance().getConfig().getInt("tradeItems.MELON") + "$"));
        result.add(makeItem(Material.PUMPKIN, "Тыква " + Earth.getInstance().getConfig().getInt("tradeItems.PUMPKIN") + "$"));
        result.add(makeItem(Material.GREEN_DYE, "Чай " + Earth.getInstance().getConfig().getInt("tradeItems.GREEN_DYE") + "$"));
        result.add(makeItem(Material.WHEAT, "Пшеница " + Earth.getInstance().getConfig().getInt("tradeItems.WHEAT") + "$"));

        result.add(makeItem(Material.CARROT, "Морковь " + Earth.getInstance().getConfig().getInt("tradeItems.CARROT") + "$"));
        result.add(makeItem(Material.POTATO, "Картошка " + Earth.getInstance().getConfig().getInt("tradeItems.POTATO") + "$"));
        result.add(makeItem(Material.BEETROOT, "Редис " + Earth.getInstance().getConfig().getInt("tradeItems.BEETROOT") + "$"));




        return result;
    }

    public static ArrayList<ItemStack> getForgeItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(Material.IRON_SWORD, "Оружие " + Earth.getInstance().getConfig().getInt("tradeItems.IRON_SWORD") + "$"));




        return result;
    }

    public static ArrayList<ItemStack> getPlantItems() {
        ArrayList<ItemStack> result = new ArrayList<>();
        List<String> bannedName = CustomConfig.get().getStringList("items.banned");

        result.add(makeItem(Material.GUNPOWDER,"Порох " + Earth.getInstance().getConfig().getInt("tradeItems.GUNPOWDER") + "$"));
        result.add(makeItem(Material.DIAMOND, "Алмаз " + Earth.getInstance().getConfig().getInt("tradeItems.DIAMOND") + "$"));
        result.add(makeItem(Material.AMETHYST_SHARD, "Аметист " + Earth.getInstance().getConfig().getInt("tradeItems.AMETHYST_SHARD") + "$"));

        result.add(makeItem(Material.SUGAR_CANE, "Тростник " + Earth.getInstance().getConfig().getInt("tradeItems.SUGAR_CANE") + "$"));
        result.add(makeItem(Material.COCOA_BEANS, "Какао-бобы " + Earth.getInstance().getConfig().getInt("tradeItems.COCOA_BEANS") + "$"));
        result.add(makeItem(Material.GLOWSTONE_DUST,"Специи " + Earth.getInstance().getConfig().getInt("tradeItems.GLOWSTONE_DUST") + "$"));

        result.add(makeItem(Material.IRON_INGOT,"Обработанное железо " + Earth.getInstance().getConfig().getInt("tradeItems.IRON_INGOT") + "$","Требуется наличие сырого железа"));
        result.add(makeItem(Material.COPPER_INGOT,"Обработанная медь " + Earth.getInstance().getConfig().getInt("tradeItems.COPPER_INGOT") + "$","Требуется наличие сырой меди"));
        result.add(makeItem(Material.COAL, "Уголь " + Earth.getInstance().getConfig().getInt("tradeItems.COAL") + "$"));

        result.add(makeItem(Material.MELON, "Арбуз " + Earth.getInstance().getConfig().getInt("tradeItems.MELON") + "$"));
        result.add(makeItem(Material.PUMPKIN, "Тыква " + Earth.getInstance().getConfig().getInt("tradeItems.PUMPKIN") + "$"));
        result.add(makeItem(Material.GREEN_DYE, "Чай " + Earth.getInstance().getConfig().getInt("tradeItems.GREEN_DYE") + "$"));

        result.add(makeItem(Material.PAPER, "Бумага " + Earth.getInstance().getConfig().getInt("tradeItems.PAPER") + "$"));
        result.add(makeItem(Material.BOOK, "Книги " + Earth.getInstance().getConfig().getInt("tradeItems.BOOK") + "$"));
        result.add(makeItem(Material.IRON_SWORD, "Оружие " + Earth.getInstance().getConfig().getInt("tradeItems.IRON_SWORD") + "$"));

        result.add(makeItem(Material.OAK_WOOD, "Древесина " + Earth.getInstance().getConfig().getInt("tradeItems.OAK_WOOD") + "$"));
        result.add(makeItem(Material.COBBLESTONE, "Булыжник " + Earth.getInstance().getConfig().getInt("tradeItems.COBBLESTONE") + "$"));

        result.add(makeItem(Material.WHITE_WOOL, "Шерсть " + Earth.getInstance().getConfig().getInt("tradeItems.WHITE_WOOL") + "$"));
        result.add(makeItem(Material.FIRE_CHARGE, "Ядра для пушек " + Earth.getInstance().getConfig().getInt("tradeItems.FIRE_CHARGE") + "$","Требуется наличие обработанного железа"));
        result.add(makeItem(Material.LEATHER, "Кожа " + Earth.getInstance().getConfig().getInt("tradeItems.LEATHER") + "$"));
        result.add(makeItem(Material.FEATHER, "Перо " + Earth.getInstance().getConfig().getInt("tradeItems.FEATHER") + "$"));



        // Массив явно исключаемых предметов
        Set<Material> excludedMaterials = new HashSet<>(Arrays.asList(
                Material.GUNPOWDER,
                Material.DIAMOND,
                Material.AMETHYST_SHARD,
                Material.IRON_INGOT,
                Material.COPPER_INGOT,
                Material.COAL,
                Material.PAPER,
                Material.BOOK,
                Material.IRON_SWORD,
                Material.SUGAR_CANE,
                Material.COCOA_BEANS,
                Material.MELON,
                Material.PUMPKIN,
                Material.GLOWSTONE_DUST,
                Material.GREEN_DYE,
                Material.COBBLESTONE,
                Material.LEATHER,
                Material.FEATHER,
                Material.OAK_WOOD

        ));
        for (Material material : Material.values()) {
            if (material.equals(Material.AIR)) continue;

            if (!material.isItem()) continue;

            if (excludedMaterials.contains(material)) continue;

            String name = material.name();

            // Автоматические исключения по названиям

            if (!bannedName.stream().anyMatch(name::contains) && !(name.contains("ARROW") && !name.equals("ARROW"))){
                result.add(new ItemStack(material));
            }
        }

        return result;
    }

}
