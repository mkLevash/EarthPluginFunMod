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

import java.sql.SQLException;
import java.util.List;

public class BuildingsMenu extends Menu {
    private final Earth earthPlugin;
    public BuildingsMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
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
                Player p = (Player) e.getWhoClicked();
                e.getWhoClicked().closeInventory();
                new StandartBuildingsMenu(new MenuUtility(p), this.earthPlugin).open();

            }
            case ICE -> {
                Player p = (Player) e.getWhoClicked();
                e.getWhoClicked().closeInventory();
                new WarBuildingsMenu(new MenuUtility(p),this.earthPlugin).open();

            }
            case BARRIER -> {
                Player p = (Player) e.getWhoClicked();
                e.getWhoClicked().closeInventory();
                new MainMenu(new MenuUtility(p), this.earthPlugin).open();

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

        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.RED + "BACK");
        next.setItemMeta(nextMeta);


        inventory.setItem(3, economy);

        inventory.setItem(4, war);
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        inventory.setItem(8, next);

    }
}
