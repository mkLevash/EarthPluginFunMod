package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.AdminMenu;
import earthrp.tools.maps.RegionMap;
import earthrp.tools.maps.*;
import org.bukkit.Bukkit;
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
import java.util.concurrent.CompletableFuture;

public class EarthCommand implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length >= 1){
            switch (args[0]){
                case "reload" ->{
                    Earth.getInstance().getLoadingManager().reload();
                    Bukkit.broadcastMessage("Earth Plugin was reloaded");
                }
                case "panel" -> {
                    if(sender instanceof Player player){
                        MenuUtility pmu = new MenuUtility(player);
                        new AdminMenu(pmu).open();
                    }
                }
                case "allRender" ->{
                    if(sender instanceof Player player){
                        WorldScanner worldScanner = new WorldScanner(player.getWorld().getWorldFolder());

                        CompletableFuture<RegionMap> future = worldScanner.scanWorldAsync();

                        future.thenAccept(map -> {
                            Earth.getInstance().setRegionMap(map);
                            Bukkit.broadcastMessage("render is over");
                        }).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        });


                    }
                }
                case "render" -> {
                    if(sender instanceof Player player && args.length == 2){
                        int amount;
                        try {
                            amount = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы верно ввели <radius>");
                            return true;
                        }
                        WorldScanner worldScanner = new WorldScanner(player.getWorld().getWorldFolder());
                        int chunkX = player.getLocation().getChunk().getX();
                        int chunkZ = player.getLocation().getChunk().getZ();
                        CompletableFuture<RegionMap> future = worldScanner.scanWorldAsyncInRadius(chunkX,chunkZ,amount);

                        future.thenAccept(map -> {
                            Earth.getInstance().setRegionMap(map);
                            Bukkit.broadcastMessage("render is over");
                        }).exceptionally(ex -> {
                            ex.printStackTrace();
                            return null;
                        });


                    }
                }
                case "chunk" ->{
                    if(sender instanceof Player player && args.length == 2){
                        ServerDatabase db = Earth.getInstance().getDatabase();
                        if(args[1].equals("remove")){
                            Town town = db.getTownAtChunk(player.getLocation());
                            if(town!=null){
                                int x = player.getLocation().getChunk().getX();
                                int z = player.getLocation().getChunk().getZ();
                                town.getData().getChunk().remove(new CityBoundaryCalculator.chunkPoint(x,z));
                                return true;
                            }else{

                            }
                        }
                    }else return false;

                }
            }

            return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length==1){
            return List.of(
                    "reload"
            );
        }

        if(sender instanceof Player player && args.length > 0){
            switch (args[0]){
                case "chunk" ->{
                    if(args.length == 2){
                        ServerDatabase db = Earth.getInstance().getDatabase();
                        Town town = db.getTownAtChunk(player.getLocation());
                        if(town!=null){
                            return List.of("remove");
                        }
                        List<String> towns = new ArrayList<>();
                        for(Town t:db.getTowns()){
                            towns.add(t.getUniqueId().toString());
                        }
                        return towns;
                    }
                }
                case "render" ->{
                    if(args.length == 2){
                        return List.of("<chunk_radius>");
                    }
                }
            }
        }



        return List.of();
    }
}
