package earthrp.battle;

import earthrp.customObjects.Army;
import earthrp.customObjects.Unit;
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
        for (Unit u : attacker.getUnits()){
            attUnits.add(new BattleUnit(u));
        }
        for (Unit u : defender.getUnits()){
            defUnits.add(new BattleUnit(u));
        }

        aRow1 = new BattleUnit[cw];
        dRow1 = new BattleUnit[cw];
        round = 0;
        aCavDebuff = 0;
        dCavDebuff = 0;

        aRow2 = new BattleUnit[cw];
        dRow2 = new BattleUnit[cw];

        attacker.setBattle(true);
        defender.setBattle(true);

        this.attacker = attacker;
        this.defender = defender;

    }

    private Army attacker;
    private Army defender;
    private final UUID uuid;
    private final Set<Army> att = new HashSet<>();
    private final Set<Army> def = new HashSet<>();
    private final int ter;
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

    private BattleUnit[] aRow1;
    private BattleUnit[] dRow1;

    private BattleUnit[] aRow2;
    private BattleUnit[] dRow2;


    private List<BattleUnit> attUnits = new ArrayList<>();
    private List<BattleUnit> defUnits = new ArrayList<>();

    public static BattleUnit getUnit(String type, List<BattleUnit> units, String status, int rowStatus){
        return units.stream()
                .filter(u -> u.getType().equals(type)
                        && u.getStatus().equals(status)
                        && u.getRowIndex() == rowStatus).max(Comparator.comparingInt(BattleUnit::getLvl) // Сначала по первому параметру
                        .thenComparingInt(BattleUnit::getHp))
                .orElse(null);
    }

    public static BattleUnit getUnit(String type, List<BattleUnit> units, String status){
        // Сначала по первому параметру
        // Если равны, то по второму
        // Переворачиваем, чтобы были по убыванию
        return units.stream()
                .filter(u -> u.getType().equals(type)
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
}
