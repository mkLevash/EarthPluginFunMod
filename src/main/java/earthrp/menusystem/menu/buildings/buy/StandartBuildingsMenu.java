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
import io.papermc.paper.datacomponent.item.ItemAttributeModifiers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static earthrp.customEnums.BuildingType.*;

import static earthrp.tools.PDCKeys.*;

public class StandartBuildingsMenu extends Menu {
    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();


    public StandartBuildingsMenu(MenuUtility menuUtility){
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
                    new StandartBuildingsMenu(menuUtility).open();
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


        ItemStack pasture = createBuildingBuy(PASTURE);


        ItemStack farm = createBuildingBuy(FARM);

        ItemStack lumber = createBuildingBuy(LUMBER);

        ItemStack career = createBuildingBuy(QUARRY);

        ItemStack mineV2 = createBuildingBuy(PIT);

        ItemStack mineV1 = createBuildingBuy(MINE);

        ItemStack factory = createBuildingBuy(MANUFACTURE);

        ItemStack plant = createBuildingBuy(WORKSHOP);

        ItemStack university = createBuildingBuy(UNIVERSITY);
        ItemStack library = createBuildingBuy(LIBRARY);

        ItemStack bank = createBuildingBuy(BANK);

        ItemStack market = createBuildingBuy(MARKETPLACE);

        ItemStack port = createBuildingBuy(PORT);

        ItemStack barn = createBuildingBuy(BARN);

        ItemStack courtHouse = createBuildingBuy(COURTHOUSE);




        inventory.setItem(0, lumber);
        inventory.setItem(9, pasture);
        inventory.setItem(18, farm);
        inventory.setItem(27, createBuildingBuy(FISHER));

        inventory.setItem(2, career);
        inventory.setItem(11, mineV2);
        inventory.setItem(20, mineV1);

        inventory.setItem(4, factory);
        inventory.setItem(13, plant);

        inventory.setItem(17, university);
        inventory.setItem(26, library);
        inventory.setItem(6, bank);

        inventory.setItem(8, barn);
        inventory.setItem(15, courtHouse);

        inventory.setItem(35, market);
        inventory.setItem(44, port);

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
