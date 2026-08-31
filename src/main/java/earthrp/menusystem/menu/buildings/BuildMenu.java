package earthrp.menusystem.menu.buildings;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class BuildMenu extends PaginatedMenu {


    public BuildMenu(MenuUtility menuUtility) {
        super(menuUtility);
        db = Earth.getInstance().getDatabase();
        player = db.getPlayer(p.getUniqueId());
    }
    private final ServerDatabase db;
    private final ItemStack bItem = menuUtility.getBuildingItem();
    List<ItemStack> tItems = new ArrayList<>();
    Player p = menuUtility.getOwner();
    EPlayer player;


    @Override
    public String getMenuName() {
        return "Выберете город в котором строите здание";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e){
        ItemStack item = e.getCurrentItem();
        if(item != null){
            switch (item.getType()){
                case BARRIER -> {
                    e.getWhoClicked().closeInventory();
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
                        if (!((index + 1) >= tItems.size())){
                            page = page + 1;
                            super.open();
                        }else{
                            p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                        }
                    }
                }
                case END_CRYSTAL -> {
                    UUID townId = UUID.fromString(Objects.requireNonNull(Objects.requireNonNull(item.getItemMeta()).getPersistentDataContainer().get(townIdKey, PersistentDataType.STRING)));
                    Town town = db.getTown(townId);
                    String type = bItem.getItemMeta().getPersistentDataContainer().get(buildingTypeKey,PersistentDataType.STRING);
                    if(town.canNewBuild(type) ){
                        e.getWhoClicked().closeInventory();
                        menuUtility.setTown(town);
                        new BuildConfirmMenu(menuUtility).open();
                    }
                }
            }

        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        addMenuBorder();
        List<Town> towns;
        if(bItem.getItemMeta().hasLore() && bItem.getItemMeta().getLore().get(0).contains("debug")){
            towns = new ArrayList<>(db.getTowns());
        }else{
            towns = new ArrayList<>(player.getTowns());
        }
        towns.sort(Comparator.comparingDouble(town ->
                town.getLocation().distanceSquared(p.getLocation())
        ));
        for (Town t: towns){
            tItems.add(Tools.createTownItem(t));
        }
        setItems(tItems);


    }
}


