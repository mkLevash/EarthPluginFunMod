package earthrp.menusystem.menu.buildings;

import earthrp.Earth;
import earthrp.customObjects.Building;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.buildingIdKey;
import static earthrp.tools.PDCKeys.buildingTypeKey;

public class BuildConfirmMenu extends Menu {
    public BuildConfirmMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }
    private final ItemStack bItem = menuUtility.getBuildingItem();
    Town town = menuUtility.getTown();
    Inventory chest = menuUtility.getBuildingChest();

    @Override
    public String getMenuName() {
        return "";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getCurrentItem() != null){
            switch (e.getCurrentItem().getType()){
                case EMERALD ->{
                    e.getWhoClicked().closeInventory();
                    build(bItem,chest,town);
                }

                case BARRIER -> {
                    e.getWhoClicked().closeInventory();
                    new BuildMenu(menuUtility).open();
                }
            }
        }


    }

    @Override
    public void setMenuItems() {
        List<String> lore = List.of(Tools.colorText("&fВы построите &3" + bItem.getItemMeta().getDisplayName() + " &fв &d" + town.getName()));
        ItemStack yes = Tools.createItem(Material.EMERALD,ChatColor.GREEN + "Да",lore);

        ItemStack no = Tools.createItem(Material.BARRIER,ChatColor.RED + "Нет",null);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }

    private static void build(ItemStack bItem, Inventory chest, Town town){

        ServerDatabase db = Earth.getInstance().getServerDatabase();
        PersistentDataContainer data = bItem.getItemMeta().getPersistentDataContainer();
        UUID buildingId = UUID.fromString(data.get(buildingIdKey, PersistentDataType.STRING));
        String type = data.get(buildingTypeKey,PersistentDataType.STRING);
        String displayName = bItem.getItemMeta().getDisplayName();
        Location loc = chest.getLocation();
        Building building = new Building(
                buildingId,
                town.getUniqueId(),
                town.getName(),
                town.getMarketId(),
                type,
                1,
                null,
                loc

        );
        db.addBuilding(building);

        Tools.spawnHologram(loc.clone(),String.valueOf(buildingId),"buildingId", false);

        Tools.spawnHologram(loc.clone().add(0.5, 1, 0.5),displayName,"buildingName" , true);

        chest.addItem(bItem);
        bItem.setAmount(0);

    }
}
