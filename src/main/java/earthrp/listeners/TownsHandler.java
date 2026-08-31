package earthrp.listeners;


import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.configs.CustomConfig;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TownsMenu;
import earthrp.menusystem.menu.buildings.TownConfirmMenu;
import earthrp.tools.maps.CityBoundaryCalculator;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static earthrp.tools.PDCKeys.*;

import java.util.*;


public class TownsHandler implements Listener {

    private final Earth earthPlugin;
    int townSize = Earth.getInstance().getConfig().getInt("townSize");
    public TownsHandler(Earth moraPlugin) {
        this.earthPlugin = moraPlugin;
        db = Earth.getInstance().getDatabase();
    }
    ServerDatabase db;

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        // Проверяем сущности в только что загруженном чанке
        for (Entity entity : event.getChunk().getEntities()) {
            var data = entity.getPersistentDataContainer();
            if (data.has(villagerTownKey, PersistentDataType.STRING)) {
                String id = data.get(villagerTownKey,PersistentDataType.STRING);
                if(CustomConfig.get().getStringList("deletedTowns").contains(id)){
                    entity.remove();
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if(isValidTownInteract(e)){
            e.setCancelled(true);
            CityBoundaryCalculator calculator = new CityBoundaryCalculator(Earth.getInstance().getRegionMap());
            int x = e.getClickedBlock().getChunk().getX();
            int z = e.getClickedBlock().getChunk().getZ();
            int townSize = (int) Math.pow(Earth.getInstance().getConfig().getInt("townSize"),2);
            Set<CityBoundaryCalculator.chunkPoint> chunks = calculator.calculateCityArea(x,z,townSize);
            if(chunks.size() > townSize/2){
                MenuUtility menuUtility = new MenuUtility(e.getPlayer());

                ItemStack item = e.getItem();
                menuUtility.setBuildingItem(item);

                Container container = (Container) e.getClickedBlock().getState();
                menuUtility.setBuildingChest(container.getInventory());
                menuUtility.setTownChunks(chunks);

                new TownConfirmMenu(menuUtility).open();
            }else{
                e.getPlayer().sendMessage("Слишком мало места для города");
            }

        }
    }

    @EventHandler
    public void onInventoryOpen(PlayerInteractEvent e) {
        // Проверь, открыт ли сундук (Chest)
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK
                || e.getClickedBlock() == null
                || e.getClickedBlock().getType() != Material.CHEST) return;

        // Получи игрока
        Player player = e.getPlayer();

        // Получи Holder и убедись, что это сундук

        Container container = (Container) e.getClickedBlock().getState();
        Inventory inventory = container.getInventory();

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                List<String> lore = null;
                if(meta.hasLore()){
                    lore = meta.getLore();
                }
                if ( lore!=null && List.of("capital", "townHall").contains(lore.get(0))) {
                    e.setCancelled(true);
                    Town town = db.getTown(UUID.fromString(lore.get(1)));
                    MenuUtility pmu = new MenuUtility(player);
                    pmu.setTown(town);
                    pmu.setBuildingChest(inventory);
                    new TownsMenu(pmu).open();
                }

            }
        }
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



}
