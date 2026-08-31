package earthrp.customEnums;

import earthrp.Earth;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Item;

import java.util.Set;

import static earthrp.customEnums.EarthItem.ItemType.*;

@Getter
public enum EarthItem {

    GOLD_INGOT(Material.GOLD_INGOT, "Золото"),

    WHEAT(Material.WHEAT,"Зерно",FOOD,1),
    SWEET_BERRIES(Material.SWEET_BERRIES,"Ягоды",FOOD,1),
    GLOW_BERRIES(Material.GLOW_BERRIES,"Ягоды",FOOD,1),
    CARROT(Material.CARROT,"Морковь",FOOD,1),
    POTATO(Material.POTATO,"Картофель",FOOD,1),
    APPLE(Material.APPLE,"Яблоко",FOOD,1),
    MELON_SLICE(Material.MELON_SLICE,"Арбуз",FOOD,1),
    PUMPKIN(Material.PUMPKIN,"Тыква",FOOD,1),
    BEETROOT(Material.BEETROOT,"Свекла",FOOD,1),
    DRIED_KELP(Material.DRIED_KELP,"Сушенная ламинария",FOOD,1),




    BEEF(Material.BEEF, "Сырая говядина",FOOD,3),
    PORKCHOP(Material.PORKCHOP, "Сырая свинина",FOOD,3),
    CHICKEN(Material.CHICKEN, "Сырая курица",FOOD,3),
    MUTTON(Material.MUTTON, "Сырая баранина",FOOD,3),
    COD(Material.COD, "Сырая треска",FOOD,2),
    SALMON(Material.SALMON, "Сырой лосось",FOOD,2),
    RABBIT(Material.RABBIT, "Сырой кролик",FOOD,3),

    BREAD(Material.BREAD, "Хлеб",Set.of(WHEAT),FOOD,4),
    COOKIE(Material.COOKIE, "Печенье",Set.of(WHEAT),FOOD,4),
    PUMPKIN_PIE(Material.PUMPKIN_PIE,"Тыквенный пирог",Set.of(PUMPKIN),FOOD,4),
    BAKED_POTATO(Material.BAKED_POTATO,"Печённый картофель",Set.of(POTATO),FOOD,4),
    GOLDEN_APPLE(Material.GOLDEN_APPLE,"Золотое яблоко",Set.of(APPLE,GOLD_INGOT),FOOD,4),

    COOKED_BEEF(Material.COOKED_BEEF, "Жаренная говядина", Set.of(BEEF),FOOD,6),
    COOKED_PORKCHOP(Material.COOKED_PORKCHOP, "Жаренная свинина", Set.of(PORKCHOP),FOOD,6),
    COOKED_CHICKEN(Material.COOKED_CHICKEN, "Жаренная курица", Set.of(CHICKEN),FOOD,6),
    COOKED_MUTTON(Material.COOKED_MUTTON, "Жаренная баранина", Set.of(MUTTON),FOOD,6),
    COOKED_COD(Material.COOKED_COD, "Жаренная треска", Set.of(COD),FOOD,5),
    COOKED_SALMON(Material.COOKED_SALMON, "Жаренный лосось", Set.of(SALMON),FOOD,5),
    COOKED_RABBIT(Material.COOKED_RABBIT, "Жаренный кролик", Set.of(RABBIT),FOOD,6),

    CHORUS_FRUIT(Material.CHORUS_FRUIT,"Драконий фрукт",FOOD,67),
    ENCHANTED_GOLDEN_APPLE(Material.ENCHANTED_GOLDEN_APPLE,"Хуяблоко",FOOD,67),
    GOLDEN_CARROT(Material.GOLDEN_CARROT,"Золотая морковь",Set.of(CARROT,GOLD_INGOT),FOOD,10),


    LEATHER(Material.LEATHER, "Кожа"),
    FEATHER(Material.FEATHER, "Перья"),
    WHITE_WOOL(Material.WHITE_WOOL, "Шерсть"),


    OAK_LOG(Material.OAK_LOG, "Дубовое бревно"),
    OAK_PLANKS(Material.OAK_PLANKS, "Дубовые доски", Set.of(OAK_LOG)),

    DARK_OAK_LOG(Material.DARK_OAK_LOG, "Бревно темного дуба"),
    DARK_OAK_PLANKS(Material.DARK_OAK_PLANKS, "Доски темного дуба", Set.of(DARK_OAK_LOG)),

    BIRCH_LOG(Material.BIRCH_LOG, "Березовое бревно"),
    BIRCH_PLANKS(Material.BIRCH_PLANKS, "Березовые доски", Set.of(BIRCH_LOG)),

