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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class EcoTechMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    public EcoTechMenu(MenuUtility menuUtility, Earth earthPlugin)  {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
        player = db.getPlayer(uuid);

    }
    Player p = this.menuUtility.getOwner();
    UUID uuid = p.getUniqueId();
    EPlayer player;



    @Override
    public String getMenuName() {
        return "Экономические технологии";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if(item!=null){
            e.getWhoClicked().closeInventory();
            if(item.getItemMeta().getPersistentDataContainer().has(techIdKey)){
                Tools.techProcess(item,player);
            }
            if(item.getType().equals(Material.BARRIER)) new TechnologyMenu(new MenuUtility(p), this.earthPlugin).open();
            else new EcoTechMenu(new MenuUtility(p), this.earthPlugin).open();
        }


    }

    @Override
    public void setMenuItems() {


        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.DARK_GREEN + "BACK");
        next.setItemMeta(nextMeta);

        ItemStack bank = Tools.createItemTech("bankBase",player);
        ItemStack bankUp = Tools.createItemTech("bankUp",player);
        ItemStack trade = Tools.createItemTech("trade",player);
        ItemStack shipping = Tools.createItemTech("shipping",player);
        ItemStack railroad = Tools.createItemTech("railroad",player);
        inventory.setItem(2, trade);

        inventory.setItem(3, shipping);

        inventory.setItem(4, bank);

        inventory.setItem(5, bankUp);

        inventory.setItem(6, railroad);

        inventory.setItem(8, next);

    }
}
