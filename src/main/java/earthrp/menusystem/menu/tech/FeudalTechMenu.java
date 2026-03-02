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
        ItemStack feudalism = makeTech(Material.GRASS_BLOCK, "feudalism", player);
        inventory.setItem(22, feudalism); // Центр
        
        // === ГРАЖДАНСКИЕ ТЕХНОЛОГИИ (слева) ===
        // Экономика
        ItemStack bankBase = makeTech(Material.ENDER_CHEST, "bankBase", player);
        ItemStack trade = makeTech(Material.EMERALD, "trade", player);
        // Строительство
        ItemStack motte = makeTech(Material.OAK_FENCE, "motte", player);
        ItemStack castle = makeTech(Material.STONE_BRICKS, "castle", player);
        // Администрация
        ItemStack banner = makeTech(Material.WHITE_BANNER, "banner", player);
        ItemStack medievalAdministration = makeTech(Material.WRITABLE_BOOK, "medievalAdministration", player);
        
        inventory.setItem(10, bankBase);
        inventory.setItem(11, trade);
        inventory.setItem(12, motte);
        inventory.setItem(13, castle);
        inventory.setItem(14, banner);
        inventory.setItem(15, medievalAdministration);
        
        // === ВОЕННЫЕ ТЕХНОЛОГИИ (справа) ===
        // Кавалерия
        ItemStack horseRidding = makeTech(Material.SADDLE, "horseRidding", player);
        // Пехота
        ItemStack lowerMedievalMilitary = makeTech(Material.IRON_SWORD, "lowerMedievalMilitary", player);
        ItemStack highMedievalMilitary = makeTech(Material.SHIELD, "highMedievalMilitary", player);
        // Ресурсы
        ItemStack copper = makeTech(Material.COPPER_INGOT, "copper", player);
        ItemStack iron = makeTech(Material.IRON_INGOT, "iron", player);
        // Инженерия
        ItemStack engineering = makeTech(Material.COMPASS, "engineering", player);
        ItemStack workshop = makeTech(Material.CRAFTING_TABLE, "workshop", player);
        
        inventory.setItem(28, horseRidding);
        inventory.setItem(29, lowerMedievalMilitary);
        inventory.setItem(30, highMedievalMilitary);
        inventory.setItem(31, copper);
        inventory.setItem(32, iron);
        inventory.setItem(33, engineering);
        inventory.setItem(34, workshop);
        
        // === МОРСКИЕ ТЕХНОЛОГИИ (низ) ===
        ItemStack shipbuilding = makeTech(Material.OAK_BOAT, "shipbuilding", player);
        ItemStack earlyCarrack = makeTech(Material.OAK_BOAT, "earlyCarrack", player);
        
        inventory.setItem(37, shipbuilding);
        inventory.setItem(38, earlyCarrack);

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
