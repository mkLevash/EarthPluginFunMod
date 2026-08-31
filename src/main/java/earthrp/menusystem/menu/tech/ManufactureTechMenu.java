package earthrp.menusystem.menu.tech;

import earthrp.customObjects.EPlayer;
import earthrp.configs.CustomConfig;
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
        inventory.clear();
        // Эпоха 3: Мануфактуры
        
        // === ЦЕНТР - Ключевая технология ===
        List<String> manufactureLore = CustomConfig.get().getStringList("tech.lore.manufacture");
        ItemStack manufacture = makeItem(Material.SMITHING_TABLE,"<light_purple>Мануфактуры", manufactureLore);
        inventory.setItem(0, manufacture); // Центр
        
        // === ГРАЖДАНСКИЕ ТЕХНОЛОГИИ (слева) ===
        // Строительство
        ItemStack fortress = makeTech(Material.STONE_BRICKS, "fortress", player);
        // Администрация
        ItemStack constitution = makeTech(Material.WRITABLE_BOOK, "constitution", player);
        ItemStack bureaucracyUp = makeTech(Material.WRITABLE_BOOK, "bureaucracyUp", player);
        // Наука/Образование
        ItemStack enlightenment = makeTech(Material.BOOK, "enlightenment", player);
        
        inventory.setItem(10, fortress);
        inventory.setItem(13, constitution);
        inventory.setItem(15, bureaucracyUp);
        inventory.setItem(14, enlightenment);
        
        // === ВОЕННЫЕ ТЕХНОЛОГИИ (справа) ===
        // Пехота
        ItemStack lineInfantry = makeTech(Material.CROSSBOW, "lineInfantry", player);
        // Кавалерия
        ItemStack cav5 = makeTech( "cav5", player,"cav5");
        ItemStack art3 = makeTech( "art3", player,"art3");
        // Артиллерия
        ItemStack inf5 = makeTech("inf5", player,"inf5");

        ItemStack impulseWarfare = makeTech(Material.NETHERITE_SWORD, "impulseWarfare", player);
        // Кавалерия
        ItemStack cav6 = makeTech( "cav6", player,"cav6");
        ItemStack art4 = makeTech( "art4", player,"art4");
        // Артиллерия
        ItemStack inf6 = makeTech("inf6", player,"inf6");
        
        inventory.setItem(23, lineInfantry);

        inventory.setItem(22, cav5);
        inventory.setItem(24, inf5);

        inventory.setItem(25, art3);

        inventory.setItem(32, impulseWarfare);

        inventory.setItem(31, cav6);
        inventory.setItem(33, inf6);

        inventory.setItem(34, art4);
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
