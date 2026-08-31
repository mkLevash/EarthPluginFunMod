package earthrp.menusystem.menu.tech;

import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TechnologyMenu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static earthrp.tools.PDCKeys.*;

public class EcoTechMenu extends Menu {

    public EcoTechMenu(MenuUtility menuUtility)  {
        super(menuUtility);

    }
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();



    @Override
    public String getMenuName() {
        return "Экономические технологии";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if(item!=null){
            p.closeInventory();
            if(item.getType().equals(Material.BARRIER)){
                new TechnologyMenu(menuUtility).open();
            }else if(item.getItemMeta().getPersistentDataContainer().has(techIdKey)){
                Tools.techProcess(item,player);
                new EcoTechMenu(menuUtility).open();
            }
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();



        ItemStack bank = makeTech( Material.CHEST,"bankBase",player );
        ItemStack bankUp = makeTech(Material.ENDER_CHEST,"bankUp",player);
        ItemStack trade = makeTech(Material.GOLD_INGOT,"trade",player, "moraIngot");
        ItemStack shipping = makeTech(Material.EGG,"shipping",player,"ship");
        ItemStack railroad = makeTech(Material.RAIL,"railroad",player);

        inventory.setItem(11, trade);

        inventory.setItem(12, shipping);

        inventory.setItem(13, bank);

        inventory.setItem(14, bankUp);

        inventory.setItem(15, railroad);


        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 17);
        }


        inventory.setItem(22,createBackItem());

    }
}
