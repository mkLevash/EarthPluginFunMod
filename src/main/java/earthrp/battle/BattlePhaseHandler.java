package earthrp.battle;

import earthrp.Earth;
import earthrp.customEnums.UnitTech.UnitType;
import earthrp.customObjects.Town;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Army;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.*;


public class BattlePhaseHandler implements Listener {
    private final Earth earth;
    public BattlePhaseHandler( Earth earth) {
        this.earth = earth;
    }

    private String formatUnit(BattleUnit u,int i){
        if (u != null) return "["+i+"]" + u;
        else return "["+i+"]";
    }

    @EventHandler
    public void battlePhase(BattlePhaseEvent e){

        for(Battle battle : e.getBattles()){
            if (battle.isPrePhase()) continue;
            Earth.getInstance().getBattleManager().updateBattle(battle);
            if(isSideAlive(battle.getAttUnits()) && isSideAlive(battle.getDefUnits())){

                unFillRows(battle);
                fillRows(battle,"inBattle");
                fillRows(battle,"reserve");






                updateBattleHolograms(battle);
                int R = battle.getRound()+1;
                battle.setRound(R);
                if(R == battle.getPhaseRound()){
                    battle.setFire(!battle.isFire());
                    battle.setPhaseRound(R+3);
                }

                battle.setADice((int) (Math.random() * 10));
                battle.setDDice((int) (Math.random() * 10));

                if (Earth.getInstance().getConfig().getBoolean("debug")){
                    Earth.getInstance().getLogger().warning("Проверьте файл config.yml в папке плагина");


                    Bukkit.getConsoleSender().sendMessage("\n=== BATTLE FIELD DEBUG " + battle.getRound() + " ===\n");
                    int width = battle.getCw();

                    for (int i = 0; i < width; i++) {
                        Bukkit.getConsoleSender().sendMessage(formatUnit(battle.getARow2()[i],i) + "|"+ formatUnit(battle.getARow1()[i],i) + " || " + formatUnit(battle.getDRow1()[i],i) + "|" + formatUnit(battle.getDRow2()[i],i));
                    }

                    // Выводим в консоль Paper
                    Bukkit.getConsoleSender().sendMessage("===========================");
                }


                fighting(battle);






               // System.out.println("[Earth]"+battle.getRoundACas()+"| Урон |"+battle.getRoundDCas());

                //System.out.println("[Earth]("+getInBattleRgt(battle.getAttUnits())+")"+getInBattleTroops(battle.getAttUnits())+"| В бою |"+getInBattleTroops(battle.getDefUnits()) + "("+getInBattleRgt(battle.getDefUnits())+")" );

                //System.out.println("[Earth]("+getRetreatRgt(battle.getAttUnits())+")"+getRetreatTroops(battle.getAttUnits())+"| Отступили |"+getRetreatTroops(battle.getDefUnits()) + "("+getRetreatRgt(battle.getDefUnits())+")" );

                //System.out.println("[Earth]"+getLoses(battle.getAttUnits()) + "| Потери |" + getLoses(battle.getDefUnits()));

                //System.out.println("[Earth]"+getReserves(battle.getAttUnits())+"| Резервы |"+getReserves(battle.getDefUnits()));

                //System.out.println("[Earth]"+battle.getAMorale()+"|ᠩ|"+battle.getDMorale());

                //System.out.println("[Earth]"+ battle.getAtt().getTactic()*(1-battle.getACavDebuff())+"|ᠨ|"+battle.getDef().getTactic()*(1-battle.getDCavDebuff()));

                //System.out.println("[Earth]-----------");


            }else if (isSideAlive(battle.getAttUnits())) {
                finalizeBattle(battle.getAtt(), battle.getDef(), true, battle);
            } else {
                finalizeBattle(battle.getDef(), battle.getAtt(), false, battle);
            }
        }

    }

