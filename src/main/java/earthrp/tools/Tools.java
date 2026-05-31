package earthrp.tools;

import earthrp.Earth;
import earthrp.battle.Battle;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;
import earthrp.customObjects.*;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import earthrp.menusystem.MenuUtility;
import me.clip.placeholderapi.libs.kyori.adventure.text.Component;
import me.clip.placeholderapi.libs.kyori.adventure.text.format.TextColor;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class Tools {



    public Tools(){}


    public static double round(double value){
        return BigDecimal
                .valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static double calculateDistance(int x1, int z1, int x2, int z2) {
        int dx = x2 - x1;
        int dz = z2 - z1;
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static void addStat(UUID uuid, String statId, double value, String name){
        ServerDatabase db = Earth.getInstance().getServerDatabase();
        EPlayer player = db.getPlayer(uuid);
        PlayerModifier mod = new PlayerModifier(name,name+statId,value, PlayerModifier.Operation.ADD);
        player.getData().addModifier(EPlayerAttribute.fromString(statId),mod);
        //player.addAttribute(EPlayerAttribute.fromString(statId),value);

    }

    public static void removeStat(UUID uuid, String statId, String name){
        ServerDatabase db = Earth.getInstance().getServerDatabase();
        EPlayer player = db.getPlayer(uuid);
        player.getData().removeModifier(EPlayerAttribute.fromString(statId),name+statId);
        //player.addAttribute(EPlayerAttribute.fromString(statId),value);

    }

    public static void backIdea(ItemStack item){
        String name = item.getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
        List<String> effectId = item.getItemMeta().getPersistentDataContainer().get(ideaEffectIdKey,PersistentDataType.LIST.strings());
        List<Double> effect = item.getItemMeta().getPersistentDataContainer().get(ideaEffectKey,PersistentDataType.LIST.doubles());
        UUID playerId = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(ideaOwnerKey, PersistentDataType.STRING));

        if((effectId!= null && effect!=null) && effect.size() == effectId.size()){
            for (int i = 0; i < effect.size(); i++) {
                removeStat(playerId,effectId.get(i),name);
            }
        }
    }

    public static void investIdea(ItemStack item){
        String name = item.getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
        List<String> effectId = item.getItemMeta().getPersistentDataContainer().get(ideaEffectIdKey,PersistentDataType.LIST.strings());
        List<Double> effect = item.getItemMeta().getPersistentDataContainer().get(ideaEffectKey,PersistentDataType.LIST.doubles());
        UUID playerId = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(ideaOwnerKey, PersistentDataType.STRING));

        if((effectId!= null && effect!=null) && effect.size() == effectId.size()){
            for (int i = 0; i < effect.size(); i++) {
                addStat(playerId,effectId.get(i),effect.get(i),name);
            }
        }
    }

    public static ItemStack createIdeaItem(Material material,String name,List<String> effectId, List<String> lore, List<Double> effect, UUID ownerId){
        ItemStack item = createItem(material,name,lore);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ideaNameKey,PersistentDataType.STRING, name);
        meta.getPersistentDataContainer().set(ideaEffectIdKey,PersistentDataType.LIST.strings(), effectId);
        meta.getPersistentDataContainer().set(ideaEffectKey,PersistentDataType.LIST.doubles(), effect);
        meta.getPersistentDataContainer().set(ideaOwnerKey,PersistentDataType.STRING, ownerId.toString());
        item.setItemMeta(meta);
        return item;
    }

    public static void giveIdea(MenuUtility mu){

    }



    public static void buyBuilding(EPlayer p, int buildingCost){
        if (p.getAttribute(EPlayerAttribute.TREASURY)>=buildingCost){
            p.addAttribute(EPlayerAttribute.TREASURY,-buildingCost);

        }
    }

    public static ItemStack createTownItem(Town town){
        ItemStack item = createItem(Material.END_CRYSTAL,town.getName(),List.of(town.getOwnerName()));
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(townIdKey, PersistentDataType.STRING,town.getUniqueId().toString());
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createBuildingBuy(Material material, String name, String type, List<String> lore, int cost, boolean techCheck, String modelId){
        if(!techCheck && lore!=null){
            //lore.addFirst(colorText(" "));
            //lore.addFirst(colorText("&cНе изучено"));
        }
        ItemStack building = createItem(material,colorText("&f" + name + " &6" + cost + "&f$"),lore);
        ItemMeta meta = building.getItemMeta();
        meta.getPersistentDataContainer().set(buildingNameKey,PersistentDataType.STRING, name);
        meta.getPersistentDataContainer().set(buildingTypeKey,PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(buildingCostKey,PersistentDataType.INTEGER, cost);
        meta.getPersistentDataContainer().set(buildingTechCheckKey,PersistentDataType.BOOLEAN, techCheck);
        meta.addEnchant(Enchantment.INFINITY,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(modelId));
        meta.setCustomModelDataComponent(cmd);

        building.setItemMeta(meta);
        return building;
    }

    public static ItemStack createBuildingBuy(Material material, String name, String type, List<String> lore, int cost, boolean techCheck){
        if(!techCheck && lore!=null){
            lore.addFirst(colorText(" "));
            lore.addFirst(colorText("&cНе изучено"));
        }
        ItemStack building = createItem(material,colorText("&f" + name + " &6" + cost + "&f$"),lore);
        ItemMeta meta = building.getItemMeta();
        meta.getPersistentDataContainer().set(buildingNameKey,PersistentDataType.STRING, name);
        meta.getPersistentDataContainer().set(buildingTypeKey,PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(buildingCostKey,PersistentDataType.INTEGER, cost);
        meta.getPersistentDataContainer().set(buildingTechCheckKey,PersistentDataType.BOOLEAN, techCheck);
        meta.addEnchant(Enchantment.INFINITY,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        building.setItemMeta(meta);
        return building;
    }



    public static ItemStack createBuilding(Material material, String name, String type, String modelId){
        ItemStack building = createItem(material,name,null);
        ItemMeta meta = building.getItemMeta();
        meta.getPersistentDataContainer().set(buildingTypeKey,PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(buildingIdKey,PersistentDataType.STRING, String.valueOf(UUID.randomUUID()));
        meta.addEnchant(Enchantment.INFINITY,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(modelId));
        meta.setCustomModelDataComponent(cmd);

        building.setItemMeta(meta);
        return building;

    }

    public static ItemStack createBuilding(Material material, String name, String type){
        ItemStack building = createItem(material,name,null);
        ItemMeta meta = building.getItemMeta();
        meta.getPersistentDataContainer().set(buildingTypeKey,PersistentDataType.STRING, type);
        meta.getPersistentDataContainer().set(buildingIdKey,PersistentDataType.STRING, String.valueOf(UUID.randomUUID()));
        meta.addEnchant(Enchantment.INFINITY,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);


        building.setItemMeta(meta);
        return building;

    }

    public static String getColorMod(double modifier, boolean negative, boolean flat){
        if(flat){
            String s = "&f";
            if (negative){
                if(modifier>0){s = "&c+";}
                if(modifier<0){s = "&a";}
            }else{
                if(modifier>0){s = "&a+";}
                if(modifier<0){s = "&c";}
            }

            return s+(int)modifier+"&f";
        }
        int mod = (int) Math.round(modifier*100)-100;
        String s = "&f";
        if (negative){
            if(modifier>1){s = "&c+";}
            if(modifier<1){s = "&a";}
        }else{
            if(modifier>1){s = "&a+";}
            if(modifier<1){s = "&c";}
        }

        return s+mod+"&f%";
    }

    public static String getColorMod(double modifier, boolean negative){
        int mod = (int) Math.round(modifier*100)-100;
        String s = "&f";
        if (negative){
            if(modifier>1){s = "&c+";}
            if(modifier<1){s = "&a";}
        }else{
            if(modifier>1){s = "&a+";}
            if(modifier<1){s = "&c";}
        }

        return s+mod+"&f%";
    }

    public static String getColorMod(double modifier){
        int mod = (int) Math.round(modifier*100)-100;
        String s = "&f";
        if(modifier>1){s = "&a+";}
        if(modifier<1){s = "&c";}
        return s+mod+"&f%";
    }

    public static ItemStack createArmyCraftItem(
            String displayName,List<String> lore, String type, int lvl, double disc, double fire, double shock, double morale){

        ItemStack item = new ItemStack(Material.EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(colorText(displayName));
        itemMeta.setLore(lore);
        itemMeta.getPersistentDataContainer().set(unitTypeKey, PersistentDataType.STRING, type);
        itemMeta.getPersistentDataContainer().set(unitLvlKey, PersistentDataType.INTEGER, lvl);
        itemMeta.getPersistentDataContainer().set(unitDiscKey, PersistentDataType.DOUBLE, disc);
        itemMeta.getPersistentDataContainer().set(unitFireKey, PersistentDataType.DOUBLE, fire);
        itemMeta.getPersistentDataContainer().set(unitShockKey, PersistentDataType.DOUBLE, shock);
        itemMeta.getPersistentDataContainer().set(unitMoraleKey, PersistentDataType.DOUBLE, morale);

        CustomModelDataComponent cmd = itemMeta.getCustomModelDataComponent();
        if(type.equals("art")) cmd.setStrings(List.of(type+(lvl-2)));
        else cmd.setStrings(List.of(type+lvl));
        itemMeta.setCustomModelDataComponent(cmd);

        item.setItemMeta(itemMeta);
        return item;
    }
    
    public static ItemStack createArmyCraftItem(
            String displayName,List<String> lore, String type, int lvl, double disc, double fire, double shock){

        ItemStack item = new ItemStack(Material.EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(colorText(displayName));
        itemMeta.setLore(lore);
        itemMeta.getPersistentDataContainer().set(unitTypeKey, PersistentDataType.STRING, type);
        itemMeta.getPersistentDataContainer().set(unitLvlKey, PersistentDataType.INTEGER, lvl);
        itemMeta.getPersistentDataContainer().set(unitDiscKey, PersistentDataType.DOUBLE, disc);
        itemMeta.getPersistentDataContainer().set(unitFireKey, PersistentDataType.DOUBLE, fire);
        itemMeta.getPersistentDataContainer().set(unitShockKey, PersistentDataType.DOUBLE, shock);

        CustomModelDataComponent cmd = itemMeta.getCustomModelDataComponent();
        if(type.equals("art")) cmd.setStrings(List.of(type+(lvl-2)));
        else cmd.setStrings(List.of(type+lvl));
        itemMeta.setCustomModelDataComponent(cmd);

        item.setItemMeta(itemMeta);
        return item;
    }
    
    

    public static ItemStack createDebtItem(int lvl){
        int debtSize;
        switch (lvl) {
            case 1 -> debtSize = 11;
            case 2 -> debtSize = 26;
            default -> debtSize = 6;
        };
        ItemStack item = new ItemStack(Material.PAPER);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName("Долг " + debtSize + " моры");
        itemMeta.setLore(List.of(colorText("&fНажмите чтобы вернуть")));
        itemMeta.getPersistentDataContainer().set(debtSizeKey,PersistentDataType.INTEGER,debtSize);
        itemMeta.getPersistentDataContainer().set(debtLvlKey,PersistentDataType.INTEGER,lvl);
        item.setItemMeta(itemMeta);
        return item;


    }

    public static ItemStack createMora(int amount){
        ItemStack mora = new ItemStack(Material.GOLD_NUGGET, amount);
        ItemMeta moraMeta = mora.getItemMeta();
        assert moraMeta != null;
        moraMeta.setDisplayName(ChatColor.YELLOW + "Мора");
        moraMeta.setLore(Collections.singletonList("1 мора"));

        CustomModelDataComponent cmd = moraMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of("mora"));
        moraMeta.setCustomModelDataComponent(cmd);

        mora.setItemMeta(moraMeta);
        return mora;
    }

    public static ItemStack createIdea(){
        ItemStack idea = new ItemStack(Material.SOUL_LANTERN);
        ItemMeta ideaMeta = idea.getItemMeta();
        ideaMeta.setDisplayName("Идея");
        ideaMeta.addEnchant(Enchantment.INFINITY,1,true);
        ideaMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        ideaMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING,"idea");
        idea.setItemMeta(ideaMeta);

        return idea;
    }

    public static ItemStack createRev(){
        ItemStack debt = new ItemStack(Material.PAPER, 1);
        ItemMeta debtMeta = debt.getItemMeta();
        debtMeta.setDisplayName("Возможность простить 1 долг");
        debtMeta.addEnchant(Enchantment.INFINITY,1,true);
        debtMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        debtMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING,"rev");
        debt.setItemMeta(debtMeta);
        return debt;
    }

    public static double CustomRound(double value, int n){
        return BigDecimal
                .valueOf(value)
                .setScale(n, RoundingMode.HALF_UP)
                .doubleValue();
    }

    public static void removeBattleHologram(Location loc) {
        for (Entity entity : loc.getChunk().getEntities()) {
            if (entity instanceof TextDisplay display) {
                if (display.getPersistentDataContainer().has(holoKey) && Objects.requireNonNull(display.getPersistentDataContainer().get(holoKey, PersistentDataType.STRING)).contains("battle")) {
                    display.remove();
                }
            }
        }
    }

    public static void removePreBattleHologram(Location loc) {
        for (Entity entity : loc.getChunk().getEntities()) {
            if (entity instanceof TextDisplay display) {
                if (display.getPersistentDataContainer().has(holoKey) && Objects.requireNonNull(display.getPersistentDataContainer().get(holoKey, PersistentDataType.STRING)).contains("preBattle")) {
                    display.remove();
                }
            }
        }
    }

    public static void spawnHologram(Location loc, String customName, String type, boolean visible) {



        ArmorStand hologram = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        hologram.setMarker(true);
        hologram.getPersistentDataContainer().set(holoKey, PersistentDataType.STRING, type);
        hologram.setVisible(false);
        hologram.setCustomNameVisible(visible);
        hologram.setCustomName(customName);
        hologram.setGravity(false);
        hologram.setCollidable(false);
        hologram.setInvulnerable(true); // Защита от случайного удаления
    }

    public static void spawnHologram(Location loc, String customName, String type) {

        TextDisplay textDisplay = loc.getWorld().spawn(loc, TextDisplay.class, display ->{
            display.setText(customName);
            display.setBackgroundColor(Color.fromARGB(255, 0, 0, 0));

            display.getPersistentDataContainer().set(holoKey, PersistentDataType.STRING, type);

            // Можно также включить/выключить тень текста
            display.setShadowed(true);

            // Настройка яркости (чтобы текст не темнел ночью)
            display.setBrightness(new Display.Brightness(15, 15));

            display.setBillboard(Display.Billboard.VERTICAL);
        });

    }

    public static void spawnPreBattleHologram(Location location, EPlayer attacker, EPlayer defender){

        Location spawnLoc = location.clone().add(0,2,0);

        spawnHologram(spawnLoc, attacker.getCountryName() + " vs " + defender.getCountryName(),"preBattleTitle");

        spawnHologram(spawnLoc.clone().add(0,-0.50,0), "Введите модификатор местности при помощи /battle","preBattleDesc");

    }

    public static void spawnBattleHologram(Battle battle){

        Location spawnLoc = battle.getLoc().add(0,2,0);
        EPlayer att = battle.getAttacker().getFirst();
        EPlayer def = battle.getDefender().getFirst();
        spawnHologram(spawnLoc, att.getCountryName() + " vs " + def.getCountryName(),"battleTitle");
        int i = 0;
        spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0), String.valueOf(Component.text("████ Привет ████").color(TextColor.color(0x555555))),"battlePhase");
        spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),"бросок кубика", "battleDice" );
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),"урон", "battleCas" );
        spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),"0 в бою 0", "battleTroops" );
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),"0 отступили 0", "battleRetreat");
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),att.getSize() + "резервы" + def.getSize(), "battleReserve");
        spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),att.getMoraleMod() + "мораль" + def.getMoraleMod(), "battleMorale" );
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),att.getTactic() + "тактика" +def.getTactic() , "battleTac" );


    }

    public static void editHologram(Location location, String type, String newValue ){
        for (Entity entity : location.getChunk().getEntities()) {
            if (entity instanceof TextDisplay display) {
                if (display.getPersistentDataContainer().has(holoKey) && display.getPersistentDataContainer().get(holoKey, PersistentDataType.STRING).equals(type) ) {
                    display.setText(Tools.colorText(newValue));
                }
            }
        }
    }

    public static void deleteHologram(Location location, String type){
        for (Entity entity : location.getChunk().getEntities()) {
            if (entity instanceof TextDisplay display) {
                if (display.getPersistentDataContainer().has(holoKey) && display.getPersistentDataContainer().get(holoKey, PersistentDataType.STRING).equals(type) ) {
                    display.remove();
                }
            }
        }
    }



    public static Army getArmyFromShulker(Block block){

        if (block.getState() instanceof ShulkerBox shulker) {
            return getArmyFromInventory(shulker.getInventory());
        }
        return null;
    }

    public static Army getArmyFromInventory(Inventory inventory){
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.hasItemMeta()) {

                ItemMeta meta = item.getItemMeta();
                if (meta.getPersistentDataContainer().has(armyIdKey, PersistentDataType.STRING)) {
                    UUID armyId = UUID.fromString(Objects.requireNonNull(meta.getPersistentDataContainer().get(armyIdKey, PersistentDataType.STRING)));
                    return(Earth.getInstance().getServerDatabase().getArmy(armyId));
                };
            }
        }
        return null;
    }



    public static ItemStack createItem(Material material, String displayName, List<String> lore, String customModel){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(customModel));
        meta.setCustomModelDataComponent(cmd);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String displayName, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack doomStick(){
        ItemStack item = createItem(Material.STICK,colorText("&4DoomStick"),List.of("earth debug stick"));
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(debugStickKey,PersistentDataType.BOOLEAN,true);
        item.setItemMeta(meta);
        return item;
    }



    public static ItemStack createTroopItem(Material material,String displayName, String type, int lvl, Army army, String customModel){
        int[] troops = army.getLvlTroops(type,lvl);
        List<String> lore = List.of(
                colorText("&fКоличество: &d" + troops[0]),
                colorText("&fЗаполненность: &a"+ troops[1])
        );
        return createItem(material,displayName,lore,customModel);

    }

    public static ItemStack createCountryStat(String displayName, List<String> lore, String statId){
        ItemStack item = new ItemStack(Material.BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);
        meta.getPersistentDataContainer().set(statIdKey,PersistentDataType.STRING,statId);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemTech(String techName, String techId, int techCost, int techStatus, List<String> techLore, int techCheck, String customModel){
        ItemStack item = new ItemStack(Material.EGG);
        ItemMeta itemMeta = item.getItemMeta();

        if (techStatus == 0) {
            itemMeta.setDisplayName(colorText("&f" + techName + " &b" + techCost + "&fૹ"));
            itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, false);

        } else {
            itemMeta.setDisplayName(colorText("&f" + techName));
            techLore.addFirst(colorText("&2Уже исследовано"));
            itemMeta.addEnchant(Enchantment.INFINITY, 1, true);
            itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, true);

        }
        if(techCheck==0){
            itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, false);
        }else{
            itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, true);
        }
        itemMeta.setLore(techLore);
        itemMeta.getPersistentDataContainer().set(techCostKey,PersistentDataType.INTEGER, techCost);
        itemMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, techId);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        CustomModelDataComponent cmd = itemMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of(customModel));
        itemMeta.setCustomModelDataComponent(cmd);

        item.setItemMeta(itemMeta);
        return item;
    }



