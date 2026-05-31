package earthrp.customObjects;

import earthrp.Earth;
import earthrp.database.ServerDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import com.google.gson.Gson;

import java.util.*;

@Getter
@Setter
public class Building implements Comparable<Building>{




    private final ServerDatabase db;

    private final UUID uuid;

    private UUID muuid;

    private final String type;

    private String tName;
    private UUID tuuid;

    private int status;

    private final Location location;

    private Material item;




    public Building( UUID buildingUniqueId, UUID townUniqueId, String townName, UUID marketUniqueId, String buildingType, int buildingStatus, String material, Location location, String jsonData){
        this.uuid = buildingUniqueId;
        this.tuuid = townUniqueId;
        this.tName = townName;
        this.muuid = marketUniqueId;
        this.type = buildingType;
        this.status = buildingStatus;
        this.item = material != null ? Material.matchMaterial(material) : null;
        this.location = location;
        loadData(jsonData);
        db = Earth.getInstance().getServerDatabase();

    }

    private static final Gson gson = new Gson();

    private BuildingData data; // Объект с данными
    private String rawJson;    // То, что пришло из БД

    // Вызываем при загрузке из БД
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

    // Удобный геттер
    public BuildingData getData() {
        if (this.data == null) this.data = new BuildingData();
        return this.data;
    }

    public UUID getUniqueId(){
        return this.uuid;
    }
    public UUID getTownId(){return this.tuuid;}
    public Town getTown(){return db.getTown(tuuid);}
    public EPlayer getOwner(){return db.getPlayer(getTown().getOwnerId());}
    public UUID getMarketId(){return this.muuid;}
    public String getTownName(){return this.tName;}




    public void setTownId(UUID newTownId){this.tuuid = newTownId;}

    public void setMarketId(UUID newMarketId){this.muuid = newMarketId;
        
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
        return "Building{town = '" + tName + "', type='" + type + "', uuid=" + uuid.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(Building other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = tName.compareTo(other.tName);
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
