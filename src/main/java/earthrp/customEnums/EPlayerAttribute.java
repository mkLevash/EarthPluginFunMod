package earthrp.customEnums;

import earthrp.Earth;
import lombok.Getter;

@Getter
public enum EPlayerAttribute {
    // Очки влияния (OI)
    TREASURY(0,"Казна"),
    POLIT_BALANCE(0,"Баланс полит власти"),
    OI_BALANCE(0,"Баланс ОИ"),
    MANPOWER(0,"Людской ресурс"),

    OI_INCOME(0,"Прирост ОИ"),
    OI_SPENT(0, "Потраченные ОИ"),
    OI_INCOME_MOD(0.0, "Модификатор прироста ОИ"),
    OI_FROM_BUILDING(1.0, "ОИ от зданий"),

    // Политическая власть
    POLIT_INCOME(0, "Прирост политической власти"),
    POLIT_INCOME_MOD(1.0, "Модификатор прироста политической власти"),
    POLIT_MAX(5, "Максимум политической власти"),
    POLIT_MAX_MOD(1.0, "Модификатор максимума политической власти"),

    // Экономика и доходы
    INCOME(0, "Базовый доход"),
    PROD_INCOME(0, "Базовый Доход от производства"),
    PROD_MOD(1.0, "Эффективность производства"),
    GOODS_MOD(0.0, "Модификатор производства товаров"),
    LUMBER_PROD(0.0, "Модификатор производства на лесопилках"),
    MINE_PROD(0.0, "Модификатор производства в шахтах"),
    TRADE_INCOME(0, "Базовый Доход от торговли"),
    TRADE_MOD(1.0, "Эффективность торговли"),

    TAX_INCOME(0, "Базовый Доход от налогов"),
    TAX_MOD(1.0, "Эффективность сбора налогов"),
    EXPENSE(0, "Расходы"),
    FARM_EFFICIENCY(0.0, "Эффективность ферм"),
    INTEREST(0.4, "Базовый процент",true),

    CORRUPTION(0, "Коррупция"),
    INFLATION(0, "Инфляция"),
    INFLATION_REDUCE(0, "Снижение инфляции", true),
    INFLATION_REDUCTION(0.1,"Повышение инфляции от долгов",true,true),
    TRIBUTE(0, "Дань плоская", true),
    TRIBUTE_PERCENTAGE(0.0, "Процентная дань", true),
    TRIBUTE_MOD(1.0, "Модификатор дани", true),


    TRADE_FRIGATE_MOD(1.0, "Эфф. торговли от фрегатов"),
    TRADE_GOODS_MOD(1.0, "Эфф. торговли от товаров"),
    TRADE_PEOPLE_MOD(1.0, "Эфф. торговли от населения"),
    TRADE_MONOPOLIZE_MOD(2.0, "Множитель эфф. торговли от монополии", false, true),

    // Государственные показатели
    WAR_SUPPORT(0, "Поддержка войны"),
    STABILITY(0, "Стабильность"),
    STAB_COST(1.0, "Стоимость повышения стабильности", true),
    TRUCE_BREAK_COST(3,"Снижение стабильности от разрыва мира", true),
    TRUCE_LENGTH(0,"Длительность перемирия", true),
    CORE_CREATION_COST(1.0, "Стоимость национализации провинции", true),
    ADMIN_EFFICIENCY(0, "Административная эффективность"),
    REVANCHISM(0, "Реваншизм"),
    REVANCHISM_MOD(1.0, "Модификатор реваншизма"),
    EXPAND_INFRASTRUCTURE_COST(1.0, "Стоимость расширения инфраструктуры", true),
    BUILD_SITES(0, "Доступные участки для застройки"),
    TECH_COST(1.0, "Стоимость технологий", true),
    IDEA_COST(1.0, "Стоимость идей", true),

    // Строительство
    BUILDING_COST(1.0, "Стоимость строительства", true),
    LIVING_BUILDING_COST(1.0, "Стоимость строительства жилых домов", true),
    SCIENCE_BUILDING_COST(1.0, "Стоимость научных зданий", true),
    WAR_BUILDING_COST(1.0, "Стоимость военных зданий", true),

    // Военные модификаторы (Общие)
    LIMIT_MOD(1.0, "Модификатор лимита армии"),
    ARMY_EXPENSE(0, "Содержание армии", true),
    ARMY_EXPENSE_MOD(1.0, "Модификатор содержания регуляров", true),

