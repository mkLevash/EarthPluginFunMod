package earthrp.runnable;

import earthrp.events.NewDayEvent;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;

public class CheckDay extends BukkitRunnable {

    public CheckDay() {}

    @Override
    public void run() {
        long time = Objects.requireNonNull(Bukkit.getServer().getWorld("world")).getTime();
        if (time < 20){
            Bukkit.getServer().getPluginManager().callEvent(new NewDayEvent());
        }


    }
}
