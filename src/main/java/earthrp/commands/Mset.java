package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.database.dbTools;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
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
import java.util.Locale;
import java.util.UUID;

public class Mset implements CommandExecutor, TabCompleter {

    private final Earth plugin;
    ServerDatabase db;
    public Mset(Earth plugin) {
        this.plugin = plugin;
        this.db = plugin.getDatabase();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
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
                    sender.sendMessage(ChatColor.YELLOW + "/mset <mora> <on>/<off>");
                    return true;
                }

            } else {
                sender.sendMessage(ChatColor.YELLOW + "/mset <statistic for change> <player> <amount>");
                return true;
            }
        }
        if(args.length == 4){
            EPlayer country = db.getPlayer(args[1]);
            if(country == null){
                if(sender instanceof Player player){
                    player.sendMessage("игрок не найден");
                }else{
                    Earth.getInstance().getLogger().info("игрок не найден");
                }
                return true;
            }

            EPlayer targetCountry = db.getPlayer(args[3]);
            if(targetCountry == null){
                if(sender instanceof Player player){
                    player.sendMessage("таргет игрок не найден");
                }else{
                    Earth.getInstance().getLogger().info("таргет игрок не найден");
                }
                return true;
            }
            switch (args[0]){
                case "peace" ->{
                    switch (args[2]){
                        case "declare" ->{
                            targetCountry.getData().getWaitingTruce().add(country.getUniqueId());
                            Player targetPlayer = Bukkit.getPlayer(args[3]);
                            if(targetPlayer!=null){
                                targetPlayer.sendMessage(Tools.deserialize("<aqua>"+country.getDisplayName() + " <white>предлагает заключить <green>мир!"));
                                Component message = Tools.deserialize("<yellow>Нажмите чтобы подтвердить")
                                        .clickEvent(ClickEvent.runCommand("/peace accept " + country.getDisplayName()))
                                        .hoverEvent(Tools.deserialize("Вы заключите <green>мир <white>с <aqua>" + country.getDisplayName()));
                                targetPlayer.sendMessage(message);
                            }
                            if(sender instanceof Player player){
                                player.sendMessage(Tools.deserialize(args[1]+" отправил предложение мира <aqua>"+args[3]));
                            }else{
                                Earth.getInstance().getLogger().info("/eset <peace> <player_name> <declare>/<accept> <player_name>");
                            }
                            return true;

                        }
                        case "accept" ->{
                            Bukkit.broadcast(Tools.deserialize("<aqua>" + country.getDisplayName() + "<white> заключил мир с <green>" + args[3]));

                            targetCountry.declareTruce(country);

                            for(Town t:country.getTowns()){
                                if(t.getController().equals(targetCountry)) country.controlTown(t);
                            }

                            for(Town t:targetCountry.getTowns()){
                                if(t.getController().equals(country)) targetCountry.controlTown(t);
                            }
                            country.getData().getWaitingTruce().remove(targetCountry.getUniqueId());

                        }
                        default -> {
                            if(sender instanceof Player player){
                                player.sendMessage("/eset <peace> <player_name> <declare>/<accept> <player_name>");
                            }else{
                                Earth.getInstance().getLogger().info("/eset <peace> <player_name> <declare>/<accept> <player_name>");
                            }
                            return true;
                        }
                    }
                }
                case "ally" ->{
                    switch (args[2]){
                        case "create" ->{
                            targetCountry.getData().getWaitingAlly().add(country.getUniqueId());
                            Player targetPlayer = Bukkit.getPlayer(args[3]);
                            if(targetPlayer!=null){
                                targetPlayer.sendMessage(Tools.deserialize("<aqua>"+country.getDisplayName() + " <white>предлагает заключить <green>союз!"));
                                Component message = Tools.deserialize("<yellow>Нажмите чтобы подтвердить")
                                        .clickEvent(ClickEvent.runCommand("/ally accept " + country.getDisplayName()))
                                        .hoverEvent(Tools.deserialize("Вы заключите <green>союз <white>с <aqua>" + country.getDisplayName()));
                                targetPlayer.sendMessage(message);
                            }

                        }
                        case "accept" ->{
                            if(country.getData().getWaitingAlly().contains(targetCountry.getUniqueId())){
                                country.getData().getAlly().add(targetCountry.getUniqueId());
                                country.getData().getWaitingAlly().remove(targetCountry.getUniqueId());
                                targetCountry.getData().getAlly().add(country.getUniqueId());
                                Bukkit.broadcast(Tools.deserialize("<aqua>" + country.getDisplayName() + " <white>и <aqua>" + args[3] + " <white>заключили <green>союз!"));
                            }else{
                                if(sender instanceof Player player){
                                    player.sendMessage(args[3]+" не отправлял союз" + args[1]);
                                }else{
                                    Earth.getInstance().getLogger().info(args[3]+" не отправлял союз" + args[1]);
                                }
                                return true;
                            }

                        }
                        case "break" ->{
                            if(country.getData().getAlly().contains(targetCountry.getUniqueId())){
                                country.breakAlly(targetCountry);
                                Bukkit.broadcast(Tools.deserialize("<aqua>" + country.getDisplayName() + " <red>разорвал союз <white>с <aqua>" + args[3] + "<white>!"));
                            }else{
                                if(sender instanceof Player player){
                                    player.sendMessage(args[3]+" не союзники" + args[1]);
                                }else{
                                    Earth.getInstance().getLogger().info(args[3]+" не союзники" + args[1]);
                                }
                                return true;
                            }

                        }
                        default -> {
                            if(sender instanceof Player player){
                                player.sendMessage("/eset <ally> <player_name> <declare>/<accept> <player_name>");
                            }else{
                                Earth.getInstance().getLogger().info("/eset <ally> <player_name> <declare>/<accept> <player_name>");
                            }
                            return true;
                        }
                    }

                }
                default -> {
                    if(sender instanceof Player player){
                        player.sendMessage("/eset <peace>/<ally> <player_name> <declare>/<accept>/<break> <player_name>");
                    }else{
                        Earth.getInstance().getLogger().info("/eset <peace>/<ally> <player_name> <declare>/<accept>/<break> <player_name>");
                    }
                    return true;
                }
            }
        }
        if (args.length != 3){
            sender.sendMessage(ChatColor.YELLOW + "/mset <statistic for change> <player> <amount>");
            return true;
        }

        if(args[0].equals("war")){
            EPlayer country = db.getPlayer(args[1]);
            if(country == null){
                if(sender instanceof Player player){
                    player.sendMessage("игрок не найден");
                }else{
                    Earth.getInstance().getLogger().info("игрок не найден");
                }
                return true;
            }

            EPlayer targetCountry = db.getPlayer(args[2]);
            if(targetCountry == null){
                if(sender instanceof Player player){
                    player.sendMessage("таргет игрок не найден");
                }else{
                    Earth.getInstance().getLogger().info("таргет игрок не найден");
                }
                return true;
            }
            UUID targetId = targetCountry.getUniqueId();
            if(country.getData().getTruceMap().containsKey(targetId)){
                Bukkit.broadcastMessage(Tools.colorText("&4" + country.getDisplayName() + "&e объявил вону &a" + args[2]));
                country.getData().getEnemies().add(targetId);
                targetCountry.getData().getEnemies().add(country.getUniqueId());
                country.truceBreak(targetCountry);
            }else{
                Bukkit.broadcastMessage(Tools.colorText("&4" + country.getDisplayName() + "&e объявил вону &a" + args[2]));
                country.getData().getEnemies().add(targetId);
                targetCountry.getData().getEnemies().add(country.getUniqueId());
            }
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
            String input = args[0];
            if("war".startsWith(input.toLowerCase(Locale.ROOT))){
                attributes.addFirst("war");
            }
            if("peace".startsWith(input.toLowerCase(Locale.ROOT))){
                attributes.addFirst("peace");
            }
            if("ally".startsWith(input.toLowerCase(Locale.ROOT))){
                attributes.addFirst("ally");
            }
            if("mora".startsWith(input.toLowerCase(Locale.ROOT))){
                attributes.addFirst("mora");
            }
            return attributes;
        }
        if (args.length == 2){

            return getStrings(args);
        }
        if (args.length == 3){
            switch (args[0]){

                case "war" ->{
                    List<String> players = new ArrayList<>();
                    for(EPlayer p:db.getPlayers()){
                        String input = args[1];
                        String suggestion = p.getDisplayName();
                        if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                            players.add(suggestion);
                        }
                    }
                    return players;
                }
                case "ally" -> {
                    return List.of(
                            "create","break","accept"
                    );

                }
                case "peace" ->{
                    return List.of(
                            "declare","accept"
                    );

                }
                default -> {
                    return List.of(
                            "<новое значение для статистики>"
                    );
                }
            }
        }
        if(args.length == 4){
            List<String> players = new ArrayList<>();
            switch (args[0]){
                case "ally", "peace" -> {
                    for(EPlayer p:db.getPlayers()){
                        String input = args[3];
                        String suggestion = p.getDisplayName();
                        if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                            players.add(suggestion);
                        }
                    }
                }
            }
            return players;
        }
        return null;
    }

    private @NonNull List<String> getStrings(@NotNull String @NonNull [] args) {
        List<String> players = new ArrayList<>();

        if (args[0].equals("mora")) {
            players.add("on");
            players.add("off");
            return players;
        }



        if(args[1].isEmpty()){
            players.add("@a");
        }
        if (args[1].startsWith("@")) {
            players.add("@a");
            return players;
        }

        for(EPlayer p:db.getPlayers()){
            String input = args[1];
            String suggestion = p.getDisplayName();
            if (suggestion.toLowerCase().startsWith(input.toLowerCase())) {
                players.add(suggestion);
            }
        }
        return players;
    }
}
