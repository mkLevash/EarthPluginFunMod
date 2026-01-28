package earthrp.menusystem.menu.buildings;

import earthrp.customObjects.Building;
import earthrp.Earth;
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

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class MiningBuildingMenu extends Menu {
    private final Earth earthPlugin;
    Building b = menuUtility.getBuilding();
    public MiningBuildingMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
    }

    @Override
    public String getMenuName() {
        System.out.println("[Earth]"+b.getType());
        if(b.getType().equals("pasture")){
            System.out.println("[Earth]ok");
        }

        return switch (b.getType().trim().toLowerCase()) {
            case "minev1" -> "Шахта";
            case "minev2" -> "Рудник";
            case "career" -> "Карьер";
            case "lumber" -> "Лесопилка";
            case "pasture" -> "Пастбище";
            case "farm" -> "Плантация";
            case "factory" -> "Завод";
            case "plant" -> "Мануфактура";
            case "forge" -> "Кузня";
            default -> {
                Bukkit.broadcastMessage("Неизвестный тип: " + b.getType());
                yield "Ошибка ID";
            }
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

            case HEART_OF_THE_SEA -> {

                e.getWhoClicked().closeInventory();

                new TechnologyMenu(new MenuUtility(p), this.earthPlugin).open();

            }
            case SOUL_TORCH, BELL -> {

                e.getWhoClicked().closeInventory();
                //new LoadingMenu(menuUtility, this.earthPlugin).open();
            }
            case REDSTONE_TORCH -> {

                e.getWhoClicked().closeInventory();
                new PaginatedItemMenu(menuUtility, this.earthPlugin).open();

            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                menuUtility.setDeleteBuilding(b);
                new DeleteConfirmMenu(menuUtility,this.earthPlugin).open();
            }

        }
        if (e.getCurrentItem().getType().equals(item)){
            e.getWhoClicked().closeInventory();
            new PaginatedItemMenu(menuUtility, this.earthPlugin).open();
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

        ItemStack item;
        ItemMeta itemMeta;
        if(b.getItem()!=null){
            item = new ItemStack(b.getItem(), 1);
            itemMeta = item.getItemMeta();
            itemMeta.setLore(List.of(ChatColor.WHITE + "Здание производит этот ресурс"));
        }else {
            item = new ItemStack(Material.REDSTONE_TORCH, 1);
            itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(ChatColor.RED + "Производимый ресурс не выбран");
            itemMeta.setLore(List.of(ChatColor.WHITE + "Нажмите чтобы выбрать ресурс"));
        }
        item.setItemMeta(itemMeta);



        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));
        delete.setItemMeta(deleteMeta);






        inventory.setItem(3, town);
        inventory.setItem(4, item);
        inventory.setItem(8, delete);


    }
}
