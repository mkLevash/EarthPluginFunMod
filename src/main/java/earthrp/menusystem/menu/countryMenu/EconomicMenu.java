package earthrp.menusystem.menu.countryMenu;

import earthrp.customEnums.EPlayerTech;
import earthrp.menusystem.menu.MainMenu;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class EconomicMenu extends Menu {

    public EconomicMenu(MenuUtility menuUtility) {super(menuUtility);}
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
        if (item!=null && item.getItemMeta().getPersistentDataContainer().has(menuIdKey)){
            String statId = item.getItemMeta().getPersistentDataContainer().get(menuIdKey, PersistentDataType.STRING);
            switch (statId){

                case "debt" ->{
                    if(player.canDebt()){
                        int size = player.getDebtSize();
                        ItemStack mora = Tools.createMora(size);
                        UUID debtId = UUID.randomUUID();
                        player.getData().getDebtMap().put(debtId,size);
                        player.getData().getInterestMap().put(debtId,player.getInterest());
                        player.addAttribute(EPlayerAttribute.INFLATION,0.1);
                        p.getInventory().addItem(mora);
                    }else {
                        p.sendMessage("Вы взяли максимальное кол-во займов");
                    }

                }

                case "unDebt" -> {
                    p.closeInventory();
                    new DebtsMenu(menuUtility).open();
                    return;
                }

                case "inflation" ->{
                    if(player.getAttribute(EPlayerAttribute.POLIT_BALANCE)>=1 && player.getAttribute(EPlayerAttribute.INFLATION) > 0){
                        player.addAttribute(EPlayerAttribute.POLIT_BALANCE,-1);
                        player.addAttribute(EPlayerAttribute.INFLATION,-2);
                        if(player.getAttribute(EPlayerAttribute.INFLATION)<0) player.setAttribute(EPlayerAttribute.INFLATION,0);
                    }else{
                        p.sendMessage("Недостаточно £");
                    }
                }


            }

        }else if(item!=null && item.getType().equals(Material.BARRIER)){
            e.getWhoClicked().closeInventory();
            new MainMenu(menuUtility).open();
            return;
        }

        inventory.clear();
        setMenuItems();


    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        List<String> incomeList = List.of(
                "Налоги("+ player.getTaxModColor() +"): <green>" + player.getTaxIncome(),
                "Торговля("+player.getTradeModColor()+"): <green>" + player.getTradeIncome()
        );
        ItemStack income = makeItem("Доходы <green>" + player.getIncome() + "<white>$","income","income",incomeList);
        inventory.setItem(20,income);

        List<String> expenseList = new ArrayList<>(Arrays.asList(
                "Содержание регуляров("+player.getArmyMaintenanceColor()+") <red>" + player.getLandArmyExpense(),
                "Содержание наёмников("+player.getMercMaintenanceColor()+") <red>" + player.getMercExpense(),
                "Проценты по долгам <red>" + player.getDebtExpense(),
                "Выплаты сюзерену <red>" + player.getTribute()
        ));
        ItemStack expense = makeItem("Расходы <red>" + player.getExpense() + "<white>$","expense","expense",expenseList);
        inventory.setItem(21,expense);

        List<String> inflationLore = new ArrayList<>();
        inflationLore.add("<yellow>ЛКМ<white> - чтобы снизить на 2% за 1£");
        ItemStack inflation = makeItem("Инфляция - <yellow>" + player.getAttribute(EPlayerAttribute.INFLATION) + "%","inflation","inflation",inflationLore);
        inventory.setItem(22,inflation);


        List<String> debtList = List.of(
                "Размер 1 долга: " + player.getDebtSize() + "$",
                "Процентная ставка <yellow>" + (int) (player.getInterest() * 100) + "%",
                "Каждый долг увеличивает инфляцию на <yellow>0.1% в день"
        );
        ItemStack debt = makeItem("Взять долг","debt","debt",debtList);
        if(player.getTech(EPlayerTech.BANK_BASE)){
            inventory.setItem(23,debt);
        }

        List<String> unDebtList = List.of(
                "У вас " + player.getData().getDebtMap().size() + " долг на сумму <red>" + player.getDebt()
        );
        ItemStack unDebt = makeItem("Выплатить долги","unDebt","unDebt",unDebtList);
        inventory.setItem(24,unDebt);

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
}
