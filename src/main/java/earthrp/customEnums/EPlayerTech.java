package earthrp.customEnums;

import lombok.Getter;

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


    // ==================== ЭПОХА 1: ФЕОДАЛИЗМ ====================
    FEUDALISM(-1, Set.of(TRIBAL)),

    BANK_BASE(1),
    BANNER(1),
    MOTTE(1),
    HORSE_RIDDING(1),
    TRADE(1),
    COPPER(1),
    ENGINEERING(1),
    LOWER_MEDIEVAL_MILITARY(1),
    SHIPBUILDING(1),
    IRON(1),
    EARLY_CARRACK(1),
    WORKSHOP(1),
    HIGH_MEDIEVAL_MILITARY(1),
    MEDIEVAL_ADMINISTRATION(1),
    CASTLE(1),

    // ==================== ЭПОХА 2: РЕНЕССАНС ====================
    RENAISSANCE(-1, Set.of(FEUDALISM)),

    UNIVERSITY(2, Set.of(WRITING)),
    BASTION(2),
    BANK_UP(2, Set.of(BANK_BASE)),
    GUNPOWDER(2),
    INF3(2, Set.of(GUNPOWDER)),
    CAV3(2, Set.of(GUNPOWDER)),
    PROFESSIONAL_ARMY(2),
    DOCK(2),
    EARLY_MODERN_ADMINISTRATION(2),
    PRINTING_PRESS(2),
    STAR_FORT(2),
    SPAIN_SQUARE(2),
    ART1(2),
    CAV4(2, Set.of(CAV3)),
    CHARTER_COMPANY(2),
    SEPARATION_POWER(2),
    BUREAUCRACY_BASE(2),

    // ==================== ЭПОХА 3: МАНУФАКТУРЫ ====================
    MANUFACTURE(-1, Set.of(RENAISSANCE)),

    FORTRESS(3),
    ENLIGHTENMENT(3),
    CONSTITUTION(3),
    BUREAUCRACY_UP(3),
    LINE_INFANTRY(3),
    CARTRIDGES(3),
    CAV5(3),

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
        if (dependence.isEmpty()) return true;

        // Проверяем, что ВСЕ зависимости из сета имеют значение true в мапе игрока
        return dependence.stream()
                .allMatch(dep -> playerTechs.getOrDefault(dep, dep.isResearched()));
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

}
