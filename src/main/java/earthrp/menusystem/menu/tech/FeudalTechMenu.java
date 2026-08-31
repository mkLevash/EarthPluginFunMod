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

            if (item.getType().equals(Material.BARRIER)) {
                p.closeInventory();
                new TechnologyMenu(menuUtility).open();
            } else if (item.getItemMeta().getPersistentDataContainer().has(techIdKey)) {
                Tools.techProcess(item, player);
                inventory.clear();
                this.setMenuItems();
            }
        }
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
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

        ItemStack admin = makeTech(Material.WRITABLE_BOOK, "medievalAdministration", player);
        //ItemStack medievalAdministration = makeTech(Material.WRITABLE_BOOK, "medievalAdministration", player);
        
        inventory.setItem(14, bankBase);
        inventory.setItem(15, admin);
        //inventory.setItem(11, trade);
        //inventory.setItem(12, motte);
        inventory.setItem(20, castle);
        inventory.setItem(16, banner);
        //inventory.setItem(15, medievalAdministration);
        
        // === ВОЕННЫЕ ТЕХНОЛОГИИ (справа) ===
        // Кавалерия

        // Пехота
        ItemStack BaseMedievalMilitary = makeTech(Material.IRON_SWORD,"baseMedievalMilitary", player,"baseMedievalMilitary");
        inventory.setItem(24, BaseMedievalMilitary);
        ItemStack inf1 = makeTech("inf1", player,"inf1");
        inventory.setItem(25, inf1);
        ItemStack cav1 = makeTech("cav1", player,"cav1");
        inventory.setItem(23, cav1);


        ItemStack highMedievalMilitary = makeTech(Material.DIAMOND_SWORD,"newMedievalMilitary", player);
        inventory.setItem(33, highMedievalMilitary);
        ItemStack inf2 = makeTech("inf2", player,"inf2");
        inventory.setItem(34, inf2);
        ItemStack cav2 = makeTech("cav2", player,"cav2");
        inventory.setItem(32, cav2);
        // Ресурсы
        //ItemStack copper = makeTech(Material.COPPER_INGOT, "copper", player);
        ItemStack iron = makeTech(Material.IRON_INGOT, "iron", player);
        ItemStack ironLumber = makeTech(Material.IRON_AXE, "ironLumber", player);
        ItemStack ironMine = makeTech(Material.IRON_PICKAXE, "ironMine", player);
        // Инженерия
        ItemStack engineering = makeTech(Material.COMPASS, "engineering", player);
        ItemStack workshop = makeTech(Material.CRAFTING_TABLE, "workshop", player);
        



        //inventory.setItem(31, copper);
        inventory.setItem(13, ironLumber);
        inventory.setItem(22, iron);
        inventory.setItem(31, ironMine);

        inventory.setItem(21, engineering);
        inventory.setItem(30, workshop);
        
        // === МОРСКИЕ ТЕХНОЛОГИИ (низ) ===
        ItemStack shipbuilding = makeTech(Material.OAK_BOAT, "shipbuilding", player);
        //ItemStack earlyCarrack = makeTech(Material.OAK_BOAT, "earlyCarrack", player);
        
        inventory.setItem(12, shipbuilding);
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
