package earthrp.commands;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Leader implements CommandExecutor, TabCompleter {
    private final Earth earth;
    public Leader(Earth plugin) {
        this.earth = plugin;
    }
    private final NamespacedKey armyOwnerKey = new NamespacedKey(Earth.getInstance(), "armyOwner");
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    private final NamespacedKey unitTypeKey = new NamespacedKey(Earth.getInstance(),"unitType");
    private final NamespacedKey unitLvlKey = new NamespacedKey(Earth.getInstance(),"unitLvl");
    private final NamespacedKey unitDiscKey = new NamespacedKey(Earth.getInstance(),"unitDisc");
    private final NamespacedKey unitFireKey = new NamespacedKey(Earth.getInstance(),"unitFire");
    private final NamespacedKey unitShockKey = new NamespacedKey(Earth.getInstance(),"unitShock");
    private final NamespacedKey botNameKey = new NamespacedKey(Earth.getInstance(), "botName");
    private final NamespacedKey holoKey = new NamespacedKey(Earth.getInstance(), "holoType");

    private final NamespacedKey leaderFireKey = new NamespacedKey(Earth.getInstance(),"leaderFire");
    private final NamespacedKey leaderShockKey = new NamespacedKey(Earth.getInstance(),"leaderShock");
    private final NamespacedKey leaderMoveKey = new NamespacedKey(Earth.getInstance(),"leaderMove");
    private final NamespacedKey leaderSiegeKey = new NamespacedKey(Earth.getInstance(),"leaderSiege");


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        ServerDatabase db = Earth.getInstance().getDatabase();
        if(sender instanceof Player player){
            if(args.length==6 && args[0].equals("set") && player.isOp()){

                int fire = Integer.parseInt(args[2]);
                int shock = Integer.parseInt(args[3]);
                int move = Integer.parseInt(args[4]);
                int siege = Integer.parseInt(args[5]);
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemStack leader = item;
                if(!item.getType().equals(Material.PLAYER_HEAD)) leader = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta leaderMeta = (SkullMeta) leader.getItemMeta();
                leaderMeta.setDisplayName(ChatColor.translateAlternateColorCodes('~',
                        "~d" + args[1] + " ~4" + fire + " ~6" + shock + " ~9" + move + " ~a" + siege));
                leaderMeta.getPersistentDataContainer().set(leaderShockKey,PersistentDataType.INTEGER,shock);
                leaderMeta.getPersistentDataContainer().set(leaderFireKey,PersistentDataType.INTEGER,fire);
                leaderMeta.getPersistentDataContainer().set(leaderMoveKey,PersistentDataType.INTEGER,move);
                leaderMeta.getPersistentDataContainer().set(leaderSiegeKey,PersistentDataType.INTEGER,siege);
                leader.setItemMeta(leaderMeta);
                if(!item.getType().equals(Material.PLAYER_HEAD)) player.getInventory().addItem(leader);
            }else if( args.length==2){
                EPlayer p = db.getPlayer(player.getUniqueId());
                int d;
                double tradition = p.getAttribute(EPlayerAttribute.TRADITION);
                int max = 5;
                if(tradition<=5) max = 2;
                if(tradition<=10) max = 3;
                if(tradition<=20) max = 4;

                try {
                    d = parseInt(args[0]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.YELLOW + "Ошибка, убедитесь что вы верно ввели число");
                    return true;
                }
                if(db.getPlayer(player).getAttribute(EPlayerAttribute.POLIT_BALANCE)>=d){
                    d = (int) Math.floor(d*1.5/5);
                    db.getPlayer(player).addAttribute(EPlayerAttribute.POLIT_BALANCE,-d);
                }else{
                    sender.sendMessage(ChatColor.YELLOW + "Недостаточно полит. власти");
                    return true;
                }

                //Bukkit.broadcastMessage(String.valueOf(d));
                int shock = roll(max,1+d).stream().mapToInt(i -> i).max().orElse(0);
                int fire = roll(max,1+d).stream().mapToInt(i -> i).max().orElse(0);
                int move = roll(max,1+d/2).stream().mapToInt(i -> i).max().orElse(0);
                int siege = roll(max,1+d/2).stream().mapToInt(i -> i).max().orElse(0);
                ItemStack item = player.getInventory().getItemInMainHand();
                ItemStack leader = item;
                if(!item.getType().equals(Material.PLAYER_HEAD)) leader = new ItemStack(Material.PLAYER_HEAD);
                SkullMeta leaderMeta = (SkullMeta) leader.getItemMeta();
                leaderMeta.setDisplayName(ChatColor.translateAlternateColorCodes('~',
                        "~d" + args[1] + " ~4" + fire + " ~6" + shock + " ~9" + move + " ~a" + siege));
                leaderMeta.getPersistentDataContainer().set(leaderShockKey,PersistentDataType.INTEGER,shock);
                leaderMeta.getPersistentDataContainer().set(leaderFireKey,PersistentDataType.INTEGER,fire);
                leaderMeta.getPersistentDataContainer().set(leaderMoveKey,PersistentDataType.INTEGER,move);
                leaderMeta.getPersistentDataContainer().set(leaderSiegeKey,PersistentDataType.INTEGER,siege);
                leader.setItemMeta(leaderMeta);
                if(!item.getType().equals(Material.PLAYER_HEAD)) player.getInventory().addItem(leader);


            }else if(args.length==6){

                player.sendMessage(ChatColor.RED + "Ошибка прав");
                player.sendMessage( "/leader <кол-во политки> <имя генерала>");
                return true;
            }
        }


        return false;
    }

    private List<Integer> roll(int d, int k){
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            res.add((int) (Math.random()*d));
        }
        return res;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length==1) {
            return  List.of("<Количество полит власти>");
        }
        if(args.length==2){
            return  List.of("<Имя генерала>");}
        else if(args[0].equals("set")){
            if(args.length==3) return List.of("<огонь>");
            if(args.length==4) return List.of("<шок>");
            if(args.length==5) return List.of("<манёвр>");
            if(args.length==6) return List.of("<осада>");
        }
        return null;


    }




}
