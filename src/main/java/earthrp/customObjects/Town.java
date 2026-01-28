package earthrp.customObjects;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.*;

@Getter
@Setter
public class Town implements Comparable<Town> {
    
    private ServerDatabase db;


    private final UUID uuid;
    private UUID ownerId;
    @Setter
    private boolean port;
    private boolean landHub;
    private boolean fort = false;
    private boolean core = true;
    private UUID tradeTownId;
    private String type;
    private String name;
    private String ownerName;
    private boolean status = true;
    private boolean bStatus = false;
    private int houses;
    private int bonusBuildSite;
    private int infrastructure = 0;
    private final String world;
    private final int x;
    private final int z;
    private Location location;

    public Town(UUID townUniqueId, UUID playerUniqueId, String townType, String townName, String playerName, int townHouses, int bonusBuildingsSites, String worldName, int chunkX, int chunkZ, boolean port, boolean landHub, UUID tradeTownId){
        this.uuid = townUniqueId;
        this.ownerId = playerUniqueId;
        this.type = townType;
        this.name = townName;
        this.ownerName = playerName;
        this.houses = townHouses;
        this.bonusBuildSite = bonusBuildingsSites;
        this.world = worldName;
        this.x = chunkX;
        this.z = chunkZ;
        this.port = port;
        this.landHub = landHub;
        this.tradeTownId = tradeTownId;
        db = Earth.getInstance().getServerDatabase();

        Entity[] tileEntities = Bukkit.getWorld(this.world).getChunkAt(x,z).getEntities();
        for(Entity en: tileEntities){
            if (en instanceof ArmorStand armorStand && uuid.toString().equals(armorStand.getCustomName())) {
                location = armorStand.getLocation();
            }
        }
    }
    public Town(UUID townUniqueId, UUID playerUniqueId, String townType, String townName, String playerName, String worldName, int chunkX, int chunkZ){
        this.uuid = townUniqueId;
        this.ownerId = playerUniqueId;
        this.type = townType;
        this.name = townName;
        this.ownerName = playerName;
        this.houses = 0;
        this.bonusBuildSite = 0;
        this.world = worldName;
        this.x = chunkX;
        this.z = chunkZ;
        this.port = false;
        this.landHub = false;
        this.tradeTownId = null;
        db = Earth.getInstance().getServerDatabase();

        Entity[] tileEntities = Bukkit.getWorld(this.world).getChunkAt(x,z).getEntities();
        for(Entity en: tileEntities){
            if (en instanceof ArmorStand armorStand && uuid.toString().equals(armorStand.getCustomName())) {
                location = armorStand.getLocation();
            }
        }
    }
//
//    public void delMarket(){
//        market = null;
//        setLandHubId(null);
//        
//    }
//
//    public void updateMarket(Market market){
//        this.market = market;
//        
//    }

    private final Set<Building> buildings = new HashSet<>();

    // Получить список городов (неизменяемый для безопасности)
    public Set<Building> getBuildings() {
        return Collections.unmodifiableSet(buildings);
    }

    // Внутренние методы для управления связями
    public void addBuilding(Building building) {
        this.buildings.add(building);
        switch (building.getType()){
            case "landHub" ->{
                for(Building b:buildings){
                    if(b.getMarketId()==null)  b.setMarketId(uuid);
                }
                setLandHub(true);
                CustomConfig.set("trade.towns."+uuid+".tradeMod", 1.0);
                CustomConfig.set("trade.towns."+uuid+".name", name);
            }
            case "fort" ->{
                fort = true;
            }
            case "port" -> {
                setPort(true);
            }
        }
    }

    public void removeBuilding(Building building) {
        this.buildings.remove(building);
        switch (building.getType()){
            case "landHub" ->{
                for(Building b:db.getBuildings()){
                    if(b.getMarketId()==null && b.getMarketId().equals(uuid))  b.setMarketId(null);
                }
                setLandHub(false);
            }
            case "fort" ->{
                fort = false;
            }
            case "port" -> {
                setPort(false);
            }
        }
    }




    public UUID getUniqueId(){return this.uuid;}

    public EPlayer getOwner(){
        return db.getPlayer(this.ownerId);
    }

    public boolean getBlockadeStatus(){return this.bStatus;}

    public String getCoreStatus(){
        if (isCore()){
            return Tools.colorText("&2Национальная территория");
        }else {
            return Tools.colorText("&4Не национализирован");
        }
    }

    public int getCoreCost(){
        return (int) Math.round(getPeople()*getOwner().getCoreCost());
    }

    public int getPeople(){
        if (this.type.equals("capital")){
            return this.houses+5;
        }else{
            return this.houses+3;
        }
    }

    public int getInfrastructureCost(){
        double capitalMod = 0;
        if(type.equals("capital")){
            capitalMod = Tools.round(getOwner().getPeople()*0.01);
        }
        return (int) Math.round(5 * (1+infrastructure) * Math.max(0.1, getOwner().getAttribute(EPlayerAttribute.EXPAND_INFRASTRUCTURE_COST) - capitalMod) );
    }


    public int getBuildSite(){
        int base = 1;
        if (houses >= 5) base = 2;
        if(houses >= 8) base = 3;
        if(isPort()) base++;
        if(isLandHub()) base++;
        if(isFort()) base++;
        return base+infrastructure+bonusBuildSite;
    }

    public int getChunkX(){return this.x;}
    public int getChunkZ(){return this.z;}


    public boolean canNewBuild(){
        return buildings.size() < getBuildSite();
    }

    public UUID getMarketId(){
        if(tradeTownId!=null) return tradeTownId;
        if(landHub) return uuid;
        return null;
    }

