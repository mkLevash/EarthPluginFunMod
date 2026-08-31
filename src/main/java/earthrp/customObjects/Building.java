package earthrp.customObjects;

import earthrp.Earth;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EarthItem;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import com.google.gson.Gson;

import java.util.*;

@Getter
@Setter
public class Building implements Comparable<Building>{




    private final ServerDatabase db;

    private final UUID uuid;

    private UUID townId;

    private final Location location;







    public Building( UUID buildingUniqueId, UUID townUniqueId, Location location, String jsonData){
        this.uuid = buildingUniqueId;
        this.townId = townUniqueId;
        this.location = location;

        loadData(jsonData);
        db = Earth.getInstance().getDatabase();

    }



    //data
    private static final Gson gson = new Gson();
    private BuildingData data;
    private String rawJson;    // То, что пришло из БД


    public void loadData(String json) {
        if (json == null || json.isEmpty()) {
            this.data = new BuildingData();
        } else {
            this.data = gson.fromJson(json, BuildingData.class);
        }
    }

    // Вызываем перед сохранением в БД
    public String serializeData() {
        return gson.toJson(this.data);
    }

    public BuildingData getData() {
        if (this.data == null) this.data = new BuildingData();
        return this.data;
    }
    //data

    public UUID getUniqueId(){
        return this.uuid;
    }

    public Town getTown(){return db.getTown(townId);}

    public EPlayer getOwner(){return db.getPlayer(getTown().getOwnerId());}



    public double getFarmEfficiency(){
        double temp = location.getBlock().getTemperature();
        double humidity = location.getBlock().getHumidity();

        //если температура 0.15 и ниже (вода замерзает) — ферма не работает
        if (temp <= 0.15) {
            return 0;
        }
        String biome = location.getBlock().getBiome().translationKey().toLowerCase(Locale.ROOT);

        double tempFactor = 1.0 - Math.abs(0.8 - temp);
        double mod = Math.max(0.0, tempFactor * (1+humidity/2));
        if(biome.contains("plains")){
            return mod;
        } else if (biome.contains("swamp") && biome.contains("peaks")) {
            return 0.0;
        } else{
            return 0.5 * mod;
        }


    }

    public double getIncome(Set<EarthItem> world){
        double income = 0;
        BuildingType type = getData().getType();
        if(type == BuildingType.PASTURE || type == BuildingType.FARM) return income;
        EarthItem item = getData().getItem();
        if(item!=null){
            double mod;
            if(!world.contains(item))mod = 2.0;
            else mod = 1.0;

            income += item.getCost() * mod * getBaseProduction();
        }
        return income;
    }

    public long getFarmProduction(){
        if(getFarmEfficiency() == 0) return 0;
        double prod = getFarmEfficiency() + getOwner().getAttribute(EPlayerAttribute.GOODS_MOD) + getOwner().getAttribute(EPlayerAttribute.FARM_EFFICIENCY);
        int farmLand = data.countFarmland(location);
        return Math.round(farmLand * prod);

    }

    public long getPastureProduction(){
        int S = getData().pastureArea;
        return Math.round(((0.5 + getOwner().getAttribute(EPlayerAttribute.GOODS_MOD) ) * S)/2.0 + 1 );

    }

    public long getFisherProduction(){
        int F = getOwner().getData().getTradeShips();
        return Math.round(getBaseProduction() * F);

    }

    public double getBaseProduction(){
        double base = this.data.getType().getBaseProd();
        base += getOwner().getAttribute(EPlayerAttribute.GOODS_MOD);
        switch (getData().getType()){
            case LUMBER -> {
                base += getOwner().getAttribute(EPlayerAttribute.LUMBER_PROD);
                base *= getLumberEfficiency();
            }
            case MINE,PIT,QUARRY -> {
                base += getOwner().getAttribute(EPlayerAttribute.MINE_PROD);
                base *= getMineEfficiency();
            }
        }
        return Tools.round(base);

    }

    public double getMineEfficiency(){

        String biome = location.getBlock().getBiome().translationKey().toLowerCase(Locale.ROOT);

        List<String> mtsBiomes = List.of("peaks","windswept","meadow");
        if(mtsBiomes.stream().anyMatch(biome::contains)) {
            return 1.0;
        }
        return 0.25;

    }

    public double getLumberEfficiency(){

        String biome = location.getBlock().getBiome().translationKey().toLowerCase(Locale.ROOT);

        List<String> forestBiomes = List.of("forest","taiga");
        if(forestBiomes.stream().anyMatch(biome::contains)) {
            return 1.0;
        }
        return 0.25;

    }

    public long getProduction(){
        int N = getTown().getPeasant();
        return Math.round(getBaseProduction() * N);
    }



