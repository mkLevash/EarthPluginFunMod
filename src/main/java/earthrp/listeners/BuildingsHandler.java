package earthrp.listeners;

import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.Main;
import earthrp.menusystem.menu.buildings.BuildMenu;
import earthrp.tools.Tools;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Container;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class BuildingsHandler implements Listener {

    private static final String NO_BUILD_SLOTS_MSG = "Недостаточно ячеек строительства";
    ServerDatabase db;

    public BuildingsHandler( Earth moraPlugin) {
        this.db = moraPlugin.getServerDatabase();
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (isValidBuildingInteract(e)) {
            //Bukkit.broadcastMessage(String.valueOf(isValidBuildingInteract(e)));
            e.setCancelled(true);
            ItemStack building = e.getItem();
            Inventory chestInventory = null;
            if(e.getClickedBlock().getState() instanceof Container container){ // всегда проходит потому что сюда попадают только интеракты с сундуками
                chestInventory = container.getInventory();
            }
            MenuUtility menuUtility = new MenuUtility(e.getPlayer());
            menuUtility.setBuildingItem(building);
            menuUtility.setBuildingChest(chestInventory);
            new BuildMenu(menuUtility).open();
        }
        if(isValidTownInteract(e)){
            ItemStack item = e.getItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();

            String type = lore.get(0);
            UUID townId = UUID.fromString(lore.get(1));
            UUID ownerId = UUID.fromString(lore.get(2));
            String ownerName = lore.get(3);
            String townName = meta.getDisplayName();
            Location loc = e.getClickedBlock().getLocation();

            Inventory chestInventory = null;
            if(e.getClickedBlock().getState() instanceof Container container){ // всегда проходит потому что сюда попадают только интеракты с сундуками
                chestInventory = container.getInventory();
            }

            handleNewTown(townId, ownerId, ownerName, townName, type, loc);

            chestInventory.addItem(item);
            item.setAmount(0);

        }
        if(isValidMenuInteract(e)){
            e.setCancelled(true);
            Player p = e.getPlayer();
            MenuUtility mu = new MenuUtility(p);
            mu.setPlayer(db.getPlayer(p.getUniqueId()));
            Main menu = new Main(mu);
            menu.open();
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (!isValidBuildingTransfer(e)) {
            return;
        }

        ItemStack item = e.getCurrentItem();
        ItemMeta meta = item.getItemMeta();
        UUID buildingId = UUID.fromString(meta.getLore().get(1));

        if (db.buildingExists(buildingId)) {
            handleExistingBuilding(e, buildingId, item);
        }
    }

    @EventHandler
    public void onChestExplode(EntityExplodeEvent event) {
        // Получаем список всех блоков, которые должны разрушиться
        List<Block> blocks = event.blockList();

        // Проходимся по списку и ищем сундуки
        blocks.removeIf(block -> {
            if (block.getType() == Material.CHEST) {

                if (block.getState() instanceof Container container){
                    Inventory chestInventory = container.getInventory();

                    for (ItemStack item : chestInventory.getContents()) {
                        if (item != null && item.hasItemMeta()) {
                            ItemMeta meta = item.getItemMeta();
                            PersistentDataContainer data = meta.getPersistentDataContainer();
                            if(data.has(buildingIdKey)){

                                UUID bId = UUID.fromString(data.get(buildingIdKey, PersistentDataType.STRING));
                                Building building = db.getBuilding(bId);
                                if(building==null) break;
                                event.setCancelled(true);

                            }
                        }
                    }
                }


                // Если вы хотите, чтобы сундук НЕ взрывался:
                return true; // Удаляем из списка разрушаемых блоков
            }
            return false;
        });
    }

    private void handleExistingBuilding(InventoryClickEvent e, UUID buildingId,  ItemStack item) {
        Building building = db.getBuilding(buildingId);
        if(building.getItem()==null){
            db.deleteBuilding(building);
            e.getWhoClicked().getInventory().addItem(item);
        }else{
            e.setCancelled(true);
        }
    }

    private void handleNewTown(UUID townId, UUID ownerId, String ownerName, String townName, String type, Location loc) {
        int x = loc.getChunk().getX();
        int z = loc.getChunk().getZ();
        String world = loc.getWorld().getName();

        createTownHologram(loc, townName, townId);



        Town town = new Town(townId,ownerId,type,townName,ownerName,world,x,z);
        db.addTown(town);

    }

    private void createTownHologram(Location loc, String name, UUID townId) {
        spawnHologram(loc.getWorld(), loc.clone(), String.valueOf(townId), false);

        // Голограмма для отображаемого имени
        spawnHologram(loc.getWorld(), loc.clone().add(0.5, 1, 0.5), name, true);
    }


    private void handleNewBuilding(InventoryClickEvent e, UUID buildingId, ItemMeta meta){

        //            Player player = Bukkit.getPlayer(e.getWhoClicked().getUniqueId());
//            Town[] towns = db.getPlayerTowns(player.getUniqueId());
//            MenuUtility menuUtility = new MenuUtility(player);
//            menuUtility.setPlayerTowns(towns);
//            e.getWhoClicked().closeInventory();
//            new BuildMenu(menuUtility).open();
        // 1. Получаем данные чанка и города
        String type = meta.getLore().get(2);
        Location loc = e.getInventory().getLocation();
        int x = loc.getChunk().getX();
        int z = loc.getChunk().getZ();
        Town town = null;
        UUID townId = null; //db.isChunkClaimed(new Earth.ChunkPosition(loc.getChunk().getX(), loc.getChunk().getZ(),loc.getWorld().getName()));
        if(townId != null){
            town = db.getTown(townId);
        }else{
            HashSet<Town> towns = db.getTowns();
            double min = Earth.getInstance().getConfig().getInt("townSize")*4;
            for (Town t : towns){
                double distance = Tools.calculateDistance(x,z,t.getChunkX(),t.getChunkZ());
                if(distance<min){
                    town = t;
                    min = distance;
                }
            }
        }
        if (town == null) {
            cancelEventWithMessage(e, "Город не найден");
            return;
        }

//        // 2. Проверяем лимит строительства
//        if (town.getBuildingsAmount() >= town.getBuildSite()&&!type.equals("port")) {
//            cancelEventWithMessage(e, NO_BUILD_SLOTS_MSG);
//            return;
//        }

        // 3. Создаем голограммы
        createBuildingHolograms(loc, buildingId, meta.getDisplayName());

        // 4. Обновляем данные в БД
       // updateTownAndBuildingData(town, buildingId, type, loc);

    }

    private void createBuildingHolograms(Location loc, UUID buildingId, String displayName) {

        // Голограмма для ID здания
        spawnHologram(loc.getWorld(), loc.clone(), String.valueOf(buildingId), false);

        // Голограмма для отображаемого имени
        spawnHologram(loc.getWorld(), loc.clone().add(0.5, 1, 0.5), displayName, true);
    }

    private void spawnHologram(World world, Location loc, String text, boolean visible) {
        ArmorStand hologram = (ArmorStand) world.spawnEntity(loc, EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setMarker(true);
        hologram.setCustomNameVisible(visible);
        hologram.setCustomName(text);
        hologram.setGravity(false);
        hologram.setCollidable(false);
        //hologram.setInvulnerable(true); // Защита от случайного удаления
    }
//
//    private void updateTownAndBuildingData(Town town, UUID buildingId, String type, Location loc) {
//        // Обновляем данные в транзакции
//
//        db.addBuilding(
//                town.getName(),
//                buildingId,
//                town.getUniqueId(),
//                type,
//                loc.getWorld().getName(),
//                loc.getChunk().getX(),
//                loc.getChunk().getZ()
//        );
//        Building building = db.getBuilding(buildingId);
//        if(town.getTradeTownId()!=null){
//            building.setMarketId(town.getTradeTownId());
//        } else if (town.getLandHubId()!=null) {
//            building.setMarketId(town.getLandHubId());
//        }
//        if(type.equals("port")){
//            town.setPortId(buildingId);
//        }else{
//            town.setBuildingsAmount(town.getBuildingsAmount() + 1);
//        }
//        if(type.equals("landHub")){
//            town.setLandHubId(buildingId);
//            CustomConfig.set("trade.towns."+town.getUniqueId()+".tradeMod",1.0);
//            CustomConfig.set("trade.towns."+town.getUniqueId()+".name",town.getName());
//            Set<Building> buildings = db.getBuildings();
//            for(Building b:buildings){
//                if(b.getTownId().equals(town.getUniqueId())){
//                    b.setMarketId(buildingId);
//                }
//            }
//        }
////        try (Connection conn = db.getConnection()) {
////            conn.setAutoCommit(false); // Начинаем транзакцию
////
////            try {
////
////
////                conn.commit(); // Всё прошло успешно — коммитим
////
////            } catch (SQLException e) {
////                conn.rollback(); // Откат в случае ошибки
////                throw e;
////            }
////
////        } catch (SQLException ex) {
////            System.err.println("Transaction failed: " + ex.getMessage());
////            throw ex;
////        }
//    }

    private boolean isBlockingBuildingInteract(PlayerInteractEvent e) {
        return e.hasItem() && e.getItem().getItemMeta().getPersistentDataContainer().has(buildingIdKey);
    }

    private boolean isValidBuildingInteract(PlayerInteractEvent e) {
        return e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.hasItem()
                && e.getClickedBlock().getType().equals(Material.CHEST)
                && e.getItem().getItemMeta().getPersistentDataContainer().has(buildingIdKey);
    }

    private boolean isValidMenuInteract(PlayerInteractEvent e) {
        return e.hasItem()
                && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR)
                && e.getItem().hasItemMeta()
                && e.getItem().getItemMeta().getDisplayName().equals("Главное меню");
    }

    private boolean isValidTownInteract(PlayerInteractEvent e) {
        return e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.hasItem()
                && e.getClickedBlock().getType().equals(Material.CHEST)
                && e.getItem() != null
                && e.getItem().hasItemMeta()
                && e.getItem().getItemMeta().hasLore()
                && List.of("capital", "townHall").contains(e.getItem().getItemMeta().getLore().get(0));
    }

    private boolean isValidBuildingTransfer(InventoryClickEvent e) {
        return e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                && e.getInventory().getType() == InventoryType.CHEST
                && e.getCurrentItem() != null
                && e.getCurrentItem().getItemMeta() != null
                && e.getCurrentItem().getItemMeta().getLore() != null
                && e.getCurrentItem().getItemMeta().getLore().get(0).equals("building");
    }

    private void cancelEventWithMessage(InventoryClickEvent e, String message) {
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(message);
    }

//    @EventHandler
//    public void onMenuClick(InventoryClickEvent e)  {
//        Bukkit.broadcastMessage(String.valueOf(e.getAction()));
////         = e.getAction().equals(InventoryAction.PICKUP_ALL);
//        boolean b = e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY) && String.valueOf(e.getInventory().getType()).equals("CHEST") && Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getLore() != null;
//        if (b){
//            //System.out.println(e.getCurrentItem().getItemMeta().getPersistentDataContainer());
//            ItemStack item = e.getCurrentItem();
//            ItemMeta itemMeta = item.getItemMeta();
//            List<String> lore = Objects.requireNonNull(itemMeta.getLore());
//            String itemType = lore.get(0);
//            if (itemType.equals("building") ){
//                UUID buildingId = UUID.fromString(lore.get(1));
//                String type = lore.get(2);
//                Location loc = e.getInventory().getLocation();
//                assert loc != null;
//                String world = Objects.requireNonNull(loc.getWorld()).getName();
//                int x = loc.getChunk().getX();
//                int z = loc.getChunk().getZ();
//                if(e.getAction().equals(InventoryAction.MOVE_TO_OTHER_INVENTORY)){
//                    if (!db.buildingExists(buildingId)){
//                        Town town = null;
//                        Earth.EarthChunk chunk = db.getChunk(new Earth.ChunkPosition(x,z),loc.getWorld().getName());
//                        if(chunk != null){
//                            if (chunk.townId()!=null){
//                                town = db.getTown(UUID.fromString(chunk.townId()));
//                            }
//                        }
//                        if (town != null && town.getBuildings()<town.getBuildSite()){
//                            ArmorStand hologram0 = (ArmorStand) Bukkit.getWorld(world).spawnEntity(loc, EntityType.ARMOR_STAND);
//                            hologram0.setVisible(false);
//                            hologram0.setCustomNameVisible(false);
//                            hologram0.setCustomName(String.valueOf(buildingId));
//                            hologram0.setGravity(false);
//                            hologram0.setCollidable(false);
//                            ArmorStand hologram = (ArmorStand) Bukkit.getWorld(world).spawnEntity(loc.add(0.5,-1,0.5), EntityType.ARMOR_STAND);
//                            hologram.setVisible(false);
//                            hologram.setCustomNameVisible(true);
//                            hologram.setCustomName(itemMeta.getDisplayName());
//                            hologram.setGravity(false);
//                            hologram.setCollidable(false);
//
//                            String townName = town.getName();
//                            UUID townId = town.getUniqueId();
//                            db.addBuilding(townName,buildingId,townId,type,loc.getWorld().getName(),loc.getChunk().getX(),loc.getChunk().getZ());
//                            int buildingAmount = town.getBuildings();
//                            town.setBuildings(buildingAmount+1);
//                            db.updateTown(town);
//                        }else{
//                            e.setCancelled(true);
//                            e.getWhoClicked().sendMessage("Недостаточно ячеек строительства");
//                        }
//
//
//
//                    }else {
//
//                        Building building = db.getBuilding(buildingId);
//                        if(building.getItem()==null){
//                            db.deleteBuilding(building);
//                            e.getWhoClicked().getInventory().addItem(item);
//                        }else{
//                            e.setCancelled(true);
//                        }
//                    }
//
//                }
//            }
//        }
//    }
    @EventHandler
    public void onBuild(PlayerInteractEvent e){
        if(e.hasItem() && e.getItem().getItemMeta().getPersistentDataContainer().has(buildingIdKey)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onFeed(PlayerInteractEntityEvent e){

        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        if(item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(buildingIdKey)){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        // Проверяем, является ли блок сундуком
        if (!(block.getState() instanceof Chest chest)) return;

        // Получаем содержимое инвентаря сундука
        ItemStack[] contents = chest.getBlockInventory().getContents();

        if (contents == null) return;

        for (ItemStack item : contents) {
            if (item == null || !item.hasItemMeta()) continue;

            ItemMeta meta = item.getItemMeta();
            if(meta.hasLore()){
                List<String> lore = meta.getLore();
                if (lore == null || lore.isEmpty()) continue;

                String tag = lore.get(0).toLowerCase();
                if (tag.equals("capital") || tag.equals("townhall")) {
                    UUID townId = UUID.fromString(lore.get(1));
                    if(db.getTown(townId)!=null) e.setCancelled(true);
                    return; // Прерываем дальше, блокировать достаточно один раз
                }
                if(tag.equals("building")){
                    UUID townId = UUID.fromString(lore.get(1));
                    if(db.getBuilding(townId)!=null) e.setCancelled(true);
                    return;
                }

            }
            PersistentDataContainer data = meta.getPersistentDataContainer();
            if(data.has(buildingIdKey)){
                if (db.getBuilding(UUID.fromString(data.get(buildingIdKey, PersistentDataType.STRING)))!=null){
                    Bukkit.broadcastMessage("canceled");
                    e.setCancelled(true);
                    return;
                }

            }


        }
    }


}
