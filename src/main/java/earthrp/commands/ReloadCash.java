package earthrp.commands;

import earthrp.Earth;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCash implements CommandExecutor {
    private final Earth plugin;

    public ReloadCash(Earth plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        switch (args[0]){
            case "reload" ->{
                Earth.getInstance().getServerDatabase().loadCache();
            }
            case "save" ->{
                Earth.getInstance().getServerDatabase().saveCache();
            }
            case "print" ->{
                Earth.getInstance().getServerDatabase().printCacheStatus();
            }

        }

        return true;
    }
}
