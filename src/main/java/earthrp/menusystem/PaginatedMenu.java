package earthrp.menusystem;

import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

/*

A class extending the functionality of the regular Menu, but making it Paginated

This pagination system was made from Jer's code sample. <3

 */

public abstract class PaginatedMenu extends Menu {

    //Keep track of what page the menu is on
    protected int page = 0;
    //28 is max items because with the border set below,
    //28 empty slots are remaining.
    @Getter
    protected int maxItemsPerPage = 28;
    //the index represents the index of the slot
    //that the loop is on
    protected int index = 0;

    public PaginatedMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    //Set the border and menu buttons for the menu
    public void addMenuBorder(){
        inventory.setItem(48, makeItem(Material.DARK_OAK_BUTTON, "<green>Left"));

        inventory.setItem(49, makeItem(Material.BARRIER, "<red>Close"));

        inventory.setItem(50, makeItem(Material.DARK_OAK_BUTTON, "<green>Right"));

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
            if (inventory.getItem(i+44) == null) {
                inventory.setItem(i+44, super.FILLER_GLASS);
            }
        }

        inventory.setItem(17, super.FILLER_GLASS);
        inventory.setItem(18, super.FILLER_GLASS);
        inventory.setItem(26, super.FILLER_GLASS);
        inventory.setItem(27, super.FILLER_GLASS);
        inventory.setItem(35, super.FILLER_GLASS);
        inventory.setItem(36, super.FILLER_GLASS);


    }

    public void setItems(List<ItemStack> items){


        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;
            if(index >= items.size()) break;
            if (items.get(index) != null){

                ItemStack item = items.get(index);
                inventory.addItem(item);

            }
        }
    }
}
