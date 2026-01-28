package earthrp.files;

import earthrp.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        get().addDefault("status.mora","off");
        get().addDefault("status.day",3);

        get().addDefault("tech.cost.bankBase",15);
        get().addDefault("tech.name.bankBase","Банковское дело");
        get().addDefault("tech.lore.bankBase",List.of(Tools.colorText("&7Добавляет возможность строить банки")));

        get().addDefault("tech.cost.bankUp",80);
        get().addDefault("tech.name.bankUp","Развитая банковская система");
        get().addDefault("tech.lore.bankUp",List.of(
                Tools.colorText("&eДолжен быть построен банк"),
                Tools.colorText("&aСнижает процентную ставку до 4%"),
                Tools.colorText("&aЕдиничный долг - 25 моры"),
                Tools.colorText("&eУвеличивает стоимость ревизии в 2 раза")
        ));

        get().addDefault("tech.cost.trade",10);
        get().addDefault("tech.name.trade","Торговля");
        get().addDefault("tech.lore.trade",List.of(
                Tools.colorText("&7Позволяет собирать доходы с торговли"),
                Tools.colorText("&7открывает возможность экономических"),
                Tools.colorText("&7отношений с другими государствами"),
                Tools.colorText("&eПодробнее в правилах Глава 7")
            ));

        get().addDefault("tech.cost.shipping",10);
        get().addDefault("tech.name.shipping","Мореплавание");
        get().addDefault("tech.lore.shipping",List.of(Tools.colorText("&7Позволяет строить порты и транспортные корабли")));

        get().addDefault("tech.cost.railroad",90);
        get().addDefault("tech.name.railroad","Железные Дороги");
        get().addDefault("tech.lore.railroad",List.of(Tools.colorText("&7Даёт возможность строить железные дороги")));

        get().addDefault("tech.cost.diplomacy",10);
        get().addDefault("tech.name.diplomacy","Дипломатия");
        get().addDefault("tech.lore.diplomacy",List.of(
                Tools.colorText("&7Позволяет совершать дипломатические"),
                Tools.colorText("действия с другими гос-вами")));

        get().addDefault("tech.cost.officeBase",10);
        get().addDefault("tech.name.officeBase","Базовая канцелярия");
        get().addDefault("tech.lore.officeBase",List.of(
                Tools.colorText("&eДолжна быть исследована &bдипломатия"),
                Tools.colorText("&7Позволяет издавать базовые указы"),
                Tools.colorText("&7макс стоимость &610&f полит. власти")
        ));

        get().addDefault("tech.cost.officeUp",30);
        get().addDefault("tech.name.officeUp","Развитая бюрократия");
        get().addDefault("tech.lore.officeUp",List.of(
                Tools.colorText("&eДолжна быть исследована &bбазовая канцелярия"),
                Tools.colorText( "&7Позволяет издавать сложные законы и указы"),
                Tools.colorText( "&7без ограничения по политической власти")
        ));

        get().addDefault("tech.cost.school",30);
        get().addDefault("tech.name.school","Школы");
        get().addDefault("tech.lore.school",List.of(
                Tools.colorText( "&7Позволяет строить школы"),
                Tools.colorText( "&aТакже получаете 1 идею")
        ));

        get().addDefault("tech.cost.university",50);
        get().addDefault("tech.name.university","Университеты");
        get().addDefault("tech.lore.university",List.of(
                Tools.colorText( "&eДолжна быть исследована &bшкола"),
                Tools.colorText( "&7Позволяет строить университеты"),
                Tools.colorText( "&aТакже получаете 2 идеи")
        ));

        get().addDefault("tech.cost.ministry",100);
        get().addDefault("tech.name.ministry","Министерства");
        get().addDefault("tech.lore.ministry",List.of(
                Tools.colorText( "&7Позволяет строить министерства")
        ));

        get().addDefault("tech.cost.tech12",100);
        get().addDefault("tech.name.tech12","Многосторонние союзы");
        get().addDefault("tech.lore.tech12",List.of(
                Tools.colorText( "&eДолжна быть исследована &bбазовая канцелярия"),
                Tools.colorText( "&7Позволяет заключать военные союзы"),
                Tools.colorText( "&7в которых будет больше 2 стран")
        ));

        get().addDefault("tech.cost.adminEfficiency",100);
        get().addDefault("tech.name.adminEfficiency","Административная эффективность");
        get().addDefault("tech.lore.adminEfficiency",List.of(
                Tools.colorText( "&eДолжна быть исследована &bразвитая бюрократия"),
                Tools.colorText( "&7Увеличивает административную эффективность на 4")
        ));

        get().addDefault("tech.cost.pasture",10);
        get().addDefault("tech.name.pasture","Пастбища");
        get().addDefault("tech.lore.pasture",List.of(ChatColor.DARK_AQUA +"Позволяет разводить скот."));

        get().addDefault("tech.cost.lumber",10);
        get().addDefault("tech.name.lumber","Лесопилки");
        get().addDefault("tech.lore.lumber",List.of(ChatColor.DARK_AQUA +"Позволяет строить лесопилка."));

        get().addDefault("tech.cost.mine",15);
        get().addDefault("tech.name.mine","Шахта");
        get().addDefault("tech.lore.mine",List.of(ChatColor.DARK_AQUA +"Позволяет строить шахту."));

        get().addDefault("tech.cost.pit",40);
        get().addDefault("tech.name.pit","Рудник");
        get().addDefault("tech.lore.pit",List.of(
                ChatColor.YELLOW + "Должна быть исследована шахта",
                ChatColor.DARK_AQUA +"Позволяет строить рудник."));

        get().addDefault("tech.cost.quarry",75);
        get().addDefault("tech.name.quarry","Карьер");
        get().addDefault("tech.lore.quarry",List.of(
                ChatColor.YELLOW + "Должны быть исследованы рудник и порох",
                ChatColor.DARK_AQUA +"Позволяет строить карьер."));

        get().addDefault("tech.cost.forge",15);
        get().addDefault("tech.name.forge","Кузница");
        get().addDefault("tech.lore.forge",List.of(
                ChatColor.DARK_AQUA +"Позволяет строить кузницу."));

        get().addDefault("tech.cost.shipyard",15);
        get().addDefault("tech.name.shipyard","Верфь");
        get().addDefault("tech.lore.shipyard",List.of(
                ChatColor.YELLOW + "Должно быть исследовано мореплавание",
                ChatColor.DARK_AQUA +"Позволяет строить верфь."));

        get().addDefault("tech.cost.manufacture",50);
        get().addDefault("tech.name.manufacture","Мануфактура");
        get().addDefault("tech.lore.manufacture",List.of(
                ChatColor.DARK_AQUA +"Позволяет строить Мануфактуры."));

        get().addDefault("tech.cost.factory",80);
        get().addDefault("tech.name.factory","Завод");
        get().addDefault("tech.lore.factory",List.of(
                ChatColor.YELLOW + "Должны быть исследованы мануфактуры и порох",
                ChatColor.DARK_GREEN +"Позволяет строить Заводы."));

        get().addDefault("tech.cost.officeMil",10);
        get().addDefault("tech.name.officeMil","Военная канцелярия");
        get().addDefault("tech.lore.officeMil",List.of(
                Tools.colorText( "&eДолжна быть исследована &bбазовая канцелярия"),
                Tools.colorText( "&7Позволяет делать &dвоенные указы.")));

        get().addDefault("tech.cost.fort",15);
        get().addDefault("tech.name.fort","Крепость");
        get().addDefault("tech.lore.fort",List.of(
                Tools.colorText( "&7Позволяет строить &dкрепости.")));

        get().addDefault("tech.cost.levies",15);
        get().addDefault("tech.name.levies","Система призыва");
        get().addDefault("tech.lore.levies",List.of(Tools.colorText( "&a+10&f% &7к восстановлению &dMP&7")));

        get().addDefault("tech.cost.siege",30);
        get().addDefault("tech.name.siege","Осадные тактики");
        get().addDefault("tech.lore.siege",List.of(
                Tools.colorText( "&7Позволяет производить &dштурм крепости"),
                Tools.colorText( "&dСтартовая осада &a+10%" )));

        get().addDefault("tech.cost.metalPcg",30);
        get().addDefault("tech.name.metalPcg","Обработка металла");
        get().addDefault("tech.lore.metalPcg",List.of(Tools.colorText( "&a+15% &dСопротивления урону в фазу &6Шока.")));

        get().addDefault("tech.cost.standard",30);
        get().addDefault("tech.name.standard","Стандартизация");
        get().addDefault("tech.lore.standard",List.of(Tools.colorText( "&a+10% &dурона &fв фазе &4Огня.")));

        get().addDefault("tech.cost.heavyCav",30);
        get().addDefault("tech.name.heavyCav","Бронированная кавалерия");
        get().addDefault("tech.lore.heavyCav",List.of(Tools.colorText( "&a+25% &dКомбат Абилити &eКавалерии.")));

        get().addDefault("tech.cost.gunpowder",150);
        get().addDefault("tech.name.gunpowder","Порох");
        get().addDefault("tech.lore.gunpowder",List.of(
                Tools.colorText( "&7Открывает &5порох&7 и &5файрболы&7"),
                Tools.colorText( "&6Каждая пороховое предприятие потребляет &5уголь")
        ));

        get().addDefault("tech.cost.inf1",10);
        get().addDefault("tech.name.inf1","Пехота 1 уровня");
        get().addDefault("tech.lore.inf1",List.of(
                Tools.colorText("&fКопейщики"),
                Tools.colorText("&fМоральᠩ&23.0"),
                Tools.colorText("&fУрон - &40.8&f/&60.95"),
                Tools.colorText("&fОчки &41&f/&61&f/&20")
        ));

        get().addDefault("tech.cost.inf2",30);
        get().addDefault("tech.name.inf2","Пехота 2 уровня");
        get().addDefault("tech.lore.inf2",List.of(
                Tools.colorText("&fЛучники"),
                Tools.colorText("&fМоральᠩ&24.0"),
                Tools.colorText("&fУрон - &40.8&f/&60.95"),
                Tools.colorText("&fОчки &41&f/&61&f/&20")
        ));

        get().addDefault("tech.cost.inf3",50);
        get().addDefault("tech.name.inf3","Пехота 3 уровня");
        get().addDefault("tech.lore.inf3",List.of(
                Tools.colorText("&fАркебузиры"),
                Tools.colorText("&fМоральᠩ&25.0"),
                Tools.colorText("&fУрон - &42.1&f/&61.6"),
                Tools.colorText("&fОчки &42&f/&61&f/&21"),
                Tools.colorText( "&eДолжен быть исследован &bпорох")
        ));

        get().addDefault("tech.cost.inf4",75);
        get().addDefault("tech.name.inf4","Пехота 4 уровня");
        get().addDefault("tech.lore.inf4",List.of(
                Tools.colorText("&fМушкетёры"),
                Tools.colorText("&fМоральᠩ&26.0"),
                Tools.colorText("&fУрон - &43.1&f/&62.1"),
                Tools.colorText("&fОчки &43&f/&62&f/&22")
        ));

        get().addDefault("tech.cost.cav1",10);
        get().addDefault("tech.name.cav1","Кавалерия 1 уровня");
        get().addDefault("tech.lore.cav1",List.of(
                Tools.colorText("&fЛёгкие всадники"),
                Tools.colorText("&fМоральᠩ&23.0"),
                Tools.colorText("&fУрон - &40.0&f/&61.3"),
                Tools.colorText("&fОчки &40&f/&61&f/&21")
        ));

        get().addDefault("tech.cost.cav2",30);
        get().addDefault("tech.name.cav2","Кавалерия 2 уровня");
        get().addDefault("tech.lore.cav2",List.of(
                Tools.colorText("&fКонные лучники"),
                Tools.colorText("&fМоральᠩ&24.0"),
                Tools.colorText("&fУрон - &40.0&f/&62.0"),
                Tools.colorText("&fОчки &40&f/&62&f/&21")
        ));

        get().addDefault("tech.cost.cav3",50);
        get().addDefault("tech.name.cav3","Кавалерия 3 уровня");
        get().addDefault("tech.lore.cav3",List.of(
                Tools.colorText("&fТяжёлая кавалерия"),
                Tools.colorText("&fМоральᠩ&25.0"),
                Tools.colorText("&fУрон - &40.5&f/&63.0"),
                Tools.colorText("&fОчки &40&f/&63&f/&22")
        ));

        get().addDefault("tech.cost.cav4",75);
        get().addDefault("tech.name.cav4","Кавалерия 4 уровня");
        get().addDefault("tech.lore.cav4",List.of(
                Tools.colorText("&fКарабинеры"),
                Tools.colorText("&fМоральᠩ&26.0"),
                Tools.colorText("&fУрон - &41.0&f/&64.0"),
                Tools.colorText("&fОчки &41&f/&63&f/&23")
        ));

        get().addDefault("tech.cost.art1",75);
        get().addDefault("tech.name.art1","Артиллерия 1 уровня");
        get().addDefault("tech.lore.art1",List.of(
                Tools.colorText("&fБольшая чугунная пушка"),
                Tools.colorText("&fМоральᠩ&25.0"),
                Tools.colorText("&fУрон - &44.0&f/&60.5"),
                Tools.colorText("&fОчки &45&f/&62&f/&22"),
                Tools.colorText( "&eДолжна быть исследована &bпехота &33&b уровня")
        ));

        get().addDefault("tech.cost.art2",100);
        get().addDefault("tech.name.art2","Артиллерия 2 уровня");
        get().addDefault("tech.lore.art2",List.of(
                Tools.colorText("&fТяжёлая гаубица"),
                Tools.colorText("&fМоральᠩ&26.0"),
                Tools.colorText("&fУрон - &48.0&f/&61.5"),
                Tools.colorText("&fОчки &48&f/&64&f/&24")
        ));

        get().addDefault("tech.cost.tech40",10);
        get().addDefault("tech.name.tech40","ревизия");


        get().addDefault("tech.cost.tech41",10);
        get().addDefault("tech.name.tech41","идеи");

        get().addDefault("trade.playerName.playerName",true);

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
