package earthrp.menusystem.menu;

import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.buildings.buy.StandartBuildingsMenu;
import earthrp.menusystem.menu.buildings.buy.VillageBuildingsMenu;
import earthrp.menusystem.menu.buildings.buy.WarBuildingsMenu;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
        return 45;
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

            case VILLAGER_SPAWN_EGG -> {
                e.getWhoClicked().closeInventory();
                new VillageBuildingsMenu(menuUtility).open();

            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                new MainMenu(menuUtility).open();

            }

        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        ItemStack economy = new ItemStack(Material.CRAFTING_TABLE, 1);
        ItemMeta economyMeta = economy.getItemMeta();
        economyMeta.setDisplayName(ChatColor.YELLOW + "Основные");
        economy.setItemMeta(economyMeta);

        ItemStack war = Tools.createItemLegacy(Material.ICE,ChatColor.RED + "Военные", List.of(),"art");



        inventory.setItem(21, economy);

        inventory.setItem(22, war);

        inventory.setItem(23, Tools.createItem(Material.VILLAGER_SPAWN_EGG,"Жилые",null));
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
}
