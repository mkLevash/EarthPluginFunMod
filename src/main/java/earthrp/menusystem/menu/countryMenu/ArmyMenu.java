package earthrp.menusystem.menu.countryMenu;

import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.MainMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArmyMenu extends Menu {

    public ArmyMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }
    
    EPlayer player = menuUtility.getPlayer();
    Player p = menuUtility.getOwner();




    @Override
    public String getMenuName() {
        return "Меню армии";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if(item!=null && item.getType().equals(Material.BARRIER)){
            p.closeInventory();
            new MainMenu(menuUtility).open();
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        List<String> manpowerLore = List.of(
                "Доступный " + (int) player.getAttribute(EPlayerAttribute.MANPOWER),
                "Максимум " + player.getManpowerLimit(),
                "Прирост " + player.getManpowerIncrease()
        );
        ItemStack manpower = makeItem(Material.EGG,"Людской ресурс","manpower","manpower",manpowerLore);
        inventory.setItem(28,manpower);





        double tac = Tools.round(player.getAttribute(EPlayerAttribute.TACTIC) * player.getAttribute(EPlayerAttribute.DISCIPLE));
        String dis = Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.DISCIPLE));
        List<String> mainStatsList = List.of(
                Tools.colorText("&fМоральᠩ&a"+Tools.getColorModLegacy(player.getMoraleMod())),
                Tools.colorText("&fТрадиции &2" + (int) player.getAttribute(EPlayerAttribute.TRADITION)),
                Tools.colorText("&fТактикаᠨ&a" + tac + " &f|ᠨ&b" + player.getAttribute(EPlayerAttribute.TACTIC) + " " + dis + "&fᠧ"),
                Tools.colorText("&fСоотношение кав-рииᢰ&d" + (int) Math.round(player.getAttribute(EPlayerAttribute.CAV_RATIO) * 100)+"&f%" )
        );
        ItemStack mainStats = Tools.createCountryStat("Основная статистика",mainStatsList,"mainStats");
        inventory.setItem(13,mainStats);


        List<String> damageStatsList = List.of(
                Tools.colorText("&fФаза &4огня " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.FIRE_DAMAGE))),
                Tools.colorText("&fФаза &6шока " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.SHOCK_DAMAGE))),
                Tools.colorText("&2Мораль " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.MORALE_DAMAGE)))
        );

        ItemStack damageStats = Tools.createCountryStat("Модификаторы урона", damageStatsList,"damageStats");
        inventory.setItem(12,damageStats);

        List<String> resistStatsList = List.of(
                Tools.colorText("&fФаза &4огня " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.FIRE_RESIST))),
                Tools.colorText("&fФаза &6шока " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.SHOCK_RESIST))),
                Tools.colorText("&2Мораль " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.MORALE_RESIST)))
        );

        ItemStack resistStats = Tools.createCountryStat("Модификаторы сопротивления", resistStatsList,"resistStats");
        inventory.setItem(14,resistStats);

        List<String> armyCostList = List.of(
                Tools.colorText("&fПехота(&d" +  player.getTroops().get("inf").size() + "&f) &4" + player.getInfExpense() + " &f| " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.INF_COST), true) ),
                Tools.colorText("&fКавалерия(&d" +  player.getTroops().get("cav").size() + "&f) &4" + player.getCavExpense() + " &f| " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.CAV_COST), true) ),
                Tools.colorText("&fАртиллерия(&d" +  player.getTroops().get("art").size() + "&f) &4" + player.getArtExpense() + " &f| " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.ART_COST), true) ),
                Tools.colorText("&fВсего &4" + player.getLandArmyExpense() + " &f| " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD), true))
        );

        ItemStack armyCost = Tools.createCountryStat("Содержание армии",armyCostList,"armyCost");
        inventory.setItem(16,armyCost);

        List<String> armyStatsList = List.of(
                Tools.colorText("&fПехота " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.INF_COMBAT_ABILITY))),
                Tools.colorText("&fКавалерия " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.CAV_COMBAT_ABILITY))),
                Tools.colorText("&fАртиллерия " + Tools.getColorModLegacy(player.getAttribute(EPlayerAttribute.ART_COMBAT_ABILITY)))
        );

        ItemStack armyStats = Tools.createCountryStat("Боевая мощь войск",  armyStatsList,"armyStats");
        inventory.setItem(25,armyStats);


        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 35);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);


        inventory.setItem(40,createBackItem());



    }
}
