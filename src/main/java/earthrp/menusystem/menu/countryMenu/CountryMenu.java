package earthrp.menusystem.menu.countryMenu;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.Main;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static earthrp.tools.PDCKeys.*;

public class CountryMenu extends Menu {

    public CountryMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }
    EPlayer player = menuUtility.getPlayer();
    Player p = menuUtility.getOwner();
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
            switch (statId){
                case "economicStat" ->{
                    p.closeInventory();
                    new EconomicMenu(menuUtility).open();
                }


            }

        }else if(item!=null && item.getType().equals(Material.BARRIER)){
            p.closeInventory();
            new Main(menuUtility).open();
        }


    }

    @Override
    public void setMenuItems() {

//        List<String> incomeList = List.of(
//                Tools.colorText("&fНалоги: &a" + player.getTaxIncome()  + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.TAX_MOD))),
//                Tools.colorText("&fПроизводство: &a" + player.getProdIncome()  + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.PROD_MOD))),
//                Tools.colorText("&fТорговля: &a" + player.getTradeIncome()  + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.TRADE_MOD)))
//
//        );
//        ItemStack income = Tools.createCountryStat("Доходы",incomeList,"income");
//        inventory.setItem(10,income);
//
//        List<String> expenseList = List.of(
//                Tools.colorText("&fВойска &c" + player.getArmyExpense() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD), true)),
//                Tools.colorText("&fПроценты &c" + player.getDebtExpense())
//
//        );
//        ItemStack expense = Tools.createCountryStat("Расходы",expenseList,"expense");
//        inventory.setItem(11,expense);
//
//
//        List<String> debtList = List.of(
//                Tools.colorText("&fРазмер 1 долга: " + player.getOneDebt())
//        );
//        ItemStack debt = Tools.createCountryStat("Взять долг",debtList,"debt");
//        inventory.setItem(19,debt);
//
//        List<String> unDebtList = List.of(
//                Tools.colorText("&fВаш долг &c" + player.getDebt())
//        );
//        ItemStack unDebt = Tools.createCountryStat("Выплатить долг",unDebtList,"unDebt");
//        inventory.setItem(20,unDebt);

        Set<Town> towns = player.getTowns();
        List<String> mainStatsList = List.of(
                Tools.colorText("&fГорода &6"+towns.size()),
                Tools.colorText("&fНаселение &7"+player.getPeople()),
                Tools.colorText("&fРеваншизм &c" + (int) player.getAttribute(EPlayerAttribute.REVANCHISM)),
                Tools.colorText("&fСтабильность &b" + (int) player.getAttribute(EPlayerAttribute.STABILITY)),
                Tools.colorText("&fПоддержка войны&fस&8" + (int) player.getAttribute(EPlayerAttribute.WAR_SUPPORT)),
                Tools.colorText("&fАдмин эффективность &d" + (int) player.getAttribute(EPlayerAttribute.ADMIN_EFFICIENCY))
                //Tools.colorText("&eНажмите для подробностей")
        );
        ItemStack mainStats = Tools.createCountryStat("Основная статистика",mainStatsList,"mainStats");
        inventory.setItem(13,mainStats);


        List<String> ecnomicList = List.of(
                Tools.colorText("&fКазна &6" + (long) player.getAttribute(EPlayerAttribute.TREASURY)),
                Tools.colorText("&fБаланс " + Tools.getColorMod(Tools.getBalance(player),false,true)),
                Tools.colorText("&fКоррупцияখ" + (int) player.getAttribute(EPlayerAttribute.CORRUPTION)),
                Tools.colorText("&fИнфляция &e" + (int) player.getAttribute(EPlayerAttribute.INFLATION) + "&f%" + " | " + Tools.getColorMod((int) player.getAttribute(EPlayerAttribute.INFLATION_REDUCE), true,true)),
                Tools.colorText("&eНажмите для подробностей")
        );
        ItemStack economicStat = Tools.createCountryStat("Экономика",ecnomicList,"economicStat");
        inventory.setItem(22,economicStat);

        List<String> politList = List.of(
                Tools.colorText("&fБаланс &3" + (int) player.getAttribute(EPlayerAttribute.POLIT_BALANCE)),
                Tools.colorText("&fПрирост &2" + player.getPolitIncome() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.POLIT_INCOME_MOD))),
                Tools.colorText("&fМаксимум &e" + player.getMaxPolit() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.POLIT_MAX_MOD)))
        );
        ItemStack politStats = Tools.createCountryStat("Политическая Власть£",politList,"politStats");
        inventory.setItem(10,politStats);

        List<String> townStatsList = new ArrayList<>();
        if (towns != null){
            for(Town t:towns){
                townStatsList.add(Tools.colorText("&f"+t.getName() + ": &2" + t.getIncome() + "&f$" + t.getPeople() + "ह"));
            }
        }
        ItemStack townStats = Tools.createCountryStat("Статистика по городам",townStatsList,"townStats");
        //inventory.setItem(12,townStats);

        List<String> techStatsList = List.of(
                Tools.colorText("&fБалансૹ " + (int) player.getAttribute(EPlayerAttribute.OI_BALANCE)),
                Tools.colorText("&fПриростૹ " + player.getOiIncome()),
                Tools.colorText("&fПотраченоૹ " + (int) player.getAttribute(EPlayerAttribute.OI_SPENT))
        );
        ItemStack techStats = Tools.createCountryStat("Технологии",techStatsList,"techStats");
        inventory.setItem(16,techStats);

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 26);
        }
        fillIfEmpty(17);
        fillIfEmpty(18);


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
