package earthrp.commands;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class DeclareWar implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public DeclareWar(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getServerDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length == 0){
                sender.sendMessage(ChatColor.YELLOW + "Введите /war <player_name>");
                return true;}
            EPlayer p = db.getPlayer(player.getUniqueId());
            String targetPlayer = args[0];
            EPlayer targetCountry = db.getPlayer(db.getPlayerUuid(targetPlayer));
            targetCountry.setAttribute(EPlayerAttribute.WAR_STATUS,1);
            p.setAttribute(EPlayerAttribute.WAR_STATUS,1);
            Bukkit.broadcastMessage(Tools.colorText("&4" + p.getDisplayName() + "&e объявил вону &a" + args[0]));
            String path = "war."+p.getDisplayName()+"."+targetPlayer;
            String tarPath = "war."+targetPlayer+"."+p.getDisplayName();
            CustomConfig.set(path,true);
            CustomConfig.set(tarPath,true);
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        Player[] OnlinePlayers = Bukkit.getServer().getOnlinePlayers().toArray(new Player[0]);
        String[] Players;
        Players = new String[OnlinePlayers.length];
        for (int i = 0; i < OnlinePlayers.length; i++){
            Players[i] = OnlinePlayers[i].getDisplayName();
        }

        return Arrays.asList(Players);
    }
}