//    public static ItemStack createItemTech(Material material, String techName, String techId, int techCost, int techStatus, List<String> techLore, int techCheck){
//        ItemStack item = new ItemStack(material);
//        ItemMeta itemMeta = item.getItemMeta();
//
//        if (techStatus == 0) {
//            itemMeta.setDisplayName(colorText("&f" + techName + " &b" + techCost + "&fૹ"));
//            itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, false);
//
//        } else {
//            itemMeta.setDisplayName(colorText("&f" + techName));
//            techLore.addFirst(colorText("&2Уже исследовано"));
//            itemMeta.addEnchant(Enchantment.INFINITY, 1, true);
//            itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, true);
//
//        }
//        if(techCheck==0){
//            itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, false);
//        }else{
//            itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, true);
//        }
//        itemMeta.setLore(techLore);
//        itemMeta.getPersistentDataContainer().set(techCostKey,PersistentDataType.INTEGER, techCost);
//        itemMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, techId);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        item.setItemMeta(itemMeta);
//        return item;
//    }

    public static ItemStack createWarItemTech(String techName, String techId, int techCost, int techStatus, List<String> techLore, int techCheck, List<String> effectId, List<Double> effects){
        ItemStack item;
        ItemMeta itemMeta;

        if (techStatus == 0) {
            item = new ItemStack(Material.BOOK);
            itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(colorText("&f" + techName + " &b" + techCost + "&fૹ"));
            itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, false);

        } else {
            item = new ItemStack(Material.ENCHANTED_BOOK);
            itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(colorText("&f" + techName));
            techLore.addFirst(colorText("&2Уже исследовано"));
            itemMeta.addEnchant(Enchantment.INFINITY, 1, true);
            itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, true);

        }
        if(techCheck==0){
            itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, false);
        }else{
            itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, true);
        }
        itemMeta.setLore(techLore);
        itemMeta.getPersistentDataContainer().set(techCostKey,PersistentDataType.INTEGER, techCost);
        itemMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, techId);
        itemMeta.getPersistentDataContainer().set(techEffectIdKey,PersistentDataType.LIST.strings(), effectId);
        itemMeta.getPersistentDataContainer().set(techEffectKey,PersistentDataType.LIST.doubles(), effects);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack createMainMenuItem(String name, String customModel, String mainMenuId){
        ItemStack item = createItem(Material.EGG,name,null,customModel);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(menuIdKey,PersistentDataType.STRING, mainMenuId);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItemTech(String techId, EPlayer player, String modelId){
        ItemStack item = new ItemStack(Material.ICE);
        ItemMeta itemMeta;
        double costMod = player.getAttribute(EPlayerAttribute.TECH_COST);
        boolean techStatus = player.getTech(EPlayerTech.fromString(techId));
        boolean techCheck = EPlayerTech.fromString(techId).canResearch(player.getTechMap());
        String techName = CustomConfig.get().getString("tech.name."+techId);
        List<String> techLore = CustomConfig.get().getStringList("tech.lore."+techId);
        int techCost = (int) Math.round(CustomConfig.get().getInt("tech.cost."+techId) * costMod );

        if (!techStatus) {
            itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(colorText("&f" + techName + " &b" + techCost + "&fૹ"));

        } else {
            itemMeta = item.getItemMeta();
            itemMeta.setDisplayName(colorText("&f" + techName));
            techLore.addFirst(colorText("&2Уже исследовано"));
            itemMeta.addEnchant(Enchantment.INFINITY, 1, true);
        }
        itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, techStatus);
        itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, techCheck);
        itemMeta.setLore(techLore);
        itemMeta.getPersistentDataContainer().set(techCostKey,PersistentDataType.INTEGER, techCost);
        itemMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, techId);
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        CustomModelDataComponent cmd = itemMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of(modelId));
        itemMeta.setCustomModelDataComponent(cmd);

        item.setItemMeta(itemMeta);
        return item;
    }

