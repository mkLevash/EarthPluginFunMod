package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.customObjects.Army;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class OccupationConfirmMenu extends Menu {
    private final Earth earthPlugin;
    Town town = menuUtility.getTown();
    private final ServerDatabase db;
    public OccupationConfirmMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
    }

    @Override
    public String getMenuName() {
        return "Вы уверены?";

    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        switch (e.getCurrentItem().getType()){
            case EMERALD->{
                p.closeInventory();
                town.setStatus(false);
                p.sendMessage("Вы оккупировали "+town.getName());

                new TownsMenu(menuUtility,earthPlugin).open();
            }case BARRIER->{
                p.closeInventory();
                new TownsMenu(menuUtility,earthPlugin).open();
            }
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack yes = new ItemStack(Material.EMERALD, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Да");
        ArrayList<String> yes_lore = new ArrayList<>();
        yes_lore.add(ChatColor.AQUA + "Вы оккупируете этот город");

        yes_meta.setLore(yes_lore);
        yes.setItemMeta(yes_meta);
        ItemStack no = new ItemStack(Material.BARRIER, 1);
        ItemMeta no_meta = no.getItemMeta();
        no_meta.setDisplayName(ChatColor.DARK_RED + "Нет");
        no.setItemMeta(no_meta);

        inventory.setItem(3, yes);
        inventory.setItem(5, no);

    }
}
