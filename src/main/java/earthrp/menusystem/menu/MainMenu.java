package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.tools.Tools;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.countryMenu.ArmyStatsMenu;
import earthrp.menusystem.menu.countryMenu.CountryMenu;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

import static earthrp.tools.PDCKeys.*;

public class MainMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    public MainMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
    }

    @Override
    public String getMenuName() {
        return "Главное меню";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item != null){
            e.getWhoClicked().closeInventory();
            switch (item.getType()){
                case HEART_OF_THE_SEA -> {
                    new TechnologyMenu(menuUtility, this.earthPlugin).open();

                }
                case BARREL -> {
                    new BuildingsMenu(menuUtility, this.earthPlugin).open();
                }
                case LIGHT -> {


                    new IdeasMenu(menuUtility, this.earthPlugin).open();

                }
                case BOOK->{
                    new CountryMenu(menuUtility, this.earthPlugin).open();
                }
                case VILLAGER_SPAWN_EGG -> {
                    new ArmyStatsMenu(menuUtility, this.earthPlugin).open();
                }

            }
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if(data.has(mainMenuIdKey)){
                switch (data.get(mainMenuIdKey, PersistentDataType.STRING)){
                    case "tech" ->{
                        new TechnologyMenu(menuUtility, this.earthPlugin).open();
                    }
                    case "idea" ->{
                        new IdeasMenu(menuUtility, this.earthPlugin).open();
                    }
                    case "army" ->{
                        new ArmyStatsMenu(menuUtility, this.earthPlugin).open();
                    }
                }
            }
        }


    }

    @Override
    public void setMenuItems() {



        ItemStack tech = Tools.createMainMenuItem("Технологии","menuTech","tech");

        ItemStack build = new ItemStack(Material.BARREL);
        ItemMeta buildMeta = build.getItemMeta();
        buildMeta.setDisplayName(ChatColor.GOLD + "Здания");
        build.setItemMeta(buildMeta);

        ItemStack country = Tools.createItem(Material.BOOK,"Статистика страны", List.of());

        ItemStack war = Tools.createMainMenuItem("Армия страны","menuArmyStat","army");





        ItemStack idea = Tools.createMainMenuItem("Идеи","menuIdea","idea");

        ItemStack close = new ItemStack(Material.BARRIER, 1);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Закрыть");
        close.setItemMeta(closeMeta);

        inventory.setItem(0, tech);

        inventory.setItem(1, idea);

        inventory.setItem(4, build);

        inventory.setItem(7, country);
        inventory.setItem(8, war);


    }
}
