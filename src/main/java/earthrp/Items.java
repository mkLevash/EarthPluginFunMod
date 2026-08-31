package earthrp;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import earthrp.customEnums.EarthItem;
import earthrp.configs.CustomConfig;
import earthrp.tools.Tools;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static earthrp.tools.PDCKeys.earthItemKey;

public class Items {

    public static ItemStack makeItem(EarthItem item) {

        ItemStack itemStack = new ItemStack(item.getMaterial());
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(item.getDisplayName() + " " + item.getCost() + "$");
        itemMeta.getPersistentDataContainer().set(earthItemKey, PersistentDataType.STRING, item.toString());

        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create();

        // 2. Добавляем фейковый модификатор (+0.0 к урону)
        // Так как он равен 0, он не изменит стандартный урон меча (он останется 7.0)
        modifiers.put(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                NamespacedKey.minecraft("fake_hidden_modifier"),
                0.0,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));

        // 3. Записываем этот модификатор в мету
        itemMeta.setAttributeModifiers(modifiers);

        CustomModelDataComponent cmd = itemMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of(item.getCustomModel()));
        itemMeta.setCustomModelDataComponent(cmd);


        itemStack.setItemMeta(itemMeta);



        return itemStack;
    }


    public static ArrayList<ItemStack> getWoods(){
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.OAK_LOG));

        result.add(makeItem(EarthItem.DARK_OAK_LOG));

        result.add(makeItem(EarthItem.BIRCH_LOG));

        result.add(makeItem(EarthItem.SPRUCE_LOG));

        result.add(makeItem(EarthItem.JUNGLE_LOG));

        result.add(makeItem(EarthItem.CHERRY_LOG));

        result.add(makeItem(EarthItem.ACACIA_LOG));

        result.add(makeItem(EarthItem.MANGROVE_LOG));

        result.add(makeItem(EarthItem.PALE_OAK_LOG));

        result.add(makeItem(EarthItem.CRIMSON_STEM));

        result.add(makeItem(EarthItem.WARPED_STEM));

        result.add(makeItem(EarthItem.BAMBOO));


        return result;
    }

    public static ArrayList<ItemStack> getMineV1Items() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.COBBLESTONE));
        result.add(makeItem(EarthItem.RAW_COPPER));
        result.add(makeItem(EarthItem.RAW_IRON));
        result.add(makeItem(EarthItem.RAW_EBONY));
        result.add(makeItem(EarthItem.DIAMOND));
        result.add(makeItem(EarthItem.GOLD_INGOT));
        result.add(makeItem(EarthItem.COAL));
        result.add(makeItem(EarthItem.LAPIS_LAZULI));
        result.add(makeItem(EarthItem.QUARTZ));
        result.add(makeItem(EarthItem.CLAY_BALL));
        result.add(makeItem(EarthItem.SAND));

        result.add(makeItem(EarthItem.GRANITE));
        result.add(makeItem(EarthItem.DIORITE));
        result.add(makeItem(EarthItem.ANDESITE));
        result.add(makeItem(EarthItem.COBBLED_DEEPSLATE));
        result.add(makeItem(EarthItem.TUFF));
        result.add(makeItem(EarthItem.GRAVEL));
        result.add(makeItem(EarthItem.TERRACOTTA));

        return result;
    }

    public static ArrayList<ItemStack> getMineV2Items() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.DIAMOND));
        result.add(makeItem(EarthItem.RAW_EBONY));
        result.add(makeItem(EarthItem.COBBLESTONE));
        result.add(makeItem(EarthItem.RAW_COPPER));
        result.add(makeItem(EarthItem.RAW_IRON));
        result.add(makeItem(EarthItem.GOLD_INGOT));
        result.add(makeItem(EarthItem.COAL));
        result.add(makeItem(EarthItem.LAPIS_LAZULI));
        result.add(makeItem(EarthItem.QUARTZ));
        result.add(makeItem(EarthItem.CLAY_BALL));
        result.add(makeItem(EarthItem.SAND));

        result.add(makeItem(EarthItem.GRANITE));
        result.add(makeItem(EarthItem.DIORITE));
        result.add(makeItem(EarthItem.ANDESITE));
        result.add(makeItem(EarthItem.COBBLED_DEEPSLATE));
        result.add(makeItem(EarthItem.TUFF));
        result.add(makeItem(EarthItem.GRAVEL));
        result.add(makeItem(EarthItem.TERRACOTTA));



        return result;
    }

    public static ArrayList<ItemStack> getCareerItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.DIAMOND));

        result.add(makeItem(EarthItem.COBBLESTONE));
        result.add(makeItem(EarthItem.COPPER_INGOT));
        result.add(makeItem(EarthItem.IRON_INGOT));
        result.add(makeItem(EarthItem.GOLD_INGOT));
        result.add(makeItem(EarthItem.COAL));
        result.add(makeItem(EarthItem.LAPIS_LAZULI));
        result.add(makeItem(EarthItem.QUARTZ));
        result.add(makeItem(EarthItem.CLAY_BALL));
        result.add(makeItem(EarthItem.SAND));

        result.add(makeItem(EarthItem.GRANITE));
        result.add(makeItem(EarthItem.DIORITE));
        result.add(makeItem(EarthItem.ANDESITE));
        result.add(makeItem(EarthItem.COBBLED_DEEPSLATE));
        result.add(makeItem(EarthItem.TUFF));
        result.add(makeItem(EarthItem.GRAVEL));
        result.add(makeItem(EarthItem.TERRACOTTA));

        return result;
    }

    public static ArrayList<ItemStack> getPastureItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.BEEF));
        result.add(makeItem(EarthItem.MUTTON));
        result.add(makeItem(EarthItem.CHICKEN));
        result.add(makeItem(EarthItem.PORKCHOP));
        result.add(makeItem(EarthItem.RABBIT));


        return result;
    }

    public static ArrayList<ItemStack> getFisherItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.COD));
        result.add(makeItem(EarthItem.SALMON));


        return result;
    }
    public static ArrayList<ItemStack> getFarmItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.WHEAT));
        result.add(makeItem(EarthItem.CARROT));
        result.add(makeItem(EarthItem.POTATO));
        result.add(makeItem(EarthItem.BEETROOT));
        result.add(makeItem(EarthItem.PUMPKIN));
        result.add(makeItem(EarthItem.MELON_SLICE));
        result.add(makeItem(EarthItem.APPLE));
        result.add(makeItem(EarthItem.SWEET_BERRIES));




        return result;
    }

    public static ArrayList<ItemStack> getForgeItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.WOODEN_SWORD));
        result.add(makeItem(EarthItem.STONE_SWORD));
        result.add(makeItem(EarthItem.COPPER_SWORD));
        result.add(makeItem(EarthItem.IRON_SWORD));
        result.add(makeItem(EarthItem.DIAMOND_SWORD));
        result.add(makeItem(EarthItem.NETHERITE_SWORD));




        return result;
    }

    public static ArrayList<ItemStack> getGunFactoryItems() {
        ArrayList<ItemStack> result = new ArrayList<>();

        result.add(makeItem(EarthItem.FIRE_CHARGE));




        return result;
    }
    
    private static ItemStack fillerGlass(){
        return Tools.createItemLegacy(Material.WHITE_STAINED_GLASS_PANE," ",null,UUID.randomUUID().toString());
    }
    
    

    public static ArrayList<ItemStack> getWorkShopItems() {
        ArrayList<ItemStack> result = new ArrayList<>();
        List<String> bannedName = CustomConfig.get().getStringList("items.banned");

        result.add(makeItem(EarthItem.COPPER_INGOT));
        result.add(makeItem(EarthItem.IRON_INGOT));
        result.add(makeItem(EarthItem.EBONY_INGOT));

        result.add(fillerGlass());

        result.add(makeItem(EarthItem.PAPER));
        result.add(makeItem(EarthItem.CLOTH));
        result.add(makeItem(EarthItem.GLASS));

        result.add(makeItem(EarthItem.WOODEN_SWORD));
        result.add(makeItem(EarthItem.STONE_SWORD));
        result.add(makeItem(EarthItem.COPPER_SWORD));

        result.add(fillerGlass());

        result.add(makeItem(EarthItem.WHITE_WOOL));
        result.add(makeItem(EarthItem.LEATHER));
        result.add(makeItem(EarthItem.FEATHER));

        result.add(makeItem(EarthItem.IRON_SWORD));
        result.add(makeItem(EarthItem.DIAMOND_SWORD));
        result.add(makeItem(EarthItem.NETHERITE_SWORD));

        result.add(fillerGlass());

        result.add(makeItem(EarthItem.FLINT));
        result.add(makeItem(EarthItem.CHARCOAL));
        result.add(fillerGlass());


        result.add(makeItem(EarthItem.GUNPOWDER));
        result.add(makeItem(EarthItem.FIRE_CHARGE));
        result.add(makeItem(EarthItem.GUN));


        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());






        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());






        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(makeItem(EarthItem.DARK_OAK_PLANKS));
        result.add(makeItem(EarthItem.BIRCH_PLANKS));
        result.add(makeItem(EarthItem.SPRUCE_PLANKS));
        result.add(fillerGlass());
        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(makeItem(EarthItem.MANGROVE_PLANKS));
        result.add(makeItem(EarthItem.PALE_OAK_PLANKS));
        result.add(makeItem(EarthItem.ACACIA_PLANKS));
        result.add(fillerGlass());
        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(makeItem(EarthItem.CRIMSON_PLANKS));
        result.add(makeItem(EarthItem.CHERRY_PLANKS));
        result.add(makeItem(EarthItem.WARPED_PLANKS));
        result.add(fillerGlass());
        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(makeItem(EarthItem.BAMBOO_PLANKS));
        result.add(makeItem(EarthItem.JUNGLE_PLANKS));
        result.add(makeItem(EarthItem.OAK_PLANKS));
        result.add(fillerGlass());
        result.add(fillerGlass());




        result.add(makeItem(EarthItem.BREAD));
        result.add(makeItem(EarthItem.COOKIE));
        result.add(makeItem(EarthItem.PUMPKIN_PIE));
        result.add(makeItem(EarthItem.BAKED_POTATO));
        result.add(makeItem(EarthItem.GOLDEN_APPLE));
        result.add(makeItem(EarthItem.COOKED_COD));
        result.add(makeItem(EarthItem.COOKED_SALMON));

        result.add(fillerGlass());
        result.add(makeItem(EarthItem.COOKED_BEEF));
        result.add(makeItem(EarthItem.COOKED_PORKCHOP));
        result.add(makeItem(EarthItem.COOKED_CHICKEN));
        result.add(makeItem(EarthItem.COOKED_MUTTON));
        result.add(makeItem(EarthItem.COOKED_RABBIT));
        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(makeItem(EarthItem.GOLDEN_CARROT));
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());



        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());

        result.add(makeItem(EarthItem.BLACK_DYE));
        result.add(makeItem(EarthItem.WHITE_DYE));
        result.add(makeItem(EarthItem.ORANGE_DYE));
        result.add(makeItem(EarthItem.YELLOW_DYE));
        result.add(makeItem(EarthItem.MAGENTA_DYE));
        result.add(makeItem(EarthItem.LIME_DYE));
        result.add(makeItem(EarthItem.PINK_DYE));

        result.add(fillerGlass());
        result.add(makeItem(EarthItem.PURPLE_DYE));
        result.add(makeItem(EarthItem.BLUE_DYE));
        result.add(makeItem(EarthItem.BROWN_DYE));
        result.add(makeItem(EarthItem.RED_DYE));
        result.add(makeItem(EarthItem.CYAN_DYE));
        result.add(fillerGlass());

        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());
        result.add(fillerGlass());



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
                Material.GOLD_INGOT,
                Material.EMERALD,
                Material.EMERALD_BLOCK,
                Material.REDSTONE,
                Material.REDSTONE_BLOCK,
                Material.WHEAT,
                Material.LAPIS_LAZULI,
                Material.QUARTZ,
                Material.PORKCHOP,
                Material.CHICKEN,
                Material.RABBIT,
                Material.MUTTON,
                Material.DIRT_PATH,


                
                Material.OAK_LOG,
                Material.OAK_WOOD,
                Material.DARK_OAK_LOG,
                Material.DARK_OAK_WOOD,
                Material.BIRCH_LOG,
                Material.BIRCH_WOOD,
                Material.SPRUCE_LOG,
                Material.SPRUCE_WOOD,
                Material.JUNGLE_LOG,
                Material.JUNGLE_WOOD,
                Material.CHERRY_LOG,
                Material.CHERRY_WOOD,
                Material.ACACIA_LOG,
                Material.ACACIA_WOOD,
                Material.MANGROVE_LOG,
                Material.MANGROVE_WOOD,
                Material.PALE_OAK_LOG,
                Material.PALE_OAK_WOOD,

                Material.OAK_PLANKS,
                Material.DARK_OAK_PLANKS,
                Material.BIRCH_PLANKS,
                Material.SPRUCE_PLANKS,
                Material.JUNGLE_PLANKS,
                Material.CHERRY_PLANKS,
                Material.ACACIA_PLANKS,
        
                Material.MANGROVE_PLANKS,
                Material.PALE_OAK_PLANKS,

                Material.BREAD,
                Material.COOKED_BEEF,
                Material.COOKED_MUTTON,
                Material.COOKED_CHICKEN,
                Material.COOKED_PORKCHOP,
                Material.COOKED_COD,

                Material.IRON_SWORD,
                Material.GUNPOWDER,
                Material.IRON_INGOT,
                Material.COPPER_INGOT,

                Material.PAPER,
                Material.CHARCOAL,
                Material.WHITE_WOOL,
                Material.LEATHER,
                Material.FEATHER
                

        ));
//        for (Material material : Material.values()) {
//            if (material.equals(Material.AIR)) continue;
//
//            if (!material.isItem()) continue;
//
//            if (excludedMaterials.contains(material)) continue;
//
//            String name = material.name();
//
//            // Автоматические исключения по названиям
//
//            boolean isAllowed = bannedName.stream().noneMatch(name::contains) &&
//                    !(name.contains("WOOD") ||
//                            name.contains("LOG") ||
//                            name.contains("GLASS") ||
//                            name.contains("WOOL") ||
//                            name.contains("INFESTED") ||
//                            name.contains("ICE") ||
//                            name.contains("ARROW"));
//
//            if (isAllowed){
//                result.add(new ItemStack(material));
//            }
//        }

        return result;
    }

}
