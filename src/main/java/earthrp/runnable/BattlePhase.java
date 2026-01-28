package earthrp.runnable;

import earthrp.battle.BattlePhaseEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class BattlePhase extends BukkitRunnable {

    public BattlePhase() {}

    @Override
    public void run() {
        Bukkit.getServer().getPluginManager().callEvent(new BattlePhaseEvent());
    }
}
