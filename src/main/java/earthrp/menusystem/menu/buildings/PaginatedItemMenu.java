package earthrp.menusystem.menu.buildings;

import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.menusystem.PaginatedMenu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import earthrp.Items;
import org.bukkit.inventory.meta.ItemMeta;


import java.sql.SQLException;
import java.util.ArrayList;

public class PaginatedItemMenu extends PaginatedMenu {
    private final Earth earthPlugin;
    Building building = menuUtility.getBuilding();
    Player p = menuUtility.getOwner();
    ArrayList<ItemStack> items;

    public PaginatedItemMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        if (building.getType().equals("lumber")){
            this.items = Items.getWoods();
        }
        if (building.getType().equals("plant")||building.getType().equals("factory")) {
            this.items = Items.getPlantItems();
        }
        if(building.getType().equals("mineV1")){
            this.items = Items.getMineV1Items();
        }
        if(building.getType().equals("mineV2")){
            this.items = Items.getMineV2Items();
        }
        if(building.getType().equals("career")){
            this.items = Items.getCareerItems();
        }
        if(building.getType().equals("pasture")){
            this.items = Items.getPastureItems();
        }
        if(building.getType().equals("farm")){
            this.items = Items.getFarmItems();
        }
        if(building.getType().equals("forge")){
            this.items = Items.getForgeItems();
        }
    }

    @Override
    public String getMenuName() {
        return "Выбор ресурса. Страница "+page;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        if (e.getCurrentItem().getType().equals(Material.BARRIER)) {
            //close inventory
            new MiningBuildingMenu(menuUtility,this.earthPlugin).open();

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
        } else if (e.getCurrentItem().getType().equals(Material.REDSTONE_TORCH)) {
            building.setItem(null);
            
            menuUtility.setBuilding(building);
            new MiningBuildingMenu(menuUtility,this.earthPlugin).open();

        } else if(!e.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE)){
            System.out.println("[Earth]check0");
            building.setItem(e.getCurrentItem().getType());
            
            menuUtility.setBuilding(building);
            new MiningBuildingMenu(menuUtility,this.earthPlugin).open();
        }


    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        ItemStack border = super.FILLER_GLASS;

        ItemStack off = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta offMeta = off.getItemMeta();
        offMeta.setDisplayName(ChatColor.RED + "Выключить строение");
        off.setItemMeta(offMeta);

        inventory.setItem(0,off);

        if(building.getType().equals("lumber")){
            inventory.setItem(16,border);
            inventory.setItem(25,border);
            inventory.setItem(34,border);
            inventory.setItem(43,border);
        }
        if ((building.getType().equals("plant")||building.getType().equals("factory"))&&page==0) {
            inventory.setItem(13,border);
            inventory.setItem(22,border);
            inventory.setItem(31,border);
            inventory.setItem(40,border);
            inventory.setItem(39,border);
            inventory.setItem(38,border);
            inventory.setItem(41,border);

        }
        if(building.getType().equals("mineV1")){
            inventory.setItem(14,border);
            inventory.setItem(15,border);
            inventory.setItem(16,border);
        }
        if(building.getType().equals("mineV2")){
            inventory.setItem(15,border);
            inventory.setItem(16,border);
            inventory.setItem(19,border);
            inventory.setItem(20,border);
            inventory.setItem(21,border);
            inventory.setItem(22,border);
            inventory.setItem(23,border);
            inventory.setItem(24,border);
            inventory.setItem(25,border);
        }
        if(building.getType().equals("career")){
            inventory.setItem(19,border);
            inventory.setItem(20,border);
            inventory.setItem(21,border);
            inventory.setItem(22,border);
            inventory.setItem(23,border);
            inventory.setItem(24,border);
            inventory.setItem(25,border);
        }

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
