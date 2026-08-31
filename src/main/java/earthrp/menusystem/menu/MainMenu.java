package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.menu.countryMenu.ArmyMenuv2;
import earthrp.menusystem.menu.countryMenu.CountryMenu;
import earthrp.menusystem.menu.countryMenu.EconomicMenu;
import earthrp.tools.Tools;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.countryMenu.ArmyMenu;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class MainMenu extends Menu {
    public MainMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    EPlayer player = menuUtility.getPlayer();

    @Override
    public String getMenuName() {
        return "Главное меню";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item != null){
            e.getWhoClicked().closeInventory();
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if(data.has(menuIdKey)){
                switch (data.get(menuIdKey, PersistentDataType.STRING)){
                    case "tech" ->{
                        new TechnologyMenu(menuUtility).open();
                    }
                    case "buildings" ->{
                        new BuildingsMenu(menuUtility).open();
                    }
                    case "army" ->{
                        new ArmyMenuv2(menuUtility).open();
                    }
                    case "mainStats" ->{
                        new CountryMenu(menuUtility).open();
                    }
                    case "economicStat" ->{
                        new EconomicMenu(menuUtility).open();
                    }
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();


        List<String> techStatsList = List.of(
        );
        ItemStack tech = makeItem("Технологии","tech","menuTech",techStatsList);

        ItemStack build = makeItem("<gold>Здания","buildings","buildings");

        List<String> armyQualityList = List.of(
                "ᠩ" + Tools.getColorModComponent(player.getMoraleMod(),false,false) +
                        "ᠨ<aqua>" + player.getAttribute(EPlayerAttribute.TACTIC)+
                        "<white>ᠧ" + Tools.getColorModComponent(player.getAttribute(EPlayerAttribute.DISCIPLE),false,false)
        );

        ItemStack armyQuality = makeItem("Армия","army","armyQuality",armyQualityList);

//        List<String> armyList = List.of(
//                "Пехота(<light_purple>" +  player.getTroops().get("inf").size() + "<white>) <red>" + player.getInfExpense() + " <white>$ ",
//                "Кавалерия(<light_purple>" +  player.getTroops().get("cav").size() + "<white>) <red>" + player.getCavExpense() + " <white>$ ",
//                "Артиллерия(<light_purple>" +  player.getTroops().get("art").size() + "<white>) <red>" + player.getArtExpense() + " <white>$ ",
//                "<yellow>ЛКМ <white>- для подробностей"
//        );
//        ItemStack army = makeItem("Армия","army","menuArmyStat",armyList);



        Set<Town> towns = player.getTowns();
        List<String> mainStatsList = List.of(
                "ऄ" + (int) player.getAttribute(EPlayerAttribute.STABILITY) +
                        "स" + (int) player.getAttribute(EPlayerAttribute.WAR_SUPPORT) +
                        "£" + (int) player.getAttribute(EPlayerAttribute.POLIT_BALANCE) +
                        "आ" + (int) player.getAttribute(EPlayerAttribute.REVANCHISM) +
                        "अ" + player.getAdminEff() +
                        "খ" + (int) player.getAttribute(EPlayerAttribute.CORRUPTION)
        );
        ItemStack mainStats = makeItem("Государство","mainStats","mainStats",mainStatsList);


        List<String> ecnomicList = List.of(
                "Казна <gold>" + (long) player.getAttribute(EPlayerAttribute.TREASURY),
                "Доходы <green>" + player.getIncome(),
                "Расходы <red>" + player.getExpense()
                //"Баланс " + Tools.getColorModComponent(player.getBalance(),false,true),
                //"Инфляция <yellow>" + (int) player.getAttribute(EPlayerAttribute.INFLATION) + "<white>% | " + Tools.getColorModComponent((int) player.getAttribute(EPlayerAttribute.INFLATION_REDUCE), true,true),
                //"Города <green>"+towns.size(),
                //"Население <green>"+player.getPeople(),
        );
        ItemStack economicStat = makeItem("Экономика","economicStat","economic",ecnomicList);








        inventory.setItem(20,mainStats);
        inventory.setItem(21,economicStat);
        //inventory.setItem(21,diplomacy);
        inventory.setItem(22, tech);
        inventory.setItem(23, build);
        inventory.setItem(24, armyQuality);
        //inventory.setItem(25, army);






        //inventory.setItem(15, country);



        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 35);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);



    }
}
