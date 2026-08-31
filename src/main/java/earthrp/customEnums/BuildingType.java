package earthrp.customEnums;

import earthrp.customObjects.EPlayer;
import earthrp.configs.BuildingConfig;
import earthrp.tools.Tools;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static earthrp.customEnums.BuildingType.BuildingGroupType.*;

@Getter
public enum BuildingType {

    FARM(STANDARD,Material.WHEAT,EPlayerTech.IRRIGATION,"Грядка",false,false,false,0),
    PASTURE(STANDARD,Material.LEATHER,EPlayerTech.LIVESTOCK,"Пастбище",false,false,false,0),
    MINE(STANDARD,Material.STONE_PICKAXE,EPlayerTech.MINING,"Шахта",0.5,false,false,0.1),
    PIT(STANDARD,Material.DIAMOND_PICKAXE,EPlayerTech.GUNPOWDER,"Рудник",1.0,false,false,0.25),
    QUARRY(STANDARD,Material.NETHERITE_PICKAXE,EPlayerTech.MANUFACTURE,"Карьер",2.0,false,false,0.5),
    LUMBER(STANDARD,Material.OAK_LOG,EPlayerTech.LUMBER,"Лесорубы",0.5,false,false,0.1),
    WORKSHOP(STANDARD,Material.CRAFTING_TABLE,EPlayerTech.ENGINEERING,"Мастерская",1,false,false,0.25),
    MANUFACTURE(STANDARD,Material.SMITHING_TABLE,EPlayerTech.MANUFACTURE,"Мануфактура",2,false,false,0.5),

    UNIVERSITY(SCIENCE,Material.LECTERN,EPlayerTech.UNIVERSITY,"Университет",true,false,0),
    LIBRARY(SCIENCE,Material.BOOKSHELF,EPlayerTech.WRITING,"Библиотека",true,false,0),

    BANK(STANDARD,Material.ENDER_CHEST,EPlayerTech.NATIONAL_BANK,"Нац Банк",false,false,0),
    MARKETPLACE(STANDARD,Material.BELL,EPlayerTech.TRADE,"Рынок",false,false,false,0),
    PORT(STANDARD,Material.BARREL,EPlayerTech.SHIPPING,"Порт",false,false,false,0),
    FISHER(STANDARD,Material.FISHING_ROD,EPlayerTech.SHIPPING,"Рыбацкий дом", false,0.5,false,false,0),

    BARRACK(WAR,Material.IRON_SWORD,EPlayerTech.BASE_MEDIEVAL_MILITARY,"Казармы",false,true,0),
    STABLE(WAR,Material.SADDLE,EPlayerTech.BASE_MEDIEVAL_MILITARY,"Конюшня",false,true,0),
    GUN_FACTORY(WAR,Material.FIRE_CHARGE,EPlayerTech.GUNPOWDER,"Оружейная фабрика",0.75,false,true,0),
    FORT(WAR,Material.STONE_BRICKS,EPlayerTech.FORT,"Крепость",false,false,true,0.1),
    FORGE(WAR,Material.ANVIL,EPlayerTech.IRON,"Кузня",0.5,false,true,0.1),
    SHIPYARD(WAR,Material.OAK_BOAT,EPlayerTech.SHIPBUILDING,"Верфь",false,true,0.1),

    BARN(STANDARD,Material.CHEST,EPlayerTech.BUILDING,"Амбар",false,false,false,0),
    COURTHOUSE(STANDARD,Material.CARTOGRAPHY_TABLE,EPlayerTech.EARLY_MODERN_ADMINISTRATION,"Суд",false,false,false,0),




    ;





    BuildingType(BuildingGroupType type, Material material,EPlayerTech tech,  String displayName, boolean buildSiteReq, double baseProduction,boolean science, boolean military, double hungerMod) {
        this.type = type;
        this.displayName = displayName;
        this.buildSiteReq = buildSiteReq;
        this.tech = tech;
        baseProd = baseProduction;
        this.science = science;
        this.military = military;
        this.hungerMod = hungerMod;
        this.material = material;
    }

    BuildingType(BuildingGroupType type, Material material,EPlayerTech tech, String displayName, double baseProduction,boolean science, boolean military, double hungerMod) {
        this(type,material,tech,displayName,true,baseProduction,science,military,hungerMod);
    }

    BuildingType(BuildingGroupType type, Material material,EPlayerTech tech, String displayName, boolean buildSiteReq,boolean science, boolean military, double hungerMod) {
        this(type,material,tech,displayName,buildSiteReq,0,science,military,hungerMod);
    }

    BuildingType(BuildingGroupType type, Material material,EPlayerTech tech, String displayName,boolean science, boolean military, double hungerMod) {
        this(type,material,tech,displayName,true,0,science,military,hungerMod);
    }



    @Getter
    private final EPlayerTech tech;

    @Getter
    private final BuildingGroupType type;

    @Getter
    private final Material material;

    @Getter
    private final String displayName;

    @Getter
    private final boolean buildSiteReq;

