package earthrp.battle;

import earthrp.Earth;
import earthrp.customObjects.Army;
import earthrp.customObjects.ArmyUnit;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import lombok.Getter;
import org.bukkit.Chunk;
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

    public void newBattle(Set<Army> attacker, Set<Army> defender, Location location){



        Battle battle = new Battle(attacker,defender,location);
        battleCache.put(battle.getUuid(),battle);
    }

    public void updateBattle(Battle battle){
        battleCache.remove(battle.getUuid());
        battleCache.put(battle.getUuid(),battle);

    }

    public void shutdownBattles(){
        for(Battle b:getBattles()){
            for(Army a:b.getAtt()){
                a.setBattle(null);
            }
            for(Army a:b.getDef()){
                a.setBattle(null);
            }
            Tools.removePreBattleHologram(b.getLoc());
            Tools.removeBattleHologram(b.getLoc());
        }
    }

    public void delBattle(Battle battle){
        ServerDatabase db = Earth.getInstance().getDatabase();
        Tools.removePreBattleHologram(battle.getLoc());
        Tools.removeBattleHologram(battle.getLoc());
        for(BattleUnit u:battle.getUnits()){
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
        }

        for (Army a:battle.getAtt()){
            if (a.isBarbarian()) db.deleteArmy(a);
        }
        for (Army a:battle.getDef()){
            if (a.isBarbarian()) db.deleteArmy(a);
        }
        battleCache.remove(battle.getUuid());
    }

    public List<Battle> getBattles(){
        return new ArrayList<>(battleCache.values());
    }

    public Battle getBattle(Chunk chunk){
        for (Battle b:getBattles()){
            if(b.getLoc().getChunk().equals(chunk)){
                return b;
            }
        }
        return null;
    }


    private int baseCas(){
        return 0;
    }
}
