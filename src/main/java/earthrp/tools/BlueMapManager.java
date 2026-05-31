package earthrp.tools;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.BlueMapMap;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import earthrp.Earth;
import earthrp.customObjects.Town;
import de.bluecolored.bluemap.api.markers.ExtrudeMarker;

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
    public void updateTownMarker(Town town) {
        // Проверяем, включен ли BlueMap прямо сейчас
        BlueMapAPI.getInstance().ifPresent(api -> {

            // Находим нужную карту BlueMap по названию игрового мира
            Optional<BlueMapMap> optMap = api.getMap(town.getWorld());
            if (optMap.isEmpty()) return;
            BlueMapMap map = optMap.get();

            // Получаем или создаем набор маркеров (слой на карте)
            MarkerSet markerSet = map.getMarkerSets().computeIfAbsent(MARKER_SET_ID, id ->
                    MarkerSet.builder().label(MARKER_SET_LABEL).build()
            );

            // Идентификатор маркера для конкретного города
            String markerId = "town_" + town.getUniqueId().toString();

            // Считаем крайние координаты границ города на основе радиуса (в блоках)
            // Радиус 10 чанков = 160 блоков во все стороны от центрального чанка
            int radiusBlocks = 10 * 16;
            int minX = town.getLocation().getBlockX() - radiusBlocks;
            int maxX = town.getLocation().getBlockX() + radiusBlocks + 16;
            int minZ = town.getLocation().getBlockZ() - radiusBlocks;
            int maxZ = town.getLocation().getBlockZ() + radiusBlocks + 16;

            // Строим прямоугольную форму привата (двумерный контур)
            Shape shape = Shape.createRect(minX, minZ, maxX, maxZ);

            // Настройки цвета (RGBA)
            Map<String,Integer> rgb = town.getOwner().getData().getRgb();
            Color borderAndWallColor = new Color(rgb.get("red"),rgb.get("green"), rgb.get("blue"), 0.25f); // Полупрозрачный зеленый

            // Создаем 3D-маркер (объемную зону привата)
            ExtrudeMarker townMarker = ExtrudeMarker.builder()
                    .label(town.getName())
                    .shape(shape, 10f, 120f) // Высота привата на карте: от 60 до 120 блока
                    .lineColor(borderAndWallColor)
                    .fillColor(borderAndWallColor)
                    .detail("<b>Город:</b> " + town.getName() + "<br><b>Правитель:</b> " + town.getOwnerName())
                    .build();

            // Добавляем маркер на слой. Если он там уже был, BlueMap его обновит
            markerSet.getMarkers().put(markerId, townMarker);
        });
    }

    /**
     * Удаляет приват города с карты при его расформировании.
     */
    public void removeTownMarker(Town town) {
        BlueMapAPI.getInstance().ifPresent(api -> {
            Optional<BlueMapMap> optMap = api.getMap(town.getWorld());
            if (optMap.isEmpty()) return;

            BlueMapMap map = optMap.get();
            MarkerSet markerSet = map.getMarkerSets().get(MARKER_SET_ID);

            if (markerSet != null) {
                String markerId = "town_" + town.getUniqueId().toString();
                markerSet.getMarkers().remove(markerId);
            }
        });
    }

    public void refreshAllTowns(java.util.Collection<Town> towns) {
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
}