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

import static earthrp.tools.PDCKeys.techIdKey;

public class CraftTechMenu extends Menu {


    public CraftTechMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();




    @Override
    public String getMenuName() {
        return "Ремесленные технологии";
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
                new CraftTechMenu(menuUtility).open();
            }
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        // Эпоха 3: Мануфактуры
        ItemStack manufacture = makeTech(Material.CRAFTING_TABLE, "manufacture", player);


        inventory.setItem(13, manufacture);

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 17);
        }

        inventory.setItem(22, createBackItem());

    }
}
