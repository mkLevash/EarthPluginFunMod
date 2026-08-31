package earthrp.customObjects;

import com.google.gson.Gson;
import earthrp.Earth;
import earthrp.battle.Battle;
import earthrp.customEnums.EPlayerTech;
import earthrp.customEnums.UnitTech.UnitType;
import earthrp.events.ArmyMoveEvent;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static earthrp.customEnums.UnitTech.UnitType.*;
import static earthrp.tools.PDCKeys.*;


@Getter
@Setter
public class Army implements Comparable<Army>{

    @Getter(AccessLevel.NONE) // <--- не сгенерирует getter
    private final ServerDatabase db;

    private final UUID uuid;

    //data
    private static final Gson gson = new Gson();
    private ArmyData data;
    private String rawJson;    // То, что пришло из БД


    public void loadData(String json) {
        if (json == null || json.isEmpty()) {
            this.data = new ArmyData();
        } else {
            this.data = gson.fromJson(json, ArmyData.class);
        }
    }

    // Вызываем перед сохранением в БД
    public String serializeData() {
        return gson.toJson(this.data);
    }

    public ArmyData getData() {
        if (this.data == null) this.data = new ArmyData();
        return this.data;
    }
    //data

    public Army(UUID uuid, UUID ownerId, String data){
        this.uuid = uuid;
        this.ownerId = ownerId;
        if (data == null) data = "";
        loadData(data);
        db = Earth.getInstance().getDatabase();
    }

    public Army(UUID uuid, UUID ownerId,Location loc){
        this.uuid = uuid;
        this.ownerId = ownerId;
        loadData("");
        db = Earth.getInstance().getDatabase();
        setLocation(loc);
    }




    private UUID ownerId;

    private String leaderName;
    private int leaderFire;
    private int leaderShock;

    public void setLeaderMove(int value){
        data.setLeaderMovement(value);
    }

    public int getLeaderMove(){
        return data.getLeaderMovement();
    }

    public void setLeaderSiege(int value){
        data.setLeaderSiege(value);
    }

    public int getLeaderSiege(){
        return data.getLeaderSiege();
    }


    private final Set<ArmyUnit> units = new HashSet<>();

    public Set<ArmyUnit> getUnits() {
        return Collections.unmodifiableSet(units);
    }

    public void addUnit(ArmyUnit unit) {
        units.add(unit);
        db.addUnit(unit);

    }

    public void removeUnit(ArmyUnit unit) {
        this.units.remove(unit);
    }

    public int getInfantry(){
        int amount = 0;
        for (ArmyUnit u:units){
            if(u.getType().equals(INF)) amount++;
        }
        return amount;
    }
    public int getCavalry(){
        int amount = 0;
        for (ArmyUnit u:units){
            if(u.getType().equals(CAV)) amount++;
        }
        return amount;
    }

    public int getArtillery(){
        int amount = 0;
        for (ArmyUnit u:units){
            if(u.getType().equals(ART)) amount++;
        }
        return amount;
    }

    public int getArtilleryTroops(){
        int amount = 0;
        for (ArmyUnit u:units){
            if(u.getType().equals(ART)) amount+= u.getHp();
        }
        return amount;
    }





    public EPlayer getOwner(){
        return db.getPlayer(ownerId);
    }
    
    
    public boolean isSieging(){
        return !getData().getSiegeTown().equals(Tools.EMPTY_UUID);
    }







    public String getCA(String type){
        double ca = 0.0;
        switch (type){
            case "inf" -> ca = getOwner().getAttribute(EPlayerAttribute.INF_COMBAT_ABILITY);
            case "cav" -> ca = getOwner().getAttribute(EPlayerAttribute.CAV_COMBAT_ABILITY);
            case "art" -> ca = getOwner().getAttribute(EPlayerAttribute.ART_COMBAT_ABILITY);
        }

        return Tools.colorText(Tools.getColorModLegacy(ca));
    }

    public String getTroopsCost(String type){
        double cost = getOwner().getAttribute(EPlayerAttribute.fromString(type+"Cost"));
        String color = "&c";
        if(cost>1) color = "&a+";
        else if(cost==1) color = "&f";

        return ChatColor.translateAlternateColorCodes('&', color + (int) Math.round(cost*100-100) + "%");
    }

