package earthrp.menusystem.menu.countryMenu;

import earthrp.Earth;
import earthrp.customObjects.PlayerData;
import earthrp.database.ServerDatabase;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.MainMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.*;

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
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if(item!=null){
            if(item.getType().equals(Material.BARRIER)){
                p.closeInventory();
                new MainMenu(menuUtility).open();
                return;
            }
            if(item.getItemMeta().getPersistentDataContainer().has(menuIdKey)){
                String statId = item.getItemMeta().getPersistentDataContainer().get(menuIdKey, PersistentDataType.STRING);
                switch (statId){
                    case "modifiers"->{
                        e.getWhoClicked().closeInventory();
                        new ModifiersMenu(menuUtility).open();
                    }
                    case "stability" ->{
                        if(player.getAttribute(EPlayerAttribute.POLIT_BALANCE)>=player.getStabCost()){
                            player.addAttribute(EPlayerAttribute.POLIT_BALANCE,-player.getStabCost());
                            player.addAttribute(EPlayerAttribute.STABILITY,1);
                            inventory.clear();
                            setMenuItems();
                        }else{
                            p.sendMessage("Недостаточно £");
                        }
                    }
                    case "polit" ->{
                        if(player.getAttribute(EPlayerAttribute.POLIT_BALANCE)>0){
                            player.addAttribute(EPlayerAttribute.POLIT_BALANCE,-1);
                            List<String> lore = List.of("Потраченная политка ",String.valueOf(Earth.getInstance().getDatabase().getStatusDay()));
                            Map<Integer, ItemStack> overflow = p.getInventory().addItem(Tools.createItem(Material.ICE,"<aqua>Политическая власть",lore,"politPower"));

                            if (!overflow.isEmpty()) {
                                for (ItemStack remaining : overflow.values()) {
                                    p.getWorld().dropItemNaturally(p.getLocation(), remaining);
                                }
                                p.sendMessage("Ваш инвентарь полон! Часть предметов упала на землю.");
                            }
                            inventory.clear();
                            setMenuItems();
                        }
                    }
                    case "corruption" ->{
                        if(e.isLeftClick()){
                            if(player.getAttribute(EPlayerAttribute.POLIT_BALANCE)>=10 && player.getAttribute(EPlayerAttribute.CORRUPTION)>0){
                                player.addAttribute(EPlayerAttribute.POLIT_BALANCE,-10);
                                player.addAttribute(EPlayerAttribute.CORRUPTION,-1);
                                inventory.clear();
                                setMenuItems();
                            }else{
                                p.sendMessage("Недостаточно £");
                            }
                        }else if (e.isRightClick()){
                            if(player.getAttribute(EPlayerAttribute.CORRUPTION)<3){
                                player.addAttribute(EPlayerAttribute.TREASURY,player.getDebtSize()*5);
                                player.addAttribute(EPlayerAttribute.CORRUPTION,1);
                                inventory.clear();
                                setMenuItems();
                            }else{
                                p.sendMessage("Коррупция максимальна");
                            }
                        }
                    }
                }
            }
            int rawSlot = e.getRawSlot();
            int topSize = e.getView().getTopInventory().getSize();
            if (rawSlot > topSize) {
                if(item.getItemMeta().hasLore() && item.getItemMeta().lore().size() == 2 && item.getItemMeta().lore().get(1).equals(String.valueOf(Earth.getInstance().getDatabase().getStatusDay()))){
                    player.addAttribute(EPlayerAttribute.POLIT_BALANCE,1);
                    item.setAmount(0);
                }
            }
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();

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

//        Set<Town> towns = player.getTowns();
//        List<String> mainStatsList = List.of(
//                Tools.colorText("&fГорода &6"+towns.size()),
//                Tools.colorText("&fНаселение &7"+player.getPeople()),
//                Tools.colorText("&fРеваншизм &c" + (int) player.getAttribute(EPlayerAttribute.REVANCHISM)),
//                Tools.colorText("&fСтабильность &b" + (int) player.getAttribute(EPlayerAttribute.STABILITY)),
//                Tools.colorText("&fПоддержка войны&fस&8" + (int) player.getAttribute(EPlayerAttribute.WAR_SUPPORT)),
//                Tools.colorText("&fАдмин эффективность &d" + (int) player.getAttribute(EPlayerAttribute.ADMIN_EFFICIENCY))
//                //Tools.colorText("&eНажмите для подробностей")
//        );
//        ItemStack mainStats = Tools.createCountryStat("Основная статистика",mainStatsList,"mainStats");
//        inventory.setItem(13,mainStats);
//
//
//        List<String> ecnomicList = List.of(
//                Tools.colorText("&fКазна &6" + (long) player.getAttribute(EPlayerAttribute.TREASURY)),
//                Tools.colorText("&fБаланс " + Tools.getColorMod(player.getBalance(),false,true)),
//                Tools.colorText("&fКоррупцияখ" + (int) player.getAttribute(EPlayerAttribute.CORRUPTION)),
//                Tools.colorText("&fИнфляция &e" + (int) player.getAttribute(EPlayerAttribute.INFLATION) + "&f%" + " | " + Tools.getColorMod((int) player.getAttribute(EPlayerAttribute.INFLATION_REDUCE), true,true)),
//                Tools.colorText("&eНажмите для подробностей")
//        );
//        ItemStack economicStat = Tools.createCountryStat("Экономика",ecnomicList,"economicStat");
//        inventory.setItem(22,economicStat);




        List<String> stabilityLore = new ArrayList<>();
        stabilityLore.add("Эффекты:");
        int stab = (int) player.getAttribute(EPlayerAttribute.STABILITY);
        String color1;
        String color2;
        if(stab<0){
            color1 = " <red>";
            color2 = " <red>+";

        }else if(stab > 0) {
            color1 = " <green>+";
            color2 = " <green>";
        }else{
            color1 = "";
            color2 = "";
        }

        if(stab!= 0){
            stabilityLore.add("Налоги:"+color1 + (10 * stab ) + "%");
            stabilityLore.add("Эффективность торговли:"+color1 + (5 * stab ) + "%");
            stabilityLore.add("Процент долга:"+color2 + (-2 * stab ) + "%");
        }else {
            stabilityLore.add("<gray>Эффектов нет");
        }
        stabilityLore.add("<yellow>ЛКМ <white>- чтобы повысить за " + player.getStabCost() + "£");

        ItemStack stability = makeItem("Стабильность " + stab,"stability","stability", stabilityLore);
        inventory.setItem(15,stability);

        List<String> warSupLore = new ArrayList<>();
        warSupLore.add("Эффекты:");
        int ws = (int) player.getAttribute(EPlayerAttribute.WAR_SUPPORT);
        String color;
        if(stab<0){
            color = " <red>";

        }else if(stab > 0) {
            color = " <green>+";
        }else{
            color = "<gray>";
        }

        if(ws!= 0){
            warSupLore.add("Восстановлениеऴ"+color + (10 * ws ) + "%");
        }else {
            warSupLore.add("<gray>Эффектов нет");
        }

        ItemStack warSup = makeItem("Поддержка войны " + ws,"warSup","war_support", warSupLore);
        inventory.setItem(16,warSup);

        List<String> politList = List.of(
                "Прирост <green>" + player.getPolitIncome(),
                "Максимум <yellow>" + player.getMaxPolit(),
                "<yellow>ЛКМ <white>- чтобы потратить"
        );
        ItemStack politStats = makeItem("Политическая Власть " + (int) player.getAttribute(EPlayerAttribute.POLIT_BALANCE),"polit","politPower", politList);
        inventory.setItem(24,politStats);


        List<String> revanchismList = new ArrayList<>();
        int rev = (int) player.getAttribute(EPlayerAttribute.REVANCHISM);
        double revMod = player.getAttribute(EPlayerAttribute.REVANCHISM_MOD);
        revanchismList.add("Эффекты:");
        PlayerData data = player.getData();
        if(rev!=0){
            revanchismList.add("Мораль <green>+" + (int) (rev * (10 * revMod ) ) + "%");
            revanchismList.add("Дисциплина <green>+" + (int) (rev * (5 * revMod ) ) + "%");
            if(data.isRevanchism0()){
                revanchismList.add("Максимумऴ<green>+" + (int) (rev * (5 * revMod ) ) + "k");
            }
            if(data.isRevanchism1()){
                revanchismList.add("Стоимость воен. зданий <green>" + (int) (rev * (-5 * revMod ) ) + "%");
            }
            if(data.isRevanchism2()){
                revanchismList.add("Содержание войск <green>" + (int) (rev * (-5 * revMod ) ) + "%");
                revanchismList.add("Налоги <green>" + (int) (rev * (5 * revMod ) ) + "%");
            }
            if(data.isRevanchism3()){
                revanchismList.add("Прирост традиций от битв <green>" + (int) (rev * (10 * revMod ) ) + "%");
            }
        }else{
            revanchismList.add("<gray>Эффектов нет");
        }
            ItemStack revanchism = makeItem("Реваншизм " + rev,"revanchism","revanchism", revanchismList);
        inventory.setItem(25,revanchism);


        List<String> corruptionLore = new ArrayList<>();
        int cor = (int) player.getAttribute(EPlayerAttribute.CORRUPTION);
        corruptionLore.add("Эффекты:");
        if(cor!=0){
            corruptionLore.add("Баланс <red>" + (cor * -10 ) + "%");
        }else{
            corruptionLore.add("<green>Эффектов нет");
        }
        corruptionLore.add("<yellow>ЛКМ <white>- чтобы понизить за 10£");
        corruptionLore.add("<yellow>ПКМ <white>- чтобы повысить и получить "+(player.getDebtSize()*5)+"$");
        ItemStack corruption = makeItem("Коррупция " + cor,"corruption","corruption", corruptionLore);
        inventory.setItem(33,corruption);

        List<String> adminLore = new ArrayList<>();
        int adm = (int) player.getAttribute(EPlayerAttribute.ADMIN_EFFICIENCY);
        ItemStack admin = makeItem("Административная эффективность " + adm,"admin","administrative_efficiency", adminLore);
        inventory.setItem(34,admin);


        List<String> diplomacyList = getDiploLore();

        ItemStack diplomacy = makeItem("Дипломатия","politStats","diplomacy",diplomacyList);
        inventory.setItem(19,diplomacy);

        ItemStack modifiers = makeItem(Material.BOOK,"Модификаторы","modifiers");
        inventory.setItem(20,modifiers);

//        List<String> townStatsList = new ArrayList<>();
//        for(UUID id :player.getData().getEnemies()){
//            townStatsList.add(Earth.getInstance().getDatabase().getPlayer(id).getCountryName());
//        }
//        ItemStack townStats = Tools.createCountryStat("Война",townStatsList,"townStats");
//        inventory.setItem(19,townStats);
//
//        List<String> techStatsList = List.of(
//                Tools.colorText("&fБалансૹ " + (int) player.getAttribute(EPlayerAttribute.OI_BALANCE)),
//                Tools.colorText("&fПриростૹ " + player.getOiIncome()),
//                Tools.colorText("&fПотраченоૹ " + (int) player.getAttribute(EPlayerAttribute.OI_SPENT))
//        );
//        ItemStack techStats = Tools.createCountryStat("Технологии",techStatsList,"techStats");
//        inventory.setItem(16,techStats);

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 35);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);


        inventory.setItem(40,createBackItem());

