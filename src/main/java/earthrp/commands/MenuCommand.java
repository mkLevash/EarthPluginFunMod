package earthrp.commands;

import earthrp.Earth;
import earthrp.menusystem.menu.MainMenu;
import earthrp.tools.Tools;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MenuCommand implements CommandExecutor {
    private final Earth instance;

    public MenuCommand(Earth instance) {
        this.instance = instance;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player p){
            if(args.length == 1 && args[0].equals("get")){
                ItemStack menu = new ItemStack(Material.WRITABLE_BOOK);
                ItemMeta meta = menu.getItemMeta();
                meta.setDisplayName("Главное меню");
                meta.setLore(Collections.singletonList(Tools.colorText("&fНажми чтобы открыть меню")));
                menu.setItemMeta(meta);
                p.getInventory().addItem(menu);
                sender.sendMessage(ChatColor.GREEN + "Вы выдали себе меню");
                return true;
            }
            MenuUtility mu = new MenuUtility(p);
            mu.setPlayer(Earth.getInstance().getDatabase().getPlayer(p.getUniqueId()));
            new MainMenu(mu).open();
            return true;
        }

        return false;
    }
}
