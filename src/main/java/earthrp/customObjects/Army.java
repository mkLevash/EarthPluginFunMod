package earthrp.customObjects;

import earthrp.Earth;
import earthrp.customEnums.EPlayerTech;
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

import java.util.*;
import java.util.stream.Collectors;


@Getter
@Setter
public class Army implements Comparable<Army>{
    @Getter(AccessLevel.NONE) // <--- не сгенерирует getter
    private final ServerDatabase db;

    private final UUID uuid;

    public Army(UUID uuid, UUID ownerId){
        this.uuid = uuid;
        this.ownerId = ownerId;
        infantry = 0;
        cavalry = 0;
        artillery = 0;
        db = Earth.getInstance().getServerDatabase();
    }


    private int techLvl;
    private UUID ownerId;
    private int infantry;
    private int cavalry;
    private int artillery;
    private String leaderName;
    private int leaderFire;
    private int leaderShock;
    private boolean battle = false;

    private final Set<Unit> units = new HashSet<>();

    public Set<Unit> getUnits() {
        return Collections.unmodifiableSet(units);
    }

    public void addUnit(Unit unit) {
        this.units.add(unit);
        switch (unit.getType()){
            case "inf" -> infantry++;
            case "cav" -> cavalry++;
            case "art" -> artillery++;
        }
    }

    public void removeUnit(Unit unit) {
        this.units.remove(unit);
        switch (unit.getType()){
            case "inf" -> infantry--;
            case "cav" -> cavalry--;
            case "art" -> artillery--;
        }
    }


    private Location staticLocation;
    private UUID playerUUID = null;
    private boolean playerOffline = false; // Новый флаг