//        inventory.setItem(0, tech);
//
//        inventory.setItem(1, idea);
//
//        inventory.setItem(4, build);
//
//        inventory.setItem(7, eco);
//        inventory.setItem(8, war);


    }

    private @NonNull List<String> getDiploLore() {
        List<String> diplomacyList = new ArrayList<>();
        ServerDatabase db = Earth.getInstance().getDatabase();
        Map<UUID, Integer> truceMap = player.getData().getTruceMap();
        if(!truceMap.isEmpty()){
            diplomacyList.add("Перемирие:");
            for (UUID id : truceMap.keySet()){
                int dur = truceMap.get(id) - Earth.getInstance().getDatabase().getStatusDay();
                diplomacyList.add(" С <aqua>"+db.getPlayer(id).getDisplayName() + " <white>истекает через <green>"+ dur);
            }
        }
        Set<UUID> ally = player.getData().getAlly();

        if(!ally.isEmpty()){
            diplomacyList.add("Союзники:");
            for (UUID id : ally){
                diplomacyList.add(" <green>"+db.getPlayer(id).getDisplayName());
            }
        }

        Set<UUID> enemies = player.getData().getEnemies();
        if(!enemies.isEmpty()){
            diplomacyList.add("Враги:");
            for (UUID id : enemies){
                diplomacyList.add(" <red>"+db.getPlayer(id).getDisplayName());
            }
        }
        return diplomacyList;
    }


}
