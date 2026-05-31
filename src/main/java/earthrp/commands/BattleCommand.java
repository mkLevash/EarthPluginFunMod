package earthrp.commands;

import earthrp.battle.Battle;
import earthrp.customObjects.Army;
import earthrp.Earth;
import earthrp.tools.Tools;
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
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static earthrp.tools.PDCKeys.*;


public class BattleCommand implements CommandExecutor, TabCompleter {
    private final Earth earth;
    public BattleCommand(Earth plugin) {
        this.earth = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {


        if(args.length==2 && args[0].equals(args[1])) return false;

        if(args.length ==1 && args[0].equals("clear")){
            Map<UUID, earthrp.battle.Battle> map = Earth.getInstance().getBattleManager().getBattleCache();
            List<earthrp.battle.Battle> battles = new ArrayList<>(map.values());
            if(!battles.isEmpty()){
                for(earthrp.battle.Battle b:battles){

                    this.earth.getBattleManager().delBattle(b);
                    Bukkit.broadcastMessage("удалено");
                }
                return true;
            }else{
                Bukkit.broadcastMessage("null");
            }
        } else if (args.length == 1 && commandSender instanceof Player player) {
            int ter = 0;
            try{
               ter = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage("Вы неправильно ввели модификатор местности");
                return true;
            }

            for(Battle b:Earth.getInstance().getBattleManager().getBattles()){
                if(player.getLocation().getChunk().equals(b.getLoc().getChunk())){

                    Tools.removePreBattleHologram(b.getLoc());
                    Tools.spawnBattleHologram(b);

                    b.setTer(ter);
                    b.setPrePhase(false);
                }
            }

        }
        if(commandSender instanceof Player player && args.length >= 2){
            Army attacker = null;
            Army defender = null;
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.getType().equals(Material.SHULKER_BOX)){
                ShulkerBox shulkerBox = (ShulkerBox) ((BlockStateMeta) item.getItemMeta()).getBlockState();

                for (ItemStack i : shulkerBox.getInventory().getContents()) {
                    if (i != null && item.hasItemMeta()) {
                        ItemMeta meta = item.getItemMeta();
                        if(meta.getPersistentDataContainer().has(armyIdKey, PersistentDataType.STRING)){
                            attacker = earth.getServerDatabase().getArmy(UUID.fromString(meta.getPersistentDataContainer().get(armyIdKey,PersistentDataType.STRING)));
                        }
                    }
                }
            }

            Player[] OnlinePlayers = Bukkit.getServer().getOnlinePlayers().toArray(new Player[0]);
            for (Player p : OnlinePlayers) {
                if (p.getDisplayName().equals(args[0])){
                    if(p.getInventory().getItemInMainHand() instanceof ShulkerBox shulkerBox ){
                        for (ItemStack i : shulkerBox.getInventory().getContents()) {
                            if (i != null && item.hasItemMeta()) {
                                ItemMeta meta = item.getItemMeta();
                                if(meta.getPersistentDataContainer().has(armyIdKey, PersistentDataType.STRING)){
                                    defender = earth.getServerDatabase().getArmy(UUID.fromString(meta.getPersistentDataContainer().get(armyIdKey,PersistentDataType.STRING)));
                                }
                            }
                        }
                    }
                }
            }



            Location eye = player.getEyeLocation();
            Location eyeLoc = eye.add(eye.getDirection().normalize().multiply(2));
            Location spawnLoc = new Location(eye.getWorld(), eyeLoc.getX(), player.getLocation().getY() - 0.25,eyeLoc.getZ());

            this.earth.getBattleManager().newBattle(attacker,defender,0,spawnLoc);


            return true;
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length==1) {
            return  List.of("<Модификатор местности>");
        } else{
            return List.of();
        }


    }




}
