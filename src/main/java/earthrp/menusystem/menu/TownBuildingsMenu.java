package earthrp.menusystem.menu;

import earthrp.customEnums.EarthItem;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.PaginatedMenu;
import earthrp.menusystem.menu.buildings.inGame.*;
import earthrp.tools.Tools;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static earthrp.tools.PDCKeys.*;

public class TownBuildingsMenu extends PaginatedMenu {
    public TownBuildingsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    private Town town = menuUtility.getTown();
    private List<Building> buildings = town.getBuildings().stream().toList();
    @Override
    public String getMenuName() {
        return "";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if(item != null && item.hasItemMeta()){
            Player p = (Player) e.getWhoClicked();
            if(item.getType().equals(Material.BARRIER)){
                p.closeInventory();
                new TownsMenu(menuUtility).open();
            }
            if(item.getItemMeta().getPersistentDataContainer().has(menuIdKey)){
                String index = item.getPersistentDataContainer().get(menuIdKey, PersistentDataType.STRING);
                if(index != null){
                    int value;
                    try {
                        value = Integer.parseInt(index);
                        // Ваша логика с полученным числом
                    } catch (NumberFormatException ex) {
                        // Предмет не содержит число (например, это фоновое стекло или кнопка)
                        return;
                    }
                    var b = buildings.get(value);
                    menuUtility.setBuilding(b);
                    menuUtility.setBuildingItem(item);
                    switch (b.getData().getType()) {
                        case MINE,PIT,QUARRY,LUMBER,WORKSHOP,PASTURE,FARM,FORGE,MANUFACTURE,FISHER ->{
                            new MiningBuildingMenu(menuUtility).open();
                        }
                        case LIBRARY,UNIVERSITY ->{
                            new ScienceBuildingMenu(menuUtility).open();
                        }
                        case MARKETPLACE,PORT ->{
                            new MarketplaceMenu(menuUtility).open();
                        }
                        case SHIPYARD -> {
                            new ShipyardMenu(menuUtility).open();

                        }
                        case BARRACK -> {
                            new BarrackMenu(menuUtility).open();
                        }
                        case STABLE -> {
                            new StableMenu(menuUtility).open();
                        }
                        case GUN_FACTORY -> {
                            new GunFactoryMenu(menuUtility).open();
                        }
                        default ->{
                            new DefaultBuildingMenu(menuUtility).open();
                        }
                    }
                }
                
            }

            


        }



    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        addMenuBorder();
        
        List<ItemStack> items = new ArrayList<>();
        
        for(int i = 0; i < buildings.size(); i++){
            
            var b = buildings.get(i);

            List<String> lore = new ArrayList<>();
            if(b.getData().getItem()!=null){
                lore = b.getItemLore();
                lore.addFirst(b.getData().getItem().getDisplayName());
            }
            var type = b.getData().getType();
            items.add(makeItem(type.getMaterial(),type.getDisplayName(), String.valueOf(i),"",lore));
        }
        setItems(items);


    }
}
