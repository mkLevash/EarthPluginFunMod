package earthrp.listeners;

import earthrp.customEnums.TownItem;
import earthrp.customObjects.*;
import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.events.TownCheckEvent;
import earthrp.tools.Tools;
import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.Set;

public class TownCheck implements Listener {

    private final Earth earthPlugin;
    private final ServerDatabase db;

    public TownCheck( Earth moraPlugin) {
        this.earthPlugin = moraPlugin;
        db = Earth.getInstance().getServerDatabase();
    }

    @EventHandler
    public void townCheck(TownCheckEvent e) {
        Set<EPlayer> players = db.getPlayers();
        for (EPlayer p:players){

            for(Building b:p.getBuildings()){
                if(b.getItem()!=null){
                    switch (b.getType()){
                        case "mineV1","lumber","mineV2" -> spawnItem(b,1);
                        case "factory" -> spawnItem(b,3);
                        case "career", "plant" -> spawnItem(b,2);

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
                double newManpower = p.getAttribute(EPlayerAttribute.MANPOWER) * 1000;
                for (Unit u : p.getUnits()) {
                    // 1. Восстановление морали (оставляем как было)
                    if (u.getMorale() != u.getMaxMorale()) {
                        double newMorale = Tools.round(u.getMorale() + (u.getMaxMorale() * 0.01) * p.getAttribute(EPlayerAttribute.MORALE_REDUCE));
                        u.setMorale(Math.min(u.getMaxMorale(), newMorale));
                    }

                    // 2. Восстановление HP на 3%
                    if (u.getHp() < 1000) {
                        double healAmount = 1000 * 0.03; // 3% от 1000 = 30 единиц HP

                        if (newManpower >= healAmount) {
                            newManpower -= healAmount;


                            u.setHp(Math.min(1000, u.getHp() + (int)healAmount));
                        }
                    }
                }
                p.setAttribute(EPlayerAttribute.MANPOWER, Math.min(p.getManpowerLimit(), Math.floor(newManpower / 1000.0)));
            }

        }


    }



    private void spawnItem(Building building, int amount){
        Location loc = building.getLocation();
        for (BlockState blockState : loc.getChunk().getTileEntities()) {
            if (building.getItem() != null && blockState instanceof Chest chest && loc.equals(chest.getLocation())) {
                chest.getBlockInventory().addItem(new ItemStack(building.getItem(),amount));
            }
        }
    }
}
