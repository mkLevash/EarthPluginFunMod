package earthrp.configs;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BuildingConfig {

    private final static BuildingConfig instance = new BuildingConfig();

    private static File file;
    private static FileConfiguration customFile;

    //генерация файла конфига
    public static void setup(){
        file = new File(Bukkit.getServer().getPluginManager().getPlugin("Earth").getDataFolder(), "BuildingConfig.yml");

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

        // === ПАСТБИЩЕ ===
        get().addDefault("buildings.PASTURE.lore", List.of(
                "<gold>%cost_32%<dark_purple>x<white>Бревно, <gold>%cost_12%<dark_purple>x<white>Сноп сена",
                "<white>Позволяет разводить скот.",
                "<white>Производительность зависит от <green>площади"
        ));
        get().addDefault("buildings.PASTURE.cost",48);


        // === ФЕРМА (ПЛАНТАЦИЯ) ===
        get().addDefault("buildings.FARM.lore", List.of(
                "<white>Позволяет выращивать культуры на грядках.",
                "<white>Плодородность зависит от <green>биома",
                "<white>Производительность зависит от <green>площади",
                "<white>1 грядка учитывает блоки в радиусе <yellow>13<white> блока"
        ));
        get().addDefault("buildings.FARM.cost",48);

        // === ЛЕСОПИЛКА ===
        get().addDefault("buildings.LUMBER.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Бревно; <gold>%cost_1%<dark_purple>x<white>Железный блок,Камнерез",
                " ",
                "<white>Добывает древесину."
        ));
        get().addDefault("buildings.LUMBER.cost",64);

        // === КАРЬЕР ===
        get().addDefault("buildings.QUARRY.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Каменный кирпич,Бревно,Фонарь;",
                "<gold>%cost_16%<dark_purple>x<white>Порох",
                "<gold>%cost_2%<dark_purple>x<white>Алмазный блок; <gold>%cost_6%<dark_purple>x<white>Железный блок;",
                "<gold>%cost_5%<dark_purple>x<white>Камнерез,Точило; <gold>%cost_3%<dark_purple>x<white>Наковальня;",
                " ",
                "<white>Производит сразу <blue>обр. металл<white>, ",
                "но <red>потребляет <blue>порох <white>и <blue>уголь"
        ));
        get().addDefault("buildings.QUARRY.cost",128);

        // === РУДНИК (MINE V2) ===
        get().addDefault("buildings.PIT.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Булыжник;",
                "<gold>%cost_32%<dark_purple>x<white>Бревно, Фонарь;",
                "<gold>%cost_4%<dark_purple>x<white>Железный блок; <gold>%cost_3%<dark_purple>x<white>Камнерез, Плавильня",
                " "
        ));
        get().addDefault("buildings.PIT.cost",96);

        // === ШАХТА (MINE V1) ===
        get().addDefault("buildings.MINE.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Булыжник,",
                "<gold>%cost_32%<dark_purple>x<white>Бревно,Факел",
                "<gold>%cost_2%<dark_purple>x<white>Камнерез,Железный блок;",
                " "
        ));
        get().addDefault("buildings.MINE.cost",64);

        // КАЗАРМА (barrack)
        get().addDefault("buildings.BARRACK.lore", List.of(
                "<gold>%cost_64%<dark_purple>x<white>Бревно,Стрел; <gold>%cost_16%<dark_purple>x<white>Мишень,Перо",
                " ",
                "<white>Позволяет создавать Пехоту 1+ уровня."
        ));
        get().addDefault("buildings.BARRACK.cost", 128);

        // КОНЮШНЯ (stable)
        get().addDefault("buildings.STABLE.lore", List.of(
                "<gold>%cost_64%<dark_purple>x<white>Бревно; <gold>%cost_32%<dark_purple>x<white>Перо,Кожа,Мишень",

                " ",
                "<white>Позволяет создавать Кавалерию 1+ уровня."
        ));
        get().addDefault("buildings.STABLE.cost", 160);

        // ОРУЖЕЙНАЯ ФАБРИКА (gunFactory)
        get().addDefault("buildings.GUN_FACTORY.lore", List.of(
                "<gold>%cost_256%<dark_purple>x<white>Бревно; <gold>%cost_64%<dark_purple>x<white>Порох,Каменный Кирпич",
                "<gold>%cost_6%<dark_purple>x<white>Железный блок,Алмазный блок",
                " ",
                "<white>Позволяет создавать Артиллерию."
        ));
        get().addDefault("buildings.GUN_FACTORY.cost", 256);

        // КРЕПОСТЬ (fort)
        get().addDefault("buildings.FORT.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Каменный кирпич; <gold>%cost_64%<dark_purple>x<white>Факел",
                " ",
                "<white>Не даёт противнику оккупировать город.",
                "<white>Нужно будет провести <light_purple>осаду.",
                "<white>Предупреждает о врагах в области <light_purple>250.",
                "<white>Не занимает <gold>ячейку строительства<white>"
        ));
        get().addDefault("buildings.FORT.cost", 48);

        // КУЗНЯ (forge)
        get().addDefault("buildings.FORGE.lore", List.of(
                "<gold>%cost_16%<dark_purple>x<white>Бревно, <gold>%cost_6%<dark_purple>x<white>Наковальня,Ведро лавы",
                "",
                "<green>+5%<light_purple> к резисту<white> в фазе<gold> шока.",
                "<white>Производит <light_purple>Оружие<white>,",
                "<white>Можно построить<gold> только одну<white> в городе."
        ));
        get().addDefault("buildings.FORGE.cost", 64);

        // ВЕРФЬ (shipyard)
        get().addDefault("buildings.SHIPYARD.lore", List.of(
                "<gold>%cost_32%<dark_purple>x<white>Бревно, <gold>%cost_12%<dark_purple>x<white>Бочка",
                "<gold>%cost_1%<dark_purple>x<white>Железный блок",
                "",
                "<white>Открывает возможность строить корабли.",
                "<green>+5<white> Лимит флота",
                "<white>Можно построить<gold> только одну<white> в городе."
        ));
        get().addDefault("buildings.SHIPYARD.cost", 64);

        // === ЗАВОД ===
