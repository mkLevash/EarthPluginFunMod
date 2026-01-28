package earthrp.menusystem.menu;

import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.ideas.*;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import static earthrp.tools.PDCKeys.*;

public class IdeasMenu extends Menu {
    public IdeasMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }


    @Override
    public String getMenuName() {
        return "Идеи";
    }

    @Override
    public int getSlots() {
        return 18;
    }

    @Override
    public void handleMenu(InventoryClickEvent e){

        if(e.getCurrentItem()!=null){
            if(e.getCurrentItem().getType().equals(Material.BARRIER)){
                e.getWhoClicked().closeInventory();
                new Main(menuUtility).open();
            }else{
                String type = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaTypeKey,PersistentDataType.STRING);
                String material = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaMaterialKey,PersistentDataType.STRING);
                String color = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaColorKey,PersistentDataType.STRING);
                String name = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(ideaNameKey,PersistentDataType.STRING);
                menuUtility.setIdeaType(type);
                menuUtility.setIdeaMaterial(material);
                menuUtility.setIdeaColor(color);
                menuUtility.setIdeaName(name);
                new IdeaMenu(menuUtility).open();
            }
        }

//        switch (Objects.requireNonNull(e.getCurrentItem()).getType()){
//
//            case CYAN_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new DipIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case YELLOW_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new TradeIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case GREEN_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new AdminIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case ORANGE_SHULKER_BOX -> {
//
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case BLACK_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new ImperialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case WHITE_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new SeparateIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case LIGHT_BLUE_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new ScienceIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case BROWN_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new RevanchismIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case LIME_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case PINK_SHULKER_BOX -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new IsolationIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//            case BARRIER -> {
//                Player p = (Player) e.getWhoClicked();
//                e.getWhoClicked().closeInventory();
//                new MainMenu(new MenuUtility(p), this.earthPlugin).open();
//
//            }
//
//        }

    }

    @Override
    public void setMenuItems() {

        ItemStack economy = new ItemStack(Material.ORANGE_SHULKER_BOX, 1);
        ItemMeta meta = economy.getItemMeta();
        meta.setDisplayName(ChatColor.GOLD + "Экономические");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"economic");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§6");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"ORANGE");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Экономические");
        economy.setItemMeta(meta);

        ItemStack admin = new ItemStack(Material.GREEN_SHULKER_BOX, 1);
        meta = admin.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GREEN + "Административные");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"admin");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§2");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"GREEN");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Административные");
        admin.setItemMeta(meta);

        ItemStack trade = new ItemStack(Material.YELLOW_SHULKER_BOX, 1);
        meta = trade.getItemMeta();
        meta.setDisplayName(ChatColor.YELLOW + "Торговые");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"trade");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§e");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"YELLOW");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Торговые");
        trade.setItemMeta(meta);

        ItemStack dip = new ItemStack(Material.CYAN_SHULKER_BOX, 1);
        meta = dip.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_AQUA + "Дипломатические");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"diplomatic");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§3");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"CYAN");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Дипломатические");
        dip.setItemMeta(meta);

        ItemStack war = new ItemStack(Material.BLACK_SHULKER_BOX, 1);
        meta = war.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_GRAY + "Империалистические");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"imperialism");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§7");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"BLACK");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Империалистические");
        war.setItemMeta(meta);

        ItemStack separate = new ItemStack(Material.WHITE_SHULKER_BOX, 1);
        meta = separate.getItemMeta();
        meta.setDisplayName(ChatColor.WHITE + "Освободительные");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"freedom");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§f");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"WHITE");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Освободительные");
        separate.setItemMeta(meta);

        ItemStack science = new ItemStack(Material.LIGHT_BLUE_SHULKER_BOX, 1);
        meta = science.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + "Научные");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"science");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§b");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"LIGHT_BLUE");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Научные");
        science.setItemMeta(meta);

        ItemStack revanchism = new ItemStack(Material.BROWN_SHULKER_BOX, 1);
        meta = revanchism.getItemMeta();
        meta.setDisplayName(ChatColor.DARK_RED + "Реваншистские");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"revanchism");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§4");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"BROWN");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Реваншистские");
        revanchism.setItemMeta(meta);

//        ItemStack colonial = new ItemStack(Material.LIME_SHULKER_BOX, 1);
//        meta = colonial.getItemMeta();
//        meta.setDisplayName(ChatColor.GREEN + "Колонизационные");
//        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"colonise");
//        colonial.setItemMeta(meta);

        ItemStack isolation = new ItemStack(Material.PINK_SHULKER_BOX, 1);
        meta = isolation.getItemMeta();
        meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Изоляционные");
        meta.getPersistentDataContainer().set(ideaTypeKey, PersistentDataType.STRING,"isolation");
        meta.getPersistentDataContainer().set(ideaColorKey, PersistentDataType.STRING,"§d");
        meta.getPersistentDataContainer().set(ideaMaterialKey, PersistentDataType.STRING,"PINK");
        meta.getPersistentDataContainer().set(ideaNameKey, PersistentDataType.STRING,"Изоляционные");
        isolation.setItemMeta(meta);



        inventory.setItem(2, economy);
        inventory.setItem(3, admin);
        inventory.setItem(4, trade);
        inventory.setItem(5, dip);
        inventory.setItem(6, war);

        inventory.setItem(11, separate);
        inventory.setItem(12, science);
        inventory.setItem(13, revanchism);
        //inventory.setItem(14, colonial);
        inventory.setItem(15, isolation);
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        inventory.setItem(17, createBackItem());

    }
}
