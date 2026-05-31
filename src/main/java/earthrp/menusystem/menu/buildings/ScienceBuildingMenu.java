package earthrp.menusystem.menu.buildings;

import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.BuildingsMenu;
import earthrp.menusystem.menu.DeleteConfirmMenu;
import earthrp.menusystem.menu.TechnologyMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ScienceBuildingMenu extends Menu {
    private final Earth earthPlugin;
    Building b = menuUtility.getBuilding();
    public ScienceBuildingMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        if(b.getType().equals("university")){
            oiIncome = Earth.getInstance().getConfig().getInt("universityIncome");
        } else if (b.getType().equals("school")) {
            oiIncome = Earth.getInstance().getConfig().getInt("schoolIncome");
        }
    }
    int oiIncome;

    @Override
    public String getMenuName() {

        return switch (b.getType()) {
            case "university" -> "Университет";
            case "school" -> "Школа";
            default -> "Ошибка ID";
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

        ItemStack science;
        ItemMeta scienceMeta;
        science = new ItemStack(Material.PRISMARINE_CRYSTALS, 1);
        scienceMeta = science.getItemMeta();
        scienceMeta.setDisplayName(ChatColor.AQUA + "Ваш прирост ОИ увеличен на " + oiIncome);
        science.setItemMeta(scienceMeta);

        ItemStack item;
        ItemMeta itemMeta;
        item = new ItemStack(Material.BOOK, 1);
        itemMeta = item.getItemMeta();
        itemMeta.setLore(List.of(ChatColor.WHITE + "Здание производит этот ресурс"));
        item.setItemMeta(itemMeta);

        ItemStack market;
        ItemMeta marketMeta;
        market = new ItemStack(Material.LEGACY_BOOK_AND_QUILL, 1);
        marketMeta = market.getItemMeta();
        marketMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Более подробная информация");
        market.setItemMeta(marketMeta);

        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));
        delete.setItemMeta(deleteMeta);






        inventory.setItem(3, town);
        inventory.setItem(4, science);
        if(b.getType().equals("university")){
            inventory.setItem(5, market);
        }
        inventory.setItem(8, delete);


    }
}
