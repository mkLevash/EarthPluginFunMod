package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Trade implements CommandExecutor, TabCompleter {
    private final Earth earth;
    ServerDatabase db;
    public Trade(Earth earth) {
        this.earth= earth;
        db = earth.getServerDatabase();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p){
            EPlayer player = db.getPlayer(p.getUniqueId());
            if (args.length != 1){
                //sender.sendMessage(ChatColor.GREEN + "Ваша эффективность торговли - " + c.getTradeMod()*100 + "%");
                sender.sendMessage(ChatColor.YELLOW + "Введите /trade <имя игрока>");
                return true;
            }
            EPlayer targetPlayer = db.getPlayer(db.getPlayerUuid(args[0]));
            if(targetPlayer ==null){
                p.sendMessage(ChatColor.YELLOW + "Игрок не найден!");
                p.sendMessage(ChatColor.YELLOW + "Убедитесь что верно ввели имя");
                return true;
            }
            String path = "trade."+player.getDisplayName()+"."+args[0];
            String tarPath = "trade."+args[0]+"."+player.getDisplayName();
            boolean bool = CustomConfig.get().getBoolean(path);
            CustomConfig.set(path,!bool);
            CustomConfig.set(tarPath,!bool);
            //double mod = c.getTaxMod()+(double)amount/100;
            //c.updateTaxMod(mod);
            //sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаша эффективность торговли был успешно увеличена на &e" + amount +"%, теперь она равна" + mod));
            return true;


        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> tab = new ArrayList<>();
        for(EPlayer player:db.getPlayers()){
            tab.add(player.getDisplayName());
        }
        return tab;
    }
}
