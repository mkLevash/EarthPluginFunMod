//package earthrp.menusystem.menu.markets;
//
//import earthrp.Earth;
//import earthrp.customObjects.Town;
//import earthrp.menusystem.Menu;
//import earthrp.menusystem.MenuUtility;
//import earthrp.menusystem.menu.buildings.MiningBuildingMenu;
//import org.bukkit.Bukkit;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//import static earthrp.Pathfinding.findClosestMarketTownsByDirection;
//
//public class LoadingMenu extends Menu {
//    private final Earth earthPlugin;
//    Town town = menuUtility.getTown();
//
//    public LoadingMenu(MenuUtility menuUtility, Earth earthPlugin) {
//        super(menuUtility);
//        this.earthPlugin = earthPlugin;
//    }
//
//    @Override
//    public String getMenuName() {
//        return "Поиск ближайших городов...";
//    }
//
//    @Override
//    public int getSlots() {
//        return 9;
//    }
//
//    @Override
//    public void handleMenu(InventoryClickEvent e) {
//        // Блокируем любые клики во время загрузки
//        e.setCancelled(true);
//    }
//
//    @Override
//    public void setMenuItems() {
//        // Анимация загрузки
//        ItemStack loadingItem = new ItemStack(Material.COMPASS);
//        ItemMeta meta = loadingItem.getItemMeta();
//        meta.setDisplayName(ChatColor.YELLOW + "Идет поиск городов...");
//        meta.setLore(List.of(
//                ChatColor.GRAY + "Пожалуйста, подождите",
//                ChatColor.GRAY + "Это может занять несколько секунд"
//        ));
//        loadingItem.setItemMeta(meta);
//
//        for (int i = 0; i < getSlots(); i++) {
//            inventory.setItem(i, loadingItem);
//        }
//
//        // Запускаем асинхронный поиск городов
//        findTownsAsync();
//    }
//
//    private void findTownsAsync() {
//        CompletableFuture.supplyAsync(() -> {
//            Town currentTown = town;
//            List<Town> allTowns = new ArrayList<>();
//
//            return findClosestMarketTownsByDirection(currentTown, allTowns);
//        }).thenAcceptAsync(result -> {
//            // Возвращаемся в главный поток для открытия меню
//            Bukkit.getScheduler().runTask(earthPlugin, () -> {
//                menuUtility.setNearbyTowns(result);
//                new NearbyTradeTownsMenu(menuUtility, earthPlugin).open();
//            });
//        }).exceptionally(e -> {
//            Bukkit.getScheduler().runTask(earthPlugin, () -> {
//                Player player = menuUtility.getOwner();
//                player.sendMessage(ChatColor.RED + "Ошибка при поиске городов: " + e.getMessage());
//                new MiningBuildingMenu(menuUtility, earthPlugin).open();
//            });
//            return null;
//        });
//    }
//}