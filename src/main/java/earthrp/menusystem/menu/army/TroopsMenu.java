package earthrp.menusystem.menu.army;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Army;
import earthrp.customObjects.Unit;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Set;

public class TroopsMenu extends Menu {
    Army army = menuUtility.getArmy();
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    public TroopsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if(item!=null&&item.getType().equals(Material.BARRIER)){
            e.getWhoClicked().closeInventory();
            new ArmyMenu(menuUtility).open();
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack inf0 = Tools.createTroopItem(Material.ICE,"Ополчение","inf",0,army,"inf0");
        inventory.setItem(0,inf0);
        ItemStack inf1 = Tools.createTroopItem(Material.ICE,"Копейщики","inf",1,army,"inf1");
        inventory.setItem(1,inf1);
        ItemStack inf2 = Tools.createTroopItem(Material.ICE,"Лучники","inf",2,army,"inf2");
        inventory.setItem(2,inf2);
        ItemStack inf3 = Tools.createTroopItem(Material.ICE,"Аркебузиры","inf",3,army,"inf3");
        inventory.setItem(3,inf3);
        ItemStack inf4 = Tools.createTroopItem(Material.ICE,"Мушкетёры","inf",4,army,"inf4");
        inventory.setItem(4,inf4);

        ItemStack cav1 = Tools.createTroopItem(Material.ICE,"Легкие всадники","cav",1,army,"cav1");
        inventory.setItem(10,cav1);
        ItemStack cav2 = Tools.createTroopItem(Material.ICE,"Конные лучники","cav",2,army,"cav2");
        inventory.setItem(11,cav2);
        ItemStack cav3 = Tools.createTroopItem(Material.ICE,"Тяжёлые всадники","cav",3,army,"cav3");
        inventory.setItem(12,cav3);
        ItemStack cav4 = Tools.createTroopItem(Material.ICE,"Карабинеры","cav",4,army,"cav4");
        inventory.setItem(13,cav4);

        ItemStack art3 = Tools.createTroopItem(Material.ICE,"Большая чугунная пушка","art",3,army,"art1");
        inventory.setItem(21,art3);
        ItemStack art4 = Tools.createTroopItem(Material.ICE,"Тяжёлая Гаубица","art",4,army,"art2");
        inventory.setItem(22,art4);

        ItemStack troops = new ItemStack(Material.PORKCHOP,1);
        ItemMeta troopsMeta = troops.getItemMeta();
        troopsMeta.setDisplayName("Ваши войска");
        troopsMeta.setCustomModelData(1);
        Set<Unit> units = army.getUnits();
        double moraleSum = 0;
        int hp = 0;
        if(units != null){
            for(Unit u : units){
                moraleSum += u.getMorale();
                hp += u.getHp();
            }
        }

        double morale = Tools.round(moraleSum/army.getSize());
        troopsMeta.setLore(List.of(
                ChatColor.translateAlternateColorCodes('~', "~fМораль армии - ~2" + morale),
                ChatColor.translateAlternateColorCodes('~', "~fЗаполненность - ~d" + hp + "~f/~7" +army.getSize()*1000),
                ChatColor.translateAlternateColorCodes('~', "~fПолки:"),
                ChatColor.translateAlternateColorCodes('~', "~fПехота - ~a" + army.getInfantry()),
                ChatColor.translateAlternateColorCodes('~', "~fКавалерия - ~a" + army.getCavalry())

        ));
        troops.setItemMeta(troopsMeta);
        inventory.setItem(26, createBackItem());


        


    }
}
