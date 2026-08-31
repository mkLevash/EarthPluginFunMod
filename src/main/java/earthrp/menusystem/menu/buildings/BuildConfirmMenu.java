package earthrp.menusystem.menu.buildings;

import earthrp.Earth;
import earthrp.customEnums.BuildingType;
import earthrp.customObjects.Building;
import earthrp.tools.Tools;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Locale;
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
        inventory.clear();
        List<String> lore = List.of(("Вы построите <aqua>" + Tools.serialize(bItem.getItemMeta().displayName()) + "<white> в <light_purple>" + town.getName()));
        ItemStack yes = Tools.createItem(Material.EMERALD,"<green>Да",lore);

        ItemStack no = Tools.createItem(Material.BARRIER,"<red>Нет",null);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }

    private static void build(ItemStack bItem, Inventory chest, Town town){

        ServerDatabase db = Earth.getInstance().getDatabase();
        PersistentDataContainer data = bItem.getItemMeta().getPersistentDataContainer();
        UUID buildingId = UUID.fromString(data.get(buildingIdKey, PersistentDataType.STRING));
        String type = data.get(buildingTypeKey,PersistentDataType.STRING);
        String displayName = bItem.getItemMeta().getDisplayName();
        Location loc = chest.getLocation();
        Building building = new Building(
                buildingId,
                town.getUniqueId(),
                loc,
                ""

        );
        building.getData().setType(BuildingType.fromString(type));
        switch (building.getData().getType()){
            case PASTURE -> {
                building.getData().pastureArea = Building.countEnclosedArea(building.getLocation(),1000);
            }

            case LUMBER -> {
                String biome = loc.getBlock().getBiome().translationKey().toLowerCase(Locale.ROOT);
                double lumberEfficiency = 0.1;
                List<String> forestBiomes = List.of("taiga","forest","jungle","swamp");
                if(forestBiomes.stream().anyMatch(biome::contains)) lumberEfficiency = 0.75;

                building.getData().setLumberEfficiency(lumberEfficiency);
            }

            case MINE,PIT,QUARRY -> {
                String biome = loc.getBlock().getBiome().translationKey().toLowerCase(Locale.ROOT);
                double mineEfficiency = 0;
                List<String> forestBiomes = List.of("peaks","hills");
                if(forestBiomes.stream().anyMatch(biome::contains)) {
                    switch (building.getData().getType()){
                        case QUARRY -> {
                            mineEfficiency = 0.75;
                        }
                        case PIT -> {
                            mineEfficiency = 0.25;
                        }
                        case MINE -> {
                            mineEfficiency = 0.15;
                        }
                    }

                }


                building.getData().setMineEfficiency(mineEfficiency);
            }
        }
        db.addBuilding(building);

        Tools.spawnHologramLegacy(loc.clone(),String.valueOf(buildingId),"buildingId", false);

        Tools.spawnHologramLegacy(loc.clone().add(0.5, 1, 0.5),displayName,"buildingName" , true);

        chest.addItem(bItem);
        bItem.setAmount(0);

    }
}
