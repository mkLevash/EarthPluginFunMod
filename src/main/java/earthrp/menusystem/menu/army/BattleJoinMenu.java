package earthrp.menusystem.menu.army;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Army;
import earthrp.battle.Battle;
import earthrp.customObjects.Unit;
import earthrp.battle.BattleUnit;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.List;

public class BattleJoinMenu extends Menu {
    Army join = menuUtility.getDefender();

    Army defender;
    Army attacker;
    Battle battle;
    Location loc = menuUtility.getArmyShulkerLoc();

    Material shulkerColor = menuUtility.getShulkerColor();
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    public BattleJoinMenu(MenuUtility menuUtility) {
        super(menuUtility);
        for(Battle b:Earth.getInstance().getBattleManager().getBattles()){
            if(b.getAttacker().equals(join) || b.getDefender().equals(join)){
                battle = b;
                attacker = b.getAttacker();
                defender = b.getDefender();
            }
        }
    }

    @Override
    public String getMenuName() {
        return "Начало битвы";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        switch (e.getCurrentItem().getType()){
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
            }


            case EMERALD -> {
                e.getWhoClicked().closeInventory();
                Block block = loc.getBlock();
                ItemStack item = e.getWhoClicked().getInventory().getItemInMainHand();
                block.setType(item.getType());
                ShulkerBox shulker = (ShulkerBox) block.getState();
                BlockStateMeta meta = (BlockStateMeta) item.getItemMeta();
                ShulkerBox shulkerInHand = (ShulkerBox) meta.getBlockState();
                Army army = Tools.getArmyFromInventory(shulkerInHand.getInventory());
                if(army!=null){
                    shulker.getInventory().setContents(shulkerInHand.getInventory().getContents());
                    item.setAmount(0);
                    if(attacker.equals(join)){
                        battle.getAtt().add(army);
                        for(Unit u:army.getUnits()){
                            battle.getAttUnits().add(new BattleUnit(u));
                        }

                    }

                }

            }

        }


    }

    @Override
    public void setMenuItems() {

        ItemStack defenderItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta defenderMeta = (SkullMeta) defenderItem.getItemMeta();
        defenderMeta.setOwningPlayer(Bukkit.getOfflinePlayer(defender.getOwnerId()));
        defenderMeta.setDisplayName("Защитник");
        defenderMeta.setLore(List.of(defender.getOwner().getDisplayName()));
        defenderItem.setItemMeta(defenderMeta);
        inventory.setItem(0,defenderItem);


        ItemStack attackerItem = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta attackerMeta = (SkullMeta) attackerItem.getItemMeta();
        attackerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(attacker.getOwnerId()));
        attackerMeta.setDisplayName("Атакующий");
        attackerMeta.setLore(List.of(attacker.getOwner().getDisplayName()));
        attackerItem.setItemMeta(attackerMeta);
        inventory.setItem(1,attackerItem);


        ItemStack yes = new ItemStack(Material.EMERALD);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.setDisplayName("Присоединиться к " + join.getOwner().getCountryName());
        yes.setItemMeta(yesMeta);
        inventory.setItem(7,yes);

        ItemStack no = new ItemStack(Material.BARRIER);
        ItemMeta noMeta = no.getItemMeta();
        noMeta.setDisplayName("Отступить");
        no.setItemMeta(noMeta);
        inventory.setItem(8, no);




//        attackerMeta.getPersistentDataContainer().set(armyIdKey, PersistentDataType.STRING, army.getUniqueId().toString());
//        attackerMeta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Владелец армии"));
//        attacker.setItemMeta(attackerMeta);


//        ItemStack leader = new ItemStack(Material.PLAYER_HEAD, 1);
//        SkullMeta leaderMeta = (SkullMeta) leader.getItemMeta();
//        if (army.getLeaderName()==null){
//            leaderMeta.setDisplayName(ChatColor.RED + "Генерал отсутствует");
//        } else if (army.getLeaderName().equals("ruler")) {
//            leaderMeta.setOwningPlayer(Bukkit.getOfflinePlayer(army.getOwnerId()));
//            leaderMeta.setDisplayName( ChatColor.translateAlternateColorCodes('~',
//                    "~d" + army.getOwner().getDisplayName() + " ~4" + army.getLeaderFire() + " ~6" + army.getLeaderShock())
//            );
//        }
//        leader.setItemMeta(leaderMeta);
//        inventory.setItem(12,leader);




        


    }
}
