package earthrp.listeners;

import earthrp.customEnums.BuildingType;
import earthrp.customObjects.PlayerData;
import earthrp.menusystem.menu.buildings.inGame.*;
import earthrp.tools.Tools;
import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class BuildingsClickHandler implements Listener {

    private final Earth earthPlugin;

    public BuildingsClickHandler(Earth moraPlugin) {
        this.earthPlugin = moraPlugin;
    }

    private boolean isIdea(ItemStack item){
        return item != null && item.hasItemMeta() && item.getType().equals(Material.SOUL_LANTERN) && item.getItemMeta().getPersistentDataContainer().has(techIdKey);
    }

    private boolean isMilIdea(ItemStack item){
        return item != null && item.hasItemMeta() && item.getType().equals(Material.LANTERN) && item.getItemMeta().getPersistentDataContainer().has(techIdKey);
    }

    private boolean isIdeaDesc(ItemStack item){
        return item.getItemMeta().getPersistentDataContainer().has(ideaEffectIdKey);
    }



    @EventHandler
    public void investIdea(InventoryClickEvent e){

        if(e.getInventory().getType() == InventoryType.SHULKER_BOX){
            ItemStack light = e.getInventory().getItem(9);
            if(light == null) return;
            PersistentDataContainer pdc = light.getItemMeta().getPersistentDataContainer();
            if(pdc.has(ideaOwnerKey)){
                e.setCancelled(true);
                int rawSlot = e.getRawSlot();
                int topSize = e.getView().getTopInventory().getSize();
                if (rawSlot < topSize){
                    ItemStack ideaDesc = e.getInventory().getItem(Math.max(0,rawSlot-9));
                    ItemStack idea = e.getCurrentItem();
                    if(ideaDesc != null && isIdeaDesc(ideaDesc) && e.getInventory().getItem(rawSlot+1) == null){
                        if(isIdea(idea) || isMilIdea(idea)){


                            if(rawSlot == 24 && isIdea(idea)){
                                UUID ownerId = UUID.fromString(pdc.get(ideaOwnerKey,PersistentDataType.STRING));
                                PlayerData data = Earth.getInstance().getDatabase().getPlayer(ownerId).getData();
                                data.setIdeas(data.getIdeas()-1);

                            }
                            Tools.backIdea(ideaDesc);
                            e.getWhoClicked().getInventory().addItem(idea.clone());
                            idea.setAmount(0);

                        }


                    }
                }else{
                    ItemStack idea = e.getCurrentItem();
                    int[] slots = {20, 21, 22, 23, 24};

                    for (int slot : slots) {
                        if (e.getInventory().getItem(slot) == null) {
                            if(isMilIdea(idea) || isIdea(idea)){
                                Tools.investIdea(e.getInventory().getItem(slot-9));

                                if(slot == 24 && isIdea(idea)){
                                    UUID ownerId = UUID.fromString(pdc.get(ideaOwnerKey,PersistentDataType.STRING));
                                    PlayerData data = Earth.getInstance().getDatabase().getPlayer(ownerId).getData();
                                    data.setIdeas(data.getIdeas()+1);
                                }
                                ItemStack placedIdea = idea.clone();
                                placedIdea.setAmount(1);
                                e.getInventory().setItem(slot, placedIdea);
                                idea.setAmount(idea.getAmount()-1);
                                break;
                            }

                        }
                    }

                }
            }

        }

    }
    @EventHandler
    public void onBuild(PlayerInteractEvent e){
        final ServerDatabase db = Earth.getInstance().getDatabase();
        if(e.getAction().equals(Action.LEFT_CLICK_BLOCK)
                && e.getClickedBlock().getType().equals(Material.CHEST)
                && e.getClickedBlock().getState() instanceof Container container){
            Inventory chestInventory = container.getInventory();

            for (ItemStack item : chestInventory.getContents()) {
                if (item != null && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    if(data.has(buildingIdKey)){

                        UUID bId = UUID.fromString(data.get(buildingIdKey, PersistentDataType.STRING));
                        Building building = db.getBuilding(bId);
                        if(building==null) break;
                        e.setCancelled(true);
                        MenuUtility pmu = new MenuUtility(e.getPlayer());
                        pmu.setBuilding(building);
                        e.getPlayer().openInventory(chestInventory);

                    }
                }
            }
        }
    }

    @EventHandler
    public void check2(InventoryDragEvent e){
        if(isIdea(e.getOldCursor())
                && e.getInventory().getHolder() != null
                && !e.getInventory().getHolder().equals(e.getWhoClicked().getInventory().getHolder())){
            e.setCancelled(true);
            for (Integer i : e.getInventorySlots()){
                if(!isIdeaDesc(e.getInventory().getItem(Math.max(0,i-9)))){
                    return;
                }
            }
            for (Integer i : e.getInventorySlots()){
                e.getInventory().setItem(i, Tools.createIdea());
                Tools.investIdea(e.getInventory().getItem(i-9));
            }
            ItemStack cursor = e.getOldCursor();
            cursor.setAmount(e.getOldCursor().getAmount()-e.getInventorySlots().size());
            Bukkit.getScheduler().runTask(Earth.getInstance(), () -> {
                // Устанавливаем предмет в курсор через View игрока
                e.getWhoClicked().setItemOnCursor(cursor);

                // Важно: обновляем инвентарь, чтобы клиент увидел изменения
                ((Player) e.getWhoClicked()).updateInventory();
            });


        }
//        for(Integer i : e.getInventorySlots()){
//            ItemStack item = e.getInventory().getItem(i-9);
//            if (isIdeaDesc(item)){
//
//            }
//        }
//        Bukkit.broadcastMessage(String.valueOf(e.getInventorySlots()));
    }
    @EventHandler
    public void onInventoryOpen(PlayerInteractEvent e) {
        final ServerDatabase db = Earth.getInstance().getDatabase();
        // Проверь, открыт ли сундук (Chest)

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK
                && e.getClickedBlock().getType().equals(Material.CHEST)
                && e.getClickedBlock().getState() instanceof Container container){
            Inventory chestInventory = container.getInventory();

            for (ItemStack item : chestInventory.getContents()) {
                if (item != null && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    PersistentDataContainer data = meta.getPersistentDataContainer();
                    if(data.has(buildingIdKey)){

                        UUID bId = UUID.fromString(data.get(buildingIdKey, PersistentDataType.STRING));
                        Building building = db.getBuilding(bId);
                        if(building == null) break;
                        e.setCancelled(true);
                        BuildingType type = building.getData().getType();
                        MenuUtility pmu = new MenuUtility(e.getPlayer());
                        pmu.setBuildingItem(item);
                        pmu.setBuilding(building);
                        switch (type) {
                            case MINE,PIT,QUARRY,LUMBER,WORKSHOP,PASTURE,FARM,FORGE,MANUFACTURE,FISHER ->{
                                new MiningBuildingMenu(pmu).open();
                            }
                            case LIBRARY,UNIVERSITY ->{
                                new ScienceBuildingMenu(pmu).open();
                            }
                            case MARKETPLACE,PORT ->{
                                new MarketplaceMenu(pmu).open();
                            }
                            case SHIPYARD -> {
                                new ShipyardMenu(pmu).open();

                            }
                            case BARRACK -> {
                                new BarrackMenu(pmu).open();
                            }
                            case STABLE -> {
                                new StableMenu(pmu).open();
                            }
                            case GUN_FACTORY -> {
                                new GunFactoryMenu(pmu).open();
                            }
                            default ->{
                                new DefaultBuildingMenu(pmu).open();
                            }
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e)  {

        if (e.getAction() != InventoryAction.PICKUP_ALL || e.getInventory().getType() != InventoryType.CHEST) {
            return;
        }

        ItemStack item = e.getCurrentItem();
        if (item == null || item.getType().isAir()) {
            return;
        }

        ItemMeta meta = item.getItemMeta();
        if (meta == null || meta.getLore() == null) {
            return;
        }
        ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
        List<String> lore = Objects.requireNonNull(itemMeta.getLore());
        String itemType = lore.get(0);
        if (itemType.equals("building") ){
            e.setCancelled(true);
            UUID buildingId = UUID.fromString(lore.get(1));
            String type = lore.get(2);
            if(this.earthPlugin.getDatabase().buildingExists(buildingId)){
                Building building = this.earthPlugin.getDatabase().getBuilding(buildingId);
                Player p = (Player) e.getWhoClicked();
                e.getWhoClicked().closeInventory();
                MenuUtility menuUtility = new MenuUtility(p);
                menuUtility.setBuilding(building);

                switch (type) {
                    case "mineV1":
                    case "mineV2":
                    case "career":
                    case "lumber":
                    case "pasture":
                    case "farm":
                    case "forge":
                    case "plant":
                    case "factory":
                        new MiningBuildingMenu(menuUtility).open();
                        break;
                    case "school":
                    case "university":
                        new ScienceBuildingMenu(menuUtility).open();
                        break;
                    case "landHub":
                    case "port":
                        break;
                    default:
                        p.sendMessage(ChatColor.RED + "Неизвестный тип здания: " + type);
                        break;
                }
            }

        }
    }

}
