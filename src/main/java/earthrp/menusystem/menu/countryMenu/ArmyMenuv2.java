package earthrp.menusystem.menu.countryMenu;

import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.UnitTech;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.PlayerModifier;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.MainMenu;
import earthrp.tools.Tools;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ArmyMenuv2 extends Menu {

    private final EPlayer player;
    private final Player p;

    public ArmyMenuv2(MenuUtility menuUtility) {
        super(menuUtility);
        this.player = menuUtility.getPlayer();
        this.p = menuUtility.getOwner();
    }

    @Override
    public String getMenuName() {
        return "Меню армии";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item == null) return;

        if (item.getType() == Material.BARRIER) {
            p.closeInventory();
            new MainMenu(menuUtility).open();
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        double tactic = player.getAttribute(EPlayerAttribute.TACTIC);
        double disciple = player.getAttribute(EPlayerAttribute.DISCIPLE);
        double finalTactic = Tools.round(tactic * disciple);



        // Мораль
        double tradition = player.getAttribute(EPlayerAttribute.TRADITION);
        List<String> moraleLore = new ArrayList<>();
        moraleLore.add("Традиции(<dark_green>"+(int) tradition+"<white>) " + color(Tools.getColorModLegacy( Tools.round(1.0 + (tradition * player.getAttribute(EPlayerAttribute.MORALE_TRADITION))))));

        Set<PlayerModifier> modifiers = player.getAttributeModifiers(EPlayerAttribute.MORALE_MOD);
        if (modifiers != null && !modifiers.isEmpty()) {
            for (PlayerModifier mod : modifiers){
                moraleLore.add((mod.getName() + mod.getColorValue(EPlayerAttribute.MORALE_MOD)));
            }
        }

        inventory.setItem(10, createStat(
                Material.EGG,
                "<green>Мораль " + color(Tools.getColorModLegacy(player.getMoraleMod())),
                "land_morale-1",
                moraleLore
        ));

        // Дисциплина
        List<String> discLore = new ArrayList<>();
        modifiers = player.getAttributeModifiers(EPlayerAttribute.DISCIPLE);
        if (modifiers != null && !modifiers.isEmpty()) {
            for (PlayerModifier mod : modifiers){
                discLore.add((mod.getName() + mod.getColorValue(EPlayerAttribute.DISCIPLE)));
            }
        }
        inventory.setItem(11, createStat(
                Material.EGG,
                "Дисциплина " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.DISCIPLE))),
                "discipline",
                discLore
        ));

        // Тактика
        inventory.setItem(12, createStat(
                Material.EGG,
                "Тактика("+color(Tools.getColorModLegacy(disciple))+")<green> " + finalTactic,
                "military_tactics",
                List.of(
                        "Технологии <aqua>" + Tools.round(tactic)
                )
        ));




        // Соотношение кавалерии
        inventory.setItem(19, createStat(
                Material.SADDLE,
                "<light_purple>Соотношение кавалерии",
                "cav_ratio",
                List.of(
                        "<white>Кавалерия <light_purple>"
                                + (int) Math.round(player.getAttribute(EPlayerAttribute.CAV_RATIO) * 100)
                                + "<white>%"
                )
        ));

        // Людской ресурс
        inventory.setItem(20, makeItem(
                Material.EGG,
                "Рекруты("+player.getMPLimitModColor()+") " + (int) player.getAttribute(EPlayerAttribute.MANPOWER) + "/<yellow>"  + player.getManpowerLimit(),
                "manpower",
                "manpower",
                List.of(
                        "Восстановление("+player.getMPIncreaseModColor()+") " + player.getManpowerIncrease() + "/день"
                )
        ));

        // Наёмники
        inventory.setItem(21, makeItem(
                Material.EGG,
                "Наёмники",
                "mercenary",
                "mercenary",
                List.of(
                        "Количество("+color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.MERC_LIMIT)))+") <light_purple>" + player.getMercAmount() + "<white>/<yellow>" + player.getMercLimit(),
                        "Содержание(" + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.MERC_COST),true)) + ") <dark_red>" + player.getMercExpense(),
                        "Мораль наёмников " + player.getMercMoraleColor(),
                        "Дисциплина наёмников "+player.getMercDiscColor()
                )
        ));

        // Урон в фазе огня
        inventory.setItem(16, createStat(
                Material.EGG,
                "<red>Урон в фазе огня",
                "fire_damage",
                List.of(
                        "<white>Модификатор " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.FIRE_DAMAGE)))
                )
        ));

        // Урон в фазе шока
        inventory.setItem(25, createStat(
                Material.EGG,
                "<gold>Урон в фазе шока",
                "shock_damage",
                List.of(
                        "<white>Модификатор " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.SHOCK_DAMAGE)))
                )
        ));

        // Урон по морали
        inventory.setItem(34, createStat(
                Material.EGG,
                "<dark_green>Урон по морали",
                "morale_damage",
                List.of(
                        "<white>Модификатор " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.MORALE_DAMAGE)))
                )
        ));

        // Сопротивление в фазе огня
        inventory.setItem(15, createStat(
                Material.EGG,
                "<red>Сопротивление в фазе огня",
                "fire_resist",
                List.of(
                        "<white>Модификатор " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.FIRE_RESIST)))
                )
        ));

        // Сопротивление в фазе шока
        inventory.setItem(24, createStat(
                Material.EGG,
                "<white>Сопротивление в фазе шока",
                "shock_resist",
                List.of(
                        "<white>Модификатор " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.SHOCK_RESIST)))
                )
        ));

        // Сопротивление урону по морали
        inventory.setItem(33, createStat(
                Material.EGG,
                "<green>Сопротивление урону по морали",
                "morale_resist",
                List.of(
                        "<white>Модификатор " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.MORALE_RESIST)))
                )
        ));

        // Боевая мощь пехоты
        inventory.setItem(14, createStat(
                Material.EGG,
                "Пехота",
                "inf",
                List.of(
                        "Количество <light_purple>" + troopCount("inf"),
                        "Содержание("+player.getArmyMaintenanceColor(UnitTech.UnitType.INF)+") <dark_red>" + player.getInfExpense(),
                        "Боевая мощь " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.INF_COMBAT_ABILITY)))


                )
        ));

        // Боевая мощь кавалерии
        inventory.setItem(23, createStat(
                Material.EGG,
                "Кавалерии",
                "cav",
                List.of(
                        "Количество <light_purple>" + troopCount("cav"),
                        "Содержание("+player.getArmyMaintenanceColor(UnitTech.UnitType.CAV)+") <dark_red>" + player.getCavExpense(),
                        "Боевая мощь " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.CAV_COMBAT_ABILITY)))
                )
        ));

        // Боевая мощь артиллерии
        inventory.setItem(32, createStat(
                Material.EGG,
                "Артиллерии",
                "art",
                List.of(
                        "Количество <light_purple>" + troopCount("art"),
                        "Содержание("+player.getArmyMaintenanceColor(UnitTech.UnitType.ART)+") <dark_red>" + player.getArtExpense(),
                        "Боевая мощь " + color(Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.ART_COMBAT_ABILITY)))
                )
        ));

