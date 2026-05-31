package earthrp.menusystem.menu;

import earthrp.customObjects.Army;
import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class DeleteConfirmMenu extends Menu {

    Town town = menuUtility.getDeleteTown();
    Building building = menuUtility.getDeleteBuilding();
    Army army = menuUtility.getDeleteArmy();
    private final ServerDatabase db;
    public DeleteConfirmMenu(MenuUtility menuUtility) {
        super(menuUtility);
        db = Earth.getInstance().getServerDatabase();
    }

    @Override
    public String getMenuName() {
//        if(town==null){
//            return "Вы уверены что хотите безвозвратно удалить строение?";
//        }else{
//            return "Вы уверены что хотите безвозвратно удалить город?";
//        }
        return "Вы уверены?";

    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        switch (e.getCurrentItem().getType()){
            case EMERALD:
                //they pressed yes, kill yourself
                e.getWhoClicked().closeInventory();
                if(building!=null){
                    db.deleteBuilding(building);
                }
                if(town!=null){
                    db.deleteTown(town);
                }
                if(army!=null){
                    army.mergeUnits();
                    army.disband(e.getWhoClicked().getInventory());
                    menuUtility.getArmyShulkerBox().getInventory().clear();
                    db.deleteArmy(army);
                }
//                else if (market!=null) {
//                    db.deleteMarket(market);
//                }
                break;
            case BARRIER:
                e.getWhoClicked().closeInventory();
                break;
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack yes = new ItemStack(Material.EMERALD, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Да");
        ArrayList<String> yes_lore = new ArrayList<>();
        if(town==null){
            yes_lore.add(ChatColor.AQUA + "Вы безвозвратно удалите это строение");;
        }else{
            yes_lore.add(ChatColor.AQUA + "Вы безвозвратно удалите этот город");;
        }

        yes_meta.setLore(yes_lore);
        yes.setItemMeta(yes_meta);
        ItemStack no = new ItemStack(Material.BARRIER, 1);
        ItemMeta no_meta = no.getItemMeta();
        no_meta.setDisplayName(ChatColor.DARK_RED + "Нет");
        no.setItemMeta(no_meta);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }
}
