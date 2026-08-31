package earthrp.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import earthrp.Earth;
import earthrp.database.dbTools;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.MainMenu;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import io.papermc.paper.command.brigadier.Commands;
import org.bukkit.ChatColor;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import com.mojang.brigadier.Command;
import org.bukkit.entity.Player;

//Set time in milliseconds

public class EGet implements PaperCommand{
    ServerDatabase db;
    public EGet(Earth instance) {
        db = instance.getDatabase();
    }
    @Override
    public void register(Commands registrar){
        registrar.register(
                Commands.literal("eget")
                        .requires(stack -> stack.getSender().hasPermission("myplugin.check"))
                        .then(Commands.argument("statId", StringArgumentType.word())
                                .suggests((context, builder) -> {
                                    for(EPlayerAttribute a:EPlayerAttribute.values()){
                                        String input = builder.getRemaining();
                                        String suggestion = dbTools.toCamelCase(a.name());
                                        if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                                            // 4. Если совпало — отправляем красивый вариант (с заглавными)
                                            builder.suggest(suggestion);
                                        }
                                    }

                                    return builder.buildFuture();
                                })
                                .then(Commands.argument("playerNick", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for(EPlayer p:db.getPlayers()){
                                                builder.suggest(p.getDisplayName());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(this::execute)
                                )
                        )
                        .build()
        );

    }
    private int execute(CommandContext<CommandSourceStack> ctx) {
        String a = StringArgumentType.getString(ctx, "statId");
        String b = StringArgumentType.getString(ctx, "playerNick");

        EPlayer p = db.getPlayer(b);

        if(a.equals("doomStick") && ctx.getSource().getSender() instanceof Player player){
            player.getInventory().addItem(Tools.doomStick());
            return Command.SINGLE_SUCCESS;
        }
        if(p==null){
            ctx.getSource().getSender().sendMessage(ChatColor.YELLOW + "игрок не найден");
            return Command.SINGLE_SUCCESS;
        }

        if(a.equals("menu") && ctx.getSource().getSender() instanceof Player player){
            MenuUtility mu = new MenuUtility(player);
            mu.setPlayer(Earth.getInstance().getDatabase().getPlayer(p.getUniqueId()));
            new MainMenu(mu).open();
            return Command.SINGLE_SUCCESS;
        }

        EPlayerAttribute stat = EPlayerAttribute.fromString(a);
        if (stat == null) {
            ctx.getSource().getSender().sendMessage(ChatColor.YELLOW + "модификатор не найден");
            return Command.SINGLE_SUCCESS;
        }

        double value = p.getAttribute(stat);
        // Ваша логика (например, обращение к вашему кэшу)
        ctx.getSource().getSender().sendMessage(Tools.colorText("&2"+a + "&e "+ b + " = "+ value));

        return Command.SINGLE_SUCCESS;
    }
}
//
//
//public class Mget implements CommandExecutor, TabCompleter {
//
//    private final Earth plugin;
//    ServerDatabase db;
//    public Mget(Earth plugin) {
//        this.plugin = plugin;
//        db = plugin.getServerDatabase();
//    }
//
//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
//        EPlayer p;
//        if(args[0].equals("doomStick")&& sender instanceof Player player){
//            player.getInventory().addItem(Tools.doomStick());
//
//        }
//        if (args.length != 2){
//            sender.sendMessage(ChatColor.YELLOW + "/mget <statistic for check> <player>");
//            return true;
//        }
//        String playerName = args[1];
//        p = db.getPlayer(playerName);
//        EPlayerAttribute stat = EPlayerAttribute.fromString(args[0]);
//        if (stat == null) {
//            sender.sendMessage(ChatColor.YELLOW + "модификатор не найден");
//            return true;
//        }
//        double value = p.getAttribute(stat);
//        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&2"+args[0] + "&e "+ playerName + " = "+ value));
//        return true;
//
//
//
//
//
//    }
//    @Override
//    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
//        if (args.length == 1){
//            List<String> attributes = new ArrayList<>();
//            for(EPlayerAttribute a:EPlayerAttribute.values()){
//                attributes.add(dbTools.toCamelCase(a.name()));
//            }
//            return attributes;
//        }
//        if (args.length == 2){
//            List<String> players = new ArrayList<>();
//            for(EPlayer p:db.getPlayers()){
//                players.add(p.getDisplayName());
//            }
//
//            return players;
//        }
//
//        return null;
//    }
//
//
//}
