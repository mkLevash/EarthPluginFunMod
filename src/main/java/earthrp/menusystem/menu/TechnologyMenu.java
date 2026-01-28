package earthrp.menusystem.menu;

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
    public TechnologyMenu(MenuUtility menuUtility) {
        super(menuUtility);
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
                    new EcoTechMenu(menuUtility).open();

                }
                case BONE_MEAL -> {
                    e.getWhoClicked().closeInventory();
                    new ReusTechMenu(menuUtility).open();

                }
                case BOOK -> {
                    e.getWhoClicked().closeInventory();
                    new SocTechMenu(menuUtility).open();

                }
                case CRAFTING_TABLE -> {
                    e.getWhoClicked().closeInventory();
                    new CraftTechMenu(menuUtility).open();

                }
                case FIRE_CHARGE -> {

                    e.getWhoClicked().closeInventory();
                    new WarTechMenu(menuUtility).open();

                }
                case BARRIER -> {
                    e.getWhoClicked().closeInventory();
                    new Main(menuUtility).open();

                }
            }
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if(data.has(menuIdKey)){
                e.getWhoClicked().closeInventory();
                switch (data.get(menuIdKey, PersistentDataType.STRING)){
                    case "war" ->{
                        new WarTechMenu(menuUtility).open();
                    }
                    case "reus" ->{
                        new ReusTechMenu(menuUtility).open();
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

        ItemStack reusable = makeItem("Многоразовые","techReus","reus");

        ItemStack social = new ItemStack(Material.BOOK, 1);
        ItemMeta socialMeta = social.getItemMeta();
        socialMeta.setDisplayName(ChatColor.BLUE + "Социальные");
        social.setItemMeta(socialMeta);

        ItemStack craft = makeItem(Material.CRAFTING_TABLE,ChatColor.LIGHT_PURPLE + "Ремесленные");

        ItemStack war = makeItem("Военные","menuArmyStat","war");

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
