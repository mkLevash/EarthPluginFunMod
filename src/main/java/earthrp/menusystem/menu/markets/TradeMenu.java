package earthrp.menusystem.menu.markets;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TownsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TradeMenu extends Menu {
    Town town = menuUtility.getTown();
    public TradeMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "Торговля " + town.getName();
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        switch (e.getCurrentItem().getType()){


            case BOOK-> {
                Player p = (Player) e.getWhoClicked();
                e.getWhoClicked().closeInventory();


            }

            case SADDLE->{
                e.getWhoClicked().closeInventory();
                new MarketSelectMenu(menuUtility).open();
                //new LoadingMenu(menuUtility,this.earthPlugin).open();
            }

            case BARRIER-> {
                e.getWhoClicked().closeInventory();
                new TownsMenu(menuUtility).open();

            }
        }

    }

    @Override
    public void setMenuItems() {
        ItemStack stats;
        if(town.isLandHub()){
            stats = new ItemStack(Material.BOOK);
            ItemMeta statsMeta = stats.getItemMeta();
            statsMeta.setDisplayName(ChatColor.AQUA + "Основная Информация о рынке");
            System.out.println("[Earth]town " + town.getTradeMod()*100.0);
            statsMeta.setLore(List.of(
                            Tools.colorText("&fБонус рынка:" + town.getColorTradeMod()),
                            Tools.colorText("&fКоличество товаров: " + town.getMarketGoods()),
                            Tools.colorText("&dДоход: &a" + town.getTradeIncome())
                    )
            );
            stats.setItemMeta(statsMeta);
        }else{
            stats = new ItemStack(Material.BELL);
            ItemMeta statsMeta = stats.getItemMeta();
            statsMeta.setDisplayName(ChatColor.RED + "В городе отсутствует рынок!");
            statsMeta.setLore(List.of(
                            ChatColor.translateAlternateColorCodes('&', "&fЦена товаров в городе: &a" + town.getLocalGoodsCost()),
                            ChatColor.translateAlternateColorCodes('&', "&fНажмите чтобы перенаправить торговлю")
                    )
            );
            stats.setItemMeta(statsMeta);
        }



        ItemStack tradeWay = Tools.createItem(Material.SADDLE,"Перенаправить торговлю",null);
        inventory.setItem(1,tradeWay);

        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.RED + "BACK");
        next.setItemMeta(nextMeta);




        inventory.setItem(0, stats);
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        inventory.setItem(26, next);

    }
}
