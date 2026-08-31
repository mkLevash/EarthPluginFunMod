package earthrp.menusystem.menu.ideas;

import earthrp.configs.CustomConfig;
import earthrp.customObjects.EPlayer;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.MainIdeasMenu;
import earthrp.menusystem.menu.MilIdeasMenu;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static earthrp.tools.PDCKeys.ideaOwnerKey;
import static earthrp.tools.PDCKeys.isMilitaryIdeaKey;

public class MilIdeaMenu extends Menu {
    public MilIdeaMenu(MenuUtility menuUtility) {
        super(menuUtility);

        for(int j = 0; j < 3; j++){
            ideaNames.add(menuUtility.getIdeaColor() + CustomConfig.get().getString(path+j+".name"));
            ideaDesc.add(CustomConfig.get().getStringList(path+j+".desc"));
            ideaEffectsId.add(CustomConfig.get().getStringList(path+j+".effectsId"));
            ideaEffects.add(CustomConfig.get().getDoubleList(path+j+".effects"));
        }
    }
    Player p = this.menuUtility.getOwner();


    String path = "ideas." + menuUtility.getIdeaType() + ".idea";

    List<String> ideaNames = new ArrayList<>();
    List<List<String>> ideaDesc = new ArrayList<>();
    List<List<String>> ideaEffectsId = new ArrayList<>();
    List<List<Double>> ideaEffects = new ArrayList<>();

    ItemStack shulkerItem = new ItemStack(Material.getMaterial(menuUtility.getIdeaMaterial()+"_SHULKER_BOX"));


    @Override
    public String getMenuName() {return "";}

    @Override
    public int getSlots() {return 27;}

    @Override
    public void handleMenu(InventoryClickEvent e) {


        switch (e.getCurrentItem().getType()){

            case EMERALD -> {
                e.getWhoClicked().closeInventory();
                e.getWhoClicked().getInventory().addItem(shulkerItem);

            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                new MilIdeasMenu(menuUtility).open();

            }

        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        EPlayer player = menuUtility.getPlayer();
//
//        ItemStack lantern = new ItemStack(Material.SOUL_LANTERN, 1);
//        ItemMeta lanternMeta = lantern.getItemMeta();
//        lanternMeta.setDisplayName("Идея");
//        lanternMeta.addEnchant(Enchantment.INFINITY,1,true);
//        lantern.setItemMeta(lanternMeta);

        ItemStack light = new ItemStack(Material.EGG);
        ItemMeta meta = light.getItemMeta();
        meta.getPersistentDataContainer().set(ideaOwnerKey, PersistentDataType.STRING,player.getUniqueId().toString());
        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of("militaryIdea"));
        meta.setCustomModelDataComponent(cmd);
        meta.setDisplayName(menuUtility.getIdeaName());
        meta.addEnchant(Enchantment.INFINITY,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);


        light.setItemMeta(meta);

        Material material = Material.getMaterial(menuUtility.getIdeaMaterial()+"_CONCRETE_POWDER");


        List<ItemStack> ideas = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemStack idea = Tools.createIdeaItem(material,ideaNames.get(i),ideaEffectsId.get(i),ideaDesc.get(i),ideaEffects.get(i),player.getUniqueId());
            if(menuUtility.getTerrain()==1){
                ItemMeta itemMeta = idea.getItemMeta();
                itemMeta.getPersistentDataContainer().set(isMilitaryIdeaKey,PersistentDataType.BOOLEAN,true);
                idea.setItemMeta(itemMeta);
            }
            ideas.add(idea);
        }





        BlockStateMeta bsm = (BlockStateMeta) shulkerItem.getItemMeta();
        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();

        for (int j = 12; j < 15; j++) {

            inventory.setItem(j, ideas.get(j-12));
            shulker.getInventory().setItem(j, ideas.get(j-12));

        }


        List<String> lore = List.of(Tools.colorText("&fПолучить идеи"));
        ItemStack yes = Tools.createItemLegacy(Material.EMERALD,ChatColor.GREEN + "Да",lore);

        inventory.setItem(9,light);
        meta = light.getItemMeta();
        meta.setLore(List.of(Tools.colorText("&fПринадлежит &d" + player.getDisplayName())));
        light.setItemMeta(meta);
        shulker.getInventory().setItem(9,light);
        inventory.setItem(18,yes);
        inventory.setItem(26, createBackItem());

        bsm.setBlockState(shulker);
        shulker.update();
        bsm.setDisplayName(menuUtility.getIdeaName());
        shulkerItem.setItemMeta(bsm);


    }
}
