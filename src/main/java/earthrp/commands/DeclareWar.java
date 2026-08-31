package earthrp.commands;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class DeclareWar implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public DeclareWar(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length == 0){
                sender.sendMessage(ChatColor.YELLOW + "Введите /war <player_name>");
                return true;}


            EPlayer p = db.getPlayer(player.getUniqueId());
            String targetName = args[0];
            EPlayer targetCountry = db.getPlayer(targetName);
            if(targetCountry == null){
                player.sendMessage("игрок не найден");
                return true;
            }
            UUID targetId = targetCountry.getUniqueId();
            if(args.length == 1){
                if(p.getData().getAlly().contains(targetId)){
                    player.sendMessage("Вы не можете объявить войну союзнику!");
                    player.sendMessage("Используйте /ally break");
                }else if(p.getData().getTruceMap().containsKey(targetId)) {
                    player.sendMessage("У вас мир с этой страной!");
                    player.sendMessage("Чтобы подтвердить нарушение мира введите /war <player_name> accept");
                }else{
                    Bukkit.broadcastMessage(Tools.colorText("&4" + p.getDisplayName() + "&e объявил вону &a" + args[0]));
                    p.getData().getEnemies().add(targetId);
                    targetCountry.getData().getEnemies().add(p.getUniqueId());

                }
            } else if (args.length == 2 && args[1].equals("accept")) {



                if(!p.getData().getAlly().contains(targetId)){

                    if(p.getData().getTruceMap().containsKey(targetId)){
                        if(p.isImperialism()){
                            if(p.getAttribute(EPlayerAttribute.POLIT_BALANCE) < 3){
                                player.sendMessage("У вас недостаточно ПП чтобы разорвать мир");
                            }else {
                                Bukkit.broadcastMessage(Tools.colorText("&4" + p.getDisplayName() + "&e объявил вону &a" + args[0]));
                                p.getData().getEnemies().add(targetId);
                                targetCountry.getData().getEnemies().add(p.getUniqueId());
                                p.truceBreak(targetCountry);
                            }
                        }else{
                            if(p.getAttribute(EPlayerAttribute.STABILITY) < 0){
                                player.sendMessage("Вы не можете объявить войну при отрицательной стабильности");
                            }else {
                                Bukkit.broadcastMessage(Tools.colorText("&4" + p.getDisplayName() + "&e объявил вону &a" + args[0]));
                                p.getData().getEnemies().add(targetId);
                                targetCountry.getData().getEnemies().add(p.getUniqueId());
                                p.truceBreak(targetCountry);

                            }

                        }
                    }else{
                        Bukkit.broadcastMessage(Tools.colorText("&4" + p.getDisplayName() + "&e объявил вону &a" + args[0]));
                        p.getData().getEnemies().add(targetId);
                        targetCountry.getData().getEnemies().add(p.getUniqueId());
                    }

                }else{
                    player.sendMessage("Вы не можете объявить войну союзнику!");
                    player.sendMessage("Используйте /ally break");
                }

            }


        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        List<String> players = new ArrayList<>();
        if(commandSender instanceof Player player){
            for(EPlayer ep : db.getPlayers()){
                if(!ep.getUniqueId().equals(player.getUniqueId())){
                    players.add(ep.getDisplayName());
                }

            }
        }
        return players;
    }
}
