package earthrp.menusystem;

import earthrp.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.List;

import static earthrp.tools.PDCKeys.menuIdKey;

/*
    Defines the behavior and attributes of all menus in our plugin
 */
public abstract class Menu implements InventoryHolder {

    //Protected values that can be accessed in the menus
    protected MenuUtility menuUtility;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");

    //Constructor for Menu. Pass in a PlayerMenuUtility so that
    // we have information on who's menu this is and
    // what info is to be transfered
    public Menu(MenuUtility menuUtility) {
        this.menuUtility = menuUtility;
    }

    //let each menu decide their name
    public abstract String getMenuName();

    //let each menu decide their slot amount
    public abstract int getSlots();

    //let each menu decide how the items in the menu will be handled when clicked
    public abstract void handleMenu(InventoryClickEvent e);



    //let each menu decide what items are to be placed in the inventory menu
    public abstract void setMenuItems();

    //When called, an inventory is created and opened for the player
    public void open(){
        //The owner of the inventory created is the Menu itself,
        // so we are able to reverse engineer the Menu object from the
        // inventoryHolder in the MenuListener class when handling clicks
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        //grab all the items specified to be used for this menu and add to inventory
        this.setMenuItems();

        //open the inventory for the player
        menuUtility.getOwner().openInventory(inventory);
    }

    //Overridden method from the InventoryHolder interface
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    //Helpful utility method to fill all remaining slots with "filler glass"
    public void setFillerGlass(){
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }



    public static ItemStack makeItem(String displayName, String menuId, String customModel, String... lore){
        ItemStack item = Tools.createItem(Material.EGG,displayName,Arrays.asList(lore),customModel);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(menuIdKey, PersistentDataType.STRING, menuId);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack makeItem(Material material, String displayName, String menuId, String... lore){
        ItemStack item = Tools.createItem(material,displayName,Arrays.asList(lore),null);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(menuIdKey, PersistentDataType.STRING, menuId);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack makeItem(Material material, String displayName, String... lore) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(displayName);

        itemMeta.setLore(Arrays.asList(lore));
        item.setItemMeta(itemMeta);
        return item;
    }

    public void fillIfEmpty(int slot) {
        if (inventory.getItem(slot) == null) inventory.setItem(slot, FILLER_GLASS);
    }

    public static ItemStack createBackItem(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Назад");
        close.setItemMeta(closeMeta);
        return close;
    }

}