    public void cancelSiege(){
        Town town = db.getTown(getData().getSiegeTown());
        town.getData().getSiegeArmy().remove(this.getUuid());
        Earth.getInstance().getBlueMapManager().updateTownMarker(town);
        getData().setSiegeTown(Tools.EMPTY_UUID);
    }

    public double getCavRatio(){
        return getOwner().getAttribute(EPlayerAttribute.CAV_RATIO);
    }

    public boolean isRetreat(){
        return data.isRetreat();
    }

    public void setRetreat(boolean b){
        data.setRetreat(b);
    }

    public boolean isBattle(){
        return battle != null;
    }

    private Battle battle;
    public void setBattle(Battle b){
        battle = b;
    }

    public String getCavRatioColor(){

        int inf = getInfantry();
        int cav = getCavalry();
        String color = "&a";
        int troops = inf + cav;
        if (troops == 0) return  color + 0 + "&f%/&e" + (int) (getCavRatio() * 100) + "&f%";

        double cavRatio = (double) cav / troops;
        if(cavRatio>getCavRatio()) color = "&c";

        return color + (int) (cavRatio*100) + "&f%/&e" + (int) (getCavRatio() * 100) + "&f%";

    }

    public String getCavRatioColored(){

        int inf = getInfantry();
        int cav = getCavalry();
        String color = "<green>";
        int troops = inf + cav;
        if (troops == 0) return  color + 0 + "<white>%/<yellow>" + (int) (getCavRatio() * 100) + "<white>%";

        double cavRatio = (double) cav / troops;
        if(cavRatio>getCavRatio()) color = "<red>";

        return color + (int) (cavRatio*100) + "<white>%/<yellow>" + (int) (getCavRatio() * 100) + "<white>%";

    }



    public void disband(Inventory inventory){
        for(ArmyUnit u:units){
            if(u.getData().isMerc()) continue;
            if(u.getData().isLevies()) continue;
            ItemStack unit = Tools.createArmyCraftItem(u.getTech(),u.getData().getDisc(),u.getMorale(),false,false);

            Map<Integer, ItemStack> overflow = inventory.addItem(unit);

            if (!overflow.isEmpty()) {
                // Если карта не пуста, значит, часть предметов не влезла
                for (ItemStack remaining : overflow.values()) {
                    // Спавним не поместившиеся предметы на землю рядом с игроком
                    inventory.getLocation().getWorld().dropItemNaturally(inventory.getLocation(), remaining);
                }

            }
        }
        if(leaderName!=null){
            int fire =getLeaderFire();
            int shock = getLeaderShock();
            int move = getLeaderMove();
            int siege = getLeaderSiege();

            ItemStack leader = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta leaderMeta = (SkullMeta) leader.getItemMeta();
            leaderMeta.setDisplayName(leaderName);
            leaderMeta.getPersistentDataContainer().set(leaderShockKey, PersistentDataType.INTEGER,shock);
            leaderMeta.getPersistentDataContainer().set(leaderFireKey,PersistentDataType.INTEGER,fire);
            leaderMeta.getPersistentDataContainer().set(leaderMoveKey,PersistentDataType.INTEGER,move);
            leaderMeta.getPersistentDataContainer().set(leaderSiegeKey,PersistentDataType.INTEGER,siege);
            leader.setItemMeta(leaderMeta);

            Map<Integer, ItemStack> overflow = inventory.addItem(leader);

            if (!overflow.isEmpty()) {
                // Если карта не пуста, значит, часть предметов не влезла
                for (ItemStack remaining : overflow.values()) {
                    // Спавним не поместившиеся предметы на землю рядом с игроком
                    inventory.getLocation().getWorld().dropItemNaturally(inventory.getLocation(), remaining);
                }

            }
        }
    }

    private boolean tryPayment(Player player, Map<Material,Integer> requirements) {

        if (requirements.isEmpty()) return false;

        // Сначала проверяем наличие всех ресурсов
        for (Map.Entry<Material, Integer> entry : requirements.entrySet()) {

            if (!player.getInventory().containsAtLeast(new ItemStack(entry.getKey()), entry.getValue())) {

                return false;
            }
        }

        // Если всё есть — списываем
        for (Map.Entry<Material, Integer> entry : requirements.entrySet()) {
            player.getInventory().removeItem(new ItemStack(entry.getKey(), entry.getValue()));
        }
        return true;
    }

