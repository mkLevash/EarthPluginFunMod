package earthrp.battle;

import earthrp.Earth;
import earthrp.customEnums.UnitTech.UnitType;
import earthrp.customObjects.Army;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.ArmyUnit;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
public class Battle {



    public Battle(Army attacker, Army defender, int terrain, Location location){
        this.uuid = UUID.randomUUID();
        att.add(attacker);
        def.add(defender);
        ter = -terrain;
        loc = location;
        cw = Stream.concat(att.stream(), def.stream())
                .mapToInt(Army::getCW) // Превращаем поток армий в поток чисел (double или int)
                .max()                    // Ищем максимум
                .orElse(15);
        for (ArmyUnit u : attacker.getUnits()){
            attUnits.add(new BattleUnit(u));
        }
        for (ArmyUnit u : defender.getUnits()){
            defUnits.add(new BattleUnit(u));
        }

        aRow1 = new BattleUnit[cw];
        dRow1 = new BattleUnit[cw];
        round = 0;
        phaseRound = 4;
        fire = true;
        aCavDebuff = 0;
        dCavDebuff = 0;

        aRow2 = new BattleUnit[cw];
        dRow2 = new BattleUnit[cw];

        attacker.setBattle(this);
        defender.setBattle(this);
    }

    public Battle(Set<Army> attacker, Set<Army> defender, Location location){
        this.uuid = UUID.randomUUID();
        att.addAll(attacker);
        def.addAll(defender);
        prePhase = true;
        loc = location;
        cw = Stream.concat(att.stream(), def.stream())
                .mapToInt(Army::getCW) // Превращаем поток армий в поток чисел (double или int)
                .max()                    // Ищем максимум
                .orElse(15);
        for(Army a:att){
            for (ArmyUnit u : a.getUnits()){
                attUnits.add(new BattleUnit(u));
            }
        }
        for(Army a:def){
            for (ArmyUnit u : a.getUnits()){
                defUnits.add(new BattleUnit(u));
            }
        }

        aRow1 = new BattleUnit[cw];
        dRow1 = new BattleUnit[cw];
        round = 0;
        phaseRound = 4;
        fire = true;
        aCavDebuff = 0;
        dCavDebuff = 0;

        aRow2 = new BattleUnit[cw];
        dRow2 = new BattleUnit[cw];

        for (Army a:attacker){
            a.setBattle(this);
        }
        for (Army a:defender){
            a.setBattle(this);
        }

        Tools.spawnPreBattleHologram(location, getAttacker(), getDefender());
    }


    public EPlayer getAttacker(){
        return getAttList().getFirst().getOwner();
    }

    public EPlayer getDefender(){
        return getDefList().getFirst().getOwner();
    }

    private final UUID uuid;
    private final Set<Army> att = new LinkedHashSet<>();
    private final Set<Army> def = new LinkedHashSet<>();

    public List<Army> getAttList(){return new ArrayList<>(att);}

    public List<Army> getDefList(){
        return new ArrayList<>(def);
    }

    private int ter;
    private final Location loc;

    private int roundACas;
    private int roundDCas;

    private int aDice;
    private int dDice;

    private double aCavDebuff;
    private double dCavDebuff;

    private final int cw;
    private final int fr = 2;

    private int round;
    private int phaseRound;
    private boolean fire;
    private boolean prePhase;

    private boolean townBattle;

    private BattleUnit[] aRow1;
    private BattleUnit[] dRow1;

    private BattleUnit[] aRow2;
    private BattleUnit[] dRow2;


    private List<BattleUnit> attUnits = new ArrayList<>();
    private List<BattleUnit> defUnits = new ArrayList<>();



    public static BattleUnit getUnit(UnitType type, List<BattleUnit> units, String status){

        return units.stream()
                .filter(u -> u.getTech().getType().equals(type)
                        && u.getStatus().equals(status)).max(Comparator.comparingInt(BattleUnit::getLvl) // Сначала по первому параметру
                        .thenComparingInt(BattleUnit::getHp))
                .orElse(null);
    }

    public List<BattleUnit> getUnits(){
        List<BattleUnit> units = new ArrayList<>(attUnits);
        units.addAll(defUnits);
        return units;
    }

    public double getAMorale(){
        List<BattleUnit> units = attUnits;
        if(units==null)return 0.0;
        double moraleSum = 0;
        for(BattleUnit u : units){
            moraleSum += u.getMorale();
        }

        return Tools.round(moraleSum/attUnits.size());
    }

    public double getDMorale(){
        List<BattleUnit> units = defUnits;
        if(units==null)return 0.0;
        double moraleSum = 0;
        for(BattleUnit u : units){
            moraleSum += u.getMorale();
        }

        return Tools.round(moraleSum/defUnits.size());
    }

    public void join(Army army, boolean joinAttacker){
        if(joinAttacker){
            this.getAtt().add(army);
            for(ArmyUnit u:army.getUnits()){
                this.getAttUnits().add(new BattleUnit(u));
            }
            army.setBattle(this);
        }else{
            this.getDef().add(army);
            for(ArmyUnit u:army.getUnits()){
                this.getDefUnits().add(new BattleUnit(u));
            }
            army.setBattle(this);
        }

    }

    public int getLoses(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            amount += 1000 - bu.getHp();
        }
        return amount;
    }

    public int getDefLoses(){
        return getLoses(defUnits);
    }

    public int getAttLoses(){
        return getLoses(attUnits);
    }

    public int getRetreatTroops(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("retreat")){
                amount += bu.getHp();
            }
        }
        return amount;
    }

    public int getDefRetreatTroops(){
        return getRetreatTroops(defUnits);
    }

    public int getAttRetreatTroops(){
        return getRetreatTroops(attUnits);
    }

    public int getRetreatRgt(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("retreat")){
                amount++;
            }
        }
        return amount;
    }

    public int getDefRetreatRgt(){
        return getRetreatRgt(defUnits);
    }

    public int getAttRetreatRgt(){
        return getRetreatRgt(attUnits);
    }



    private int getInBattleTroops(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("inRow")){
                amount += bu.getHp();
            }
        }
        return amount;
    }

    public int getDefInBattleTroops(){
        return getInBattleTroops(defUnits);
    }

    public int getAttInBattleTroops(){
        return getInBattleTroops(attUnits);
    }



    private int getInBattleRgt(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("inRow")){
                amount++;
            }
        }
        return amount;
    }

    public int getDefInBattleRgt(){
        return getInBattleRgt(defUnits);
    }

    public int getAttInBattleRgt(){
        return getInBattleRgt(attUnits);
    }

    private int getReserves(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("reserve")){
                amount += bu.getHp();
            }
        }
        return amount;
    }

    public int getDefReserves(){
        return getReserves(defUnits);
    }

    public int getAttReserves(){
        return getReserves(attUnits);
    }



    public void retreat(Army a){
        ServerDatabase db = Earth.getInstance().getDatabase();
        for(BattleUnit u:getUnits()){
            if(u.getArmy().equals(a)){
                ArmyUnit unit = db.getUnit(u.getUniqueId());
                unit.setMorale(u.getMorale());
                unit.setHp(u.getHp());
                if (unit.getHp() == 0) {
                    if(Earth.getInstance().getConfig().getBoolean("debug")){
                        Earth.getInstance().getLogger().info("unit Dead -" + unit);
                    }
                    else{
                        db.deleteUnit(u);
                    }
                }
                u.setStatus("retreat");
            }

        }
        a.setRetreat(true);


    }
}
