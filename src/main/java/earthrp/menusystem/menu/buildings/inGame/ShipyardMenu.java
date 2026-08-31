package earthrp.menusystem.menu.buildings.inGame;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Building;
import earthrp.customObjects.Town;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.DeleteConfirmMenu;
import earthrp.menusystem.menu.TownBuildingsMenu;
import earthrp.menusystem.menu.buildings.PaginatedItemMenu;
import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShipyardMenu extends Menu {

    Building b = menuUtility.getBuilding();

    public ShipyardMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return b.getData().getType().getDisplayName();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        Player p = (Player) e.getWhoClicked();
        Material item = Material.AIR;
        switch (Objects.requireNonNull(e.getCurrentItem()).getType()){


            case SOUL_TORCH, BELL -> {

                e.getWhoClicked().closeInventory();
                //new LoadingMenu(menuUtility, this.earthPlugin).open();
            }
            case OAK_BOAT -> {
                int ships = b.getOwner().getData().getTradeShips();
                if(ships < b.getOwner().getNavalLimit() && b.getOwner().getAttribute(EPlayerAttribute.TREASURY)>=16){
                    b.getOwner().getData().setTradeShips(ships+1);
                    b.getOwner().addAttribute(EPlayerAttribute.TREASURY,-16);
                }

                inventory.clear();
                setMenuItems();

            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                if(menuUtility.getTown()!=null){
                    new TownBuildingsMenu(menuUtility).open();
                }else{
                    menuUtility.setDeleteBuilding(b);
                    new DeleteConfirmMenu(menuUtility).open();
                }
            }

        }
        if (e.getCurrentItem().getType().equals(item)){
            e.getWhoClicked().closeInventory();
            new PaginatedItemMenu(menuUtility).open();
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        ItemStack town = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta townMeta = town.getItemMeta();
        Town t = Earth.getInstance().getDatabase().getTown(b.getTownId());
        townMeta.setDisplayName(ChatColor.LIGHT_PURPLE + t.getName());
        townMeta.setLore(List.of(ChatColor.WHITE + "Местоположение здания"));
        town.setItemMeta(townMeta);


        ItemStack bItem = menuUtility.getBuildingItem().clone();
        List<String> buildingLore = new ArrayList<>();
        buildingLore.add("ЛКМ - Построить фрегат 16$");
        buildingLore.add("32 бревна, 16 железа, 16 шерсти");

        bItem.lore();

        inventory.setItem(4,makeItem(Material.OAK_BOAT,"Фрегаты " + b.getOwner().getData().getTradeShips() + "/" + b.getOwner().getNavalLimit(),"","",buildingLore));


        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));
        if(menuUtility.getTown()!=null){
            deleteMeta.displayName(Tools.deserialize("Назад"));
            deleteMeta.lore(List.of());
        }
        delete.setItemMeta(deleteMeta);






        inventory.setItem(3, town);

        inventory.setItem(8, delete);


    }
    private boolean checkMaterial(Player player, Material material, int amount) {
        return player.getInventory().containsAtLeast(new ItemStack(material), amount);
    }
}