    private void notifyUpgrade(Player player, int amount) {
        // Красивый шаблон с градиентом и иконкой
        String template = "<gradient:gold:yellow>Вы успешно улучшили</gradient> " +
                "<green><amount></green><white> полков";

        // Десериализация с подстановкой значений
        var component = MiniMessage.miniMessage().deserialize(template,
                Placeholder.unparsed("amount", String.valueOf(amount))
        );

        // Отправляем сообщение
        player.sendActionBar(component);

        // Добавляем эпичный звук (уровень, громкость, тональность)
        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.2f);
    }

    public void upgradeUnits(Player player){


        int maxInf = 1;
        double infFire= 0 ;
        double infShock= 0 ;
        int infFirePips= 0 ;
        int infShockPips= 0 ;
        int infMoralePips= 0 ;
        int maxCav = 1;
        double cavFire= 0 ;
        double cavShock= 0 ;
        int cavFirePips= 0 ;
        int cavShockPips= 0 ;
        int cavMoralePips= 0 ;
        int maxArt = 3;
        double artFire= 0 ;
        double artShock= 0 ;
        int artFirePips= 0 ;
        int artShockPips= 0 ;
        int artMoralePips= 0 ;
        Map<Material,Integer> infMaterials = new HashMap<>();
        Map<Material,Integer> cavMaterials = new HashMap<>();
        Map<Material,Integer> artMaterials = new HashMap<>();

        if (getOwner().getTech(EPlayerTech.INF3)) {
            maxInf = 3;
            infFire = 2.1;
            infShock = 1.6;
            infFirePips = 2;
            infShockPips = 1;
            infMoralePips = 1;
            infMaterials = Map.of(Material.HAY_BLOCK,2,Material.IRON_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.COOKED_PORKCHOP,2,Material.GUNPOWDER,1);
        }

        if (getOwner().getTech(EPlayerTech.CAV3)) {
            maxCav = 3;
            cavFire = 0.5;
            cavShock = 3;
            cavShockPips = 3;
            cavMoralePips = 2;
            cavMaterials = Map.of(Material.HAY_BLOCK,1,Material.IRON_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.LEATHER,1,Material.COOKED_PORKCHOP,1,Material.GUNPOWDER,1);
        }
        if (getOwner().getTech(EPlayerTech.CAV4)) {
            maxCav = 4;
            cavFire = 1;
            cavShock = 4;
            cavFirePips = 1;
            cavShockPips = 3;
            cavMoralePips = 3;
            cavMaterials = Map.of(Material.HAY_BLOCK,1,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.LEATHER,1,Material.GUNPOWDER,2);
        }

        if (getOwner().getTech(EPlayerTech.ART2)) {
            maxArt = 4;
            artFire = 8;
            artShock = 1.5;
            artFirePips = 8;
            artShockPips = 4;
            artMoralePips = 4;
            artMaterials = Map.of(Material.IRON_BLOCK,1,Material.AMETHYST_BLOCK,1,Material.DIAMOND_BLOCK,1,Material.FIRE_CHARGE,2,Material.GUNPOWDER,2);
        }
        int c = 0;
        for(ArmyUnit u : units){
            switch (u.getType()){
                case INF ->{
                    if (u.getLvl() < maxInf && u.getLvl()!=0 && tryPayment(player,infMaterials)) {
                        u.setLvl(maxInf);
                        u.setMorale(u.getMaxMorale());
                        u.setPipsFire(infFirePips);
                        u.setPipsShock(infShockPips);
                        u.setPipsMorale(infMoralePips);
                        u.setFire(infFire);
                        u.setShock(infShock);
                        c++;
                    }
                }
                case CAV ->{
                    if (u.getLvl() < maxCav && u.getLvl()!=0 && tryPayment(player,cavMaterials)) {
                        u.setLvl(maxCav);
                        u.setMorale(u.getMaxMorale());
                        u.setPipsFire(cavFirePips);
                        u.setPipsShock(cavShockPips);
                        u.setPipsMorale(cavMoralePips);
                        u.setFire(cavFire);
                        u.setShock(cavShock);
                        c++;
                    }
                }
                case ART ->{
                    if (u.getLvl() < maxArt && u.getLvl()!=0 && tryPayment(player,artMaterials)) {
                        u.setLvl(maxArt);
                        u.setMorale(u.getMaxMorale());
                        u.setPipsFire(artFirePips);
                        u.setPipsShock(artShockPips);
                        u.setPipsMorale(artMoralePips);
                        u.setFire(artFire);
                        u.setShock(artShock);
                        c++;
                    }
                }
            }
        }
        if(c>0)notifyUpgrade(player,c);

    }


    public void mergeUnits(boolean shift) {
        // 1. Сначала удаляем всех, у кого HP <= 0
        units.removeIf(u -> u.getHp() <= 0);

        // 2. Группируем юнитов по типу и уровню, чтобы не смешать кавалерию с пехотой
        // Ключ: "type_lvl", Значение: список подходящих юнитов
        Map<String, List<ArmyUnit>> groups = units.stream()
                .collect(Collectors.groupingBy(u -> u.getTech() + "_" + u.getLvl()));

        for (List<ArmyUnit> squad : groups.values()) {
            if (squad.size() < 2) continue;

            for (int i = 0; i < squad.size(); i++) {
                ArmyUnit target = squad.get(i);

                // Если этот юнит уже ранен (не полный HP)
                if (target.getHp() < 1000) {

                    // Ищем, у кого забрать HP (начиная со следующих в списке)
                    for (int j = i + 1; j < squad.size(); j++) {
                        ArmyUnit source = squad.get(j);

                        int needed = target.getDamageTaken();
                        int available = source.getHp();

                        if (available <= needed) {
                            // Забираем всё здоровье у источника
                            target.setHp(target.getHp() + available);
                            source.setHp(0);
                        } else {
                            // Забираем только часть, чтобы заполнить цель
                            target.setHp(1000);
                            source.setHp(available - needed);
                        }

                        // Если цель заполнена, переходим к следующей цели
                        if (target.getHp() >= 1000) break;
                    }
                }
            }
        }

        // 3. Финальная чистка: удаляем тех, кто отдал всё здоровье
        if(!shift){
            units.removeIf(u -> u.getHp() <= 0);
        }


    }

    public Town getTownAt(){
        return db.getTownAtChunk(getData().getLocation());
    }

    public boolean isAllyLoc(){
        if(getTownAt() == null) return false;
        UUID controllerId = getTownAt().getController().getUniqueId();
        return (controllerId.equals(getOwnerId()) || getOwner().getData().getAlly().contains(controllerId));
    }

    public boolean isEnemyLoc(){
        if(getTownAt() == null) return false;
        UUID controllerId = getTownAt().getController().getUniqueId();
        return getOwner().getData().getEnemies().contains(controllerId);
    }

    public boolean isBarbarianLoc(){

        return getTownAt() == null;
    }





    public int getCW(){
        return (int) getOwner().getAttribute(EPlayerAttribute.CW);
    }



