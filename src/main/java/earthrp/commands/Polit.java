package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Polit implements CommandExecutor, TabCompleter {
    private final Earth earth;
    ServerDatabase db;
    public Polit(Earth earth) {
        this.earth = earth;
        db = earth.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            EPlayer p = db.getPlayer(player.getUniqueId());
            if (args.length != 1){
                sender.sendMessage(ChatColor.YELLOW + "/polit <amount>");
            }else{
                int amount = 0;
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
                    return true;
                }
                double balance = p.getAttribute(EPlayerAttribute.POLIT_BALANCE);
                if (balance >= amount){
                    p.addAttribute(EPlayerAttribute.POLIT_BALANCE, -amount);
                    List<String> lore = List.of("Потраченная политка",String.valueOf(earth.getDatabase().getStatusDay()));
                    player.getInventory().addItem(Tools.createItem(Material.ICE,"<aqua>Политическая власть",lore,"politPower"));
                    sender.sendMessage(ChatColor.GREEN + "Вы выдали себе " + amount + "£");
                }else{
                    sender.sendMessage(ChatColor.YELLOW + "У вас недостаточно Политической Власти!");
                }

            }
            return true;

        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            return List.of(
                    "max",
                    "max_mod",
                    "income",
                    "income_mod",
                    "give"
            );
        }
        if (args.length == 2){
            return List.of(
                    "<значение>"
            );
        }
        return null;
    }
}
