package earthrp.listeners;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class VillagerSpawnHandler implements Listener {
    private final Earth earth;
    ServerDatabase db;
    public VillagerSpawnHandler(Earth earth) {
        this.earth = earth;
        db = earth.getDatabase();
    }
    @EventHandler
    public void onVillagerSpawn(EntitySpawnEvent e)  {
        Entity entity = e.getEntity();
        if (entity instanceof Villager villager && villager.getCustomName() != null){
            //Villager villager = (Villager) e.getEntity();
            String name = villager.getCustomName();
            if (name.startsWith("Крестьянин")){
                e.getEntity().remove();
                //UUID id = db.getPlayerUuid(); // (name.substring(8));
                EPlayer p = db.getPlayer(name.substring(11));
                if (p==null) return;
                int cost = (int) Math.ceil(5 * (p.getLivingBuildingCost()));
                if(p.getAttribute(EPlayerAttribute.TREASURY)<cost) return;
                p.addAttribute(EPlayerAttribute.TREASURY,-cost);
                Location loc = e.getLocation();
                Villager customVillager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
                customVillager.setCustomName(p.getDisplayName());
                customVillager.setCustomNameVisible(true);
                customVillager.setAI(false);
                customVillager.setProfession(villager.getProfession());
                customVillager.setVillagerType(villager.getVillagerType());




                var container = customVillager.getPersistentDataContainer();

                container.set(villagerCostKey, PersistentDataType.INTEGER,cost);
                container.set(villagerTypeKey, PersistentDataType.STRING,"peasant");

                Town closestTown = db.getTownAtChunk(loc);

                if(closestTown != null){
                    container.set(villagerTownKey,PersistentDataType.STRING,closestTown.getUniqueId().toString());
                    closestTown.getData().houses += 1;
                    //int tax = closestTown.getHouses();
                    //closestTown.setHouses(tax+1);
                }else{
                    p.addAttribute(EPlayerAttribute.TAX_INCOME,1);

                }


            } else if (name.startsWith("Дворянин")) {

                //UUID id = db.getPlayerUuid(); // (name.substring(8));
                EPlayer p = db.getPlayer(name.substring(9));
                if (p==null) return;
                int cost = (int) Math.ceil(15 * (p.getLivingBuildingCost()));
                Location loc = e.getLocation();
                Town closestTown = db.getTownAtChunk(loc);

                if(closestTown!=null && p.getAttribute(EPlayerAttribute.TREASURY)>=cost){
                    if(closestTown.getNobleSites() <= closestTown.getData().noble) return;
                    p.addAttribute(EPlayerAttribute.TREASURY,-cost);

                    Villager customVillager = (Villager) loc.getWorld().spawnEntity(loc, EntityType.VILLAGER);
                    customVillager.setCustomName(p.getDisplayName());
                    customVillager.setCustomNameVisible(true);
                    customVillager.setAI(false);
                    customVillager.setProfession(villager.getProfession());
                    customVillager.setVillagerType(villager.getVillagerType());

                    var container = customVillager.getPersistentDataContainer();
                    container.set(villagerCostKey, PersistentDataType.INTEGER,cost);
                    container.set(villagerTypeKey, PersistentDataType.STRING,"noble");
                    container.set(villagerTownKey,PersistentDataType.STRING,closestTown.getUniqueId().toString());



                    closestTown.getData().noble += 1;

                }

            }
        }
    }
    @EventHandler
    public void onVillagerKilled(EntityDeathEvent e)  {
        Entity villager = e.getEntity();
        if (villager.getCustomName() != null && villager.getType().equals(EntityType.VILLAGER)){
            String name = villager.getCustomName();
            EPlayer p = db.getPlayer(name);
            if(p==null) return;
            Location loc = villager.getLocation();
            var container = villager.getPersistentDataContainer();

            Town closestTown = db.getTownAtChunk(loc);

            if(closestTown != null && container.has(villagerTypeKey)){
                String type = container.get(villagerTypeKey,PersistentDataType.STRING);
                int amount = Math.max(1,container.get(villagerCostKey,PersistentDataType.INTEGER) - 2);
                ItemStack mora = Tools.createMora(amount);
                loc.getWorld().dropItemNaturally(loc, mora);
                if(type.equals("peasant")){
                    closestTown.getData().houses -= 1;
                } else if (type.equals("noble")) {
                    closestTown.getData().noble -= 1;
                }

                //int tax = closestTown.getHouses();
                //closestTown.setHouses(tax-1);
            }else if(p.getAttribute(EPlayerAttribute.TAX_INCOME)>0 && !container.has(villagerTownKey)){
                p.addAttribute(EPlayerAttribute.TAX_INCOME,-1);
            }
        }

    }
}
