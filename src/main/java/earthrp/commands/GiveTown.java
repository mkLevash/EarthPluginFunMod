package earthrp.commands;

import earthrp.Earth;
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

import java.util.List;
import java.util.UUID;

public class GiveTown implements CommandExecutor, TabCompleter {
    private final Earth plugin;

    public GiveTown(Earth plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p){
            if (args.length == 2){
                String capitalId = String.valueOf(UUID.randomUUID());
                ItemStack capital = new ItemStack(Material.END_CRYSTAL, 1);
                ItemMeta capitalMeta = capital.getItemMeta();
                capitalMeta.setDisplayName(args[1]);
                capitalMeta.setLore(List.of(args[0],capitalId,String.valueOf(p.getUniqueId()),p.getDisplayName(),"debug"));
                capital.setItemMeta(capitalMeta);
                p.getInventory().addItem(capital);
                sender.sendMessage(ChatColor.GREEN + "Вы выдали себе ратушу");
            }
            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            return List.of("townHall","capital");
        }
        if(args.length == 2) {
            return List.of("<Название города>");
        }
        return List.of();

    }
}
