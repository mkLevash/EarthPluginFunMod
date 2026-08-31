package earthrp.tools;

import com.flowpowered.math.vector.Vector2d;
import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import earthrp.Earth;
import earthrp.customObjects.Town;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;
import earthrp.tools.maps.CityBoundaryCalculator;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlueMapManager {


    private static final String MARKER_SET_ID = "earthrp_towns";
    private static final String MARKER_SET_LABEL = "Города";

    public BlueMapManager() {


        // Регистрируем слушатель включения BlueMap API
        BlueMapAPI.onEnable(api -> {
            Earth.getInstance().getLogger().info("Успешно подключено к BlueMap API!");
            // При перезагрузке плагина можно обновить все города
            // refreshAllTowns();
        });
    }

    /**
     * Отрисовывает или обновляет приват города на карте.
     */
//    public void updateTownMarker(Town town) {
//        // Проверяем, включен ли BlueMap прямо сейчас
//        BlueMapAPI.getInstance().ifPresent(api -> {
//
//            // Находим нужную карту BlueMap по названию игрового мира
//            Optional<BlueMapMap> optMap = api.getMap(town.getWorld());
//            if (optMap.isEmpty()) return;
//            BlueMapMap map = optMap.get();
//
//            // Получаем или создаем набор маркеров (слой на карте)
//            MarkerSet markerSet = map.getMarkerSets().computeIfAbsent(MARKER_SET_ID, id ->
//                    MarkerSet.builder().label(MARKER_SET_LABEL).build()
//            );
//
//            // Идентификатор маркера для конкретного города
//            String markerId = "town_" + town.getUniqueId().toString();
//
//            // Считаем крайние координаты границ города на основе радиуса (в блоках)
//            int radiusBlocks = Earth.getInstance().getConfig().getInt("townSize") * 16;
//            int minX = town.getLocation().getBlockX() - radiusBlocks;
//            int maxX = town.getLocation().getBlockX() + radiusBlocks + 16;
//            int minZ = town.getLocation().getBlockZ() - radiusBlocks;
//            int maxZ = town.getLocation().getBlockZ() + radiusBlocks + 16;
//
//            // Строим прямоугольную форму привата
//            Shape shape = Shape.createRect(minX,minZ,maxX,maxZ);
//
//            // Определение цвета и описания в зависимости от статуса оккупации
//            Color zoneColor;
//            String htmlDetails;
//
//            // Предположим, у Town есть метод получения статуса оккупации.
//            // ЗАМЕНИ эти методы на свои актуальные методы из класса Town!
//            if (town.isSiege()) { // Предположим, у тебя есть статус осады
//                // Делаем регион статично-красным/оранжевым во время осады
//                zoneColor = new Color(255, 120, 0, 0.4f);
//
//                // Добавляем HTML/CSS для мигающего индикатора
//                htmlDetails = "<b>Город:</b> " + town.getName() + "<br>" +
//                        "<b>Правитель:</b> " + town.getOwnerName() + "<br>" +
//                        "<b>Подконтролен:</b> " + town.getController().getCountryName() + "<br>" +
//                        "<b>Осаждает:</b> " + town.getSieger().getOwner().getCountryName() + "<br>" +
//                        "<div style='display: flex; align-items: center; margin-top: 5px;'>" +
//                        "  <span style='" +
//                        "    width: 10px; height: 10px; " +
//                        "    background-color: red; " +
//                        "    border-radius: 50%; " +
//                        "    margin-right: 8px; " +
//                        "    animation: blink 1s infinite alternate;" +
//                        "  '></span>" +
//                        "  <b style='color: red;'>ИДЕТ ОСАДА!</b>" +
//                        "</div>" +
//                        // Внедряем сам CSS-эффект мигания (плавное затухание)
//                        "<style>" +
//                        "  @keyframes blink { 0% { opacity: 0.2; } 100% { opacity: 1; } }" +
//                        "</style>";
//            }else if (town.isOccupied()) {
//                // Если город оккупирован: делаем его, например, тревожно-красным (RGBA)
//                zoneColor = new Color(255, 0, 0, 0.4f);
//
//                htmlDetails = "<b>Город:</b> " + town.getName() + "<br>" +
//                        "<b>Правитель:</b> " + town.getOwnerName() + "<br>" +
//                        "<b style='color: red;'>ОККУПИРОВАН:</b> " + town.getOccupierName();
//            }
//            else {
//                // Если город свободен: берем стандартный цвет нации/города
//                Map<String, Integer> rgb = town.getOwner().getData().getRgb();
//                zoneColor = new Color(rgb.get("red"), rgb.get("green"), rgb.get("blue"), 0.25f);
//
//                htmlDetails = "<b>Город:</b> " + town.getName() + "<br>" +
//                        "<b>Правитель:</b> " + town.getOwnerName();
//            }
//
//            // Создаем или обновляем 3D-маркер
//            ExtrudeMarker townMarker = ExtrudeMarker.builder()
//                    .label(town.getName() + (town.isOccupied() ? " (Оккупирован)" : ""))
//                    .shape(shape, 10f, 120f) // Высота привата
//                    .lineColor(zoneColor)
//                    .fillColor(zoneColor)
//                    .detail(htmlDetails)
//                    .build();
//
//            // Добавляем/обновляем маркер на слое
//            markerSet.getMarkers().put(markerId, townMarker);
//        });
//    }

    /**
     * Удаляет приват города с карты при его расформировании.
     */
    public void removeTownMarker(Town town) {
        BlueMapAPI.getInstance().ifPresent(api -> {
            Optional<BlueMapMap> optMap = api.getMap(town.getLocation().getWorld().getName());
            if (optMap.isEmpty()) return;

            BlueMapMap map = optMap.get();
            MarkerSet markerSet = map.getMarkerSets().get(MARKER_SET_ID);

            if (markerSet != null) {
                String markerId = "town_" + town.getUniqueId().toString();
                markerSet.getMarkers().remove(markerId);
            }
        });
    }

    public void refreshAllTowns() {
        java.util.Collection<Town> towns = Earth.getInstance().getDatabase().getTowns();
        // Ждем, пока BlueMap API станет доступен (на случай, если плагины включаются одновременно)
        de.bluecolored.bluemap.api.BlueMapAPI.getInstance().ifPresentOrElse(api -> {
            Earth.getInstance().getLogger().info("Запуск массовой отрисовки маркеров городов в BlueMap...");

            for (Town town : towns) {
                // Вызываем твой текущий метод отрисовки (почанковый или круговой)
                updateTownMarker(town);
            }

            Earth.getInstance().getLogger().info("Отрисовано городов на карте: " + towns.size());
        }, () -> {
            // Если при старте BlueMap еще не прогрузился, регистрируем отложенное действие
            de.bluecolored.bluemap.api.BlueMapAPI.onEnable(api -> {
                for (Town town : towns) {
                    updateTownMarker(town);
                }
            });
        });
    }

    public void updateTownMarker(Town town) {
        // Проверяем, включен ли BlueMap прямо сейчас
        BlueMapAPI.getInstance().ifPresent(api -> {

            // Находим нужную карту BlueMap по названию игрового мира
            Optional<BlueMapMap> optMap = api.getMap(town.getLocation().getWorld().getName());
            if (optMap.isEmpty()) return;
            BlueMapMap map = optMap.get();

            // Получаем или создаем набор маркеров (слой на карте)
            MarkerSet markerSet = map.getMarkerSets().computeIfAbsent(MARKER_SET_ID, id ->
                    MarkerSet.builder().label(MARKER_SET_LABEL).build()
            );

            // Идентификатор маркера для конкретного города
            String markerId = "town_" + town.getUniqueId().toString();



            // Строим прямоугольную форму привата

            Shape shape = createCustomShape(CityBoundaryCalculator.getBoundary(town.getData().getChunk()));

            // Определение цвета и описания в зависимости от статуса оккупации
            Color zoneColor;
            String htmlDetails;

            // Предположим, у Town есть метод получения статуса оккупации.
            // ЗАМЕНИ эти методы на свои актуальные методы из класса Town!
            if (town.isSiege()) { // Предположим, у тебя есть статус осады
                // Делаем регион статично-красным/оранжевым во время осады
                zoneColor = new Color(255, 120, 0, 0.4f);

                // Добавляем HTML/CSS для мигающего индикатора
                htmlDetails = "<b>Город:</b> " + town.getName() + "<br>" +
                        "<b>Правитель:</b> " + town.getOwnerName() + "<br>" +
                        "<b>Подконтролен:</b> " + town.getController().getCountryName() + "<br>" +
                        "<b>Осаждает:</b> " + town.getSieger().getOwner().getCountryName() + "<br>" +
                        "<div style='display: flex; align-items: center; margin-top: 5px;'>" +
                        "  <span style='" +
                        "    width: 10px; height: 10px; " +
                        "    background-color: red; " +
                        "    border-radius: 50%; " +
                        "    margin-right: 8px; " +
                        "    animation: blink 1s infinite alternate;" +
                        "  '></span>" +
                        "  <b style='color: red;'>ИДЕТ ОСАДА!</b>" +
                        "</div>" +
                        // Внедряем сам CSS-эффект мигания (плавное затухание)
                        "<style>" +
                        "  @keyframes blink { 0% { opacity: 0.2; } 100% { opacity: 1; } }" +
                        "</style>";
            }else if (town.isOccupied()) {
                // Если город оккупирован: делаем его, например, тревожно-красным (RGBA)
                zoneColor = new Color(255, 0, 0, 0.4f);

                htmlDetails = "<b>Город:</b> " + town.getName() + "<br>" +
                        "<b>Правитель:</b> " + town.getOwnerName() + "<br>" +
                        "<b style='color: red;'>ОККУПИРОВАН:</b> " + town.getOccupierName();
            }
            else {
                // Если город свободен: берем стандартный цвет нации/города
                Map<String, Integer> rgb = town.getOwner().getData().getRgb();
                zoneColor = new Color(rgb.get("red"), rgb.get("green"), rgb.get("blue"), 0.25f);

                htmlDetails = "<b>Город:</b> " + town.getName() + "<br>" +
                        "<b>Правитель:</b> " + town.getOwnerName();
            }

            // Создаем или обновляем 3D-маркер
            ExtrudeMarker townMarker = ExtrudeMarker.builder()
                    .label(town.getName() + (town.isOccupied() ? " (Оккупирован)" : ""))
                    .shape(shape, 10f, 120f) // Высота привата
                    .lineColor(zoneColor)
                    .fillColor(zoneColor)
                    .detail(htmlDetails)
                    .build();

            // Добавляем/обновляем маркер на слое
            markerSet.getMarkers().put(markerId, townMarker);
        });
    }

    public void onTownOccupationChanged(Town town) {
        // Просто перерисовываем маркер, логика внутри updateTownMarker сама всё поймет
        updateTownMarker(town);
    }

    public Shape createCustomShape(List<CityBoundaryCalculator.chunkPoint> points) {
        Vector2d[] vertices = new Vector2d[points.size()];
        for (int i = 0; i < points.size(); i++) {
            CityBoundaryCalculator.chunkPoint p = points.get(i);
            // Добавляем точки города
            vertices[i] = new Vector2d(p.x(), p.z());
        }
        return new Shape(vertices);
    }


}