package earthrp.customEnums;

import earthrp.files.CustomConfig;
import earthrp.tools.Tools;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Getter
public enum EPlayerTech {

    // ==================== ЭПОХА 0: ПЛЕМЯ ====================
    TRIBAL(-1, true),
    MINING(0),
    BUILDING(0),
    IRRIGATION(0),
    LIVESTOCK(0),
    WRITING(0),
    SHIPPING(0),
    FORT(0),
    LUMBER(0),
    HORSE_RIDDING(0,Set.of(LIVESTOCK)),
    BASE_MILITARY(0,Set.of(HORSE_RIDDING),Map.of(EPlayerAttribute.TACTIC,0.1,EPlayerAttribute.LAND_MORALE,0.5)),
    TRADE(0),

    // ==================== ЭПОХА 1: ФЕОДАЛИЗМ ====================
    FEUDALISM(-1, Set.of(TRIBAL)),

    BANK_BASE(1),
    ENGINEERING(1,Map.of(EPlayerAttribute.SIEGE_ABILITY,0.1)),
    BANNER(1,Map.of(EPlayerAttribute.MANPOWER_REC_MOD,0.05)),

    CASTLE(1,Set.of(ENGINEERING)),
    SHIPBUILDING(1,Set.of(ENGINEERING),Map.of(EPlayerAttribute.NAVAL_MORALE,0.5)),
    IRON(1),
    WORKSHOP(1,Set.of(ENGINEERING)),
    MEDIEVAL_MILITARY(1,Set.of(ENGINEERING,IRON),Map.of(EPlayerAttribute.TACTIC,0.25,EPlayerAttribute.LAND_MORALE,0.5)),

    // ==================== ЭПОХА 2: РЕНЕССАНС ====================
    RENAISSANCE(-1, Set.of(FEUDALISM),Map.of(EPlayerAttribute.OI_FROM_BUILDING,0.5)),

    BASTION(2),
    BANK_UP(2),
    DOCK(2,Map.of(EPlayerAttribute.NAVAL_MORALE,1.0)),

    PRINTING_PRESS(2,Map.of(EPlayerAttribute.TECH_COST,-0.1)),
    EARLY_MODERN_ADMINISTRATION(2,Set.of(PRINTING_PRESS)),
    UNIVERSITY(2,Set.of(PRINTING_PRESS)),
    SEPARATION_POWER(2,Set.of(PRINTING_PRESS)),
    BUREAUCRACY_BASE(2,Set.of(PRINTING_PRESS),Map.of(EPlayerAttribute.ADMIN_EFFICIENCY,1.0)),


    PROFESSIONAL_ARMY(2,Map.of(EPlayerAttribute.TACTIC,0.15,EPlayerAttribute.LAND_MORALE,1.0)),
    INF3(2,Set.of(PROFESSIONAL_ARMY)),
    CAV3(2,Set.of(PROFESSIONAL_ARMY)),



    SPAIN_SQUARE(2,Set.of(PROFESSIONAL_ARMY),Map.of(EPlayerAttribute.TACTIC,0.5)),

    GUNPOWDER(2),
    INF4(2,Set.of(GUNPOWDER,SPAIN_SQUARE,INF3)),
    CAV4(2,Set.of(SPAIN_SQUARE,CAV3)),
    ART2(2,Set.of(GUNPOWDER)),


    // ==================== ЭПОХА 3: МАНУФАКТУРЫ ====================
    MANUFACTURE(-1, Set.of(RENAISSANCE)),

    FORTRESS(3),
    ENLIGHTENMENT(3),
    CONSTITUTION(3,Set.of(ENLIGHTENMENT)),
    BUREAUCRACY_UP(3,Set.of(ENLIGHTENMENT)),
    LINE_INFANTRY(3),
    INF5(3,Set.of(LINE_INFANTRY)),
    CAV5(3,Set.of(LINE_INFANTRY)),
    ART3(3,Set.of(LINE_INFANTRY)),

    INDUSTRIAL(-1, Set.of(MANUFACTURE));


    private final boolean researched;
    private final Set<EPlayerTech> dependence;
    private final Map<EPlayerAttribute, Double> effect;
    private final int lvl;



    EPlayerTech(int lvl, boolean researched) {
        this.researched = researched;
        this.lvl = lvl;
        dependence = Set.of();
        effect = Map.of();
    }

