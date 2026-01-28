package earthrp.runnable;

import earthrp.events.NewDayEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Meteor extends BukkitRunnable {

    public Meteor() {
    }

    @Override
    public void run() {
        int r = (int) (Math.random() * 100);
        if (r==69){
            Bukkit.getServer().getPluginManager().callEvent(new NewDayEvent());

        }


    }
}
