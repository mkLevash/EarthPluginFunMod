package earthrp.runnable;


import earthrp.Earth;
import earthrp.battle.Battle;
import earthrp.battle.BattleManager;
import earthrp.battle.BattleUnit;
import earthrp.customObjects.Army;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Unit;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ArmyTask extends BukkitRunnable {

    private boolean isOwnerOfChunk(EPlayer country) {
        var town = Earth.getInstance().getServerDatabase().getTownAtChunk(country.getData().getLocation());
        return town != null && town.getOwnerId().equals(country.getUniqueId());
    }

    // Быстрый сбор армий из UUID в объекты Army


    private Location getChunkCenter(Player player) {
        Location loc = player.getLocation();
        World world = loc.getWorld();

        // Находим координаты самого чанка (побитовый сдвиг вправо на 4, то же самое что деление на 16)
        int chunkX = loc.getBlockX() >> 4;
        int chunkZ = loc.getBlockZ() >> 4;

        // Вычисляем координаты центра чанка:
        // (chunk * 16) — это начало чанка (нулевой блок), и прибавляем 8 блоков для выхода на центр.
        // Добавляем 0.5, чтобы встать ровно в центр блока, а не на его край.
        double centerX = (chunkX << 4) + 8.5;
        double centerZ = (chunkZ << 4) + 8.5;

        // Y-координату (высоту) оставляем как у игрока, либо берем поверхность земли:
        //double centerY = loc.getY();
        // Если нужно найти именно поверхность земли в центре чанка, раскомментируйте строку ниже:
        double centerY = world.getHighestBlockYAt((int)centerX, (int)centerZ) + 1.0;

        return new Location(world, centerX, centerY, centerZ);
    }


    public ArmyTask() {
    }

    @Override
    public void run() {
        List<EPlayer> players = new ArrayList<>(Earth.getInstance().getServerDatabase().getPlayers());

        // Двойной цикл для сравнения всех армий между собой
        for (int i = 0; i < players.size(); i++) {
            EPlayer countryA = players.get(i);

            for (int j = i + 1; j < players.size(); j++) {
                EPlayer countryB = players.get(j);



                if(countryA.getData().getLocation() != countryB.getData().getLocation()) continue;


                if (countryA.getData().isRetreat() || countryB.getData().isRetreat()) continue;


                if(!countryA.getData().getWar().contains(countryB.getUniqueId())) continue;


                if(countryA.getData().armiesInHand.isEmpty() || countryB.getData().armiesInHand.isEmpty() ) continue;


                Location location = getChunkCenter(Bukkit.getPlayer(countryA.getUniqueId()));
                BattleManager bm = Earth.getInstance().getBattleManager();
                List<Battle> battles = bm.getBattles();
                Battle battle = null;
                for (Battle b:battles){
                    if(b.getLoc().getChunk().getChunkKey() == countryA.getData().getLocation()){
                        battle = b;
                        break;
                    }
                }
                if(battle == null){

                    List<Army> defender;
                    EPlayer def;
                    List<Army> attacker;
                    EPlayer att;
                    if (isOwnerOfChunk(countryA)) {
                        defender = countryA.getArmiesInHand();
                        def = countryA;
                        att = countryB;
                        attacker = countryB.getArmiesInHand();
                    } else if (isOwnerOfChunk(countryB)) {
                        defender = countryB.getArmiesInHand();
                        def = countryB;
                        att = countryA;
                        attacker = countryA.getArmiesInHand();
                    } else {
                        // Если земля ничья, защищается тот, кто пришел раньше (меньше время локации)
                        if (countryA.getData().getLocationTime() < countryB.getData().getLocationTime()) {
                            defender = countryA.getArmiesInHand();
                            def = countryA;
                            att = countryB;
                            attacker = countryB.getArmiesInHand();
                        } else {
                            defender = countryB.getArmiesInHand();
                            def = countryB;
                            att = countryA;
                            attacker = countryA.getArmiesInHand();
                        }
                    }
                    att.getData().setBattle(true);
                    def.getData().setBattle(true);
                    bm.newBattle(attacker,defender,location,att,def);
                }else{
                    if(!countryA.getData().isBattle()){
                        if(countryA.getData().getWar().contains(battle.getAttacker().getFirst().getUniqueId())){
                            battle.joinDef(countryA);
                        }else{
                            battle.joinAtt(countryA);
                        }
                    }
                    if(!countryB.getData().isBattle()){
                        if(countryB.getData().getWar().contains(battle.getAttacker().getFirst().getUniqueId())){
                            battle.joinDef(countryB);
                        }else{
                            battle.joinAtt(countryB);
                        }
                    }

                }




            }
        }
    }

}