    private void finalizeBattle(Set<Army> winners, Set<Army> losers, boolean attackerWon, Battle battle) {
        // Начисляем очки победителям (3) и проигравшим (1)
        boolean flag = false;
        for(var a : losers){
            if(a.isBarbarian()){
                flag = true;
                break;
            }
        }
        if(flag){
            processParticipants(winners, 3);
            processParticipants(losers, 1);
        }

        // Логика сообщения
        String msg = String.format("&4%s &eнапал &fна &2%s &fи %s",
                battle.getAttacker().getCountryName(),
                battle.getDefender().getCountryName(),
                (attackerWon ? "&aпобедил" : "&cпроиграл"));
        Bukkit.broadcastMessage(Tools.colorText(msg));

        // Логика отступления
        retreat(attackerWon ? battle.getDef() : battle.getAtt(),
                attackerWon ? battle.getAtt() : battle.getDef(),
                attackerWon ? battle.getDefUnits() : battle.getAttUnits());

        Earth.getInstance().getBattleManager().delBattle(battle);
    }

    private void processParticipants(Set<Army> armies, int points) {
        for (Army a : armies) {
            if (a.isBarbarian()) continue;
            a.getOwner().addAttribute(EPlayerAttribute.TRADITION, Math.round(points * a.getOwner().getAttribute(EPlayerAttribute.TRADITION_MOD)));
            if(a.getOwner().getAttribute(EPlayerAttribute.TRADITION)>100){
                a.getOwner().setAttribute(EPlayerAttribute.TRADITION,100.0);
            }
            a.setBattle(null);
        }
    }

    private static void retreat(Set<Army> retreat, Set<Army> winner, List<BattleUnit> winnerUnits){
        for(Army a:retreat){
            Player javaPlayer = Bukkit.getPlayer(a.getOwner().getUniqueId());
            if(javaPlayer != null) javaPlayer.sendActionBar(Tools.deserialize("Ваша армия отступает"));
            a.getData().setRetreat(true);
            a.setBattle(null);
            if(a.isBarbarian()){
                handleBarbarian(a);
            }
        }
        for(Army a:winner){
            a.setBattle(null);
            if(a.isBarbarian()){
                handleBarbarian(a);
            }
        }
        for(BattleUnit bu:winnerUnits){
            if(bu.getMorale()!=bu.getMaxMorale()){
                double newMorale = Tools.round(bu.getMorale() + (bu.getMaxMorale()*0.33));
                bu.setMorale(Math.min(bu.getMaxMorale(),newMorale));
            }
        }
    }



    private void fighting(Battle battle){

        Map<UUID, Map<String, Double>> aCas;
        Map<UUID, Map<String, Double>> dCas;

        BattleUnit[] ARow1 = battle.getARow1();
        BattleUnit[] ARow2 = battle.getARow2();


        BattleUnit[] DRow1 = battle.getDRow1();
        BattleUnit[] DRow2 = battle.getDRow2();


        calcCavDebuff(ARow1);
        calcCavDebuff(DRow1);


        aCas = attackRow(battle,ARow1,DRow1,ARow2,DRow2);
        dCas = attackRow(battle,DRow1,ARow1,DRow2,ARow2);

        battle.setRoundACas(0);
        battle.setRoundDCas(0);

        for (BattleUnit bu: ARow1){
            if(bu!=null){
                if(dCas.containsKey(bu.getUniqueId())){
                    int tCas = (int) (double) dCas.get(bu.getUniqueId()).get("cas");
                    double mCas = dCas.get(bu.getUniqueId()).get("mCas");
                    battle.setRoundDCas( (battle.getRoundDCas()+tCas));
                    bu.dealCas(tCas);
                    bu.dealMCas(mCas);
                    if (bu.getHp()==0){
                        bu.setStatus("dead");
                        ARow1[bu.getRowIndex()] = null;
                    } else if (bu.getMorale() <= 0) {
                        bu.setStatus("retreat");
                        ARow1[bu.getRowIndex()] = null;
                    }
                }
            }
        }

        for (BattleUnit bu: DRow1){
            if(bu!=null){
                if(aCas.containsKey(bu.getUniqueId())){
                    int tCas = (int) (double) aCas.get(bu.getUniqueId()).get("cas");
                    double mCas = aCas.get(bu.getUniqueId()).get("mCas");
                    battle.setRoundACas( (battle.getRoundACas()+tCas));
                    bu.dealCas(tCas);
                    bu.dealMCas(mCas);
                    if (bu.getHp()==0){
                        System.out.println("[Earth]"+bu.getStatus());
                        bu.setStatus("dead");
                        DRow1[bu.getRowIndex()] = null;
                    } else if (bu.getMorale() <= 0) {
                        bu.setStatus("retreat");
                        DRow1[bu.getRowIndex()] = null;
                    }
                }
            }
        }

        reserveDamage(battle.getAttUnits(),getMaxMorale(battle.getDefUnits()));
        reserveDamage(battle.getDefUnits(),getMaxMorale(battle.getAttUnits()));


    }