    SPRUCE_LOG(Material.SPRUCE_LOG, "Еловое бревно"),
    SPRUCE_PLANKS(Material.SPRUCE_PLANKS, "Еловые доски", Set.of(SPRUCE_LOG)),

    JUNGLE_LOG(Material.JUNGLE_LOG, "Бревно джунглевого дерева"),

    JUNGLE_PLANKS(Material.JUNGLE_PLANKS, "Доски джунглевого дерева", Set.of(JUNGLE_LOG)),

    CHERRY_LOG(Material.CHERRY_LOG, "Вишневое бревно"),

    CHERRY_PLANKS(Material.CHERRY_PLANKS, "Вишневые доски", Set.of(CHERRY_LOG)),

    ACACIA_LOG(Material.ACACIA_LOG, "Акациевое бревно"),

    ACACIA_PLANKS(Material.ACACIA_PLANKS, "Акациевые доски", Set.of(ACACIA_LOG)),

    MANGROVE_LOG(Material.MANGROVE_LOG, "Мангровое бревно"),

    MANGROVE_PLANKS(Material.MANGROVE_PLANKS, "Мангровые доски", Set.of(MANGROVE_LOG)),

    PALE_OAK_LOG(Material.PALE_OAK_LOG, "Бревно бледного дуба"),
    PALE_OAK_PLANKS(Material.PALE_OAK_PLANKS, "Доски бледного дуба", Set.of(PALE_OAK_LOG)),

    BAMBOO(Material.BAMBOO, "Бамбук"),
    BAMBOO_PLANKS(Material.BAMBOO_PLANKS, "Бамбуковые доски", Set.of(BAMBOO)),

    CRIMSON_STEM(Material.CRIMSON_STEM, "Багровый стебель"),
    CRIMSON_PLANKS(Material.CRIMSON_PLANKS, "Багровые доски", Set.of(CRIMSON_STEM)),

    WARPED_STEM(Material.WARPED_STEM, "Искажённый стебель"),
    WARPED_PLANKS(Material.WARPED_PLANKS, "Искажённые доски", Set.of(WARPED_STEM)),



    DIAMOND(Material.DIAMOND, "Алмаз"),


    LAPIS_LAZULI(Material.LAPIS_LAZULI, "Лазурит"),
    QUARTZ(Material.QUARTZ, "Кварц"),
    CLAY_BALL(Material.CLAY_BALL, "Глина"),

    TERRACOTTA(Material.TERRACOTTA, "Терракота"),

    BLACK_DYE(Material.BLACK_DYE, "Черный краситель"),
    WHITE_DYE(Material.WHITE_DYE, "Белый краситель"),
    ORANGE_DYE(Material.ORANGE_DYE, "Оранжевый краситель"),
    MAGENTA_DYE(Material.MAGENTA_DYE, "Пурпурный краситель"),
    YELLOW_DYE(Material.YELLOW_DYE, "Желтый краситель"),
    LIME_DYE(Material.LIME_DYE, "Лаймовый краситель"),
    PINK_DYE(Material.PINK_DYE, "Розовый краситель"),
    GRAY_DYE(Material.GRAY_DYE, "Серый краситель"),
    CYAN_DYE(Material.CYAN_DYE, "Бирюзовый краситель"),
    PURPLE_DYE(Material.PURPLE_DYE, "Фиолетовый краситель"),
    BLUE_DYE(Material.BLUE_DYE, "Синий краситель"),
    BROWN_DYE(Material.BROWN_DYE, "Коричневый краситель"),
    RED_DYE(Material.RED_DYE, "Красный краситель"),





    COAL(Material.COAL, "Уголь"),
    CHARCOAL(Material.CHARCOAL, "Древесный Уголь",Set.of(
            OAK_LOG,
            DARK_OAK_LOG,
            BIRCH_LOG,
            SPRUCE_LOG,
            JUNGLE_LOG,
            CHERRY_LOG,
            ACACIA_LOG,
            MANGROVE_LOG,
            PALE_OAK_LOG
    )),
    RAW_EBONY(Material.AMETHYST_CLUSTER, "Эбонитовая руда"),
    EBONY_INGOT(Material.AMETHYST_SHARD, "Эбонитовый слиток",Set.of(RAW_EBONY,COAL,CHARCOAL)),
    RAW_IRON(Material.RAW_IRON, "Железная руда"),
    IRON_INGOT(Material.IRON_INGOT, "Железный слиток",Set.of(RAW_IRON,COAL,CHARCOAL)),
    RAW_COPPER(Material.RAW_COPPER, "Медная руда"),
    COPPER_INGOT(Material.COPPER_INGOT, "Медный слиток",Set.of(RAW_COPPER,COAL,CHARCOAL)),

