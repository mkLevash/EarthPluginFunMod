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
import java.util.Set;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class BuildingsHandler implements Listener {

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
            boolean pastureCheck = true;
            boolean farmCheck = true;
            if(e.getClickedBlock().getState() instanceof Container container){ // всегда проходит потому что сюда попадают только интеракты с сундуками
                chestInventory = container.getInventory();
                PersistentDataContainer data = building.getItemMeta().getPersistentDataContainer();
                String type = data.get(buildingTypeKey,PersistentDataType.STRING);
                if(type.equals("pasture")){
                    if(Building.countEnclosedArea(container.getLocation(),1000) == -1) pastureCheck = false;

                }
                if (type.equals("farm")){
                    for(Building b:db.getBuildings()){
                        if(b.getType().equals(type)){
                            Location loc = b.getLocation();
                            double dx = Math.abs(e.getClickedBlock().getX() - loc.getBlockX());
                            double dz = Math.abs(e.getClickedBlock().getZ() - loc.getBlockZ());

                            if (dx <= 8f && dz <= 8f)
                            {
                                farmCheck = false;
                                break;
                            }
                        }
                    }
                }
            }

            if(!pastureCheck){
                e.getPlayer().sendMessage("Пастбище не закрытое или превышает максимальную площадь в 1000 блоков");

            } else if (!farmCheck) {
                e.getPlayer().sendMessage("Слишком близко к другой плантации");
            } else{
                MenuUtility menuUtility = new MenuUtility(e.getPlayer());
                menuUtility.setBuildingItem(building);
                menuUtility.setBuildingChest(chestInventory);

                new BuildMenu(menuUtility).open();
            }


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
