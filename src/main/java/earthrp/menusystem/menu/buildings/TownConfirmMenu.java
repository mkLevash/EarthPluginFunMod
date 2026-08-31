package earthrp.menusystem.menu.buildings;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.UnitTech;
import earthrp.customObjects.Army;
import earthrp.customObjects.ArmyUnit;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.tools.Tools;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class TownConfirmMenu extends Menu {
    public TownConfirmMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }
    private final ItemStack townItem = menuUtility.getBuildingItem();
    Inventory chest = menuUtility.getBuildingChest();

    @Override
    public String getMenuName() {
        return "Вы уверены что хотите начать битву с варварами за город?";
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

                    ServerDatabase db = Earth.getInstance().getDatabase();
                    e.getWhoClicked().closeInventory();
                    ItemMeta meta = townItem.getItemMeta();
                    List<String> lore = meta.getLore();
                    String type = lore.get(0);
                    UUID townId = UUID.fromString(lore.get(1));
                    UUID ownerId = UUID.fromString(lore.get(2));
                    EPlayer owner = db.getPlayer(ownerId);
                    ItemStack ownerItem = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta ownerMeta = (SkullMeta) ownerItem.getItemMeta();
                    ownerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(ownerId));
                    ownerMeta.getPersistentDataContainer().set(townOwnerKey, PersistentDataType.STRING,ownerId.toString());
                    ownerItem.setItemMeta(ownerMeta);
                    String townName = meta.getDisplayName();
                    Location loc = chest.getLocation();
                    Town town = new Town(owner,townId,type,townName,loc);
                    String debug = lore.getLast();
                    town.getData().setChunk(menuUtility.getTownChunks());
                    if(debug.equals("debug")){
                        chest.addItem(ownerItem);
                        chest.addItem(townItem.clone());
                        handleNewTown(town,loc);
                    }else{
                        int barbarianAmount = (int) (Math.random() * 3) + owner.getData().getBarbarians();
                        EPlayer barbarian = db.getPlayer("barbarian");
                        if(barbarian == null){
                            db.addBot("barbarian",UUID.randomUUID());
                            barbarian = db.getPlayer("barbarian");
                        }
                        Army army = new Army(UUID.randomUUID(),barbarian.getUniqueId(),"");
                        army.setBarbarianChest(chest);
                        army.setBarbarianOwnerItem(ownerItem);
                        army.setBarbarianTown(town);
                        army.setBarbarianTownItem(townItem);
                        army.setLocation(loc,0);
                        army.setShulkerLoc(loc);
                        Tools.spawnHologram(loc.clone().add(0.5,1.5,0.5),"Чтобы построить город победите варваров","townHolo");
                        Tools.spawnHologram(loc.clone().add(0.5,1.25,0.5),"Армия <light_purple>" + army.getOwner().getDisplayName() + "<white>'a - <green>" + (army.getTroops()/1000) + "K","armyHoloTroops" + army.getUuid());
                        Tools.spawnHologram(loc.clone().add(0.5,1.0,0.5),"Мораль <dark_green>" + army.getMorale() + "<white> / <dark_green>" + army.getMaxMorale(),"armyHoloMorale" + army.getUuid());
                        db.addArmy(army);
                        UnitTech barbarianTech = UnitTech.valueOf("INF"+ (int)owner.getAttribute(EPlayerAttribute.INF_LVL));
                        for (int i = 0; i < barbarianAmount; i++) {
                            army.addUnit(new ArmyUnit(barbarianTech,UUID.randomUUID(),army.getUuid(),""));
                        }
                    }
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
        inventory.clear();
        List<String> lore = List.of(Tools.colorText("&fВы построите &3" + townItem.getItemMeta().getDisplayName()));
        ItemStack yes = Tools.createItemLegacy(Material.EMERALD,ChatColor.GREEN + "Да",lore);

        ItemStack no = Tools.createItemLegacy(Material.BARRIER,ChatColor.RED + "Нет",null);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }

    private void handleNewTown(Town town, Location loc) {

        Tools.spawnHologramLegacy(loc.clone().add(0.5, 1, 0.5),town.getName(),town.getUniqueId().toString());
        Earth.getInstance().getDatabase().addTown(town);


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
