package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AllyCommand implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public AllyCommand(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String @NonNull [] args) {
        if (sender instanceof Player player){
            if (args.length != 2){
                sender.sendMessage(ChatColor.YELLOW + "Введите /ally <create>/<break>/<accept> <player_name>");
                return true;}
            EPlayer p = db.getPlayer(player.getUniqueId());
            String targetName = args[1];
            EPlayer targetCountry = db.getPlayer(db.getPlayerUuid(targetName));
            if(targetCountry == null){
                player.sendMessage("игрок не найден");
                return true;
            }
            switch (args[0]){
                case "create" -> {
                    targetCountry.getData().getWaitingAlly().add(p.getUniqueId());
                    player.sendMessage(Tools.deserialize("Вы отправили предложение союза <aqua>"+targetName));
                    Player targetPlayer = Bukkit.getPlayer(targetName);
                    if(targetPlayer!=null){
                        targetPlayer.sendMessage(Tools.deserialize("<aqua>"+p.getDisplayName() + " <white>предлагает заключить <green>союз!"));
                        Component message = Tools.deserialize("<yellow>Нажмите чтобы подтвердить")
                                .clickEvent(ClickEvent.runCommand("/ally accept " + p.getDisplayName()))
                                .hoverEvent(Tools.deserialize("Вы заключите <green>союз <white>с <aqua>" + p.getDisplayName()));
                        targetPlayer.sendMessage(message);
                    }

                }
                case "break" -> {
                    if(p.getData().getAlly().contains(targetCountry.getUniqueId())){
                        p.breakAlly(targetCountry);
                        Bukkit.broadcast(Tools.deserialize("<aqua>" + p.getDisplayName() + " <red>разорвал союз <white>с <aqua>" + targetName + "<white>!"));
                    }else{
                        player.sendMessage(targetName +" не ваш союзник");
                    }

                }

                case "accept" -> {
                    if(p.getData().getWaitingAlly().contains(targetCountry.getUniqueId())){
                        p.getData().getAlly().add(targetCountry.getUniqueId());
                        p.getData().getWaitingAlly().remove(targetCountry.getUniqueId());
                        targetCountry.getData().getAlly().add(p.getUniqueId());
                        Bukkit.broadcast(Tools.deserialize("<aqua>" + p.getDisplayName() + " <white>и <aqua>" + targetName + " <white>заключили <green>союз!"));
                    }else{
                        player.sendMessage(targetName +" не отправлял вам союз");
                    }
                }
                default -> {
                    player.sendMessage(ChatColor.YELLOW + "Введите /ally <create>/<break>/<accept> <player_name>");
                }

            }
            return true;


        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NonNull [] args) {

        if(args.length == 1){
            return List.of("create","break","accept");
        }
        if(args.length == 2 && commandSender instanceof Player javaPlayer){
            List<String> names = new ArrayList<>();
            EPlayer country = db.getPlayer(javaPlayer);
            switch (args[0]){
                case "create" ->{

                    for(EPlayer p:db.getPlayers()){
                        if(!p.getData().getEnemies().contains(javaPlayer.getUniqueId()) && !p.getData().getAlly().contains(javaPlayer.getUniqueId())){
                            names.add(p.getDisplayName());
                        }
                    }

                }
                case "break" ->{
                    for(EPlayer p:db.getPlayers()){
                        if(p.getData().getAlly().contains(javaPlayer.getUniqueId())){
                            names.add(p.getDisplayName());
                        }
                    }

                }
                case "accept" -> {
                    for(UUID id : country.getData().getWaitingAlly()){
                        names.add(db.getPlayer(id).getDisplayName());
                    }
                }
            }
            return names;
        }
        return null;
    }
}
