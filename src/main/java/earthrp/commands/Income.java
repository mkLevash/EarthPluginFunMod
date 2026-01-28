package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Income implements CommandExecutor {
    private final Earth earth;
    ServerDatabase db;
    public Income(Earth earth) {
        this.earth = earth;
        db = earth.getServerDatabase();
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        EPlayer p;
        if (sender instanceof Player player){
            p = db.getPlayer(player.getUniqueId());
        }else{
            return false;
        }
        if (args.length != 1 && args.length != 0){
            sender.sendMessage(ChatColor.YELLOW + "Введите /income <amount>");
            return true;
        }else if (args.length == 0){
            sender.sendMessage(ChatColor.GREEN + "Ваш базовый доход - " + p.getAttribute(EPlayerAttribute.INCOME));
            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /income <amount>");
            return true;
        }
        int amount;
        try {
            amount = Integer.parseInt(args[0]);

        }catch (NumberFormatException e){
            sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
            return true;
        }
        p.setAttribute(EPlayerAttribute.INCOME,amount);
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш доход был успешно изменена на &e" + amount));

        return true;
    }
}
