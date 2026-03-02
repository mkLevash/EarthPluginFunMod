package earthrp.files;

import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;

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
        get().addDefault("status.day",3);

        get().addDefault("tech.lore.tribal",List.of(
                "<white>Полит Власть:",
                "<white>Прирост 0£",
                "<white>Максимум 5£",
                "<white>Админ. эфф. 0अ"
        ));

        // ==================== ЭПОХА 0: ПЛЕМЯ ====================
        get().addDefault("tech.cost.mining",5);
        get().addDefault("tech.name.mining","Горное дело");
        get().addDefault("tech.lore.mining",List.of(
                "<dark_green>Позволяет строить <green>шахты"
        ));

        get().addDefault("tech.cost.building",5);
        get().addDefault("tech.name.building","Каменная кладка");
        get().addDefault("tech.lore.building",List.of(
                "<gray>Позволяет строить <light_purple>древние стены, застава лесорубов, амбар"
        ));

        get().addDefault("tech.cost.shipping",5);
        get().addDefault("tech.name.shipping","Мореплавание");
        get().addDefault("tech.lore.shipping",List.of(
                "<gray>Позволяет строить <light_purple>порт"
        ));

        get().addDefault("tech.cost.irrigation",5);
        get().addDefault("tech.name.irrigation","Орошение");
        get().addDefault("tech.lore.irrigation",List.of(
                "<gray>Позволяет строить <light_purple>грядка"
        ));

        get().addDefault("tech.cost.livestock",5);
        get().addDefault("tech.name.livestock","Животноводство");
        get().addDefault("tech.lore.livestock",List.of(
                "<gray>Позволяет строить <light_purple>пастбище"
        ));

        get().addDefault("tech.cost.writing",5);
        get().addDefault("tech.name.writing","Письменность");
        get().addDefault("tech.lore.writing",List.of(
                "<gray>Позволяет строить <light_purple>библиотека"
        ));

        // ==================== ЭПОХА 1: ФЕОДАЛИЗМ ====================
        get().addDefault("tech.cost.feudalism",15);
        get().addDefault("tech.name.feudalism","Феодализм");
        get().addDefault("tech.lore.feudalism",List.of(
                "<white>Полит Власть:",
                "<white>Прирост 1£",
                "<white>Максимум 10£",
                "<white>Админ. эфф. 1अ"
        ));

        get().addDefault("tech.cost.bankBase",10);
        get().addDefault("tech.name.bankBase","Ростовщики");
        get().addDefault("tech.lore.bankBase",List.of(
                "<gray>Возможность брать долги",
                "<white>1 <gray>долг - <green>5<white>$<gray> под <white>20%"
        ));

        get().addDefault("tech.cost.banner",10);
        get().addDefault("tech.name.banner","Баннеры");
        get().addDefault("tech.lore.banner",List.of(
                "<gray>Возможность поднимать <light_purple>ополчение",
                "<white>Восстановление ऴ <green>+10<white>%"
        ));

        get().addDefault("tech.cost.motte",10);
        get().addDefault("tech.name.motte","Мотт");
        get().addDefault("tech.lore.motte",List.of(
                "<gray>Позволяет строить <light_purple>крепость <green>2 <gray>уровня"
        ));

        get().addDefault("tech.cost.horseRidding",10);
        get().addDefault("tech.name.horseRidding","Верховая езда");
        get().addDefault("tech.lore.horseRidding",List.of(
                "<gray>Позволяет нанимать <light_purple>лёгких всадников"
        ));

        get().addDefault("tech.cost.trade",10);
        get().addDefault("tech.name.trade","Торговля");
        get().addDefault("tech.lore.trade",List.of(
                "<gray>Позволяет строить <light_purple>рынок"
        ));

        get().addDefault("tech.cost.copper",10);
        get().addDefault("tech.name.copper","Обработка меди");
        get().addDefault("tech.lore.copper",List.of(
                "<gray>Позволяет добывать <light_purple>медь уголь",
                "<gray>Позволяет строить <light_purple>кузня"
        ));

        get().addDefault("tech.cost.engineering",15);
        get().addDefault("tech.name.engineering","Инженерное дело");
        get().addDefault("tech.lore.engineering",List.of(
                "<green>+10<white>% базовой осады",
                "<gray>Позволяет штурмовать крепость",
                "<gray>Позволяет строить <light_purple>акведук"
        ));

        get().addDefault("tech.cost.lowerMedievalMilitary",15);
        get().addDefault("tech.name.lowerMedievalMilitary","Нижне-средневековое военное дело");
        get().addDefault("tech.lore.lowerMedievalMilitary",List.of(
                "<white>ᠨ<green>+0.1<white>ᠩ<green>+0.5",
                "<gray>Позволяет нанимать <light_purple>лёгких мечников"
        ));

        get().addDefault("tech.cost.shipbuilding",10);
        get().addDefault("tech.name.shipbuilding","Базовое судостроение");
        get().addDefault("tech.lore.shipbuilding",List.of(
                "<gray>Позволяет строить <light_purple>верфи"
        ));

        get().addDefault("tech.cost.iron",15);
        get().addDefault("tech.name.iron","Обработка железа");
        get().addDefault("tech.lore.iron",List.of(
                "<gray>Позволяет добывать <light_purple>железо"
        ));

        get().addDefault("tech.cost.earlyCarrack",10);
        get().addDefault("tech.name.earlyCarrack","Ранняя каррака");
        get().addDefault("tech.lore.earlyCarrack",List.of(
                "<yellow>Требуется: <aqua>Базовое судостроение"
        ));

        get().addDefault("tech.cost.workshop",15);
        get().addDefault("tech.name.workshop","Мастерские");
        get().addDefault("tech.lore.workshop",List.of(
                "<yellow>Требуется: <aqua>Инженерное дело",
                "<gray>Открывает мастерские",
                "<green>Увеличивает производительность шахты +0.25"
        ));

        get().addDefault("tech.cost.highMedievalMilitary",20);
        get().addDefault("tech.name.highMedievalMilitary","Верхне-средневековое военное дело");
        get().addDefault("tech.lore.highMedievalMilitary",List.of(
                "<yellow>Требуется: <aqua>Нижне-средневековое военное дело, Обработка железа",
                "<green>Тактика +0.15, мораль +0.5",
                "<gray>Открывает казармы и конюшни (2 лвл войск)"
        ));

        get().addDefault("tech.cost.medievalAdministration",15);
        get().addDefault("tech.name.medievalAdministration","Средневековая администрация");
        get().addDefault("tech.lore.medievalAdministration",List.of(
                "<green>Админ. эффективность +1"
        ));

        get().addDefault("tech.cost.castle",15);
        get().addDefault("tech.name.castle","Замки");
        get().addDefault("tech.lore.castle",List.of(
                "<yellow>Требуется: <aqua>Мотт",
                "<gray>Открывает крепость 3 уровня"
        ));

        // ==================== ЭПОХА 2: РЕНЕССАНС ====================
        get().addDefault("tech.cost.renaissance",50);
        get().addDefault("tech.name.renaissance","Ренессанс");
        get().addDefault("tech.lore.renaissance",List.of(
                "<green>Прирост ОИ от научных зданий +0.5"
        ));

        get().addDefault("tech.cost.university",50);
        get().addDefault("tech.name.university","Университет");
        get().addDefault("tech.lore.university",List.of(
                "<gray>Университет",
                "<yellow>Минимум 20 населения в городе"
        ));

        get().addDefault("tech.cost.bastion",30);
        get().addDefault("tech.name.bastion","Бастион");
        get().addDefault("tech.lore.bastion",List.of(
                "<gray>Открывает крепость 5 уровня"
        ));

        get().addDefault("tech.cost.bankUp",30);
        get().addDefault("tech.name.bankUp","Банковское дело");
        get().addDefault("tech.lore.bankUp",List.of(
                "<gray>Возможность строить банк",
                "<green>1 долг после постройки банка - 10$ под 10%",
                "<green>+-% 1% за ед. стабильности с капом в 4%"
        ));

        get().addDefault("tech.cost.gunpowder",75);
        get().addDefault("tech.name.gunpowder","Порох");
        get().addDefault("tech.lore.gunpowder",List.of(
                "<gray>Артиллерия и оружейная фабрика",
                "<gray>Пороховые мастерские, рудники"
        ));

        get().addDefault("tech.cost.professionalArmy",50);
        get().addDefault("tech.name.professionalArmy","Профессиональная армия");
        get().addDefault("tech.lore.professionalArmy",List.of(
                "<green>Тактика +0.25, мораль +1"
        ));

        get().addDefault("tech.cost.inf3",15);
        get().addDefault("tech.name.inf3","Аркебузиры");
        get().addDefault("tech.lore.inf3",List.of(
                "<yellow>Требуется: <aqua>Профессиональная армия",
                "<gray>Пехота 3 уровня"
        ));

        get().addDefault("tech.cost.cav3",15);
        get().addDefault("tech.name.cav3","Рейтары");
        get().addDefault("tech.lore.cav3",List.of(
                "<yellow>Требуется: <aqua>Профессиональная армия",
                "<gray>Кавалерия 3 уровня"
        ));

        get().addDefault("tech.cost.dock",30);
        get().addDefault("tech.name.dock","Доки");
        get().addDefault("tech.lore.dock",List.of(
                "<gray>Каррака, каравелла, боевая галера, флейт",
                "<green>+0.5 нави морали"
        ));

        get().addDefault("tech.cost.earlyModernAdministration",50);
        get().addDefault("tech.name.earlyModernAdministration","Администрация Нового времени");
        get().addDefault("tech.lore.earlyModernAdministration",List.of(
                "<gray>Суды",
                "<green>Макс ПП +5"
        ));

        get().addDefault("tech.cost.printingPress",100);
        get().addDefault("tech.name.printingPress","Печатный станок");
        get().addDefault("tech.lore.printingPress",List.of(
                "<green>Стоимость технологий -10%"
        ));

        get().addDefault("tech.cost.starFort",30);
        get().addDefault("tech.name.starFort","Звёздчатый форт");
        get().addDefault("tech.lore.starFort",List.of(
                "<yellow>Требуется: <aqua>Печатный станок",
                "<gray>Открывает крепость 7 уровня"
        ));

        get().addDefault("tech.cost.spainSquare",50);
        get().addDefault("tech.name.spainSquare","Испанское каре");
        get().addDefault("tech.lore.spainSquare",List.of(
                "<yellow>Требуется: <aqua>Печатный станок",
                "<green>Тактика +0.25",
                "<gray>Терции - пехота 4 уровня"
        ));

        get().addDefault("tech.cost.art1",15);
        get().addDefault("tech.name.art1","Стандартизация калибров");
        get().addDefault("tech.lore.art1",List.of(
                "<yellow>Требуется: <aqua>Испанское каре",
                "<gray>Артиллерия 2 уровня"
        ));

        get().addDefault("tech.cost.cav4",15);
        get().addDefault("tech.name.cav4","Карабинеры");
        get().addDefault("tech.lore.cav4",List.of(
                "<yellow>Требуется: <aqua>Испанское каре",
                "<gray>Кавалерия 4 уровня"
        ));

        get().addDefault("tech.cost.charterCompany",30);
        get().addDefault("tech.name.charterCompany","Чартерные компании");
        get().addDefault("tech.lore.charterCompany",List.of(
                "<yellow>Требуется: <aqua>Печатный станок",
                "<gray>Галеас, Галеон, Ранний фрегат, Бриг",
                "<green>+1 нави мораль"
        ));

        get().addDefault("tech.cost.separationPower",30);
        get().addDefault("tech.name.separationPower","Разделение властей");
        get().addDefault("tech.lore.separationPower",List.of(
                "<yellow>Требуется: <aqua>Печатный станок",
                "<gray>Позволяет менять Форму Правления"
        ));

        get().addDefault("tech.cost.bureaucracyBase",30);
        get().addDefault("tech.name.bureaucracyBase","Базовая бюрократия");
        get().addDefault("tech.lore.bureaucracyBase",List.of(
                "<yellow>Требуется: <aqua>Печатный станок",
                "<green>Админ. эффективность +1"
        ));

        // ==================== ЭПОХА 3: МАНУФАКТУРЫ ====================
        get().addDefault("tech.cost.manufacture",100);
        get().addDefault("tech.name.manufacture","Мануфактуры");
        get().addDefault("tech.lore.manufacture",List.of(
                "<gray>Открывает мануфактуры и карьеры"
        ));

        get().addDefault("tech.cost.fortress",30);
        get().addDefault("tech.name.fortress","Цитадель");
        get().addDefault("tech.lore.fortress",List.of(
                "<gray>Открывает крепость 9 уровня"
        ));

        get().addDefault("tech.cost.enlightenment",30);
        get().addDefault("tech.name.enlightenment","Просвещение");
        get().addDefault("tech.lore.enlightenment",List.of(
                "<green>Снижает требование для университетов в 2 раза"
        ));

        get().addDefault("tech.cost.constitution",30);
        get().addDefault("tech.name.constitution","Конституция");
        get().addDefault("tech.lore.constitution",List.of(
                "<green>Макс ПП +5"
        ));

        get().addDefault("tech.cost.bureaucracyUp",30);
        get().addDefault("tech.name.bureaucracyUp","Развитая бюрократия");
        get().addDefault("tech.lore.bureaucracyUp",List.of(
                "<green>Стоимость национализации -50%"
        ));

        get().addDefault("tech.cost.lineInfantry",50);
        get().addDefault("tech.name.lineInfantry","Линейное построение");
        get().addDefault("tech.lore.lineInfantry",List.of(
                "<green>Тактика +0.25, мораль +1"
        ));

        get().addDefault("tech.cost.cartridges",15);
        get().addDefault("tech.name.cartridges","Патроны");
        get().addDefault("tech.lore.cartridges",List.of(
                "<gray>Артиллерия 3 уровня",
                "<gray>Пехота 5 уровня"
        ));

        get().addDefault("tech.cost.cav5",15);
        get().addDefault("tech.name.cav5","Разведка");
        get().addDefault("tech.lore.cav5",List.of(
                "<gray>Кавалерия 5 уровня"
        ));

        get().addDefault("trade.playerName.playerName",true);

        // Сохраняем конфиг с default значениями
        save();

        get().addDefault("ideas.admin.idea0.name", "Stable Government");
        get().addDefault("ideas.admin.idea0.desc", List.of("§fСтоимость стабильности §a-25%"));

        get().addDefault("ideas.admin.idea1.name", "Locale Rule");
        get().addDefault("ideas.admin.idea1.desc", List.of("§fСтоимость жилого дома §a-1§f$"));

        get().addDefault("ideas.admin.idea2.name", "Cultural Regulation");
        get().addDefault("ideas.admin.idea2.desc", List.of("§fСтоимость национализации §a-25%"));
        get().addDefault("ideas.admin.idea2.effectsId", List.of("coreCreationCost"));
        get().addDefault("ideas.admin.idea2.effects", List.of(-0.25));

        get().addDefault("ideas.admin.idea3.name", "Centralization");
        get().addDefault("ideas.admin.idea3.desc", List.of("§fСтоимость повышения инфраструктуры §a-50%"));
        get().addDefault("ideas.admin.idea3.effectsId", List.of("expandInfrastructureCost"));
        get().addDefault("ideas.admin.idea3.effects", List.of(-0.5));

        get().addDefault("ideas.admin.idea4.name", "Adaptability");
        get().addDefault("ideas.admin.idea4.desc", List.of("§fКоличество зданий в городе §a+1"));
        get().addDefault("ideas.admin.idea4.effectsId", List.of("buildSites"));
        get().addDefault("ideas.admin.idea4.effects", List.of(1.0));
        
        

        get().addDefault("ideas.economic.idea0.name", "National Bank");
        get().addDefault("ideas.economic.idea0.desc", List.of("§fДоход от налогов §a+10%"));
        get().addDefault("ideas.economic.idea0.effectsId", List.of("taxMod"));
        get().addDefault("ideas.economic.idea0.effects", List.of(0.10));

        get().addDefault("ideas.economic.idea1.name", "Bureaucracy");
        get().addDefault("ideas.economic.idea1.desc", List.of("§fСтоимость строительства §a-10%"));
        get().addDefault("ideas.economic.idea1.effectsId", List.of("buildingCost"));
        get().addDefault("ideas.economic.idea1.effects", List.of(-0.1));

        get().addDefault("ideas.economic.idea2.name", "Debt and Loans");
        get().addDefault("ideas.economic.idea2.desc", List.of("§fСодержание армии §a-5%"));
        get().addDefault("ideas.economic.idea2.effectsId", List.of("armyExpenseMod"));
        get().addDefault("ideas.economic.idea2.effects", List.of(-0.05));

        get().addDefault("ideas.economic.idea3.name", "Efficient Mining");
        get().addDefault("ideas.economic.idea3.desc", List.of("§fЕжедневное снижение инфляции §a-1%"));
        get().addDefault("ideas.economic.idea3.effectsId", List.of("inflationReduce"));
        get().addDefault("ideas.economic.idea3.effects", List.of(-1.0));

        get().addDefault("ideas.economic.idea4.name", "Smithsonian Economics");
        get().addDefault("ideas.economic.idea4.desc", List.of("§fПроизводство §a+0.2"));
        get().addDefault("ideas.economic.idea4.effectsId", List.of("goodsMod"));
        get().addDefault("ideas.economic.idea4.effects", List.of(0.2));



        get().addDefault("ideas.trade.idea0.name", "Merchant Adventures");
        get().addDefault("ideas.trade.idea0.desc", List.of("§ax2§f Бонус от количества товаров на рынке"));
        get().addDefault("ideas.trade.idea0.effectsId", List.of("tradeGoodsMod"));
        get().addDefault("ideas.trade.idea0.effects", List.of(2.0));

        get().addDefault("ideas.trade.idea1.name", "Overseas Merchants");
        get().addDefault("ideas.trade.idea1.desc", List.of(Tools.colorText("&ax2&f Бонус от торговых кораблей")));
        get().addDefault("ideas.trade.idea1.effectsId", List.of("frigateMod"));
        get().addDefault("ideas.trade.idea1.effects", List.of(2.0));

        get().addDefault("ideas.trade.idea2.name", "National Trade Policy");
        get().addDefault("ideas.trade.idea2.desc", List.of(Tools.colorText("&fПроизводство &a+0.1")));
        get().addDefault("ideas.trade.idea2.effectsId", List.of("goodsMod"));
        get().addDefault("ideas.trade.idea2.effects", List.of(0.1));

        get().addDefault("ideas.trade.idea3.name", "Efficient Mining");
        get().addDefault("ideas.trade.idea3.desc", List.of(Tools.colorText("&fЭффективность торговли &a+25%")));
        get().addDefault("ideas.trade.idea3.effectsId", List.of("tradeMod"));
        get().addDefault("ideas.trade.idea3.effects", List.of(0.25));

        get().addDefault("ideas.trade.idea4.name", "Smithsonian Economics");
        get().addDefault("ideas.trade.idea4.desc", List.of(
                Tools.colorText("&fВозможность изменить"),
                Tools.colorText("&fглобальную цену &a1 товара")
        ));



        get().addDefault("ideas.diplomatic.idea0.name", "Experienced Diplomats");
        get().addDefault("ideas.diplomatic.idea0.desc", List.of(Tools.colorText("&a+3&f ПМА")));

        get().addDefault("ideas.diplomatic.idea1.name", "Benign Diplomats");
        get().addDefault("ideas.diplomatic.idea1.desc", List.of(Tools.colorText("&a+25%&f к улучшению отношений")));

        get().addDefault("ideas.diplomatic.idea2.name", "Diplomatic Corps");
        get().addDefault("ideas.diplomatic.idea2.desc", List.of(Tools.colorText("&fАдминистративная эффективность &a+1")));

        get().addDefault("ideas.diplomatic.idea3.name", "Cabinet");
        get().addDefault("ideas.diplomatic.idea3.desc", List.of(Tools.colorText("&fТелепорты в посольствах")));

        get().addDefault("ideas.diplomatic.idea4.name", "Flexible Negotiations");
        get().addDefault("ideas.diplomatic.idea4.desc", List.of(Tools.colorText("&fДлительность перемирия сокращена на &21&f день")));



        get().addDefault("ideas.imperialism.idea0.name", "Experienced Diplomats");
        get().addDefault("ideas.imperialism.idea0.desc", List.of(Tools.colorText("&a+3&f ПМА")));

        get().addDefault("ideas.imperialism.idea1.name", "Adaptability");
        get().addDefault("ideas.imperialism.idea1.desc", List.of(Tools.colorText("&a-50%&f стоимость национализации")));
        get().addDefault("ideas.imperialism.idea1.effectsId", List.of("coreCreationCost"));
        get().addDefault("ideas.imperialism.idea1.effects", List.of(-0.5));

        get().addDefault("ideas.imperialism.idea2.name", "Diplomatic Corps");
        get().addDefault("ideas.imperialism.idea2.desc", List.of(Tools.colorText("&fАдминистративная эффективность &a+2")));


        get().addDefault("ideas.imperialism.idea3.name", "State Propaganda");
        get().addDefault("ideas.imperialism.idea3.desc", List.of(Tools.colorText("&fНарушение перемирия не накладывает штраф, но стоит &c3&f£")));


        get().addDefault("ideas.imperialism.idea4.name", "Grand Army");
        get().addDefault("ideas.imperialism.idea4.desc", List.of(Tools.colorText("&a+25% &fк морали вам и вашим вассалам")));
        get().addDefault("ideas.imperialism.idea4.effectsId", List.of("moraleMod"));
        get().addDefault("ideas.imperialism.idea4.effects", List.of(0.25));



        get().addDefault("ideas.freedom.idea0.name", "Separatism");
        get().addDefault("ideas.freedom.idea0.desc", List.of(Tools.colorText("&a-50%&f к дани ")));
        get().addDefault("ideas.freedom.idea0.effectsId", List.of("tributeMod"));
        get().addDefault("ideas.freedom.idea0.effects", List.of(-0.5));

        get().addDefault("ideas.freedom.idea1.name", "Taxation with Representation");
        get().addDefault("ideas.freedom.idea1.desc", List.of(Tools.colorText("&a+10%&f доход от налогов")));
        get().addDefault("ideas.freedom.idea1.effectsId", List.of("taxMod"));
        get().addDefault("ideas.freedom.idea1.effects", List.of(0.1));

        get().addDefault("ideas.freedom.idea2.name", "Diplomatic Corps");
        get().addDefault("ideas.freedom.idea2.desc", List.of(Tools.colorText("&a+1&f к ПМА")));

        get().addDefault("ideas.freedom.idea3.name", "Experienced Diplomats");
        get().addDefault("ideas.freedom.idea3.desc", List.of(Tools.colorText("&a+50% &fк согласию поддержать независисмость")));

        get().addDefault("ideas.freedom.idea4.name", "Revanchism");
        get().addDefault("ideas.freedom.idea4.desc", List.of(Tools.colorText("&a+2&f реваншизма")));
        get().addDefault("ideas.freedom.idea4.effectsId", List.of("revanchism"));
        get().addDefault("ideas.freedom.idea4.effects", List.of(2.0));


        get().addDefault("ideas.science.idea0.name", "Patron of the Arts");
        get().addDefault("ideas.science.idea0.desc", List.of(Tools.colorText("&a-25%&f Стоимость строительства науч зданий")));
        get().addDefault("ideas.science.idea0.effectsId", List.of("scienceBuildingCost"));
        get().addDefault("ideas.science.idea0.effects", List.of(-0.25));

        get().addDefault("ideas.science.idea1.name", "Empiricism");
        get().addDefault("ideas.science.idea1.desc", List.of(Tools.colorText("&a+2&f прирост ОИ")));
        get().addDefault("ideas.science.idea1.effectsId", List.of("oiIncomeMod"));
        get().addDefault("ideas.science.idea1.effects", List.of(2.0));

        get().addDefault("ideas.science.idea2.name", "Print Culture");
        get().addDefault("ideas.science.idea2.desc", List.of(Tools.colorText("&a-10%&f стоимость технологий")));
        get().addDefault("ideas.science.idea2.effectsId", List.of("techCost"));
        get().addDefault("ideas.science.idea2.effects", List.of(-0.25));

        get().addDefault("ideas.science.idea3.name", "Expanded Policies");
        get().addDefault("ideas.science.idea3.desc", List.of(Tools.colorText("&a+0.5&f прирост ОИ от науч зданий")));
        get().addDefault("ideas.science.idea3.effectsId", List.of("oiFromBuilding"));
        get().addDefault("ideas.science.idea3.effects", List.of(0.5));
        //get().addDefault("ideas.science.idea3.desc", MiniMessage.miniMessage());

        get().addDefault("ideas.science.idea4.name", "Scientific Revolution");
        get().addDefault("ideas.science.idea4.desc", List.of(Tools.colorText("&fИсследуйте 1 технологию")));


        get().addDefault("ideas.revanchism.idea0.name", "Revanchism 1");
        get().addDefault("ideas.revanchism.idea0.desc", List.of(Tools.colorText("&a+20&f к максимумуऴ")));
        get().addDefault("ideas.revanchism.idea0.effectsId", List.of("manpowerLimitMod"));
        get().addDefault("ideas.revanchism.idea0.effects", List.of(0.2));

        get().addDefault("ideas.revanchism.idea1.name", "Revanchism 2");
        get().addDefault("ideas.revanchism.idea1.desc", List.of(Tools.colorText("&a-25%&f Стоимость строительства военных зданий")));
        get().addDefault("ideas.revanchism.idea1.effectsId", List.of("warBuildingCost"));
        get().addDefault("ideas.revanchism.idea1.effects", List.of(-0.25));

        get().addDefault("ideas.revanchism.idea2.name", "Revanchism 3");
        get().addDefault("ideas.revanchism.idea2.desc", List.of(
                Tools.colorText("&a-20%&f содержание войск"),
                Tools.colorText("&a+20%&f доход от налогов")));
        get().addDefault("ideas.revanchism.idea2.effectsId", List.of("armyExpenseMod","taxMod"));
        get().addDefault("ideas.revanchism.idea2.effects", List.of(-0.20,0.2));

        get().addDefault("ideas.revanchism.idea3.name", "Revanchism 4");
        get().addDefault("ideas.revanchism.idea3.desc", List.of(
                Tools.colorText("&a-25%&f стоимость строительства"),
                Tools.colorText("&ax2&f получение военных традиций")
        ));
        get().addDefault("ideas.revanchism.idea3.effectsId", List.of("buildingCost","traditionMod"));
        get().addDefault("ideas.revanchism.idea3.effects", List.of(-0.25,2.0));

        get().addDefault("ideas.revanchism.idea4.name", "Revanchism 5");
        get().addDefault("ideas.revanchism.idea4.desc", List.of(Tools.colorText("&fБонусы от реваншизма удваиваются")));
        get().addDefault("ideas.revanchism.idea4.effectsId", List.of("revanchismMod"));
        get().addDefault("ideas.revanchism.idea4.effects", List.of(2.0));


        get().addDefault("ideas.isolation.idea0.name", "isolation 1");
        get().addDefault("ideas.isolation.idea0.desc", List.of(Tools.colorText("&a-50%&fСтоимость повышения стабильности")));

        get().addDefault("ideas.isolation.idea1.name", "isolation 2");
        get().addDefault("ideas.isolation.idea1.desc", List.of(Tools.colorText("&a+25%&f сопротивление урону в фазе &6Шока")));
        get().addDefault("ideas.isolation.idea1.effectsId", List.of("shockResist"));
        get().addDefault("ideas.isolation.idea1.effects", List.of(0.25));

        get().addDefault("ideas.isolation.idea2.name", "isolation 3");
        get().addDefault("ideas.isolation.idea2.desc", List.of(Tools.colorText("&a-1&f$ спад стабильности")));

        get().addDefault("ideas.isolation.idea3.name", "isolation 4");
        get().addDefault("ideas.isolation.idea3.desc", List.of(
                Tools.colorText("&a+100%&f к приросту полит власти")
        ));
        get().addDefault("ideas.isolation.idea3.effectsId", List.of("politIncomeMod"));
        get().addDefault("ideas.isolation.idea3.effects", List.of(1.0));

        get().addDefault("ideas.isolation.idea4.name", "isolation 5");
        get().addDefault("ideas.isolation.idea4.desc", List.of(Tools.colorText("&a+0.2&fк производству везде")));
        get().addDefault("ideas.isolation.idea4.effectsId", List.of("goodsMod"));
        get().addDefault("ideas.isolation.idea4.effects", List.of(0.2));



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
