package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
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

public class AnnexConfirmMenu extends Menu {
    Town town = menuUtility.getTown();
    private final ServerDatabase db;
    public AnnexConfirmMenu(MenuUtility menuUtility) {
        super(menuUtility);
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
        EPlayer player = db.getPlayer(p.getUniqueId());
        switch (e.getCurrentItem().getType()){
            case EMERALD->{
                p.closeInventory();
                town.getOwner().removeTown(town);
                if(town.getType().equals("capital")) town.setType("townHall");
                town.setStatus(true);
                town.setCore(false);
                town.setOwnerId(p.getUniqueId());
                town.setOwnerName(player.getCountryName());
                player.addTown(town);
                p.sendMessage("Вы аннексировали "+town.getName());
                Player target = Bukkit.getPlayer(town.getOwnerName());
                if(target!= null) target.sendMessage(p.getDisplayName() + " оккупировал ваш город "+town.getName()+ "!");
                new TownsMenu(menuUtility).open();
            }case BARRIER->{
                p.closeInventory();
                new TownsMenu(menuUtility).open();
            }
        }

    }

    @Override
    public void setMenuItems() {

        ItemStack yes = new ItemStack(Material.EMERALD, 1);
        ItemMeta yes_meta = yes.getItemMeta();
        yes_meta.setDisplayName(ChatColor.GREEN + "Да");
        ArrayList<String> yes_lore = new ArrayList<>();
        yes_lore.add(ChatColor.AQUA + "Вы аннексируете этот город");

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
