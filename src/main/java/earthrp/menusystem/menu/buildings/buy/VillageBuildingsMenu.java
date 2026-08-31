package earthrp.menusystem.menu.buildings.buy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.BuildingsMenu;
import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static earthrp.customEnums.BuildingType.*;
import static earthrp.tools.PDCKeys.*;

public class VillageBuildingsMenu extends Menu {

    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();




    public VillageBuildingsMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }




    @Override
    public String getMenuName() {
        return "Военные";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        double treasury = player.getAttribute(EPlayerAttribute.TREASURY);
        ItemStack item = e.getCurrentItem();
        if(item != null){
            ItemStack villager = new ItemStack(Material.VILLAGER_SPAWN_EGG);
            ItemMeta villagerMeta = villager.getItemMeta();
            switch (item.getType()){
                case GOLD_INGOT -> {

                    villagerMeta.setDisplayName("Дворянин " + player.getDisplayName());
                    villager.setItemMeta(villagerMeta);
                }
                case WHEAT -> {
                    villagerMeta.setDisplayName("Крестьянин " + player.getDisplayName());
                    villager.setItemMeta(villagerMeta);
                }
                case BARRIER -> {
                    new BuildingsMenu(menuUtility).open();
                    return;
                }
            }
            p.getInventory().addItem(villager);
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        List<String> hoseLore = new ArrayList<>();
        hoseLore.add("Минимальный размер дома = 3x3x3");
        hoseLore.add("Должны быть печка, верстак, кровать, сундук и 4 факела");
        hoseLore.add("+ 1 населения в городе");
        inventory.setItem(23, Tools.createItem(Material.WHEAT,"Крестьянин " + (int) Math.round(5 * player.getLivingBuildingCost()) + "$",hoseLore));

        List<String> nobleLore = new ArrayList<>();
        nobleLore.add("Минимальный размер дома =9x3x9");
        nobleLore.add("должны быть печка, верстак, 2 кровати, сундук, 8 фонарей, 5 изумрудов");
        nobleLore.add("даёт 3 моры налога, увеличивает доход от налогово в городе на 10%");
        nobleLore.add("Увеличивает потребление пищи на 50");
        inventory.setItem(24, Tools.createItem(Material.GOLD_INGOT,"Дворянин " + (int) Math.round(15 * player.getLivingBuildingCost()) + "$",nobleLore));

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 35);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);

        inventory.setItem(40, createBackItem());

    }

    private ItemStack createBuildingBuy( BuildingType bt){


        Component name = colorText("<white>" + bt.getDisplayName() + " <gold>" + bt.getCost(menuUtility.getPlayer()) + "<white>$");
        if(bt.isBuildSiteReq()){
            name = name.append(colorText("|<yellow>Спец. здание"));
        }
        ItemStack building = createItem(bt.getMaterial(),name, bt.getLore(menuUtility.getPlayer()));
        ItemMeta meta = building.getItemMeta();
        meta.getPersistentDataContainer().set(buildingNameKey,PersistentDataType.STRING, bt.getDisplayName());
        meta.getPersistentDataContainer().set(buildingTypeKey,PersistentDataType.STRING, bt.toString());
        meta.getPersistentDataContainer().set(buildingCostKey,PersistentDataType.INTEGER, bt.getCost(menuUtility.getPlayer()));
        meta.getPersistentDataContainer().set(buildingTechCheckKey,PersistentDataType.BOOLEAN, bt.isTech(menuUtility.getPlayer()));
        meta.addEnchant(Enchantment.INFINITY,1,true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        Multimap<Attribute, AttributeModifier> modifiers = ArrayListMultimap.create();

        // 2. Добавляем фейковый модификатор (+0.0 к урону)
        // Так как он равен 0, он не изменит стандартный урон меча (он останется 7.0)
        modifiers.put(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                NamespacedKey.minecraft("fake_hidden_modifier"),
                0.0,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlotGroup.MAINHAND
        ));

        // 3. Записываем этот модификатор в мету
        meta.setAttributeModifiers(modifiers);


        building.setItemMeta(meta);
        return building;
    }

    private ItemStack createItem(Material material, Component displayName, List<Component> lore){
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(displayName);
        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
