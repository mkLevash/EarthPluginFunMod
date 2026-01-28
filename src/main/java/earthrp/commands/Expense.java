package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Expense implements CommandExecutor {
    private final Earth moraPlugin;

    public Expense(Earth moraPlugin) {
        this.moraPlugin = moraPlugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        EPlayer p;
        if(sender instanceof Player player){
            p = moraPlugin.getServerDatabase().getPlayer(player.getUniqueId());
        }else{
            return false;
        }
        if (args.length != 1 && args.length != 0){
            sender.sendMessage(ChatColor.YELLOW + "Введите /expense <amount>");
            return true;
        }else if (args.length == 0){
            sender.sendMessage(ChatColor.GREEN + "Ваш базовый расход - " + p.getAttribute(EPlayerAttribute.EXPENSE) + "%");
            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /expense <amount>");
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[0]);

        }catch (NumberFormatException e){
            sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
            return true;
        }
        p.setAttribute(EPlayerAttribute.EXPENSE,amount);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш базовый расход был успешно изменена на &e" + amount));

        return true;
    }
}