//        // Стоимость пехоты
//        inventory.setItem(13, createStat(
//                Material.EGG,
//                "<gold>Стоимость пехоты",
//                "inf_cost",
//                List.of(
//
//
//                        "<white>Модификатор " + color(Tools.getColorMod(player.getAttribute(EPlayerAttribute.INF_COST), true))
//                )
//        ));
//
//        // Стоимость кавалерии
//        inventory.setItem(22, createStat(
//                Material.EGG,
//                "<yellow>Стоимость кавалерии",
//                "cav_cost",
//                List.of(
//                        "<white>Количество <light_purple>" + troopCount("cav"),
//                        "<white>Расход <dark_red>" + player.getCavExpense(),
//                        "<white>Модификатор " + color(Tools.getColorMod(player.getAttribute(EPlayerAttribute.CAV_COST), true))
//                )
//        ));
//
//        // Стоимость артиллерии
//        inventory.setItem(31, createStat(
//                Material.EGG,
//                "<gold>Стоимость артиллерии",
//                "art_cost",
//                List.of(
//                        "<white>Количество <light_purple>" + troopCount("art"),
//                        "<white>Расход <dark_red>" + player.getArtExpense(),
//                        "<white>Модификатор " + color(Tools.getColorMod(player.getAttribute(EPlayerAttribute.ART_COST), true))
//                )
//        ));

        // Заполнение пустых слотов
        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 35);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);


        // Кнопка назад
        inventory.setItem(40, createBackItem());
    }

    private ItemStack createStat(Material material, String displayName, String menuId, List<String> lore) {
        return makeItem(material, displayName, menuId, menuId.toLowerCase(), lore);
    }

    private int troopCount(String type) {
        if (player.getTroops() == null) {
            return 0;
        }

        List<?> units = player.getTroops().get(type);
        return units == null ? 0 : units.size();
    }

    /**
     * Если Tools.getColorMod(...) всё ещё возвращает legacy-формат (&a, §a),
     * этот метод сконвертирует его в MiniMessage.
     *
     * Если Tools.getColorMod(...) уже возвращает MiniMessage-теги,
     * метод просто вернёт строку без изменений.
     */
    private String color(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        if (input.indexOf('&') == -1 && input.indexOf('§') == -1) {
            return input;
        }

        return MiniMessage.miniMessage().serialize(
                LegacyComponentSerializer.legacyAmpersand()
                        .deserialize(input.replace('§', '&'))
        );
    }
}