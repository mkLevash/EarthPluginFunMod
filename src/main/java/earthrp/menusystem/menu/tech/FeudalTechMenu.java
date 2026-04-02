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

public class FeudalTechMenu extends Menu {

    public FeudalTechMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();

    @Override
    public String getMenuName() {
        return "Феодализм";
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
                new FeudalTechMenu(menuUtility).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        // Эпоха 1: Феодализм
        
        // === ЦЕНТР - Ключевая технология ===
        List<String> feudalismLore = CustomConfig.get().getStringList("tech.lore.feudalism");
        ItemStack feudalism = makeItem("<green>Феодализм","","feudalism", feudalismLore);
        //ItemStack feudalism = makeTech(Material.GRASS_BLOCK, "feudalism", player);
        inventory.setItem(0, feudalism); // Центр
        
        // === ГРАЖДАНСКИЕ ТЕХНОЛОГИИ (слева) ===
        // Экономика
        ItemStack bankBase = makeTech("bankBase", player, "bankBase");

        // Строительство
        //ItemStack motte = makeTech(Material.OAK_FENCE, "motte", player);
        ItemStack castle = makeTech(Material.STONE_BRICKS, "castle", player);
        // Администрация
        ItemStack banner = makeTech(Material.WHITE_BANNER, "banner", player);
        //ItemStack medievalAdministration = makeTech(Material.WRITABLE_BOOK, "medievalAdministration", player);
        
        inventory.setItem(12, bankBase);
        //inventory.setItem(11, trade);
        //inventory.setItem(12, motte);
        inventory.setItem(21, castle);
        inventory.setItem(32, banner);
        //inventory.setItem(15, medievalAdministration);
        
        // === ВОЕННЫЕ ТЕХНОЛОГИИ (справа) ===
        // Кавалерия

        // Пехота

        ItemStack highMedievalMilitary = makeTech("medievalMilitary", player,"medievalMilitary");
        // Ресурсы
        //ItemStack copper = makeTech(Material.COPPER_INGOT, "copper", player);
        ItemStack iron = makeTech(Material.IRON_INGOT, "iron", player);
        // Инженерия
        ItemStack engineering = makeTech(Material.COMPASS, "engineering", player);
        ItemStack workshop = makeTech(Material.CRAFTING_TABLE, "workshop", player);
        


        inventory.setItem(31, highMedievalMilitary);
        //inventory.setItem(31, copper);
        inventory.setItem(30, iron);
        inventory.setItem(22, engineering);
        inventory.setItem(23, workshop);
        
        // === МОРСКИЕ ТЕХНОЛОГИИ (низ) ===
        ItemStack shipbuilding = makeTech(Material.OAK_BOAT, "shipbuilding", player);
        //ItemStack earlyCarrack = makeTech(Material.OAK_BOAT, "earlyCarrack", player);
        
        inventory.setItem(13, shipbuilding);
        //inventory.setItem(38, earlyCarrack);

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