//        get().addDefault("buildings.FACTORY.lore", List.of(
//                "<gold>%cost_128%<dark_purple>x<white>Кирпичный блок,Бревно; <gold>%cost_64%<dark_purple>x<white>Фонарь,Порох;",
//                "<gold>%cost_3%<dark_purple>x<white>Стол кузнеца,Наковальня,Плавильня;",
//                "<gold>%cost_5%<dark_purple>x<white>Алмазный блок,Железный блок,Угольный блок",
//                " ",
//                "<white>Производит <green>3<white> ресурса, которые",
//                "<white>уже производятся или <green>2 <dark_purple>пороха <white>",
//                " ",
//                "<white>Может перерабатывать сырые",
//                "<white>ресурсы в готовые изделия"
//        ));

        // === МАНУФАКТУРА ===
        get().addDefault("buildings.WORKSHOP.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Камень;",
                "<gold>%cost_32%<dark_purple>x<white>Бревно,Факел;",
                "<gold>%cost_3%<dark_purple>x<white>Верстак,Камнерез,Точило;",
                "<gold>%cost_1%<dark_purple>x<white>Железный блок,Угольный блок",
                " ",
                "<white>Перерабатывает сырые ресурсы",
                "<white>в готовые изделия"
        ));
        get().addDefault("buildings.WORKSHOP.cost",96);

        get().addDefault("buildings.MANUFACTURE.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Каменный кирпич;",
                "<gold>%cost_32%<dark_purple>x<white>Бревно,Фонарь;",
                "<gold>%cost_6%<dark_purple>x<white>Верстак,Камнерез,Точило;",
                "<gold>%cost_3%<dark_purple>x<white>Железный блок,Угольный блок",
                " ",
                "<white>Перерабатывает сырые ресурсы",
                "<white>в готовые изделия"
        ));
        get().addDefault("buildings.MANUFACTURE.cost",164);

        // === УНИВЕРСИТЕТ ===
        get().addDefault("buildings.UNIVERSITY.lore", List.of(
                "<gold>%cost_64%<dark_purple>x<white>Книжная полка,Стекло;",
                "<gold>%cost_32%<dark_purple>x<white>Каменный кирпич,Фонарь",
                "<gold>%cost_5%<dark_purple>x<white>Кафедра,Большой сундук;",
                " ",
                "<white>Увеличивает прирост ОИ на <green>3.",
                "<white>Производит <blue>книги.",
                "<white>Минимальное население для постройки в городе: <gold>15"
        ));
        get().addDefault("buildings.UNIVERSITY.cost",216);

        // === БАНК ===
        get().addDefault("buildings.BANK.lore", List.of(
                "<gold>%cost_128%<dark_purple>x<white>Стекло,Бревно,Каменный Кирпич,Бумага;",
                "<gold>%cost_16%<dark_purple>x<white>Большой сундук;",
                " ",
                "<white>Процентная ставка - 10%",
                "<white>Единичный долг - 10$."
        ));
        get().addDefault("buildings.BANK.cost",256);

        // === РЫНОК ===
        get().addDefault("buildings.MARKETPLACE.lore", List.of(
                "<gold>%cost_28%<dark_purple>x<white>Золото,Сундук;",
                "<gold>%cost_16%<dark_purple>x<white>Перо,Бревно",
                " ",
                "<white>Дает возможность получать доход от торговли"
        ));
        get().addDefault("buildings.MARKETPLACE.cost",32);

        // === ПОРТ ===
        get().addDefault("buildings.PORT.lore", List.of(
                "<gold>%cost_64%<dark_purple>x<white>Бревно",
                "<gold>%cost_32%<dark_purple>x<white>Бочка;",
                " ",
                "<white>Дает возможность построить верфь",
                "<white>Увеличивает доход от торговли"
        ));
        get().addDefault("buildings.PORT.cost",32);

        // === АМБАР ===
        get().addDefault("buildings.BARN.lore", List.of(
                "<gold>%cost_256%<dark_purple>x<white>Бревно",
                "<gold>%cost_6%<dark_purple>x<white>Сундук;",
                " ",
                "<white>Вместимость в городе +<green>10 368"
        ));
        get().addDefault("buildings.BARN.cost",32);

        // === СУД ===
        get().addDefault("buildings.COURTHOUSE.lore", List.of(
                "<gold>%cost_6%<dark_purple>x<white>Сундук;",
                "<gold>%cost_64%<dark_purple>x<white>Бревно,Каменный кирпич,Бумага",
                " ",
                "<white>Увеличивает прирост ПП <green>+1"
        ));
        get().addDefault("buildings.COURTHOUSE.cost",128);

        // === БИБЛИОТЕКА ===
        get().addDefault("buildings.LIBRARY.lore", List.of(
                "<gold>%cost_16%<dark_purple>x<white>Книжная полка,Стекло;",
                "<gold>%cost_32%<dark_purple>x<white>Бревно,Факел",
                " ",
                "<white>Увеличивает прирост ОИ <green>+1"
        ));
        get().addDefault("buildings.LIBRARY.cost",128);

        get().addDefault("buildings.FISHER.lore", List.of(
                "<gold>%cost_32%<dark_purple>x<white>Бревно",
                "<gold>%cost_6%<dark_purple>x<white>Бочка",
                " ",
                "Ловит рыбу"
        ));
        get().addDefault("buildings.FISHER.cost",16);




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
