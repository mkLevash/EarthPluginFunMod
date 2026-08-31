package earthrp.menusystem.menu.countryMenu;

import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.PaginatedMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class DebtsMenu extends PaginatedMenu {

    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();

    public DebtsMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }






    @Override
    public String getMenuName() {
        return "Долги. Страница "+page;
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
                        if (!((index + 1) > player.getDebtList().size())){
                            page = page + 1;
                            super.open();
                        }else{
                            p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                        }
                    }
                }
                case PAPER -> {
                    PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                    UUID debtId = UUID.fromString(data.get(debtIdKey,PersistentDataType.STRING));
                    double interest = 1.0 + player.getData().getInterestMap().get(debtId);
                    int debtSize = (int) Math.ceil(data.get(debtSizeKey,PersistentDataType.INTEGER) * interest);
                    if(debtSize<=player.getAttribute(EPlayerAttribute.TREASURY)){
                        player.addAttribute(EPlayerAttribute.TREASURY, -debtSize);

                        player.getData().getDebtMap().remove(debtId);
                    }
                }
                case MAP ->{
                    player.getData().getDebtMap().entrySet().removeIf(entry -> {
                        int debtSize = entry.getValue();
                        if (player.getAttribute(EPlayerAttribute.TREASURY) >= debtSize) {
                            player.addAttribute(EPlayerAttribute.TREASURY, -debtSize);
                            return true;
                        }
                        return false;
                    });
                }
                case PRISMARINE_CRYSTALS -> {
                    if (player.getAttribute(EPlayerAttribute.OI_BALANCE)>=10 && !player.getData().getDebtMap().isEmpty()){
                        player.addAttribute(EPlayerAttribute.OI_BALANCE,-10);
                        Iterator<UUID> iterator = player.getData().getDebtMap().keySet().iterator();
                        if (iterator.hasNext()) {
                            iterator.next();
                            iterator.remove();
                        }

                    }else{
                        p.sendMessage("недостаточно ૹ");
                    }
                }
            }
            inventory.clear();
            setMenuItems();
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        addMenuBorder();
        ItemStack border = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
        inventory.setItem(0,border);
        //The thing you will be looping through to place items
        List<String> allDebtsLore = List.of(
                Tools.colorText("&fОбщий долг: &c" + player.getDebt()));
        ItemStack allDebts = Tools.createItemLegacy(Material.MAP,"Выплатить все долги",allDebtsLore);
        inventory.setItem(4,allDebts);

        List<String> revisionLore = new ArrayList<>();
        revisionLore.add("Стоимость 10ૹ");
        revisionLore.add("Прощает 1 долг");
        ItemStack revision = makeItem(Material.PRISMARINE_CRYSTALS,"Провести ревизию",revisionLore);
        inventory.setItem(5,revision);

        List<ItemStack> debts = player.getDebtList();
        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;

            if(index >= debts.size()) break;
            if (debts.get(index) != null){
                inventory.addItem(debts.get(index));

            }
        }



    }
}
