package earthrp.menusystem.menu;

import earthrp.customEnums.EarthItem;
import earthrp.customObjects.Town;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.PaginatedMenu;
import earthrp.tools.Tools;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TownItemsMenu extends PaginatedMenu {
    public TownItemsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    private Town town = menuUtility.getTown();
    private final Map<EarthItem, Long> townItems = town.getData().getItems();
    @Override
    public String getMenuName() {
        return "";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if(item != null){
            Player p = (Player) e.getWhoClicked();
            if(item.getType().equals(Material.BARRIER)){
                p.closeInventory();
                new TownsMenu(menuUtility).open();
            }
            EarthItem ti = EarthItem.fromString(item.getType().toString());

            if(!item.getType().equals(Material.ICE) && ti!=null){

                int rawSlot = e.getRawSlot();
                int topSize = e.getView().getTopInventory().getSize();
                boolean success = false;
                if (rawSlot < topSize) {
                    // Логика изъятия из склада

                    int amount = 0;

                    if (e.isShiftClick()) {
                        if (e.isLeftClick() && town.getItem(ti) >= 10) { amount = 10; }
                        else if (e.isRightClick() && town.getItem(ti) >= 100) { amount = 100; }
                    } else if (e.isLeftClick() && town.getItem(ti) >= 1) {
                        amount = 1;
                    }

                    if (amount > 0) {
                        town.addItem(ti, -amount);
                        Map<Integer, ItemStack> overflow = p.getInventory().addItem(new ItemStack(item.getType(), amount));

                        if (!overflow.isEmpty()) {
                            // Если карта не пуста, значит, часть предметов не влезла
                            for (ItemStack remaining : overflow.values()) {
                                // Спавним не поместившиеся предметы на землю рядом с игроком
                                p.getWorld().dropItemNaturally(p.getLocation(), remaining);
                            }
                            p.sendMessage("Ваш инвентарь полон! Часть предметов упала на землю.");
                        }
                        success = true;
                    }
                } else {
                    // Логика внесения на склад
                    long space = town.getAvailableSpace();
                    int amountToAdd = Math.toIntExact(Math.min(item.getAmount(), space));

                    town.addItem(ti, amountToAdd);
                    item.setAmount(item.getAmount() - amountToAdd);
                    success = true;

                }

                if (success) {
                    inventory.clear();
                    this.setMenuItems();
                }
            }


        }



    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        addMenuBorder();
        inventory.setItem(0,Tools.createItem(Material.CHEST,"Доступное место: " + town.getItemAmount() + "/" + town.getItemMax(),null));

        List<ItemStack> items = new ArrayList<>();

        for(EarthItem i :townItems.keySet()){
            if(i==null) continue;
            if(townItems.get(i)==0) continue;
            List<String> lore = new ArrayList<>();
            long amount = townItems.get(i);
            if(i.getType()== EarthItem.ItemType.FOOD){
                long food = i.getFood() * amount;
                lore.add(amount + " = " + food + "इ");
            }else{
                lore.add(String.valueOf(amount));
            }
            items.add(Tools.createItem(i.getMaterial(),i.getDisplayName(),lore,i.getCustomModel()));
        }
        setItems(items);


    }
}
