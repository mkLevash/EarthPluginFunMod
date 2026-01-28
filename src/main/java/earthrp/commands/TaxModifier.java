package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TaxModifier implements CommandExecutor {
    private final Earth plugin;
    ServerDatabase db;
    public TaxModifier(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getServerDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            EPlayer p = db.getPlayer(player.getUniqueId());
            if (args.length != 1){
                //sender.sendMessage(ChatColor.GREEN + "Ваш модификатор налогов - " + c.getTaxMod()*100);
                sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /tax_mod <amount>");
                return true;
            }
            int amount;
            try {
                amount = Integer.parseInt(args[0]);

            }catch (NumberFormatException e){
                sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
                return true;
            }
            //sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш модификатор дохода от налогов был успешно увеличен на &e" + amount +"%, теперь он равен" + mod));
            return true;


        }
        return false;
    }
}
