package earthrp.menusystem.menu.markets;

import earthrp.Earth;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.DeleteConfirmMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class LandHubMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    Town town = menuUtility.getTown();
    public LandHubMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
    }

    @Override
    public String getMenuName() {
        return "Рынок " + town.getName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        switch (e.getCurrentItem().getType()){


            case BOOK-> {

                e.getWhoClicked().closeInventory();
                new TradeMenu(menuUtility,this.earthPlugin).open();

            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                //menuUtility.setDeleteBuilding(market);
                new DeleteConfirmMenu(menuUtility,this.earthPlugin).open();
            }
        }

    }

    @Override
    public void setMenuItems() {


        ItemStack stats = new ItemStack(Material.BOOK);
        ItemMeta statsMeta = stats.getItemMeta();
        statsMeta.setDisplayName(ChatColor.AQUA + "Основная Информация о рынке");
        statsMeta.setLore(List.of(
                ChatColor.translateAlternateColorCodes('&', "&fБонус рынка: &a" +  "&f%"),
                ChatColor.translateAlternateColorCodes('&', "&fКоличество товаров: " ),
                ChatColor.translateAlternateColorCodes('&', "&dДоход: &a" )
                )
        );
        stats.setItemMeta(statsMeta);

        ItemStack reusable = new ItemStack(Material.VILLAGER_SPAWN_EGG, 1);
        ItemMeta reusableMeta = reusable.getItemMeta();
        reusableMeta.setDisplayName(ChatColor.RED + "Военные");
        reusable.setItemMeta(reusableMeta);

        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.RED + "BACK");
        next.setItemMeta(nextMeta);




        inventory.setItem(4, stats);
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        inventory.setItem(8, next);

    }
}
