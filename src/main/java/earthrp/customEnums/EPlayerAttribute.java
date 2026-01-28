package earthrp.customEnums;

import earthrp.Earth;
import lombok.Getter;

@Getter
public enum EPlayerAttribute {
    // Очки влияния (OI)
    TREASURY(0.0),
    POLIT_BALANCE(0.0),
    OI_BALANCE(0.0),
    MANPOWER(0.0),

    OI_INCOME(0.0),
    OI_SPENT(0.0),
    OI_INCOME_MOD(0.0),
    OI_FROM_BUILDING(0.0),

    // Политическая власть

    POLIT_INCOME(0.0),
    POLIT_INCOME_MOD(1.0),
    POLIT_MAX(0.0),
    POLIT_MAX_MOD(1.0),

    // Экономика и доходы
    INCOME(0.0),
    PROD_INCOME(0.0),
    PROD_MOD(1.0),
    GOODS_MOD(0.0),
    TRADE_INCOME(0.0),
    TRADE_MOD(1.0),
    TRADE_GOODS_MOD(1.0),
    TAX_INCOME(0.0),
    TAX_MOD(1.0),
    EXPENSE(0.0),

    CORRUPTION(0.0),
    INFLATION(0.0),
    INFLATION_REDUCE(0.0),
    TRIBUTE(0.0),
    TRIBUTE_MOD(1.0),

    // Флот
    FRIGATE_MOD(1.0),

    // Государственные показатели
    PEOPLE(0.0),
    WAR_SUPPORT(0.0),
    STABILITY(0.0),
    STAB_COST(1.0),
    WAR_STATUS(0.0),
    LEVIES_STATUS(0.0),
    CORE_CREATION_COST(1.0),
    ADMIN_EFFICIENCY(1.0),
    REVANCHISM(0.0),
    REVANCHISM_MOD(1.0),
    EXPAND_INFRASTRUCTURE_COST(1.0),
    BUILD_SITES(0.0),
    TECH_COST(1.0),
    IDEA_COST(1.0),

    // Строительство
    BUILDING_COST(1.0),
    SCIENCE_BUILDING_COST(1.0),
    WAR_BUILDING_COST(1.0),

    // Военные модификаторы (Общие)
    LIMIT_MOD(1.0),
    ARMY_EXPENSE(0.0),
    ARMY_EXPENSE_MOD(1.0),

    MANPOWER_LIMIT_MOD(1.0),
    MANPOWER_REC_MOD(0.1),
    DISCIPLE(1.0),
    TACTIC(Earth.getInstance().getConfig().getDouble("tacBase")),
    MORALE_MOD(1.0),
    MORALE_REDUCE(0.33),
    TROOPS_REDUCE(0.33),
    TRADITION(0.0),
    TRADITION_MOD(1.0),
    SIEGE_ABILITY(0.0),

    // Боевые фазы и урон
    FIRE_DAMAGE(1.0),
    FIRE_RESIST(1.0),
    SHOCK_DAMAGE(1.0),
    SHOCK_RESIST(1.0),
    MORALE_DAMAGE(1.0),
    MORALE_RESIST(1.0),

    // Типы войск (Стоимость и Боеспособность)
    INF_COST(1.0),
    INF_COMBAT_ABILITY(1.0),
    CAV_COST(1.0),
    CAV_RATIO(0.5),
    CAV_COMBAT_ABILITY(1.0),
    ART_COST(1.0),
    ART_COMBAT_ABILITY(1.0);


    private final double defaultValue;

    EPlayerAttribute(double defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static EPlayerAttribute fromString(String text) {
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
