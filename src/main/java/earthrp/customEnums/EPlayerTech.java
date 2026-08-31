package earthrp.customEnums;

import earthrp.configs.CustomConfig;
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
    BASE_MILITARY(0,Map.of(EPlayerAttribute.TACTIC,0.25,EPlayerAttribute.CW,5.0)),
    TRADE(0),

    // ==================== ЭПОХА 1: ФЕОДАЛИЗМ ====================
    FEUDALISM(-1, Set.of(TRIBAL),Map.of(EPlayerAttribute.POLIT_INCOME,1.0,EPlayerAttribute.POLIT_MAX,5.0)),

    MEDIEVAL_ADMINISTRATION(1,Map.of(EPlayerAttribute.ADMIN_EFFICIENCY,1.0)),

    BANK_BASE(1, Set.of(MEDIEVAL_ADMINISTRATION)),
    ENGINEERING(1,Map.of(EPlayerAttribute.SIEGE_ABILITY,0.1)),
    BANNER(1, Set.of(MEDIEVAL_ADMINISTRATION),Map.of(EPlayerAttribute.MANPOWER_REC_MOD,0.05)),

    CASTLE(1,Set.of(ENGINEERING),Map.of(EPlayerAttribute.FORT_LVL,1.0)),
    SHIPBUILDING(1,Set.of(ENGINEERING),Map.of(EPlayerAttribute.NAVAL_MORALE,0.5)),
    IRON(1),
    IRON_MINE(1,Set.of(IRON),Map.of(EPlayerAttribute.MINE_PROD,0.5)),
    IRON_LUMBER(1,Set.of(IRON),Map.of(EPlayerAttribute.LUMBER_PROD,0.5)),
    WORKSHOP(1,Set.of(ENGINEERING)),

    BASE_MEDIEVAL_MILITARY(1,Set.of(ENGINEERING,IRON),Map.of(EPlayerAttribute.TACTIC,0.25)),
    INF1(1,Set.of(BASE_MEDIEVAL_MILITARY),Map.of(EPlayerAttribute.INF_LVL,1.0)),
    CAV1(1,Set.of(BASE_MEDIEVAL_MILITARY),Map.of(EPlayerAttribute.CAV_LVL,1.0)),

    NEW_MEDIEVAL_MILITARY(1,Set.of(BASE_MEDIEVAL_MILITARY),Map.of(EPlayerAttribute.TACTIC,0.5,EPlayerAttribute.CW,5.0)),
    INF2(1,Set.of(NEW_MEDIEVAL_MILITARY,INF1),Map.of(EPlayerAttribute.INF_LVL,1.0)),
    CAV2(1,Set.of(NEW_MEDIEVAL_MILITARY,CAV1),Map.of(EPlayerAttribute.CAV_LVL,1.0)),

    // ==================== ЭПОХА 2: РЕНЕССАНС ====================
    RENAISSANCE(-1, Set.of(FEUDALISM),Map.of(EPlayerAttribute.OI_FROM_BUILDING,0.5)),

    BASTION(2,Map.of(EPlayerAttribute.FORT_LVL,1.0)),
    BANK_UP(2),


    PRINTING_PRESS(2,Map.of(EPlayerAttribute.TECH_COST,-0.1)),
    EARLY_MODERN_ADMINISTRATION(2,Set.of(PRINTING_PRESS)),
    UNIVERSITY(2,Set.of(PRINTING_PRESS)),
    SEPARATION_POWER(2,Set.of(PRINTING_PRESS)),
    BUREAUCRACY_BASE(2,Set.of(PRINTING_PRESS),Map.of(EPlayerAttribute.ADMIN_EFFICIENCY,1.0)),


    PROFESSIONAL_ARMY(2,Map.of(EPlayerAttribute.TACTIC,0.5)),
    INF3(2,Set.of(PROFESSIONAL_ARMY),Map.of(EPlayerAttribute.INF_LVL,1.0)),
    CAV3(2,Set.of(PROFESSIONAL_ARMY),Map.of(EPlayerAttribute.CAV_LVL,1.0)),



    SPAIN_SQUARE(2,Set.of(PROFESSIONAL_ARMY),Map.of(EPlayerAttribute.TACTIC,0.5,EPlayerAttribute.CW,5.0)),

    GUNPOWDER(2,Map.of(EPlayerAttribute.ART_LVL,1.0)),
    INF4(2,Set.of(GUNPOWDER,SPAIN_SQUARE,INF3),Map.of(EPlayerAttribute.INF_LVL,1.0)),
    CAV4(2,Set.of(SPAIN_SQUARE,CAV3),Map.of(EPlayerAttribute.CAV_LVL,1.0)),
    ART2(2,Set.of(GUNPOWDER),Map.of(EPlayerAttribute.ART_LVL,1.0)),


    // ==================== ЭПОХА 3: МАНУФАКТУРЫ ====================
    MANUFACTURE(-1, Set.of(RENAISSANCE)),

    NATIONAL_BANK(3),
    FORTRESS(3,Map.of(EPlayerAttribute.FORT_LVL,1.0)),
    ENLIGHTENMENT(3),
    CONSTITUTION(3,Set.of(ENLIGHTENMENT)),
    BUREAUCRACY_UP(3,Set.of(ENLIGHTENMENT)),
    LINE_INFANTRY(3,Map.of(EPlayerAttribute.TACTIC,0.5)),
    INF5(3,Set.of(LINE_INFANTRY),Map.of(EPlayerAttribute.INF_LVL,1.0)),
    CAV5(3,Set.of(LINE_INFANTRY),Map.of(EPlayerAttribute.CAV_LVL,1.0)),
    ART3(3,Set.of(LINE_INFANTRY),Map.of(EPlayerAttribute.ART_LVL,1.0)),

    IMPULSE_WARFARE(3,Set.of(LINE_INFANTRY),Map.of(EPlayerAttribute.TACTIC,0.5,EPlayerAttribute.CW,5.0)),

    INF6(3,Set.of(IMPULSE_WARFARE,INF5),Map.of(EPlayerAttribute.INF_LVL,1.0)),
    CAV6(3,Set.of(IMPULSE_WARFARE,CAV5),Map.of(EPlayerAttribute.CAV_LVL,1.0)),
    ART4(3,Set.of(IMPULSE_WARFARE,ART3),Map.of(EPlayerAttribute.ART_LVL,1.0)),

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

    public String getName(){
        return CustomConfig.get().getString("tech.name."+this, "techName");
    }


    private static Component colorText(String text){

        return MiniMessage.miniMessage().deserialize("<!italic><white>" + text);
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
