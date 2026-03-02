package earthrp.menusystem.menu;

import earthrp.tools.Tools;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.countryMenu.ArmyMenu;
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

public class Main extends Menu {
    public Main(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "Главное меню";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item != null){
            e.getWhoClicked().closeInventory();
            switch (item.getType()){
                case HEART_OF_THE_SEA -> {
                    new TechnologyMenu(menuUtility).open();

                }
                case BARREL -> {
                    new BuildingsMenu(menuUtility).open();
                }
                case LIGHT -> {


                    new IdeasMenu(menuUtility).open();

                }
                case BOOK->{
                    new CountryMenu(menuUtility).open();
                }
                case VILLAGER_SPAWN_EGG -> {
                    new ArmyMenu(menuUtility).open();
                }
            }
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if(data.has(menuIdKey)){
                switch (data.get(menuIdKey, PersistentDataType.STRING)){
                    case "tech" ->{
                        new TechnologyMenu(menuUtility).open();
                    }
                    case "idea" ->{
                        new IdeasMenu(menuUtility).open();
                    }
                    case "army" ->{
                        new ArmyMenu(menuUtility).open();
                    }
                }
            }
        }
    }

    @Override
    public void setMenuItems() {



        ItemStack tech = makeItem("Технологии","tech","menuTech");

        ItemStack build = makeItem(Material.BARREL, "<gold>Здания");

        ItemStack country = makeItem(Material.BOOK,"Статистика страны");

        ItemStack war = makeItem("Армия страны","army","menuArmyStat");

        ItemStack idea = makeItem("Идеи","idea","menuIdea");


        inventory.setItem(10, tech);

        inventory.setItem(11, idea);

        inventory.setItem(13, build);

        inventory.setItem(15, country);
        inventory.setItem(16, war);




    }
}
