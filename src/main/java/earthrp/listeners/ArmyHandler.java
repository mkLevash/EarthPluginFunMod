package earthrp.listeners;

import earthrp.tools.Tools;
import earthrp.customObjects.Army;
import earthrp.Earth;
import earthrp.customObjects.Unit;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.army.ArmyMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class ArmyHandler implements Listener {


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
    public ArmyHandler(Earth earth){
        this.earth = earth;
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        try{
            if(!validUnitTransfer(e)) return;

            if(!armyExists(e)){
                handleNewArmy(e);
            }else{
                handleExistingArmy(e);
            }



        } catch (Exception ex) {
            Bukkit.getLogger().severe("Ошибка при обработке клика: " + ex.getMessage());
            e.setCancelled(true);
        }

    }



    private boolean validUnitTransfer(InventoryClickEvent e) {
        if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY && (e.getInventory().getType() == InventoryType.SHULKER_BOX || armyExists(e))){
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()){
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                NamespacedKey key = new NamespacedKey(Earth.getInstance(), "unitType");

                return (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING) || meta.getPersistentDataContainer().has(leaderFireKey, PersistentDataType.INTEGER));
            }
        }
        return false;
    }

    private boolean armyExists(InventoryClickEvent e){
        for(ItemStack item: e.getInventory().getContents()){
            if(item != null && item.hasItemMeta()){
                ItemMeta meta = item.getItemMeta();
                return meta.getPersistentDataContainer().has(armyIdKey, PersistentDataType.STRING);
            }
        }
        return false;
    }

    private void handleNewArmy(InventoryClickEvent e){
        ServerDatabase db = this.earth.getServerDatabase();
        UUID ownerId = e.getWhoClicked().getUniqueId();
        UUID armyId = UUID.randomUUID();

        e.getInventory().clear();

        ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta ownerMeta = (SkullMeta) owner.getItemMeta();
        ownerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerId));
        ownerMeta.getPersistentDataContainer().set(armyOwnerKey, PersistentDataType.STRING, ownerId.toString());
        ownerMeta.getPersistentDataContainer().set(armyIdKey, PersistentDataType.STRING, armyId.toString());
        ownerMeta.setDisplayName(e.getWhoClicked().getName());
        ownerMeta.setLore(Collections.singletonList(ChatColor.RED + "ВЫ СОЗДАЛИ АРМИЮ, ОТКРОЙТЕ ЗАНОВОЙ ШАЛКЕР ЧТОБЫ ПРОДОЛЖИТЬ"));
        owner.setItemMeta(ownerMeta);






        Army army = new Army(armyId,ownerId);

        ItemStack item = e.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String type = data.get(unitTypeKey,PersistentDataType.STRING);
        int lvl = data.get(unitLvlKey,PersistentDataType.INTEGER);
        double disc = data.get(unitDiscKey,PersistentDataType.DOUBLE);
        double fire = data.get(unitFireKey,PersistentDataType.DOUBLE);
        double shock = data.get(unitShockKey,PersistentDataType.DOUBLE);

        for (int i = 0; i < item.getAmount(); i++) {
            Unit unit = new Unit(UUID.randomUUID());
            unit.setArmyId(armyId);
            unit.setType(type);
            unit.setLvl(lvl);
            unit.setHp(1000);
            unit.setMorale(Tools.round(unit.getBaseMorale() * db.getPlayer(ownerId).getMorale()));
            if(data.has(unitMoraleKey)){
                unit.setMorale(data.get(unitMoraleKey,PersistentDataType.DOUBLE));
            }
            unit.setDisc(disc);
            unit.setFire(fire);
            unit.setShock(shock);
            db.addUnit(unit);
            army.addUnit(unit);
        }
        army.setTechLvl(lvl);
        db.addArmy(army);

        e.getInventory().addItem(owner);




    }

    private void handleExistingArmy(InventoryClickEvent e){
        ServerDatabase db = this.earth.getServerDatabase();
        UUID armyId = null;
        int leaderFire = -1;
        for(ItemStack item: e.getInventory().getContents()){
            if(item != null && item.hasItemMeta()){
                ItemMeta meta = item.getItemMeta();
                if(meta.getPersistentDataContainer().has(armyIdKey, PersistentDataType.STRING)){
                    armyId = UUID.fromString(meta.getPersistentDataContainer().get(armyIdKey, PersistentDataType.STRING));
                }

            }
        }


        if(armyId!=null){
            Army army = db.getArmy(armyId);
            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(unitTypeKey,PersistentDataType.STRING)){
                String type = data.get(unitTypeKey,PersistentDataType.STRING);
                int lvl = data.get(unitLvlKey,PersistentDataType.INTEGER);
                double disc = data.get(unitDiscKey,PersistentDataType.DOUBLE);
                double fire = data.get(unitFireKey,PersistentDataType.DOUBLE);
                double shock = data.get(unitShockKey,PersistentDataType.DOUBLE);
                for (int i = 0; i < item.getAmount(); i++) {
                    Unit unit = new Unit(UUID.randomUUID());
                    unit.setArmyId(armyId);
                    unit.setType(type);
                    unit.setLvl(lvl);
                    unit.setHp(1000);
                    unit.setMorale(unit.getMaxMorale());
                    if(data.has(unitMoraleKey)){
                        unit.setMorale(data.get(unitMoraleKey,PersistentDataType.DOUBLE));
                    }
                    unit.setDisc(disc);
                    unit.setFire(fire);
                    unit.setShock(shock);
                    db.addUnit(unit);
                    army.addUnit(unit);

                }
                if(lvl>=army.getTechLvl()){
                    army.setTechLvl(lvl);
                }
                for (int i = 0; i < e.getWhoClicked().getInventory().getSize(); i++) {
                    ItemStack itemStack = e.getWhoClicked().getInventory().getItem(i);
                    if(itemStack!=null && itemStack.equals(e.getCurrentItem())){
                        e.getWhoClicked().getInventory().setItem(i,new ItemStack(Material.AIR));
                    }
                }
            } else if (data.has(leaderFireKey)) {
                updateLeader(army,data);
                army.setLeaderName(meta.getDisplayName());
            }else{
                e.setCancelled(true);
            }


            MenuUtility mu = new MenuUtility((Player) e.getWhoClicked());
            army = db.getArmy(army.getUuid());
            mu.setArmy(army);
            e.getWhoClicked().closeInventory();
            new ArmyMenu(mu).open();


        }
    }

    private void updateLeader(Army army, PersistentDataContainer data){
        int fire = data.get(leaderFireKey,PersistentDataType.INTEGER);
        int shock = data.get(leaderShockKey,PersistentDataType.INTEGER);

        army.setLeaderFire(fire);
        army.setLeaderShock(shock);
    }

    private void getLeader(ShulkerBox shulkerBox,MenuUtility mu){
        for(ItemStack item:shulkerBox.getInventory().getContents()){
            if (item != null && item.getType().equals(Material.PLAYER_HEAD)){
                if (item.getItemMeta().getPersistentDataContainer().has(leaderFireKey)){
                    mu.setLeaderHead(item);
                }
            }
        }
    }

    @EventHandler
    public void onBuild(PlayerInteractEvent e){
        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)
                && e.getPlayer().getInventory().getItemInMainHand().equals(Tools.doomStick())
                && e.getClickedBlock()!=null){
            Army army = Tools.getArmyFromShulker(e.getClickedBlock());
            if (army != null){
                e.setCancelled(true);
                for(Unit u:army.getUnits()){
                    u.setHp(1000);
                    u.setMorale(u.getMaxMorale());
                }
            }
        }
    }


    @EventHandler
    public void onInventoryOpen(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK|| e.getClickedBlock() == null) return;



        Army army = Tools.getArmyFromShulker(e.getClickedBlock());
        if (army != null){

            MenuUtility mu = new MenuUtility(e.getPlayer());
            ShulkerBox shulkerBox = (ShulkerBox) e.getClickedBlock().getState();
            ItemStack leader = e.getPlayer().getInventory().getItemInMainHand();

            if(leader.getType().equals(Material.PLAYER_HEAD)){

                PersistentDataContainer data = leader.getItemMeta().getPersistentDataContainer();
                if(data.has(leaderFireKey)){

                    shulkerBox.getInventory().addItem(leader);
                    mu.setLeaderHead(leader);
                    army.setLeaderName(leader.getItemMeta().getDisplayName());
                    updateLeader(army,data);
                    leader.setAmount(-1);
                    
                }
            }

            if(mu.getLeaderHead()==null&&army.getLeaderName()!=null){


            }
            e.setCancelled(true);
            mu.setArmy(army);
            mu.setArmyShulkerBox(shulkerBox);

            new ArmyMenu(mu).open();
        }
//        if(e.getClickedBlock().getState() instanceof ShulkerBox shulker){
//            Inventory inventory = shulker.getInventory();
//
//            for (ItemStack item : inventory.getContents()) {
//                if (item != null && item.hasItemMeta()) {
//                    ItemMeta meta = item.getItemMeta();
//                    if(meta.getPersistentDataContainer().has(armyIdKey, PersistentDataType.STRING)){
//
//                        UUID armyId = UUID.fromString(meta.getPersistentDataContainer().get(armyIdKey,PersistentDataType.STRING));
//                        MenuUtility mu = new MenuUtility(e.getPlayer());
//                        mu.setArmy(db.getArmy(armyId));
//                        if(meta.getPersistentDataContainer().has(botNameKey, PersistentDataType.STRING)){
//                            mu.setBotName(meta.getPersistentDataContainer().get(botNameKey,PersistentDataType.STRING));
//                        }
//                        new ArmyMenu(mu).open();
//                    }
//                }
//            }
//        }

    }
}
