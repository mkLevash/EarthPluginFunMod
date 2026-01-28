package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.tech.*;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static earthrp.tools.PDCKeys.*;

public class TechnologyMenu extends Menu {
    private final Earth earthPlugin;
    public TechnologyMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
    }

    @Override
    public String getMenuName() {
        return "Технологии";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if(item!=null){
            switch (e.getCurrentItem().getType()){

                case GOLD_INGOT -> {
                    e.getWhoClicked().closeInventory();
                    new EcoTechMenu(menuUtility, this.earthPlugin).open();

                }
                case BONE_MEAL -> {
                    e.getWhoClicked().closeInventory();
                    new ReusTechMenu(menuUtility,this.earthPlugin).open();

                }
                case BOOK -> {
                    e.getWhoClicked().closeInventory();
                    new SocTechMenu(menuUtility, this.earthPlugin).open();

                }
                case CRAFTING_TABLE -> {
                    e.getWhoClicked().closeInventory();
                    new CraftTechMenu(menuUtility, this.earthPlugin).open();

                }
                case FIRE_CHARGE -> {

                    e.getWhoClicked().closeInventory();
                    new WarTechMenu(menuUtility, this.earthPlugin).open();

                }
                case BARRIER -> {
                    e.getWhoClicked().closeInventory();
                    new MainMenu(menuUtility, this.earthPlugin).open();

                }
            }
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if(data.has(mainMenuIdKey)){
                e.getWhoClicked().closeInventory();
                switch (data.get(mainMenuIdKey, PersistentDataType.STRING)){
                    case "war" ->{
                        new WarTechMenu(menuUtility, this.earthPlugin).open();
                    }
                    case "reus" ->{
                        new ReusTechMenu(menuUtility, this.earthPlugin).open();
                    }
                }
            }

        }



    }

    @Override
    public void setMenuItems() {

        ItemStack economy = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta economyMeta = economy.getItemMeta();
        economyMeta.setDisplayName(ChatColor.YELLOW + "Экономические");
        economy.setItemMeta(economyMeta);

        ItemStack reusable = Tools.createMainMenuItem("Многоразовые","techReus","reus");

        ItemStack social = new ItemStack(Material.BOOK, 1);
        ItemMeta socialMeta = social.getItemMeta();
        socialMeta.setDisplayName(ChatColor.BLUE + "Социальные");
        social.setItemMeta(socialMeta);

        ItemStack craft = new ItemStack(Material.CRAFTING_TABLE, 1);
        ItemMeta craftMeta = craft.getItemMeta();
        craftMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Ремесленные");
        craft.setItemMeta(craftMeta);

        ItemStack war = Tools.createMainMenuItem("Военные","menuArmyStat","war");

        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.RED + "BACK");
        next.setItemMeta(nextMeta);


        inventory.setItem(2, economy);

        inventory.setItem(3, reusable);

        inventory.setItem(4, social);

        inventory.setItem(5, craft);

        inventory.setItem(6, war);
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        inventory.setItem(8, next);

    }
}
