package earthrp.menusystem;

import earthrp.customObjects.*;
import earthrp.tools.maps.CityBoundaryCalculator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/*
Companion class to all menus. This is needed to pass information across the entire
 menu system no matter how many inventories are opened or closed.

 Each player has one of these objects, and only one.
 */
@Getter
@Setter
public class MenuUtility {



    private final Player owner;
    private Town town;
    private Building building;
    private Army army;

    private Army attacker;
    private Army defender;
    private int terrain;
    private Location armyShulkerLoc;
    private UUID armyId;
    private Material shulkerColor;
    private ItemStack leaderHead;
    private ShulkerBox armyShulkerBox;

    private Army deleteArmy;
    private Town deleteTown;
    private Building deleteBuilding;

    private EPlayer player;


    private ItemStack buildingItem;
    private Inventory buildingChest;

    private Set<CityBoundaryCalculator.chunkPoint> townChunks;

    private String ideaType;
    private String ideaColor;
    private String ideaMaterial;
    private String ideaName;

    private Town siegeTown;

    public MenuUtility(Player p) {
        this.owner = p;
    }


}
