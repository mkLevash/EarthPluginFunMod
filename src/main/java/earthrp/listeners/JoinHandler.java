package earthrp.listeners;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import earthrp.configs.CustomConfig;
import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class JoinHandler implements Listener {

    private final Earth earth;
    private ServerDatabase db;
    public JoinHandler(Earth earth) {
        this.earth = earth;
        db = this.earth.getDatabase();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e)  {
        if (Earth.getInstance().getConfig().getBoolean("debug")){
            Bukkit.broadcastMessage("[Earth] Проверьте файл config.yml в папке плагина");
        }
        Player javaPlayer = e.getPlayer();
        Set<EPlayer> players = db.getPlayers();

        List<NamespacedKey> recipesToDiscover = new ArrayList<>();
        recipesToDiscover.add(new NamespacedKey(Earth.getInstance(), "superMora"));
        recipesToDiscover.add(new NamespacedKey(Earth.getInstance(), "megaMora"));
        recipesToDiscover.add(new NamespacedKey(Earth.getInstance(), "army_inf0"));
        recipesToDiscover.add(new NamespacedKey(Earth.getInstance(), "army_cav0"));
        recipesToDiscover.add(new NamespacedKey(Earth.getInstance(), "town"));
        // Добавляйте сюда остальные ключи, если их несколько

        // Открываем рецепты игроку
        javaPlayer.discoverRecipes(recipesToDiscover);



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
        }
        if(!db.playerNameActual(javaPlayer)){
            EPlayer player = db.getPlayer(javaPlayer.getUniqueId());
            player.setDisplayName(MiniMessage.miniMessage().serialize(javaPlayer.displayName()));
        }

        EPlayer player = db.getPlayer(javaPlayer);

        if(!player.getData().getWaitingTruce().isEmpty()){
            for(UUID id : player.getData().getWaitingTruce()){

                EPlayer peaceProposer = db.getPlayer(id);
                if(peaceProposer!=null){
                    javaPlayer.sendMessage(Tools.deserialize("<aqua>"+peaceProposer.getDisplayName() + " <white>предлагает заключить <green>мир!"));
                    Component message = Tools.deserialize( "<yellow>Нажмите чтобы подтвердить")
                            .clickEvent(ClickEvent.runCommand("/peace accept " + peaceProposer.getDisplayName()))
                            .hoverEvent(Tools.deserialize("Вы заключите <green>мир <white>с <aqua>" + peaceProposer.getDisplayName()));
                    javaPlayer.sendMessage(message);
                }
            }
        }

        if(!player.getData().getWaitingAlly().isEmpty()){
            for(UUID id : player.getData().getWaitingAlly()){

                EPlayer allyProposer = db.getPlayer(id);

                if(allyProposer!=null){
                    javaPlayer.sendMessage(Tools.deserialize("<aqua>"+allyProposer.getDisplayName() + " <white>предлагает заключить <green>союз!"));
                    Component message = Tools.deserialize( "<yellow>Нажмите чтобы подтвердить")
                            .clickEvent(ClickEvent.runCommand("/ally accept " + allyProposer.getDisplayName()))
                            .hoverEvent(Tools.deserialize("Вы заключите <green>союз <white>с <aqua>" + allyProposer.getDisplayName()));
                    javaPlayer.sendMessage(message);
                }
            }
        }



    }

    private void setCustomHexName(Player player, String hexColor) {
        // Создаем HEX цвет
        TextColor customColor = TextColor.color(Integer.parseInt(hexColor.replace("#", ""), 16));

        // Красим ник целиком для таба
        player.playerListName(Component.text(player.getName()).color(customColor));

        // По желанию красим и для чата
        player.displayName(Component.text(player.getName()).color(customColor));
    }

}
