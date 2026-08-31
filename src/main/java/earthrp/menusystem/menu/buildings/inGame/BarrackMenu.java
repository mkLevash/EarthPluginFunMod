package earthrp.menusystem.menu.buildings.inGame;

import earthrp.Earth;
import earthrp.customEnums.*;
import earthrp.customObjects.*;
import earthrp.database.ServerDatabase;
import earthrp.configs.CustomConfig;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.DeleteConfirmMenu;
import earthrp.menusystem.menu.TechnologyMenu;
import earthrp.menusystem.menu.TownBuildingsMenu;
import earthrp.tools.Tools;
import io.papermc.paper.persistence.PersistentDataContainerView;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class BarrackMenu extends Menu {
    private final Building b = menuUtility.getBuilding();
    private int unitLvl = (int) b.getOwner().getAttribute(EPlayerAttribute.INF_LVL);
    private UnitTech unitTech = UnitTech.valueOf("INF"+unitLvl);
    private Map<Material,Integer> materials = unitTech.getMaterials();

    public BarrackMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    private static ItemStack fillerGlass(){
        return Tools.createItemLegacy(Material.WHITE_STAINED_GLASS_PANE," ",null, UUID.randomUUID().toString());
    }

    @Override
    public String getMenuName() {


        return b.getData().getType().getDisplayName();
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ServerDatabase db = Earth.getInstance().getDatabase();
        Army army = db.getArmy(b.getData().getArmyId());
        Player javaPlayer = (Player) e.getWhoClicked();
        EPlayer ePlayer = db.getPlayer(javaPlayer);
        ItemStack item = e.getCurrentItem();
        if(item == null) return;
        int rawSlot = e.getRawSlot();
        int topSize = e.getView().getTopInventory().getSize();
        boolean success = false;
        if(item.getType().equals(Material.BARRIER)){
            e.getWhoClicked().closeInventory();
            if(menuUtility.getTown()!=null){
                new TownBuildingsMenu(menuUtility).open();
            }else{
                menuUtility.setDeleteBuilding(b);
                new DeleteConfirmMenu(menuUtility).open();
            }
        }
        if (rawSlot < topSize) {
            // Нажатие по меню
            PersistentDataContainerView data = item.getPersistentDataContainer();
            if(data.has(menuIdKey)){
                switch (data.get(menuIdKey, PersistentDataType.STRING)){
                    case "tech" ->{
                        new TechnologyMenu(menuUtility).open();
                    }
                    case "merc" ->{
                        if(
                                b.getOwner().getMercAmount()<b.getOwner().getManpowerLimit()/1000
                                        && army!=null
                                        && b.getOwner().getAttribute(EPlayerAttribute.TREASURY)>=18
                        ){
                            ArmyUnit unit = new ArmyUnit(unitTech,UUID.randomUUID(),army.getUuid(),"");
                            b.getOwner().addAttribute(EPlayerAttribute.TREASURY,-18);

                            unit.getData().setMerc(true);
                            army.addUnit(unit);
                            success = true;
                        }
                    }

                    case "levies" ->{

                        if(!b.getOwner().isLevies() && b.getOwner().isWar()){
                            if(army == null){
                                UUID armyId = UUID.randomUUID();
                                army = new Army(armyId,b.getOwner().getUniqueId(),"");
                                db.addArmy(army);
                                b.getData().setArmyId(armyId);
                            }
                            for (int i = 0; i < b.getOwner().getManpowerLimit()/1000; i++) {
                                ArmyUnit unit = new ArmyUnit(unitTech,UUID.randomUUID(),army.getUuid(),"");
                                unit.getData().setLevies(true);
                                army.addUnit(unit);
                            }
                            b.getOwner().setLevies(true);
                            success = true;
                        }



                    }

                    case "regular" ->{
                        if(tryPayment(javaPlayer,materials)&&army!=null){
                            ArmyUnit unit = new ArmyUnit(unitTech,UUID.randomUUID(),army.getUuid(),"");

                            army.addUnit(unit);
                            success = true;
                        }
                    }
                    case "create" ->{
                        UUID armyId = UUID.randomUUID();
                        army = new Army(armyId,b.getOwner().getUniqueId(),"");
                        army.setLocation(b.getLocation());
                        db.addArmy(army);
                        b.getData().setArmyId(armyId);
                        success = true;



                    }
                    case "army" ->{


                        ItemStack owner = new ItemStack(Material.PLAYER_HEAD);
                        SkullMeta ownerMeta = (SkullMeta) owner.getItemMeta();
                        ownerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(b.getOwner().getUniqueId()));
                        ownerMeta.getPersistentDataContainer().set(armyOwnerKey, PersistentDataType.STRING, b.getOwner().getUniqueId().toString());
                        ownerMeta.getPersistentDataContainer().set(armyIdKey, PersistentDataType.STRING, army.getUuid().toString());
                        ownerMeta.setDisplayName(b.getOwner().getDisplayName());
                        owner.setItemMeta(ownerMeta);

                        ItemStack shulkerItem = new ItemStack(Material.SHULKER_BOX);
                        BlockStateMeta bsm = (BlockStateMeta) shulkerItem.getItemMeta();
                        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
                        shulker.getInventory().addItem(owner);
                        bsm.setBlockState(shulker);
                        shulker.update();
                        bsm.setDisplayName("Армия " + b.getOwner().getCountryName());
                        shulkerItem.setItemMeta(bsm);

                        ePlayer.takeArmy(army);
                        army.setLocation(javaPlayer.getLocation());
                        int amplifier = 5 - (ePlayer.getMaxLeaderMovement() / 2 );
                        PotionEffect slowness = new PotionEffect(PotionEffectType.SLOWNESS, -1, amplifier);
                        PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING, -1, 1);
                        javaPlayer.addPotionEffect(slowness);
                        javaPlayer.addPotionEffect(glowing);

                        javaPlayer.getInventory().addItem(shulkerItem);
                        b.getData().setArmyId(null);
                        success = true;
                    }
                }
            }
        } else {
            if(Tag.SHULKER_BOXES.isTagged(item.getType())){
                if (item.hasItemMeta() && item.getItemMeta() instanceof BlockStateMeta bsm) {

                    if (bsm.getBlockState() instanceof ShulkerBox shulkerBox) {
                        army = Tools.getArmyFromInventory(shulkerBox.getInventory());
                        if (army != null){
                            ePlayer.placeArmy(army);
                            if(ePlayer.getData().armiesInHand.isEmpty()){
                                javaPlayer.clearActivePotionEffects();
                            }
                            b.getData().setArmyId(army.getUuid());
                            item.setAmount(0);
                            success = true;

                        }
                    }
                }
            }

        }

        if (success) {
            inventory.clear();
            this.setMenuItems();
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        ServerDatabase db = Earth.getInstance().getDatabase();
        ItemStack town = new ItemStack(Material.END_CRYSTAL, 1);
        ItemMeta townMeta = town.getItemMeta();
        Town t = Earth.getInstance().getDatabase().getTown(b.getTownId());
        townMeta.displayName(colorText("<light_purple>"+t.getName()));
        townMeta.lore(List.of(colorText("<white>Местоположение здания")));
        town.setItemMeta(townMeta);




        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));
        if(menuUtility.getTown()!=null){
            deleteMeta.displayName(Tools.deserialize("Назад"));
            deleteMeta.lore(List.of());
        }
        delete.setItemMeta(deleteMeta);

        ItemStack buildingItem = menuUtility.getBuildingItem();

        List<String> sLore = new ArrayList<>();
        sLore.add("Стоимость: <gold>18<white>$");
        sLore.add("Содержание: <gold>"+ (1 + unitLvl) + "<white>$");
        sLore.add("Лимит: " + b.getOwner().getMercAmount() + "/" + b.getOwner().getManpowerLimit()/1000);
        ItemStack merc = makeItem("Нанять наёмников " + unitLvl + " уровня","merc","inf",sLore);

        sLore = new ArrayList<>();
        if(!b.getOwner().isWar()){
            sLore.add("<red>Доступно только во время войны!");
        }
        if (b.getOwner().isLevies()){
            sLore.add("<red>Уже поднято!");
        }

        sLore.add("Лимит: " + b.getOwner().getManpowerLimit() / 1000);
        ItemStack levies = makeItem("Собрать ополчение " + unitLvl + " уровня","levies","inf",sLore);






        sLore = new ArrayList<>();
        sLore.add("Стоимость:");
        sLore.add("<gold>9 <white>$");
        sLore.addAll(CustomConfig.get().getStringList("tech.lore.inf"+unitLvl));
        sLore.add("Содержание: 1$");
        ItemStack regular = makeItem("Нанять пехоту " + unitLvl + " уровня","regular","inf",sLore);
        ItemMeta meta = regular.getItemMeta();
        if (meta != null) {

            List<Component> componentLore = meta.lore();
            var mm = net.kyori.adventure.text.minimessage.MiniMessage.miniMessage();
            int insertIndex = 2;

            for (Map.Entry<Material, Integer> entry : materials.entrySet()) {
                Material m = entry.getKey();
                int amount = entry.getValue();

                // Строим динамический компонент: Число (золотое) + Пробел + Перевод материала (бирюзовый)
                var color = net.kyori.adventure.text.format.NamedTextColor.RED;
                if(checkMaterial(menuUtility.getOwner(),m,amount)){
                    color = net.kyori.adventure.text.format.NamedTextColor.GREEN;
                }
                Component materialLine = Component.text()
                        .append(mm.deserialize("<gold>" + amount + " "))
                        .append(Component.translatable(m.translationKey()).color(color))
                        .build();

                componentLore.add(insertIndex++, materialLine);
            }

            // Записываем обновленный лор обратно в предмет
            meta.lore(componentLore);
            regular.setItemMeta(meta);
        }

        sLore = new ArrayList<>();
        ItemStack armyShulker;
        Army army = db.getArmy(b.getData().getArmyId());
        if(army==null){
            sLore.add("<yellow>ЛКМ<white> чтобы создать армию");
            armyShulker = makeItem(Material.SHULKER_BOX,"Отсутствует армия","create","inf",sLore);

        }else{
            sLore.add("ह<light_purple>" + (army.getTroops()/1000) + "<white>k/<gray>" +army.getArmySize() + "<white>k");
            sLore.add("ᠩ<dark_green>" + army.getMorale() + "<white>/<dark_green>" + army.getMaxMorale());
            sLore.add("ᠧ" + army.getDiscipleColored());
            sLore.add( "ᠨ<aqua>" + army.getTactic());
            sLore.add("ᢰ" + army.getCavRatioColored());
            armyShulker = makeItem(Material.SHULKER_BOX,"Армия " +army.getOwner().getDisplayName(),"army","inf",sLore);
        }




        


        inventory.setItem(3, town);
        inventory.setItem(4, buildingItem);

        inventory.setItem(5, delete);


        for (int i = 0; i < 10; i++) {
            fillIfEmpty(i);
        }
        fillIfEmpty(17);
        fillIfEmpty(26);
        fillIfEmpty(18);
        fillIfEmpty(27);

        for (int i = 35; i < 45; i++) {
            fillIfEmpty(i);
        }

        for (int i = 10; i < 17; i++) {
            inventory.setItem(i,fillerGlass());
        }

        inventory.setItem(19,fillerGlass());

        inventory.setItem(20,merc);

        if(b.getOwner().getTech(EPlayerTech.BANNER)){
            inventory.setItem(22,levies);
        }

        inventory.setItem(24,regular);



        inventory.setItem(21,fillerGlass());
        inventory.setItem(22,fillerGlass());
        inventory.setItem(23,fillerGlass());
        inventory.setItem(25,fillerGlass());

        for (int i = 28; i < 35; i++) {
            inventory.setItem(i,fillerGlass());
        }

        inventory.setItem(31,armyShulker);


    }

    private boolean checkMaterial(Player player, Material material, int amount) {
        return player.getInventory().containsAtLeast(new ItemStack(material), amount);
    }

    private boolean tryPayment(Player player, Map<Material,Integer> requirements) {

        if (requirements.isEmpty()) return false;

        // Сначала проверяем наличие всех ресурсов
        for (Map.Entry<Material, Integer> entry : requirements.entrySet()) {

            if (!player.getInventory().containsAtLeast(new ItemStack(entry.getKey()), entry.getValue())) {

                return false;
            }
        }

        if(b.getOwner().getAttribute(EPlayerAttribute.TREASURY)<9){
            return false;
        }

        if(b.getOwner().getAttribute(EPlayerAttribute.MANPOWER)<1000){
            return false;
        }

        // Если всё есть — списываем
        for (Map.Entry<Material, Integer> entry : requirements.entrySet()) {
            player.getInventory().removeItem(new ItemStack(entry.getKey(), entry.getValue()));
        }
        b.getOwner().addAttribute(EPlayerAttribute.TREASURY,-9);
        b.getOwner().addAttribute(EPlayerAttribute.MANPOWER,-1000);
        return true;
    }
}


