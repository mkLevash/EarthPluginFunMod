package earthrp.menusystem.menu.tech;

import earthrp.customObjects.EPlayer;
import earthrp.files.CustomConfig;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TechnologyMenu;
import earthrp.tools.Tools;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static earthrp.tools.PDCKeys.techIdKey;

public class TribalTechMenu extends Menu {

    public TribalTechMenu(MenuUtility menuUtility)  {
        super(menuUtility);

    }
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();



    @Override
    public String getMenuName() {
        return "Племенные технологии";
    }

    @Override
    public int getSlots() {
        return 54;
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
                new TribalTechMenu(menuUtility).open();
            }
        }


    }

    @Override
    public void setMenuItems() {
        // Эпоха 0: Племя
        List<String> tribalLore = CustomConfig.get().getStringList("tech.lore.tribal");
        ItemStack tribal = makeItem("<red>Племенной строй","","tribal", tribalLore);
        inventory.setItem(0,tribal);
        // === ЦЕНТР - Ключевые технологии ===
        ItemStack mining = makeTech("mining",player,"mining");
        ItemStack building = makeTech("building",player, "building");

        inventory.setItem(21, mining); // Центр
        inventory.setItem(30, building);
        
        // === ЭКОНОМИКА (слева) ===
        ItemStack livestock = makeTech(Material.LEATHER,"livestock",player);
        ItemStack irrigation = makeTech(Material.WHEAT,"irrigation",player);
        inventory.setItem(22, livestock);
        inventory.setItem(23, irrigation);
        
        // === ТОРГОВЛЯ И МОРСКОЕ ДЕЛО (справа) ===
        ItemStack shipping = makeTech(Material.OAK_BOAT,"shipping",player);
        inventory.setItem(31, shipping);
        
        // === НАУКА (низ) ===
        ItemStack writing = makeTech(Material.BOOK,"writing",player);
        inventory.setItem(32, writing);

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 44);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);
        fillIfEmpty(35);
        fillIfEmpty(36);

        inventory.setItem(49, createBackItem());
    }
}
