package earthrp.customObjects;

import earthrp.customEnums.EarthItem;
import earthrp.tools.Tools;
import earthrp.tools.maps.CityBoundaryCalculator;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.*;
@Getter
@Setter
public class TownData {

    private final Map<EarthItem, Long> items = new EnumMap<>(EarthItem.class);


    private int specialBuildings = 0;


    private UUID controller = Tools.EMPTY_UUID;

    private int siegeChance;
    public void addSiegeChance(int value){
        this.siegeChance+=value;
    }


    private final Set<UUID> siegeArmy = new LinkedHashSet<>();




    Set<CityBoundaryCalculator.chunkPoint> chunk = new HashSet<>();

    private boolean port;
    private boolean landHub;
    private boolean fort;
    private boolean core = true;
    private boolean blockade;
    public int houses;
    public int noble;
    public int bonusBuildSite;
    public int infrastructure;








}