//    public List<Unit> getUnits(){
//        List<Unit> units = db.getUnits();
//        List<Unit> thisUnits = new ArrayList<>();
//
//        if(units == null){
//            return null;
//        }
//        for (Unit unit : units){
//            if (unit.getArmyId().equals(this.uuid)){
//                String path = "pips.standard."+unit.getType() + techLvl + ".";
//                unit.setPipsFire(CustomConfig.get().getInt(path+"fire")+techLvl);
//                unit.setPipsShock(CustomConfig.get().getInt(path + "shock")+techLvl);
//                unit.setPipsMorale(CustomConfig.get().getInt(path + "morale")+techLvl);
//
//                thisUnits.add(unit);
//            }
//        }
//        return thisUnits;
//    }

    public double getMorale(){
        if(units.isEmpty()) return 0;
        double moraleSum = 0;
        for(ArmyUnit u : units){
            moraleSum += u.getMorale();
        }

        return Tools.round(moraleSum/ units.size());
    }

    public double getMaxMorale(){
        if(units.isEmpty()) return 0;
        double moraleSum = 0;
        for(ArmyUnit u : units){
            moraleSum += u.getMaxMorale();
        }

        return Tools.round(moraleSum/ units.size());
    }

    public int[] getLvlTroops(String type, int lvl){
        int[] res = new int[2];
        int hp = 0;
        int amount = 0;
        for (ArmyUnit u : units) {
            if (u.getTech().toString().toLowerCase(Locale.ROOT).equals(type) && u.getLvl() == lvl) {
                hp += u.getHp();
                amount++;
            }
        }
        res[0] = amount;
        res[1] = hp;
        return res;
    }

    public int getTypeTroops(UnitType type){
        int hp = 0;
        for (ArmyUnit u : units) {
            if (u.getType().equals(type)) hp += u.getHp();
        }
        return hp;
    }

    public int getTroops(){
        int hp = 0;
        for (ArmyUnit u : units) {
            hp += u.getHp();
        }
        return hp;
    }

    public void killInfantry(int amount){
        for(ArmyUnit u:units){
            if (u.getHp() < amount ){
                amount -= u.getHp();
                u.setHp(0);
            }else{
                u.setHp(u.getHp()-amount);
                amount = 0;
            }
        }
    }

    public double getTactic(){
        return Tools.round(getOwner().getAttribute(EPlayerAttribute.TACTIC)*getDisciple());
    }


    public double getDisc(){
        return getOwner().getAttribute(EPlayerAttribute.DISCIPLE);
    }

    public double getDisciple(){
        if(getArmySize()==0) return getDisc();
        double disSum = 0;
        for(ArmyUnit u:units){
            disSum += getDisc() + u.getDisc() + u.getData().getDisc();
        }

        return Tools.round(disSum/ getArmySize());
    }



    public String getDiscipleColor(){
        int d = (int) Math.round(getDisciple()*100);
        String c = "&f";
        if(d>100)c = "&a";
        if(d<100)c = "&c";
        return c+d+"&f%";
    }

    public String getDiscipleColored(){
        int d = (int) Math.round(getDisciple()*100);
        String c = "<white>";
        if(d>100)c = "<green>";
        if(d<100)c = "<red>";
        return c+d+"<white>%";
    }

    public String getDiscipleMod(){
        int d = (int) Math.round((getDisciple()-1)*100);
        String c = "&f";
        if(d>0)c = "&a+";
        if(d<0)c = "&c";
        return c+d+"&f%";
    }

    public int getArmySize(){
        return units.size();
    }



    public boolean isBarbarian(){
        return getOwner().getDisplayName().equals("barbarian");
    }

    public void setLocation(@NotNull Location loc, long time){
        setLocation(loc.getChunk(),time);
    }


    public void setLocation(@NotNull Location loc){
        setLocation(loc.getChunk());
    }

    public void setLocation(@NotNull Chunk chunk, long time){
        Chunk fromChunk = chunk.getWorld().getChunkAt(data.getLocation());
        if(data.getLocation()!=chunk.getChunkKey()){
            data.setLocation(chunk.getChunkKey());
            data.setLocationTime(time);
            Bukkit.getServer().getPluginManager().callEvent(new ArmyMoveEvent(this,chunk,fromChunk));
        }
    }

    public void setLocation(@NotNull Chunk chunk){
        setLocation(chunk,System.currentTimeMillis());
    }

    public long getChunkKey(){
        return data.getLocation();
    }

    public long getChunkTime(){
        return data.getLocationTime();
    }

    public void setShulkerLoc(Location loc){
        if(loc == null){
            data.setShulkerX(null);
            data.setShulkerY(null);
            data.setShulkerZ(null);
        }else{
            data.setShulkerX(loc.getX());
            data.setShulkerY(loc.getY());
            data.setShulkerZ(loc.getZ());
        }
    }

    public Location getShulkerLoc(){
        Double x = data.getShulkerX();
        Double y = data.getShulkerY();
        Double z = data.getShulkerZ();
        if(x==null){
            return null;
        }else{
            return new Location(Bukkit.getWorlds().get(0),x,y,z);
        }
    }



    @Getter
    @Setter
    private Town barbarianTown;

    @Getter
    @Setter
    private Inventory barbarianChest;

    @Getter
    @Setter
    private ItemStack barbarianTownItem;

    @Getter
    @Setter
    private ItemStack barbarianOwnerItem;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Army army = (Army) o;
        return Objects.equals(uuid, army.getUuid());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Army{owner = '" + getOwner().getDisplayName() + "', size='" + getArmySize() + "', uuid=" + uuid.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(Army other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = getOwner().compareTo(other.getOwner());
        if (res == 0) res = uuid.compareTo(other.uuid);
        return res;
    }

}
