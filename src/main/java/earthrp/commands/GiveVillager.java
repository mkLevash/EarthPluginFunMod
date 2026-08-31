package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GiveVillager implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public GiveVillager(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p){
            ItemStack villager = new ItemStack(Material.VILLAGER_SPAWN_EGG);
            ItemMeta villagerMeta = villager.getItemMeta();
            if (args.length == 1){
                EPlayer player = db.getPlayer(args[0]);
                if(player == null) {
                    p.sendMessage("Игрок не найден");
                    return true;
                }
                villagerMeta.setDisplayName("Villager " + player.getDisplayName());

            } else{

                villagerMeta.setDisplayName("Villager " + p.getDisplayName());


            }
            villagerMeta.setLore(Collections.singletonList("Житель твой"));
            villager.setItemMeta(villagerMeta);
            p.getInventory().addItem(villager);
            sender.sendMessage(ChatColor.GREEN + "Вы выдали себе жителя");
            return true;
        }


        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] strings) {
        List<String> tab = new ArrayList<>();
        for(EPlayer player:db.getPlayers()){
            tab.add(player.getDisplayName());
        }
        return tab;
    }
}
