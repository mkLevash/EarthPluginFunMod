package earthrp.menusystem.menu.tech;

import earthrp.Earth;
import earthrp.customEnums.EPlayerTech;
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

public class CraftTechMenu extends Menu {

    private final Earth earthPlugin;
    private final ServerDatabase db;
    public CraftTechMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
        player = db.getPlayer(uuid);
    }

    Player p = this.menuUtility.getOwner();
    UUID uuid = this.menuUtility.getOwner().getUniqueId();
    EPlayer player;




    @Override
    public String getMenuName() {
        return "Ремесленные технологии";
    }

    @Override
    public int getSlots() {
        return 18;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        int oiBalance = (int) player.getAttribute(EPlayerAttribute.OI_BALANCE);
        ItemStack item = e.getCurrentItem();
        if(item!=null){
            e.getWhoClicked().closeInventory();
            if(item.getItemMeta().getPersistentDataContainer().has(techIdKey)){
                Tools.techProcess(item,player);
            }
            if(item.getType().equals(Material.BARRIER)) new TechnologyMenu(new MenuUtility(p), this.earthPlugin).open();
            else new CraftTechMenu(new MenuUtility(p), this.earthPlugin).open();
        }


    }

    @Override
    public void setMenuItems() {

        ItemStack pasture = Tools.createItemTech(Material.LEATHER,"pasture", player);
        ItemStack lumber = Tools.createItemTech(Material.OAK_LOG, "lumber", player);

        ItemStack mine = Tools.createItemTech(Material.STONE_PICKAXE, "mine", player);
        ItemStack pit = Tools.createItemTech(Material.IRON_PICKAXE, "pit", player);
        ItemStack quarry = Tools.createItemTech(Material.DIAMOND_PICKAXE, "quarry", player);

        ItemStack forge = Tools.createItemTech(Material.ANVIL, "forge", player);

        ItemStack shipyard = Tools.createItemTech("shipyard", player,"ship");

        ItemStack manufacture = Tools.createItemTech(Material.CRAFTING_TABLE, "manufacture", player);

        ItemStack factory = Tools.createItemTech(Material.SMITHING_TABLE, "factory", player);


        ItemStack back = new ItemStack(Material.BARRIER, 1);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.DARK_GREEN + "BACK");
        back.setItemMeta(backMeta);


        inventory.setItem(0, lumber);

        inventory.setItem(1, shipyard);

        inventory.setItem(9, pasture);

        inventory.setItem(10, forge);

        inventory.setItem(12, mine);
        inventory.setItem(13, pit);
        inventory.setItem(14, quarry);

        inventory.setItem(16, manufacture);
        inventory.setItem(17, factory);

        inventory.setItem(8, back);

    }
}
