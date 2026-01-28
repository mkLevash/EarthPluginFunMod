package earthrp.customObjects;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
public class Unit implements Comparable<Unit>{
    @Getter(AccessLevel.NONE)
    private final ServerDatabase db;

    private final UUID uniqueId;

    public Unit(UUID uniqueId){
        this.uniqueId = uniqueId;
        db = Earth.getInstance().getServerDatabase();
    }
//    armyId TEXT NOT NULL,
//    type TEXT,
//    hp INT DEFAULT 1000,
//    morale REAL,
//    disc REAL,
//    fire INT,
//    shock INT
    private UUID armyId;
    private String type;
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
        return Math.max(2.5,lvl+2);
    }

    public int getDamageTaken(){
        return 1000-hp;
    }

    public String getName(){
        switch (type){
            case "inf" ->{
                switch (lvl){
                    case 0 ->{return "Ополчение";}
                    case 1 ->{return "Копейщики";}
                    case 2 ->{return "Лучники";}
                    case 3 ->{return "Аркебузиры";}
                    case 4 ->{return "Мушкетёры";}
                }
            }
            case "cav" ->{
                switch (lvl){
                    case 0 ->{return "Ополчение";}
                    case 1 ->{return "Лёгкие всадники";}
                    case 2 ->{return "Конные Лучники";}
                    case 3 ->{return "Тяжёлая кавалерия";}
                    case 4 ->{return "Карабинеры";}
                }
            }
            case "art" ->{
                switch (lvl){
                    case 0 ->{return "Ополчение";}
                    case 1 ->{return "Лёгкие всадники";}
                    case 2 ->{return "Конные Лучники";}
                    case 3 ->{return "Большая чугунная пушка";}
                    case 4 ->{return "Тяжёлая гаубица";}
                }
            }
        }
        return "default Name";
    }

    public double getMaxMorale() {
        return Tools.round(getBaseMorale() * getArmy().getOwner().getMorale());
    }

    public Army getArmy() {
        return Earth.getInstance().getServerDatabase().getArmy(this.getArmyId());
    }

    public double getTac(){
        return Tools.round(getArmy().getOwner().getAttribute(EPlayerAttribute.TACTIC)*(getArmy().getDisc()+disc));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return Objects.equals(uniqueId, unit.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(uniqueId);
    }

    @Override
    public String toString() {
        return "Unit{owner = '" + getArmy().getOwner().getDisplayName() + "', type='" + type + "', uuid=" + uniqueId.toString().substring(0,5) + "}";
    }

    @Override
    public int compareTo(Unit other) {
        // Сортировка по имени, а если имена равны — по UUID
        int res = type.compareTo(other.type);
        if (res == 0) res = uniqueId.compareTo(other.uniqueId);
        return res;
    }
}
