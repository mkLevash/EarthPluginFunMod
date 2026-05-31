package earthrp.customEnums;

import earthrp.Earth;
import lombok.Getter;
import org.bukkit.Material;

@Getter
public enum TownItem {


    WHEAT(Material.WHEAT,"Зерно"),
    BREAD(Material.BREAD, "Хлеб"),

    BEEF(Material.BEEF, "Сырая говядина"),
    PORKCHOP(Material.PORKCHOP, "Сырая свинина"),
    CHICKEN(Material.CHICKEN, "Сырая курятина"),
    MUTTON(Material.MUTTON, "Сырая баранина"),
    COD(Material.COD, "Сырая треска"),

    COOKED_BEEF(Material.COOKED_BEEF, "Приготовленная говядина"),
    COOKED_PORKCHOP(Material.COOKED_PORKCHOP, "Приготовленная свинина"),
    COOKED_CHICKEN(Material.COOKED_CHICKEN, "Приготовленная курятина"),
    COOKED_MUTTON(Material.COOKED_MUTTON, "Приготовленная баранина"),
    COOKED_COD(Material.COOKED_COD, "Приготовленная треска"),



    OAK_LOG(Material.OAK_LOG, "Дубовое бревно"),
    OAK_WOOD(Material.OAK_WOOD, "Дубовая древесина"),
    OAK_PLANKS(Material.OAK_PLANKS, "Дубовые доски"),
    DARK_OAK_LOG(Material.DARK_OAK_LOG, "Бревно темного дуба"),
    DARK_OAK_WOOD(Material.DARK_OAK_WOOD, "Древесина темного дуба"),
    DARK_OAK_PLANKS(Material.DARK_OAK_PLANKS, "Доски темного дуба"),
    BIRCH_LOG(Material.BIRCH_LOG, "Березовое бревно"),
    BIRCH_WOOD(Material.BIRCH_WOOD, "Березовая древесина"),
    BIRCH_PLANKS(Material.BIRCH_PLANKS, "Березовые доски"),
    SPRUCE_LOG(Material.SPRUCE_LOG, "Еловое бревно"),
    SPRUCE_WOOD(Material.SPRUCE_WOOD, "Еловая древесина"),
    SPRUCE_PLANKS(Material.SPRUCE_PLANKS, "Еловые доски"),
    JUNGLE_LOG(Material.JUNGLE_LOG, "Бревно джунглевого дерева"),
    JUNGLE_PLANKS(Material.JUNGLE_PLANKS, "Доски джунглевого дерева"),
    JUNGLE_WOOD(Material.JUNGLE_WOOD, "Древесина джунглевого дерева"),
    CHERRY_LOG(Material.CHERRY_LOG, "Вишневое бревно"),
    CHERRY_WOOD(Material.CHERRY_WOOD, "Вишневая древесина"),
    CHERRY_PLANKS(Material.CHERRY_PLANKS, "Вишневые доски"),
    ACACIA_LOG(Material.ACACIA_LOG, "Акациевое бревно"),
    ACACIA_WOOD(Material.ACACIA_WOOD, "Акациевая древесина"),
    ACACIA_PLANKS(Material.ACACIA_PLANKS, "Акациевые доски"),
    MANGROVE_LOG(Material.MANGROVE_LOG, "Мангровое бревно"),
    MANGROVE_WOOD(Material.MANGROVE_WOOD, "Мангровая древесина"),
    MANGROVE_PLANKS(Material.MANGROVE_PLANKS, "Мангровые доски"),
    PALE_OAK_LOG(Material.PALE_OAK_LOG, "Бревно бледного дуба"),
    PALE_OAK_WOOD(Material.PALE_OAK_WOOD, "Древесина бледного дуба"),
    PALE_OAK_PLANKS(Material.PALE_OAK_PLANKS, "Доски бледного дуба"),

    DIAMOND(Material.DIAMOND, "Алмаз"),
    AMETHYST_SHARD(Material.AMETHYST_SHARD, "Осколок аметиста"),

    IRON_INGOT(Material.IRON_INGOT, "Железный слиток"),
    RAW_IRON(Material.RAW_IRON, "Необработанное железо"),
    COPPER_INGOT(Material.COPPER_INGOT, "Медный слиток"),
    RAW_COPPER(Material.RAW_COPPER, "Необработанная медь"),
    COAL(Material.COAL, "Уголь"),

    SAND(Material.SAND, "Песок"),
    COBBLESTONE(Material.COBBLESTONE, "Булыжник"),
    GRANITE(Material.GRANITE, "Гранит"),
    DIORITE(Material.DIORITE, "Диорит"),
    ANDESITE(Material.ANDESITE, "Андезит"),
    COBBLED_DEEPSLATE(Material.COBBLED_DEEPSLATE, "Глубинный булыжник"),
    TUFF(Material.TUFF, "Туф"),

    IRON_SWORD(null, "Железный Меч"),
    COPPER_SWORD(null, "Медный Меч"),
    GUN(null, "Оружие"),
    CLOTH(null, "Ткань"),
    CANNONBALL(null, "Пушечное ядро"),

    GUNPOWDER(Material.GUNPOWDER, "Порох"),
    PAPER(Material.PAPER, "Бумага"),
    BOOK(Material.BOOK, "Книга"),

    WOOL(Material.WHITE_WOOL, "Шерсть");








    ;





    TownItem(Material material,String displayName) {
        this.material = material;
        this.displayName = displayName;
    }

    @Getter
    private final Material material;

    @Getter
    private final String displayName;

    public int getCost(){
        return Math.max(1,Earth.getInstance().getConfig().getInt("tradeItems." + this));
    }


    public static TownItem fromString(String text) {
        if (text == null || text.isEmpty()) return null;

        // 1. Сначала вставляем подчеркивания перед заглавными буквами (camelCase -> snake_case)
        String formatted = text.replaceAll("([a-z])([A-Z])", "$1_$2");

        // 2. Убираем лишние пробелы и переводим в верхний регистр
        formatted = formatted.replace(" ", "_").toUpperCase();

        try {
            return valueOf(formatted);
        } catch (IllegalArgumentException e) {
            // Если даже после форматирования не нашли (например, опечатка)
            return null;
        }
    }
}
