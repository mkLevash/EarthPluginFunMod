package earthrp.tools;

import earthrp.Earth;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.Collections;
import java.util.List;

public final class Crafts {

    
    public static void enableCrafts(){

        
        ItemStack mora = Tools.createMora(1);

        ItemStack manpower = new ItemStack(Material.VILLAGER_SPAWN_EGG, 1);
        ItemMeta manpowerMeta = manpower.getItemMeta();
        assert manpowerMeta != null;
        manpowerMeta.setDisplayName("Manpower");
        manpower.setItemMeta(manpowerMeta);


        ItemStack superMora = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta superMoraMeta = superMora.getItemMeta();

        CustomModelDataComponent cmd = superMoraMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of("moraIngot"));
        superMoraMeta.setCustomModelDataComponent(cmd);

        superMoraMeta.setDisplayName(ChatColor.YELLOW + "Горсть Моры");
        superMoraMeta.setLore(Collections.singletonList("9 моры"));
        superMora.setItemMeta(superMoraMeta);
        ShapedRecipe superMoraRecipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "superMora"), superMora);
        superMoraRecipe.shape("XXX","XXX", "XXX");
        superMoraRecipe.setIngredient('X', new RecipeChoice.ExactChoice(mora));
        Bukkit.addRecipe(superMoraRecipe);

        ItemStack megaMora = new ItemStack(Material.GOLD_BLOCK, 1);
        ItemMeta megaMoraMeta = megaMora.getItemMeta();
        assert megaMoraMeta != null;
        megaMoraMeta.setDisplayName(ChatColor.YELLOW + "Сундук Моры");
        megaMoraMeta.setLore(Collections.singletonList("81 моры"));

        cmd = superMoraMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of("moraBlock"));
        megaMoraMeta.setCustomModelDataComponent(cmd);

        megaMora.setItemMeta(megaMoraMeta);
        ShapedRecipe megaMoraRecipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "megaMora"), megaMora);
        megaMoraRecipe.shape("XXX","XXX", "XXX");
        megaMoraRecipe.setIngredient('X', new RecipeChoice.ExactChoice(superMora));
        Bukkit.addRecipe(megaMoraRecipe);


        List<String> art1Lore = List.of(
                Tools.colorText("&fМоральᠩ&25.0"),
                Tools.colorText("&fУрон - &44.0&f/&60.5"),
                Tools.colorText("&fОчки &45&f/&62&f/&22"));

        ItemStack art1 = Tools.createArmyCraftItem(
                "&cАртиллерия &a1&f уровня",art1Lore,"art",3,0.0,4.0,0.5
        );

        ShapedRecipe art1Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_art1"), art1);
        art1Recipe.shape("#D#","G G", "F F");
        art1Recipe.setIngredient('#', Material.IRON_BLOCK);
        art1Recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        art1Recipe.setIngredient('G', Material.GUNPOWDER);
        art1Recipe.setIngredient('F', Material.FIRE_CHARGE);
        Bukkit.addRecipe(art1Recipe);


        List<String> art2Lore = List.of(
                Tools.colorText("&fМоральᠩ&26.0"),
                Tools.colorText("&fУрон - &48.0&f/&61.5"),
                Tools.colorText("&fОчки &48&f/&64&f/&24"));
        ItemStack art2 = Tools.createArmyCraftItem(
                "&cАртиллерия &a2&f уровня",art2Lore,"art",4,0.0,30.0,0.0
        );
        ShapedRecipe art2Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_art2"), art2);
        art2Recipe.shape("WAD","G G", "F F");
        art2Recipe.setIngredient('W', Material.IRON_BLOCK);
        art2Recipe.setIngredient('A', Material.AMETHYST_BLOCK);
        art2Recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        art2Recipe.setIngredient('G', Material.GUNPOWDER);

        art2Recipe.setIngredient('F', Material.FIRE_CHARGE);
        Bukkit.addRecipe(art2Recipe);


        List<String> inf1Lore = List.of(
                Tools.colorText("&fМоральᠩ&23.0"),
                Tools.colorText("&fУрон - &40.3&f/&60.35"),
                Tools.colorText("&fОчки &41&f/&60&f/&20"));
        ItemStack inf1 = Tools.createArmyCraftItem(
                "&fПехота &a1&f уровня",inf1Lore,"inf",1,0.0,0.3,0.35
        );

        ShapedRecipe inf1Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_inf1"), inf1);
        inf1Recipe.shape("LSL","H H", "F F");
        inf1Recipe.setIngredient('L', Material.LEATHER);
        inf1Recipe.setIngredient('S', Material.STONE_SWORD);
        inf1Recipe.setIngredient('H', Material.HAY_BLOCK);

        inf1Recipe.setIngredient('F', Material.FLINT);
        Bukkit.addRecipe(inf1Recipe);

        List<String> inf2Lore = List.of(
                Tools.colorText("&fМоральᠩ&24.0"),
                Tools.colorText("&fУрон - &40.8&f/&60.95"),
                Tools.colorText("&fОчки &41&f/&61&f/&20"));
        ItemStack inf2 = Tools.createArmyCraftItem(
                "&fПехота &a2&f уровня",inf2Lore,"inf",2,0.0,0.8,0.95
        );

        ShapedRecipe inf2Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_inf2"), inf2);
        inf2Recipe.shape("ILI","A A", "H H");


        inf2Recipe.setIngredient('I', Material.IRON_BLOCK);
        inf2Recipe.setIngredient('L', Material.LEATHER);
        inf2Recipe.setIngredient('A', Material.ARROW);
        inf2Recipe.setIngredient('H', Material.HAY_BLOCK);
        Bukkit.addRecipe(inf2Recipe);

        List<String> inf3Lore = List.of(
                Tools.colorText("&fМоральᠩ&25.0"),
                Tools.colorText("&fУрон - &42.1&f/&61.6"),
                Tools.colorText("&fОчки &42&f/&61&f/&21"));
        ItemStack inf3 = Tools.createArmyCraftItem(
                "&fПехота &a3&f уровня",inf3Lore,"inf",3,0.0,2.1,1.6
        );

        ShapedRecipe inf3Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_inf3"), inf3);
        inf3Recipe.shape("ILI","A D", "H H");


        inf3Recipe.setIngredient('I', Material.COOKED_PORKCHOP);
        inf3Recipe.setIngredient('L', Material.GUNPOWDER);
        inf3Recipe.setIngredient('A', Material.IRON_BLOCK);
        inf3Recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        inf3Recipe.setIngredient('H', Material.HAY_BLOCK);
        Bukkit.addRecipe(inf3Recipe);


        List<String> inf4Lore = List.of(
                Tools.colorText("&fМоральᠩ&26.0"),
                Tools.colorText("&fУрон - &43.1&f/&62.1"),
                Tools.colorText("&fОчки &43&f/&62&f/&22"));
        ItemStack inf4 = Tools.createArmyCraftItem(
                "&fПехота &a4&f уровня",inf4Lore,"inf",4,0.0,3.1,2.1
        );

        ShapedRecipe inf4Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_inf4"), inf4);
        inf4Recipe.shape("AGD","G G", "H H");

        inf4Recipe.setIngredient('H', Material.HAY_BLOCK);
        inf4Recipe.setIngredient('A', Material.AMETHYST_BLOCK);
        inf4Recipe.setIngredient('G', Material.GUNPOWDER);
        inf4Recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        Bukkit.addRecipe(inf4Recipe);


        List<String> cav1Lore = List.of(
                Tools.colorText("&fМоральᠩ&23.0"),
                Tools.colorText("&fУрон - &40.0&f/&61.3"),
                Tools.colorText("&fОчки &40&f/&61&f/&21"));

        ItemStack cav1 = Tools.createArmyCraftItem(
                "&eКавалерия &a1&f уровня",cav1Lore,"cav",1,0.0,0.0,1.3
        );

        ShapedRecipe cav1Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_cav1"), cav1);
        cav1Recipe.shape("L L","F F", "H H");
        cav1Recipe.setIngredient('L', Material.LEATHER);
        cav1Recipe.setIngredient('H', Material.HAY_BLOCK);

        cav1Recipe.setIngredient('F', Material.FLINT);
        Bukkit.addRecipe(cav1Recipe);

        List<String> cav2Lore = List.of(
                Tools.colorText("&fМоральᠩ&24.0"),
                Tools.colorText("&fУрон - &40.0&f/&62.0"),
                Tools.colorText("&fОчки &40&f/&62&f/&21"));
        ItemStack cav2 = Tools.createArmyCraftItem(
                "&eКавалерия &a2&f уровня",cav2Lore,"cav",2,0.0,0.0,2.0
        );
        ShapedRecipe cav2Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_cav2"), cav2);
        cav2Recipe.shape("A L","I I", "H H");
        cav2Recipe.setIngredient('I', Material.IRON_BLOCK);
        cav2Recipe.setIngredient('L', Material.LEATHER);
        cav2Recipe.setIngredient('A', Material.ARROW);
        cav2Recipe.setIngredient('H', Material.HAY_BLOCK);
        Bukkit.addRecipe(cav2Recipe);


        List<String> cav3Lore = List.of(
                Tools.colorText("&fМоральᠩ&25.0"),
                Tools.colorText("&fУрон - &40.5&f/&63.0"),
                Tools.colorText("&fОчки &40&f/&63&f/&22"));
        ItemStack cav3 = Tools.createArmyCraftItem(
                "&eКавалерия &a3&f уровня",cav3Lore,"cav",3,0.0,0.1,3.0
        );

        ShapedRecipe cav3Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_cav3"), cav3);
        cav3Recipe.shape("L C","I D", "H G");

        cav3Recipe.setIngredient('C', Material.COOKED_PORKCHOP);
        cav3Recipe.setIngredient('L', Material.LEATHER);
        cav3Recipe.setIngredient('I', Material.IRON_BLOCK);
        cav3Recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        cav3Recipe.setIngredient('H', Material.HAY_BLOCK);
        cav3Recipe.setIngredient('G', Material.GUNPOWDER);
        Bukkit.addRecipe(cav3Recipe);


        List<String> cav4Lore = List.of(
                Tools.colorText("&fМоральᠩ&26.0"),
                Tools.colorText("&fУрон - &41.0&f/&64.0"),
                Tools.colorText("&fОчки &41&f/&63&f/&23"));
        ItemStack cav4 = Tools.createArmyCraftItem(
                "&eКавалерия &a4&f уровня",cav4Lore,"cav",4,0.0,0.5,4.0
        );

        ShapedRecipe cav4Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "newArmy_cav4"), cav4);
        cav4Recipe.shape("ALD","G G", " H ");

        cav3Recipe.setIngredient('L', Material.LEATHER);
        cav4Recipe.setIngredient('H', Material.HAY_BLOCK);
        cav4Recipe.setIngredient('A', Material.AMETHYST_BLOCK);
        cav4Recipe.setIngredient('G', Material.GUNPOWDER);
        cav4Recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        Bukkit.addRecipe(cav4Recipe);

    }
}