    private double getMaxMorale(List<BattleUnit> sideUnits){
        double moraleSum = 0;
        for (BattleUnit u : sideUnits){

            moraleSum += u.getMaxMorale();


        }
        int size = Math.max(1,sideUnits.size());
        return Tools.round(moraleSum/size);
    }

    private void reserveDamage(List<BattleUnit> sideUnits, double maxMorale){
        for (BattleUnit u: sideUnits){
            if (u.getStatus().equals("reserve")){
                u.setMorale(Math.max (0,Tools.round(u.getMorale()-(maxMorale*0.02))));

            }
        }
    }

    private Map<UUID, Map<String, Double>> attackRow(Battle battle, BattleUnit[] row, BattleUnit[] targetRow, BattleUnit[] artRow, BattleUnit[] targetArtRow){
        Map<UUID, Map<String, Double>> cas = new HashMap<>();

        for (BattleUnit bu:row){
            if(bu!=null){
                BattleUnit targetUnit = findTarget(bu,targetRow);
                BattleUnit targetArt = targetArtRow[bu.getRowIndex()];
                if(targetUnit!=null){
                    if(cas.containsKey(targetUnit.getUniqueId())){
                        Map<String, Double> newCas = calcCas(battle,bu,targetUnit,targetArt);
                        double tCas = newCas.get("cas") + cas.get(targetUnit.getUniqueId()).get("cas");
                        double mCas = newCas.get("mCas") + cas.get(targetUnit.getUniqueId()).get("mCas");
                        Map<String, Double> updatedCas = new HashMap<>();
                        updatedCas.put("cas", tCas);
                        updatedCas.put("mCas", mCas);
                        cas.put(targetUnit.getUniqueId(),updatedCas);
                    }else cas.put(targetUnit.getUniqueId(),calcCas(battle,bu,targetUnit,targetArt));
                }
            }

        }
        for (BattleUnit bu:artRow){
            if(bu!=null){
                BattleUnit targetUnit = findTarget(bu,targetRow);
                BattleUnit targetArt = targetArtRow[bu.getRowIndex()];
                if(targetUnit!=null){
                    Map<String, Double> newCas = calcCas(battle,bu,targetUnit,targetArt);
                    double oldCas = 0;
                    double oldMCas = 0;
                    if(cas.containsKey(targetUnit.getUniqueId())) {
                        oldCas = cas.get(targetUnit.getUniqueId()).get("cas");
                        oldMCas = cas.get(targetUnit.getUniqueId()).get("mCas");
                    }
                    double tCas = (newCas.get("cas") * 0.5) + oldCas;
                    double mCas = (newCas.get("mCas") * 0.5 ) + oldMCas;
                    Map<String, Double> updatedCas = new HashMap<>();
                    updatedCas.put("cas", tCas/2);
                    updatedCas.put("mCas", mCas/2);
                    cas.put(targetUnit.getUniqueId(),updatedCas);
                }
            }
        }

        return cas;
    }

