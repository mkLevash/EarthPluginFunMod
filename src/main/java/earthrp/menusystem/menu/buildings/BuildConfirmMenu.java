package earthrp.menusystem.menu.buildings;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BuildConfirmMenu extends Menu {
    public BuildConfirmMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }
    private final ItemStack bItem = menuUtility.getBuildingItem();
    Town town = menuUtility.getTown();
    Inventory chest = menuUtility.getBuildingChest();

    @Override
    public String getMenuName() {
        return "";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getCurrentItem() != null){
            switch (e.getCurrentItem().getType()){
                case EMERALD ->{
                    e.getWhoClicked().closeInventory();
                    Tools.build(bItem,chest,town);
                }

                case BARRIER -> {
                    e.getWhoClicked().closeInventory();
                    new BuildMenu(menuUtility).open();
                }
            }
        }


    }

    @Override
    public void setMenuItems() {
        List<String> lore = List.of(Tools.colorText("&fВы построите &3" + bItem.getItemMeta().getDisplayName() + " &fв &d" + town.getName()));
        ItemStack yes = Tools.createItem(Material.EMERALD,ChatColor.GREEN + "Да",lore);

        ItemStack no = Tools.createItem(Material.BARRIER,ChatColor.RED + "Нет",null);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }
}
