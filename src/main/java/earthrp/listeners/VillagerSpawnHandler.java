package earthrp.listeners;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

import java.util.*;

public class VillagerSpawnHandler implements Listener {
    private final Earth earth;
    ServerDatabase db;
    public VillagerSpawnHandler(Earth earth) {
        this.earth = earth;
        db = earth.getServerDatabase();
    }
    @EventHandler
    public void onVillagerSpawn(EntitySpawnEvent e)  {
        Entity entity = e.getEntity();
        if (entity.getCustomName() != null && entity.getType().equals(EntityType.VILLAGER)){
            Villager villager = (Villager) e.getEntity();
            String name = villager.getCustomName();
            if (name.startsWith("Villager")){
                UUID id = db.getPlayerUuid(name.substring(9)); // (name.substring(8));
                EPlayer p = db.getPlayer(id);
                e.setCancelled(true);
                Location loc = e.getLocation();
                Villager customVillager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
                customVillager.setCustomName(name.substring(9));
                customVillager.setCustomNameVisible(true);
                customVillager.setAI(false);
                customVillager.setProfession(villager.getProfession());
                customVillager.setVillagerType(villager.getVillagerType());
                int x = loc.getChunk().getX();
                int z = loc.getChunk().getZ();


                Set<Town> towns = p.getTowns();
                Town closestTown = towns.stream()
                        .min(Comparator.comparingDouble(t ->
                                Tools.getDistanceSqrd(t.getX(), t.getZ(), x, z)
                        ))
                        .orElse(null);
                if(closestTown != null){
                    int tax = closestTown.getHouses();
                    closestTown.setHouses(tax+1);
                }else{
                    p.addAttribute(EPlayerAttribute.TAX_INCOME,1);
                }

            }
        }
    }
    @EventHandler
    public void onVillagerKilled(EntityDeathEvent e)  {
        Entity villager = e.getEntity();
        if (villager.getCustomName() != null && villager.getType().equals(EntityType.VILLAGER)){
            String name = villager.getCustomName();
            UUID id = db.getPlayerUuid(name);
            EPlayer p = db.getPlayer(id);
            Location loc = villager.getLocation();
            int x = loc.getChunk().getX();
            int z = loc.getChunk().getZ();


            Set<Town> towns = p.getTowns();
            Town closestTown = towns.stream()
                    .min(Comparator.comparingDouble(t ->
                            Tools.getDistanceSqrd(t.getX(), t.getZ(), x, z)
                    ))
                    .orElse(null);
            if(closestTown != null){
                int tax = closestTown.getHouses();
                closestTown.setHouses(tax-1);
            }else{
                p.addAttribute(EPlayerAttribute.TAX_INCOME,-1);
            }
        }

    }
}