    SAND(Material.SAND, "Песок"),
    COBBLESTONE(Material.COBBLESTONE, "Булыжник"),
    GRANITE(Material.GRANITE, "Гранит"),
    DIORITE(Material.DIORITE, "Диорит"),
    ANDESITE(Material.ANDESITE, "Андезит"),
    COBBLED_DEEPSLATE(Material.COBBLED_DEEPSLATE, "Глубинный булыжник"),
    TUFF(Material.TUFF, "Туф"),
    GRAVEL(Material.GRAVEL, "Гравий"),
    FLINT(Material.FLINT, "Кремень",Set.of(GRAVEL)),

    GUNPOWDER(Material.GUNPOWDER, "Порох",Set.of(SAND,COAL)),
    PAPER(Material.PAPER, "Бумага", Set.of(
            OAK_PLANKS,
            DARK_OAK_PLANKS,
            BIRCH_PLANKS,
            SPRUCE_PLANKS,
            JUNGLE_PLANKS,
            CHERRY_PLANKS,
            ACACIA_PLANKS,
            MANGROVE_PLANKS,
            PALE_OAK_PLANKS
    )),
    BOOK(Material.BOOK, "Книга",Set.of(PAPER)),

    GLASS(Material.GLASS, "Стекло",Set.of(SAND,COAL,CHARCOAL)),

    WOODEN_SWORD(Material.WOODEN_SWORD, "Деревянный Меч",Set.of(
            OAK_PLANKS,
            DARK_OAK_PLANKS,
            BIRCH_PLANKS,
            SPRUCE_PLANKS,
            JUNGLE_PLANKS,
            CHERRY_PLANKS,
            ACACIA_PLANKS,
            MANGROVE_PLANKS,
            PALE_OAK_PLANKS
    )),
    STONE_SWORD(Material.STONE_SWORD, "Каменный Меч",Set.of(COBBLESTONE)),
    IRON_SWORD(Material.IRON_SWORD, "Железный Меч",Set.of(IRON_INGOT)),
    COPPER_SWORD(Material.STONE_SWORD, "Медный Меч",Set.of(COPPER_INGOT)),
    DIAMOND_SWORD(Material.DIAMOND_SWORD, "Алмазный Меч",Set.of(DIAMOND)),
    NETHERITE_SWORD(Material.NETHERITE_SWORD, "Эбонитовый Меч",Set.of(EBONY_INGOT)),

    GUN(Material.ICE, "Ружье",Set.of(
            IRON_INGOT,
            COPPER_INGOT,
            GUNPOWDER,

            OAK_PLANKS,
            DARK_OAK_PLANKS,
            BIRCH_PLANKS,
            SPRUCE_PLANKS,
            JUNGLE_PLANKS,
            CHERRY_PLANKS,
            ACACIA_PLANKS,
            MANGROVE_PLANKS,
            PALE_OAK_PLANKS)),


    FIRE_CHARGE(Material.FIRE_CHARGE, "Пушечное ядро",Set.of(IRON_INGOT, COPPER_INGOT, GUNPOWDER)),




    CLOTH(Material.ICE, "Ткань",Set.of(WHITE_WOOL)),








    ;





    EarthItem(Material material, String displayName, Set<EarthItem> requirement, ItemType type, int food) {
        this.material = material;
        this.displayName = displayName;
        this.requirement = requirement;
        this.type = type;
        this.food = food;
    }

    EarthItem(Material material, String displayName, ItemType type, int food) {
        this(material,displayName,Set.of(),type, food);   }

    EarthItem(Material material, String displayName, Set<EarthItem> requirement) {
        this(material,displayName,requirement,null,0);   }


    EarthItem(Material material, String displayName) {
        this(material,displayName,Set.of(),null,0);   }

    @Getter
    private final Material material;

    @Getter
    private final String displayName;

    @Getter
    private final Set<EarthItem> requirement;

    @Getter
    private final ItemType type;

    @Getter
    private final int food;

    public int getCost(){
        return Math.max(1,Earth.getInstance().getConfig().getInt("tradeItems." + this));
    }


    public String getCustomModel(){
        switch (this){
            case COPPER_SWORD -> {
                return "copper";
            }
            case GUN -> {
                return "gun";
            }
            case CLOTH -> {
                return "cloth";
            }
            default -> {
                return "";
            }
        }
    }

    public enum ItemType {
        FOOD,
    }


    public static EarthItem fromString(String text) {
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
