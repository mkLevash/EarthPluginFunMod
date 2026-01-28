package earthrp.menusystem.menu.countryMenu;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static earthrp.tools.PDCKeys.*;

public class EconomicMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    public EconomicMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
        if (player == null){
            player = db.getPlayer(menuUtility.getOwner().getUniqueId());
        }
    }
    EPlayer player = menuUtility.getPlayer();
    @Override
    public String getMenuName() {
        return "Главное меню";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if (item!=null && item.getItemMeta().getPersistentDataContainer().has(statIdKey)){
            e.getWhoClicked().closeInventory();
            String statId = item.getItemMeta().getPersistentDataContainer().get(statIdKey, PersistentDataType.STRING);
            boolean oldMenu = true;
            switch (statId){

                case "debt" ->{
                    ItemStack mora = Tools.createMora(player.getOneDebt());
                    String path = "debt."+player.getDisplayName() + ".lvl"+player.getDebtLvl();
                    CustomConfig.set(path,CustomConfig.get().getInt(path)+1);
                    e.getWhoClicked().getInventory().addItem(mora);
                }

                case "unDebt" -> {
                    oldMenu = false;
                    new DebtsMenu(menuUtility,earthPlugin).open();

                }


            }
            if(oldMenu) new EconomicMenu(menuUtility,earthPlugin).open();

        }else if(item!=null && item.getType().equals(Material.BARRIER)){
            e.getWhoClicked().closeInventory();
            new CountryMenu(menuUtility, this.earthPlugin).open();
        }


    }

    @Override
    public void setMenuItems() {

        List<String> incomeList = List.of(
                Tools.colorText("&fНалоги: &a" + player.getTaxIncome()  + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.TAX_MOD))),
                Tools.colorText("&fПроизводство: &a" + player.getProdIncome()  + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.PROD_MOD))),
                Tools.colorText("&fТорговля: &a" + player.getTradeIncome()  + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.TRADE_MOD)))

        );
        ItemStack income = Tools.createCountryStat("Доходы",incomeList,"income");
        inventory.setItem(11,income);

        List<String> expenseList = new ArrayList<>(Arrays.asList(
                Tools.colorText("&fВойска &c" + player.getArmyExpense() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.ARMY_EXPENSE_MOD), true)),
                Tools.colorText("&fПроценты &c" + player.getDebtExpense()),
                Tools.colorText("&fДань &c" + player.getTribute() + " &f| " + Tools.getColorMod(player.getAttribute(EPlayerAttribute.TRIBUTE_MOD),true))


        ));
        ItemStack expense = Tools.createCountryStat("Расходы",expenseList,"expense");
        inventory.setItem(12,expense);


        List<String> debtList = List.of(
                Tools.colorText("&fРазмер 1 долга: " + player.getOneDebt())
        );
        ItemStack debt = Tools.createCountryStat("Взять долг",debtList,"debt");
        inventory.setItem(14,debt);

        List<String> unDebtList = List.of(
                Tools.colorText("&fВаш долг &c" + player.getDebt())
        );
        ItemStack unDebt = Tools.createCountryStat("Выплатить долг",unDebtList,"unDebt");
        inventory.setItem(15,unDebt);

        for (int i = 0; i < 10; i++) {
            if (inventory.getItem(i) == null) {
                inventory.setItem(i, super.FILLER_GLASS);
            }
            if (inventory.getItem(i+17) == null) {
                inventory.setItem(i+17, super.FILLER_GLASS);
            }
        }

        ItemStack close = new ItemStack(Material.BARRIER, 1);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Закрыть");
        close.setItemMeta(closeMeta);

        inventory.setItem(26,close);

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
