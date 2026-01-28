package earthrp.commands;

import earthrp.Earth;
import earthrp.database.dbTools;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
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
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Mset implements CommandExecutor, TabCompleter {

    private final Earth plugin;
    ServerDatabase db;
    public Mset(Earth plugin) {
        this.plugin = plugin;
        this.db = plugin.getServerDatabase();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        assert sender instanceof Player;
        if (args.length == 2){
            if (args[0].equals("mora")){
                if (args[1].equals("on")){
                    db.updateStatusMora("on");
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Мора запущена");
                    return true;
                }else if (args[1].equals("off")){
                    db.updateStatusMora("off");
                    Bukkit.broadcastMessage(ChatColor.YELLOW + "Мора выключена");
                    return true;
                }else{
                    sender.sendMessage(ChatColor.YELLOW + "/mset <statistic for change> <player> <amount>");
                    return true;
                }

            }else {
                sender.sendMessage(ChatColor.YELLOW + "/mset <statistic for change> <player> <amount>");
                return true;
            }
        }
        if (args.length != 3){
            sender.sendMessage(ChatColor.YELLOW + "/mset <statistic for change> <player> <amount>");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
            return true;
        }
        EPlayerAttribute stat = EPlayerAttribute.fromString(args[0]);
        if (stat == null) {
            sender.sendMessage(ChatColor.YELLOW + "модификатор не найден");
            return true;
        }

        String playerName = args[1];
        if(playerName.equals("@a")){
            for(EPlayer p:db.getPlayers()){
                p.setAttribute(stat,amount);
            }
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', args[0] + "&a для всех была успешно установлена на &e" + amount));
            return true;
        }
        EPlayer p = db.getPlayer(playerName);

        p.setAttribute(stat,amount);

        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', args[0] + " "+ playerName + "&a была успешно установлена на &e" + amount));

        return true;
    }
    private double getMod(int amount){
        return Tools.round((double) amount /100) + 1.0;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            List<String> attributes = new ArrayList<>();
            for(EPlayerAttribute a:EPlayerAttribute.values()){
                String input = args[0];
                String suggestion = dbTools.toCamelCase(a.name());
                if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                    // 4. Если совпало — отправляем красивый вариант (с заглавными)
                    attributes.add(suggestion);
                }

            }
            return attributes;
        }
        if (args.length == 2){

            return getStrings(args);
        }
        if (args.length == 3){
            return List.of(
                    "<новое значение для статистики>"
            );
        }

        return null;
    }

    private @NonNull List<String> getStrings(@NotNull String @NonNull [] args) {
        List<String> players = new ArrayList<>();
        if (args[1].startsWith("@") || args[1].isEmpty()) players.add("@a");
        if(args[1].startsWith("@")) return players;

        for(EPlayer p:db.getPlayers()){
            String input = args[1];
            String suggestion = p.getDisplayName();
            if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                // 4. Если совпало — отправляем красивый вариант (с заглавными)
                players.add(suggestion);
            }
        }
        return players;
    }
}
