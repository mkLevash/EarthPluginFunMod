package earthrp.customObjects;

import earthrp.Earth;
import earthrp.database.ServerDatabase;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;

import java.util.Objects;
import java.util.UUID;

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


    public Building( UUID buildingUniqueId, UUID townUniqueId, String townName, UUID marketUniqueId, String buildingType, int buildingStatus, String material, Location location){
        this.uuid = buildingUniqueId;
        this.tuuid = townUniqueId;
        this.tName = townName;
        this.muuid = marketUniqueId;
        this.type = buildingType;
        this.status = buildingStatus;
        this.item = material != null ? Material.matchMaterial(material) : null;
        this.location = location;
        db = Earth.getInstance().getServerDatabase();

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

}
