package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.customEnums.EarthItem;
import earthrp.customEnums.UnitTech.UnitType;
import earthrp.customObjects.Army;
import earthrp.customObjects.Building;
import earthrp.menusystem.menu.army.SiegeMenu;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.markets.TradeMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;

import java.util.*;

import static earthrp.tools.PDCKeys.*;

public class TownsMenu extends Menu {

    Town t = menuUtility.getTown();
    public TownsMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    @Override
    public String getMenuName() {
        return "Меню города";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        Player p = (Player) e.getWhoClicked();
        switch (Objects.requireNonNull(e.getCurrentItem()).getType()){

            case CHEST -> {
                e.getWhoClicked().closeInventory();
                new TownItemsMenu(menuUtility).open();
            }

            case EGG -> {
                new TownBuildingsMenu(menuUtility).open();
            }
            
            case BELL -> {

                e.getWhoClicked().closeInventory();
                new TradeMenu(menuUtility).open();
            }

            case BARRIER -> {
                e.getWhoClicked().closeInventory();
                menuUtility.setDeleteTown(t);
                new DeleteConfirmMenu(menuUtility).open();
            }

            case PLAYER_HEAD -> {
                if(t.getOwner().getData().getEnemies().contains(menuUtility.getOwner().getUniqueId())){
                    p.closeInventory();
                    new AnnexConfirmMenu(menuUtility).open();
                }
                else if(!t.isCore()){
                    if(t.getOwner().getAttribute(EPlayerAttribute.POLIT_BALANCE)>=t.getCoreCost()){
                        p.closeInventory();
                        p.sendMessage(ChatColor.GREEN+"Город успешно национализирован");
                        t.setCore(true);
                        t.getOwner().addAttribute(EPlayerAttribute.POLIT_BALANCE,-t.getCoreCost());
                        new TownsMenu(menuUtility).open();
                    }else{
                        p.sendMessage(ChatColor.YELLOW+"У вас недостаточно полит власти");
                    }


                }

            }

            case END_CRYSTAL -> {
                if(t.isSiege()){
                    Army army = new Army(UUID.randomUUID(),t.getController().getUniqueId(),"");
                    for(Army a:t.getController().getArmies()){
                        if (a.getTypeTroops(UnitType.INF) > army.getTypeTroops(UnitType.INF)) army = a;
                    }
                    p.closeInventory();
                    menuUtility.setArmy(army);
                    menuUtility.setSiegeTown(t);
                    new SiegeMenu(menuUtility).open();
                }
            }
            case CRAFTING_TABLE -> {

                if(t.getOwner().getAttribute(EPlayerAttribute.POLIT_BALANCE)>=t.getInfrastructureCost()){
                    p.closeInventory();
                    p.sendMessage(ChatColor.GREEN+"Инфраструктура успешно увеличена");
                    t.getOwner().addAttribute(EPlayerAttribute.POLIT_BALANCE,-t.getInfrastructureCost());
                    t.getData().infrastructure+=1;

                    new TownsMenu(menuUtility).open();
                }else{
                    p.sendMessage(ChatColor.YELLOW+"У вас недостаточно полит власти");
                }

            }

        }

    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        ItemStack owner = null;
        for(ItemStack item : menuUtility.getBuildingChest().getContents()){
            if (item == null) continue;
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();
            if(data.has(townOwnerKey)){
                owner = item;

            }
        }

        List<String> ownerLore = new ArrayList<>();

        ownerLore.add(t.getCoreStatus());
        if(t.getOwner().getUniqueId().equals(menuUtility.getOwner().getUniqueId())){
            if(!t.isCore()){
                ownerLore.add("Стоимость национализации " + t.getCoreCost() + "£");
                ownerLore.add("<yellow>ЛКМ <white>- для национализации");
            }
        }
        if(!t.getData().getController().equals(t.getOwnerId())){
            ownerLore.add("Оккупирован <red>"+ Earth.getInstance().getDatabase().getPlayer(t.getData().getController()).getCountryName());
        }
        if(t.getOwner().getData().getEnemies().contains(menuUtility.getOwner().getUniqueId())){
            ownerLore.add("<yellow>ЛКМ <white>- для аннексии");
            ownerLore.add("<red>ТОЛЬКО ПО МИРНОМУ ДОГОВОРУ");
        }
        ItemStack ownerLegacy = Tools.createItem(Material.PLAYER_HEAD,"Владелец - <light_purple>"+t.getOwner().getCountryName(),ownerLore);
        SkullMeta skullMeta = (SkullMeta) ownerLegacy.getItemMeta();
        skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer(t.getOwnerId()));
        ownerLegacy.setItemMeta(skullMeta);
        if(owner == null){
            owner = ownerLegacy;
        }else{
            ItemMeta ownerMeta = owner.getItemMeta();
            ownerMeta.displayName(skullMeta.displayName());
            ownerMeta.lore(skullMeta.lore());
            owner.setItemMeta(ownerMeta);
        }