    public List<String> getItemLore () {
        List<String> lore = new ArrayList<>();
        switch (data.getType()){
            case PASTURE -> {
                int F = getTown().getPeasant() * 50;
                int S = getData().pastureArea;
                double prod = 0.1 + getOwner().getAttribute(EPlayerAttribute.GOODS_MOD);
                int animalAmount = (int) Math.round(( prod * S * F ) / (S + F));
                lore.add(("<white>Кол-во животных в день: <green>" + animalAmount));
            }
            case FARM -> {

                long amount = getFarmProduction();
                lore.add(("<white>Производит: <green>" + amount));
            }
            case FORGE -> {
                int N = getTown().getPeasant();
                long amount = Math.round(getBaseProduction() * N);
                lore.add(("<white>Производит: <green>" + amount));
                lore.add((" "));
                switch (getData().getItem()){
                    case IRON_SWORD -> {
                        lore.add(("<white>Потребляет:"));
                        lore.add(("<blue>Железо"));

                    }

                    case COPPER_SWORD -> {
                        lore.add(("<white>Потребляет:"));
                        lore.add(("<blue>Медный слиток"));

                    }
                }
                lore.add(("<blue>Уголь"));
            }

            case FISHER -> {
                lore.add(("<white>Производит: <green>" + getFisherProduction()));
            }

            default -> {
                long amount = getProduction();
                lore.add(("<white>Производит: <green>" + amount));
                lore.add((" "));
                switch (getData().getItem()){
                    case IRON_SWORD -> {
                        lore.add(("<white>Потребляет:"));
                        lore.add(("<blue>Железный слиток"));
                    }

                    case COPPER_SWORD -> {
                        lore.add(("<white>Потребляет:"));
                        lore.add(("<blue>Медный слиток"));

                    }

                    default -> {

                        Set<EarthItem> recipe = getData().getItem().getRequirement();
                        if(!recipe.isEmpty()){
                            lore.add(("<white>Потребляет:"));
                            if(recipe.stream().anyMatch(i -> i.name().contains("PLANKS"))){
                                lore.add(("<blue>Доски"));
                            }
                            if(recipe.stream().anyMatch(i -> i.name().contains("INGOT"))){
                                lore.add(("<blue>слиток металла"));
                            }
                            if(recipe.stream().anyMatch(i -> i.name().contains("COAL"))){
                                lore.add(("<blue>Уголь"));
                            }
                            if(recipe.stream().anyMatch(i -> i.name().contains("LOG")) || recipe.stream().anyMatch(i -> i.name().contains("WOOD"))){
                                lore.add(("<blue>Древесина"));
                            }
                            for(EarthItem ei: recipe){
                                if(!ei.name().contains("PLANKS") && !ei.name().contains("LOG") && !ei.name().contains("WOOD") && !ei.name().contains("INGOT") && !ei.name().contains("COAL")){
                                    lore.add(("<blue>"+ei.getDisplayName()));
                                }
                            }
                        }


                    }
                }

            }
        }
        return lore;
    }





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return Objects.equals(uuid, building.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid);
    }

    @Override
    public String toString() {
        return "Building{town = '" + getTown().getName() + "', type='" + data.getType() + "', uuid=" + uuid.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(Building other) {
        // Сортировка по городам, а если города равны — по UUID
        int res = townId.compareTo(other.townId);
        if (res == 0) res = uuid.compareTo(other.uuid);
        return res;
    }

    public static int countEnclosedArea(Location startLoc, int maxBlocks) {
        Set<Block> found = new HashSet<>();
        Queue<Block> queue = new LinkedList<>();

        Block startBlock = startLoc.getBlock();
        if (isFence(startBlock.getType())) return 0; // Центр не может быть забором

        queue.add(startBlock);
        found.add(startBlock);

        while (!queue.isEmpty() && found.size() < maxBlocks) {
            Block current = queue.poll();

            // Проверяем 4 соседних блока (Север, Юг, Восток, Запад)
            for (BlockFace face : new BlockFace[]{BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST}) {
                Block relative = current.getRelative(face);
                Material type = relative.getType();

                // Если это не забор и мы тут еще не были
                if (!isFence(type) && !found.contains(relative)) {
                    found.add(relative);
                    queue.add(relative);
                }
            }
        }

        // Если мы достигли лимита, значит забор не закрыт или слишком велик
        if (found.size() >= maxBlocks) return -1;

        return found.size();
    }

    private static boolean isFence(Material material) {
        // Tag.FENCES — включает все деревянные и адские заборы
        // Tag.FENCE_GATES — включает все виды калиток
        return Tag.FENCES.isTagged(material) || Tag.FENCE_GATES.isTagged(material);
    }

}
