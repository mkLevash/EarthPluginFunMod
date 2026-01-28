package earthrp.menusystem.menu.markets;

import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.menusystem.PaginatedMenu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;

public class PaginatedMarketSelectMenu extends PaginatedMenu {
    private final Earth earthPlugin;
    Building building = menuUtility.getBuilding();
    Player p = menuUtility.getOwner();
    ArrayList<ItemStack> items;

    public PaginatedMarketSelectMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
    }

    @Override
    public String getMenuName() {
        return "Выбор рынка. Страница "+page;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
            //close inventory
            p.closeInventory();

        }else if(e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)){
            if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                if (page == 0){
                    p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                }else{
                    page = page - 1;
                    super.open();
                }
            }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                if (!((index + 1) >= items.size())){
                    page = page + 1;
                    super.open();
                }else{
                    p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                }
            }
        } else if (e.getCurrentItem().getType().equals(Material.SOUL_TORCH)) {
            building.setMarketId(null);
            
            p.closeInventory();

        } else if(!e.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE)){
            building.setItem(e.getCurrentItem().getType());
            
            p.closeInventory();
        }


    }

    @Override
    public void setMenuItems() {
        addMenuBorder();


        //The thing you will be looping through to place items
        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;
            if(index >= items.size()) break;
            if (items.get(index) != null){
                ///////////////////////////

                //Create an item from our collection and place it into the inventory

                ItemStack item = items.get(index);
                inventory.addItem(item);



                ////////////////////////
            }
        }



    }
}
