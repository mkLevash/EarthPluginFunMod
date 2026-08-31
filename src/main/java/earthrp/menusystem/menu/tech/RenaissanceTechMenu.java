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
        inventory.clear();
        // Эпоха 2: Ренессанс
        
        // === ЦЕНТР - Ключевая технология ===
        List<String> renaissanceLore = CustomConfig.get().getStringList("tech.lore.renaissance");
        ItemStack renaissance = makeItem(Material.BOOK,"<blue>Ренессанс", renaissanceLore);
        inventory.setItem(0, renaissance); // Центр
        
        // === ГРАЖДАНСКИЕ ТЕХНОЛОГИИ (слева) ===
        // Экономика
        ItemStack bankUp = makeTech(Material.ENDER_CHEST, "bankUp", player);
        //ItemStack charterCompany = makeTech(Material.MAP, "charterCompany", player);
        // Строительство
        ItemStack bastion = makeTech(Material.STONE_BRICKS, "bastion", player);
        //ItemStack starFort = makeTech(Material.SMOOTH_STONE, "starFort", player);
        // Администрация
        ItemStack earlyModernAdministration = makeTech(Material.WRITABLE_BOOK, "earlyModernAdministration", player);
        ItemStack bureaucracyBase = makeTech(Material.WRITABLE_BOOK, "bureaucracyBase", player);
        ItemStack separationPower = makeTech(Material.WRITABLE_BOOK, "separationPower", player);
        ItemStack university = makeTech(Material.WRITABLE_BOOK, "university", player);
        // Наука
        ItemStack printingPress = makeTech(Material.BOOK, "printingPress", player);
        
        inventory.setItem(10, bankUp);
        //inventory.setItem(11, charterCompany);
        inventory.setItem(19, bastion);

        inventory.setItem(12, university);
        inventory.setItem(13, earlyModernAdministration);
        inventory.setItem(15, bureaucracyBase);
        inventory.setItem(16, separationPower);
        inventory.setItem(14, printingPress);
        
        // === ВОЕННЫЕ ТЕХНОЛОГИИ (справа) ===
        // Пехота
        ItemStack inf3 = makeTech("inf3", player,"inf3");
        ItemStack inf4 = makeTech( "inf4", player,"inf4");
        ItemStack professionalArmy = makeTech("professionalArmy", player,"professionalArmy");
        // Кавалерия
        ItemStack cav3 = makeTech("cav3", player,"cav3");
        ItemStack cav4 = makeTech("cav4", player,"cav4");
        // Артиллерия
        ItemStack gunpowder = makeTech(Material.GUNPOWDER, "gunpowder", player);
        ItemStack art2 = makeTech("art2", player,"art2");
        // Тактика
        ItemStack spainSquare = makeTech(Material.SHIELD, "spainSquare", player);
        
        inventory.setItem(24, inf3);
        inventory.setItem(33, inf4);
        inventory.setItem(23, professionalArmy);
        inventory.setItem(22, cav3);
        inventory.setItem(31, cav4);
        inventory.setItem(25, gunpowder);
        inventory.setItem(34, art2);
        inventory.setItem(32, spainSquare);
        


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