    private Map<String, Double> calcCas(Battle battle, BattleUnit unit, BattleUnit targetUnit,BattleUnit targetArt){
        double mod = 1.0;
        if(targetUnit.getTech().getType() == UnitType.ART) mod = 2;
        if(unit.getTech().getType() == UnitType.ART) mod /= 2;
        int unitPips;
        double unitDamage;
        double damageMod;
        int targetPips;
        double cavMod = 1 - targetUnit.getCavDebuff();
        double damageRec;
        int leaderPips;
        int R = battle.getRound();
        if (!battle.isFire()){//если чёт - фаза шока
            unitPips = unit.getOffShockPips();
            targetPips = targetUnit.getDefShockPips() + (targetArt != null ? targetArt.getDefShockPips()/2 : 0);
            leaderPips = Math.max(0, unit.getArmy().getLeaderShock()-targetUnit.getArmy().getLeaderShock());
            unitDamage = unit.getShock();
            damageMod = unit.getShockDamage();
            damageRec = targetUnit.getShockReceived();
        }else{
            unitPips = unit.getOffFirePips();
            targetPips = targetUnit.getDefFirePips() + (targetArt != null ? targetArt.getDefFirePips()/2 : 0);
            leaderPips = Math.max(0, unit.getArmy().getLeaderFire()-targetUnit.getArmy().getLeaderFire());
            unitDamage = unit.getFire();
            damageMod = unit.getFireDamage();
            damageRec = targetUnit.getFireReceived();
        }
        int targetMoralPips = targetUnit.getDefMoralePips() + (targetArt != null ? targetArt.getDefMoralePips()/2 : 0);
        int d;
        int md;
        if(battle.getAtt().contains(unit.getArmy())){
            d = battle.getADice() + battle.getTer() + unitPips - targetPips + leaderPips;
            md = battle.getADice() + battle.getTer() + unit.getOffMoralePips() - targetMoralPips + leaderPips;

        }else{
            d = battle.getDDice() + unitPips - targetPips + leaderPips;
            md = battle.getDDice() + unit.getOffMoralePips() - targetMoralPips + leaderPips;
        }


        int dBase = Math.max(0, 15 + 5 * d);
        int mdBase = Math.max(0, 15 + 5 * md);

        double CASMultipliers = unit.getHp()*0.001 * (unitDamage/(targetUnit.getTac()*cavMod)) * unit.getCA() * unit.getDisciple() * (1+(double) ( R ) / 100);


        Map<String, Double> res = new HashMap<>();
        res.put("cas",mod * dBase * CASMultipliers * damageMod * damageRec);
        res.put("mCas", Tools.round(mod * mdBase * CASMultipliers * unit.getMoraleDamage() * targetUnit.getMoraleReceived() * (unit.getMaxMorale()/540.0)));
        return res;


    }




    private void calcCavDebuff(BattleUnit[] row){



        Map<UUID, Integer> cav = new HashMap<>();
        Map<UUID, Integer> totalTroops = new HashMap<>();


        for (BattleUnit bu : row) {
            if (bu == null) continue;

            UUID ownerId = bu.getArmy().getOwnerId();
            int hp = bu.getHp();
            UnitType type = bu.getTech().getType();
            totalTroops.merge(ownerId, hp, Integer::sum);

            if (type == UnitType.CAV) {
                cav.merge(ownerId, hp, Integer::sum);
            }
        }

        //  Рассчитываем дебафф для кавалерии
        for (BattleUnit bu : row) {
            if (bu != null && bu.getTech().getType() == UnitType.CAV) {
                UUID ownerId = bu.getArmy().getOwnerId();

                double cavalry = cav.getOrDefault(ownerId, 0);
                double allTroops = totalTroops.getOrDefault(ownerId, 0);

                if (allTroops > 0) {
                    double currentRatio = Tools.round(cavalry / allTroops);
                    if (currentRatio > bu.getCavRatio()) {
                        double debuff = Tools.round((currentRatio - bu.getCavRatio()) / 2.0);
                        bu.setCavDebuff(debuff);
                        continue;
                    }
                }
                bu.setCavDebuff(0.0);
            }
        }
    }

