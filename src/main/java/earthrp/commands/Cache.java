package earthrp.commands;

import earthrp.Earth;
import earthrp.database.ServerDatabase;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Cache implements CommandExecutor, TabCompleter {
    private final Earth plugin;

    public Cache(Earth plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if(args.length == 1){
            ServerDatabase db = Earth.getInstance().getDatabase();
            switch (args[0]){
                case "reload" ->{
                    db.saveCache();
                    db.loadCache();
                }
                case "save" ->{
                    db.saveCache();
                }
                case "load" ->{
                    db.loadCache();
                }
                case "print" ->{
                    Earth.getInstance().getDatabase().printCacheStatus();
                }

            }
        }


        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {

        if(args.length==1){
            return List.of("save","load","reload","print");
        }
        return null;

    }
}
