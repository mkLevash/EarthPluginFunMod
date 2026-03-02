package earthrp.menusystem;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;
import earthrp.customObjects.EPlayer;
import earthrp.files.CustomConfig;
import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static earthrp.tools.PDCKeys.*;
import static earthrp.tools.PDCKeys.techCostKey;
import static earthrp.tools.PDCKeys.techIdKey;

/*
    Defines the behavior and attributes of all menus in our plugin
 */
public abstract class Menu implements InventoryHolder {

    //Protected values that can be accessed in the menus
    protected MenuUtility menuUtility;
    protected Inventory inventory;
    protected ItemStack FILLER_GLASS = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");

    //Constructor for Menu. Pass in a PlayerMenuUtility so that
    // we have information on who's menu this is and
    // what info is to be transfered
    public Menu(MenuUtility menuUtility) {
        this.menuUtility = menuUtility;
    }

    //let each menu decide their name
    public abstract String getMenuName();

    //let each menu decide their slot amount
    public abstract int getSlots();

    //let each menu decide how the items in the menu will be handled when clicked
    public abstract void handleMenu(InventoryClickEvent e);



    //let each menu decide what items are to be placed in the inventory menu
    public abstract void setMenuItems();

    //When called, an inventory is created and opened for the player
    public void open(){
        //The owner of the inventory created is the Menu itself,
        // so we are able to reverse engineer the Menu object from the
        // inventoryHolder in the MenuListener class when handling clicks
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        //grab all the items specified to be used for this menu and add to inventory
        this.setMenuItems();

        //open the inventory for the player
        menuUtility.getOwner().openInventory(inventory);
    }

    //Overridden method from the InventoryHolder interface
    @Override
    public Inventory getInventory() {
        return inventory;
    }

    //Helpful utility method to fill all remaining slots with "filler glass"
    public void setFillerGlass(){
        for (int i = 0; i < getSlots(); i++) {
            if (inventory.getItem(i) == null){
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }


    public ItemStack makeTech(String techId, EPlayer player, String customModel){
        return makeTech(Material.EGG,techId,player,customModel);
    }

    public ItemStack makeTech(String techId, EPlayer player){
        return makeTech(Material.BOOK,techId,player," ");
    }

    public ItemStack makeTech(Material material, String techId, EPlayer player){
        return makeTech(material,techId,player," ");
    }



    public ItemStack makeTech(Material material, String techId, EPlayer player, String customModel){
        double costMod = player.getAttribute(EPlayerAttribute.TECH_COST);
        boolean techStatus = player.getTech(EPlayerTech.fromString(techId));
        boolean techCheck = EPlayerTech.fromString(techId).canResearch(player.getTechMap());
        int techCost = (int) Math.round(CustomConfig.get().getInt("tech.cost."+techId) * costMod );

        String techName = CustomConfig.get().getString("tech.name."+techId, "techName");
        List<String> techLore = CustomConfig.get().getStringList("tech.lore."+techId);
        List<Component> lore = new ArrayList<>();
        for(String s:techLore){
            lore.add(colorText(s));
        }


        ItemStack item = makeItem(material, techName + " <aqua>" + techCost + "<white>ૹ");

        int enchantLvl = 0;

        if(techStatus) {
            if(material.equals(Material.BOOK)) item = makeItem(Material.ENCHANTED_BOOK,techName);
            else item = makeItem(material,techName);
            lore.addFirst(colorText("<green>Уже исследовано"));
            enchantLvl = 1;
        }
        ItemMeta meta = item.getItemMeta();
        meta.addEnchant(Enchantment.INFINITY,enchantLvl,true);
        meta.getPersistentDataContainer().set(techStatusKey,PersistentDataType.BOOLEAN, techStatus);
        meta.getPersistentDataContainer().set(techCheckKey,PersistentDataType.BOOLEAN, techCheck);
        meta.lore(lore);
        meta.getPersistentDataContainer().set(techCostKey,PersistentDataType.INTEGER, techCost);
        meta.getPersistentDataContainer().set(techIdKey,PersistentDataType.STRING, techId);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(customModel));
        meta.setCustomModelDataComponent(cmd);
        item.setItemMeta(meta);

        item.setItemMeta(meta);
        return item;

    }




    public ItemStack makeItem(String displayName, String menuId, String customModel, List<String> lore){
        return makeItem(Material.EGG,displayName, menuId, customModel, lore);
    }

    public ItemStack makeItem(String displayName, String menuId, String customModel){
        return makeItem(Material.EGG,displayName, menuId, customModel, List.of());
    }

    public ItemStack makeItem(Material material, String displayName, String menuId) {
        return makeItem(material,displayName, menuId, " ", List.of());
    }

    public ItemStack makeItem(Material material, String displayName, List<String> lore) {
        return makeItem(material,displayName, " ", " ", lore);
    }


    public ItemStack makeItem(Material material, String displayName) {
        return makeItem(material,displayName,List.of());
    }

    public ItemStack makeItem(Material material, String displayName, String menuId, String customModel, List<String> sLore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(colorText(displayName));
        List<Component> lore = new ArrayList<>();
        for(String s:sLore){
            lore.add(colorText(s));
        }

        meta.lore(lore);
        meta.getPersistentDataContainer().set(menuIdKey, PersistentDataType.STRING, menuId);

        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(customModel));
        meta.setCustomModelDataComponent(cmd);

        item.setItemMeta(meta);
        return item;
    }