    MANPOWER_LIMIT_MOD(1.0, "Модификатор максимума рекрутов"),
    MANPOWER_REC_MOD(0.1, "Скорость восстановления рекрутов"),
    DISCIPLE(1.0, "Дисциплина"),
    TACTIC(Earth.getInstance().getConfig().getDouble("tacBase"), "Военная тактика"),
    CW(20, "Ширина фронта"),
    MORALE_MOD(1.0, "Модификатор морали"),
    MORALE_REDUCE(1.0, "Скорость восстановления морали"),
    TRADITION(0, "Армейские традиции"),
    TRADITION_MOD(1.0, "Модификатор армейских традиций"),
    MORALE_TRADITION(0.01, "Мораль от традиций"),
    SIEGE_ABILITY(0.0, "Базовая осада"),
    FORT_LVL(1,"Уровень крепости"),
    FORT_COST(1.0,"Стоимость крепости"),
    FORT_ABILITY(0.0,"Защита крепости"),
    ATTRITION_FOR_ENEMY(0.0,"Истощение противника"),
    ATTRITION_CHANCE(0.0,"Шанс истощения"),

    ARMY_SATIETY_MAX(1.0, "Максимум сытости армии"),
    ARMY_SATIETY(1.0, "Сытость армии"),
    ARMY_SUPPLY_MAX(1.0, "Максимум снабжения армии"),
    ARMY_SUPPLY(1.0, "Снабжение армии"),

    // Боевые фазы и урон
    FIRE_DAMAGE(1.0, "Урон в фазе огня"),
    FIRE_RESIST(1.0, "Снижение урона в фазе огня", true),
    SHOCK_DAMAGE(1.0, "Урон в фазе натиска"),
    SHOCK_RESIST(1.0, "Снижение урона в фазе натиска", true),
    MORALE_DAMAGE(1.0, "Урон по морали"),
    MORALE_RESIST(1.0, "Снижение урона по морали", true),

    LAND_MORALE(2.5, "Боевой дух армии"),
    NAVAL_MORALE(2.5, "Боевой дух флота"),

    // Типы войск (Стоимость и Боеспособность)
    INF_LVL(0,"Уровень пехоты"),
    CAV_LVL(0,"Уровень пехоты"),
    ART_LVL(0,"Уровень пехоты"),

    MERC_COST(1.0, "Стоимость наёмников", true),
    MERC_LIMIT(1.0,"Модификатор лимита наёмников"),
    MERC_DISC(0.0,"Дисциплина наёмников"),
    MERC_MORALE(0.0, "Мораль наёмников"),



    INF_COST(1.0, "Стоимость пехоты", true),
    INF_COMBAT_ABILITY(1.0, "Боеспособность пехоты"),
    INF_FIRE(0.0, "Огонь пехоты"),
    INF_SHOCK(0.0, "Натиск пехоты"),

    CAV_COST(1.0, "Стоимость кавалерии", true),
    CAV_RATIO(0.5, "Кавалерийское соотношение"),
    CAV_COMBAT_ABILITY(1.0, "Боеспособность кавалерии"),
    CAV_FIRE(0.0, "Огонь кавалерии"),
    CAV_SHOCK(0.0, "Натиск кавалерии"),

    ART_COST(1.0, "Стоимость артиллерии", true),
    ART_COMBAT_ABILITY(1.0, "Боеспособность артиллерии"),
    ART_FIRE(0.0, "Огонь артиллерии"),
    ART_SHOCK(0.0, "Натиск артиллерии");


    private final double defaultValue;

    private final String displayName;

    private final boolean negative;

    private final boolean flat;

    EPlayerAttribute(double defaultValue,String displayName) {

        this(defaultValue,displayName,false,false);
    }

    EPlayerAttribute(int defaultValue,String displayName) {

        this(defaultValue,displayName,false,true);
    }

    EPlayerAttribute(double defaultValue,String displayName, boolean negative) {

        this(defaultValue,displayName,negative,false);
    }

    EPlayerAttribute(int defaultValue,String displayName, boolean negative) {

        this(defaultValue,displayName,negative,true);
    }





    EPlayerAttribute(double defaultValue,String displayName,boolean negative, boolean flat) {

        this.defaultValue = defaultValue;
        this.displayName = displayName;
        this.negative = negative;
        this.flat = flat;
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
