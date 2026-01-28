package earthrp.menusystem.menu.tech;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.files.CustomConfig;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TechnologyMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class WarTechMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    public WarTechMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
        player = db.getPlayer(uuid);
    }
    Player p = this.menuUtility.getOwner();
    UUID uuid = p.getUniqueId();
    EPlayer player;

    double costMod = player.getAttribute(EPlayerAttribute.TECH_COST);



    @Override
    public String getMenuName() {
        return "Военные технологии";
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {

        ItemStack item = e.getCurrentItem();
        if(item!=null){
            e.getWhoClicked().closeInventory();
            if(item.getItemMeta().getPersistentDataContainer().has(techIdKey)){
                Tools.techProcess(item,player);
            }
            if(item.getType().equals(Material.BARRIER)) new TechnologyMenu(menuUtility, this.earthPlugin).open();
            else new WarTechMenu(menuUtility, this.earthPlugin).open();
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack officeMil = Tools.createItemTech("officeMil",player);
        ItemStack fort = Tools.createItemTech("fort",player);
        ItemStack levies = Tools.createItemTech("levies",player);
        ItemStack siege = Tools.createItemTech("siege",player);
        ItemStack metalPcg =Tools.createItemTech("metalPcg",player);
        ItemStack standard = Tools.createItemTech("standard",player);
        ItemStack heavyCav = Tools.createItemTech("heavyCav",player);
        ItemStack gunpowder = Tools.createItemTech("gunpowder",player);



        //inf
        ItemStack inf1 = Tools.createItemTech("inf1",player,"inf1");
        ItemStack inf2 = Tools.createItemTech("inf2",player,"inf2");
        ItemStack inf3 = Tools.createItemTech("inf3",player,"inf3");
        ItemStack inf4 = Tools.createItemTech("inf4",player,"inf4");

        //cav
        ItemStack cav1 = Tools.createItemTech("cav1",player,"cav1");
        ItemStack cav2 = Tools.createItemTech("cav2",player,"cav2");
        ItemStack cav3 = Tools.createItemTech("cav3",player,"cav3");
        ItemStack cav4 = Tools.createItemTech("cav4",player,"cav4");

        //art
        ItemStack art1 = Tools.createItemTech("art1",player,"art1");
        ItemStack art2 = Tools.createItemTech("art2",player,"art2");



        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.DARK_GREEN + "BACK");
        next.setItemMeta(nextMeta);


        inventory.setItem(0, officeMil);
        inventory.setItem(1, fort);
        inventory.setItem(9, levies);
        inventory.setItem(10, siege);
        inventory.setItem(18, metalPcg);
        inventory.setItem(19, standard);
        inventory.setItem(27, heavyCav);
        inventory.setItem(28, gunpowder);

        inventory.setItem(3, inf4);
        inventory.setItem(12, inf3);
        inventory.setItem(21, inf2);
        inventory.setItem(30, inf1);

        inventory.setItem(5, cav4);
        inventory.setItem(14, cav3);
        inventory.setItem(23, cav2);
        inventory.setItem(32, cav1);

        inventory.setItem(7, art2);
        inventory.setItem(16, art1);





        inventory.setItem(8, next);

    }
}
