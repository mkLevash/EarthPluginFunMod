//package earthrp.menusystem.menu.markets;
//
//import earthrp.*;
//
//import earthrp.customObjects.Town;
//import earthrp.database.ServerDatabase;
//import earthrp.menusystem.Menu;
//import earthrp.menusystem.MenuUtility;
//import earthrp.menusystem.menu.buildings.MiningBuildingMenu;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.concurrent.ConcurrentHashMap;
//
//public class NearbyTradeTownsMenu extends Menu {
//    private final Earth earthPlugin;
//    private final Map<Direction, TownDistancePair> nearbyTowns = menuUtility.getNearbyTowns();
//    Town currentTown = menuUtility.getTown();
//    private ServerDatabase db = Earth.getInstance().getServerDatabase();
//    private final Map<String, Town> townNames = new ConcurrentHashMap<>();
//
//
//    public NearbyTradeTownsMenu(MenuUtility menuUtility, Earth earthPlugin)  {
//        super(menuUtility);
//        this.earthPlugin = earthPlugin;
//    }
//
//    @Override
//    public String getMenuName() {
//        return "Ближайшие торговые города";
//    }
//
//    @Override
//    public int getSlots() {
//        return 27; // 3 строки для лучшего отображения
//    }
//
//    @Override
//    public void handleMenu(InventoryClickEvent e)  {
//        Player p = (Player) e.getWhoClicked();
//        ItemStack clickedItem = e.getCurrentItem();
//
//        if (clickedItem == null) return;
//
//        // Обработка нажатия на город
//        if (clickedItem.getType() == Material.MAP) {
//            String townName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
//            // Здесь можно добавить логику обработки выбора города
//            p.sendMessage(ChatColor.GREEN + "Вы выбрали город: " + townName);
//            UUID marketId = townNames.get(townName).getUuid();
//            //db.getMarket(marketId).updateGoods(1);
//
//            new MiningBuildingMenu(menuUtility,this.earthPlugin).open();
//        }
//
//
//        // Кнопка возврата
//        if (clickedItem.getType() == Material.BARRIER) {
//            new MiningBuildingMenu(menuUtility, earthPlugin).open();
//        }
//    }
//
//    @Override
//    public void setMenuItems() {
//
//        townNames.clear();
//        // Заполняем информацию о ближайших городах
//        // Расположение слотов по компасу (8 направлений вокруг центра)
//        // Слоты в инвентаре, расположенные по компасу (по часовой стрелке, начиная с северо-запада)
//        int[] directionSlots = {3, 4, 5, 12, 14, 21, 22, 23};
//
//// Порядок направлений, соответствующий слотам (важно сохранить эту последовательность!)
//        Direction[] compassDirections = {
//                Direction.NORTHWEST, // Слот 3 (северо-запад)
//                Direction.NORTH,      // Слот 4 (север)
//                Direction.NORTHEAST, // Слот 5 (северо-восток)
//                Direction.WEST,       // Слот 12 (запад)
//                Direction.EAST,       // Слот 14 (восток)
//                Direction.SOUTHWEST, // Слот 21 (юго-запад)
//                Direction.SOUTH,      // Слот 22 (юг)
//                Direction.SOUTHEAST  // Слот 23 (юго-восток)
//        };
//
//// Заполняем слоты в соответствии с направлениями
//
//        for (int i = 0; i < directionSlots.length; i++) {
//            Direction dir = compassDirections[i];
//            TownDistancePair pair = nearbyTowns.get(dir);
//            ItemStack townItem;
//
//            if (pair != null && pair.town != null) {
//
//
//                townNames.put(pair.town.getName(),pair.town);
//                // Создаем предмет для города
//                townItem = new ItemStack(Material.MAP, 1);
//                ItemMeta meta = townItem.getItemMeta();
//                meta.setDisplayName(ChatColor.YELLOW + pair.town.getName());
//                meta.setLore(List.of(
//                        ChatColor.WHITE + "Направление: " + dir,
//                        ChatColor.WHITE + "Расстояние: " + String.format("%.1f", pair.distance) + " чанков",
//                        ChatColor.GRAY + "(≈ " + (int)(pair.distance * 16) + " блоков)"
//                ));
//
//                townItem.setItemMeta(meta);
//            } else {
//                // Создаем заглушку для отсутствующего города
//                townItem = new ItemStack(Material.GRAY_DYE, 1);
//                ItemMeta meta = townItem.getItemMeta();
//
//                meta.setDisplayName(ChatColor.DARK_GRAY + "Нет городов");
//                meta.setLore(List.of(
//                        ChatColor.GRAY + "Направление: " + dir,
//                        ChatColor.GRAY + "Городов с рынками не обнаружено"
//                ));
//
//                townItem.setItemMeta(meta);
//            }
//
//            // Размещаем в соответствующем слоте
//            inventory.setItem(directionSlots[i], townItem);
//        }
//
//        // Центральный компас
//        ItemStack compass = new ItemStack(Material.COMPASS, 1);
//        ItemMeta compassMeta = compass.getItemMeta();
//        compassMeta.setDisplayName(ChatColor.GOLD + "Текущее положение");
//        if(currentTown.isLandHub()){
//            compassMeta.setLore(List.of(ChatColor.LIGHT_PURPLE + currentTown.getName(),
//                    ChatColor.GREEN + "Есть рынок") );
//        }else{
//            compassMeta.setLore(List.of(ChatColor.LIGHT_PURPLE + currentTown.getName(),
//                    ChatColor.RED + "Рынок отсутствует") );
//        }
//
//        compass.setItemMeta(compassMeta);
//        inventory.setItem(13, compass); // Центральный слот
//
//        // Кнопка возврата
//        ItemStack backButton = new ItemStack(Material.BARRIER, 1);
//        ItemMeta backMeta = backButton.getItemMeta();
//        backMeta.setDisplayName(ChatColor.RED + "Назад");
//        backButton.setItemMeta(backMeta);
//        inventory.setItem(15, backButton);
//
//        // Декоративные элементы
//        fillEmptySlots();
//    }
//
//    private void fillEmptySlots() {
//        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
//        ItemMeta fillerMeta = filler.getItemMeta();
//        fillerMeta.setDisplayName(" ");
//        filler.setItemMeta(fillerMeta);
//
//        for (int i = 0; i < inventory.getSize(); i++) {
//            if (inventory.getItem(i) == null) {
//                inventory.setItem(i, filler);
//            }
//        }
//    }
//
//}