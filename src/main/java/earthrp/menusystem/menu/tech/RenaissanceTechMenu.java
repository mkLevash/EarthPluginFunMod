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

public class RenaissanceTechMenu extends Menu {

    public RenaissanceTechMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();

    @Override
    public String getMenuName() {
        return "Ренессанс";
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
                new RenaissanceTechMenu(menuUtility).open();
            }
        }
    }

    @Override
    public void setMenuItems() {
        // Эпоха 2: Ренессанс
        
        // === ЦЕНТР - Ключевая технология ===
        ItemStack renaissance = makeTech(Material.GLOWSTONE, "renaissance", player);
        inventory.setItem(22, renaissance); // Центр
        
        // === ГРАЖДАНСКИЕ ТЕХНОЛОГИИ (слева) ===
        // Экономика
        ItemStack bankUp = makeTech(Material.ENDER_CHEST, "bankUp", player);
        ItemStack charterCompany = makeTech(Material.MAP, "charterCompany", player);
        // Строительство
        ItemStack bastion = makeTech(Material.STONE_BRICKS, "bastion", player);
        ItemStack starFort = makeTech(Material.SMOOTH_STONE, "starFort", player);
        // Администрация
        ItemStack earlyModernAdministration = makeTech(Material.WRITABLE_BOOK, "earlyModernAdministration", player);
        ItemStack bureaucracyBase = makeTech(Material.WRITABLE_BOOK, "bureaucracyBase", player);
        ItemStack separationPower = makeTech(Material.WRITABLE_BOOK, "separationPower", player);
        // Наука
        ItemStack printingPress = makeTech(Material.BOOK, "printingPress", player);
        
        inventory.setItem(10, bankUp);
        inventory.setItem(11, charterCompany);
        inventory.setItem(12, bastion);
        inventory.setItem(13, starFort);
        inventory.setItem(14, earlyModernAdministration);
        inventory.setItem(15, bureaucracyBase);
        inventory.setItem(16, separationPower);
        inventory.setItem(17, printingPress);
        
        // === ВОЕННЫЕ ТЕХНОЛОГИИ (справа) ===
        // Пехота
        ItemStack inf3 = makeTech(Material.BOW, "inf3", player);
        ItemStack professionalArmy = makeTech(Material.IRON_CHESTPLATE, "professionalArmy", player);
        // Кавалерия
        ItemStack cav3 = makeTech(Material.IRON_HORSE_ARMOR, "cav3", player);
        ItemStack cav4 = makeTech(Material.GOLDEN_HORSE_ARMOR, "cav4", player);
        // Артиллерия
        ItemStack gunpowder = makeTech(Material.GUNPOWDER, "gunpowder", player);
        ItemStack art1 = makeTech(Material.TNT, "art1", player);
        // Тактика
        ItemStack spainSquare = makeTech(Material.SHIELD, "spainSquare", player);
        
        inventory.setItem(28, inf3);
        inventory.setItem(29, professionalArmy);
        inventory.setItem(30, cav3);
        inventory.setItem(31, cav4);
        inventory.setItem(32, gunpowder);
        inventory.setItem(33, art1);
        inventory.setItem(34, spainSquare);
        
        // === МОРСКИЕ ТЕХНОЛОГИИ (низ) ===
        ItemStack dock = makeTech(Material.DARK_OAK_BOAT, "dock", player);
        
        inventory.setItem(38, dock);

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
