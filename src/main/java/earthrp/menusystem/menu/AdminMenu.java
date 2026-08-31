package earthrp.menusystem.menu;

import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.countryMenu.ArmyMenu;
import earthrp.menusystem.menu.countryMenu.CountryMenu;
import earthrp.tools.Tools;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Set;

import static earthrp.tools.PDCKeys.menuIdKey;

public class AdminMenu extends Menu {
    public AdminMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    EPlayer player = menuUtility.getPlayer();

    @Override
    public String getMenuName() {
        return "Главное меню";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item != null){

            switch (item.getType()){
                case IRON_SWORD -> {
                    e.getWhoClicked().closeInventory();
                    new AdminTroopsMenu(menuUtility).open();

                }
                case BARREL -> {
                    e.getWhoClicked().closeInventory();
                    new AdminBuildingsMenu(menuUtility).open();
                }
                case IRON_INGOT -> {
                    e.getWhoClicked().closeInventory();
                    new AdminItemsMenu(menuUtility).open();
                }

            }
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();


        inventory.setItem(23, makeItem(Material.BARREL,"Здания"));
        inventory.setItem(24, makeItem(Material.IRON_INGOT,"Предметы"));
        inventory.setItem(25, makeItem(Material.IRON_SWORD,"Войска"));





    }
}
