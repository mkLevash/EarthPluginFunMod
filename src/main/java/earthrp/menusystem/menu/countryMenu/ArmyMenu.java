package earthrp.menusystem.menu.countryMenu;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Unit;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.Main;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static earthrp.tools.PDCKeys.*;

public class ArmyMenu extends Menu {

    public ArmyMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }
    
    EPlayer player = menuUtility.getPlayer();
    Player p = menuUtility.getOwner();

    List<String> leviesList = new ArrayList<>(Arrays.asList(
            Tools.colorText("&fМоральᠩ&22.5"),
            Tools.colorText("&fДисциплинаᠧ&c-5%"),
            Tools.colorText("&fУрон &40.15&f/&60.15"),
            Tools.colorText("&fОчки &40&f/&60&f/&20")
    ));

    @Override
    public String getMenuName() {
        return "Главное меню";
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if (item!=null && item.getItemMeta().getPersistentDataContainer().has(statIdKey)){

            String statId = item.getItemMeta().getPersistentDataContainer().get(statIdKey, PersistentDataType.STRING);
            p.closeInventory();
            switch (statId){
                case "leviesStats" ->{
                    if(!player.isLevies() && !player.isWar()){
                        p.sendMessage(Tools.colorText( "&fДоступно только вовремя&c войны"));
                    }
                    if(player.isLevies() && !player.isWar()){
                        ServerDatabase db = Earth.getInstance().getServerDatabase();
                        for(Unit u:player.getUnits()){
                            if(u.getLvl()==0) db.deleteUnit(u);
                        }
                        player.setAttribute(EPlayerAttribute.LEVIES_STATUS,0.0);

                    }
                    if(!player.isLevies() && player.isWar()){
                        ItemStack inf0 = Tools.createArmyCraftItem("&fОполчение",leviesList,"inf",0,-0.05,0.15,0.15);
                        inf0.setAmount(player.getManpowerLimit());
                        p.getInventory().addItem(inf0);
                        player.setAttribute(EPlayerAttribute.LEVIES_STATUS,1.0);

                    }
                }
            }
            new ArmyMenu(menuUtility).open();

        }else if(item!=null && item.getType().equals(Material.BARRIER)){
            p.closeInventory();
            new Main(menuUtility).open();
        }


    }

    @Override
    public void setMenuItems() {

        List<String> manpowerLore = List.of(
                Tools.colorText("&fДоступныйऴ" + (int) player.getAttribute(EPlayerAttribute.MANPOWER)),
                Tools.colorText("&fМаксимумऴ" + player.getManpowerLimit() + " | " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.MANPOWER_LIMIT_MOD)) ),
                Tools.colorText("&fПриростऴ" + (int) 0 + " | " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.WAR_SUPPORT),false,true) + "स"),
                Tools.colorText(" ")
        );
        ItemStack manpower = Tools.createCountryStat("ऴЛюдской ресурс",manpowerLore,"manpower");
        inventory.setItem(22,manpower);
