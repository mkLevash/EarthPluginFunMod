package earthrp.menusystem.menu.army;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Army;
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

public class BattleMenu extends Menu {
    Army def = menuUtility.getDefender();
    Army att = menuUtility.getAttacker();
    int ter = menuUtility.getTerrain();
    Location loc = menuUtility.getArmyShulkerLoc();

    Material shulkerColor = menuUtility.getShulkerColor();
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    public BattleMenu(MenuUtility menuUtility) {
        super(menuUtility);
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

            case STONE -> {
                e.getWhoClicked().closeInventory();
                menuUtility.setTerrain(ter+1);
                new BattleMenu(menuUtility).open();
            }
            case STONE_SLAB -> {
                e.getWhoClicked().closeInventory();
                menuUtility.setTerrain(ter-1);
                new BattleMenu(menuUtility).open();
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
                    if(army.getUuid().equals(att.getUuid())){
                        shulker.getInventory().setContents(shulkerInHand.getInventory().getContents());
                        item.setAmount(0);
                    }
                }

                Earth.getInstance().getBattleManager().newBattle(att,def,ter,loc);

            }

        }


    }

    @Override
    public void setMenuItems() {

        ItemStack defender = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta defenderMeta = (SkullMeta) defender.getItemMeta();
        defenderMeta.setOwningPlayer(Bukkit.getOfflinePlayer(def.getOwnerId()));
        defenderMeta.setDisplayName("Защитник");
        defenderMeta.setLore(List.of(def.getOwner().getDisplayName()));
        defender.setItemMeta(defenderMeta);
        inventory.setItem(0,defender);


        ItemStack attacker = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta attackerMeta = (SkullMeta) attacker.getItemMeta();
        attackerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(att.getOwnerId()));
        attackerMeta.setDisplayName("Атакующий");
        attackerMeta.setLore(List.of(att.getOwner().getDisplayName()));
        attacker.setItemMeta(attackerMeta);
        inventory.setItem(1,attacker);

        ItemStack terrainMinus = new ItemStack(Material.STONE_SLAB);
        ItemMeta minusMeta = terrainMinus.getItemMeta();
        minusMeta.setDisplayName("Уменьшить модификатор");
        terrainMinus.setItemMeta(minusMeta);
        inventory.setItem(3,terrainMinus);

        ItemStack terrain = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta terrainMeta = terrain.getItemMeta();
        terrainMeta.setDisplayName("Модификатор местности: -" + ter);
        terrainMeta.setLore(List.of(ChatColor.WHITE+"Модификатор к броску кубика",
                ChatColor.WHITE+"который получает атакующий"));
        terrain.setItemMeta(terrainMeta);
        inventory.setItem(4,terrain);

        ItemStack terrainPlus = new ItemStack(Material.STONE);
        ItemMeta plusMeta = terrainPlus.getItemMeta();
        plusMeta.setDisplayName("Увеличить модификатор");
        terrainPlus.setItemMeta(plusMeta);
        inventory.setItem(5,terrainPlus);

        ItemStack yes = new ItemStack(Material.EMERALD);
        ItemMeta yesMeta = yes.getItemMeta();
        yesMeta.setDisplayName("Начать битву");
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
