package earthrp.menusystem.menu.buildings.inGame;

import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EarthItem;
import earthrp.customObjects.Building;
import earthrp.Earth;
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

public class MiningBuildingMenu extends Menu {
    Building b = menuUtility.getBuilding();
    public MiningBuildingMenu(MenuUtility menuUtility) {
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
        if(b.getData().getItem() != null) item = b.getData().getItem().getMaterial();
        switch (Objects.requireNonNull(e.getCurrentItem()).getType()){


            case SOUL_TORCH, BELL -> {

                e.getWhoClicked().closeInventory();
                //new LoadingMenu(menuUtility, this.earthPlugin).open();
            }
            case REDSTONE_TORCH -> {

                e.getWhoClicked().closeInventory();
                new PaginatedItemMenu(menuUtility).open();

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
            case CANDLE ->{
                b.getData().setPastureMobSpawn(!b.getData().isPastureMobSpawn());
                e.getWhoClicked().closeInventory();
                new MiningBuildingMenu(menuUtility).open();
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
        townMeta.displayName(colorText("<light_purple>"+t.getName()));
        townMeta.lore(List.of(colorText("<white>Местоположение здания")));
        town.setItemMeta(townMeta);

        ItemStack item;
        ItemMeta itemMeta;
        List<String> lore = new ArrayList<>();
        if(b.getData().getItem()!=null){

            EarthItem ei = b.getData().getItem();

            item = Tools.createItem(ei.getMaterial(),ei.getDisplayName(),b.getItemLore(),ei.getCustomModel());
        }else {
            item = new ItemStack(Material.REDSTONE_TORCH);
            itemMeta = item.getItemMeta();
            itemMeta.displayName(colorText("<white>Производимый ресурс <red>не выбран") );
            itemMeta.lore(List.of(colorText("<white>Нажмите чтобы выбрать ресурс")));
            item.setItemMeta(itemMeta);
        }



        if(b.getData().getType().equals(BuildingType.PASTURE)){
            String name;
            lore = new ArrayList<>();

            if(b.getData().isPastureMobSpawn()){
                name = "<white>Ежедневный спавн животных <green>включён";
                lore.add(("<white>Нажмите чтобы <red>выключить"));

            }else {
                name = "<white>Ежедневный спавн животных <red>выключен";
                lore.add(("<white>Нажмите чтобы <green>включить"));
            }
            lore.add(("<white>Размер пастбища: <gold>"+b.getData().pastureArea));

            ItemStack mobSpawn = Tools.createItem(Material.CANDLE,name,lore);
            inventory.setItem(6, mobSpawn);
        }




        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));

        if(menuUtility.getTown()!=null){
            deleteMeta.displayName(Tools.deserialize("Назад"));
            deleteMeta.lore(List.of());
        }
        delete.setItemMeta(deleteMeta);

        ItemStack buildingItem = menuUtility.getBuildingItem().clone();
        List<Component> cLore = new ArrayList<>();
        if(b.getData().isStatus()){
            switch (b.getData().getType()){
                case PASTURE -> {
                    cLore.add(colorText("<white>Производительность зависит от <green>площади"));
                }
                case FARM -> {

                    double prod = Tools.round(b.getFarmEfficiency() + b.getOwner().getAttribute(EPlayerAttribute.GOODS_MOD) + b.getOwner().getAttribute(EPlayerAttribute.FARM_EFFICIENCY));
                    if(b.getFarmEfficiency() == 0) prod = 0;
                    cLore.add(colorText("<white>Плодородность <green>" + prod + "x площадь"   ) );
                }
                case FISHER -> {
                    cLore.add(colorText("<white>Производительность: <green>" + b.getBaseProduction() + "<white>x фрегаты"));
                }
                default -> {
                    cLore.add(colorText("<white>Производительность: <green>" + b.getBaseProduction() + "<white>x насел"));
                }
            }

        }else{
            cLore.add(colorText("<yellow>В здании сменился ресурс!"));
            cLore.add(colorText("<white>Производство начнётся в начале след. дня!"));
        }
        buildingItem.lore(cLore);










        inventory.setItem(3, town);
        inventory.setItem(4, buildingItem);
        inventory.setItem(5, item);

        inventory.setItem(8, delete);


    }
}