    public Location getLocation() {
        // Если игрок оффлайн, возвращаем точку, где он вышел из игры
        if (playerOffline) {
            return staticLocation;
        }

        if (playerUUID != null) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (player != null && player.isOnline()) {
                return player.getLocation();
            }
        }
        return staticLocation;
    }


    public void setPlayerOffline(boolean offline, Location logoutLoc) {
        this.playerOffline = offline;
        if (offline) {
            this.staticLocation = logoutLoc; // Замораживаем на месте выхода
        }
    }


    public EPlayer getOwner(){
        return db.getPlayer(ownerId);
    }




    public String getCA(String type){
        double ca = 0.0;
        switch (type){
            case "inf" ->{
                ca = getOwner().getAttribute(EPlayerAttribute.INF_COMBAT_ABILITY);
            }
            case "cav" ->{
                ca = getOwner().getAttribute(EPlayerAttribute.CAV_COMBAT_ABILITY);
            }
            case "art" ->{
                ca = getOwner().getAttribute(EPlayerAttribute.ART_COMBAT_ABILITY);
            }
        }

        return Tools.colorText(Tools.getColorMod(ca));
    }

    public String getTroopsCost(String type){
        double cost = getOwner().getAttribute(EPlayerAttribute.fromString(type+"Cost"));
        String color = "&c";
        if(cost>1) color = "&a+";
        else if(cost==1) color = "&f";

        return ChatColor.translateAlternateColorCodes('&', color + (int) Math.round(cost*100-100) + "%");
    }

    public double getCavRatio(){
        return getOwner().getAttribute(EPlayerAttribute.CAV_RATIO);
    }

    public int getMaxLvl(String type){
        Set<Unit> units = getUnits();
        int max = 0;
        for(Unit u:units){
            if(u.getType().equals(type))max = Math.max(max,u.getLvl());
        }
        return max;
    }

    public void disband(Inventory inventory){
        for(Unit u:units){
            List<String> uDesc = List.of(
                    Tools.colorText("&fᠩ&4"+0.0 + "&f/&2" + u.getBaseMorale()),
                    Tools.colorText("&fᠧ&f" + Tools.getColorMod( u.getDisc() + 1.0))
                    );
            ItemStack unit = Tools.createArmyCraftItem(u.getName(),uDesc,u.getType(),u.getLvl(),u.getDisc(),u.getFire(),u.getShock(),0.0);
            inventory.addItem(unit);
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
        for(Unit u : units){
            switch (u.getType()){
                case "inf" ->{
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
                case "cav" ->{
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
                case "art" ->{
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


    public void mergeUnits() {
        // 1. Сначала удаляем всех, у кого HP <= 0
        units.removeIf(u -> u.getHp() <= 0);

        // 2. Группируем юнитов по типу и уровню, чтобы не смешать кавалерию с пехотой
        // Ключ: "type_lvl", Значение: список подходящих юнитов
        Map<String, List<Unit>> groups = units.stream()
                .collect(Collectors.groupingBy(u -> u.getType() + "_" + u.getLvl()));

        for (List<Unit> squad : groups.values()) {
            if (squad.size() < 2) continue;

            for (int i = 0; i < squad.size(); i++) {
                Unit target = squad.get(i);

                // Если этот юнит уже ранен (не полный HP)
                if (target.getHp() < 1000) {

                    // Ищем, у кого забрать HP (начиная со следующих в списке)
                    for (int j = i + 1; j < squad.size(); j++) {
                        Unit source = squad.get(j);

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
        units.removeIf(u -> u.getHp() <= 0);
        infantry = 0;
        cavalry = 0;
        artillery = 0;
        for(Unit u:units){
            switch (u.getType()){
                case "inf" -> infantry++;
                case "cav" -> cavalry++;
                case "art" -> artillery++;
            }
        }
    }

    public int getCW(){
        switch (techLvl){
            case 0 ->{
                return 15;
            }
            case 1 ->{
                return 20;
            }
            case 2 ->{
                return 25;
            }
            case 3 ->{
                return 30;
            }
            case 4 ->{
                return 35;
            }
        }
        return 15;
    }

    public int getFR(){
        switch (techLvl){
            case 0 ->{
                return 1;
            }
            case 1, 2 ->{
                return 2;
            }
            case 3 ->{
                return 3;
            }
            case 4 ->{
                return 4;
            }
        }
        return 2;
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

        double moraleSum = 0;
        for(Unit u : units){
            moraleSum += u.getMorale();
        }

        return Tools.round(moraleSum/getSize());
    }

    public double getMaxMorale(){
        double moraleSum = 0;
        for(Unit u : units){
            moraleSum += u.getMaxMorale();
        }

        return Tools.round(moraleSum/getSize());
    }

    public int[] getLvlTroops(String type, int lvl){
        int[] res = new int[2];
        int hp = 0;
        int amount = 0;
        for (Unit u : units) {
            if (u.getType().equals(type) && u.getLvl() == lvl) {
                hp += u.getHp();
                amount++;
            }
        }
        res[0] = amount;
        res[1] = hp;
        return res;
    }

    public int getTypeTroops(String type){
        int hp = 0;
        for (Unit u : units) {
            if (u.getType().equals(type)) hp += u.getHp();
        }
        return hp;
    }

    public int getTroops(){
        int hp = 0;
        for (Unit u : units) {
            hp += u.getHp();
        }
        return hp;
    }

    public double getTactic(){
        return Tools.round(getOwner().getAttribute(EPlayerAttribute.TACTIC)*getDisc());
    }


    public double getDisc(){
        return getOwner().getAttribute(EPlayerAttribute.DISCIPLE);
    }

    public String getDisciple(){
        int d = (int) Math.round(getDisc()*100);
        String c = "&f";
        if(d>100)c = "&a";
        if(d<100)c = "&c";
        return c+d+"&f%";
    }

    public int getSize(){
        return Math.max(1,infantry+cavalry+artillery);
    }


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
        return "Army{owner = '" + getOwner().getDisplayName() + "', size='" + getSize() + "', uuid=" + uuid.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(Army other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = getOwner().compareTo(other.getOwner());
        if (res == 0) res = uuid.compareTo(other.uuid);
        return res;
    }

}
