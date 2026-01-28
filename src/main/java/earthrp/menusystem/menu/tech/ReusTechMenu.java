package earthrp.menusystem.menu.tech;

import earthrp.Earth;
import earthrp.customEnums.EPlayerTech;
import earthrp.tools.Tools;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.TechnologyMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.UUID;

public class ReusTechMenu extends Menu {

    public ReusTechMenu(MenuUtility menuUtility, Earth earthPlugin)  {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        if(t2){
            revCost *= 2;
        }
        db = Earth.getInstance().getServerDatabase();
        player = db.getPlayer(uuid);
    }

    public String CostCheck(){
        return "";
    }
    private final Earth earthPlugin;
    private final ServerDatabase db;
    Player p = this.menuUtility.getOwner();
    UUID uuid = p.getUniqueId();
    EPlayer player;


    double techMod = player.getAttribute(EPlayerAttribute.TECH_COST);

    double tacGain = Earth.getInstance().getConfig().getDouble("tacGain");;
    int tacCostBase = Earth.getInstance().getConfig().getInt("tacCostBase");
    int tacCost = (int) Math.ceil(tacCostBase * techMod);


    double ideaCostMod = player.getAttribute(EPlayerAttribute.IDEA_COST);
    int ideaCostBase = Earth.getInstance().getConfig().getInt("ideaCostBase");
    int ideaCost = (int) Math.ceil(ideaCostMod*ideaCostBase);


    boolean t2 = player.getTech(EPlayerTech.BANK_UP);
    int revCost = 10;


    NamespacedKey techIdKey = new NamespacedKey(Earth.getInstance(), "techId");



    @Override
    public String getMenuName() {
        return "Многоразовые технологии";
    }


    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {

        int oiBalance = (int) player.getAttribute(EPlayerAttribute.OI_BALANCE);

        ItemStack item = e.getCurrentItem();
        if(item!=null){
            e.getWhoClicked().closeInventory();

            ItemMeta meta = item.getItemMeta();

            if(meta.getPersistentDataContainer().has(techIdKey)){
                String techId;
                techId = meta.getPersistentDataContainer().get(techIdKey, PersistentDataType.STRING);
                switch (techId){
                    case "tac" ->{
                        if (oiBalance>=tacCost){
                            player.addAttribute(EPlayerAttribute.TACTIC,tacGain);
                            player.addAttribute(EPlayerAttribute.OI_BALANCE, -tacCost);
                        }
                    }
                    case "rev" -> {

                        if (oiBalance>=revCost){
                            player.addAttribute(EPlayerAttribute.OI_BALANCE,-revCost);
                            p.getInventory().addItem(Tools.createRev());
                        }

                    }

                    case "idea" -> {
                        if (oiBalance>=ideaCost){
                            player.addAttribute(EPlayerAttribute.OI_BALANCE,-ideaCost);
                            p.getInventory().addItem(Tools.createIdea());
                        }

                    }
                }
            }
            if(item.getType().equals(Material.BARRIER)) new TechnologyMenu(new MenuUtility(p), this.earthPlugin).open();
            else new ReusTechMenu(new MenuUtility(p), this.earthPlugin).open();

        }



    }

    @Override
    public void setMenuItems() {

        ItemStack tech40 = new ItemStack(Material.PRISMARINE_CRYSTALS, 1);
        ItemMeta tech40Meta = tech40.getItemMeta();
        tech40Meta.setDisplayName(Tools.colorText("&bРевизия &e" + revCost + "&fૹ"));
        tech40Meta.setLore(List.of(
                ChatColor.DARK_AQUA +"Даёт возможность простить долг ",
                ChatColor.GREEN + "Многоразовое",
                ChatColor.DARK_RED + "ОИ НЕ ВОЗВРАЩАЮТСЯ"));
        tech40Meta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, "rev");
        tech40.setItemMeta(tech40Meta);

        ItemStack tech41 = new ItemStack(Material.PRISMARINE_CRYSTALS, 1);
        ItemMeta tech41Meta = tech41.getItemMeta();
        tech41Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&bИдея &e" + ideaCost + "&fૹ"));
        tech41Meta.setLore(List.of(ChatColor.DARK_AQUA +"Даёт 1 идею. ",
                ChatColor.GREEN + "Многоразовое",
                ChatColor.DARK_RED + "ОИ НЕ ВОЗВРАЩАЮТСЯ"));
        tech41Meta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, "idea");
        tech41.setItemMeta(tech41Meta);


        ItemStack tech42 = new ItemStack(Material.PRISMARINE_CRYSTALS, 1);
        ItemMeta tech42Meta = tech42.getItemMeta();
        tech42Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&bТактика &e" + tacCost + "&fૹ"));
        tech42Meta.setLore(List.of(ChatColor.DARK_AQUA +"Даёт "+tacGain+" тактики",
                ChatColor.translateAlternateColorCodes('&',"&3Текущее значение тактики - &2" + player.getAttribute(EPlayerAttribute.TACTIC)),
                ChatColor.GREEN + "Многоразовое",
                ChatColor.DARK_RED + "ОИ НЕ ВОЗВРАЩАЮТСЯ"));
        tech42Meta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, "tac");
        tech42.setItemMeta(tech42Meta);

        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.DARK_GREEN + "BACK");
        next.setItemMeta(nextMeta);




        inventory.setItem(3, tech40);

        inventory.setItem(4, tech41);

        inventory.setItem(5, tech42);



        inventory.setItem(8, next);

    }
}
