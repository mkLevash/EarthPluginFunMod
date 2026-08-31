package earthrp.menusystem.menu;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import earthrp.customEnums.BuildingType;
import earthrp.customEnums.UnitTech;
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
import static earthrp.customEnums.UnitTech.*;
import static earthrp.tools.PDCKeys.*;

public class AdminTroopsMenu extends Menu {
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();


    public AdminTroopsMenu(MenuUtility menuUtility){
        super(menuUtility);
    }


    @Override
    public String getMenuName() {
        return "Строения";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {


        ItemStack item = e.getCurrentItem();
        if(item != null){
            if (item.getType() == Material.BARRIER) {
                p.closeInventory();
                new AdminMenu(menuUtility).open();
            } else {
                p.getInventory().addItem(item);
            }
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();


        for(UnitTech tech : UnitTech.values()){
            ItemStack item = Tools.createArmyCraftItem(tech);
            inventory.addItem(item);
        }


        inventory.setItem(40, createBackItem());



    }



}
