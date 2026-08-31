package earthrp.menusystem.menu.army;

import earthrp.customEnums.UnitTech.UnitType;
import earthrp.customObjects.PlayerModifier;
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
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static earthrp.tools.PDCKeys.*;

public class ArmyMenu extends Menu {
    private final Army army = menuUtility.getArmy();
    private final ServerDatabase db;
    public ArmyMenu(MenuUtility menuUtility) {
        super(menuUtility);
        db = Earth.getInstance().getDatabase();
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
        ItemStack item = e.getCurrentItem();

        switch (item.getType()){
            case PLAYER_HEAD -> {
                PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
                if(data.has(leaderFireKey)){

                    army.setLeaderFire(0);
                    army.setLeaderShock(0);
                    army.setLeaderMove(0);
                    army.setLeaderSiege(0);
                    army.setLeaderName(null);

                    for(ItemStack i : menuUtility.getArmyShulkerBox().getInventory().getContents()){
                        if (i == null) continue;
                        PersistentDataContainer d = i.getItemMeta().getPersistentDataContainer();
                        if(d.has(leaderFireKey)){
                            i.lore(List.of(Tools.deserialize(" ")));
                            e.getWhoClicked().getInventory().addItem(i);
                            i.setAmount(0);
                            inventory.clear();
                            setMenuItems();

                        }
                    }



                }
            }

            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                menuUtility.setDeleteArmy(army);

                new DeleteConfirmMenu(menuUtility).open();
            }
            case LEAD ->{
                e.getWhoClicked().closeInventory();
                army.mergeUnits(e.isShiftClick());
                new ArmyMenu(menuUtility).open();

            }
            case NETHERITE_UPGRADE_SMITHING_TEMPLATE -> {
                e.getWhoClicked().closeInventory();
                army.upgradeUnits((Player) e.getWhoClicked());
                new ArmyMenu(menuUtility).open();
            }
            case CAMPFIRE -> {
                Town t = db.getTownAtChunk(army.getData().getLocation());
                if(t== null) break;

                if(t.getController().equals(army.getOwner()) || !army.getOwner().getData().getEnemies().contains(t.getController().getUniqueId())) break;
                army.getData().setSiegeTown(t.getUniqueId());

                if(!t.isSiege()){
                    t.getData().setSiegeChance(-75);
                    Player javaPlayer = Bukkit.getPlayer(t.getController().getUniqueId());
                    if (javaPlayer!=null) javaPlayer.sendMessage(Tools.deserialize("Началась <red>осада<white> города <aqua>"+ t.getName() + "!"));
                }
                t.getData().getSiegeArmy().add(army.getUuid());
                e.getWhoClicked().closeInventory();
                menuUtility.setSiegeTown(t);
                Earth.getInstance().getBlueMapManager().updateTownMarker(t);
                new SiegeMenu(menuUtility).open();

            }
        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();








        ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
        ItemStack leader = new ItemStack(Material.PLAYER_HEAD);
        for(ItemStack item : menuUtility.getArmyShulkerBox().getInventory().getContents()){
            if (item == null) continue;
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if(data.has(leaderFireKey)){

                leader = item;

            }
            if(data.has(armyIdKey)){
                owner = item;
            }
        }

        SkullMeta ownerMeta = (SkullMeta) owner.getItemMeta();

        ownerMeta.setLore(Collections.singletonList(ChatColor.LIGHT_PURPLE + "Владелец армии"));
        owner.setItemMeta(ownerMeta);

        inventory.setItem(0, owner);


        SkullMeta leaderMeta = (SkullMeta) leader.getItemMeta();
        if (army.getLeaderName()==null){
            leaderMeta.setDisplayName(ChatColor.RED + "Генерал отсутствует");
        }else{
            leaderMeta.lore(List.of(colorText("<yellow>ЛКМ <white>- чтобы открепить генерала")));
        }

        leader.setItemMeta(leaderMeta);
        inventory.setItem(1,leader);

        List<String> infLore = List.of(
                Tools.colorText("&fКоличество: &d" + army.getInfantry()),
                Tools.colorText("&fБоевая мощь: &f" + army.getCA("inf")),
                Tools.colorText("&fСтоимость полков: &f" + army.getTroopsCost("inf")),
                Tools.colorText("&fЗаполненность: &f" + army.getTypeTroops(UnitType.INF))

        );
        ItemStack inf = Tools.createItemLegacy(Material.ICE,"Пехота",infLore,"inf");
        inventory.setItem(18,inf);



        List<String> cavLore = List.of(
                Tools.colorText("&fКоличество: &d" + army.getCavalry()),
                Tools.colorText("&fБоевая мощь: &f" + army.getCA("cav")),
                Tools.colorText("&fСтоимость полков: &f" + army.getTroopsCost("cav")),
                Tools.colorText("&fЗаполненность: &f" + army.getTypeTroops(UnitType.CAV))

        );

        ItemStack cav = Tools.createItemLegacy(Material.ICE,"Кавалерия",cavLore,"cav");
        inventory.setItem(19,cav);



        List<String> morLore = new ArrayList<>();

        Set<PlayerModifier> modifiers = army.getOwner().getAttributeModifiers(EPlayerAttribute.MORALE_MOD);
        morLore.add(Tools.colorText("&fСытость "+army.getOwner().getSatietyColor()));
        if (modifiers != null && !modifiers.isEmpty()) {

            for (PlayerModifier mod : modifiers){
                morLore.add(Tools.colorText("&f" + mod.getName() + mod.getColorValueLegacy(EPlayerAttribute.MORALE_MOD)));
            }
        }

        List<String> disLore = new ArrayList<>();
        modifiers = army.getOwner().getAttributeModifiers(EPlayerAttribute.DISCIPLE);
        if (modifiers != null && !modifiers.isEmpty()) {

            for (PlayerModifier mod : modifiers){
                disLore.add(Tools.colorText("&f" + mod.getName() + mod.getColorValueLegacy(EPlayerAttribute.DISCIPLE)));
            }
        }

        List<String> tacLore = new ArrayList<>();
        modifiers = army.getOwner().getAttributeModifiers(EPlayerAttribute.TACTIC);
        tacLore.add(Tools.colorText("&fТехнологии &a"+army.getOwner().getAttributeValue(EPlayerAttribute.TACTIC)));
        tacLore.add(Tools.colorText("&fДисциплина &a"+army.getDiscipleMod()));
        if (modifiers != null && !modifiers.isEmpty()) {

            for (PlayerModifier mod : modifiers){
                tacLore.add(Tools.colorText("&f" + mod.getName() + mod.getColorValueLegacy(EPlayerAttribute.TACTIC)));
            }
        }




        ItemStack mor1 = Tools.createItemLegacy(Material.EGG,"Мораль " +  Tools.colorText( "&a" + army.getMorale() + "&f/&a" + army.getMaxMorale()),morLore,"land_morale-1");
        inventory.setItem(9,mor1);
        ItemStack mor2 = Tools.createItemLegacy(Material.EGG,"Мораль",null,"land_morale2");
        //inventory.setItem(2,mor2);
        ItemStack mor = Tools.createItemLegacy(Material.EGG,"Мораль",null,"land_morale");
        //inventory.setItem(3,mor);
        ItemStack morOld = Tools.createItemLegacy(Material.EGG,"Мораль",null,"land_moraleOld");
        //inventory.setItem(4,morOld);
        ItemStack dis = Tools.createItemLegacy(Material.EGG,"Дисциплина "  + Tools.colorText(String.valueOf(army.getDiscipleColor())),disLore,"discipline");
        inventory.setItem(10,dis);
        ItemStack disOld = Tools.createItemLegacy(Material.EGG,"Дисциплина ",disLore,"disciplineOld");
        //inventory.setItem(6,disOld);
        ItemStack tac = Tools.createItemLegacy(Material.EGG,"Тактика " + Tools.colorText(String.valueOf(army.getTactic())),tacLore,"military_tactics");
        inventory.setItem(11,tac);
        ItemStack tacOld = Tools.createItemLegacy(Material.EGG,"Тактика",null,"military_tacticsOld");
        //inventory.setItem(16,tacOld);



        List<String> artLore = List.of(
                Tools.colorText("&fКоличество: &d" + army.getArtillery()),
                Tools.colorText("&fБоевая мощь: &f" + army.getCA("art")),
                Tools.colorText("&fСтоимость полков: &f" + army.getTroopsCost("art")),
                Tools.colorText("&fЗаполненность: &a" + army.getTypeTroops(UnitType.ART))

        );
        ItemStack art = Tools.createItemLegacy(Material.ICE,"Артиллерия",artLore,"art");
        inventory.setItem(20,art);

        List<String> troopsLore = new ArrayList<>();
        troopsLore.add(Tools.colorText( "&fह&d" + (army.getTroops()/1000) + "k&f/&7" +army.getArmySize() + "k"));
        troopsLore.add(Tools.colorText( "&fᠩ&a" + army.getMorale() + "&f/&a" + army.getMaxMorale()));
        troopsLore.add(Tools.colorText( "&fᠧ&f" + army.getDiscipleColor()));
        troopsLore.add( Tools.colorText( "&fᠨ&a" + army.getTactic()));
        troopsLore.add(Tools.colorText( "&fᢰ&f" + army.getCavRatioColor()));
        if(army.isRetreat()){
            troopsLore.add("Отступает");
        }
        if(army.isBattle()){
            troopsLore.add("В битве");
        }

        ItemStack troops = Tools.createItemLegacy(Material.EGG,"Ваши войска", troopsLore,"manpower");
        inventory.setItem(2,troops);

        List<String> townLore = new ArrayList<>();
        townLore.add(ChatColor.WHITE + "Местоположение армии");


        String locName;
        Town t = db.getTownAtChunk(army.getData().getLocation());

        if (t != null) {
            locName = t.getName();
            townLore.add(ChatColor.WHITE + "Город " + t.getOwnerName() + "'a");

            // 2. Проверяем статус отношений с этим контроллером (всего ОДИН блок вместо двух)
            if (army.isAllyLoc()) {
                townLore.add(ChatColor.GREEN + "Союзная территория");
            }
            else if (army.isEnemyLoc()) {
                townLore.add(ChatColor.RED + "Вражеская территория");

                // Проверка на текущую осаду
                if (army.isSieging()) {
                    townLore.add(ChatColor.WHITE + "ЛКМ - чтобы открыть меню осады");
                } else {
                    townLore.add(ChatColor.WHITE + "ЛКМ - чтобы начать осаду");
                }
            }
            else{
                townLore.add(ChatColor.GRAY + "Нейтральная территория");
            }

        } else {
            locName = "Территория варваров";
        }


        ItemStack armyLocation = Tools.createItemLegacy(Material.CAMPFIRE,locName, townLore);
        inventory.setItem(8,armyLocation);

        List<String> splitDesc = List.of(
                Tools.colorText("&7Полки с неполной заполненностью буду объединены"),
                Tools.colorText("&aSHIFT &7+ &eЛКМ&7 чтобы полки с 0 заполненностью не распустились"));
        ItemStack split = Tools.createItemLegacy(Material.LEAD,Tools.colorText("&dОбъединить&f полки"),splitDesc);
        inventory.setItem(25, split);

        List<String> upgradeDesc = List.of(
                Tools.colorText("&7Полки будут улучшены до "),
                Tools.colorText("&7максимально доступного уровня"));
        ItemStack upgrade = Tools.createItemLegacy(Material.NETHERITE_UPGRADE_SMITHING_TEMPLATE,Tools.colorText("&dУлучшить&f полки"),upgradeDesc);
        ItemMeta meta = upgrade.getItemMeta();
        meta.addItemFlags(ItemFlag.HIDE_ADDITIONAL_TOOLTIP);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        upgrade.setItemMeta(meta);
        inventory.setItem(24, upgrade);


        List<String> deleteDesc = List.of(Tools.colorText("&eПроизойдёт автоматическое объединение полков"));
        ItemStack delete = Tools.createItemLegacy(Material.BARRIER,Tools.colorText("&dРаспустить&f армию"),deleteDesc);
        inventory.setItem(26, delete);







    }
}
