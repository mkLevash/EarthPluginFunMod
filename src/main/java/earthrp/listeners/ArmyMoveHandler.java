package earthrp.listeners;

import earthrp.Earth;
import earthrp.battle.BattleManager;
import earthrp.customEnums.UnitTech;
import earthrp.customObjects.Army;
import earthrp.customObjects.ArmyUnit;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.events.ArmyMoveEvent;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.army.ArmyMenu;
import earthrp.tools.Tools;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;


import static earthrp.tools.PDCKeys.*;
import static earthrp.tools.Tools.getArmyFromInventory;

public class ArmyMoveHandler implements Listener {


//    private final NamespacedKey armyOwnerKey = new NamespacedKey(Earth.getInstance(), "armyOwner");
//    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
//    private final NamespacedKey unitTypeKey = new NamespacedKey(Earth.getInstance(),"unitType");
//    private final NamespacedKey unitLvlKey = new NamespacedKey(Earth.getInstance(),"unitLvl");
//    private final NamespacedKey unitDiscKey = new NamespacedKey(Earth.getInstance(),"unitDisc");
//    private final NamespacedKey unitFireKey = new NamespacedKey(Earth.getInstance(),"unitFire");
//    private final NamespacedKey unitShockKey = new NamespacedKey(Earth.getInstance(),"unitShock");
//    private final NamespacedKey botNameKey = new NamespacedKey(Earth.getInstance(), "botName");
//    private final NamespacedKey leaderFireKey = new NamespacedKey(Earth.getInstance(),"leaderFire");
//    private final NamespacedKey leaderShockKey = new NamespacedKey(Earth.getInstance(),"leaderShock");
//    private final NamespacedKey leaderMoveKey = new NamespacedKey(Earth.getInstance(),"leaderMove");
//    private final NamespacedKey leaderSiegeKey = new NamespacedKey(Earth.getInstance(),"leaderSiege");
    private final Earth earth;
    public ArmyMoveHandler(Earth earth){
        this.earth = earth;
    }

    @EventHandler
    public void onMove(ArmyMoveEvent e){
        Army army = e.getMovedArmy();
        if(army.isSieging()){
            Town town = Earth.getInstance().getDatabase().getTownAtChunk(e.getToChunk());
            if (town == null || !town.getUniqueId().equals(army.getData().getSiegeTown())){
                army.cancelSiege();
            }
        }
        if(army.isBattle()){
            Chunk fromChunk = e.getFromChunk();
            Chunk toChunk = e.getToChunk();
            BattleManager bm = Earth.getInstance().getBattleManager();
            if(army.getBattle() == bm.getBattle(fromChunk) && army.getBattle() != bm.getBattle(toChunk) ){
                Player javaPlayer = Bukkit.getPlayer(army.getOwnerId());
                if(javaPlayer!=null) {
                    javaPlayer.sendActionBar(Tools.deserialize("Вы покидаете поле боя!"));
                }
            }
            if(army.getBattle() != bm.getBattle(fromChunk) && army.getBattle() != bm.getBattle(toChunk)){
                army.getBattle().retreat(army);
            }
        }

    }

}