//    public static ItemStack createItemTech(Material material,String techId, EPlayer player){
//        ItemStack item = new ItemStack(material);
//        ItemMeta itemMeta;
//        double costMod = player.getAttribute(EPlayerAttribute.TECH_COST);
//        boolean techStatus = player.getTech(EPlayerTech.fromString(techId));
//        boolean techCheck = EPlayerTech.fromString(techId).canResearch(player.getTechMap());
//        String techName = CustomConfig.get().getString("tech.name."+techId);
//        List<String> techLore = CustomConfig.get().getStringList("tech.lore."+techId);
//        int techCost = (int) Math.round(CustomConfig.get().getInt("tech.cost."+techId) * costMod );
//
//        if (!techStatus) {
//            itemMeta = item.getItemMeta();
//            itemMeta.setDisplayName(colorText("&f" + techName + " &b" + techCost + "&fૹ"));
//
//        } else {
//            itemMeta = item.getItemMeta();
//            itemMeta.setDisplayName(colorText("&f" + techName));
//            techLore.addFirst(colorText("&2Уже исследовано"));
//            itemMeta.addEnchant(Enchantment.INFINITY, 1, true);
//        }
//        itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, techStatus);
//        itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, techCheck);
//        itemMeta.setLore(techLore);
//        itemMeta.getPersistentDataContainer().set(techCostKey,PersistentDataType.INTEGER, techCost);
//        itemMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, techId);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        item.setItemMeta(itemMeta);
//        return item;
//    }



