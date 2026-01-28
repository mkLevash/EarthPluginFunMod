package earthrp.runnable;

import earthrp.events.TownCheckEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class CheckTowns extends BukkitRunnable {

    public CheckTowns() {
    }

    @Override
    public void run() {
        Bukkit.getServer().getPluginManager().callEvent(new TownCheckEvent());

    }
}
