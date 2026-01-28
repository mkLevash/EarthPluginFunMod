package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class GiveManpower implements CommandExecutor {
    private final Earth plugin;

    public GiveManpower(Earth plugin) {
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            EPlayer p = plugin.getServerDatabase().getPlayer(player.getUniqueId());
            if (args.length != 1){
                sender.sendMessage(ChatColor.YELLOW + "/manpower <amount>");
                return true;
            } else{
                int amount;
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы вверно ввели <amount>");
                    return true;
                }
                double manpower = p.getAttribute(EPlayerAttribute.MANPOWER);
                if (manpower-amount < 0){
                    sender.sendMessage(ChatColor.YELLOW + "У вас недостаточно людского ресурса");
                    return true;
                }
                ItemStack villager = new ItemStack(Material.VILLAGER_SPAWN_EGG, amount);
                ItemMeta villagerMeta = villager.getItemMeta();
                villagerMeta.setDisplayName("Manpower");
                villager.setItemMeta(villagerMeta);
                player.getInventory().addItem(villager);
                sender.sendMessage(ChatColor.GREEN + "Вы выдали себе " + amount + " manpower");
                p.addAttribute(EPlayerAttribute.MANPOWER,-amount);
                return true;
            }
        }


        return true;
    }
}
