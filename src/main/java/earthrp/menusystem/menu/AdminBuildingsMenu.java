package earthrp.menusystem.menu;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;
import earthrp.customObjects.EPlayer;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.tools.Tools;
import net.kyori.adventure.text.Component;
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

import java.util.List;

import static earthrp.customEnums.BuildingType.*;
import static earthrp.tools.PDCKeys.*;

public class AdminBuildingsMenu extends Menu {
    Player p = menuUtility.getOwner();



    public AdminBuildingsMenu(MenuUtility menuUtility){
        super(menuUtility);
    }


    @Override
    public String getMenuName() {
        return "Строения";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {


        ItemStack item = e.getCurrentItem();
        if(item != null){
            if(item.getType().equals(Material.BARRIER)){
                e.getWhoClicked().closeInventory();
                new BuildingsMenu(menuUtility).open();
            }
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();

            if(data.has(buildingTechCheckKey)){


                String name = data.get(buildingNameKey, PersistentDataType.STRING);
                String type = data.get(buildingTypeKey, PersistentDataType.STRING);

                ItemStack building = Tools.createBuilding(item.getType(),name,type);
                building.lore(List.of(Tools.deserialize("debug")));
                p.getInventory().addItem(building);
            }
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        for(BuildingType type:BuildingType.values()){
            ItemStack building = createBuildingBuy(type);
            inventory.addItem(building);
        }

        inventory.setItem(53, createBackItem());

    }

    private ItemStack createBuildingBuy( BuildingType bt){


        Component name = colorText("<white>" + bt.getDisplayName() + " <gold>" + bt.getBaseCost() + "<white>$");
        if(bt.isBuildSiteReq()){
            name = name.append(colorText("|<yellow>Спец. здание"));
        }
        ItemStack building = createItem(bt.getMaterial(),name, bt.getBaseLore());
        ItemMeta meta = building.getItemMeta();
        meta.getPersistentDataContainer().set(buildingNameKey,PersistentDataType.STRING, bt.getDisplayName());
        meta.getPersistentDataContainer().set(buildingTypeKey,PersistentDataType.STRING, bt.toString());
        meta.getPersistentDataContainer().set(buildingCostKey,PersistentDataType.INTEGER, 0);
        meta.getPersistentDataContainer().set(buildingTechCheckKey,PersistentDataType.BOOLEAN, true);
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
