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

import static earthrp.tools.Tools.*;
import static earthrp.tools.PDCKeys.*;

public class SocTechMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    public SocTechMenu(MenuUtility menuUtility, Earth earthPlugin) {
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
        return "Социальные технологии";
    }

    @Override
    public int getSlots() {
        return 18;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {

        ItemStack item = e.getCurrentItem();
        if(item!=null){
            e.getWhoClicked().closeInventory();
            if(item.getItemMeta().getPersistentDataContainer().has(techIdKey)){
                Tools.techProcess(item,player,p);
            }
            if(item.getType().equals(Material.BARRIER)) new TechnologyMenu(new MenuUtility(p), this.earthPlugin).open();
            else new SocTechMenu(new MenuUtility(p), this.earthPlugin).open();
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack diplomacy = Tools.createItemTech("diplomacy",player);
        ItemStack officeBase = Tools.createItemTech("officeBase",player);
        ItemStack officeUp = Tools.createItemTech("officeUp",player);
        ItemStack school = Tools.createItemTech("school",player);
        ItemStack university = Tools.createItemTech("university",player);
        ItemStack ministry = Tools.createItemTech("ministry",player);
        ItemStack adminEfficiency = Tools.createItemTech("adminEfficiency",player);


        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.DARK_GREEN + "BACK");
        next.setItemMeta(nextMeta);


        inventory.setItem(11, diplomacy);

        inventory.setItem(12, officeBase);

        inventory.setItem(3, officeUp);

        inventory.setItem(14, school);

        inventory.setItem(15, university);

        inventory.setItem(5, ministry);

        //inventory.setItem(4, tech12);

        inventory.setItem(4, adminEfficiency);

        inventory.setItem(17, next);

    }
}
