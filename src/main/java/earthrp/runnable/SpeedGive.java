package earthrp.runnable;

import earthrp.Earth;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class SpeedGive extends BukkitRunnable {
    public SpeedGive() {}
    @Override
    public void run() {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            Block block = player.getLocation().getBlock();

            if (block.getType() == Material.DIRT_PATH) {
                new BukkitRunnable(){
                    @Override
                    public void run(){
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.SPEED, 80, 3, false, false, false
                        ));
                    }
                }.runTask(Earth.getInstance());
            }

        }
    }
}
