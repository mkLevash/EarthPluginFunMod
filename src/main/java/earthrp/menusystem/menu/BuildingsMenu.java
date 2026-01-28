package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.buildings.buy.StandartBuildingsMenu;
import earthrp.menusystem.menu.buildings.buy.WarBuildingsMenu;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BuildingsMenu extends Menu {
    public BuildingsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "Строения";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        switch (e.getCurrentItem().getType()){

            case CRAFTING_TABLE -> {
                e.getWhoClicked().closeInventory();
                new StandartBuildingsMenu(menuUtility).open();

            }
            case ICE -> {
                e.getWhoClicked().closeInventory();
                new WarBuildingsMenu(menuUtility).open();

            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                new Main(menuUtility).open();

            }

        }

    }

    @Override
    public void setMenuItems() {

        ItemStack economy = new ItemStack(Material.CRAFTING_TABLE, 1);
        ItemMeta economyMeta = economy.getItemMeta();
        economyMeta.setDisplayName(ChatColor.YELLOW + "Основные");
        economy.setItemMeta(economyMeta);

        ItemStack war = Tools.createItem(Material.ICE,ChatColor.RED + "Военные", List.of(),"art");



        inventory.setItem(3, economy);

        inventory.setItem(4, war);
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        inventory.setItem(8, createBackItem());

    }
}
