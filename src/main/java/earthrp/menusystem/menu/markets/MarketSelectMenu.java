package earthrp.menusystem.menu.markets;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.PaginatedMenu;
import earthrp.menusystem.menu.buildings.MiningBuildingMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class MarketSelectMenu extends PaginatedMenu {
    private final Earth earthPlugin;
    Building building = menuUtility.getBuilding();
    Player p = menuUtility.getOwner();
    Town town = menuUtility.getTown();
    List<Town> towns = town.getTradeTowns();

    public MarketSelectMenu(MenuUtility menuUtility, Earth earthPlugin) {
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
        ItemStack item = e.getCurrentItem();
        if (item != null ) {
            switch (item.getType()){
                case BARRIER -> {
                    new TradeMenu(menuUtility,this.earthPlugin).open();
                }
                case DARK_OAK_BUTTON -> {
                    if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                        if (page == 0){
                            p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                        }else{
                            page = page - 1;
                            super.open();
                        }
                    }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                        if (!((index + 1) >= towns.size())){
                            page = page + 1;
                            super.open();
                        }else{
                            p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                        }
                    }
                }
                case BELL -> {
                    UUID id = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(tradeId,PersistentDataType.STRING));
                    town.setTradeTownId(id);
                    new TradeMenu(menuUtility,this.earthPlugin).open();
                }
            }




            //close inventory


        }else if(e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)){

        } else if (e.getCurrentItem().getType().equals(Material.BELL)) {
            e.getWhoClicked().closeInventory();

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
        ItemStack border = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        inventory.setItem(0,border);
        //The thing you will be looping through to place items
        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;

            if(towns == null || index >= towns.size()) break;
            if (towns.get(index) != null){

                ///////////////////////////
                Town market = towns.get(index);

                //Create an item from our collection and place it into the inventory
                List<String> tLore = List.of(Tools.colorText("&fБонус рынка:" + market.getColorTradeMod()));
                ItemStack item = Tools.createItem(Material.BELL, market.getName(),tLore);
                ItemMeta meta = item.getItemMeta();
                meta.getPersistentDataContainer().set(tradeId, PersistentDataType.STRING,market.getUniqueId().toString());
                item.setItemMeta(meta);
                inventory.addItem(item);



                ////////////////////////
            }
        }



    }
}
