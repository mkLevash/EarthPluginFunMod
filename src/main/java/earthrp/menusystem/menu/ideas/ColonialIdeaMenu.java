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
//import org.jetbrains.annotations.NotNull;
//
//import java.sql.SQLException;
//import java.util.List;
//import java.util.UUID;
//
//public class ColonialIdeaMenu extends Menu {
//    private final Earth earthPlugin;
//    int ideas;
//    double techMod;
//    public ColonialIdeaMenu(MenuUtility menuUtility, Earth earthPlugin){
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
//    Material block = Material.LIME_CONCRETE_POWDER;
//    String colorCode = "&a";
//
//    String idea11Name = "Colonial 1";
//    int idea1Id = 31;
//    ItemStack idea11;
//    ItemMeta idea11Meta;
//    List<@NotNull String> idea11Desc = List.of(
//            ChatColor.translateAlternateColorCodes('&',"&fПовзволяет колонизировать"));
//
//    String idea12Name = "Colonial 2";
//    int idea2Id = 32;
//    ItemStack idea12;
//    ItemMeta idea12Meta;
//    List<@NotNull String> idea12Desc = List.of(
//            ChatColor.translateAlternateColorCodes('&',"&a+0.5&f прозводства в колониях")
//            );
//
//    String idea13Name = "Colonial 3";
//    int idea3Id = 33;
//    ItemStack idea13;
//    ItemMeta idea13Meta;
//    List<@NotNull String> idea13Desc = List.of(
//            ChatColor.translateAlternateColorCodes('&',"&a-2&f$ стоимость жилых домов в колониях"));
//
//    String idea14Name = "Colonial 4";
//    int idea4Id = 34;
//    ItemStack idea14;
//    ItemMeta idea14Meta;
//    List<@NotNull String> idea14Desc = List.of(
//            ChatColor.translateAlternateColorCodes('&',"&a+10%&f эффективность торговли")
//    );
//
//    String idea15Name = "Colonial 5";
//    int idea5Id = 35;
//    ItemStack idea15;
//    ItemMeta idea15Meta;
//    List<@NotNull String> idea15Desc = List.of(
//            ChatColor.translateAlternateColorCodes('&',"&fПозволяет создавать колониальные автономии")
//    );
//
//
//
//    @Override
//    public String getMenuName() {return "Колониальные идеи";}
//
//    @Override
//    public int getSlots() {return 27;}
//
//    @Override
//    public void handleMenu(InventoryClickEvent e){
//
//
//        if(e.getCurrentItem().getItemMeta().getDisplayName().contains("ૹ")){
//            int i1 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea1Id);
//            int i2 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea2Id);
//            int i3 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea3Id);
//            int i4 = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea4Id);
//            if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea11Name)&& this.earthPlugin.investIdea(uuid,idea1Id,ideaCost)){
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(idea12Name)&& i1 != 0 && this.earthPlugin.investIdea(uuid,idea2Id,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(idea13Name)&& i2!= 0 && this.earthPlugin.investIdea(uuid,idea3Id,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(idea14Name)&& i3 != 0 && this.earthPlugin.investIdea(uuid,idea4Id,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }else if(e.getCurrentItem().getItemMeta().getDisplayName().contains(idea15Name)&& i4 != 0 && this.earthPlugin.investIdea(uuid,idea5Id,ideaCost)) {
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }
//        }else{
//            if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea11Name)) {
//                this.earthPlugin.backIdea(uuid, idea1Id, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            } else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea12Name)) {
//                this.earthPlugin.backIdea(uuid, idea2Id, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea13Name)) {
//                this.earthPlugin.backIdea(uuid, idea3Id, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea14Name)) {
//                this.earthPlugin.backIdea(uuid, idea4Id, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
//            }else if (e.getCurrentItem().getItemMeta().getDisplayName().contains(idea15Name)) {
//                this.earthPlugin.backIdea(uuid, idea5Id, ideaCost);
//                e.getWhoClicked().closeInventory();
//                new ColonialIdeaMenu(new MenuUtility(p), this.earthPlugin).open();
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
//                new IdeasMenu(new MenuUtility(p), this.earthPlugin).open();
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
//        int i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea1Id);
//        idea11 = new ItemStack(block, 1);
//        idea11Meta = idea11.getItemMeta();
//        if (i==0){
//            idea11Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea11Name +" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea11Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea11Name));
//            inventory.setItem(20, lantern);
//        }
//        idea11Meta.setLore(idea11Desc);
//        idea11.setItemMeta(idea11Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea2Id);
//        idea12 = new ItemStack(block, 1);
//        idea12Meta = idea12.getItemMeta();
//        if (i==0){
//            idea12Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea12Name +" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea12Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea12Name));
//            inventory.setItem(21, lantern);
//        }
//        idea12Meta.setLore(idea12Desc);
//        idea12.setItemMeta(idea12Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea3Id);
//        idea13 = new ItemStack(block, 1);
//        idea13Meta = idea13.getItemMeta();
//        if (i==0){
//            idea13Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea13Name +" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea13Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea13Name));
//            inventory.setItem(22, lantern);
//        }
//        idea13Meta.setLore(idea13Desc);
//        idea13.setItemMeta(idea13Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea4Id);
//        idea14 = new ItemStack(block, 1);
//        idea14Meta = idea14.getItemMeta();
//        if (i==0){
//            idea14Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea14Name +" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea14Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea14Name));
//            inventory.setItem(23, lantern);
//        }
//        idea14Meta.setLore(idea14Desc);
//        idea14.setItemMeta(idea14Meta);
//
//        i = this.earthPlugin.getServerDatabase().getPlayerIdea(uuid,idea5Id);
//        idea15 = new ItemStack(block, 1);
//        idea15Meta = idea15.getItemMeta();
//        if (i==0){
//            idea15Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea15Name +" &b"+ideaCost+"&fૹ"));
//
//        }else {
//            idea15Meta.setDisplayName(ChatColor.translateAlternateColorCodes('&',colorCode+ idea15Name));
//            inventory.setItem(24, lantern);
//        }
//        idea15Meta.setLore(idea15Desc);
//        idea15.setItemMeta(idea15Meta);
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
//        inventory.setItem(11, idea11);
//        inventory.setItem(12, idea12);
//        inventory.setItem(13, idea13);
//        inventory.setItem(14, idea14);
//        inventory.setItem(15, idea15);
//
//        inventory.setItem(26, back);
//
//    }
//}
