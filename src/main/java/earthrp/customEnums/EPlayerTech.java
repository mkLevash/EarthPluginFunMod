package earthrp.customEnums;

import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
public enum EPlayerTech {

    BANK_BASE(),
    BANK_UP(Set.of(BANK_BASE)),
    TRADE(),
    SHIPPING(),
    RAILROAD(),
    DIPLOMACY(),
    OFFICE_BASE(),
    OFFICE_UP(Set.of(OFFICE_BASE)),
    SCHOOL(),
    UNIVERSITY(Set.of(SCHOOL)),
    MINISTRY(Set.of(OFFICE_UP)),
    ADMIN_EFFICIENCY(Set.of(OFFICE_UP),Map.of(EPlayerAttribute.ADMIN_EFFICIENCY,4.0)),

    PASTURE(),
    LUMBER(),


    FORGE(),
    SHIPYARD(Set.of(SHIPPING)),
    MANUFACTURE(),
    FACTORY(Set.of(MANUFACTURE)),
    OFFICE_MIL(Set.of(OFFICE_BASE)),
    FORT(),
    LEVIES(Map.of(EPlayerAttribute.MANPOWER_REC_MOD,0.1)),
    SIEGE(Map.of(EPlayerAttribute.SIEGE_ABILITY,0.1)),
    METAL_PCG(Map.of(EPlayerAttribute.SHOCK_RESIST,0.15)),
    STANDARD(Map.of(EPlayerAttribute.FIRE_DAMAGE,0.10)),
    HEAVY_CAV(Map.of(EPlayerAttribute.CAV_COMBAT_ABILITY,0.25)),
    GUNPOWDER(),

    MINE(),
    PIT(Set.of(MINE)),
    QUARRY(Set.of(PIT,GUNPOWDER)),

    INF1(true),
    INF2(),
    INF3(Set.of(GUNPOWDER,INF2)),
    INF4(Set.of(INF3)),
    CAV1(true),
    CAV2(),
    CAV3(Set.of(GUNPOWDER,CAV2)),
    CAV4(Set.of(CAV3)),
    ART1(Set.of(GUNPOWDER)),
    ART2(Set.of(ART1));


    private final boolean researched;
    private final Set<EPlayerTech> dependence;
    private final Map<EPlayerAttribute, Double> effect;



    EPlayerTech(boolean researched) {
        this.researched = researched;
        dependence = Set.of();
        effect = Map.of();
    }

    EPlayerTech(Set<EPlayerTech> dependence, Map<EPlayerAttribute, Double> effect) {
        researched = false;
        this.dependence = dependence;
        this.effect = effect;
    }

    EPlayerTech(Map<EPlayerAttribute, Double> effect) {
        researched = false;
        this.dependence = Set.of();
        this.effect = effect;
    }

    EPlayerTech(Set<EPlayerTech> dependence) {
        researched = false;
        this.dependence = dependence;
        effect = Map.of();
    }

    EPlayerTech() {
        this(false);
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