    EPlayerTech(int lvl, Set<EPlayerTech> dependence, Map<EPlayerAttribute, Double> effect) {
        researched = false;
        this.lvl = lvl;
        this.dependence = dependence;
        this.effect = effect;
    }

    EPlayerTech(int lvl, Map<EPlayerAttribute, Double> effect) {
        researched = false;
        this.lvl = lvl;
        this.dependence = Set.of();
        this.effect = effect;
    }

    EPlayerTech(int lvl, Set<EPlayerTech> dependence) {
        researched = false;
        this.lvl = lvl;
        this.dependence = dependence;
        effect = Map.of();
    }

    EPlayerTech(int lvl) {
        this(lvl, false);
    }


    public boolean canResearch(Map<EPlayerTech, Boolean> playerTechs) {
        boolean age = true;
        switch (this.lvl){
            case 1 -> {
                age = playerTechs.getOrDefault(FEUDALISM,false);
            }
            case 2 -> {
                age = playerTechs.getOrDefault(RENAISSANCE,false);
            }
            case 3 -> {
                age = playerTechs.getOrDefault(MANUFACTURE,false);
            }
        }

        if (dependence.isEmpty()) return age;

        // Проверяем, что ВСЕ зависимости из сета имеют значение true в мапе игрока
        return age && dependence.stream()
                .allMatch(dep -> playerTechs.getOrDefault(dep, dep.isResearched()));
    }

    public boolean canRefund(Map<EPlayerTech, Boolean> playerTechs) {

        // Проверяем, что не исследована следующая эпоха
        switch (this.lvl){
            case 0 -> {
                return !playerTechs.getOrDefault(FEUDALISM,false);
            }
            case 1 -> {
                return !playerTechs.getOrDefault(RENAISSANCE,false);
            }
            case 2 -> {
                return !playerTechs.getOrDefault(MANUFACTURE,false);
            }
            case 3 -> {
                return true;
            }
        }
        return false;
    }

    public List<Component> getLore(Map<EPlayerTech, Boolean> playerTechs) {
        List<Component> lore = new ArrayList<>();
        List<String> techLore = CustomConfig.get().getStringList("tech.lore."+ this);

        for(String s:techLore){
            lore.add(colorText(s));
        }
        boolean flag = true;
        for (EPlayerTech dep : dependence){
            if(!playerTechs.getOrDefault(dep, dep.isResearched())){
                flag = false;
                break;
            }
        }
        if (dependence.isEmpty() || flag) return lore;
        lore.add(colorText(" "));
        lore.add(colorText("<gray>Необходимы следующие технологии:"));
        for (EPlayerTech dep : dependence){
            if(playerTechs.getOrDefault(dep, dep.isResearched())){
                lore.add(colorText("<green>" + CustomConfig.get().getString("tech.name."+dep, "techName")));
            }
            else{
                lore.add(colorText("<red>" + CustomConfig.get().getString("tech.name."+dep, "techName")));
            };
        }
        return lore;
    }


    private static Component colorText(String text){

        return MiniMessage.miniMessage().deserialize("<!italic>" + text);
    }



    public static EPlayerTech fromString(String text) {
        if (text == null || text.isEmpty()) return null;

        // 1. Сначала вставляем подчеркивания перед заглавными буквами (camelCase -> snake_case)
        String formatted = text.replaceAll("([a-z])([A-Z])", "$1_$2");

        // 2. Убираем лишние пробелы и переводим в верхний регистр
        formatted = formatted.replace(" ", "_").toUpperCase();

        try {
            return valueOf(formatted);
        } catch (IllegalArgumentException e) {
            // Если даже после форматирования не нашли (например, опечатка)
            return null;
        }
    }

    @Override
    public String toString() {
        // Получаем стандартное имя константы (например, VIDEO_PLAYER)
        String name = this.name().toLowerCase();

        // Если в названии нет подчеркиваний, просто возвращаем в нижнем регистре
        if (!name.contains("_")) {
            return name;
        }

        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;

        for (char c : name.toCharArray()) {
            if (c == '_') {
                // Флаг: следующий символ должен быть в верхнем регистре
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(c));
                    nextUpper = false;
                } else {
                    result.append(c);
                }
            }
        }

        return result.toString();
    }

}
