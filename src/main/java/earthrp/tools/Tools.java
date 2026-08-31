package earthrp.tools;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import earthrp.Earth;
import earthrp.battle.Battle;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;
import earthrp.customEnums.UnitTech;
import earthrp.customObjects.*;
import earthrp.database.ServerDatabase;
import earthrp.configs.CustomConfig;
import earthrp.menusystem.MenuUtility;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class Tools {



    public Tools(){}


    public static final UUID EMPTY_UUID = new UUID(0L, 0L);

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

    public static void addIdeaModifier(UUID ownerId, List<EPlayerAttribute> attributes, List<String> desc, List<Double> value, String name, String material){
        ServerDatabase db = Earth.getInstance().getDatabase();
        EPlayer player = db.getPlayer(ownerId);
        PlayerModifier mod = new PlayerModifier(name,name+ownerId,desc,value, PlayerModifier.Operation.ADD,-1,attributes, material);
        player.getData().addModifier(mod);
        //player.addAttribute(EPlayerAttribute.fromString(statId),value);

    }

    public static void removeIdeaModifier(UUID ownerId, String name){
        ServerDatabase db = Earth.getInstance().getDatabase();
        EPlayer player = db.getPlayer(ownerId);
        player.getData().removeModifier(player.getData().getModifier(name+ownerId));
        //player.addAttribute(EPlayerAttribute.fromString(statId),value);

    }

    public static void backIdea(ItemStack item){
        String name = item.getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
        List<String> effectId = item.getItemMeta().getPersistentDataContainer().get(ideaEffectIdKey,PersistentDataType.LIST.strings());
        List<Double> effect = item.getItemMeta().getPersistentDataContainer().get(ideaEffectKey,PersistentDataType.LIST.doubles());
        UUID playerId = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(ideaOwnerKey, PersistentDataType.STRING));
        EPlayer country = Earth.getInstance().getDatabase().getPlayer(playerId);
        if (country == null) return;
        PlayerData data = country.getData();
        if(name != null){
            if(name.contains("Revanchism")){
                switch (name.substring(name.length()-1)){
                    case "1" ->{
                        data.setRevanchism0(false);
                    }
                    case "2" ->{
                        data.setRevanchism1(false);
                    }
                    case "3" ->{
                        data.setRevanchism2(false);
                    }
                    case "4" ->{
                        data.setRevanchism3(false);
                    }
                }
            }
            if(name.contains("State Propaganda")){
                country.setImperialism(false);
            }

        }
        removeIdeaModifier(playerId,name);

    }

    public static void investIdea(ItemStack item){
        String name = item.getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
        List<String> effectId = item.getItemMeta().getPersistentDataContainer().get(ideaEffectIdKey,PersistentDataType.LIST.strings());
        List<Double> effect = item.getItemMeta().getPersistentDataContainer().get(ideaEffectKey,PersistentDataType.LIST.doubles());
        UUID playerId = UUID.fromString(item.getItemMeta().getPersistentDataContainer().get(ideaOwnerKey, PersistentDataType.STRING));
        EPlayer country = Earth.getInstance().getDatabase().getPlayer(playerId);
        if (country == null) return;
        PlayerData data = country.getData();
        if(name != null){
            if(name.contains("Revanchism")){
                switch (name.substring(name.length()-1)){
                    case "1" ->{
                        data.setRevanchism0(true);

                    }
                    case "2" ->{
                        data.setRevanchism1(true);
                    }
                    case "3" ->{
                        data.setRevanchism2(true);
                    }
                    case "4" ->{
                        data.setRevanchism3(true);
                    }
                }
            }
            if(name.contains("State Propaganda")){
                country.setImperialism(true);
            }

        }

        if((effectId!= null && effect!=null) && effect.size() == effectId.size()){
            List<EPlayerAttribute> attributes = new ArrayList<>();
            List<Double> value = new ArrayList<>();
            for (int i = 0; i < effect.size(); i++) {
                attributes.add(EPlayerAttribute.fromString(effectId.get(i)));
                value.add(effect.get(i));
            }

            List<String> lore = new ArrayList<>();
            List<Component> loreComponents = item.lore();
            if (loreComponents == null) return;

            for (Component line : loreComponents) {
                lore.add(Tools.serialize(line));
            }
            addIdeaModifier(playerId,attributes,lore,value,name,item.getType().toString());
        }
    }

    public static ItemStack createIdeaItemLegacy(Material material, String name, List<String> effectId, List<String> lore, List<Double> effect, UUID ownerId){
        ItemStack item = Tools.createItemLegacy(material,name,lore);
        ItemMeta meta = item.getItemMeta();
        meta.getPersistentDataContainer().set(ideaNameKey,PersistentDataType.STRING, name);
        meta.getPersistentDataContainer().set(ideaEffectIdKey,PersistentDataType.LIST.strings(), effectId);
        meta.getPersistentDataContainer().set(ideaEffectKey,PersistentDataType.LIST.doubles(), effect);
        meta.getPersistentDataContainer().set(ideaOwnerKey,PersistentDataType.STRING, ownerId.toString());
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createIdeaItem(Material material, String name, List<String> effectId, List<String> lore, List<Double> effect, UUID ownerId){
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
        ItemStack item = Tools.createItemLegacy(Material.END_CRYSTAL,town.getName(),List.of(town.getOwnerName()));
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
        ItemStack building = Tools.createItemLegacy(material,colorText("&f" + name + " &6" + cost + "&f$"),lore);
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
        ItemStack building = Tools.createItemLegacy(material,colorText("&f" + name + " &6" + cost + "&f$"),lore);
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

    public static String getColorModLegacy(double modifier, boolean negative, boolean flat){
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

    public static String getColorModComponent(double modifier, boolean negative, boolean flat){
        if(flat){
            String s = "<white>";
            if (negative){
                if(modifier>0){s = "<red>+";}
                if(modifier<0){s = "<green>";}
            }else{
                if(modifier>0){s = "<green>+";}
                if(modifier<0){s = "<red>";}
            }

            return s+(int)modifier+"<white>";
        }
        int mod = (int) Math.round((modifier-1)*100);
        String s = "<white>";
        if (negative){
            if(modifier>1){s = "<red>+";}
            if(modifier<1){s = "<green>";}
        }else{
            if(modifier>1){s = "<green>+";}
            if(modifier<1){s = "<red>";}
        }

        return s+mod+"<white>%";
    }

    public static String getColorModLegacy(double modifier, boolean negative){
        int mod = (int) Math.round((modifier-1)*100);
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

    public static String getColorModLegacy(double modifier){
        int mod = (int) Math.round((modifier-1)*100);
        String s = "&7";
        if(modifier>1){s = "&a+";}
        if(modifier<1){s = "&c";}
        return s+mod+"&f%";
    }



    public static ItemStack createArmyCraftItem(UnitTech type, double disc, double morale,boolean isLevies,boolean isMerc){

        ItemStack item = new ItemStack(Material.EGG);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(colorText(type.getDisplayName()));
        List<Component> lore = new ArrayList<>();
        lore.add(deserialize("Мораль <dark_green>"+morale));
        lore.add(deserialize("Дисциплина "+ (int) (disc * 100) + "<white>%") );
        lore.add(deserialize("Урон <red>"+type.getFire() + "<white>/<gold>"+type.getShock()));
        lore.add(deserialize("Очки <red>"+type.getFirePips() + "<white>/<gold>"+type.getShockPips() + "<white>/<dark_green>" + type.getMoralePips()));
        itemMeta.lore(lore);
        itemMeta.getPersistentDataContainer().set(unitTypeKey, PersistentDataType.STRING, type.toString());
        itemMeta.getPersistentDataContainer().set(unitDiscKey, PersistentDataType.DOUBLE, disc);
        itemMeta.getPersistentDataContainer().set(unitMoraleKey, PersistentDataType.DOUBLE, type.getMorale());
        itemMeta.getPersistentDataContainer().set(unitIsLeviesKey, PersistentDataType.BOOLEAN, isLevies);
        itemMeta.getPersistentDataContainer().set(unitIsMercKey, PersistentDataType.BOOLEAN, isMerc);

        CustomModelDataComponent cmd = itemMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of(type.toString().toLowerCase(Locale.ROOT)));
        itemMeta.setCustomModelDataComponent(cmd);

        item.setItemMeta(itemMeta);
        return item;
    }

    public static ItemStack createArmyCraftItem(UnitTech tech){

        return createArmyCraftItem(tech,0.0,tech.getMorale(),false,false);

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

    public static ItemStack createMoraIngot(int amount){
        ItemStack mora = new ItemStack(Material.GOLD_INGOT, amount);
        ItemMeta moraMeta = mora.getItemMeta();
        assert moraMeta != null;
        moraMeta.setDisplayName(ChatColor.YELLOW + "Горсть Моры");
        moraMeta.setLore(Collections.singletonList("9 моры"));

        CustomModelDataComponent cmd = moraMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of("moraIngot"));
        moraMeta.setCustomModelDataComponent(cmd);

        mora.setItemMeta(moraMeta);
        return mora;
    }

    public static ItemStack createMoraBlock(int amount){
        ItemStack mora = new ItemStack(Material.GOLD_BLOCK, amount);
        ItemMeta moraMeta = mora.getItemMeta();
        assert moraMeta != null;
        moraMeta.setDisplayName(ChatColor.YELLOW + "Мора");
        moraMeta.setLore(Collections.singletonList("81 мора"));

        CustomModelDataComponent cmd = moraMeta.getCustomModelDataComponent();
        cmd.setStrings(List.of("moraBlock"));
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

    public static ItemStack createMilitaryIdea(){
        ItemStack idea = new ItemStack(Material.LANTERN);
        ItemMeta ideaMeta = idea.getItemMeta();
        ideaMeta.setDisplayName("Военная реформа");
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

    public static void spawnHologram(Location loc, String customName, String type) {

        TextDisplay textDisplay = loc.getWorld().spawn(loc, TextDisplay.class, display ->{
            display.text(deserialize(customName));
            display.setBackgroundColor(Color.fromARGB(255, 0, 0, 0));

            display.getPersistentDataContainer().set(holoKey, PersistentDataType.STRING, type);

            // Можно также включить/выключить тень текста
            display.setShadowed(true);

            // Настройка яркости (чтобы текст не темнел ночью)
            display.setBrightness(new Display.Brightness(15, 15));

            display.setBillboard(Display.Billboard.VERTICAL);
        });

    }

    public static void spawnHologramLegacy(Location loc, String customName, String type, boolean visible) {



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

    public static void spawnHologramLegacy(Location loc, String customName, String type) {

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

        spawnHologramLegacy(spawnLoc, attacker.getCountryName() + " vs " + defender.getCountryName(),"preBattleTitle");

        spawnHologramLegacy(spawnLoc.clone().add(0,-0.50,0), "Введите модификатор местности при помощи /battle","preBattleDesc");

    }

    public static void spawnBattleHologram(Battle battle){

        Location spawnLoc = battle.getLoc().add(0,2,0);
        EPlayer att = battle.getAttacker();
        EPlayer def = battle.getDefender();
        spawnHologramLegacy(spawnLoc, att.getCountryName() + " vs " + def.getCountryName(),"battleTitle");
        int i = 0;
        spawnHologramLegacy(spawnLoc.clone().add(0,-0.25*++i,0), String.valueOf(Component.text("████ Привет ████").color(TextColor.color(0x555555))),"battlePhase");
        spawnHologramLegacy(spawnLoc.clone().add(0,-0.25*++i,0),"бросок кубика", "battleDice" );
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),"урон", "battleCas" );
        spawnHologramLegacy(spawnLoc.clone().add(0,-0.25*++i,0),"0 в бою 0", "battleTroops" );
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),"0 отступили 0", "battleRetreat");
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),att.getSize() + "резервы" + def.getSize(), "battleReserve");
        spawnHologramLegacy(spawnLoc.clone().add(0,-0.25*++i,0),att.getMoraleMod() + "мораль" + def.getMoraleMod(), "battleMorale" );
        //spawnHologram(spawnLoc.clone().add(0,-0.25*++i,0),att.getTactic() + "тактика" +def.getTactic() , "battleTac" );


    }

    public static void editHologramLegacy(Location location, String type, String newValue ){
        for (Entity entity : location.getChunk().getEntities()) {
            if (entity instanceof TextDisplay display) {
                if (display.getPersistentDataContainer().has(holoKey) && display.getPersistentDataContainer().get(holoKey, PersistentDataType.STRING).equals(type) ) {
                    display.setText(Tools.colorText(newValue));
                }
            }
        }
    }

    public static void editHologram(Location location, String type, String newValue ){
        editHologram(location.getChunk(),type,newValue);
    }
    public static void editHologram(long location, String type, String newValue){
        editHologram(Bukkit.getWorlds().getFirst().getChunkAt(location),type, newValue);
    }



    public static TextDisplay findHologram(Chunk chunk, String type){
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof TextDisplay display) {
                if (display.getPersistentDataContainer().has(holoKey) && display.getPersistentDataContainer().get(holoKey, PersistentDataType.STRING).equals(type) ) {
                   return display;
                }
            }
        }
        return null;
    }

    public static void editHologram(Chunk chunk, String type, String newValue ){
        if(chunk.isLoaded()){
            TextDisplay holo = findHologram(chunk,type);
            if(holo != null){
                holo.text(Tools.deserialize(newValue));
            }
        }else{
            Long chunkKey = chunk.getChunkKey();
            ServerDatabase.PendingTask task = new ServerDatabase.PendingTask(ServerDatabase.TaskType.EDIT,type,newValue);
            ServerDatabase db = Earth.getInstance().getDatabase();
            Set< ServerDatabase.PendingTask > tasks = db.getHoloTasks(chunkKey);
            tasks.add(task);
            db.putHoloTasks(chunkKey,tasks);
        }

    }

    public static void deleteHologram(Chunk chunk, String type){
        if(chunk.isLoaded()){
            TextDisplay holo = findHologram(chunk,type);
            if(holo != null){
                holo.remove();
            }
        }else{
            Long chunkKey = chunk.getChunkKey();
            ServerDatabase.PendingTask task = new ServerDatabase.PendingTask(ServerDatabase.TaskType.DELETE,type,"");
            ServerDatabase db = Earth.getInstance().getDatabase();
            Set< ServerDatabase.PendingTask > tasks = db.getHoloTasks(chunkKey);
            tasks.add(task);
            db.putHoloTasks(chunkKey,tasks);
        }

    }

    public static void deleteHologram(long location, String type){
        deleteHologram(Bukkit.getWorlds().getFirst().getChunkAt(location),type);
    }

    public static void deleteHologram(Location location, String type){
        deleteHologram(location.getChunk(),type);
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
                    return(Earth.getInstance().getDatabase().getArmy(armyId));
                };
            }
        }
        return null;
    }



    public static ItemStack createItemLegacy(Material material, String displayName, List<String> lore, String customModel){
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

    public static ItemStack createItemLegacy(Material material, String displayName, List<String> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        meta.setLore(lore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create();

        // 2. Добавляем фейковый модификатор (+0.0 к урону)
        // Так как он равен 0, он не изменит стандартный урон меча (он останется 7.0)
        modifiers.put(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                NamespacedKey.minecraft("fake_hidden_modifier"),
                0.0,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));

        // 3. Записываем этот модификатор в мету
        meta.setAttributeModifiers(modifiers);

        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String displayName, List<String> lore,String customModel){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(deserialize(displayName));
        List<Component> cLore = new ArrayList<>();
        if(lore!=null){
            for(String s:lore){
                cLore.add(deserialize(s));
            }
        }

        meta.lore(cLore);

        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create();

        // 2. Добавляем фейковый модификатор (+0.0 к урону)
        // Так как он равен 0, он не изменит стандартный урон меча (он останется 7.0)
        modifiers.put(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                NamespacedKey.minecraft("fake_hidden_modifier"),
                0.0,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));

        // 3. Записываем этот модификатор в мету
        meta.setAttributeModifiers(modifiers);

        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(customModel));
        meta.setCustomModelDataComponent(cmd);


        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(Material material, String displayName, List<String> lore){
        return createItem(material,displayName,lore,"");
    }

    public static ItemStack doomStick(){
        ItemStack item = Tools.createItemLegacy(Material.STICK,colorText("&4DoomStick"),List.of("earth debug stick"));
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
        return createItemLegacy(material,displayName,lore,customModel);

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
        ItemStack item = createItemLegacy(Material.EGG,name,null,customModel);
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



    public static String colorText(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public static Component deserialize(String text){
        return MiniMessage.miniMessage().deserialize("<!italic><white>" + text);
    }

    public static String serialize(Component text){
        return MiniMessage.miniMessage().serialize(text);
    }


    public static ItemStack createDebtItem(EPlayer player, UUID debtId) {
        double debtSize = player.getData().getDebtMap().get(debtId);
        double interest = 1.0 + player.getData().getInterestMap().get(debtId);
        int size = (int) Math.ceil(debtSize * interest);

        List<String> lore = List.of("Процентная ставка <yellow>" +( (int)Math.round(interest * 100) - 100 )+ "%","Нажмите чтобы вернуть <red>" + size + "<white>$");
        ItemStack debt = createItem(Material.PAPER,"Долг " + debtSize + "$",lore);
        ItemMeta meta = debt.getItemMeta();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        data.set(debtIdKey,PersistentDataType.STRING,debtId.toString());
        data.set(debtSizeKey,PersistentDataType.INTEGER,size);
        debt.setItemMeta(meta);
        return debt;
    }
}