//    public static ItemStack createItemTech(String techId, EPlayer player){
//        ItemStack item;
//        ItemMeta itemMeta;
//        double costMod = player.getAttribute(EPlayerAttribute.TECH_COST);
//        boolean techStatus = player.getTech(EPlayerTech.fromString(techId));
//        boolean techCheck = EPlayerTech.fromString(techId).canResearch(player.getTechMap());
//        String techName = CustomConfig.get().getString("tech.name."+techId);
//        List<String> techLore = CustomConfig.get().getStringList("tech.lore."+techId);
//        int techCost = (int) Math.round(CustomConfig.get().getInt("tech.cost."+techId) * costMod );
//
//        if (!techStatus) {
//            item = new ItemStack(Material.BOOK);
//            itemMeta = item.getItemMeta();
//            itemMeta.setDisplayName(colorText("&f" + techName + " &b" + techCost + "&fૹ"));
//
//        } else {
//            item = new ItemStack(Material.ENCHANTED_BOOK);
//            itemMeta = item.getItemMeta();
//            itemMeta.setDisplayName(colorText("&f" + techName));
//            techLore.addFirst(colorText("&2Уже исследовано"));
//            itemMeta.addEnchant(Enchantment.INFINITY, 1, true);
//        }
//        itemMeta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, techStatus);
//        itemMeta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, techCheck);
//        itemMeta.setLore(techLore);
//        itemMeta.getPersistentDataContainer().set(techCostKey,PersistentDataType.INTEGER, techCost);
//        itemMeta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, techId);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
//        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
//        item.setItemMeta(itemMeta);
//        return item;
//    }


    public static void techProcess(ItemStack item, EPlayer player, Player p){
        int oiBalance = (int) player.getAttribute(EPlayerAttribute.OI_BALANCE);
        EPlayerTech tech = EPlayerTech.fromString(item.getItemMeta().getPersistentDataContainer().get(techIdKey, PersistentDataType.STRING));
        boolean techCheck = item.getItemMeta().getPersistentDataContainer().get(techCheckKey, PersistentDataType.BOOLEAN);
        int techCost = item.getItemMeta().getPersistentDataContainer().get(techCostKey, PersistentDataType.INTEGER);
        if(!player.getTech(tech) && oiBalance >= techCost && techCheck){
            player.setTech(tech,true);
            player.addAttribute(EPlayerAttribute.OI_BALANCE, -techCost);
            for(EPlayerAttribute effect:tech.getEffect().keySet()){
                player.addAttribute(effect,tech.getEffect().get(effect));
            }
            ItemStack idea = createIdea();
            if(tech.equals(EPlayerTech.UNIVERSITY)){
                idea.setAmount(2);
                p.getInventory().addItem(idea);
            }
        }else {
            player.setTech(tech,false);
            player.addAttribute(EPlayerAttribute.OI_BALANCE, techCost);
            ItemStack idea = createIdea();
            if(tech.equals(EPlayerTech.UNIVERSITY)){
                idea.setAmount(2);
                p.getInventory().removeItem(idea);
            }
            for(EPlayerAttribute effect:tech.getEffect().keySet()){
                player.addAttribute(effect,-tech.getEffect().get(effect));
            }
        }


    }

    public static void techProcess(ItemStack item, EPlayer player){

        int oiBalance = (int) player.getAttribute(EPlayerAttribute.OI_BALANCE);
        EPlayerTech tech = EPlayerTech.fromString(item.getItemMeta().getPersistentDataContainer().get(techIdKey, PersistentDataType.STRING));
        if(tech == null) return;
        boolean techCheck = item.getItemMeta().getPersistentDataContainer().get(techCheckKey, PersistentDataType.BOOLEAN);
        int techCost = item.getItemMeta().getPersistentDataContainer().get(techCostKey, PersistentDataType.INTEGER);
        if(!player.getTech(tech) && oiBalance >= techCost && techCheck){
            player.setTech(tech,true);
            player.addAttribute(EPlayerAttribute.OI_BALANCE, -techCost);
            for(EPlayerAttribute effect:tech.getEffect().keySet()){
                player.addAttribute(effect,tech.getEffect().get(effect));
            }
        }else if (player.getTech(tech) && tech.canRefund(player.getTechMap())) {

            player.setTech(tech,false);
            player.addAttribute(EPlayerAttribute.OI_BALANCE, techCost);
            for(EPlayerAttribute effect:tech.getEffect().keySet()){
                player.addAttribute(effect,-tech.getEffect().get(effect));
            }
        }
    }

    public static double getDistanceSqrd(int x1, int z1, int x2, int z2){
        return Math.pow(x2-x1,2) + Math.pow(z2-z1,2);
    }

    public static ItemStack createCustomItemTech(Material type, String techName, int techCost, int techStatus, List<String> techLore){
        ItemStack item = new ItemStack(type);
        ItemMeta itemMeta = item.getItemMeta();

        if (techStatus == 0) {
            itemMeta.setDisplayName(colorText("&f" + techName + " &b" + techCost + "&fૹ"));

        } else {
            itemMeta.setDisplayName(colorText("&f" + techName));
            techLore.add(colorText("&2Уже исследовано"));
            itemMeta.addEnchant(Enchantment.INFINITY, 1, true);

        }
        itemMeta.setLore(techLore);
        item.setItemMeta(itemMeta);
        return item;
    }

//    public static int getProdIncBuild(Building b){
//        int mod = switch (b.getType()) {
//            case "plant" -> 2;
//            case "factory" -> 3;
//            default -> 1;
//        };
//        String item  = String.valueOf(b.getItem());
//        int itemCost = Earth.getInstance().getConfig().getInt("tradeItems."+item);
//        if(item != null && itemCost == 0) itemCost = Earth.getInstance().getConfig().getInt("tradeItems.STUFF");
//        return itemCost * (mod);
//    }

    public static int getBalance(EPlayer p){

        double inflation = Tools.round(1 - (p.getAttribute(EPlayerAttribute.INFLATION)*0.01));
        int income = (int) Math.round(p.getIncome() * inflation);
        double corruption = Tools.round(1 - p.getAttribute(EPlayerAttribute.CORRUPTION) * 0.1);
        return (int) Math.round( (income - p.getExpense()) * corruption );
    }

    public static String colorText(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }


}
