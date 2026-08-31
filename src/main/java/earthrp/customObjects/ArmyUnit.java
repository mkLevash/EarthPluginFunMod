package earthrp.customObjects;

import com.google.gson.Gson;
import earthrp.Earth;
import earthrp.customEnums.UnitTech;
import earthrp.customEnums.UnitTech.UnitType;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class ArmyUnit implements Comparable<ArmyUnit>{
    @Getter(AccessLevel.NONE)
    private final ServerDatabase db;

    private final UUID uniqueId;

    public ArmyUnit(UnitTech tech, UUID uniqueId, UUID armyId, String data){
        this.uniqueId = uniqueId;
        this.tech = tech;
        this.armyId = armyId;
        db = Earth.getInstance().getDatabase();
        loadData(data);
        hp = 1000;
        morale = getMaxMorale();
        type = tech.getType();
        lvl = tech.getLvl();
        fire = tech.getFire();
        shock = tech.getShock();
        pipsFire = tech.getFirePips();
        pipsShock = tech.getShockPips();
        pipsMorale = tech.getMoralePips();

    }
//    armyId TEXT NOT NULL,
//    type TEXT,
//    hp INT DEFAULT 1000,
//    morale REAL,
//    disc REAL,
//    fire INT,
//    shock INT
    private UUID armyId;
    private UnitTech tech;
    private UnitType type;
    private int lvl;
    private double disc;
    private double fire;
    private double shock;

    private int pipsFire;
    private int pipsShock;
    private int pipsMorale;

    private int hp;
    private double morale;

    public double getBaseMorale() {
        return tech.getMorale();
    }

    public int getDamageTaken(){
        return 1000-hp;
    }

    public String getName(){
        return tech.getDisplayName();
    }

    public EPlayer getOwner(){
        return getArmy().getOwner();
    }

    public double getMaxMorale() {

        double satiety = getOwner().getAttribute(EPlayerAttribute.ARMY_SATIETY);

        if(data.isLevies()){
            Set<PlayerModifier> modifiers = getOwner().getAttributeModifiers(EPlayerAttribute.MORALE_MOD);
            double mod = 1.0;
            if (modifiers != null && !modifiers.isEmpty()) {
                for (PlayerModifier m : modifiers){
                    double value = m.getAttributes().get(EPlayerAttribute.MORALE_MOD);
                    if(value<0) mod += value;
                }
            }
            return Math.max(0.1,getBaseMorale() * mod * satiety);


        }else{
            return Math.max(0.1, Tools.round((getBaseMorale() * getOwner().getMoraleMod()) * satiety) ) ;
        }


    }

    public Army getArmy() {
        return Earth.getInstance().getDatabase().getArmy(this.getArmyId());
    }

    public double getTac(){
        if(data.isLevies()){
            return Tools.round(getArmy().getOwner().getAttribute(EPlayerAttribute.TACTIC)*(1.0 + disc + data.getDisc()));
        }else{
            return Tools.round(getArmy().getOwner().getAttribute(EPlayerAttribute.TACTIC)*(getArmy().getDisc()+disc + data.getDisc()));
        }
    }

    //data
    private static final Gson gson = new Gson();
    private UnitData data;
    private String rawJson;    // То, что пришло из БД


    public void loadData(String json) {
        if (json == null || json.isEmpty()) {
            this.data = new UnitData();
        } else {
            this.data = gson.fromJson(json, UnitData.class);
        }
    }

    // Вызываем перед сохранением в БД
    public String serializeData() {
        return gson.toJson(this.data);
    }

    public void attrition(double percentage){
        setHp((int)Math.round(getHp() * (1.0 - percentage)));
    }

    public UnitData getData() {
        if (this.data == null) this.data = new UnitData();
        return this.data;
    }
    //data

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArmyUnit unit = (ArmyUnit) o;
        return Objects.equals(uniqueId, unit.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    @Override
    public String toString() {
        return "Unit{owner = '" + getArmy().getOwner().getDisplayName() + "', type='" + tech + "', uuid=" + uniqueId.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(ArmyUnit other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = tech.compareTo(other.tech);
        if (res == 0) res = uniqueId.compareTo(other.uniqueId);
        return res;
    }
}
