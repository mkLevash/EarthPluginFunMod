//package earthrp;
//
//import earthrp.customObjects.Town;
//import earthrp.database.ServerDatabase;
//import org.bukkit.Bukkit;
//import org.bukkit.Chunk;
//import org.bukkit.Location;
//import org.bukkit.Material;
//import org.bukkit.block.Biome;
//import org.bukkit.block.Block;
//import org.bukkit.util.Vector;
//import org.dynmap.DynmapAPI;
//import org.dynmap.markers.AreaMarker;
//import org.dynmap.markers.Marker;
//import org.dynmap.markers.MarkerAPI;
//import org.dynmap.markers.MarkerSet;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import static earthrp.PolygonUtils.chaikinSmoothing;
//import static earthrp.PolygonUtils.findOrderedBorderChunks;
//
//public class DynmapBorders {
//
//
//
//    public static boolean drawTownBorder(Location loc, int rad) {
//
//        DynmapAPI dynmap = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
//        if (dynmap == null) {
//            Bukkit.getLogger().warning("Dynmap не найден!");
//            return false;
//        }
//
//        MarkerAPI markerAPI = dynmap.getMarkerAPI();
//        if (markerAPI == null) {
//            Bukkit.getLogger().warning("Dynmap MarkerAPI not found!");
//            return false;
//        }
//
//        ServerDatabase db = Earth.getInstance().getServerDatabase();
//        MarkerSet markerSet = markerAPI.getMarkerSet("towns");
//        if (markerSet == null) {
//            markerSet = markerAPI.createMarkerSet("towns", "Towns", null, true);
//
//        }
//        String markerId = "town";
//        //List<Earth.ChunkPosition> borders = TerritoryGenerator.generateCountryShape(loc.getBlockX(), loc.getBlockZ(), 100);
//        String iconId = "star";
//        AreaMarker oldArea = markerSet.findAreaMarker(markerId);
//        if (oldArea != null) {
//            oldArea.deleteMarker();
//        }
//        int points = rad*3; // Чем больше точек, тем детальнее граница
//        double[] xArray = new double[points];
//        double[] zArray = new double[points];
//
//        double centerX = loc.getBlockX();
//        double centerZ = loc.getBlockZ();
//        double baseR = rad;
//
//        for (int i = 0; i < points; i++) {
//            double angle = 2 * Math.PI * i / points;
//
//            // Фрактальный шум из 3-х октав
//            double noise = 0;
//            noise += Math.sin(angle * 3) * 0.15;        // Крупные формы
//            noise += Math.sin(angle * 7 + 1.5) * 0.08;  // Средние неровности
//            noise += Math.sin(angle * 19) * 0.03;       // Мелкие детали
//
//            // Рандомизация для уникальности каждого города (сид)
//            String name = "Moscow";
//            double seed = name.hashCode() % 100;
//            noise += Math.sin(angle * 5 + seed) * 0.05;
//
//            double r = baseR * (1 + noise);
//
//            double x = centerX + r * Math.cos(angle);
//            double z = centerZ + r * Math.sin(angle);
//
//            int safetyLimit = 0;
//            while (isWater(x, z) && r > 5 && safetyLimit < baseR) {
//                r -= 1.0; // Смещаемся на 1 блок к центру
//                x = centerX + r * Math.cos(angle);
//                z = centerZ + r * Math.sin(angle);
//                safetyLimit++;
//            }
//
////            for (int it = 0; it < 3; it++) {
////                xArray = smooth(xArray);
////                zArray = smooth(zArray);
////            }
//
//            xArray[i] = x;
//            zArray[i] = z;
//
//        }
//
//        AreaMarker areaMarker = markerSet.createAreaMarker(
//                markerId,
//                markerId,
//                false,
//                "world",
//                xArray,
//                zArray,
//                true
//        );
//        areaMarker.setLineStyle(3, 1.0, 0xd0d4db);
//        areaMarker.setFillStyle(0.2, 0xd0d4db);
//        Marker marker = markerSet.findMarker(markerId);
//        if (marker != null){
//            marker.setLocation("world",loc.getBlockX(),64,loc.getBlockZ());
//            marker.setLabel("town");
//        }else{
//            markerSet.createMarker(markerId, "town", "world",
//                    loc.getBlockX(),64,loc.getBlockZ(),
//                    markerAPI.getMarkerIcon(iconId), true);
//        }
//        return true;
//    }
//
//    private static void smoothArray(double[] array) {
//        int n = array.length;
//        double[] temp = array.clone();
//        for (int i = 0; i < n; i++) {
//            int prev = (i - 1 + n) % n;
//            int next = (i + 1) % n;
//            // Берем 50% веса текущей точки и по 25% от соседей
//            array[i] = temp[prev] * 0.25 + temp[i] * 0.5 + temp[next] * 0.25;
//        }
//    }
//
//    private static Earth.ChunkPosition findNearestLand(double startX, double startZ, int maxRadius) {
//        // Проверяем блоки вокруг точки
//        for (int r = 1; r <= maxRadius; r++) {
//            // Проверяем по квадрату вокруг точки
//            for (int dx = -r; dx <= r; dx++) {
//                for (int dz = -r; dz <= r; dz++) {
//                    if (Math.abs(dx) != r && Math.abs(dz) != r) continue;
//
//                    int checkX = (int) (startX + dx);
//                    int checkZ = (int) (startZ + dz);
//
//                    if (!isWater(checkX, checkZ)) {
//                        return new Earth.ChunkPosition(checkX, checkZ, "world");
//                    }
//                }
//            }
//        }
//        return new Earth.ChunkPosition((int) startX, (int) startZ, "world"); // Суша не найдена
//    }
//
//    private static boolean isWater(double x, double z) {
//        Block block = Bukkit.getWorld("world").getHighestBlockAt((int)x, (int)z);
//        Material type = block.getType();
//
//        // Базовая проверка на воду
//        if (type == Material.WATER) return true;
//
//        // Проверка биома (океаны и реки — это "настоящая" вода)
//        Biome biome = block.getBiome();
//        return biome.name().contains("OCEAN") || biome.name().contains("RIVER");
//    }
//
//    public static boolean deleteTownBorder(Town town){
//        DynmapAPI dynmap = (DynmapAPI) Bukkit.getPluginManager().getPlugin("dynmap");
//        if (dynmap == null) {
//            Bukkit.getLogger().warning("Dynmap не найден!");
//            return false;
//        }
//
//        MarkerAPI markerAPI = dynmap.getMarkerAPI();
//        if (markerAPI == null) {
//            Bukkit.getLogger().warning("Dynmap MarkerAPI not found!");
//            return false;
//        }
//
//        ServerDatabase db = Earth.getInstance().getServerDatabase();
//        MarkerSet markerSet = markerAPI.getMarkerSet("towns");
//        if (markerSet == null) {
//            return false;
//        }
//        String markerId = "town_" + town.getUniqueId();
//        String world = town.getWorld();
//        AreaMarker townArea = markerSet.findAreaMarker(markerId);
//        if (townArea != null) {
//            townArea.deleteMarker();
//        }
//        return true;
//    }
//}
