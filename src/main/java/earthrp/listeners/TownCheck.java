package earthrp.listeners;

import earthrp.customEnums.BuildingType;
import earthrp.customObjects.*;
import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.events.TownCheckEvent;
import earthrp.tools.Tools;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class TownCheck implements Listener {

    private final Earth earthPlugin;
    private final ServerDatabase db;

    public TownCheck( Earth moraPlugin) {
        this.earthPlugin = moraPlugin;
        db = Earth.getInstance().getDatabase();
    }

    @EventHandler
    public void townCheck(TownCheckEvent e) {
        for (Army a:db.getArmies()){
            if(a.isBarbarian()) continue;
            boolean enemy = a.isEnemyLoc() || a.isBarbarianLoc();
            boolean neutral = !enemy && !a.isAllyLoc();
            double attritionChance = 0.0;

            if (a.isSieging()) {
                attritionChance = 0.3;
            } else if (enemy) {
                attritionChance = 0.2;
            } else if (neutral) {
                attritionChance = 0.1;
            }
            if (attritionChance > 0.0) {
                for (ArmyUnit u : a.getUnits()) {
                    if (Math.random() < attritionChance + a.getOwner().getAttribute(EPlayerAttribute.ATTRITION_CHANCE)) {
                        double attrition = 0.01;
                        if(a.isEnemyLoc()){
                            attrition += a.getTownAt().getController().getAttribute(EPlayerAttribute.ATTRITION_FOR_ENEMY);

                        }
                        u.attrition(attrition);

                    }
                }
            }
        }


        Set<EPlayer> players = db.getPlayers();
        for (EPlayer p:players){

            for (Army a:p.getArmiesInHand()){
                if(a.isBarbarian()) continue;
                if(a.isAllyLoc()){
                    for (ArmyUnit u : a.getUnits()) {
                        if (Math.random() < 0.05) {
                            u.attrition(0.01);
                        }
                    }
                }
            }


            for (Town t : p.getTowns()) {
                if (!t.getType().equals("capital")) continue;

                double target = p.getAttribute(EPlayerAttribute.ARMY_SATIETY_MAX);
                double current = p.getAttribute(EPlayerAttribute.ARMY_SATIETY);

                if (current > target) {
                    // Армия голодает, сытость падает
                    p.addAttribute(EPlayerAttribute.ARMY_SATIETY, -0.01);
                } else if (current < target) {
                    // Армия накормлена, сытость растет (только если еды реально много)
                    p.addAttribute(EPlayerAttribute.ARMY_SATIETY, 0.01);
                }

                target = p.getAttribute(EPlayerAttribute.ARMY_SUPPLY_MAX);
                current = p.getAttribute(EPlayerAttribute.ARMY_SUPPLY);

                if (current > target) {
                    // Армия голодает, сытость падает
                    p.addAttribute(EPlayerAttribute.ARMY_SUPPLY, -0.01);
                } else if (current < target) {
                    // Армия накормлена, сытость растет (только если еды реально много)
                    p.addAttribute(EPlayerAttribute.ARMY_SUPPLY, 0.01);
                }


                double newManpower = p.getAttribute(EPlayerAttribute.MANPOWER);
                for (ArmyUnit u : p.getUnits()) {
                    // 1. Восстановление морали (оставляем как было)
                    if (u.getMorale() != u.getMaxMorale()) {
                        double newMorale = Tools.round(u.getMorale() + (u.getMaxMorale() * 0.01) * p.getAttribute(EPlayerAttribute.MORALE_REDUCE));
                        u.setMorale(Math.min(u.getMaxMorale(), newMorale));
                    }

                    // 2. Восстановление HP на 3%
                    if (u.getHp() < 1000) {
                        double healAmount = 1000 * 0.03; // 3% от 1000 = 30 единиц HP

                        if(u.getData().isMerc()){
                            u.setHp(Math.min(1000, u.getHp() + (int)healAmount));
                        }else{
                            if (newManpower >= healAmount) {
                                newManpower -= healAmount;


                                u.setHp(Math.min(1000, u.getHp() + (int)healAmount));
                            }
                        }


                    }
                }
                p.setAttribute(EPlayerAttribute.MANPOWER, newManpower);
            }

        }


    }



    private void spawnItem(Building building, int amount){
        building.getTown().addItem(building.getData().getItem(),amount);
    }
}
