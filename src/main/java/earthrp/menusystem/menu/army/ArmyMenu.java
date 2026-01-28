package earthrp.menusystem.menu.army;

import earthrp.tools.Tools;
import earthrp.customObjects.Army;
import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.DeleteConfirmMenu;
import org.bukkit.*;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class ArmyMenu extends Menu {
    private final Army army = menuUtility.getArmy();
    private final ServerDatabase db;
    public ArmyMenu(MenuUtility menuUtility) {
        super(menuUtility);
        db = Earth.getInstance().getServerDatabase();
    }

    @Override
    public String getMenuName() {
        return "";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {

        switch (e.getCurrentItem().getType()){
            case EGG -> {
                e.getWhoClicked().closeInventory();
                new TroopsMenu(menuUtility).open();
            }
            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                menuUtility.setDeleteArmy(army);
                new DeleteConfirmMenu(menuUtility,Earth.getInstance()).open();
            }
            case LEAD ->{
                e.getWhoClicked().closeInventory();
                army.mergeUnits();
                new ArmyMenu(menuUtility).open();
            }
            case NETHERITE_UPGRADE_SMITHING_TEMPLATE -> {
                e.getWhoClicked().closeInventory();
                army.upgradeUnits((Player) e.getWhoClicked());
                new ArmyMenu(menuUtility).open();
            }
        }

    }

    @Override
    public void setMenuItems() {







        ItemStack owner = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta ownerMeta = (SkullMeta) owner.getItemMeta();
        ownerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(army.getOwnerId()));
        ownerMeta.setDisplayName(army.getOwner().getDisplayName());
        ownerMeta.getPersistentDataContainer().set(armyIdKey, PersistentDataType.STRING, army.getUuid().toString());
        ownerMeta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Владелец армии"));
        owner.setItemMeta(ownerMeta);
        inventory.setItem(0, owner);
        ItemStack leader = new ItemStack(Material.PLAYER_HEAD);
        if(menuUtility.getLeaderHead()!=null){
            leader = menuUtility.getLeaderHead();
        }

        SkullMeta leaderMeta = (SkullMeta) leader.getItemMeta();
        if (army.getLeaderName()==null){
            leaderMeta.setDisplayName(ChatColor.RED + "Генерал отсутствует");
        } else if (army.getLeaderName().equals("ruler")) {
            leaderMeta.setOwningPlayer(Bukkit.getOfflinePlayer(army.getOwnerId()));
            leaderMeta.setDisplayName( Tools.colorText(
                    "&d" + army.getOwner().getDisplayName() + " &4" + army.getLeaderFire() + " &6" + army.getLeaderShock())
            );
        }
        leader.setItemMeta(leaderMeta);
        inventory.setItem(12,leader);

        List<String> infLore = List.of(
                Tools.colorText("&fКоличество: &d" + army.getInfantry()),
                Tools.colorText("&fБоевая мощь: &f" + army.getCA("inf")),
                Tools.colorText("&fСтоимость полков: &f" + army.getTroopsCost("inf")),
                Tools.colorText("&fЗаполненность: &f" + army.getTypeTroops("inf"))

        );
        ItemStack inf = Tools.createItem(Material.ICE,"Пехота",infLore,"inf");
        inventory.setItem(18,inf);



        List<String> cavLore = List.of(
                Tools.colorText("&fКоличество: &d" + army.getCavalry()),
                Tools.colorText("&fБоевая мощь: &f" + army.getCA("cav")),
                Tools.colorText("&fСтоимость полков: &f" + army.getTroopsCost("cav")),
                Tools.colorText("&fЗаполненность: &f" + army.getTypeTroops("cav"))

        );

        ItemStack cav = Tools.createItem(Material.ICE,"Кавалерия",cavLore,"cav");
        inventory.setItem(19,cav);

        List<String> artLore = List.of(
                Tools.colorText("&fКоличество: &d" + army.getArtillery()),
                Tools.colorText("&fБоевая мощь: &f" + army.getCA("art")),
                Tools.colorText("&fСтоимость полков: &f" + army.getTroopsCost("art")),
                Tools.colorText("&fЗаполненность: &a" + army.getTypeTroops("art"))

        );
        ItemStack art = Tools.createItem(Material.ICE,"Артиллерия",artLore,"art");
        inventory.setItem(20,art);

        List<String> troopsLore = List.of(
                Tools.colorText( "&fह&d" + (army.getTroops()/1000) + "k&f/&7" +army.getSize() + "k"),

                Tools.colorText( "&fᠩ&a" + army.getMorale() + "&f/&a" + army.getMaxMorale()),
                Tools.colorText( "&fᠧ&f" + army.getDisciple()),
                Tools.colorText( "&fᠨ&a" + army.getTactic()),
                Tools.colorText( "&fᢰ&f" + (int) ((army.getOwner().getAttribute(EPlayerAttribute.CAV_RATIO))*100)  + "%" )

        );
        ItemStack troops = Tools.createItem(Material.EGG,"Ваши войска", troopsLore,"manpower");
        inventory.setItem(13,troops);

        Player player = menuUtility.getOwner();
        Chunk chunk = player.getLocation().getChunk();
        String status;
        String locOwner = "";
        String locName;
        Town t = db.getTownAtChunk(chunk.getX(), chunk.getZ());

        if(t!=null){
            if(t.getOwnerId().equals(army.getOwnerId())) status = "Союзная территория";
            else status = "Нейтральная территория";
            locOwner = "Город " + t.getOwnerName() + "'a";
            locName = t.getName();
        }else{
            locName = " ";
            status = "Территория варваров";
        }
        List<String> townLore = List.of(
                ChatColor.WHITE + "Местоположение армии",
                ChatColor.WHITE + status,
                ChatColor.WHITE + locOwner
                );

        ItemStack armyLocation = Tools.createItem(Material.CAMPFIRE,locName, townLore);
        inventory.setItem(8,armyLocation);

        List<String> splitDesc = List.of(
                Tools.colorText("&7Полки с неполной заполненностью буду объединены"),
                Tools.colorText("&7Полки с 0 заполненностью будут распущены"));
        ItemStack split = Tools.createItem(Material.LEAD,Tools.colorText("&dОбъединить&f полки"),splitDesc);
        inventory.setItem(25, split);

        List<String> upgradeDesc = List.of(
                Tools.colorText("&7Полки будут улучшены до "),
                Tools.colorText("&7максимально доступного уровня"));
        ItemStack upgrade = Tools.createItem(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,Tools.colorText("&dУлучшить&f полки"),upgradeDesc);
        ItemMeta meta = upgrade.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        upgrade.setItemMeta(meta);
        inventory.setItem(24, upgrade);


        List<String> deleteDesc = List.of(Tools.colorText("&eПроизойдёт автоматическое объединение полков"));
        ItemStack delete = Tools.createItem(Material.BARRIER,Tools.colorText("&dРаспустить&f армию"),deleteDesc);
        inventory.setItem(26, delete);







    }
}