        Set<EarthItem> world = new HashSet<>();
        for (Building b:Earth.getInstance().getDatabase().getBuildings()){

            if(b.getData().getItem()!=null && b.getOwner()!=t.getOwner() && !t.getOwner().getTraders().contains(b.getOwner())  ) world.add(b.getData().getItem());
        }

        EPlayer player = t.getOwner();
        List<String> statsLore = List.of(
                "Крестьяне <green>" + t.getPeasant(),
                "Дворяне <green>" + t.getData().noble + "<white>/<yellow>" + t.getNobleSites(),
                "Налоги ("+ t.getTaxModColor() +")<green>" + t.getTaxIncome(),
                "Торговля <green>" + t.getTradeIncome(world),
                "Спец Здания = <light_purple>" + t.getSpecialBuildingsAmount() + "<white>/<yellow>" + t.getBuildSite(),
                "Инфраструктура <gold>" + t.getData().infrastructure + " <white>ур",
                "<yellow>ЛКМ <white> для повышения инфр. за <green>" + t.getInfrastructureCost() + "<white>£"
        );
        ItemStack stats = Tools.createItem(Material.CRAFTING_TABLE,"Основная информация о городе",statsLore);


        List<String> chestLore = List.of(
                Tools.colorText("&eЛКМ &f- для подробностей")

        );
        ItemStack chest = Tools.createItemLegacy(Material.CHEST,Tools.colorText("Склад &e" + t.getItemsColor()),chestLore);

        List<String> wheatLore = new ArrayList<>();
        wheatLore.add(Tools.colorText("&fПродовольствие: " + t.getFoodColor() + "&fइ"));
        wheatLore.add(Tools.colorText("&fПотребление пищи:" ));
        wheatLore.add(Tools.colorText("&fНаселением: " + t.getHungerColor() + "&fइ" ));
        if(t.isCapital()) wheatLore.add(Tools.colorText("&fАрмия = " + player.getUnits().size() * 5 + "&fइ"));


        ItemStack wheat = Tools.createItemLegacy(Material.WHEAT,Tools.colorText("&fСытость - " + t.getFamineColor()),wheatLore);


//        ItemStack owner = new ItemStack(Material.END_CRYSTAL, 1);
//        ItemMeta townMeta = town.getItemMeta();
//        townMeta.setDisplayName(ChatColor.LIGHT_PURPLE + t.getName());
//
//        town.setItemMeta(townMeta);




//
//        ItemStack market;
//        ItemMeta marketMeta;
//        if(t.getLandHubId()!=null){
//            Market m = this.earthPlugin.getServerDatabase().getMarket(t.getLandHubId());
//            playerMenuUtility.setMarket(m);
//            market = new ItemStack(Material.BELL, 1);
//            marketMeta = market.getItemMeta();
//            marketMeta.setDisplayName(townName);
//            marketMeta.setLore(List.of(ChatColor.WHITE + "Ресуры направляются на рынок данного города"));
//        }else {
//            market = new ItemStack(Material.SOUL_TORCH, 1);
//            marketMeta = market.getItemMeta();
//            marketMeta.setDisplayName(ChatColor.RED + "Рынок не выбран");
//            marketMeta.setLore(List.of(ChatColor.WHITE + "Нажмите чтобы выбрать рынок"));
//        }
//        market.setItemMeta(marketMeta);

        ItemStack delete = new ItemStack(Material.BARRIER,1);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить город");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет город"));
        delete.setItemMeta(deleteMeta);


        List<String> fortLore = new ArrayList<>();
        if(t.isFort()){

            fortLore.add("Воентех крепости: <green>" + (int) t.getController().getAttribute(EPlayerAttribute.FORT_LVL));


        }else{
            fortLore.add("<red>Крепость отсутствует!");
        }
        if(t.isSiege()){
            fortLore.add("<red>Город находится в осаде!");
            fortLore.add("Шанс захвата: " + t.getSiegeChanceColor(true));
            fortLore.add("<yellow>ЛКМ<white> - чтобы открыть меню осады");
        }
        ItemStack fort = Tools.createItem(Material.ICE,"<aqua>"+t.getName(),fortLore,"fort");






        inventory.setItem(0, owner);
        inventory.setItem(1, fort);
        inventory.setItem(4,makeItem("<gold>Здания","buildings","buildings"));

        inventory.setItem(3, stats);
        inventory.setItem(5, chest);
        inventory.setItem(6, wheat);

        inventory.setItem(8, delete);


    }
}
