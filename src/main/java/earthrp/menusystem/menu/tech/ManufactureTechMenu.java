package earthrp.menusystem.menu.tech;

import earthrp.customObjects.EPlayer;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TechnologyMenu;
import earthrp.tools.Tools;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import static earthrp.tools.PDCKeys.techIdKey;

public class ManufactureTechMenu extends Menu {

    public ManufactureTechMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();

    @Override
    public String getMenuName() {
        return "Мануфактуры";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item != null) {
            p.closeInventory();
            if (item.getType().equals(Material.BARRIER)) {
                new TechnologyMenu(menuUtility).open();
            } else if (item.getItemMeta().getPersistentDataContainer().has(techIdKey)) {
                Tools.techProcess(item, player);
                new ManufactureTechMenu(menuUtility).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        // Эпоха 3: Мануфактуры
        
        // === ЦЕНТР - Ключевая технология ===
        ItemStack manufacture = makeTech(Material.CRAFTING_TABLE, "manufacture", player);
        inventory.setItem(22, manufacture); // Центр
        
        // === ГРАЖДАНСКИЕ ТЕХНОЛОГИИ (слева) ===
        // Строительство
        ItemStack fortress = makeTech(Material.STONE_BRICKS, "fortress", player);
        // Администрация
        ItemStack constitution = makeTech(Material.WRITABLE_BOOK, "constitution", player);
        ItemStack bureaucracyUp = makeTech(Material.WRITABLE_BOOK, "bureaucracyUp", player);
        // Наука/Образование
        ItemStack enlightenment = makeTech(Material.BOOK, "enlightenment", player);
        
        inventory.setItem(10, fortress);
        inventory.setItem(11, constitution);
        inventory.setItem(12, bureaucracyUp);
        inventory.setItem(13, enlightenment);
        
        // === ВОЕННЫЕ ТЕХНОЛОГИИ (справа) ===
        // Пехота
        ItemStack lineInfantry = makeTech(Material.CROSSBOW, "lineInfantry", player);
        // Кавалерия
        ItemStack cav5 = makeTech(Material.DIAMOND_HORSE_ARMOR, "cav5", player);
        // Артиллерия
        ItemStack cartridges = makeTech(Material.PAPER, "cartridges", player);
        
        inventory.setItem(28, lineInfantry);
        inventory.setItem(29, cav5);
        inventory.setItem(30, cartridges);

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 44);
        }
        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);

        inventory.setItem(49, createBackItem());
    }
}
