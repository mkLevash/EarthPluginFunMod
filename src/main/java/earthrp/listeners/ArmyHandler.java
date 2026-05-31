package earthrp.listeners;

import earthrp.customObjects.EPlayer;
import earthrp.tools.Tools;
import earthrp.customObjects.Army;
import earthrp.Earth;
import earthrp.customObjects.Unit;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.army.ArmyMenu;
import io.papermc.paper.event.packet.PlayerChunkLoadEvent;
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







    @EventHandler
    public void onDoomStickInteract(PlayerInteractEvent e){
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
    public void onPlayerPickupArmy(EntityPickupItemEvent event) {


        if(event.getEntity() instanceof Player player){
            ItemStack item = event.getItem().getItemStack();
            Army army = null;

            // Проверяем, является ли предмет шалкеровым ящиком
            if (item.getItemMeta() instanceof BlockStateMeta bsm) {
                if (bsm.getBlockState() instanceof ShulkerBox shulker) {
                    army = getArmyFromInventory(shulker.getInventory());
                }
            }

            if (army!=null){
                PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, Integer.MAX_VALUE, 4);
                PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1);
                player.addPotionEffect(slowness);
                player.addPotionEffect(glowing);
                Earth.getInstance().getServerDatabase().getPlayer(player.getUniqueId()).getData().armiesInHand.add(army.getUuid());
                army.setPlayerUUID(player.getUniqueId());
                player.setSprinting(false);
            }
        }

    }

    @EventHandler
    public void onShulkerOpen(PlayerInteractEvent e){
        if((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && !e.getPlayer().isSneaking()) ){
            ItemStack item = e.getItem();

            if (item == null || !Tag.SHULKER_BOXES.isTagged(item.getType())) {
                return;
            }

            if (item.hasItemMeta() && item.getItemMeta() instanceof BlockStateMeta bsm) {

                if (bsm.getBlockState() instanceof ShulkerBox shulkerBox) {
                    Army army = Tools.getArmyFromInventory(shulkerBox.getInventory());
                    if (army != null){

                        MenuUtility mu = new MenuUtility(e.getPlayer());
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
                }
            }

        }
    }



    @EventHandler
    public void onShulkerPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        Block block = e.getBlockPlaced();

        // Проверяем, является ли поставленный блок шалкеровым ящиком
        // Использование Tag.SHULKER_BOXES автоматически покроет все цвета
        if (Tag.SHULKER_BOXES.isTagged(block.getType())) {

            // Проверяем, крадется (шифтит) ли игрок
            if (!player.isSneaking()) {
                // Отменяем установку блока
                e.setCancelled(true);

            }else{
                Army army = Tools.getArmyFromShulker(e.getBlock());
                if(army != null){
                    Set<UUID> armies = Earth.getInstance().getServerDatabase().getPlayer(player.getUniqueId()).getData().armiesInHand;
                    armies.remove(army.getUuid());
                    if(armies.isEmpty()){
                        player.removePotionEffect(PotionEffectType.SLOWNESS);
                        player.removePotionEffect(PotionEffectType.GLOWING);
                        player.setSprinting(true);
                    }
                    army.setPlayerUUID(null);
                    army.setStaticLocation(e.getBlock().getLocation());

                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        // Проверяем, изменил ли игрок именно блок (чтобы не срабатывало, когда он просто крутит головой)
        if (e.getFrom().getBlockX() == e.getTo().getBlockX() &&
                e.getFrom().getBlockZ() == e.getTo().getBlockZ()) {
            return;
        }

        // Получаем чанк, ИЗ которого игрок вышел, и В который пришел
        EPlayer player = Earth.getInstance().getServerDatabase().getPlayer(e.getPlayer().getUniqueId());

        Chunk toChunk = e.getTo().getChunk();
        Chunk fromChunk = toChunk.getWorld().getChunkAt(player.getData().getLocation());

        // Если координаты чанков не совпадают — игрок перешел границу
        if (fromChunk.getX() != toChunk.getX() || fromChunk.getZ() != toChunk.getZ()) {



            if (!player.getData().armiesInHand.isEmpty()){
                player.getData().setLocation(toChunk.getChunkKey());
                player.getData().setLocationTime(System.currentTimeMillis());

            }
        }
    }

    @EventHandler
    public void onMove(PlayerChunkLoadEvent e){
        EPlayer player = Earth.getInstance().getServerDatabase().getPlayer(e.getPlayer().getUniqueId());
        if (!player.getData().armiesInHand.isEmpty()){

        }
    }

    @EventHandler
    public void onPlayerMoveInAir(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // 1. Проверяем, находится ли игрок в воздухе
        // (isOnGround может быть обманут читами, но для физики этого достаточно)
        if (player.isOnGround()) {
            return;
        }

        if (!Earth.getInstance().getServerDatabase().getPlayer(player.getUniqueId()).getData().armiesInHand.isEmpty()) {
            double reductionMultiplier = 0.2;
            Vector velocity = player.getVelocity();
            player.setVelocity(new Vector(
                    velocity.getX() * reductionMultiplier,
                    velocity.getY(),
                    velocity.getZ() * reductionMultiplier
            ));

        }


    }


    @EventHandler
    public void onArmyMenuOpen(PlayerInteractEvent e) {
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
            unit.setMorale(Tools.round(unit.getBaseMorale() * db.getPlayer(ownerId).getMoraleMod()));
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
}
