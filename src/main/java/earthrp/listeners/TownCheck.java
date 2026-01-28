package earthrp.listeners;

import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.events.TownCheckEvent;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class TownCheck implements Listener {

    private final Earth earthPlugin;
    private final ServerDatabase db;

    public TownCheck( Earth moraPlugin) {
        this.earthPlugin = moraPlugin;
        db = Earth.getInstance().getServerDatabase();
    }

    @EventHandler
    public void townCheck(TownCheckEvent e) {
        Set<EPlayer> players = db.getPlayers();
        for (EPlayer p:players){

            for(Building b:p.getBuildings()){
                if(b.getItem()!=null){
                    switch (b.getType()){
                        case "mineV1","lumber","mineV2" -> spawnItem(b,1);
                        case "factory" -> spawnItem(b,3);
                        case "career", "plant" -> spawnItem(b,2);
                    }
                }
            }
        }


    }

    private void spawnItem(Building building, int amount){
        Location loc = building.getLocation();
        for (BlockState blockState : loc.getChunk().getTileEntities()) {
            if (building.getItem() != null && blockState instanceof Chest chest && loc.equals(chest.getLocation())) {
                chest.getBlockInventory().addItem(new ItemStack(building.getItem(),amount));
            }
        }
    }
}
