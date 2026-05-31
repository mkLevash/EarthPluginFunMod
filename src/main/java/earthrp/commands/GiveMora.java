package earthrp.commands;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class GiveMora implements CommandExecutor {
    ServerDatabase db;
    public GiveMora(Earth plugin) {
        db = plugin.getServerDatabase();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (sender instanceof Player player){
            EPlayer p = db.getPlayer(player.getUniqueId());
            int amount = 0;
            if (args.length != 1){
                sender.sendMessage(ChatColor.YELLOW + "/mora <amount>");
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemStack air = new ItemStack(Material.AIR,1);
                List<String> lore = Objects.requireNonNull(item.getItemMeta()).getLore();
                assert lore != null;
                switch (lore.get(0)) {
                    case "1 мора" -> {
                        player.getInventory().setItemInMainHand(air);
                        amount = item.getAmount();
                    }
                    case "9 моры" -> {
                        player.getInventory().setItemInMainHand(air);
                        amount = item.getAmount() * 9;
                    }
                    case "81 моры" -> {
                        player.getInventory().setItemInMainHand(air);
                        amount = item.getAmount() * 81;
                    }
                }
                p.addAttribute(EPlayerAttribute.TREASURY,amount);

            } else{
                try {
                    amount = Integer.parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы верно ввели <amount>");
                    return true;
                }

                if (p.getAttribute(EPlayerAttribute.TREASURY) - amount >= 0){
                    p.addAttribute(EPlayerAttribute.TREASURY,-amount);


                   // moraMeta.getPersistentDataContainer().set(new NamespacedKey(Earth.getPlugin(), "uuid"), PersistentDataType.STRING, players.get(index).getUniqueId().toString());
                    ItemStack mora = Tools.createMora(amount);

                    Map<Integer, ItemStack> overflow = player.getInventory().addItem(mora);

                    if (!overflow.isEmpty()) {
                        // Если карта не пуста, значит, часть предметов не влезла
                        for (ItemStack remaining : overflow.values()) {
                            // Спавним не поместившиеся предметы на землю рядом с игроком
                            player.getWorld().dropItemNaturally(player.getLocation(), remaining);
                        }
                        player.sendMessage("Ваш инвентарь полон! Часть предметов упала на землю.");
                    }
                    sender.sendMessage(ChatColor.GREEN + "Вы выдали себе " + amount + " моры");
                }else{
                    sender.sendMessage(ChatColor.YELLOW + "В вашей казне недостаточно моры!");
                    return true;
                }

            }

            return true;


        }


        return true;
    }
}
