package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Color implements CommandExecutor, TabCompleter {

    ServerDatabase db;
    public Color() {
        db = Earth.getInstance().getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            if (args.length != 3){
                sender.sendMessage(ChatColor.YELLOW + "Введите /color <red> <green> <blue>");
                return true;}
            int red;
            int green;
            int blue;
            try {
                red = Integer.parseInt(args[0]);
                green = Integer.parseInt(args[1]);
                blue = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы верно ввели числа");
                return true;
            }

            EPlayer p = db.getPlayer(player.getUniqueId());

            p.getData().getRgb().put("red",red);
            p.getData().getRgb().put("green",green);
            p.getData().getRgb().put("blue",blue);
            Earth.getInstance().getBlueMapManager().refreshAllTowns();

            return true;

        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {

        if(strings.length == 1){
            return Collections.singletonList("red");
        }
        if(strings.length == 2){
            return Collections.singletonList("green");
        }
        if(strings.length == 3){
            return Collections.singletonList("blue");
        }
        return null;

    }




}
