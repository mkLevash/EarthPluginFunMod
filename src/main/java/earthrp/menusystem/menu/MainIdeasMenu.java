package earthrp.menusystem.menu;

import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.ideas.*;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static earthrp.tools.PDCKeys.*;

public class MainIdeasMenu extends Menu {
    public MainIdeasMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }


    @Override
    public String getMenuName() {
        return "Национальные Идеи";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e){

        if(e.getCurrentItem()!=null){
            if(e.getCurrentItem().getType().equals(Material.BARRIER)){
                e.getWhoClicked().closeInventory();
                new TechnologyMenu(menuUtility).open();
            }else{
                String type = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaTypeKey,PersistentDataType.STRING);
                String material = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaMaterialKey,PersistentDataType.STRING);
                String color = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaColorKey,PersistentDataType.STRING);
                String name = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
                menuUtility.setIdeaType(type);
                menuUtility.setIdeaMaterial(material);
                menuUtility.setIdeaColor(color);
                menuUtility.setIdeaName(name);
                new IdeaMenu(menuUtility).open();
            }
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();


        inventory.setItem(10, makeIdeaItem(Material.ORANGE_SHULKER_BOX,"Экономические","economic","<gold>","ORANGE"));
        inventory.setItem(11, makeIdeaItem(Material.GREEN_SHULKER_BOX,"Административные","admin","<green>","GREEN"));
        inventory.setItem(12, makeIdeaItem(Material.YELLOW_SHULKER_BOX,"Торговые","trade","<yellow>","YELLOW"));
        inventory.setItem(13, makeIdeaItem(Material.CYAN_SHULKER_BOX,"Дипломатические","diplomatic","<dark_aqua>","CYAN"));
        inventory.setItem(14, makeIdeaItem(Material.BLACK_SHULKER_BOX,"Империалистические","imperialism","<gray>","BLACK"));
        inventory.setItem(15, makeIdeaItem(Material.LIGHT_BLUE_SHULKER_BOX,"Научные","science","<aqua>","LIGHT_BLUE"));
        inventory.setItem(21, makeIdeaItem(Material.WHITE_SHULKER_BOX,"Освободительные","freedom","<white>","WHITE"));
        inventory.setItem(16, makeIdeaItem(Material.BROWN_SHULKER_BOX,"Реваншистские","revanchism","<#964B00>","BROWN"));
        inventory.setItem(22, makeIdeaItem(Material.PINK_SHULKER_BOX,"Изоляционные","isolation","<#ffc0cb>","PINK"));






        //inventory.setItem(14, colonial);
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

        inventory.setItem(40, createBackItem());

    }

    private ItemStack makeIdeaItem(Material shulkerBox, String displayName, String type, String color, String material){

        ItemStack idea = new ItemStack(shulkerBox);
        ItemMeta meta = idea.getItemMeta();
        meta.displayName(Tools.deserialize(color + displayName) );
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,type);
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,color);
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,material);
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,displayName);
        idea.setItemMeta(meta);
        return idea;


    }
}