    @Getter
    private final boolean military;

    @Getter
    private final boolean science;

    @Getter
    private final double baseProd;

    @Getter
    private final double hungerMod;


    public int getCost(EPlayer player){

        if(isMilitary()){
            return  (int) Math.round(getBaseCost() * player.getWarBuildingCost());
        }
        if(isScience()){
            return  (int) Math.round(getBaseCost() * player.getScienceBuildingCost());
        }
        return (int)  Math.round(getBaseCost() * player.getAttribute(EPlayerAttribute.BUILDING_COST));
    }

    public int getBaseCost(){
        return BuildingConfig.get().getInt("buildings."+this+".cost");
    }

    public List<Component> getBaseLore() {
        List<Component> lore = new ArrayList<>();
        List<String> configLore = BuildingConfig.get().getStringList("buildings." + this + ".lore");


        Pattern pattern = Pattern.compile("%cost_(\\d+)%");

        for(String s:configLore){
            String processedLine = getProcessedBaseLine(s, pattern);

            lore.add(colorText(processedLine));
        }

        if(baseProd != -1) {
            lore.add(colorText(" "));
            lore.add(colorText("<white>Производительность <green>" + baseProd + " x насел"));
        }
        if(hungerMod != 0){
            lore.add(colorText("<white>Увеличивает <red>потребление <dark_green>пищи<white> в городе <yellow>+"+ (int) (hungerMod * 100) + "%"));
        }
        lore.add(colorText(" "));
        lore.add(colorText("<gray>Необходимы следующие технологии:"));
        lore.add(colorText("<red>"+tech.getName()));

        return lore;
    }


    public List<Component> getLore(EPlayer player) {
        List<Component> lore = new ArrayList<>();
        List<String> configLore = BuildingConfig.get().getStringList("buildings." + this + ".lore");


        Pattern pattern = Pattern.compile("%cost_(\\d+)%");

        for(String s:configLore){
            String processedLine = getProcessedLine(s, pattern,player);

            lore.add(colorText(processedLine));
        }



        if(baseProd != 0) {
            double prod = baseProd + player.getAttribute(EPlayerAttribute.GOODS_MOD);
            lore.add(colorText(" "));
            switch (this){
                case FISHER -> {
                    lore.add(colorText("<white>Производительность <green>" + prod + " x фрегаты"));
                }
                case LUMBER -> {
                    prod += player.getAttribute(EPlayerAttribute.LUMBER_PROD);
                    lore.add(colorText("<white>Производительность <green>" + prod + " x насел"));
                }
                case MINE,PIT,QUARRY -> {
                    prod += player.getAttribute(EPlayerAttribute.MINE_PROD);
                    lore.add(colorText("<white>Производительность <green>" + prod + " x насел"));
                }
                default -> {
                    lore.add(colorText("<white>Производительность <green>" + prod + " x насел"));
                }
            }


        }
        if(hungerMod != 0){
            lore.add(colorText("<white>Увеличивает <red>потребление <dark_green>пищи<white> в городе <yellow>+"+ (int) (hungerMod * 100) + "%"));
        }
        if (player.getTech(tech)) return lore;
        lore.add(colorText(" "));
        lore.add(colorText("<gray>Необходимы следующие технологии:"));
        lore.add(colorText("<red>"+tech.getName()));

        return lore;
    }

    private @NonNull String getProcessedBaseLine(String s, Pattern pattern) {
        Matcher matcher = pattern.matcher(s);
        StringBuilder sb = new StringBuilder();

        while (matcher.find()) {
            int baseValue = Integer.parseInt(matcher.group(1));

            matcher.appendReplacement(sb, String.valueOf(baseValue));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private @NonNull String getProcessedLine(String s, Pattern pattern, EPlayer player) {
        Matcher matcher = pattern.matcher(s);
        StringBuilder sb = new StringBuilder();
        double costMod = 1.0;
        switch (this.type){
            case STANDARD -> {
                costMod = player.getAttribute(EPlayerAttribute.BUILDING_COST);
            }
            case SCIENCE -> {
                costMod = player.getScienceBuildingCost();
            }
            case WAR -> {
                costMod = player.getWarBuildingCost();
            }
        }

        while (matcher.find()) {
            int baseValue = Integer.parseInt(matcher.group(1));

            // Твоя формула: Math.ceil(baseValue * costMod * traditionMod)
            // Примечание: для Крепости (FORT) у тебя в коде не было traditionMod.
            // Если для каких-то зданий логика отличается, это нужно будет отлавливать по `this`
            int finalCost = (int) Math.ceil(baseValue * costMod);

            matcher.appendReplacement(sb, String.valueOf(finalCost));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }






    public boolean isTech(EPlayer player){
        return player.getTech(tech);
    }

    public enum BuildingGroupType {

        WAR,
        STANDARD,
        SCIENCE,
    }

    public static BuildingType fromString(String text) {
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

    private static Component colorText(String text){

        return MiniMessage.miniMessage().deserialize("<!italic>" + text);
    }
}