    public int getLocalGoods(){
        int result = 0;
        for (Building b:buildings){
            if(b.getTownId().equals(uuid)){
                result++;
            }
        }
        return result;
    }

    public int getMarketGoods(){
        int result = 0;
        if(landHub){
            for (Building b:db.getBuildings()){
                if(b.getMarketId() != null && b.getMarketId().equals(this.uuid) && b.getItem()!=null){
                    result++;
                }
            }
        }
        return result;
    }

    public List<Town> getTradeTowns(){
        HashSet<Town> towns = db.getTowns();
        List<Town> res = new ArrayList<>();

        for(Town t:towns){
            String path = "trade.towns."+ uuid + "." + t.getUniqueId();
            if (CustomConfig.get().getBoolean(path+".status")){
                res.add(t);
            }
        }
        return res;
    }

    public int getLocalGoodsCost(){
        int result = 0;
        for (Building b:buildings){
            if(b.getItem()!=null){
                int cost = Earth.getInstance().getConfig().getInt("tradeItems."+b.getItem().toString());
                if((cost==0)){
                    cost = Earth.getInstance().getConfig().getInt("tradeItems.STUFF");
                }
                result += cost;
            }
        }
        return result;
    }

    public double getTradeMod(){
        double marketMod = CustomConfig.get().getDouble("trade.towns."+uuid+".tradeMod");
        double portMod = 0.0;
        if(isPort()) portMod = 0.1;
        return Tools.round(0.01 * ((getPeople()*2) + (getMarketGoods() * getOwner().getAttribute(EPlayerAttribute.TRADE_GOODS_MOD)  )) + marketMod + portMod);
    }

    public String getColorTradeMod(){
        int t = (int) Math.round(getTradeMod()*100);

        String c = "&f";
        if(t>100)c = "&a";
        if(t<100)c = "&c";
        return c+t+"&f%";
    }

    public Town getTradeTown(){
        if(tradeTownId!=null){
            return db.getTown(tradeTownId);
        }else return null;
    }

    public double getTradeCost(){
        Town tradeTown = getTradeTown();
        double mod = 1.0;
        if(tradeTown!=null){
            String path = "trade.towns."+ uuid + "." + tradeTown.getUniqueId() + ".";
            if(CustomConfig.get().getBoolean(path+"status")){
                if (CustomConfig.get().getString(path+"type").equals("land")){
                    double distance = CustomConfig.get().getInt(path+"distance"); // Earth.getDistance(getTown().getChunkX(),getTown().getChunkZ(),b.getX(),b.getZ())*16;
                    mod -= Tools.round(0.01 * ( distance / 64));
                }

            }else{
                mod = 0.0;
            }
        }else return 1.0;
        //Bukkit.broadcastMessage(mod+" ");
        return mod;


    }

    public double getTradeIncome(){
        if(!isCore()||!isStatus()) return 0;
        double marketIncome = 0;
        double mod;
        Town tradeTown = getTradeTown();
        if(tradeTown!=null){
            mod = tradeTown.getTradeMod();
            String path = "trade.towns."+ uuid + "." + tradeTown.getUniqueId() + ".";
            if(CustomConfig.get().getBoolean(path+"status")){
                if (CustomConfig.get().getString(path+"type").equals("land")){
                    int distance = CustomConfig.get().getInt(path+"distance"); // Earth.getDistance(getTown().getChunkX(),getTown().getChunkZ(),b.getX(),b.getZ())*16;
                    mod -= 0.01 * (double) ( distance / 64);
                }

            }else{
                mod = 0;
            }
        }else mod = getTradeMod();
        if(tradeTownId != null || landHub){
            for(Building b:buildings){
                if(b.getItem()!=null){
                    double bMod = 1.0;
                    switch (b.getType()){
                        case "mineV2" ->{
                            bMod = 1.5;
                        }
                        case "career", "plant" ->{
                            bMod = 2;
                        }
                        case "factory" ->{
                            bMod = 3;
                        }
                    }
                    int cost = Earth.getInstance().getConfig().getInt("tradeItems."+b.getItem().toString());
                    if((cost==0)){
                        cost = Earth.getInstance().getConfig().getInt("tradeItems.STUFF");
                    }
                    marketIncome += Tools.round(cost * mod * bMod);
                }
            }
        }
        return marketIncome;
    }


    public double getProdIncome(){
        if(!isCore()||!isStatus()) return 0;
        double income=0;
        for(Building b:buildings){
            if (b.getItem()!=null){
                double mod = switch (b.getType()) {
                    case "plant", "career" -> 2;
                    case "mineV2" -> 1.5;
                    case "factory" -> 3;
                    default -> 1;};
                String item  = String.valueOf(b.getItem());
                int itemCost = Earth.getInstance().getConfig().getInt("tradeItems."+item);
                if(item != null && itemCost == 0) itemCost = Earth.getInstance().getConfig().getInt("tradeItems.STUFF");
                income += Tools.round(itemCost * (mod+getOwner().getAttribute(EPlayerAttribute.GOODS_MOD)));
            }

        }
        return income;
    }

    public double getIncome(){
        return Tools.round (getProdIncome()+getPeople()+getTradeIncome());
    }

    public void setTradeTownId(UUID tradeTownId) {
        this.tradeTownId = tradeTownId;
        for(Building b:buildings){
            b.setMarketId(tradeTownId);
        }
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Town town = (Town) o;
        return Objects.equals(uuid, town.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Town{name='" + name + "', uuid=" + uuid.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(Town other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = name.compareTo(other.name);
        if (res == 0) res = uuid.compareTo(other.uuid);
        return res;
    }

}
