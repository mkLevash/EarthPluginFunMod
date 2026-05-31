package earthrp.listeners;

import earthrp.tools.Tools;
import earthrp.customObjects.Building;
import earthrp.Earth;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.buildings.MiningBuildingMenu;
import earthrp.menusystem.menu.buildings.ScienceBuildingMenu;
import earthrp.menusystem.menu.buildings.landHubMenu;
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

    private boolean isIdeaDesc(ItemStack item){
        return item!=null && item.hasItemMeta() &&  item.getItemMeta().getPersistentDataContainer().has(ideaEffectIdKey);
    }


    private boolean isCorrPlaceEvent(InventoryClickEvent e){

        InventoryAction a = e.getAction();
        boolean b1 = e.getClickedInventory()!=null
                && e.getClickedInventory().getHolder() != null
                && !e.getClickedInventory().getHolder().equals(e.getWhoClicked().getInventory().getHolder())
                && (a.equals(InventoryAction.PLACE_SOME) || a.equals(InventoryAction.PLACE_ALL) || a.equals(InventoryAction.PLACE_ONE));
        if(!b1) return false;

        return isIdeaDesc(e.getInventory().getItem(Math.max(0,e.getRawSlot()-9)))
                && isIdea(e.getCursor());
    }

    private boolean isCorrPickEvent(InventoryClickEvent e){

        InventoryAction a = e.getAction();
        boolean b1 = e.getClickedInventory() != null
                && e.getClickedInventory().getHolder() !=null
                && !e.getClickedInventory().getHolder().equals(e.getWhoClicked().getInventory().getHolder())
                && (a.equals(InventoryAction.PICKUP_ALL) || a.equals(InventoryAction.PICKUP_HALF) || a.equals(InventoryAction.PICKUP_ONE) || a.equals(InventoryAction.PICKUP_SOME));
        if(!b1) return false;

        return isIdeaDesc(e.getInventory().getItem(Math.max(0,e.getRawSlot()-9)))
                && isIdea(e.getCurrentItem());
    }



    @EventHandler
    public void blockIdea(InventoryClickEvent e){
        if(e.getCurrentItem() != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(ideaEffectIdKey)){
            e.setCancelled(true);
        }
        if(e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR) && isIdea(e.getCursor()) && !e.getInventory().getHolder().equals(e.getWhoClicked().getInventory().getHolder())){
            e.setCancelled(true);
        };
    }



    @EventHandler
    public void investIdea(InventoryClickEvent e){

        if(isCorrPlaceEvent(e)){
            e.setCancelled(true);
            if(e.getCurrentItem().getAmount()==0){
                e.getInventory().setItem(e.getRawSlot(), Tools.createIdea());
                ItemStack item = e.getCursor();
                item.setAmount(item.getAmount()-1);
                Tools.investIdea(e.getClickedInventory().getItem(e.getRawSlot()-9));
            }
        }
        if(isCorrPickEvent(e)){

            Tools.backIdea(e.getClickedInventory().getItem(e.getRawSlot()-9));
        }

    }
    @EventHandler
    public void onBuild(PlayerInteractEvent e){
        final ServerDatabase db = Earth.getInstance().getServerDatabase();
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
                        String type = building.getType();
                        MenuUtility pmu = new MenuUtility(e.getPlayer());
                        pmu.setBuilding(building);
                        switch (type) {
                            case "mineV1":
                            case "mineV2":
                            case "career":
                            case "lumber":
                            case "pasture":
                            case "farm":
                            case "plant":
                            case "factory":
                            case "university":
                            case "school":
                            case "landHub":
                            case "port":
                            case "barrack":
                            case "stable":
                            case "gunFactory":
                            case "fort":
                            case "forge":
                            case "shipyard":
                                e.getPlayer().openInventory(chestInventory);
                                break;
                            default:
                                e.getPlayer().sendMessage(ChatColor.RED + "Неизвестный тип здания: " + type);
                                break;
                        }

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
        final ServerDatabase db = Earth.getInstance().getServerDatabase();
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
                        String type = building.getType();
                        MenuUtility pmu = new MenuUtility(e.getPlayer());
                        pmu.setBuilding(building);
                        switch (type) {
                            case "mineV1","mineV2","career","lumber","factory","pasture","farm","forge","plant" ->{
                                new MiningBuildingMenu(pmu).open();
                            }
                            case "school","university" ->{
                                new ScienceBuildingMenu(pmu, this.earthPlugin).open();
                            }
                            case "landHub","port","barrack","stable","gunFactory","fort","shipyard" ->{
                                new landHubMenu(pmu, this.earthPlugin).open();
                            }
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e)  {

        boolean bool = e.getAction().equals(InventoryAction.PICKUP_ALL) && String.valueOf(e.getInventory().getType()).equals("CHEST") && Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getLore() != null;
        if (bool){
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            List<String> lore = Objects.requireNonNull(itemMeta.getLore());
            String itemType = lore.get(0);
            if (itemType.equals("building") ){
                e.setCancelled(true);
                UUID buildingId = UUID.fromString(lore.get(1));
                String type = lore.get(2);
                if(this.earthPlugin.getServerDatabase().buildingExists(buildingId)){
                    Building building = this.earthPlugin.getServerDatabase().getBuilding(buildingId);
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
                            new ScienceBuildingMenu(menuUtility, this.earthPlugin).open();
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

}
