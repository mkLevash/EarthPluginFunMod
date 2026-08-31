package earthrp.tools;

import earthrp.Earth;
import earthrp.customEnums.UnitTech;
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


        ItemStack inf0 = Tools.createArmyCraftItem(UnitTech.INF0);

        ShapedRecipe inf0Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "army_inf0"), inf0);
        inf0Recipe.shape("LSL","H H", "FMF");
        inf0Recipe.setIngredient('L', Material.LEATHER);
        inf0Recipe.setIngredient('S', Material.STONE_SWORD);
        inf0Recipe.setIngredient('H', Material.HAY_BLOCK);
        inf0Recipe.setIngredient('M', superMora);
        inf0Recipe.setIngredient('F', Material.FLINT);
        Bukkit.addRecipe(inf0Recipe);


        ItemStack cav0 = Tools.createArmyCraftItem(UnitTech.CAV0);

        ShapedRecipe cav0Recipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "army_cav0"), cav0);
        cav0Recipe.shape("LML","F F", "HMH");
        cav0Recipe.setIngredient('L', Material.LEATHER);
        cav0Recipe.setIngredient('H', Material.HAY_BLOCK);
        cav0Recipe.setIngredient('M', superMora);
        cav0Recipe.setIngredient('F', Material.FLINT);
        Bukkit.addRecipe(cav0Recipe);



        ItemStack town = Tools.createItem(Material.END_CRYSTAL,"Город",null);

        ShapedRecipe townRecipe = new ShapedRecipe(new NamespacedKey(Earth.getInstance(), "town"), town);
        townRecipe.shape("HMH","MBM", "IMC");


        townRecipe.setIngredient('I', Material.IRON_BLOCK);
        townRecipe.setIngredient('M', new RecipeChoice.ExactChoice(superMora));
        townRecipe.setIngredient('B', Material.WHITE_BANNER);
        townRecipe.setIngredient('H', Material.HAY_BLOCK);
        townRecipe.setIngredient('C', Material.COAL_BLOCK);
        Bukkit.addRecipe(townRecipe);

    }
}
