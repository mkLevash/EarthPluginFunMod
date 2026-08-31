package earthrp.menusystem.menu.buildings.buy;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import earthrp.Earth;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EPlayerTech;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.BuildingsMenu;
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
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static earthrp.customEnums.BuildingType.*;
import static earthrp.tools.Tools.*;
import static earthrp.tools.PDCKeys.*;

public class WarBuildingsMenu extends Menu {

    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();




    public WarBuildingsMenu(MenuUtility menuUtility) {
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
            if(item.getType().equals(Material.BARRIER)){
                e.getWhoClicked().closeInventory();
                new BuildingsMenu(menuUtility).open();
            }
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();

            if(data.has(buildingTechCheckKey)){

                boolean techCheck = data.get(buildingTechCheckKey, PersistentDataType.BOOLEAN);
                int cost = data.get(buildingCostKey, PersistentDataType.INTEGER);
                String name = data.get(buildingNameKey, PersistentDataType.STRING);
                String type = data.get(buildingTypeKey, PersistentDataType.STRING);
                if(techCheck && treasury >= cost){
                    e.getWhoClicked().closeInventory();
                    new WarBuildingsMenu(menuUtility).open();
                    Tools.buyBuilding(player,cost);
                    ItemStack building = Tools.createBuilding(item.getType(),name,type);
                    p.getInventory().addItem(building);
                }
            }
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        inventory.setItem(11, createBuildingBuy(BARRACK));
        inventory.setItem(13, createBuildingBuy(STABLE));
        inventory.setItem(15, createBuildingBuy(GUN_FACTORY));

        inventory.setItem(29, createBuildingBuy(FORT));
        inventory.setItem(31, createBuildingBuy(FORGE));
        inventory.setItem(33, createBuildingBuy(SHIPYARD));

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
