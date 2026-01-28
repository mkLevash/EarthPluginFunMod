//package earthrp.commands;
//
//import earthrp.Earth;
//import earthrp.customObjects.EPlayer;
//import earthrp.database.ServerDatabase;
//import org.bukkit.ChatColor;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.List;
//import java.util.UUID;
//
//public class ArmyCommand implements CommandExecutor, TabCompleter {
//    private final Earth earth;
//    ServerDatabase db;
//    public ArmyCommand(Earth earthPlugin) {
//        this.earth = earthPlugin;
//        this.db = earth.getServerDatabase();
//    }
//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
//        if (sender instanceof Player player){
//            UUID uuid = player.getUniqueId();
//            EPlayer p;
//            p = db.getPlayer(uuid);
//            EPlayer.ArmyStats a = p.getArmyStats();
//            EPlayer.Country c = p.getCountry();
//            if (args.length > 2 | args.length == 0){
//                sender.sendMessage(ChatColor.YELLOW + "Введите /army <stats> <amount>");
//            }else{
//                int amount = 0;
//                if (args.length == 2){
//                    try {
//                        amount = Integer.parseInt(args[1]);
//
//                    }catch (NumberFormatException e){
//                        sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
//                        return true;
//                    }
//                }
//                                switch (args[0]){
//
//
//
//                    case "expense_mod" -> {
//                        if (args.length == 1){
//                            sender.sendMessage(ChatColor.GREEN + "Ваш модификатор расходов на армию - " + (a.getExpenseMod()*100));
//                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /army expense_mod <amount>");
//                        }else {
//                            a.updateExpenseMod((double)amount/100);
//
//                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш одификатор расходов на армию теперь - &e" + amount));
//                        }
//
//                    }
//                    case "limit_mod" -> {
//                        if (args.length == 1){
//                            sender.sendMessage(ChatColor.GREEN + "Ваш модификатор расходов на армию - " + (a.getLimitMod()*100));
//                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /army expense_mod <amount>");
//                        }else {
//                            a.updateLimitMod((double)amount/100);
//
//                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш одификатор расходов на армию теперь - &e" + amount));
//                        }
//
//                    }
//                    case "max_manpower_mod" -> {
//                        if (args.length == 1){
//                            sender.sendMessage(ChatColor.GREEN + "Ваш модификатор максимума MP - " + (int) (a.getManpowerLimitMod()*100) + "%");
//                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /army max_manpower_mod <amount>");
//                        }else {
//                            a.updateManpowerLimitMod((double)amount/100);
//
//                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш модификатор максимума MP теперь - &e" + amount + "%"));
//                        }
//
//                    }
//                    case "manpower_increase_mod" -> {
//                        if (args.length == 1){
//                            sender.sendMessage(ChatColor.GREEN + "Ваш модификатор мобилизации - " + a.getManpowerIncMod());
//                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /army manpower_increase_mod <amount>");
//                        }else {
//                            a.updateManpowerIncMod(amount);
//                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш модификатор мобилизации теперь - &e" + amount));
//                        }
//
//                    }
//                    case "war_support" -> {
//                        if (args.length == 1){
//                            sender.sendMessage(ChatColor.GREEN + "Ваша поддержка войны - " + c.getWarSup());
//                            sender.sendMessage(ChatColor.YELLOW + "Для измененния введите /army war_support <amount>");
//                        }else {
//                            c.updateWarSup(amount);
//                            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aВаш поддержка войны теперь - &e" + amount));
//                        }
//
//                    }
//
//                }
//                return true;
//
//            }
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
//        if (args.length == 1){
//            return List.of(
//                    "expense_mod",
//                    "limit_mod",
//                    "manpower_increase_mod",
//                    "max_manpower_mod",
//                    "war_support"
//            );
//        }
//        if (args.length == 2){
//            return List.of(
//                    "<новое значение>"
//            );
//        }
//        return null;
//    }
//}
