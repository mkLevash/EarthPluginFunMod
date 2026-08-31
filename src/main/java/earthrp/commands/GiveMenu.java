package earthrp.commands;

import earthrp.Earth;
import earthrp.tools.Tools;
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

public class GiveMenu implements CommandExecutor, TabCompleter {
    private final Earth plugin;
    ServerDatabase db;
    public GiveMenu(Earth plugin) {
        this.plugin = plugin;
        db = plugin.getDatabase();
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p){
            ItemStack menu = new ItemStack(Material.WRITABLE_BOOK);
            ItemMeta meta = menu.getItemMeta();
            meta.setDisplayName("Главное меню");
            meta.setLore(Collections.singletonList(Tools.colorText("&fНажми чтобы открыть меню")));
            menu.setItemMeta(meta);
            p.getInventory().addItem(menu);
            sender.sendMessage(ChatColor.GREEN + "Вы выдали себе меню");
            return true;
        }


        return true;
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
