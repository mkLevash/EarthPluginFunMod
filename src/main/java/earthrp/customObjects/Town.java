package earthrp.customObjects;

import com.google.gson.Gson;
import earthrp.Earth;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EarthItem;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.configs.CustomConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;

@Getter
@Setter
public class Town implements Comparable<Town> {


    
    private ServerDatabase db;
    private TownData data; // Объект с данными

    public void setTownItem(EarthItem type, long newValue) {
        data.getItems().put(type, newValue);
    }

    private void addTownItem(EarthItem type, long delta) {

        setTownItem(type, getItem(type) + delta);
    }

    public long getItem(EarthItem type) {
        return data.getItems().getOrDefault(type, 0L);
    }

    public long getItemAmount(){
        return data.getItems().values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }






    public long getItemMax(){
        return 3456 + (10368L * getBarnAmount());
    }

    public void addItem(EarthItem type, long delta){
        addTownItem(type, Math.min (delta, getAvailableSpace()));
    }

    public long getAvailableSpace(){
        return Math.max(0, getItemMax() - getItemAmount());
    }

    private final UUID id;
    private String type;
    private final String name;
    private UUID ownerId;
    public String getOwnerName(){
        return db.getPlayer(ownerId).getDisplayName();
    }

    public boolean isPort(){
        return data.isPort();
    }
    public void setPort(boolean bool){
        data.setPort(bool);
    }

    public boolean isLandHub(){
        return data.isLandHub();
    }

    public boolean isShipyard(){
        for(var b : getBuildings()){
            if (b.getData().getType() == BuildingType.SHIPYARD){
                return true;
            }
        }
        return false;
    }
    public void setLandHub(boolean bool){
        data.setLandHub(bool);
    }
    public boolean isFort(){
        return data.isFort();
    }
    public void setFort(boolean bool){
        data.setFort(bool);
    }

    public boolean isCore(){
        return data.isCore();
    }
    public void setCore(boolean bool){
        data.setCore(bool);
    }

    public boolean isBlockade(){
        return data.isBlockade();
    }
    public void setBlockade(boolean bool){
        data.setBlockade(bool);
    }
    private final Location location;


    private double famine = 0.0;



    public Town(UUID townUniqueId, UUID playerUniqueId, String townType, String townName, Location location, String jsonData){
        this.id = townUniqueId;
        this.ownerId = playerUniqueId;
        this.type = townType;
        this.name = townName;
        this.location = location;
        loadData(jsonData);
        db = Earth.getInstance().getDatabase();
    }

    public Town(EPlayer owner,UUID townUniqueId, String townType, String townName, Location location){
        this.id = townUniqueId;
        this.ownerId = owner.getUniqueId();
        this.type = townType;
        this.name = townName;
        this.location = location;
        loadData("");
        setController(owner);
        db = Earth.getInstance().getDatabase();
    }




    private final Set<Building> buildings = new HashSet<>();

    // Получить список городов (неизменяемый для безопасности)
    public Set<Building> getBuildings() {
        return Collections.unmodifiableSet(buildings);
    }

    // Внутренние методы для управления связями
    public void addBuilding(Building building) {
        this.buildings.add(building);
        switch (building.getData().getType()){
            case MARKETPLACE ->{

                setLandHub(true);
                CustomConfig.set("trade.towns."+ id +".tradeMod", 1.0);
                CustomConfig.set("trade.towns."+ id +".name", name);
            }
            case FORT ->{
                setFort(true);
            }
            case PORT -> {
                setPort(true);
            }
        }
    }

    public void removeBuilding(Building building) {
        this.buildings.remove(building);
        switch (building.getData().getType()){
            case MARKETPLACE ->{
                setLandHub(false);
            }
            case FORT ->{
                setFort(false);
            }
            case PORT -> {
                setPort(false);
            }
        }
    }

    public EPlayer getController(){
        return db.getPlayer(getData().getController());
    }

    public void setController(EPlayer controller){
        data.setController(controller.getUniqueId());
    }



    public int getSpecialBuildingsAmount(){
        int i = 0;
        for(Building b:buildings){
            if(b.getData().getType().isBuildSiteReq()) i++;
        }
        return i;
    }

    public int getBarnAmount(){
        int i = 0;
        for(Building b:buildings){
            if(b.getData().getType().equals(BuildingType.BARN)) i++;
        }
        return i;
    }

    private static final Gson gson = new Gson();


    private String rawJson;    // То, что пришло из БД

    // Вызываем при загрузке из БД
    public void loadData(String json) {
        if (json == null || json.isEmpty()) {
            this.data = new TownData();
        } else {
            this.data = gson.fromJson(json, TownData.class);
        }
    }

    // Вызываем перед сохранением в БД
    public String serializeData() {
        return gson.toJson(this.data);
    }

    // Удобный геттер
    public TownData getData() {
        if (this.data == null) this.data = new TownData();
        return this.data;
    }

    public boolean isSiege(){
        return !data.getSiegeArmy().isEmpty();
    }


