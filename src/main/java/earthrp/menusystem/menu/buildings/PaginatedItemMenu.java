package earthrp.menusystem.menu.buildings;

import earthrp.customEnums.EarthItem;
import earthrp.customObjects.Building;
import earthrp.menusystem.PaginatedMenu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.buildings.inGame.MiningBuildingMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import earthrp.Items;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;


import java.util.ArrayList;

import static earthrp.tools.PDCKeys.earthItemKey;

public class PaginatedItemMenu extends PaginatedMenu {
    Building building = menuUtility.getBuilding();
    Player p = menuUtility.getOwner();
    ArrayList<ItemStack> items;

    public PaginatedItemMenu(MenuUtility menuUtility) {
        super(menuUtility);
        switch (building.getData().getType()){
            case LUMBER ->{
                this.items = Items.getWoods();
            }
            case WORKSHOP,MANUFACTURE -> {
                this.items = Items.getWorkShopItems();
            }
            case MINE -> {
                this.items = Items.getMineV1Items();
            }
            case PIT -> {
                this.items = Items.getMineV2Items();
            }
            case QUARRY -> {
                this.items = Items.getCareerItems();
            }
            case FARM -> {
                this.items = Items.getFarmItems();
            }
            case PASTURE -> {
                this.items = Items.getPastureItems();
            }
            case BANK -> {
                this.items = Items.getForgeItems();
            }
            case GUN_FACTORY -> {
                this.items = Items.getGunFactoryItems();
            }
            case FISHER -> {
                items = Items.getFisherItems();
            }
            default -> {
                this.items = new ArrayList<>();
            }
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
            new MiningBuildingMenu(menuUtility).open();

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
            building.getData().setItem(null);
            building.getData().setDefaultItem(null);
            building.getData().setStatus(false);
            
            menuUtility.setBuilding(building);
            new MiningBuildingMenu(menuUtility).open();

        } else if(!e.getCurrentItem().getType().equals(Material.GRAY_STAINED_GLASS_PANE) && !e.getCurrentItem().getType().equals(Material.WHITE_STAINED_GLASS_PANE)){

            PersistentDataContainer data = e.getCurrentItem().getItemMeta().getPersistentDataContainer();
            EarthItem item = EarthItem.fromString(data.get(earthItemKey, PersistentDataType.STRING)) ;
            if(item!=null){
                building.getData().setItem(item);
                building.getData().setDefaultItem(null);
                building.getData().setStatus(false);
            }else{
                building.getData().setItem(null);
                building.getData().setDefaultItem(e.getCurrentItem().getType());
                building.getData().setStatus(false);
            }

            
            menuUtility.setBuilding(building);
            new MiningBuildingMenu(menuUtility).open();
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        addMenuBorder();

        ItemStack off = new ItemStack(Material.REDSTONE_TORCH);
        ItemMeta offMeta = off.getItemMeta();
        offMeta.setDisplayName(ChatColor.RED + "Выключить строение");
        off.setItemMeta(offMeta);

        inventory.setItem(0,off);





        setItems(items);



    }
}