    private boolean allTechsResearched(int epoch, EPlayer player) {
        for (EPlayerTech tech : EPlayerTech.values()) {
            if (tech.getLvl() == epoch && !tech.isResearched()) {
                if (!player.getTech(tech)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean canUnlockEpoch(String menuId, EPlayer player) {
        return switch (menuId) {
            case "tribal" -> true; // Эпоха 0 всегда доступна
            case "feudalism" -> allTechsResearched(0, player);
            case "renaissance" -> allTechsResearched(1, player);
            case "manufacture" -> allTechsResearched(2, player);
            default -> false;
        };
    }

    public ItemStack createEpochItem(String color,String displayName, String menuId, EPlayer player){
        return createEpochItem(Material.EGG, color, displayName, menuId, player);
    }

    public ItemStack createEpochItem(Material material, String color,String displayName, String menuId, EPlayer player) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();

        boolean canUnlock = canUnlockEpoch(menuId, player);
        boolean unlocked = player.getTech(EPlayerTech.fromString(menuId));

        double costMod = player.getAttribute(EPlayerAttribute.TECH_COST);
        int techCost = (int) Math.round(CustomConfig.get().getInt("tech.cost."+menuId) * costMod );


        color = unlocked ? color : "<gray>";
        String lockIcon = canUnlock ? "<aqua>"+techCost+"</aqua>" : "<gray>🔒</gray>";

        if(unlocked){
            meta.displayName(colorText(color + displayName));
        }else{
            meta.displayName(colorText(color + displayName + " " + lockIcon));
        }


        List<Component> lore = new ArrayList<>();


        if (unlocked) {
            lore.add(colorText("<gray>Прогресс: <white>" + getProgress(menuId, player)));
        } else {
            if(canUnlock){
                List<String> techLore = CustomConfig.get().getStringList("tech.lore."+menuId);
                for(String s:techLore){
                    lore.add(colorText(s));
                }

                lore.add(colorText("<yellow>ПКМ - открыть эпоху"));
            } else if (menuId.equals("industrial")) {
                lore.add(colorText("<red>в разработке"));
            } else{
                lore.add(colorText("<red>✗ Требуется изучить все технологии"));
                lore.add(colorText("<red>  предыдущей эпохи"));
            }

        }

        meta.lore(lore);
        meta.getPersistentDataContainer().set(menuIdKey, PersistentDataType.STRING, menuId);
        meta.getPersistentDataContainer().set(epochUnlockedKey, PersistentDataType.BOOLEAN, unlocked);

        // Устанавливаем кастомную модель
        CustomModelDataComponent cmd = meta.getCustomModelDataComponent();
        cmd.setStrings(List.of(menuId));
        meta.setCustomModelDataComponent(cmd);

        item.setItemMeta(meta);

        return item;
    }

    public String getProgress(String menuId, EPlayer player) {
        int total = 0;
        int researched = 0;
        int epoch = -1;

        switch (menuId) {
            case "tribal" -> epoch = 0;
            case "feudalism" ->  epoch = 1;
            case "renaissance" ->  epoch = 2;
            case "manufacture" ->  epoch = 3;
        }

        for (EPlayerTech tech : EPlayerTech.values()) {
            if (tech.getLvl() == epoch) {
                total++;
                if (player.getTech(tech)) {
                    researched++;
                }
            }
        }

        if (total == 0) return "100%";
        int percent = (researched * 100) / total;
        return percent + "% (" + researched + "/" + total + ")";
    }


    public static Component colorText(String text){

        return MiniMessage.miniMessage().deserialize("<!italic>" + text);
    }

    public void fillIfEmpty(int slot) {
        if (inventory.getItem(slot) == null) inventory.setItem(slot, FILLER_GLASS);
    }

    public static ItemStack createBackItem(){
        ItemStack close = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = close.getItemMeta();
        closeMeta.setDisplayName(ChatColor.RED + "Назад");
        close.setItemMeta(closeMeta);
        return close;
    }

}
