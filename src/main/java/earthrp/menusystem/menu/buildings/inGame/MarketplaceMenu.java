package earthrp.menusystem.menu.buildings.inGame;

import earthrp.Earth;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.DeleteConfirmMenu;
import earthrp.menusystem.menu.TownBuildingsMenu;
import earthrp.menusystem.menu.buildings.PaginatedItemMenu;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MarketplaceMenu extends Menu {

    Building b = menuUtility.getBuilding();
    public MarketplaceMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return b.getData().getType().getDisplayName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        Player p = (Player) e.getWhoClicked();
        Material item = Material.AIR;
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
                if(menuUtility.getTown()!=null){
                    new TownBuildingsMenu(menuUtility).open();
                }else{
                    menuUtility.setDeleteBuilding(b);
                    new DeleteConfirmMenu(menuUtility).open();
                }
            }

        }
        if (e.getCurrentItem().getType().equals(item)){
            e.getWhoClicked().closeInventory();
            new PaginatedItemMenu(menuUtility).open();
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        ItemStack town = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta townMeta = town.getItemMeta();
        Town t = Earth.getInstance().getDatabase().getTown(b.getTownId());
        townMeta.setDisplayName(ChatColor.LIGHT_PURPLE + t.getName());
        townMeta.setLore(List.of(ChatColor.WHITE + "Местоположение здания"));
        town.setItemMeta(townMeta);


        ItemStack bItem = menuUtility.getBuildingItem().clone();
        List<String> buildingLore = new ArrayList<>();
        buildingLore.add("Эфф. торговли:");
        switch (b.getData().getType()){
            case MARKETPLACE -> {

                buildingLore.add("От населения(<light_purple>" + b.getTown().getPeasant() + "<white>)<green> +" + (int)(b.getTown().getPeasantTradeMod()*100) +"%");
                buildingLore.add("От производства(<green>"+ b.getTown().getProductionValue() +"<white>)<green> +" + (int) (b.getTown().getProductionTradeMod()*100) +"%");
            }
            case PORT -> {
                buildingLore.add("От кораблей(<light_purple>" + b.getOwner().getData().getTradeShips() + "<white>)<green> +" + (int) ( b.getTown().getFrigateTradeMod() * 100) +"%");
            }
        }
        bItem.lore(makeItem("","","",buildingLore).lore());

        inventory.setItem(4,bItem);


        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));
        if(menuUtility.getTown()!=null){
            deleteMeta.displayName(Tools.deserialize("Назад"));
            deleteMeta.lore(List.of());
        }
        delete.setItemMeta(deleteMeta);






        inventory.setItem(3, town);

        inventory.setItem(8, delete);


    }
}
