package earthrp.listeners;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class JoinHandler implements Listener {

    private final Earth earth;
    private ServerDatabase db;
    public JoinHandler(Earth earth) {
        this.earth = earth;
        db = this.earth.getServerDatabase();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e)  {
        if (Earth.getInstance().getConfig().getBoolean("debug")){
            Bukkit.broadcastMessage("[Earth] Проверьте файл config.yml в папке плагина");
        }
        Player javaPlayer = e.getPlayer();
        Set<EPlayer> players = db.getPlayers();


        if(!db.playerExists(javaPlayer.getUniqueId())){
            db.addPlayer(javaPlayer);
            String path = "trade."+javaPlayer.getDisplayName()+".";

            String warPath  = "war."+javaPlayer.getDisplayName()+".";

            for (EPlayer player:players){
                if(!player.getUniqueId().equals(javaPlayer.getUniqueId())){
                    CustomConfig.set(path+player.getDisplayName(),false);
                    CustomConfig.set("trade."+player.getDisplayName()+"."+javaPlayer.getDisplayName(),false);

                    CustomConfig.set(warPath+player.getDisplayName(),false);
                    CustomConfig.set("war."+player.getDisplayName()+"."+javaPlayer.getDisplayName(),false);
                }
            }



            CustomConfig.set("pips.players."+javaPlayer.getDisplayName()+".inf","none");
            CustomConfig.set("pips.players."+javaPlayer.getDisplayName()+".cav","none");
            CustomConfig.set("pips.players."+javaPlayer.getDisplayName()+".art","none");
        }else if(!db.playerNameActual(javaPlayer)){
            EPlayer player = db.getPlayer(javaPlayer.getUniqueId());
            player.setDisplayName(javaPlayer.getDisplayName());
        }



    }

}
