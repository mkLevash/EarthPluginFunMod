package earthrp.battle;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Army;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.army.BattleJoinMenu;
import earthrp.menusystem.menu.army.BattleMenu;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;


public class BattleHandler implements Listener {
    private final Earth earth;
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    public BattleHandler(Earth earth) {
        this.earth = earth;
    }

    @EventHandler
    public void onBuild(BlockPlaceEvent e){
        Army attacker = Tools.getArmyFromShulker(e.getBlock());
        if (attacker!=null){
            Location loc = e.getBlockReplacedState().getLocation();
            Location[] locs = new Location[4];
            locs[0] = loc.clone().add(0,0,1);
            locs[1] = loc.clone().add(0,0,-1);
            locs[2] = loc.clone().add(1,0,0);
            locs[3] = loc.clone().add(-1,0,0);
            Army defender = null;
            // считывается сколько других шалкеров армий рядом с местом куда ставится шалкер с армией
            int c = 0;
            for (int i = 0; i < 4; i++) {
                if (Tools.getArmyFromShulker(locs[i].getBlock())!=null) {
                    c++;
                    defender = Tools.getArmyFromShulker(locs[i].getBlock());
                }
            }
            if (c==1 && !defender.isBattle()){// открывается меню подтверждения битвы
                e.setCancelled(true);
                MenuUtility mu = new MenuUtility(e.getPlayer());
                mu.setAttacker(attacker);
                mu.setDefender(defender);
                mu.setArmyShulkerLoc(loc);
                mu.setTerrain(0);
                mu.setShulkerColor(e.getBlock().getType());
                new BattleMenu(mu).open();
                //loc.getBlock().getState().
            } else if (c>1) {// отмена из-за наличия нескольких армий рядом
                e.setCancelled(true);
                e.getPlayer().sendMessage("неправильная позиция");
            } else if (defender != null && defender.isBattle()) {
                e.setCancelled(true);
                MenuUtility mu = new MenuUtility(e.getPlayer());
                mu.setArmyShulkerLoc(loc);
                mu.setDefender(defender);
                new BattleJoinMenu(mu).open();

            }
        }
    }
}


