package earthrp.menusystem.menu;

import earthrp.customEnums.UnitTech;
import earthrp.customObjects.EPlayer;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.tools.Tools;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class AdminItemsMenu extends Menu {
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();


    public AdminItemsMenu(MenuUtility menuUtility){
        super(menuUtility);
    }


    @Override
    public String getMenuName() {
        return "Строения";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {


        ItemStack item = e.getCurrentItem();
        if(item != null){
            if (item.getType() == Material.BARRIER) {
                p.closeInventory();
                new AdminMenu(menuUtility).open();
            } else {
                p.getInventory().addItem(item);
            }
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();


        inventory.setItem(0,Tools.createItem(Material.ICE,"<aqua>Политическая власть",null,"politPower"));
        inventory.setItem(1,Tools.createIdea());
        inventory.setItem(2,Tools.createMilitaryIdea());
        inventory.setItem(3,Tools.createMora(1));
        inventory.setItem(4,Tools.createMoraIngot(1));
        inventory.setItem(5,Tools.createMoraBlock(1));
        inventory.setItem(6,Tools.createRev());


        inventory.setItem(53, createBackItem());



    }



}
