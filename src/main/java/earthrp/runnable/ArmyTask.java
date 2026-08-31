package earthrp.runnable;


import earthrp.Earth;
import earthrp.battle.Battle;
import earthrp.battle.BattleManager;
import earthrp.customObjects.Army;
import earthrp.customObjects.EPlayer;
import earthrp.tools.Tools;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ArmyTask extends BukkitRunnable {

    private boolean isOwnerOfChunk(EPlayer country) {
        var town = Earth.getInstance().getDatabase().getTownAtChunk(country.getData().getLocation());
        return town != null && town.getOwnerId().equals(country.getUniqueId());
    }

    // Быстрый сбор армий из UUID в объекты Army





    public ArmyTask() {
    }

    @Override
    public void run() {
        Set<Army> armies = Earth.getInstance().getDatabase().getArmies();
        Map<Long, List<Army>> armiesInChunks = new HashMap<>();
        BattleManager bm = Earth.getInstance().getBattleManager();
        List<Battle> battles = bm.getBattles();

        for (Army army : armies) {
            long chunkKey = army.getChunkKey(); // Ваш метод получения ID чанка
            armiesInChunks.computeIfAbsent(chunkKey, k -> new ArrayList<>()).add(army);

            Tools.editHologram(chunkKey,"armyHoloTroops" + army.getUuid().toString(),"Армия <light_purple>" + army.getOwner().getDisplayName() + "<white>'a - <green>" + (army.getTroops()/1000) + "K");
            Tools.editHologram(chunkKey,"armyHoloMorale" + army.getUuid().toString(),"Мораль <dark_green>" + army.getMorale() + "<white> / <dark_green>" + army.getMaxMorale());
        }

        for (long chunkKey : armiesInChunks.keySet()) {
            List<Army> chunkArmies = armiesInChunks.get(chunkKey);
            int size = chunkArmies.size();
            if (size < 2) continue; // Если в чанке один юнит, сравнивать не с кем


            Battle existingBattle = null;

            for (Battle b : battles) {
                if (b.getLoc().getChunk().getChunkKey() == chunkKey) {
                    existingBattle = b;
                    break;
                }
            }

            if (existingBattle != null) {

                // Берем ID лидера атакующих для проверки отношений (без тяжелых стримов)
                UUID attackerId = existingBattle.getAttacker().getUniqueId();

                boolean barbarianAtt = existingBattle.getAttList().getFirst().isBarbarian();
                boolean barbarianDef = existingBattle.getDefList().getFirst().isBarbarian();

                for (Army army : chunkArmies) {
                    if (army.isRetreat() || army.isBattle()) continue;

                    EPlayer country = army.getOwner();
                    existingBattle.join(army, !country.getData().getEnemies().contains(attackerId));
                    if(barbarianAtt){
                        existingBattle.join(army,false);
                    } else if (barbarianDef) {
                        existingBattle.join(army,true);
                    }
                    army.setBattle(existingBattle);
                }
            }else {
                Army armyA = null;
                Army armyB = null;
                boolean conflictFound = false;

                for (int i = 0; i < size && !conflictFound; i++) {
                    for (int j = i + 1; j < size; j++) {
                        Army a = chunkArmies.get(i);
                        Army b = chunkArmies.get(j);

                        if (a.isRetreat() || b.isRetreat()) continue;

                        EPlayer countryA = a.getOwner();
                        EPlayer countryB = b.getOwner();

                        if (countryA.getData().getEnemies().contains(countryB.getUniqueId())) {
                            armyA = a;
                            armyB = b;
                            conflictFound = true;
                            break;
                        }
                        if(countryA!=countryB && (a.isBarbarian() || b.isBarbarian())){
                            armyA = a;
                            armyB = b;
                            conflictFound = true;
                            break;
                        }
                    }
                }

                // Если конфликт обнаружен — инициализируем новую битву
                if (conflictFound) {
                    EPlayer countryA = armyA.getOwner();
                    EPlayer countryB = armyB.getOwner();

                    World world = Bukkit.getWorlds().getFirst();
                    Location location = getGroundUnderTree(chunkKey,world);

                    Set<Army> defender = new LinkedHashSet<>();
                    Set<Army> attacker = new LinkedHashSet<>();
                    EPlayer def, att;

                    if (isOwnerOfChunk(countryA)) {
                        defender.add(armyA); def = countryA;
                        attacker.add(armyB); att = countryB;
                    } else if (isOwnerOfChunk(countryB)) {
                        defender.add(armyB); def = countryB;
                        attacker.add(armyA); att = countryA;
                    } else {
                        if (armyA.getData().getLocationTime() < armyB.getData().getLocationTime()) {
                            defender.add(armyA); def = countryA;
                            attacker.add(armyB); att = countryB;
                        } else {
                            defender.add(armyB); def = countryB;
                            attacker.add(armyA); att = countryA;
                        }
                    }

                    for (Army army : chunkArmies) {
                        // Пропускаем инициаторов битвы (мы их уже добавили выше)
                        if (army.equals(armyA) || army.equals(armyB)) continue;
                        if (army.isRetreat() || army.isBattle()) continue;

                        EPlayer country = army.getOwner();


                        if (country.getData().getEnemies().contains(att.getUniqueId())) {
                            defender.add(army);
                        } else if(country.getData().getEnemies().contains(def.getUniqueId())){
                            attacker.add(army);
                        }else{
                            continue;
                        } // Блокируем армию в статусе боя
                    }


                    bm.newBattle(attacker, defender, location);

                    // Остальные армии этого чанка зайдут в эту битву уже на следующем тике/проверке,
                    // что разгружает процессор и предотвращает каскадные баги.
                }
            }

        }
    }

    private static Location getGroundUnderTree(long chunkKey, World world) {
        Chunk chunk = world.getChunkAt(chunkKey);

        int worldX = (chunk.getX() << 4) + 8;
        int worldZ = (chunk.getZ() << 4) + 8;


        Block current = chunk.getWorld().getHighestBlockAt(worldX, worldZ, HeightMap.WORLD_SURFACE);

        while (current.getY() > chunk.getWorld().getMinHeight()) {
            boolean isTree = Tag.LEAVES.isTagged(current.getType()) || Tag.LOGS.isTagged(current.getType());

            // Игнорируем деревья, воздух, лианы и жидкости
            if (!isTree && !current.isLiquid() && current.getType().isSolid()) {
                Block target = current.getRelative(0, 1, 0);

                // Проверяем, что для игрока есть 2 свободных блока по высоте
                if (target.isPassable() && target.getRelative(0, 1, 0).isPassable()) {
                    return target.getLocation();
                }
            }
            current = current.getRelative(0, -1, 0);
        }
        return current.getLocation();
    }




}
