package earthrp.listeners;

import earthrp.customEnums.UnitTech;
import earthrp.customObjects.*;
import earthrp.tools.Tools;
import earthrp.Earth;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.army.ArmyMenu;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
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

            e.setCancelled(true);

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
                army.setRetreat(false);
                for(ArmyUnit u:army.getUnits()){
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
                EPlayer p = Earth.getInstance().getDatabase().getPlayer(player.getUniqueId());
                p.getData().armiesInHand.add(army.getUuid());
                int amplifier = 5 - (p.getMaxLeaderMovement() / 2 );

                PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, -1, amplifier);
                PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, -1, 1);
                player.addPotionEffect(slowness);
                player.addPotionEffect(glowing);

                Tools.deleteHologram(army.getChunkKey(),"armyHoloTroops" + army.getUuid().toString());
                Tools.deleteHologram(army.getChunkKey(),"armyHoloMorale" + army.getUuid().toString());


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
        Location loc = block.getLocation();
        Chunk toChunk =e.getBlockPlaced().getLocation().getChunk();


        if (Tag.SHULKER_BOXES.isTagged(block.getType())) {
            Army army = Tools.getArmyFromShulker(e.getBlock());
            if(army == null) return;
            if (player.isSneaking()) { // зажат shift
                Set<UUID> armies = Earth.getInstance().getDatabase().getPlayer(player.getUniqueId()).getData().armiesInHand;
                armies.remove(army.getUuid());
                if(armies.isEmpty()){
                    player.removePotionEffect(PotionEffectType.SLOWNESS);
                    player.removePotionEffect(PotionEffectType.GLOWING);
                    player.setSprinting(true);
                }
                army.setLocation(toChunk);

                Tools.spawnHologram(loc.clone().add(0.5,1.25,0.5),"Армия <light_purple>" + army.getOwner().getDisplayName() + "<white>'a - <green>" + (army.getTroops()/1000) + "K","armyHoloTroops" + army.getUuid().toString());
                Tools.spawnHologram(loc.clone().add(0.5,1.0,0.5),"Мораль <dark_green>" + army.getMorale() + "<white> / <dark_green>" + army.getMaxMorale(),"armyHoloMorale" + army.getUuid().toString());


            }else{
                e.setCancelled(true);
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

        Chunk toChunk = e.getTo().getChunk();
        Chunk fromChunk = e.getFrom().getChunk();

        // Если координаты чанков не совпадают — игрок перешел границу
        if (fromChunk.getX() != toChunk.getX() || fromChunk.getZ() != toChunk.getZ()) {
            ServerDatabase db = Earth.getInstance().getDatabase();
            EPlayer player = db.getPlayer(e.getPlayer());
            Set<UUID> armies = player.getData().armiesInHand;
            if (!armies.isEmpty()){
                e.getPlayer().removePotionEffect(PotionEffectType.SLOWNESS);
                int amplifier = 5 - (player.getMaxLeaderMovement() / 2) ;
                PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, -1, amplifier);
                Town town = db.getTownAtChunk(toChunk);
                if(town!=null && !town.getController().equals(player) && town.isFort() && !player.getData().getAlly().contains(town.getOwnerId()) ){

                    if(isBorderChunk(toChunk,town)){
                        e.getPlayer().sendMessage("Вы зашли на территорию контролируемую крепостью и не можете пройти дальше без осады или права прохода");
                        Player javaPlayer = Bukkit.getPlayer(town.getController().getUniqueId());
                        if (javaPlayer!=null) javaPlayer.sendMessage(Tools.deserialize("На границе города <aqua>"+ town.getName() + "<white> была замечена <red>армия врага!"));
                    }else {
                        slowness = new PotionEffect(PotionEffectType.SLOWNESS, -1, 6);

                    }


                }
                e.getPlayer().addPotionEffect(slowness);
                for(UUID armyId : armies){
                    Army army = db.getArmy(armyId);
                    army.setLocation(toChunk,System.currentTimeMillis());
                }

            }
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

        if (!Earth.getInstance().getDatabase().getPlayer(player.getUniqueId()).getData().armiesInHand.isEmpty()) {
            double reductionMultiplier = 0.35;
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

            if(leader.getType().equals(Material.PLAYER_HEAD) && army.getLeaderName()==null){

                PersistentDataContainer data = leader.getItemMeta().getPersistentDataContainer();
                if(data.has(leaderFireKey)){

                    shulkerBox.getInventory().addItem(leader);
                    mu.setLeaderHead(leader);
                    army.setLeaderName(leader.getItemMeta().getDisplayName());
                    updateLeader(army,data);
                    leader.setAmount(-1);
                    
                }
            }
            e.setCancelled(true);
            mu.setArmy(army);
            mu.setArmyShulkerBox(shulkerBox);

            new ArmyMenu(mu).open();
        }

    }

    private boolean validUnitTransfer(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return false;

        // Клик должен быть по инвентарю игрока (нижнему)
        if (e.getClickedInventory().equals(e.getWhoClicked().getInventory()) && (e.getInventory().getType() == InventoryType.SHULKER_BOX || armyExists((e))) ){
            if (e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta()){
                ItemMeta meta = e.getCurrentItem().getItemMeta();
                NamespacedKey key = new NamespacedKey(Earth.getInstance(), "unitType");

                return (meta.getPersistentDataContainer().has(key, PersistentDataType.STRING));
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
        ServerDatabase db = this.earth.getDatabase();
        UUID ownerId = e.getWhoClicked().getUniqueId();
        UUID armyId = UUID.randomUUID();

        e.getInventory().clear();

        ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta ownerMeta = (SkullMeta) owner.getItemMeta();
        ownerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerId));
        ownerMeta.getPersistentDataContainer().set(armyOwnerKey, PersistentDataType.STRING, ownerId.toString());
        ownerMeta.getPersistentDataContainer().set(armyIdKey, PersistentDataType.STRING, armyId.toString());
        ownerMeta.setDisplayName(e.getWhoClicked().getName());
        owner.setItemMeta(ownerMeta);





        Location loc = e.getInventory().getLocation();
        Army army = new Army(armyId,ownerId,loc);
        ItemStack item = e.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        String type = data.get(unitTypeKey,PersistentDataType.STRING);


        db.addArmy(army);

        for (int i = 0; i < item.getAmount(); i++) {
            ArmyUnit unit = new ArmyUnit(UnitTech.valueOf(type),UUID.randomUUID(),armyId,"");

            if(data.has(unitMoraleKey)){
                unit.setMorale(data.get(unitMoraleKey,PersistentDataType.DOUBLE));
            }
            if(data.has(unitDiscKey)){
                double disc = data.get(unitDiscKey,PersistentDataType.DOUBLE);
                unit.getData().setDisc(disc);
            }

            army.addUnit(unit);
        }
        item.setAmount(0);
        e.getInventory().addItem(owner);

        Tools.spawnHologram(loc.clone().add(0.5,1.25,0.5),"Армия <light_purple>" + army.getOwner().getDisplayName() + "<white>'a - <green>" + (army.getTroops()/1000) + "K","armyHoloTroops" + army.getUuid());
        Tools.spawnHologram(loc.clone().add(0.5,1.0,0.5),"Мораль <dark_green>" + army.getMorale() + "<white> / <dark_green>" + army.getMaxMorale(),"armyHoloMorale" + army.getUuid());




    }

    private void handleExistingArmy(InventoryClickEvent e){
        ServerDatabase db = this.earth.getDatabase();
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
                for (int i = 0; i < item.getAmount(); i++) {
                    ArmyUnit unit = new ArmyUnit(UnitTech.valueOf(type),UUID.randomUUID(),armyId, "");

                    if(data.has(unitMoraleKey)){
                        unit.setMorale(data.get(unitMoraleKey,PersistentDataType.DOUBLE));
                    }
                    if(data.has(unitDiscKey)){
                        double disc = data.get(unitDiscKey,PersistentDataType.DOUBLE);
                        unit.getData().setDisc(disc);
                    }
                    army.addUnit(unit);

                }

                item.setAmount(0);
            } else if (data.has(leaderFireKey)) {
                updateLeader(army,data);
                army.setLeaderName(meta.getDisplayName());
                item.setAmount(0);
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
        int move = data.get(leaderMoveKey,PersistentDataType.INTEGER);
        int siege = data.get(leaderSiegeKey,PersistentDataType.INTEGER);

        army.setLeaderFire(fire);
        army.setLeaderShock(shock);
        army.getData().setLeaderMovement(move);
        army.getData().setLeaderSiege(siege);
    }

    private boolean isBorderChunk(Chunk currentChunk, Town town) {

        ServerDatabase db = Earth.getInstance().getDatabase();

        World world = currentChunk.getWorld();
        int currentX = currentChunk.getX();
        int currentZ = currentChunk.getZ();


        int[][] neighbors = {
                {currentX, currentZ - 1}, // Север
                {currentX, currentZ + 1}, // Юг
                {currentX + 1, currentZ}, // Восток
                {currentX - 1, currentZ}  // Запад
        };


        for (int[] offset : neighbors) {
            int nextX = offset[0];
            int nextZ = offset[1];

            Chunk neighborChunk = world.getChunkAt(nextX, nextZ);


            Town neighborTown = db.getTownAtChunk(neighborChunk);
            if (neighborTown == null || !neighborTown.equals(town)) {
                return true;
            }
        }

        return false;
    }
}
