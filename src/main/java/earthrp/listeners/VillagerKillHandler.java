//package earthrp.listeners;
//
//import earthrp.Earth;
//import org.bukkit.entity.Entity;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.Listener;
//import org.bukkit.event.entity.EntityDeathEvent;
//
//import java.sql.SQLException;
//import java.util.UUID;
//
//public class VillagerKillHandler implements Listener {
//    private final Earth plugin;
//
//    public VillagerKillHandler(Earth plugin) {
//        this.plugin = plugin;
//    }
//    @EventHandler
//    public void onVillagerKilled(EntityDeathEvent event)  {
//        Entity villager = event.getEntity();
//        if (villager.getCustomName() != null){
//            String name = villager.getCustomName();
//            UUID id = this.plugin.getServerDatabase().getPlayerUuid(name);
//            int tax = this.plugin.getServerDatabase().getPlayerTaxIncome(id);
//            this.plugin.getServerDatabase().updatePlayerTaxIncome(id,tax-1);
//            int army_limit = this.plugin.getServerDatabase().getPlayerArmyLimit(id);
//            this.plugin.getServerDatabase().updatePlayerArmyLimit(id,army_limit-1);
//        }
//
//    }
//}
