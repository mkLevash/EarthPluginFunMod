package earthrp.menusystem.menu;

import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.ideas.IdeaMenu;
import earthrp.menusystem.menu.ideas.MilIdeaMenu;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static earthrp.tools.PDCKeys.*;

public class MilIdeasMenu extends Menu {
    public MilIdeasMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }


    @Override
    public String getMenuName() {
        return "Идеи";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e){
        ItemStack idea = e.getCurrentItem();
        if(idea!=null && idea.hasItemMeta()){
            if(idea.getType().equals(Material.BARRIER)){
                e.getWhoClicked().closeInventory();
                new TechnologyMenu(menuUtility).open();
            }else{
                if(idea.getPersistentDataContainer().has(ideaTypeKey)){
                    String type = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaTypeKey,PersistentDataType.STRING);
                    String material = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaMaterialKey,PersistentDataType.STRING);
                    String color = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaColorKey,PersistentDataType.STRING);
                    String name = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
                    menuUtility.setIdeaType(type);
                    menuUtility.setIdeaMaterial(material);
                    menuUtility.setIdeaColor(color);
                    menuUtility.setIdeaName(name);
                    menuUtility.setTerrain(1);
                    new IdeaMenu(menuUtility).open();
                } else if (idea.getPersistentDataContainer().has(milIdeaTypeKey)) {
                    String type = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(milIdeaTypeKey,PersistentDataType.STRING);
                    String material = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaMaterialKey,PersistentDataType.STRING);
                    String color = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaColorKey,PersistentDataType.STRING);
                    String name = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
                    menuUtility.setIdeaType(type);
                    menuUtility.setIdeaMaterial(material);
                    menuUtility.setIdeaColor(color);
                    menuUtility.setIdeaName(name);
                    new MilIdeaMenu(menuUtility).open();

                }

            }
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();



        inventory.setItem(34, makeIdeaItem(Material.RED_SHULKER_BOX,"Наступление","offence","<red>","RED"));
        inventory.setItem(33, makeIdeaItem(Material.RED_SHULKER_BOX,"Оборона","defence","<red>","RED"));
        inventory.setItem(29, makeIdeaItem(Material.RED_SHULKER_BOX,"Качество","quality","<red>","RED"));
        inventory.setItem(28, makeIdeaItem(Material.RED_SHULKER_BOX,"Количество","quantity","<red>","RED"));
        inventory.setItem(31, makeIdeaItem(Material.BLUE_SHULKER_BOX,"Флот","naval","<blue>","BLUE"));
        inventory.setItem(16, makeMilIdeaItem(Material.RED_SHULKER_BOX,"Натиск","shock","<red>","RED"));
        inventory.setItem(15, makeMilIdeaItem(Material.RED_SHULKER_BOX,"Огонь","fire","<red>","RED"));
        inventory.setItem(12, makeMilIdeaItem(Material.RED_SHULKER_BOX,"Артиллерия","artillery","<red>","RED"));
        inventory.setItem(11, makeMilIdeaItem(Material.RED_SHULKER_BOX,"Кавалерия","cavalry","<red>","RED"));
        inventory.setItem(10, makeMilIdeaItem(Material.RED_SHULKER_BOX,"Пехота","infantry","<red>","RED"));
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 35);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);


        ItemStack back = createBackItem();
        ItemMeta meta = back.getItemMeta();
        meta.getPersistentDataContainer().set(milIdeaTypeKey,PersistentDataType.STRING,"penis");
        back.setItemMeta(meta);
        inventory.setItem(40, back);

    }

    private ItemStack makeIdeaItem(Material shulkerBox, String displayName, String type, String color, String material){

        ItemStack idea = new ItemStack(shulkerBox);
        ItemMeta meta = idea.getItemMeta();
        meta.displayName(Tools.deserialize(color + displayName) );
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,type);
        meta.getPersistentDataContainer().set(milIdeaTypeKey, PersistentDataType.STRING,type);
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,color);
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,material);
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,displayName);
        idea.setItemMeta(meta);
        return idea;

    }

    private ItemStack makeMilIdeaItem(Material shulkerBox, String displayName, String type, String color, String material){

        ItemStack idea = new ItemStack(shulkerBox);
        ItemMeta meta = idea.getItemMeta();
        meta.displayName(Tools.deserialize(color + displayName) );
        meta.getPersistentDataContainer().set(milIdeaTypeKey, PersistentDataType.STRING,type);
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,color);
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,material);
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,displayName);
        idea.setItemMeta(meta);
        return idea;

    }
}
