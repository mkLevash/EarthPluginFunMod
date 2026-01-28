//package earthrp.commands;
//
//
//import earthrp.Earth;
//import earthrp.database.ServerDatabase;
//import org.bukkit.Bukkit;
//import org.bukkit.block.Biome;
//import org.bukkit.command.Command;
//import org.bukkit.command.CommandExecutor;
//import org.bukkit.command.CommandSender;
//import org.bukkit.command.TabCompleter;
//import org.bukkit.entity.Player;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//import java.util.Arrays;
//import java.util.List;
//
//public class Chunk implements CommandExecutor, TabCompleter {
//    private final Earth plugin;
//    ServerDatabase db;
//    public Chunk(Earth plugin) {
//        this.plugin = plugin;
//        db = plugin.getServerDatabase();
//    }
//    @Override
//    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
//        List<Biome> waterBiomes = List.of(
//                Biome.OCEAN,
//                Biome.COLD_OCEAN,
//                Biome.DEEP_OCEAN,
//                Biome.FROZEN_OCEAN,
//                Biome.WARM_OCEAN,
//                Biome.LUKEWARM_OCEAN,
//                Biome.DEEP_COLD_OCEAN,
//                Biome.DEEP_FROZEN_OCEAN,
//                Biome.DEEP_LUKEWARM_OCEAN,
//                Biome.RIVER,
//                Biome.FROZEN_RIVER
//        );
//
//        if (sender instanceof Player player){
//            org.bukkit.Chunk chunk = player.getLocation().getChunk();
//            String msg = "False";
//            for(Biome b:waterBiomes){
//                if (chunk.contains(b)){
//                    msg = "True";
//                    break;
//                }
//            }
//            DynmapBorders.drawTownBorder(player.getLocation(), Integer.parseInt(args[0]));
//            Bukkit.broadcastMessage(msg);
//            return true;
//        }
//        return false;
//    }
//
//    @Override
//    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
//        Player[] OnlinePlayers = Bukkit.getServer().getOnlinePlayers().toArray(new Player[0]);
//        String[] Players;
//        Players = new String[OnlinePlayers.length];
//        for (int i = 0; i < OnlinePlayers.length; i++){
//            Players[i] = OnlinePlayers[i].getDisplayName();
//        }
//
//        return Arrays.asList(Players);
//    }
//}
