package earthrp.commands;

import earthrp.Earth;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static java.lang.Integer.parseInt;

public class Roll implements CommandExecutor, TabCompleter {
    private final Earth earth;
    public Roll(Earth earth) {
        this.earth = earth;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p && args.length >= 2){
            int result = 0;
            int d;
            int mod = 0;
            int amount = 1;
            d = parseInt(args[1]);
            if (args.length >= 3){
                try {
                    mod = parseInt(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы верно ввели числа");
                    return true;
                }
            }
            if(args.length >= 4){
                try {
                    amount = parseInt(args[3]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы верно ввели числа");
                    return true;
                }
                for(int i = 0; i < amount; i++){
                    result += (int) (Math.random() * d) + 1;
                }
            }else{
                result = (int) (Math.random() * d) + 1;
            }
            if(args[0].equals("private")){
                p.sendMessage(ChatColor.GREEN + "Вам выпало число " + result + " + " + mod + " = " + (result+mod));
                return true;
            } else if (args[0].equals("public")) {
                Bukkit.broadcastMessage(p.getDisplayName() + " бросил " + amount + "d" + d);
                if(mod == 0){
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('~',"Ему выпало ~2" + result ));
                }else if(mod>0){
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('~',"Ему выпало число ~2" + result + " ~f+ ~2" + mod + " ~f= ~3" + (result+mod)));
                } else if(mod < 0){
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('~',"Ему выпало число ~2" + result + "~4" + mod + " ~f= ~3" + (result+mod)));
                }
                return true;
            }else{
                p.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы указали приватность броска");
                return true;
            }
        }else{
            sender.sendMessage(ChatColor.YELLOW + "/roll <dice> <modifier>");
            return true;
        }

    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            return List.of(
                    "private",
                    "public"
            );
        }
        if (args.length == 2){
            return List.of(
                    "<количество граней кубика>"
            );
        }
        if (args.length == 3){
            return List.of(
                    "<модификатор броска>"
            );
        }
        if (args.length == 4){
            return List.of(
                    "<Количество кубиков>"
            );
        }
        return null;
    }
}
