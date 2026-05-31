package earthrp.menusystem.menu.buildings;

import earthrp.Earth;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.DeleteConfirmMenu;
import earthrp.menusystem.menu.TechnologyMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;

public class landHubMenu extends Menu {
    private final Earth earthPlugin;
    Building b = menuUtility.getBuilding();
    public landHubMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
    }

    @Override
    public String getMenuName() {
        return switch (b.getType()) {
            case "landHub" -> "Рынок";
            case "port" -> "Порт";
            case "barrack" -> "Казармы";
            case "stable" -> "Конюшня";
            case "gunFactory" -> "Оружейная фабрика";
            case "fort" -> "Крепость";
            case "forge" -> "Кузня";
            case "shipyard" -> "Верфь";
            default -> b.getType();
        };
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        Player p = (Player) e.getWhoClicked();
        Material item = b.getItem();
        switch (Objects.requireNonNull(e.getCurrentItem()).getType()){


            case SOUL_TORCH, BELL -> {

                e.getWhoClicked().closeInventory();
                //new LoadingMenu(menuUtility, this.earthPlugin).open();
            }
            case REDSTONE_TORCH -> {

                e.getWhoClicked().closeInventory();
                new PaginatedItemMenu(menuUtility).open();

            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                menuUtility.setDeleteBuilding(b);
                new DeleteConfirmMenu(menuUtility).open();
            }

        }
        if (e.getCurrentItem().getType().equals(item)){
            e.getWhoClicked().closeInventory();
            new PaginatedItemMenu(menuUtility).open();
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack town = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta townMeta = town.getItemMeta();
        Town t = this.earthPlugin.getServerDatabase().getTown(b.getTownId());
        townMeta.setDisplayName(ChatColor.LIGHT_PURPLE + t.getName());
        townMeta.setLore(List.of(ChatColor.WHITE + "Местоположение здания"));
        town.setItemMeta(townMeta);




        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));
        delete.setItemMeta(deleteMeta);






        inventory.setItem(3, town);

        inventory.setItem(8, delete);


    }
}
