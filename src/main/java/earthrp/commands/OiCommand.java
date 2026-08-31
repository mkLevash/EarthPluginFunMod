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

import java.util.List;

public class OiCommand implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public OiCommand(Earth plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            EPlayer p;
            p = db.getPlayer(player.getUniqueId());
            if(args.length != 2){
                sender.sendMessage(ChatColor.YELLOW + "У вас сейчас " +  "ОИ");
                sender.sendMessage(ChatColor.YELLOW + "Введите /oi income <amount> для изменения");
                return true;

            } else{
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);

                }catch (NumberFormatException e){
                    sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
                    return true;
                }

                sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш прирост ОИ был успешно изменена на &e" + amount));
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            return List.of(
                    "income"
            );
        }
        if (args.length == 2) {
            return List.of(
                    "<новое значение>"
            );

        }
        return null;

    }
}