    public List<Army> getSiegeArmyList(){
        ServerDatabase db = Earth.getInstance().getDatabase();
        List<UUID> idList = new ArrayList<>(data.getSiegeArmy());
        List<Army> armyList = new ArrayList<>();
        for(UUID armyId : idList){
            armyList.add(db.getArmy(armyId));
        }
        return armyList;
    }

    public Army getSieger(){
        return getSiegeArmyList().getFirst();
    }

    public void besieged(){
        if (!isSiege()) {
            return;
        }
        EPlayer sieger = getSieger().getOwner();
        for(Army army : getSiegeArmyList()){
            army.getData().setSiegeTown(Tools.EMPTY_UUID);
        }
        getData().getSiegeArmy().clear();
        if(sieger.getData().getEnemies().contains(ownerId)){
            sieger.controlTown(this);
        }else{
            getOwner().controlTown(this);

        }
        Earth.getInstance().getBlueMapManager().updateTownMarker(this);
        Bukkit.broadcast(Tools.deserialize("Осада <aqua>" + this.name + "<white> завершилась победой <red>осаждающих!"));
    }

    public String getOccupierName(){
        if( isOccupied() ) return db.getPlayer(getData().getController()).getCountryName();
        return "";

    }

    public boolean isOccupied(){
        return !getData().getController().equals(ownerId);
    }



    public int getSiegeChance(){
        Army attacker = getSieger();

        int siegeAbility = (int) ((attacker.getOwner().getAttribute(EPlayerAttribute.SIEGE_ABILITY) - getOwner().getAttribute(EPlayerAttribute.FORT_ABILITY)) * 100 ) ;
        double attackerFort = attacker.getOwner().getAttribute(EPlayerAttribute.FORT_LVL);
        double defenderFort = Math.max(1,getOwner().getAttribute(EPlayerAttribute.FORT_LVL));
        siegeAbility += 15 * (int) (attackerFort - defenderFort);
        int art = 0;
        int siege = 0;
        for (Army army : getSiegeArmyList()){
            art += army.getArtilleryTroops() ;
            if(army.getData().getLeaderSiege()>siege){
                siege = army.getData().getLeaderSiege();
            }
        }
        siegeAbility += (int) Math.min(25,art*5/(defenderFort*1000) );
        siegeAbility += siege * 5;

        return Math.min(100,siegeAbility + getData().getSiegeChance());
    }

    public String getSiegeChanceColor(boolean defender){
        String color = "<green>";
        if(defender){
            if (getSiegeChance() > 0){
                color = "<red>";
            }
        }else{
            if (getSiegeChance() < 1){
                color = "<red>";
            }
        }
        return color + getSiegeChance() + "%";
    }



    public UUID getUniqueId(){return this.id;}

    public EPlayer getOwner(){
        return db.getPlayer(this.ownerId);
    }

    public UUID getOwnerId(){
        return getOwner().getUniqueId();
    }



    public String getCoreStatus(){
        if (isCore()){
            return Tools.colorText("<dark_green>Национальная территория");
        }else {
            return Tools.colorText("<red>Не национализирован");
        }
    }

    public int getCoreCost(){
        return (int) Math.round(getPeasant()*getOwner().getCoreCost());
    }



    public int getPeasant(){
        if (this.type.equals("capital")){
            return data.houses+5;
        }else{
            return data.houses+3;
        }
    }

    public int getNobleSites(){
        return (getPeasant() / 5) +  ((int) getOwner().getAttribute(EPlayerAttribute.BUILD_SITES));
    }


    public int getInfrastructureCost(){
        double capitalMod = 0;
        if(type.equals("capital")){
            capitalMod = Tools.round(getOwner().getPeasant()*0.01);
        }
        return (int) Math.round(5 * (1+data.infrastructure) * Math.max(0.1, getOwner().getAttribute(EPlayerAttribute.EXPAND_INFRASTRUCTURE_COST) - capitalMod) );
    }


    public int getBuildSite(){
        int base;
        if(getPeasant()<5) base = 1;
        else if (getPeasant()<8) base = 2;
        else base = 3;
        return (int) (base + data.infrastructure + data.bonusBuildSite + getOwner().getAttribute(EPlayerAttribute.BUILD_SITES));
    }




    public boolean canNewBuild(String type){
        BuildingType tp = BuildingType.fromString(type);
        if (tp == null) {
            return false;
        }


        if (tp.isBuildSiteReq() && getSpecialBuildingsAmount() >= getBuildSite()) {
            return false;
        }

        // 2. Затем проверяем уникальные ограничения для конкретных типов
        return switch (tp) {
            case FORT -> !this.isFort();
            case MARKETPLACE -> !this.isLandHub();
            case PORT -> !this.isPort();
            default -> true; // Для всех остальных типов зданий постройка разрешена
        };

    }

    public double getHungerMod(){
        double mod = 1;
        for(Building b:buildings){
            mod += b.getData().getType().getHungerMod();
        }
        return mod;
    }

    public String getHungerColor(){
        return (int) Math.round((getPeasant() * 10 + data.getNoble() * 50) * getHungerMod()) + " | " + (int) (getHungerMod() * 100) + "%";
    }

