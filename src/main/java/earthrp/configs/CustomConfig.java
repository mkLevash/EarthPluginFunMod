package earthrp.configs;

import earthrp.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class CustomConfig {

    private final static CustomConfig instance = new CustomConfig();

    private static File file;
    private static FileConfiguration customFile;

    //генерация файла конфига
    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Earth").getDataFolder(), "EarthConfig.yml");

        if (!file.exists()){
            try{
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        customFile = YamlConfiguration.loadConfiguration(file);

        // Копируем default значения в файл
        customFile.options().copyDefaults(true);

        get().addDefault("status.mora","off");
        get().addDefault("status.day",1);

        get().addDefault("tech.lore.tribal",List.of(
                "<white>Полит Власть:",
                "<white>Прирост 0£",
                "<white>Максимум 5£",
                "<white>Админ. эфф. 0अ"
        ));

        get().addDefault("tech.cost.feudalism",15);
        get().addDefault("tech.name.feudalism","Феодализм");
        get().addDefault("tech.lore.feudalism",List.of(
                "<white>Полит Власть:",
                "<white>Прирост 1£",
                "<white>Максимум 10£"
        ));


        get().addDefault("tech.cost.renaissance",50);
        get().addDefault("tech.name.renaissance","Ренессанс");
        get().addDefault("tech.lore.renaissance",List.of(
                "<gray>Прирост <aqua>ОИ<gray> от научных зданий <green>+0.5"
        ));

        get().addDefault("tech.cost.manufacture",100);
        get().addDefault("tech.name.manufacture","Мануфактуры");
        get().addDefault("tech.lore.manufacture",List.of(
                "<gray>Открывает <aqua>мануфактура, карьер"
        ));

        // ==================== ЭПОХА 0: ПЛЕМЯ ====================
        get().addDefault("tech.cost.mining",5);
        get().addDefault("tech.name.mining","Горное дело");
        get().addDefault("tech.lore.mining",List.of(
                "<gray>Позволяет строить <gold>шахты"
        ));

        get().addDefault("tech.cost.building",5);
        get().addDefault("tech.name.building","Каменная кладка");
        get().addDefault("tech.lore.building",List.of(
                "<gray>Позволяет строить <gold>амбар"
        ));

        get().addDefault("tech.cost.fort",5);
        get().addDefault("tech.name.fort","Древние стены");
        get().addDefault("tech.lore.fort",List.of(
                "<gray>Позволяет строить <gold>крепость"
        ));

        get().addDefault("tech.cost.shipping",5);
        get().addDefault("tech.name.shipping","Мореплавание");
        get().addDefault("tech.lore.shipping",List.of(
                "<gray>Позволяет строить <gold>порт"
        ));

        get().addDefault("tech.cost.trade",5);
        get().addDefault("tech.name.trade","Торговля");
        get().addDefault("tech.lore.trade",List.of(
                "<gray>Позволяет строить <gold>рынок"
        ));

        get().addDefault("tech.cost.irrigation",5);
        get().addDefault("tech.name.irrigation","Орошение");
        get().addDefault("tech.lore.irrigation",List.of(
                "<gray>Позволяет строить <gold>грядка"
        ));

        get().addDefault("tech.cost.livestock",5);
        get().addDefault("tech.name.livestock","Животноводство");
        get().addDefault("tech.lore.livestock",List.of(
                "<gray>Позволяет строить <gold>пастбище",
                "<gray>Позволяет нанимать <aqua>кавалерию"
        ));

        get().addDefault("tech.cost.writing",5);
        get().addDefault("tech.name.writing","Письменность");
        get().addDefault("tech.lore.writing",List.of(
                "<gray>Позволяет строить <gold>библиотека"
        ));

        get().addDefault("tech.cost.lumber",5);
        get().addDefault("tech.name.lumber","Лесоповал");
        get().addDefault("tech.lore.lumber",List.of(
                "<gray>Позволяет строить <gold>застава лесорубов"
        ));

        get().addDefault("tech.cost.baseMilitary",15);
        get().addDefault("tech.name.baseMilitary","base military");
        get().addDefault("tech.lore.baseMilitary",List.of(
                "<white>Тактика <green>+0.25"));

        get().addDefault("tech.cost.horseRidding",5);
        get().addDefault("tech.name.horseRidding","Верховая езда");
        get().addDefault("tech.lore.horseRidding",List.of(
                "<gray>Позволяет нанимать <aqua>лёгких всадников"
        ));

        get().addDefault("tech.name.inf0","Копейщики");
        get().addDefault("tech.lore.inf0",List.of(
                "Моральᠩ<dark_green>2.0",
                "Урон - <red>0.25<white>/<gold>0.20",
                "Очки <red>0<white>/<gold>0<white>/<dark_green>0"
        ));

        get().addDefault("tech.name.cav0","Легкие конные лучники");
        get().addDefault("tech.lore.cav0",List.of(
                "Моральᠩ<dark_green>2.0",
                "Урон - <red>0.0<white>/<gold>0.8",
                "Очки <red>0<white>/<gold>0<white>/<dark_green>0"
        ));

        // ==================== ЭПОХА 1: ФЕОДАЛИЗМ ====================


        get().addDefault("tech.cost.bankBase",10);
        get().addDefault("tech.name.bankBase","Ростовщики");
        get().addDefault("tech.lore.bankBase",List.of(
                "<gray>Позволяет брать долги",
                "<white>1 <gray>долг - <green>5<white>$<gray> под <white>40%"
        ));

        get().addDefault("tech.cost.banner",10);
        get().addDefault("tech.name.banner","Система призыва");
        get().addDefault("tech.lore.banner",List.of(
                "<gray>Позволяет поднимать <aqua>ополчение",
                "<gray>Восстановление рекрутов <green>+5 %"
        ));


        get().addDefault("tech.cost.engineering",15);
        get().addDefault("tech.name.engineering","Инженерное дело");
        get().addDefault("tech.lore.engineering",List.of(
                "<gray>Позволяет проводить <aqua>штурм крепости",
                "<gray>Стартовый прогресс осады <green>+15%"
        ));



        get().addDefault("tech.cost.shipbuilding",10);
        get().addDefault("tech.name.shipbuilding","Базовое судостроение");
        get().addDefault("tech.lore.shipbuilding",List.of(
                "<gray>Позволяет строить <gold>верфи"
        ));

        get().addDefault("tech.cost.iron",15);
        get().addDefault("tech.name.iron","Обработка металла");
        get().addDefault("tech.lore.iron",List.of(
                "<gray>Позволяет строить <gold>кузня"
        ));

        get().addDefault("tech.cost.ironLumber",10);
        get().addDefault("tech.name.ironLumber","Металлические топоры");
        get().addDefault("tech.lore.ironLumber",List.of(
                "<gray>Производительность лесорубов<green>+0.5"
        ));

        get().addDefault("tech.cost.ironMine",10);
        get().addDefault("tech.name.ironMine","Металлические кирки");
        get().addDefault("tech.lore.ironMine",List.of(
                "<gray>Производительность шахт<green>+0.25"
        ));

        get().addDefault("tech.cost.earlyCarrack",10);
        get().addDefault("tech.name.earlyCarrack","Средневековое судостроение");
        get().addDefault("tech.lore.earlyCarrack",List.of(
        ));

        get().addDefault("tech.cost.workshop",10);
        get().addDefault("tech.name.workshop","Мастерские");
        get().addDefault("tech.lore.workshop",List.of(
                "<gray>Позволяет строить <gold>мастерские"
        ));

        get().addDefault("tech.cost.baseMedievalMilitary",15);
        get().addDefault("tech.name.baseMedievalMilitary","base medieval military");
        get().addDefault("tech.lore.baseMedievalMilitary",List.of(
                "<gray>Позволяет строить <gold>казармы конюшни",
                "<aqua>Тактика <green>+0.25"
        ));

        get().addDefault("tech.cost.inf1",10);
        get().addDefault("tech.name.inf1","Пехота 1 ур");
        get().addDefault("tech.lore.inf1",List.of(
                "Моральᠩ<dark_green>2.5",
                "Урон - <red>0.35<white>/<gold>0.5",
                "Очки <red>0<white>/<gold>1<white>/<dark_green>0"
        ));

        get().addDefault("tech.cost.cav1",10);
        get().addDefault("tech.name.cav1","Кавалерия 1 ур");
        get().addDefault("tech.lore.cav1",List.of(
                "Моральᠩ<dark_green>2.5",
                "Урон - <red>0.0<white>/<gold>1.0",
                "Очки <red>0<white>/<gold>2<white>/<dark_green>1"
        ));

        get().addDefault("tech.cost.newMedievalMilitary",20);
        get().addDefault("tech.name.newMedievalMilitary","new medieval military");
        get().addDefault("tech.lore.newMedievalMilitary",List.of(
                "<aqua>Тактика <green>+0.50"
        ));

        get().addDefault("tech.cost.inf2",10);
        get().addDefault("tech.name.inf2","Пехота 2 ур");
        get().addDefault("tech.lore.inf2",List.of(
                "Моральᠩ<dark_green>3.0",
                "Урон - <red>0.8<white>/<gold>0.95",
                "Очки <red>0<white>/<gold>1<white>/<dark_green>1"
        ));

        get().addDefault("tech.cost.cav2",10);
        get().addDefault("tech.name.cav2","Кавалерия 2 ур");
        get().addDefault("tech.lore.cav2",List.of(
                "Моральᠩ<dark_green>3.0",
                "Урон - <red>0.0<white>/<gold>2.0",
                "Очки <red>0<white>/<gold>3<white>/<dark_green>1"
        ));

        get().addDefault("tech.cost.medievalAdministration",15);
        get().addDefault("tech.name.medievalAdministration","Средневековая администрация");
        get().addDefault("tech.lore.medievalAdministration",List.of(
                "<gray>Админ. эффективность <green>+1<white>अ"
        ));

        get().addDefault("tech.cost.castle",10);
        get().addDefault("tech.name.castle","Замки");
        get().addDefault("tech.lore.castle",List.of(
                "<gray>Воентех крепостей = <green>2"
        ));

      // ==================== ЭПОХА 2: РЕНЕССАНС ====================

        get().addDefault("tech.cost.university",20);
        get().addDefault("tech.name.university","Университет");
        get().addDefault("tech.lore.university",List.of(
                "<gray>Позволяет строить <gold>Университет"
        ));

        get().addDefault("tech.cost.bastion",20);
        get().addDefault("tech.name.bastion","Бастион");
        get().addDefault("tech.lore.bastion",List.of(
                "<gray>Воентех крепостей = <green>3"
        ));

        get().addDefault("tech.cost.bankUp",20);
        get().addDefault("tech.name.bankUp","Банковское дело");
        get().addDefault("tech.lore.bankUp",List.of(
                "<gray>Процент по долгам <green>-20%",
                "<gray>Размер 1 долга <green>+5<white>$"
        ));

        get().addDefault("tech.cost.gunpowder",30);
        get().addDefault("tech.name.gunpowder","Порох");
        get().addDefault("tech.lore.gunpowder",List.of(
                "<gray>Позволяет строить <aqua>оружейная фабрика</aqua>,<aqua>рудник",
                "Пушки <green>1<white> ур",
                "Моральᠩ<dark_green>4.0",
                "Урон - <red>1.0<white>/<gold>0.05",
                "Очки <red>1<white>/<gold>0<white>/<dark_green>2"
        ));

        get().addDefault("tech.cost.professionalArmy",30);
        get().addDefault("tech.name.professionalArmy","Профессиональная армия");
        get().addDefault("tech.lore.professionalArmy",List.of(
                "<aqua>Тактика <green>+0.50"
        ));

        get().addDefault("tech.cost.inf3",15);
        get().addDefault("tech.name.inf3","Аркебузиры");
        get().addDefault("tech.lore.inf3",List.of(
                "Моральᠩ<dark_green>4.0",
                "Урон - <red>1.6<white>/<gold>1.15",
                "Очки <red>2<white>/<gold>1<white>/<dark_green>2"
        ));

        get().addDefault("tech.cost.cav3",15);
        get().addDefault("tech.name.cav3","Кавалерия 3 ур");
        get().addDefault("tech.lore.cav3",List.of(
                "Моральᠩ<dark_green>4.0",
                "Урон - <red>0.5<white>/<gold>3.0",
                "Очки <red>0<white>/<gold>3<white>/<dark_green>3"
        ));


        get().addDefault("tech.cost.earlyModernAdministration",20);
        get().addDefault("tech.name.earlyModernAdministration","Администрация Нового времени");
        get().addDefault("tech.lore.earlyModernAdministration",List.of(
                "<gray>Позволяет строить <aqua>cуд"
        ));

        get().addDefault("tech.cost.printingPress",30);
        get().addDefault("tech.name.printingPress","Печатный станок");
        get().addDefault("tech.lore.printingPress",List.of(
                "<gray>Стоимость технологий <green>-10%"
        ));

        get().addDefault("tech.cost.spainSquare",50);
        get().addDefault("tech.name.spainSquare","Испанское каре");
        get().addDefault("tech.lore.spainSquare",List.of(
                "<aqua>Тактика <green>+0.5"
        ));

        get().addDefault("tech.name.art1","Пушки 1 ур");
        get().addDefault("tech.lore.art1",List.of(
                "Моральᠩ<dark_green>4.0",
                "Урон - <red>2.4<white>/<gold>0.05",
                "Очки <red>1<white>/<gold>0<white>/<dark_green>1"
        ));

        get().addDefault("tech.cost.art2",20);
        get().addDefault("tech.name.art2","Стандартизация калибров");
        get().addDefault("tech.lore.art2",List.of(
                "Моральᠩ<dark_green>4.5",
                "Урон - <red>4.4<white>/<gold>0.35",
                "Очки <red>2<white>/<gold>1<white>/<dark_green>2"
        ));

        get().addDefault("tech.cost.inf4",20);
        get().addDefault("tech.name.inf4","Пехота 4 ур");
        get().addDefault("tech.lore.inf4",List.of(
                "Моральᠩ<dark_green>4.5",
                "Урон - <red>1.6<white>/<gold>1.65",
                "Очки <red>2<white>/<gold>1<white>/<dark_green>3"
        ));

        get().addDefault("tech.cost.cav4",20);
        get().addDefault("tech.name.cav4","Кавалерия 4 ур");
        get().addDefault("tech.lore.cav4",List.of(
                "Моральᠩ<dark_green>4.5",
                "Урон - <red>0.5<white>/<gold>3.0",
                "Очки <red>0<white>/<gold>4<white>/<dark_green>4"
        ));


        get().addDefault("tech.cost.separationPower",20);
        get().addDefault("tech.name.separationPower","Разделение властей");
        get().addDefault("tech.lore.separationPower",List.of(
                "<gray>Позволяет менять <light_purple>Форму Правления"
        ));

        get().addDefault("tech.cost.bureaucracyBase",20);
        get().addDefault("tech.name.bureaucracyBase","Базовая бюрократия");
        get().addDefault("tech.lore.bureaucracyBase",List.of(
                "<gray>Админ. эффективность <green>+1"
        ));

        // ==================== ЭПОХА 3: МАНУФАКТУРЫ ====================


        get().addDefault("tech.cost.fortress",30);
        get().addDefault("tech.name.fortress","Цитадель");
        get().addDefault("tech.lore.fortress",List.of(
                "<gray>Открывает крепость 5 уровня"
        ));

        get().addDefault("tech.cost.enlightenment",30);
        get().addDefault("tech.name.enlightenment","Просвещение");
        get().addDefault("tech.lore.enlightenment",List.of(
                "<gray>Снижает требование для университетов в <green>2</green> раза"
        ));

        get().addDefault("tech.cost.constitution",30);
        get().addDefault("tech.name.constitution","Конституция");
        get().addDefault("tech.lore.constitution",List.of(
                "<white>Макс £ <green>+5"
        ));

        get().addDefault("tech.cost.bureaucracyUp",30);
        get().addDefault("tech.name.bureaucracyUp","Развитая бюрократия");
        get().addDefault("tech.lore.bureaucracyUp",List.of(
                "<gray>Стоимость национализации <green>-50%"
        ));

        get().addDefault("tech.cost.lineInfantry",50);
        get().addDefault("tech.name.lineInfantry","Линейное построение");
        get().addDefault("tech.lore.lineInfantry",List.of(
                "<aqua>Тактика <green>+0.5"
        ));

        get().addDefault("tech.cost.inf5",20);
        get().addDefault("tech.name.inf5","Патроны");
        get().addDefault("tech.lore.inf5",List.of(
                "Моральᠩ<dark_green>5.5",
                "Урон - <red>2.1<white>/<gold>1.65",
                "Очки <red>3<white>/<gold>1<white>/<dark_green>4"
        ));

        get().addDefault("tech.cost.cav5",20);
        get().addDefault("tech.name.cav5","Разведка");
        get().addDefault("tech.lore.cav5",List.of(
                "Моральᠩ<dark_green>5.5",
                "Урон - <red>1.0<white>/<gold>4.0",
                "Очки <red>1<white>/<gold>5<white>/<dark_green>5"
        ));

        get().addDefault("tech.cost.art3",20);
        get().addDefault("tech.name.art3","Артиллерия 3 ур");
        get().addDefault("tech.lore.art3",List.of(
                "Моральᠩ<dark_green>5.5",
                "Урон - <red>6.4<white>/<gold>0.45",
                "Очки <red>4<white>/<gold>1<white>/<dark_green>5"
        ));

        get().addDefault("tech.cost.impulseWarfare",50);
        get().addDefault("tech.name.impulseWarfare","Impulse Warfare");
        get().addDefault("tech.lore.impulseWarfare",List.of(
                "<aqua>Тактика <green>+0.5"
        ));

        get().addDefault("tech.cost.inf6",20);
        get().addDefault("tech.name.inf6","Пехота 6 ур");
        get().addDefault("tech.lore.inf6",List.of(
                "Моральᠩ<dark_green>6.0",
                "Урон - <red>3.1<white>/<gold>2.15",
                "Очки <red>4<white>/<gold>3<white>/<dark_green>3"
        ));

        get().addDefault("tech.cost.cav6",20);
        get().addDefault("tech.name.cav6","Кавалерия 6 ур");
        get().addDefault("tech.lore.cav6",List.of(
                "Моральᠩ<dark_green>6.0",
                "Урон - <red>1.0<white>/<gold>5.0",
                "Очки <red>1<white>/<gold>6<white>/<dark_green>5"
        ));

        get().addDefault("tech.cost.art4",20);
        get().addDefault("tech.name.art4","Артиллерия 4 ур");
        get().addDefault("tech.lore.art4",List.of(
                "Моральᠩ<dark_green>5.5",
                "Урон - <red>8.4<white>/<gold>0.55",
                "Очки <red>5<white>/<gold>4<white>/<dark_green>6"
        ));

        get().addDefault("tech.cost.nationalBank",30);
        get().addDefault("tech.name.nationalBank","Национальный банк");
        get().addDefault("tech.lore.nationalBank",List.of(
                "<gray>Позволяет строить <gold>нац банк"
        ));

        get().addDefault("trade.playerName.playerName",true);

        get().addDefault("deletedTowns", List.of(""));

        // Сохраняем конфиг с default значениями


        get().addDefault("ideas.admin.idea0.name", "Stable Government");
        get().addDefault("ideas.admin.idea0.desc", List.of("Стоимость стабильности <green>-25%"));
        get().addDefault("ideas.admin.idea0.effectsId", List.of("stabCost"));
        get().addDefault("ideas.admin.idea0.effects", List.of(-0.25));

        get().addDefault("ideas.admin.idea4.name", "Adaptability");
        get().addDefault("ideas.admin.idea4.desc", List.of("Административная эффективность <green>+1<white>"));
        get().addDefault("ideas.admin.idea4.effectsId", List.of("adminEfficiency"));
        get().addDefault("ideas.admin.idea4.effects", List.of(1.0));

        get().addDefault("ideas.admin.idea2.name", "Cultural Regulation");
        get().addDefault("ideas.admin.idea2.desc", List.of("Стоимость национализации <green>-25%"));
        get().addDefault("ideas.admin.idea2.effectsId", List.of("coreCreationCost"));
        get().addDefault("ideas.admin.idea2.effects", List.of(-0.25));

        get().addDefault("ideas.admin.idea3.name", "Centralization");
        get().addDefault("ideas.admin.idea3.desc", List.of("Стоимость повышения инфраструктуры <green>-50%"));
        get().addDefault("ideas.admin.idea3.effectsId", List.of("expandInfrastructureCost"));
        get().addDefault("ideas.admin.idea3.effects", List.of(-0.5));

        get().addDefault("ideas.admin.idea1.name", "Locale Rule");
        get().addDefault("ideas.admin.idea1.desc", List.of("Количество спец. зданий в городе <green>+1"));
        get().addDefault("ideas.admin.idea1.effectsId", List.of("buildSites"));
        get().addDefault("ideas.admin.idea1.effects", List.of(1.0));
        
        

        get().addDefault("ideas.economic.idea0.name", "National Bank");
        get().addDefault("ideas.economic.idea0.desc", List.of("Доход от налогов <green>+25%"));
        get().addDefault("ideas.economic.idea0.effectsId", List.of("taxMod"));
        get().addDefault("ideas.economic.idea0.effects", List.of(0.25));

        get().addDefault("ideas.economic.idea1.name", "Bureaucracy");
        get().addDefault("ideas.economic.idea1.desc", List.of("Стоимость строительства <green>-25%"));
        get().addDefault("ideas.economic.idea1.effectsId", List.of("buildingCost"));
        get().addDefault("ideas.economic.idea1.effects", List.of(-0.25));

        get().addDefault("ideas.economic.idea2.name", "Debt and Loans");
        get().addDefault("ideas.economic.idea2.desc", List.of("Содержание армии <green>-15%"));
        get().addDefault("ideas.economic.idea2.effectsId", List.of("armyExpenseMod"));
        get().addDefault("ideas.economic.idea2.effects", List.of(-0.05));

        get().addDefault("ideas.economic.idea3.name", "Efficient Mining");
        get().addDefault("ideas.economic.idea3.desc", List.of(
                "Ежедневное снижение инфляции <green>-1%",
                "Повышение инфляции от долгов <green>-0.05"
        ));
        get().addDefault("ideas.economic.idea3.effectsId", List.of("inflationReduce","inflationReduction"));
        get().addDefault("ideas.economic.idea3.effects", List.of(-1.0,-0.05));

        get().addDefault("ideas.economic.idea4.name", "Smithsonian Economics");
        get().addDefault("ideas.economic.idea4.desc", List.of("Глобальная производительность <green>+0.25"));
        get().addDefault("ideas.economic.idea4.effectsId", List.of("goodsMod"));
        get().addDefault("ideas.economic.idea4.effects", List.of(0.25));



        get().addDefault("ideas.trade.idea0.name", "Merchant Adventures");
        get().addDefault("ideas.trade.idea0.desc", List.of("<green>x2<white> Бонус от количества товаров на рынке"));
        get().addDefault("ideas.trade.idea0.effectsId", List.of("tradeGoodsMod"));
        get().addDefault("ideas.trade.idea0.effects", List.of(1.0));

        get().addDefault("ideas.trade.idea1.name", "Overseas Merchants");
        get().addDefault("ideas.trade.idea1.desc", List.of("<green>x2<white> Бонус от торговых кораблей"));
        get().addDefault("ideas.trade.idea1.effectsId", List.of("tradeFrigateMod"));
        get().addDefault("ideas.trade.idea1.effects", List.of(1.0));

        get().addDefault("ideas.trade.idea2.name", "National Trade Policy");
        get().addDefault("ideas.trade.idea2.desc", List.of("<green>x2<white> Бонус от населения"));
        get().addDefault("ideas.trade.idea2.effectsId", List.of("tradePeopleMod"));
        get().addDefault("ideas.trade.idea2.effects", List.of(1.0));

        get().addDefault("ideas.trade.idea3.name", "Efficient Mining");
        get().addDefault("ideas.trade.idea3.desc", List.of("Множитель от монополии <green>+1"));
        get().addDefault("ideas.trade.idea3.effectsId", List.of("tradeMonopolizeMod"));
        get().addDefault("ideas.trade.idea3.effects", List.of(1.0));

        get().addDefault("ideas.trade.idea4.name", "Smithsonian Economics");
        get().addDefault("ideas.trade.idea4.desc", List.of(
                "Измените цену <green>1 <white>товара на <aqua>25%"
        ));



        get().addDefault("ideas.diplomatic.idea0.name", "Experienced Diplomats");
        get().addDefault("ideas.diplomatic.idea0.desc", List.of("ПМА <green>+3"));

        get().addDefault("ideas.diplomatic.idea1.name", "Benign Diplomats");
        get().addDefault("ideas.diplomatic.idea1.desc", List.of("Улучшению отношений <green>+25%"));

        get().addDefault("ideas.diplomatic.idea2.name", "Diplomatic Corps");
        get().addDefault("ideas.diplomatic.idea2.desc", List.of("Административная эффективность <green>+1"));
        get().addDefault("ideas.diplomatic.idea2.effectsId", List.of("adminEfficiency"));
        get().addDefault("ideas.diplomatic.idea2.effects", List.of(1.0));
        //get().addDefault("ideas.diplomatic.idea2.effectsId", List.of(Tools.colorText("Административная эффективность <green>+1")));


        get().addDefault("ideas.diplomatic.idea3.name", "Cabinet");
        get().addDefault("ideas.diplomatic.idea3.desc", List.of("Телепорты в посольствах"));

        get().addDefault("ideas.diplomatic.idea4.name", "Flexible Negotiations");
        get().addDefault("ideas.diplomatic.idea4.desc", List.of("Длительность перемирия сокращена на <green>1<white> день"));
        get().addDefault("ideas.diplomatic.idea4.effectsId", List.of("truceLength"));
        get().addDefault("ideas.diplomatic.idea4.effects", List.of(-1.0));



        get().addDefault("ideas.imperialism.idea0.name", "Experienced Diplomats");
        get().addDefault("ideas.imperialism.idea0.desc", List.of(
                "ПМА <green>+3"
        ));

        get().addDefault("ideas.imperialism.idea1.name", "Adaptability");
        get().addDefault("ideas.imperialism.idea1.desc", List.of(
                "Стоимость национализации <green>-50%"
        ));
        get().addDefault("ideas.imperialism.idea1.effectsId", List.of("coreCreationCost"));
        get().addDefault("ideas.imperialism.idea1.effects", List.of(-0.5));

        get().addDefault("ideas.imperialism.idea2.name", "Diplomatic Corps");
        get().addDefault("ideas.imperialism.idea2.desc", List.of(
                "Административная эффективность <green>+2"
        ));
        get().addDefault("ideas.imperialism.idea2.effectsId", List.of("adminEfficiency"));
        get().addDefault("ideas.imperialism.idea2.effects", List.of(2.0));


        get().addDefault("ideas.imperialism.idea3.name", "State Propaganda");
        get().addDefault("ideas.imperialism.idea3.desc", List.of(
                "Нарушение перемирия не накладывает штраф, но стоит <red>3<white>£"
        ));


        get().addDefault("ideas.imperialism.idea4.name", "Grand Army");
        get().addDefault("ideas.imperialism.idea4.desc", List.of(
                "Боевой дух вашей армии и армий вассалов <green>+25%"
        ));
        get().addDefault("ideas.imperialism.idea4.effectsId", List.of("moraleMod"));
        get().addDefault("ideas.imperialism.idea4.effects", List.of(0.25));



        get().addDefault("ideas.freedom.idea0.name", "Separatism 1");
        get().addDefault("ideas.freedom.idea0.desc", List.of(
                "Вы платите сюзерену в 2 раза меньше"
        ));

        get().addDefault("ideas.freedom.idea1.name", "Taxation with Representation");
        get().addDefault("ideas.freedom.idea1.desc", List.of(
                "Доход от налогов <green>+10%"
        ));

        get().addDefault("ideas.freedom.idea2.name", "Diplomatic Corps");
        get().addDefault("ideas.freedom.idea2.desc", List.of(
                "ПМА <green>+1"
        ));

        get().addDefault("ideas.freedom.idea3.name", "Experienced Diplomats");
        get().addDefault("ideas.freedom.idea3.desc", List.of("Согласие поддержать независимость <green>+50%"));

        get().addDefault("ideas.freedom.idea4.name", "Separatism 5");
        get().addDefault("ideas.freedom.idea4.desc", List.of("Реваншизм <green>+2"));


        get().addDefault("ideas.science.idea0.name", "Patron of the Arts");
        get().addDefault("ideas.science.idea0.desc", List.of(
                "Стоимость строительства науч зданий <green>-50%"
        ));
        get().addDefault("ideas.science.idea0.effectsId", List.of("scienceBuildingCost"));
        get().addDefault("ideas.science.idea0.effects", List.of(-0.5));

        get().addDefault("ideas.science.idea1.name", "Empiricism");
        get().addDefault("ideas.science.idea1.desc", List.of(
                "Прирост ОИ <green>+2"
        ));
        get().addDefault("ideas.science.idea1.effectsId", List.of("oiIncome"));
        get().addDefault("ideas.science.idea1.effects", List.of(2.0));

        get().addDefault("ideas.science.idea2.name", "Print Culture");
        get().addDefault("ideas.science.idea2.desc", List.of("Стоимость технологий <green>-25%"));
        get().addDefault("ideas.science.idea2.effectsId", List.of("techCost"));
        get().addDefault("ideas.science.idea2.effects", List.of(-0.25));

        get().addDefault("ideas.science.idea3.name", "Expanded Policies");
        get().addDefault("ideas.science.idea3.desc", List.of(
                "Прирост ОИ от науч зданий <green>+0.5"
        ));
        get().addDefault("ideas.science.idea3.effectsId", List.of("oiFromBuilding"));
        get().addDefault("ideas.science.idea3.effects", List.of(0.5));
        //get().addDefault("ideas.science.idea3.desc", MiniMessage.miniMessage());

        get().addDefault("ideas.science.idea4.name", "Scientific Revolution");
        get().addDefault("ideas.science.idea4.desc", List.of("Исследуйте 1 технологию"));


        get().addDefault("ideas.revanchism.idea0.name", "Revanchism 1");
        get().addDefault("ideas.revanchism.idea0.desc", List.of(
                "Максимумऴ <green>+5k<white> за ед. реваншизма"
        ));
        //get().addDefault("ideas.revanchism.idea0.effectsId", List.of("manpowerLimitMod"));
        //get().addDefault("ideas.revanchism.idea0.effects", List.of(0.2));

        get().addDefault("ideas.revanchism.idea1.name", "Revanchism 2");
        get().addDefault("ideas.revanchism.idea1.desc", List.of(
                "Стоимость строительства военных зданий <green>-5%<white> за ед. реваншизма"
        ));
        //get().addDefault("ideas.revanchism.idea1.effectsId", List.of("warBuildingCost"));
        //get().addDefault("ideas.revanchism.idea1.effects", List.of(-0.25));

        get().addDefault("ideas.revanchism.idea2.name", "Revanchism 3");
        get().addDefault("ideas.revanchism.idea2.desc", List.of(
                "Содержание армии <green>-5%<white> за ед. реваншизма",
                "Доход от налогов <green>+5%<white> за ед. реваншизма"
        ));
        //get().addDefault("ideas.revanchism.idea2.effectsId", List.of("armyExpenseMod","taxMod"));
        //get().addDefault("ideas.revanchism.idea2.effects", List.of(-0.20,0.2));

        get().addDefault("ideas.revanchism.idea3.name", "Revanchism 4");
        get().addDefault("ideas.revanchism.idea3.desc", List.of(
                "Военные традиции за битвы <green>+10%<white> за ед. реваншизма"
        ));
        //get().addDefault("ideas.revanchism.idea3.effectsId", List.of("buildingCost","traditionMod"));
        //get().addDefault("ideas.revanchism.idea3.effects", List.of(-0.25,2.0));

        get().addDefault("ideas.revanchism.idea4.name", "Revanchism 5");
        get().addDefault("ideas.revanchism.idea4.desc", List.of("Бонусы от реваншизма <green>удваиваются"));
        //get().addDefault("ideas.revanchism.idea4.effectsId", List.of("revanchismMod"));
        //get().addDefault("ideas.revanchism.idea4.effects", List.of(1.0));


        get().addDefault("ideas.isolation.idea0.name", "isolation 1");
        get().addDefault("ideas.isolation.idea0.desc", List.of("Стоимость повышения стабильности <green>-50%"));
        get().addDefault("ideas.isolation.idea1.effectsId", List.of("stabCost"));
        get().addDefault("ideas.isolation.idea1.effects", List.of(-0.5));

        get().addDefault("ideas.isolation.idea1.name", "isolation 2");
        get().addDefault("ideas.isolation.idea1.desc", List.of("Сопротивление урону в фазе <gold>Шока <green>+25% "));
        get().addDefault("ideas.isolation.idea1.effectsId", List.of("shockResist"));
        get().addDefault("ideas.isolation.idea1.effects", List.of(0.25));

        get().addDefault("ideas.isolation.idea2.name", "isolation 3");
        get().addDefault("ideas.isolation.idea2.desc", List.of("Спад стабильности <green>-1"));

        get().addDefault("ideas.isolation.idea3.name", "isolation 4");
        get().addDefault("ideas.isolation.idea3.desc", List.of("приросту полит власти <green>+100%"));
        get().addDefault("ideas.isolation.idea3.effectsId", List.of("politIncomeMod"));
        get().addDefault("ideas.isolation.idea3.effects", List.of(1.0));

        get().addDefault("ideas.isolation.idea4.name", "isolation 5");
        get().addDefault("ideas.isolation.idea4.desc", List.of("Глобальная производительность <green>+0.25"));
        get().addDefault("ideas.isolation.idea4.effectsId", List.of("goodsMod"));
        get().addDefault("ideas.isolation.idea4.effects", List.of(0.2));

        get().addDefault("ideas.offence.idea0.name", "offence 1");
        get().addDefault("ideas.offence.idea0.desc", List.of("Стартовая осада <green>+15%"));
        get().addDefault("ideas.offence.idea0.effectsId", List.of("siegeAbility"));
        get().addDefault("ideas.offence.idea0.effects", List.of(0.15));

        get().addDefault("ideas.offence.idea1.name", "offence 2");
        get().addDefault("ideas.offence.idea1.desc", List.of("ПВ от побед в войне <green>+1 "));

        get().addDefault("ideas.offence.idea2.name", "offence 3");
        get().addDefault("ideas.offence.idea2.desc", List.of("Огонь и Натиск генералов <green>+1"));

        get().addDefault("ideas.offence.idea3.name", "offence 4");
        get().addDefault("ideas.offence.idea3.desc", List.of("Лимит MP <green>+15%"));
        get().addDefault("ideas.offence.idea3.effectsId", List.of("manpowerLimitMod"));
        get().addDefault("ideas.offence.idea3.effects", List.of(0.15));

        get().addDefault("ideas.offence.idea4.name", "offence 5");
        get().addDefault("ideas.offence.idea4.desc", List.of("Дисциплина <green>+5%"));
        get().addDefault("ideas.offence.idea4.effectsId", List.of("disciple"));
        get().addDefault("ideas.offence.idea4.effects", List.of(0.05));

        get().addDefault("ideas.defence.idea0.name", "defence 1");
        get().addDefault("ideas.defence.idea0.desc", List.of("Традиции от битв <green>+100%"));
        get().addDefault("ideas.defence.idea0.effectsId", List.of("traditionMod"));
        get().addDefault("ideas.defence.idea0.effects", List.of(1.0));

        get().addDefault("ideas.defence.idea1.name", "defence 2");
        get().addDefault("ideas.defence.idea1.desc", List.of("Стоимость крепостей <green>-50%"));

        get().addDefault("ideas.defence.idea2.name", "defence 3");
        get().addDefault("ideas.defence.idea2.desc", List.of("Мораль <green>+15%"));

        get().addDefault("ideas.defence.idea3.name", "defence 4");
        get().addDefault("ideas.defence.idea3.desc", List.of("Защита крепостей <green>+50%"));
        get().addDefault("ideas.defence.idea3.effectsId", List.of("fortAbility"));
        get().addDefault("ideas.defence.idea3.effects", List.of(0.50));

        get().addDefault("ideas.defence.idea4.name", "defence 5");
        get().addDefault("ideas.defence.idea4.desc", List.of("Истощение противника <green>+2%"));
        get().addDefault("ideas.defence.idea4.effectsId", List.of("attritionForEnemy"));
        get().addDefault("ideas.defence.idea4.effects", List.of(0.02));

        get().addDefault("ideas.quality.idea0.name", "quality 1");
        get().addDefault("ideas.quality.idea0.desc", List.of("Мораль от традиций <green>+1%"));
        get().addDefault("ideas.quality.idea0.effectsId", List.of("siegeAbility"));
        get().addDefault("ideas.quality.idea0.effects", List.of(0.01));

        get().addDefault("ideas.quality.idea1.name", "quality 2");
        get().addDefault("ideas.quality.idea1.desc", List.of("Снижение урона в фазе огня <green>-10%"));
        get().addDefault("ideas.quality.idea1.effectsId", List.of("fireResist"));
        get().addDefault("ideas.quality.idea1.effects", List.of(-0.1));

        get().addDefault("ideas.quality.idea2.name", "quality 3");
        get().addDefault("ideas.quality.idea2.desc", List.of("Боевая мощь войск <green>+10%"));
        get().addDefault("ideas.quality.idea2.effectsId", List.of("cavCombatAbility","infCombatAbility","artCombatAbility"));
        get().addDefault("ideas.quality.idea2.effects", List.of(0.1,0.1,0.1));

        get().addDefault("ideas.quality.idea3.name", "quality 4");
        get().addDefault("ideas.quality.idea3.desc", List.of("Снижение урона в фазе натиска <green>-10%"));
        get().addDefault("ideas.quality.idea3.effectsId", List.of("shockResist"));
        get().addDefault("ideas.quality.idea3.effects", List.of(-0.1));

        get().addDefault("ideas.quality.idea4.name", "quality 5");
        get().addDefault("ideas.quality.idea4.desc", List.of("Дисциплина <green>+5%"));
        get().addDefault("ideas.quality.idea4.effectsId", List.of("disciple"));
        get().addDefault("ideas.quality.idea4.effects", List.of(0.05));

        get().addDefault("ideas.quantity.idea0.name", "quantity 1");
        get().addDefault("ideas.quantity.idea0.desc", List.of("Шанс истощения <green>-10%"));
        get().addDefault("ideas.quantity.idea0.effectsId", List.of("attritionChance"));
        get().addDefault("ideas.quantity.idea0.effects", List.of(-0.1));

        get().addDefault("ideas.quantity.idea1.name", "quantity 2");
        get().addDefault("ideas.quantity.idea1.desc", List.of("Восстановление MP <green>+30%"));
        get().addDefault("ideas.quantity.idea1.effectsId", List.of("manpowerRecMod"));
        get().addDefault("ideas.quantity.idea1.effects", List.of(0.3));

        get().addDefault("ideas.quantity.idea2.name", "quantity 3");
        get().addDefault("ideas.quantity.idea2.desc", List.of("Содержание регуляров <green>-15%"));
        get().addDefault("ideas.quantity.idea2.effectsId", List.of("armyExpenseMod"));
        get().addDefault("ideas.quantity.idea2.effects", List.of(-0.15));

        get().addDefault("ideas.quantity.idea3.name", "quantity 4");
        get().addDefault("ideas.quantity.idea3.desc", List.of("Лимит MP <green>+30%"));
        get().addDefault("ideas.quantity.idea3.effectsId", List.of("manpowerLimitMod"));
        get().addDefault("ideas.quantity.idea3.effects", List.of(0.30));

        get().addDefault("ideas.quantity.idea4.name", "quantity 5");
        get().addDefault("ideas.quantity.idea4.desc", List.of("Мораль <green>+5%"));
        get().addDefault("ideas.quantity.idea4.effectsId", List.of("moraleMod"));
        get().addDefault("ideas.quantity.idea4.effects", List.of(0.05));

        get().addDefault("ideas.naval.idea0.name", "naval 1");
        get().addDefault("ideas.naval.idea0.desc", List.of("Стоимость кораблей <green>-50%"));


        get().addDefault("ideas.naval.idea1.name", "naval 2");
        get().addDefault("ideas.naval.idea1.desc", List.of("Лимит флота <green>+100%"));

        get().addDefault("ideas.naval.idea2.name", "naval 3");
        get().addDefault("ideas.naval.idea2.desc", List.of("Вместимость на кораблях <green>+100%"));

        get().addDefault("ideas.naval.idea3.name", "naval 4");
        get().addDefault("ideas.naval.idea3.desc", List.of("Мораль флота <green>+20%"));


        get().addDefault("ideas.naval.idea4.name", "naval 5");
        get().addDefault("ideas.naval.idea4.desc", List.of("Корабли не требуют содержания"));


        get().addDefault("ideas.shock.idea0.name", "shock 1");
        get().addDefault("ideas.shock.idea0.desc", List.of("Урон в фазе натиска <green>+30%"));
        get().addDefault("ideas.shock.idea0.effectsId", List.of("shockDamage"));
        get().addDefault("ideas.shock.idea0.effects", List.of(0.30));

        get().addDefault("ideas.shock.idea1.name", "shock 2");
        get().addDefault("ideas.shock.idea1.desc", List.of("Снижение урона в фазе огня<green>-30%"));
        get().addDefault("ideas.shock.idea1.effectsId", List.of("fireResist"));
        get().addDefault("ideas.shock.idea1.effects", List.of(-0.30));

        get().addDefault("ideas.shock.idea2.name", "shock 3");
        get().addDefault("ideas.shock.idea2.desc", List.of("Урон по морали <green>+30%"));
        get().addDefault("ideas.shock.idea2.effectsId", List.of("moraleDamage"));
        get().addDefault("ideas.shock.idea2.effects", List.of(0.30));

        get().addDefault("ideas.fire.idea0.name", "fire 1");
        get().addDefault("ideas.fire.idea0.desc", List.of("Урон в фазе огня <green>+30%"));
        get().addDefault("ideas.shock.idea0.effectsId", List.of("fireDamage"));
        get().addDefault("ideas.shock.idea0.effects", List.of(0.30));

        get().addDefault("ideas.fire.idea1.name", "fire 2");
        get().addDefault("ideas.fire.idea1.desc", List.of("Снижение урона в фазе натиска<green>-30%"));
        get().addDefault("ideas.shock.idea2.effectsId", List.of("shockResist"));
        get().addDefault("ideas.shock.idea2.effects", List.of(-0.30));

        get().addDefault("ideas.fire.idea2.name", "fire 3");
        get().addDefault("ideas.fire.idea2.desc", List.of("Дисциплина <green>+10%"));
        get().addDefault("ideas.shock.idea2.effectsId", List.of("disciple"));
        get().addDefault("ideas.shock.idea2.effects", List.of(0.10));

        get().addDefault("ideas.artillery.idea0.name", "artillery 1");
        get().addDefault("ideas.artillery.idea0.desc", List.of("Боевая мощь артиллерии <green>+30%"));
        get().addDefault("ideas.artillery.idea0.effectsId", List.of("artCombatAbility"));
        get().addDefault("ideas.artillery.idea0.effects", List.of(0.3));

        get().addDefault("ideas.artillery.idea1.name", "artillery 2");
        get().addDefault("ideas.artillery.idea1.desc", List.of("Содержание артиллерии <green>-50%"));
        get().addDefault("ideas.artillery.idea1.effectsId", List.of("artCost"));
        get().addDefault("ideas.artillery.idea1.effects", List.of(-0.5));

        get().addDefault("ideas.artillery.idea2.name", "artillery 3");
        get().addDefault("ideas.artillery.idea2.desc", List.of("Стартовая осада <green>+15%"));
        get().addDefault("ideas.artillery.idea2.effectsId", List.of("siegeAbility"));
        get().addDefault("ideas.artillery.idea2.effects", List.of(0.15));

        get().addDefault("ideas.cavalry.idea0.name", "cavalry 1");
        get().addDefault("ideas.cavalry.idea0.desc", List.of("Соотношение кавалерии <green>+50%"));
        get().addDefault("ideas.cavalry.idea0.effectsId", List.of("cavRatio"));
        get().addDefault("ideas.cavalry.idea0.effects", List.of(0.5));

        get().addDefault("ideas.cavalry.idea1.name", "cavalry 2");
        get().addDefault("ideas.cavalry.idea1.desc", List.of("Боевая мощь кавалерии <green>+30%"));
        get().addDefault("ideas.cavalry.idea1.effectsId", List.of("cavCombatAbility"));
        get().addDefault("ideas.cavalry.idea1.effects", List.of(0.3));

        get().addDefault("ideas.cavalry.idea2.name", "cavalry 3");
        get().addDefault("ideas.cavalry.idea2.desc", List.of("Содержание кавалерии <green>-30%"));
        get().addDefault("ideas.cavalry.idea2.effectsId", List.of("cavCost"));
        get().addDefault("ideas.cavalry.idea2.effects", List.of(-0.3));

        get().addDefault("ideas.infantry.idea0.name", "infantry 1");
        get().addDefault("ideas.infantry.idea0.desc", List.of("Боевая мощь пехота <green>+30%"));
        get().addDefault("ideas.infantry.idea0.effectsId", List.of("infCombatAbility"));
        get().addDefault("ideas.infantry.idea0.effects", List.of(0.3));

        get().addDefault("ideas.infantry.idea1.name", "infantry 2");
        get().addDefault("ideas.infantry.idea1.desc", List.of("Снижение урона по морали <green>-15%"));
        get().addDefault("ideas.infantry.idea1.effectsId", List.of("moraleResist"));
        get().addDefault("ideas.infantry.idea1.effects", List.of(-0.15));

        get().addDefault("ideas.infantry.idea2.name", "infantry 3");
        get().addDefault("ideas.infantry.idea2.desc", List.of("Дисциплина <green>+10%"));
        get().addDefault("ideas.infantry.idea2.effectsId", List.of("disciple"));
        get().addDefault("ideas.infantry.idea2.effects", List.of(0.1));



        List<String> forbiddenPatterns = Arrays.asList(
                "POTION", "LINGERING", "SPLASH",
                "ENCHANTED_BOOK", "PROPAGULE", "DEAD_BUSH", "COBWEB", "SEA_PICKLE", "SPONGE",
                "SPAWN_EGG",
                "SPAWNER",
                "SCULK",
                "COMMAND_BLOCK",
                "STRUCTURE_VOID", "BARRIER",
                "COMMAND_BLOCK_MINECART",
                "STRUCTURE_BLOCK", "JIGSAW",
                "EXPERIENCE_BOTTLE",
                "SMITHING_TEMPLATE",
                "TRIAL_KEY",
                "POTTERY_SHERD", "DECORATED_POT",
                "NETHER_STAR",
                "OMINOUS_BOTTLE",
                "TOTEM_OF_UNDYING",
                "SWORD", "AXE", "PICKAXE", "SHOVEL", "HOE",
                "HELMET", "CHESTPLATE", "LEGGINGS", "BOOTS",
                "SHIELD", "BOW", "CROSSBOW", "TRIDENT",
                "FISHING_ROD", "FLINT_AND_STEEL", "SHEARS", "BRUSH",
                "MACE", "ELYTRA",
                "MUSIC_DISC",
                "GOAT_HORN",
                "BOAT", "MINECART", "CHEST_BOAT", "CHEST_MINECART",
                "FURNACE_MINECART", "HOPPER_MINECART", "TNT_MINECART",
                "RAIL", "SADDLE", "DRAGON_EGG", "BEACON", "FARMLAND", "SNOW", "STAIRS",
                "BUNDLE", "LECTERN", "TARGET", "ROD", "DETECTOR", "HOOK", "HAY_BLOCK", "CARPET", "ANVIL",
                "PAINTING", "ON_A_STICK", "END", "REPEATER", "COMPARATOR", "PISTON", "SLAB", "OBSERVER", "HOPPER", "DISPENSER","DROPPER",
                "END_CRYSTAL", "CONDUIT",
                "SAPLING", "HEART", "TABLE", "CHEST", "LIGHT", "BOX", "TURTLE_EGG", "SNIFFER_EGG",
                "MUSHROOM", "END_TORCH",
                "FLOWER", "GRASS", "FERN", "LEAVES", "TULIP", "ORCHID", "FLOWER", "ALLIUM", "AZURE_BLUET", "BLUE_ORCHID",
                "DANDELION", "CORNFLOWER", "POPPY", "OXEYE_DAISY", "WITHER_ROSE", "LILY_OF_THE_VALLEY", "RED_TULIP",
                "ORANGE_TULIP", "WHITE_TULIP", "PINK_TULIP", "YELLOW_TULIP", "TORCHFLOWER",
                "GRASS", "TALL_GRASS", "FERN", "LARGE_FERN", "DEAD_BUSH", "SHRUB",
                "LEAVES", // Общее имя — не конкретный ID, но подходит под паттерн
                "SPORE_BLOSSOM", "PEONY", "ROSE_BUSH", "LILAC", "SUNFLOWER",
                "SMALL_DRIPLEAF", "BIG_DRIPLEAF", "AZALEA", "FLOWERING_AZALEA", "SEAGRASS", "TALL_SEAGRASS",

                "VINE", "LILY", "AZALEA", "ROOTS", "VAULT", "DRIPSTONE", "FROG", "BED", "SEEDS", "FLESH", "TEAR", "NUGGET", "STAND", "BONE", "BUCKET",
                "HANGING", "MOSS", "SEAGRASS", "KELP", "OBSIDIAN", "BEE", "AMETHYST", "COMPASS", "CLOCK", "SIGN", "WATER", "NETHERITE",
                "BAMBOO", "CACTUS", "CORAL", "DRIPLEAF", "SKULL", "BOOK", "WIND", "CAULDRON", "PATTERN", "SHULKER", "DRAGON", "CAMPFIRE",
                "SPROUTS", "WEEPING", "TWISTING", "WART", "WALL", "PLATE", "DOOR", "BUTTON", "GATE", "TRAPDOOR", "ARMOR", "GOLDEN_APPLE", "CRAFTER",
                "CRIMSON", "WARPED", "BOOKSHELF", "CHORUS", "EYEBLOSSOM", "PLANT", "PETALS", "FENCE", "LADDER", "FURNACE", "LICHEN",
                "HEAD", "ARMOUR", "ORE", "RAW", "BANNER", "TORCH", "SOUL", "FIRE", "GOLDEN_CARROT", "DISC", "DEBUG",
                "IRON_BLOCK","COPPER_BLOCK", "GOLD_BLOCK", "DIAMOND_BLOCK","COAL_BLOCK", "EMERALD_BLOCK", "DEBRIS"
        );
        get().addDefault("items.banned",forbiddenPatterns);

        get().addDefault("pips.standard.inf0.fire",0);
        get().addDefault("pips.standard.inf0.shock",0);
        get().addDefault("pips.standard.inf0.morale",0);

        get().addDefault("pips.standard.inf1.fire",1);
        get().addDefault("pips.standard.inf1.shock",0);
        get().addDefault("pips.standard.inf1.morale",0);

        get().addDefault("pips.standard.inf2.fire",2);
        get().addDefault("pips.standard.inf2.shock",1);
        get().addDefault("pips.standard.inf2.morale",0);

        get().addDefault("pips.standard.inf3.fire",3);
        get().addDefault("pips.standard.inf3.shock",2);
        get().addDefault("pips.standard.inf3.morale",1);

        get().addDefault("pips.standard.inf4.fire",3);
        get().addDefault("pips.standard.inf4.shock",2);
        get().addDefault("pips.standard.inf4.morale",2);

        get().addDefault("pips.standard.cav1.fire",0);
        get().addDefault("pips.standard.cav1.shock",2);
        get().addDefault("pips.standard.cav1.morale",1);

        get().addDefault("pips.standard.cav2.fire",0);
        get().addDefault("pips.standard.cav2.shock",3);
        get().addDefault("pips.standard.cav2.morale",1);

        get().addDefault("pips.standard.cav3.fire",0);
        get().addDefault("pips.standard.cav3.shock",4);
        get().addDefault("pips.standard.cav3.morale",2);

        get().addDefault("pips.standard.cav4.fire",1);
        get().addDefault("pips.standard.cav4.shock",4);
        get().addDefault("pips.standard.cav4.morale",2);

        get().addDefault("pips.standard.art3.fire",5);
        get().addDefault("pips.standard.art3.shock",2);
        get().addDefault("pips.standard.art3.morale",2);

        get().addDefault("pips.standard.art4.fire",8);
        get().addDefault("pips.standard.art4.shock",4);
        get().addDefault("pips.standard.art4.morale",4);


        get().addDefault("trade.towns.id.id.status",true);
        get().addDefault("trade.towns.id.id.distance",100);
        get().addDefault("trade.towns.id.id.type","land or sea");

        save();

    }

    public static FileConfiguration get(){
        return customFile;
    }

    public static void save(){
        try {
            customFile.save(file);
        }catch (IOException e){
            System.out.println("[Earth]couldn't save config file");
        }
    }


    public static void set(String path, Object value){
        customFile.set(path, value);
        save();
    }

    public static void reload(){
        customFile = YamlConfiguration.loadConfiguration(file);
    }


}
