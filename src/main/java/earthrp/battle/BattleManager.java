package earthrp.battle;

import earthrp.Earth;
import earthrp.customObjects.Army;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Unit;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import lombok.Getter;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class BattleManager {


    public BattleManager(){}

    private final Map<UUID, Battle> battleCache = new ConcurrentHashMap<>();


    public void newBattle(Army attacker, Army defender, int terrain, Location location){
        Battle battle = new Battle(attacker,defender,terrain,location);
        battleCache.put(battle.getUuid(),battle);

        Tools.spawnBattleHologram(battle);
    }

    public void newBattle(List<Army> attacker, List<Army> defender, Location location, EPlayer a,EPlayer d){

        Tools.spawnPreBattleHologram(location, a, d);

        Battle battle = new Battle(attacker,a,defender,d,location);
        battleCache.put(battle.getUuid(),battle);
    }

    public void updateBattle(Battle battle){
        battleCache.remove(battle.getUuid());
        battleCache.put(battle.getUuid(),battle);

    }

    public void delBattle(Battle battle){
        ServerDatabase db = Earth.getInstance().getServerDatabase();
        Tools.removePreBattleHologram(battle.getLoc());
        Tools.removeBattleHologram(battle.getLoc());
        for(BattleUnit u:battle.getAttUnits()){
            Unit unit = db.getUnit(u.getUniqueId());
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
        }
        for(BattleUnit u:battle.getDefUnits()){
            Unit unit = db.getUnit(u.getUniqueId());
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
        }
        battleCache.remove(battle.getUuid());
    }

    public List<Battle> getBattles(){
        return new ArrayList<>(battleCache.values());
    }


    private int baseCas(){
        return 0;
    }
}
