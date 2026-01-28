package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Polit implements CommandExecutor, TabCompleter {
    private final Earth earth;
    ServerDatabase db;
    public Polit(Earth earth) {
        this.earth = earth;
        db = earth.getServerDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            EPlayer p = db.getPlayer(player.getUniqueId());
            if (args.length > 2 | args.length == 0){
                sender.sendMessage(ChatColor.YELLOW + "/polit <stat> <amount>");
            }else{
                int amount = 0;
                if (args.length == 2){
                    try {
                        amount = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
                        return true;
                    }
                }
                switch (args[0]){
                    case "max" -> {
                        if (args.length == 1){
                            sender.sendMessage(ChatColor.GREEN + "Ваш максимум полит власти - ");
                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /polit max <amount>");
                            return true;
                        }else {
                            //c.setPolitMax(amount);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш максимум полит власти теперь - &e" + amount));
                        }
                    }
                    case "max_mod" -> {
                        if (args.length == 1){
                            sender.sendMessage(ChatColor.GREEN + "Ваш модификатор максимума полит власти - ");
                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /polit max_mod <amount>");
                        }else {
                            //c.setPolitMaxMod(amount);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш модификатор максимума полит власти теперь - &e" + amount));
                        }
                    }
                    case "income" -> {
                        if (args.length == 1){
                            sender.sendMessage(ChatColor.GREEN + "Ваш прирост полит власти - ");
                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /polit income <amount>");
                        }else {
                            //c.setPolitIncome(amount);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш прирост полит власти теперь - &e" + amount));
                        }

                    }
                    case "income_mod" -> {
                        if (args.length == 1){
                            sender.sendMessage(ChatColor.GREEN + "Ваш модификатор прироста полит власти - ");
                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /polit income_mod <amount>");
                        }else {
                            //c.setPolitIncomeMod(amount);
                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш модификатор прироста полит власти теперь - &e" + amount));
                        }

                    }
                    case "give" -> {
                        if (args.length == 1){
                            sender.sendMessage(ChatColor.GREEN + "Ваш баланс полит власти - ");
                            sender.sendMessage(ChatColor.YELLOW + "Для выдачи введите /polit give <amount>");
                        }else {
                            double balance = p.getAttribute(EPlayerAttribute.POLIT_BALANCE);
                            if (balance >= amount){
                                p.addAttribute(EPlayerAttribute.POLIT_BALANCE, -amount);
                                ItemStack polit = new ItemStack(Material.HEART_OF_THE_SEA, amount);
                                ItemMeta politMeta = polit.getItemMeta();
                                politMeta.setDisplayName(ChatColor.BLUE + "Политическая власть");
                                politMeta.setLore(Collections.singletonList("Потраченная политка"));
                                polit.setItemMeta(politMeta);
                                player.getInventory().addItem(polit);
                                sender.sendMessage(ChatColor.GREEN + "Вы выдали себе " + amount + "£");
                            }else{
                                sender.sendMessage(ChatColor.YELLOW + "У вас недостаточно Политической Власти!");
                                return true;
                            }
                        }
                    }
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
