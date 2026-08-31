package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.Army;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CreateBot implements CommandExecutor, TabCompleter {
    private final ServerDatabase db;
    private final Earth earth;
    public CreateBot(Earth plugin) {
        this.earth = plugin;
        db = plugin.getDatabase();
    }
    private final NamespacedKey armyOwnerKey = new NamespacedKey(Earth.getInstance(), "armyOwner");
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    private final NamespacedKey botNameKey = new NamespacedKey(Earth.getInstance(), "botName");
    private final NamespacedKey unitTypeKey = new NamespacedKey(Earth.getInstance(),"unitType");
    private final NamespacedKey unitLvlKey = new NamespacedKey(Earth.getInstance(),"unitLvl");
    private final NamespacedKey unitDiscKey = new NamespacedKey(Earth.getInstance(),"unitDisc");
    private final NamespacedKey unitFireKey = new NamespacedKey(Earth.getInstance(),"unitFire");
    private final NamespacedKey unitShockKey = new NamespacedKey(Earth.getInstance(),"unitShock");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        //Bukkit.broadcastMessage(" " + args.length);
        if (sender instanceof Player player){

            switch (args.length){
                case 1 -> {
                    UUID botId = UUID.randomUUID();
                    String botName = args[0];
                    if(db.getPlayer(botName)!=null) return false;
                    db.addBot(botName, botId);

                    String capitalId = String.valueOf(UUID.randomUUID());
                    ItemStack capital = new ItemStack(Material.END_CRYSTAL, 1);
                    ItemMeta capitalMeta = capital.getItemMeta();
                    capitalMeta.setDisplayName("Столица " + botName);
                    capitalMeta.setLore(List.of("capital",capitalId,String.valueOf(botId),botName));
                    capital.setItemMeta(capitalMeta);
                    player.getInventory().addItem(capital);


                    return true;
                }
                case 2 ->{
                    EPlayer bot = db.getPlayer(args[0]);
                    if(bot == null) return true;
                    String townId = String.valueOf(UUID.randomUUID());
                    ItemStack town = new ItemStack(Material.END_CRYSTAL);
                    ItemMeta townMeta = town.getItemMeta();
                    switch (args[1]){
                        case "army" -> {
                            UUID armyId = UUID.randomUUID();
                            Army army = new Army(armyId,bot.getUniqueId(),"");
                            db.addArmy(army);

                            ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
                            SkullMeta ownerMeta = (SkullMeta) owner.getItemMeta();
                            ownerMeta.getPersistentDataContainer().set(armyOwnerKey, PersistentDataType.STRING, bot.getUniqueId().toString());
                            ownerMeta.getPersistentDataContainer().set(armyIdKey, PersistentDataType.STRING, armyId.toString());
                            ownerMeta.setDisplayName(args[0]);
                            owner.setItemMeta(ownerMeta);

                            ItemStack shulkerItem = new ItemStack(Material.SHULKER_BOX);
                            BlockStateMeta bsm = (BlockStateMeta) shulkerItem.getItemMeta();
                            ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
                            shulker.getInventory().addItem(owner);
                            bsm.setBlockState(shulker);
                            shulker.update();
                            bsm.setDisplayName("Армия " + args[0]);
                            shulkerItem.setItemMeta(bsm);

                            player.getInventory().addItem(shulkerItem);
                        }
                        case "capital" -> {
                            townMeta.setDisplayName("Столица " + bot.getCountryName());
                            townMeta.setLore(List.of("capital", townId,String.valueOf(bot.getUniqueId()),bot.getDisplayName()));
                            town.setItemMeta(townMeta);
                            player.getInventory().addItem(town);
                        }
                        case "townHall" -> {
                            townMeta.setDisplayName("Город " + bot.getCountryName());
                            townMeta.setLore(List.of("townHall", townId,String.valueOf(bot.getUniqueId()),bot.getDisplayName()));
                            town.setItemMeta(townMeta);
                            player.getInventory().addItem(town);
                        }
                    }
                }
            }



        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            List<String> list = new ArrayList<>();
            list.add("<Имя бота>");
            outerLoop:
            for (EPlayer player:db.getPlayers()){
                OfflinePlayer[] players = Bukkit.getOfflinePlayers();
                for(OfflinePlayer oPlayer: players){
                    if (player.getUniqueId().equals(oPlayer.getUniqueId())) continue outerLoop;
                }
                list.add(player.getDisplayName());
            }
            return list;
        }
        if(args.length == 2){
            return List.of("army","capital","townHall");
        }
        return Collections.singletonList("");
    }
}
