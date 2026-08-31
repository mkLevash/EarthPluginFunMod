package earthrp.commands;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.configs.CustomConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TradeWay implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public TradeWay(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length < 4){
                sender.sendMessage(ChatColor.YELLOW + "Введите /tradeway <way type> <distance> <town> ");
                return true;}
            int distance;
            try {
                distance = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы верно ввели <distance>");
                return true;
            }
            Location loc = player.getLocation();
            int x = loc.getChunk().getX();
            int z = loc.getChunk().getZ();
            String world = loc.getWorld().getName();
            HashSet<Town> towns = db.getTowns();
            Town closestTown = towns.stream()
                    .filter(t -> t.getLocation().getWorld().equals(player.getLocation().getWorld()))
                    .min(Comparator.comparingDouble(t -> t.getLocation().distanceSquared(player.getLocation())))
                    .orElse(null);
            String townId = String.valueOf(closestTown.getUniqueId());
            if(townId==null)return false;

            String id = args[2];
            if (townId.equals(id)) return false;
            String name = args[3];
            String path = "trade.towns."+townId+"."+id+".";
            CustomConfig.set(path+"type",args[0]);
            CustomConfig.set(path+"distance",distance);
            CustomConfig.set(path+"status",true);

            Bukkit.broadcastMessage(Tools.colorText("&fВы проложили торговый путь до &a" + name));

        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(sender instanceof Player player){
            if(args.length ==3){
                List<Town> towns = db.getPlayer(player.getUniqueId()).getTradeTowns();
                List<String> res = new ArrayList<>();
                for(Town t:towns){
                    //if(t.isLandHub())res.add(t.getUniqueId() + " " + t.getName());
                }

                return res;
            }
            if(args.length == 1){
                return List.of(
                        "sea",
                        "land");
            }
            if(args.length == 2){
                return List.of(
                        "<way distance>");
            }

        }
        return null;

    }
}