    public String getFamineColor(){
        if (getFamine()>0) return "&c" + (int) ((1-getFamine()) * 100) + "%";
        return "&a" + (int) ((1-getFamine()) * 100) + "%";
    }



    public String getFoodColor(){
        long food = 0;

        for(EarthItem i : getData().getItems().keySet()){
            if(i == null) continue;
            food+= i.getFood() * getData().getItems().get(i);

        }

        long hunger = Math.round((getPeasant() * 10 + data.getNoble() * 50) * getHungerMod());
        if(isCapital()){
            hunger += (getOwner().getUnits().size()* 5L);
        }
        if (food>hunger) return "&a" + food;
        return "&c" + food;
    }

    public String getItemsColor(){
        if  (getItemAmount() < getItemMax()){
            return "&a" + getItemAmount() + "&f/&e" + getItemMax();
        }else{
            return "&c" + getItemAmount() + "&f/&e" + getItemMax();
        }
    }





    public List<Town> getTradeTowns(){
        HashSet<Town> towns = db.getTowns();
        List<Town> res = new ArrayList<>();

        for(Town t:towns){
            String path = "trade.towns."+ id + "." + t.getUniqueId();
            if (CustomConfig.get().getBoolean(path+".status")){
                res.add(t);
            }
        }
        return res;
    }




    public String getColorTradeMod(){
        int t = 1;//(int) Math.round(getTradeMod()*100);

        String c = "&f";
        if(t>100)c = "&a";
        if(t<100)c = "&c";
        return c+t+"&f%";
    }



    public double getPeasantTradeMod(){
        return Tools.round((0.01 * getOwner().getAttribute(EPlayerAttribute.TRADE_PEOPLE_MOD)) * getPeasant());
    }

    public double getProductionValue(){
        double totalProductionValue = 0;

        for(Building b:buildings){
            if (b.getData().getItem()!=null){
                totalProductionValue += b.getBaseProduction();
            }
        }
        return Tools.round(totalProductionValue);
    }

    public double getProductionTradeMod(){
        return Tools.round( getProductionValue() * (0.02 * getOwner().getAttribute(EPlayerAttribute.TRADE_GOODS_MOD) ));
    }

    public double getFrigateTradeMod(){
        return Tools.round((0.01 * getOwner().getAttribute(EPlayerAttribute.TRADE_FRIGATE_MOD) ) * getOwner().getData().getTradeShips());
    }

    public double getTradeMod(){
        double tradeMod = 0.0;
        if(isOccupied() || (!isCapital() && isSiege()) ) return tradeMod;
        if(isLandHub()){
            tradeMod += getPeasantTradeMod() + getProductionTradeMod();
        }
        if (isPort()){
            tradeMod += getFrigateTradeMod();
        }
        return Tools.round(tradeMod);
    }

    public double getTradeIncome(Set<EarthItem> world){
        double income = 0.0;
        if(isOccupied() || (!isCapital() && isSiege()) ) return income;





        for (Building b:getBuildings()){
            income += b.getIncome(world);
        }

        return Tools.round(income * getOwner().getTradeMod());


    }



    public double getProdIncome(){
        if(!isCore()||isOccupied() || (!isCapital() && isSiege()) ) return 0;
        double totalProductionValue = 0;

        for(Building b:buildings){
            if (b.getData().getItem()!=null){
                totalProductionValue += (b.getBaseProduction() * b.getData().getItem().getCost());
            }

        }
        return ( totalProductionValue * getPeasant() ) / 12.0;
    }

    public double getTaxIncome(){
        if(!isCore()|| isOccupied() || (!isCapital() && isSiege()) ) return 0;
        int tax = getPeasant() + (getData().noble * 3);

        return Tools.round(tax * getTaxMod());
    }

    public double getTaxMod(){
        double nobleMod = 1.0 + (getData().noble * 0.10);
        double famine = 1.0 - this.famine;
        return Tools.round(nobleMod * famine);
    }

    public String getTaxModColor(){
        double mod = getTaxMod();
        String modifier = Tools.round((getTaxMod() - 1) * 100) + "<white>%";
        if(mod < 1){
            return "<red>" + modifier;
        }
        if(mod > 1){
            return "<green>" + modifier;
        }
        return "<gray>" + modifier;





    }




//
//    public void setLandHubId(UUID landHubId) {
//
//        for(Building b:buildings){
//            if(landHubId == null && b.getMarketId() != null && b.getMarketId().equals(this.landHubId)){
//                if(b.getTownId()!=uuid) b.setMarketId(b.getTown().getLandHubId());
//                else b.setMarketId(null);
//
//            } else if (landHubId != null && b.getTownId().equals(uuid)) {
//                b.setMarketId(landHubId);
//            }
//        }
//        this.landHubId = landHubId;
//        }

    public boolean isCapital(){
        return this.type.equals("capital");
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return Objects.equals(id, town.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Town{name='" + name + "', uuid=" + id.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(Town other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = name.compareTo(other.name);
        if (res == 0) res = id.compareTo(other.id);
        return res;
    }

}
