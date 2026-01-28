package earthrp.battle;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Army;
import org.bukkit.Bukkit;
import org.bukkit.Location;
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
            int cw = battle.getCw();
            Earth.getInstance().getBattleManager().updateBattle(battle);
            if(isSideAlive(battle.getAttUnits()) && isSideAlive(battle.getDefUnits())){
                fillRows(battle);





                updateBattleHolograms(battle);

                battle.setRound(battle.getRound()+1);

                battle.setADice((int) (Math.random() * 10));
                battle.setDDice((int) (Math.random() * 10));

                if (Earth.getInstance().getConfig().getBoolean("debug")){
                    Earth.getInstance().getLogger().warning("Проверьте файл config.yml в папке плагина");


                    Bukkit.getConsoleSender().sendMessage("\n=== BATTLE FIELD DEBUG ===\n");
                    int width = battle.getCw();

                    for (int i = 0; i < width; i++) {
                        Bukkit.getConsoleSender().sendMessage(formatUnit(battle.getARow2()[i],i) + "|"+ formatUnit(battle.getARow1()[i],i) + " || " + formatUnit(battle.getDRow1()[i],i) + "|" + formatUnit(battle.getDRow2()[i],i));
                    }

                    // Выводим в консоль Paper
                    Bukkit.getConsoleSender().sendMessage("===========================");
                }


                fighting(battle);

                refillRows(battle);




               // System.out.println("[Earth]"+battle.getRoundACas()+"| Урон |"+battle.getRoundDCas());

                //System.out.println("[Earth]("+getInBattleRgt(battle.getAttUnits())+")"+getInBattleTroops(battle.getAttUnits())+"| В бою |"+getInBattleTroops(battle.getDefUnits()) + "("+getInBattleRgt(battle.getDefUnits())+")" );

                //System.out.println("[Earth]("+getRetreatRgt(battle.getAttUnits())+")"+getRetreatTroops(battle.getAttUnits())+"| Отступили |"+getRetreatTroops(battle.getDefUnits()) + "("+getRetreatRgt(battle.getDefUnits())+")" );

                //System.out.println("[Earth]"+getLoses(battle.getAttUnits()) + "| Потери |" + getLoses(battle.getDefUnits()));

                //System.out.println("[Earth]"+getReserves(battle.getAttUnits())+"| Резервы |"+getReserves(battle.getDefUnits()));

                //System.out.println("[Earth]"+battle.getAMorale()+"|ᠩ|"+battle.getDMorale());

                //System.out.println("[Earth]"+ battle.getAtt().getTactic()*(1-battle.getACavDebuff())+"|ᠨ|"+battle.getDef().getTactic()*(1-battle.getDCavDebuff()));

                //System.out.println("[Earth]-----------");


            }else if(isSideAlive(battle.getAttUnits())){
                for (Army a: battle.getAtt()){
                    a.getOwner().addAttribute(EPlayerAttribute.TRADITION,1);
                    a.setBattle(false);
                }
                for (Army a: battle.getDef()){
                    a.getOwner().addAttribute(EPlayerAttribute.TRADITION,3);
                    a.setBattle(false);
                }
                Bukkit.broadcastMessage(Tools.colorText("&4" + battle.getAttacker().getOwner().getCountryName() + "&e напал &fна &2" + battle.getDefender().getOwner().getCountryName() + "&f и&a победил"));
                Earth.getInstance().getBattleManager().delBattle(battle);
            }else{
                for (Army a: battle.getAtt()){
                    a.getOwner().addAttribute(EPlayerAttribute.TRADITION,3);
                    a.setBattle(false);
                }
                for (Army a: battle.getDef()){
                    a.getOwner().addAttribute(EPlayerAttribute.TRADITION,1);
                    a.setBattle(false);
                }
                Bukkit.broadcastMessage(Tools.colorText("&4" + battle.getAttacker().getOwner().getCountryName() + "&e напал &fна &2" + battle.getDefender().getOwner().getCountryName() + "&f и&c проиграл"));
                Earth.getInstance().getBattleManager().delBattle(battle);
            }
        }

    }



    private void fighting(Battle battle){

        Map<UUID, Map<String, Double>> aCas;
        Map<UUID, Map<String, Double>> dCas;

        Army att = battle.getAttacker();
        BattleUnit[] ARow1 = battle.getARow1();
        BattleUnit[] ARow2 = battle.getARow2();

        Army def = battle.getDefender();
        BattleUnit[] DRow1 = battle.getDRow1();
        BattleUnit[] DRow2 = battle.getDRow2();

        battle.setACavDebuff(calcCavDebuff(ARow1,att.getCavRatio()));
        battle.setDCavDebuff(calcCavDebuff(DRow1,def.getCavRatio()));

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
                    } else if (bu.getMorale() == 0) {
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
                    } else if (bu.getMorale() == 0) {
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
                u.setMorale(Tools.round(u.getMorale()-(maxMorale*0.02)));

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
        if(targetUnit.getType().equals("art")) mod = 2;
        int unitPips;
        double unitDamage;
        double damageMod;
        int targetPips;
        double cavMod;
        double damageRec;
        int leaderPips;
        int R = battle.getRound();
        if (R%2==0){//если чёт - фаза шока
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
            cavMod = 1 - battle.getDCavDebuff();
        }else{
            d = battle.getDDice() + unitPips - targetPips + leaderPips;
            md = battle.getDDice() + unit.getOffMoralePips() - targetMoralPips + leaderPips;
            cavMod = 1 - battle.getACavDebuff();
        }


        int dBase = Math.max(0,15 + 5 * d);
        int mdBase = Math.max(0,15 + 5 * md);

        double CASMultipliers = unit.getHp()*0.001 * (unitDamage/targetUnit.getTac()*cavMod) * unit.getCA() * unit.getDisciple() * (1+(double) ( R * 3) / 100);


        Map<String, Double> res = new HashMap<>();
        res.put("cas",mod * dBase * CASMultipliers * damageMod * damageRec);
        res.put("mCas", Tools.round(mod * mdBase * CASMultipliers * unit.getMoraleDamage() * targetUnit.getMoraleReceived() * (unit.getMaxMorale()/540.0)));
        return res;


    }




    private double calcCavDebuff(BattleUnit[] row, double cavRatio){
        double inf = 0;
        double cav = 0;
        for(BattleUnit bu: row){
            if(bu!=null){
                if(bu.getType().equals("inf")) inf+=bu.getHp();
                if(bu.getType().equals("cav")) cav+=bu.getHp();
            }
        }
        double troops = inf+cav;
        if( Tools.round(cav / troops) > cavRatio){
            return Tools.round((cav/troops - cavRatio) / 2);
        }else return 0.0;
    }

    private boolean isSideAlive(List<BattleUnit> sideUnits){
        return !sideUnits.stream().allMatch(battleUnit -> battleUnit.getStatus().equals("retreat") || battleUnit.getStatus().equals("dead"));
    }

    private void updateBattleHolograms(Battle battle){
        Location battleLoc = battle.getLoc();
        String ter;
        if(battle.getTer()!=0) ter = Tools.getColorMod(battle.getTer(),false,true);
        else ter = "";
        if(battle.getRound()%2==0) Tools.editHologram(battleLoc,"battlePhase","Фаза &6шока");
        else Tools.editHologram(battleLoc,"battlePhase","Фаза &4огня");

        Tools.editHologram(battleLoc,"battleDice","&3"+battle.getADice()+ter+"&f |Бросок &3кубика&f| &3"+battle.getDDice());

        Tools.editHologram(battleLoc,"battleCas","&c"+battle.getRoundACas()+"&f |Урон| &c"+battle.getRoundDCas());

        Tools.editHologram(battleLoc,"battleTroops","(&d"+getInBattleRgt(battle.getAttUnits())+"&f)&a"+getInBattleTroops(battle.getAttUnits())+" &f|В бою| &a"+getInBattleTroops(battle.getDefUnits()) + "&f(&d"+getInBattleRgt(battle.getDefUnits())+"&f)" );

        Tools.editHologram(battleLoc,"battleRetreat","(&d"+getRetreatRgt(battle.getAttUnits())+"&f)&4"+getRetreatTroops(battle.getAttUnits())+"&f|Отступили|&4"+getRetreatTroops(battle.getDefUnits()) + "&f(&d"+getRetreatRgt(battle.getDefUnits())+"&f)" );

        Tools.editHologram(battleLoc,"battleReserve",getReserves(battle.getAttUnits())+" |Резервы| "+getReserves(battle.getDefUnits()));

        Tools.editHologram(battleLoc,"battleMorale", "&2"+battle.getAMorale()+"&f |ᠩ| &2"+battle.getDMorale());

        Tools.editHologram(battleLoc,"battleTac", "&b"+battle.getAttacker().getTactic()*(1-battle.getACavDebuff())+"&f |ᠨ| &b"+battle.getDefender().getTactic()*(1-battle.getDCavDebuff()));




    }

    public int getLoses(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            amount += 1000 - bu.getHp();
        }
        return amount;
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

    public int getRetreatRgt(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("retreat")){
                amount++;
            }
        }
        return amount;
    }



    private int getInBattleTroops(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("inBattle")){
                amount += bu.getHp();
            }
        }
        return amount;
    }

    private int getInBattleRgt(List<BattleUnit> sideUnits){
        int amount = 0;
        for(BattleUnit bu:sideUnits){
            if(bu.getStatus().equals("inBattle")){
                amount++;
            }
        }
        return amount;
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

    private void fillRows(Battle battle){
        int fr = battle.getFr();
        int cw = battle.getCw();





        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(),"inf", fr, cw - fr);
        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(),"cav", 0, cw);
        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(),"inf", 0, cw);
        fillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(), "art", 0, cw);


        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), "inf",  fr, cw - fr);
        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), "cav",  0, cw);
        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), "inf",  0, cw);
        fillRow(battle.getDefUnits(), battle.getDRow1(),battle.getDRow2(), "art",  0, cw);



    }

    private void refillRows(Battle battle){
        int fr = battle.getFr();
        int cw = battle.getCw();

        unFillRows(battle);


        refillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(), "inf", fr, cw - fr);
        refillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(), "cav", 0, cw);
        refillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(), "inf", 0, cw);
        refillRow(battle.getAttUnits(), battle.getARow1(), battle.getARow2(), "art", 0, cw);


        refillRow(battle.getDefUnits(), battle.getDRow1(), battle.getDRow2(), "inf",  fr, cw - fr);
        refillRow(battle.getDefUnits(), battle.getDRow1(), battle.getDRow2(), "cav",  0, cw);
        refillRow(battle.getDefUnits(), battle.getDRow1(), battle.getDRow2(), "inf",  0, cw);
        refillRow(battle.getDefUnits(), battle.getDRow1(), battle.getDRow2(), "art",  0, cw);



    }

    private void unFillRows(Battle battle){


        for(BattleUnit u: battle.getUnits()){
            if(u.getStatus().equals("inBattle")){
                u.setRowIndex(-1);
            }
        }

        Arrays.fill(battle.getARow1(), null);
        Arrays.fill(battle.getARow2(), null);

        Arrays.fill(battle.getDRow1(), null);
        Arrays.fill(battle.getDRow2(), null);
    }

    private void refillRow(List<BattleUnit> sideUnits,   BattleUnit[] firstRow, BattleUnit[] secondRow  , String type, int start, int end) {
        int middle = (start + end) / 2;
        String status = "inBattle";
        for (int i = 0; i <= middle; i++) {
            int right = middle + i;
            int left = middle - i;
            // Заполняем вправо
            if (right < end && firstRow[right] == null) {
                BattleUnit firstUnit = Battle.getUnit(type,sideUnits,status,-1);
                if(firstUnit==null) break;
                firstRow[right] = firstUnit;
                firstUnit.setRowIndex(right);
                BattleUnit secondUnit = Battle.getUnit("art",sideUnits,status,-1);
                if(secondRow[right] == null &&  secondUnit != null){
                    secondRow[right] = secondUnit;
                    secondUnit.setRowIndex(right);
                }

            }
            // Заполняем влево
            if (left >= start && firstRow[left] == null){
                BattleUnit firstUnit = Battle.getUnit(type,sideUnits,status,-1);
                if(firstUnit==null) break;
                firstRow[left] = firstUnit;
                firstUnit.setRowIndex(left);
                BattleUnit secondUnit = Battle.getUnit("art",sideUnits,status,-1);
                if(secondRow[left] == null &&  secondUnit != null){
                    secondRow[left] = secondUnit;
                    secondUnit.setRowIndex(left);
                }
            }
        }
    }


    private void fillRow(List<BattleUnit> sideUnits,   BattleUnit[] firstRow, BattleUnit[] secondRow , String type, int start, int end) {
        int middle = (start + end) / 2;
        String status = "reserve";

        for (int i = 0; i <= middle; i++) {
            int right = middle + i;
            int left = middle - i;
            // Заполняем вправо
            if (right < end && firstRow[right] == null) {
                BattleUnit firstUnit = Battle.getUnit(type,sideUnits,status);
                if(firstUnit==null) break;
                firstRow[right] = firstUnit;
                firstUnit.setRowIndex(right);
                firstUnit.setStatus("inBattle");
                BattleUnit secondUnit = Battle.getUnit("art",sideUnits,status);
                if(secondRow[right] == null &&  secondUnit != null){
                    secondRow[right] = secondUnit;
                    secondUnit.setRowIndex(right);
                    secondUnit.setStatus("inBattle");
                }

            }
            // Заполняем влево
            if (left >= start && firstRow[left] == null){
                BattleUnit firstUnit = Battle.getUnit(type,sideUnits,status);
                if(firstUnit==null) break;
                firstRow[left] = firstUnit;
                firstUnit.setRowIndex(left);
                firstUnit.setStatus("inBattle");
                BattleUnit secondUnit = Battle.getUnit("art",sideUnits,status);
                if(secondRow[left] == null &&  secondUnit != null){
                    secondRow[left] = secondUnit;
                    secondUnit.setRowIndex(left);
                    secondUnit.setStatus("inBattle");
                }
            }
        }


//        while (sideUnits.stream().anyMatch(bu -> bu.getStatus().equals("reserve") && bu.getType().equals(type))
//                && Arrays.stream(sideRow).anyMatch(Objects::isNull)
//                && (left >= start || right < end)) {
//
//            if (left >= start && sideRow[left] == null) {
//                for (BattleUnit bu : sideUnits) {
//                    if (bu.getStatus().equals("reserve") && bu.getType().equals(type)) {
//                        sideRow[left] = bu;
//                        bu.setStatus("inBattle");
//                        bu.setRowIndex(left);
//                        break;
//                    }
//                }
//            }
//            left--;
//
//            if (right < end && sideRow[right] == null) {
//                for (BattleUnit bu : sideUnits) {
//                    if (bu.getStatus().equals("reserve") && bu.getType().equals(type)) {
//                        sideRow[right] = bu;
//                        bu.setStatus("inBattle");
//                        bu.setRowIndex(right);
//                        break;
//                    }
//                }
//            }
//            right++;
//        }
    }





}
