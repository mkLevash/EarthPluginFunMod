package earthrp.menusystem.menu.countryMenu;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Building;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.files.CustomConfig;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

import static earthrp.tools.Tools.*;
import static earthrp.tools.PDCKeys.*;

public class DebtsMenu extends PaginatedMenu {

    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();

    public DebtsMenu(MenuUtility menuUtility) {
        super(menuUtility);
        int[] debtAmount = player.getDebts();
        for (int i=0; i<3;i++) {
            for (int j = 0; j < debtAmount[i]; j++) {
                debts.add(Tools.createDebtItem(i));
            }
        }
    }
    List<ItemStack> debts = new ArrayList<>();





    @Override
    public String getMenuName() {
        return "Выбор рынка. Страница "+page;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if (item != null ) {
            switch (item.getType()){
                case BARRIER -> {
                    new EconomicMenu(menuUtility).open();
                }
                case DARK_OAK_BUTTON -> {
                    if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                        if (page == 0){
                            p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                        }else{
                            page = page - 1;
                            super.open();
                        }
                    }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                        if (!((index + 1) > player.getDebtExpense())){
                            page = page + 1;
                            super.open();
                        }else{
                            p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                        }
                    }
                }
                case PAPER -> {
                    p.closeInventory();
                    PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                    int debtSize = data.get(debtSizeKey,PersistentDataType.INTEGER);
                    if(debtSize<=player.getAttribute(EPlayerAttribute.TREASURY)){
                        int lvl = data.get(debtLvlKey,PersistentDataType.INTEGER);
                        String path = "debt."+player.getDisplayName()+".lvl"+lvl;
                        CustomConfig.set(path,CustomConfig.get().getInt(path)-1);
                        player.addAttribute(EPlayerAttribute.TREASURY, -debtSize);
                    }
                    new DebtsMenu(menuUtility).open();


                }
                case MAP ->{
                    p.closeInventory();
                    if(player.getAttribute(EPlayerAttribute.TREASURY)>=player.getDebt()){
                        player.addAttribute(EPlayerAttribute.TREASURY, -player.getDebt());
                        String path = "debt."+player.getDisplayName()+".lvl";
                        for (int i = 0; i < 3; i++) {
                            CustomConfig.set(path+i,0);
                        }
                        new CountryMenu(menuUtility).open();
                    }else{
                        new DebtsMenu(menuUtility).open();
                    }

                }
            }
        }


    }

    @Override
    public void setMenuItems() {
        addMenuBorder();
        ItemStack border = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        inventory.setItem(0,border);
        //The thing you will be looping through to place items
        List<String> allDebtsLore = List.of(
                Tools.colorText("&fОбщий долг: &c" + player.getDebt()));
        ItemStack allDebts = Tools.createItem(Material.MAP,"Выплатить все долги",allDebtsLore);
        inventory.setItem(4,allDebts);


        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;

            if(index >= debts.size()) break;
            if (debts.get(index) != null){
                inventory.addItem(debts.get(index));

            }
        }



    }
}