//
//        List<String> mpModsList = List.of(
//                Tools.colorText("&fМаксимумऴ" + Tools.getColorMod(player.getManpowerLimitMod()) ),
//                Tools.colorText("&fПриростऴ" + player.getManpowerIncMod()),
//                Tools.colorText("&fЛимит армии " + Tools.getColorMod(player.getLimitMod()))
//
//        );
//        ItemStack mpMods = Tools.createCountryStat("Модификаторыऴ",mpModsList,"mpMods");
//        inventory.setItem(19,mpMods);



        double tac = Tools.round(player.getAttribute(EPlayerAttribute.TACTIC) * player.getAttribute(EPlayerAttribute.DISCIPLE));
        String dis = Tools.getColorMod(player.getAttribute(EPlayerAttribute.DISCIPLE));
        List<String> mainStatsList = List.of(
                Tools.colorText("&fМоральᠩ&a"+Tools.getColorMod(player.getMoraleMod())),
                Tools.colorText("&fТрадиции &2" + (int) player.getAttribute(EPlayerAttribute.TRADITION)),
                Tools.colorText("&fТактикаᠨ&a" + tac + " &f|ᠨ&b" + player.getAttribute(EPlayerAttribute.TACTIC) + " " + dis + "&fᠧ"),
                Tools.colorText("&fСоотношение кав-рииᢰ&d" + (int) Math.round(player.getAttribute(EPlayerAttribute.CAV_RATIO) * 100)+"&f%" )
        );
        ItemStack mainStats = Tools.createCountryStat("Основная статистика",mainStatsList,"mainStats");
        inventory.setItem(13,mainStats);


        List<String> damageStatsList = List.of(
                Tools.colorText("&fФаза &4огня " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.FIRE_DAMAGE))),
                Tools.colorText("&fФаза &6шока " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.SHOCK_DAMAGE))),
                Tools.colorText("&2Мораль " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.MORALE_DAMAGE)))
        );

        ItemStack damageStats = Tools.createCountryStat("Модификаторы урона", damageStatsList,"damageStats");
        inventory.setItem(12,damageStats);

        List<String> resistStatsList = List.of(
                Tools.colorText("&fФаза &4огня " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.FIRE_RESIST))),
                Tools.colorText("&fФаза &6шока " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.SHOCK_RESIST))),
                Tools.colorText("&2Мораль " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.MORALE_RESIST)))
        );

        ItemStack resistStats = Tools.createCountryStat("Модификаторы сопротивления", resistStatsList,"resistStats");
        inventory.setItem(14,resistStats);

        List<String> armyCostList = List.of(
                Tools.colorText("&fПехота(&d" +  player.getTroops().get("inf").size() + "&f) &4" + player.getInfExpense() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.INF_COST), true) ),
                Tools.colorText("&fКавалерия(&d" +  player.getTroops().get("cav").size() + "&f) &4" + player.getCavExpense() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.CAV_COST), true) ),
                Tools.colorText("&fАртиллерия(&d" +  player.getTroops().get("art").size() + "&f) &4" + player.getArtExpense() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.ART_COST), true) ),
                Tools.colorText("&fВсего &4" + player.getArmyExpense() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD), true))
        );

        ItemStack armyCost = Tools.createCountryStat("Содержание армии",armyCostList,"armyCost");
        inventory.setItem(16,armyCost);

        List<String> armyStatsList = List.of(
                Tools.colorText("&fПехота " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.INF_COMBAT_ABILITY))),
                Tools.colorText("&fКавалерия " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.CAV_COMBAT_ABILITY))),
                Tools.colorText("&fАртиллерия " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.ART_COMBAT_ABILITY)))
        );

        ItemStack armyStats = Tools.createCountryStat("Боевая мощь войск",  armyStatsList,"armyStats");
        inventory.setItem(25,armyStats);



        if(!player.isWar() && !player.isLevies()) leviesList.addFirst(Tools.colorText( "&eДоступно только вовремя войны"));
        if(player.isLevies()) leviesList.addFirst(Tools.colorText( "&eОполчение поднято!"));
        ItemStack leviesStat = Tools.createCountryStat("Ополчение",  leviesList,"leviesStats");
        if(!player.isWar() && player.isLevies()) leviesStat = Tools.createCountryStat("Распустить ополчение",  leviesList,"leviesStats");
        if(player.isWar() && !player.isLevies()) leviesStat = Tools.createCountryStat("Поднять ополчение",  leviesList,"leviesStats");
        inventory.setItem(10,leviesStat);

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
            if (inventory.getItem(i+26) == null) {
                inventory.setItem(i+26, super.FILLER_GLASS);
            }
        }
        inventory.setItem(17, super.FILLER_GLASS);
        inventory.setItem(18, super.FILLER_GLASS);


        inventory.setItem(35,createBackItem());

//        inventory.setItem(0, tech);
//
//        inventory.setItem(1, idea);
//
//        inventory.setItem(4, build);
//
//        inventory.setItem(7, eco);
//        inventory.setItem(8, war);


    }
}
