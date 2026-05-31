package earthrp.menusystem.menu.buildings;

import earthrp.Earth;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.buildingIdKey;
import static earthrp.tools.PDCKeys.buildingTypeKey;

public class TownConfirmMenu extends Menu {
    public TownConfirmMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }
    private final ItemStack townItem = menuUtility.getBuildingItem();
    Inventory chest = menuUtility.getBuildingChest();

    @Override
    public String getMenuName() {
        return "Вы уверены что хотите здесь построить город?";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

        if(e.getCurrentItem() != null){
            switch (e.getCurrentItem().getType()){
                case EMERALD ->{
                    e.getWhoClicked().closeInventory();
                    ItemMeta meta = townItem.getItemMeta();
                    List<String> lore = meta.getLore();
                    String type = lore.get(0);
                    UUID townId = UUID.fromString(lore.get(1));
                    UUID ownerId = UUID.fromString(lore.get(2));
                    String ownerName = lore.get(3);
                    String townName = meta.getDisplayName();
                    Location loc = chest.getLocation();
                    handleNewTown(townId, ownerId, ownerName, townName, type, loc);
                    chest.addItem(townItem);
                    townItem.setAmount(0);
                }

                case BARRIER -> {
                    e.getWhoClicked().closeInventory();
                    new BuildMenu(menuUtility).open();
                }
            }
        }


    }

    @Override
    public void setMenuItems() {
        List<String> lore = List.of(Tools.colorText("&fВы построите &3" + townItem.getItemMeta().getDisplayName()));
        ItemStack yes = Tools.createItem(Material.EMERALD,ChatColor.GREEN + "Да",lore);

        ItemStack no = Tools.createItem(Material.BARRIER,ChatColor.RED + "Нет",null);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }

    private void handleNewTown(UUID townId, UUID ownerId, String ownerName, String townName, String type, Location loc) {
        int x = loc.getChunk().getX();
        int z = loc.getChunk().getZ();
        String world = loc.getWorld().getName();
        Tools.spawnHologram(loc.clone().add(0.5, 1, 0.5),townName,townId.toString());

        Town town = new Town(townId,ownerId,type,townName,ownerName,world,x,z);
        ServerDatabase db = Earth.getInstance().getServerDatabase();
        db.addTown(town);


    }

    private void createTownHologram(Location loc, String name, UUID townId) {
        spawnHologram(loc.getWorld(), loc.clone(), String.valueOf(townId), false);

        // Голограмма для отображаемого имени
        spawnHologram(loc.getWorld(), loc.clone().add(0.5, 1, 0.5), name, true);
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
}
