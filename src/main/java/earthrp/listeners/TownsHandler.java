package earthrp.listeners;


import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TownsMenu;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;



public class TownsHandler implements Listener {

    private final Earth earthPlugin;
    int townSize = Earth.getInstance().getConfig().getInt("townSize");
    public TownsHandler(Earth moraPlugin) {
        this.earthPlugin = moraPlugin;
        db = Earth.getInstance().getServerDatabase();
    }
    ServerDatabase db;

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        try {
            if (!isValidTownTransfer(e)) return;

            ItemStack item = e.getCurrentItem();
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();

            String type = lore.get(0);
            UUID townId = UUID.fromString(lore.get(1));
            UUID ownerId = UUID.fromString(lore.get(2));
            String ownerName = lore.get(3);
            String townName = meta.getDisplayName();
            Location loc = e.getInventory().getLocation();

            if (db.townExists(townId)) {
                handleExistingTown(e, townId, townName);
            } else {
                handleNewTown(e, townId, ownerId, ownerName, townName, type, loc);
            }
        } catch (Exception ex) {
            Bukkit.getLogger().severe("Ошибка при обработке клика: " + ex.getMessage());
            e.setCancelled(true);
        }
    }

    private void handleExistingTown(InventoryClickEvent e, UUID townId, String townName) {
        Town town = db.getTown(townId);
        db.deleteTown(town);
    }

    private void handleNewTown(InventoryClickEvent e, UUID townId, UUID ownerId, String ownerName, String townName, String type, Location loc) {
        int x = loc.getChunk().getX();
        int z = loc.getChunk().getZ();
        String world = loc.getWorld().getName();


        if (db.isChunkClaimed(x,z)) {
            cancelEventWithMessage(e, "Слишком близко к другому городу");
            return;
        }

        createTownHologram(loc, townName, townId);


        Town town = new Town(townId,ownerId,type,townName,ownerName,world,x,z);
        db.addTown(town);
        db.markChunk(x,z,townId);

    }

    private void createTownHologram(Location loc, String name, UUID townId) {
        spawnHologram(loc.clone(), String.valueOf(townId), false);

        // Голограмма для отображаемого имени
        spawnHologram(loc.clone().add(0.5, 1, 0.5), name, true);
    }

    private void spawnHologram(Location loc, String text, boolean visible) {
        ArmorStand hologram = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        hologram.setVisible(false);
        hologram.setMarker(true);
        hologram.setCustomNameVisible(visible);
        hologram.setCustomName(text);
        hologram.setGravity(false);
        hologram.setCollidable(false);
        hologram.setInvulnerable(true); // Защита от случайного удаления
    }

    private boolean isValidTownTransfer(InventoryClickEvent e) {
        return e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY
                && e.getInventory().getType() == InventoryType.CHEST
                && e.getCurrentItem() != null
                && e.getCurrentItem().getItemMeta() != null
                && e.getCurrentItem().getItemMeta().getLore() != null
                && List.of("capital", "townHall").contains(e.getCurrentItem().getItemMeta().getLore().get(0));
    }

    private void cancelEventWithMessage(InventoryClickEvent e, String message) {
        e.setCancelled(true);
        e.getWhoClicked().sendMessage(message);
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
                    new TownsMenu(pmu, Earth.getInstance()).open();
                }
            }
        }
    }

}
