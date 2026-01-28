//package earthrp.menusystem.menu.ideas;
//
//import earthrp.Earth;
//import earthrp.menusystem.Menu;
//import earthrp.menusystem.MenuUtility;
//import earthrp.menusystem.menu.IdeasMenu;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.enchantments.Enchantment;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.InventoryClickEvent;
//import org.bukkit.inventory.ItemStack;
//import org.bukkit.inventory.meta.ItemMeta;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.UUID;
//
//public class EcoIdeaMenu extends Menu {
//    private final Earth earthPlugin;
//    int ideas;
//    double techMod;
//    public EcoIdeaMenu(MenuUtility menuUtility, Earth earthPlugin)  {
//        super(menuUtility);
//        this.earthPlugin = earthPlugin;
//        ideas = this.earthPlugin.getServerDatabase().getPlayerTech(uuid,41);
//        fullIdeas = (int) ( Math.floor((double) ideas / 5));
//        techMod = 1;
//        ideaCost = (int) ( Math.floor((fullIdeas*ideaIncMod + 10) * techMod));
//    }
//    Player p = this.menuUtility.getOwner();
//    UUID uuid = p.getUniqueId();
//    int fullIdeas;
//    int ideaIncMod = Earth.getInstance().getConfig().getInt("ideaIncMod");
//    int ideaCost;
//
//    String idea1Nam = "National Bank";
//    String idea2Nam = "Bureaucracy";
//    String idea3Nam = "Debt and Loans";
//    String idea4Nam = "Efficient Mining";
//    String idea5Nam = "Smithsonian Economics";
//
//
//
//    @Override
//    public String getMenuName() {return "Экономические идеи";}
//
//    @Override
//    public int getSlots() {return 27;}
//
//    @Override
//    public void handleMenu(InventoryClickEvent e)  {
//
//
//        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("ૹ")){
//            int i1 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,1);
//            int i2 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,2);
//            int i3 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,3);
//            int i4 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,4);
//            if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea1Nam)&& this.earthPlugin.investIdea(uuid,1,ideaCost)){
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            } else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea2Nam)&& i1 !=0 && this.earthPlugin.investIdea(uuid,2,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea3Nam)&& i2 !=0 && this.earthPlugin.investIdea(uuid,3,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea4Nam)&& i3 !=0 && this.earthPlugin.investIdea(uuid,4,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea5Nam)&& i4 !=0 && this.earthPlugin.investIdea(uuid,5,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }
//        }else{
//            if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea1Nam)) {
//                this.earthPlugin.backIdea(uuid, 1, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            } else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea2Nam)) {
//                this.earthPlugin.backIdea(uuid, 2, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea3Nam)) {
//                this.earthPlugin.backIdea(uuid, 3, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea4Nam)) {
//                this.earthPlugin.backIdea(uuid, 4, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea5Nam)) {
//                this.earthPlugin.backIdea(uuid, 5, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new EcoIdeaMenu(menuUtility, this.earthPlugin).open();
//            }
//        }
//
//        switch (e.getCurrentItem().getType()){
//
//            case GOLD_INGOT -> {
//                e.getWhoClicked().closeInventory();
//                e.getWhoClicked().sendMessage("Социальные");
//
//            }
//            case BARRIER -> {
//                e.getWhoClicked().closeInventory();
//                new IdeasMenu(menuUtility, this.earthPlugin).open();
//
//            }
//
//        }
//
//    }
//
//    @Override
//    public void setMenuItems() {
//
//        ItemStack lantern = new ItemStack(Material.SOUL_LANTERN, 1);
//        ItemMeta lanternMeta = lantern.getItemMeta();
//        lanternMeta.setDisplayName("Идея");
//        lanternMeta.addEnchant(Enchantment.INFINITY,1,true);
//        lantern.setItemMeta(lanternMeta);
//
//
//
//        int i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,1);
//        ItemStack idea1 = new ItemStack(Material.ORANGE_CONCRETE_POWDER, 1);
//        ItemMeta idea1Meta = idea1.getItemMeta();
//        if (i==0){
//            idea1Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea1Nam+" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea1Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea1Nam));
//            inventory.setItem(20, lantern);
//        }
//        idea1Meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&',"&fДоход от налогов &a+10%")));
//        idea1.setItemMeta(idea1Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,2);
//        ItemStack idea2 = new ItemStack(Material.ORANGE_CONCRETE_POWDER, 1);
//        ItemMeta idea2Meta = idea2.getItemMeta();
//        if (i==0){
//            idea2Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea2Nam+" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea2Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea2Nam));
//            inventory.setItem(21, lantern);
//        }
//        idea2Meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&',"&fСтоимость строительства &a-10%")));
//        idea2.setItemMeta(idea2Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,3);
//        ItemStack idea3 = new ItemStack(Material.ORANGE_CONCRETE_POWDER, 1);
//        ItemMeta idea3Meta = idea3.getItemMeta();
//        if (i==0){
//            idea3Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea3Nam+" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea3Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea3Nam));
//            inventory.setItem(22, lantern);
//        }
//        idea3Meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&',"&fСодержание армии &a-5%")));
//        idea3.setItemMeta(idea3Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,4);
//        ItemStack idea4 = new ItemStack(Material.ORANGE_CONCRETE_POWDER, 1);
//        ItemMeta idea4Meta = idea4.getItemMeta();
//        if (i==0){
//            idea4Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea4Nam+" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea4Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea4Nam));
//            inventory.setItem(23, lantern);
//        }
//        idea4Meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&',"&fЕжедневное снижение инфляции &a-1%")));
//        idea4.setItemMeta(idea4Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,5);
//        ItemStack idea5 = new ItemStack(Material.ORANGE_CONCRETE_POWDER, 1);
//        ItemMeta idea5Meta = idea5.getItemMeta();
//        if (i==0){
//            idea5Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea5Nam+" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea5Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',"&6"+idea5Nam));
//            inventory.setItem(24, lantern);
//        }
//        idea5Meta.setLore(List.of(ChatColor.translateAlternateColorCodes('&',"&fПроизводство &a+0.2")));
//        idea5.setItemMeta(idea5Meta);
//
//        ItemStack back = new ItemStack(Material.BARRIER, 1);
//        ItemMeta backMeta = back.getItemMeta();
//        backMeta.setDisplayName(ChatColor.RED + "BACK");
//        back.setItemMeta(backMeta);
//
//
//
//
//
//        inventory.setItem(11, idea1);
//        inventory.setItem(12, idea2);
//        inventory.setItem(13, idea3);
//        inventory.setItem(14, idea4);
//        inventory.setItem(15, idea5);
//
//        inventory.setItem(26, back);
//
//    }
//}
