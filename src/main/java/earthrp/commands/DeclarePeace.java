package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.*;

public class DeclarePeace implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public DeclarePeace(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NonNull [] args) {
        if (sender instanceof Player player){
            if (args.length == 0){
                sender.sendMessage(ChatColor.YELLOW + "Введите /peace <player_name>");
                return true;}


            if(args.length == 1){
                EPlayer p = db.getPlayer(player);
                String targetName = args[0];
                EPlayer targetCountry = db.getPlayer(db.getPlayerUuid(targetName));
                if(targetCountry == null){
                    player.sendMessage("игрок не найден");
                    return true;
                }
                targetCountry.getData().getWaitingTruce().add(p.getUniqueId());
                player.sendMessage(Tools.deserialize("Вы отправили предложение мира <aqua>"+targetName));
                Player targetPlayer = Bukkit.getPlayer(targetName);
                if(targetPlayer!=null){
                    targetPlayer.sendMessage(Tools.deserialize("<aqua>"+p.getDisplayName() + " <white>предлагает заключить <green>мир!"));
                    Component message = Tools.deserialize("<yellow>Нажмите чтобы подтвердить")
                            .clickEvent(ClickEvent.runCommand("/peace accept " + p.getDisplayName()))
                            .hoverEvent(Tools.deserialize("Вы заключите <green>мир <white>с <aqua>" + p.getDisplayName()));
                    targetPlayer.sendMessage(message);
                }

            } else if (args.length == 2 && args[0].equals("accept")) {
                EPlayer p = db.getPlayer(player);
                String targetName = args[1];
                EPlayer targetCountry = db.getPlayer(db.getPlayerUuid(targetName));
                if(targetCountry == null){
                    player.sendMessage("игрок не найден");
                    return true;
                }
                if(p.getData().getWaitingTruce().contains(targetCountry.getUniqueId())){
                    Bukkit.broadcast(Tools.deserialize("<aqua>" + p.getDisplayName() + "<white> заключил мир с <green>" + targetName));

                    targetCountry.declareTruce(p);

                    for(Town t:p.getTowns()){
                        if(t.getController().equals(targetCountry)) p.controlTown(t);
                    }

                    for(Town t:targetCountry.getTowns()){
                        if(t.getController().equals(p)) targetCountry.controlTown(t);
                    }
                    p.getData().getWaitingTruce().remove(targetCountry.getUniqueId());
                }else{
                    player.sendMessage("Вам не приходил мир от этого игрока");
                    return true;
                }


            }


            return true;

        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NonNull [] args) {

        if(commandSender instanceof Player javaPlayer){
            EPlayer country = db.getPlayer(javaPlayer);
            if(args.length == 1){
                Set<EPlayer> players = Earth.getInstance().getDatabase().getPlayers();
                List<String> names = new ArrayList<>();
                for(EPlayer p:players){
                    if(p.getData().getEnemies().contains(javaPlayer.getUniqueId())){
                        names.add(p.getDisplayName());
                    }
                }

                if(!country.getData().getWaitingTruce().isEmpty()){
                    names.addFirst("accept");
                }
                return names;
            } else if (args.length == 2 && args[0].equals("accept")) {
                List<String> names = new ArrayList<>();
                for(UUID id : country.getData().getWaitingTruce()){
                    names.add(db.getPlayer(id).getDisplayName());
                }
                return names;

            }

        }
        return null;
    }


}