    private boolean isSideAlive(List<BattleUnit> sideUnits){
        return !sideUnits.stream().allMatch(battleUnit -> battleUnit.getStatus().equals("retreat") || battleUnit.getStatus().equals("dead"));
    }

    private void updateBattleHolograms(Battle battle){
        Location battleLoc = battle.getLoc();
        String ter;
        if(battle.getTer()!=0) ter = Tools.getColorModLegacy(battle.getTer(),false,true);
        else ter = "";
        if(!battle.isFire()) Tools.editHologramLegacy(battleLoc,"battlePhase","Фаза &6шока");
        else Tools.editHologramLegacy(battleLoc,"battlePhase","Фаза &4огня");

        Tools.editHologramLegacy(battleLoc,"battleDice","&c" + battle.getRoundACas() + " &3"+battle.getADice()+ter+"&f |Бросок &3кубика&f| &3"+battle.getDDice() + "&c " + battle.getRoundDCas() );

        //Tools.editHologram(battleLoc,"battleCas","&c"+battle.getRoundACas()+"&f |Урон| &c"+battle.getRoundDCas());

        Tools.editHologramLegacy(battleLoc,"battleTroops","(&d"+battle.getAttInBattleRgt()+"&f/&a"+battle.getCw() + "&f)" + "&a"+battle.getAttInBattleTroops()+" &f|В бою| &a"+battle.getDefInBattleTroops()  + "&f(&d"+battle.getDefInBattleRgt()+"&f/&a"+battle.getCw() +"&f)" );

        //Tools.editHologram(battleLoc,"battleRetreat","(&d"+getRetreatRgt(battle.getAttUnits())+"&f)&4"+getRetreatTroops(battle.getAttUnits())+"&f|Отступили|&4"+getRetreatTroops(battle.getDefUnits()) + "&f(&d"+getRetreatRgt(battle.getDefUnits())+"&f)" );

        //Tools.editHologram(battleLoc,"battleReserve",getReserves(battle.getAttUnits())+" |Резервы| "+getReserves(battle.getDefUnits()));

        Tools.editHologramLegacy(battleLoc,"battleMorale", "&2"+battle.getAMorale()+"&f |ᠩ| &2"+battle.getDMorale());

       //Tools.editHologram(battleLoc,"battleTac", "&b"+battle.getAttacker().getTactic()*(1-battle.getACavDebuff())+"&f |ᠨ| &b"+battle.getDefender().getTactic()*(1-battle.getDCavDebuff()));




    }



    private BattleUnit findTarget(BattleUnit attacker, BattleUnit[] targetRow){
        int pos = attacker.getRowIndex(); // Текущая позиция атакующего
        int range = attacker.getFr();     // Дальность атаки (фронтаж)
        int maxIndex = targetRow.length - 1;

        // 1. Проверка юнита прямо перед собой
        if (pos >= 0 && pos <= maxIndex && targetRow[pos] != null) {
            return targetRow[pos];
        }

        // 2. Поиск в радиусе getFr()
        // Мы идем кругами от центральной точки pos: pos+1, pos-1, pos+2, pos-2...
        for (int i = 1; i <= range; i++) {
            int right = pos + i;
            int left = pos - i;

            // Проверяем соседа справа
            if (right <= maxIndex && targetRow[right] != null) {
                return targetRow[right];
            }
            // Проверяем соседа слева
            if (left >= 0 && targetRow[left] != null) {
                return targetRow[left];
            }
        }

        // 3. Если в первом ряду никого нет в радиусе, цель не найдена
        return null;


    }

    private void fillRows(Battle battle, String status){
        int fr = battle.getFr();
        int cw = battle.getCw();

        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(),UnitType.INF, fr, cw - fr,status);
        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(),UnitType.CAV, 0, cw,status);
        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(),UnitType.INF, 0, cw,status);
        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(), UnitType.ART, 0, cw,status);


        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), UnitType.INF,  fr, cw - fr,status);
        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), UnitType.CAV,  0, cw,status);
        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), UnitType.INF,  0, cw,status);
        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), UnitType.ART,  0, cw,status);



    }

    private void unFillRows(Battle battle){


        for(BattleUnit u: battle.getUnits()){
            if(u.getStatus().equals("inRow")){
                u.setStatus("inBattle");
            }
        }

        Arrays.fill(battle.getARow1(), null);
        Arrays.fill(battle.getARow2(), null);

        Arrays.fill(battle.getDRow1(), null);
        Arrays.fill(battle.getDRow2(), null);
    }


    private void fillRow(List<BattleUnit> sideUnits,   BattleUnit[] firstRow, BattleUnit[] secondRow , UnitType type, int start, int end, String status) {
        int middle = (start + end) / 2;
        String inRow = "inRow";
        for (int i = 0; i <= middle; i++) {
            int right = middle + i;
            int left = middle - i;
            // Заполняем вправо
            if (right < end) {
                if(firstRow[right] == null){
                    BattleUnit firstUnit = Battle.getUnit(type,sideUnits,status);
                    if(firstUnit==null) break;
                    firstRow[right] = firstUnit;
                    firstUnit.setRowIndex(right);
                    firstUnit.setStatus(inRow);
                }else{
                    BattleUnit secondUnit = Battle.getUnit(UnitType.ART,sideUnits,status);
                    if(secondRow[right] == null &&  secondUnit != null){
                        secondRow[right] = secondUnit;
                        secondUnit.setRowIndex(right);
                        secondUnit.setStatus(inRow);
                    }
                }



            }
            // Заполняем влево
            if (left >= start && left != right){
                if(firstRow[left] == null){
                    BattleUnit firstUnit = Battle.getUnit(type,sideUnits,status);
                    if(firstUnit==null) break;
                    firstRow[left] = firstUnit;
                    firstUnit.setRowIndex(left);
                    firstUnit.setStatus(inRow);
                }else{
                    BattleUnit secondUnit = Battle.getUnit(UnitType.ART,sideUnits,status);
                    if(secondRow[left] == null &&  secondUnit != null){
                        secondRow[left] = secondUnit;
                        secondUnit.setRowIndex(left);
                        secondUnit.setStatus(inRow);
                    }
                }


            }
        }

    }

    private static void handleBarbarian(Army a){
        if(a.isRetreat()){

            Town town = a.getBarbarianTown();
            a.getBarbarianChest().addItem(a.getBarbarianOwnerItem());
            a.getBarbarianChest().addItem(a.getBarbarianTownItem().clone());
            a.getBarbarianTownItem().setAmount(0);

            Tools.deleteHologram(a.getChunkKey(),"townHolo");
            Tools.deleteHologram(a.getChunkKey(),"armyHoloTroops" + a.getUuid());
            Tools.deleteHologram(a.getChunkKey(),"armyHoloMorale" + a.getUuid());

            Tools.spawnHologramLegacy(a.getBarbarianChest().getLocation().clone().add(0.5, 1, 0.5),town.getName(),town.getUniqueId().toString());

            Earth.getInstance().getDatabase().addTown(town);
            town.getOwner().addBarbariansAmount(1);


        }else{
            a.getBarbarianTown().getOwner().addBarbariansAmount(1);
        }



        a.setBarbarianTownItem(null);
        a.setBarbarianTown(null);
        a.setBarbarianOwnerItem(null);
        a.setBarbarianChest(null);
    }






}
