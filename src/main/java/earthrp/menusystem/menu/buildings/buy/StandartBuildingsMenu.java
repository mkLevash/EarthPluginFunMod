package earthrp.menusystem.menu.buildings.buy;

import earthrp.Earth;
import earthrp.customEnums.EPlayerTech;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.BuildingsMenu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static earthrp.tools.PDCKeys.*;

public class StandartBuildingsMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    Player p = this.menuUtility.getOwner();
    UUID uuid = this.menuUtility.getOwner().getUniqueId();
    EPlayer player;
    double costMod = player.getAttribute(EPlayerAttribute.BUILDING_COST);
    double scienceCostMod = player.getScienceBuildingCost();
    int pastureCost = (int) Math.ceil(48 * costMod);
    boolean tPasture = player.getTech(EPlayerTech.PASTURE);
    int farmCost = (int) Math.ceil(48 * costMod);
    boolean tFarm = true;
    int lumberCost = (int) Math.ceil(64 * costMod);
    boolean tLumber = player.getTech(EPlayerTech.LUMBER);
    int careerCost = (int) Math.ceil(128 * costMod);
    boolean tCareer = player.getTech(EPlayerTech.QUARRY);
    int mineV2Cost = (int) Math.ceil(96 * costMod);
    boolean tMineV2 = player.getTech(EPlayerTech.PIT);
    int mineV1Cost = (int) Math.ceil(64 * costMod);
    boolean tMineV1 = player.getTech(EPlayerTech.MINE);
    int factoryCost = (int) Math.ceil(320 * costMod);
    boolean tFactory = player.getTech(EPlayerTech.FACTORY);
    int plantCost = (int) Math.ceil(160 * costMod);
    boolean tPlant = player.getTech(EPlayerTech.MANUFACTURE);
    int universityCost = (int) Math.ceil(240 * scienceCostMod);
    boolean tUniversity  = player.getTech(EPlayerTech.UNIVERSITY);
    int schoolCost = (int) Math.ceil(64 * scienceCostMod);
    boolean tSchool= player.getTech(EPlayerTech.SCHOOL);
    int diplomacyCost = (int) Math.ceil(32 * costMod);
    boolean tDiplomacy= player.getTech(EPlayerTech.DIPLOMACY);
    int bankCost = (int) Math.ceil(128 * costMod);
    boolean tBank = player.getTech(EPlayerTech.BANK_BASE);
    int marketCost= (int) Math.ceil(64 * costMod);
    boolean tMarket= player.getTech(EPlayerTech.TRADE);
    int portCost = marketCost;
    boolean tPort= player.getTech(EPlayerTech.SHIPPING);
    public StandartBuildingsMenu(MenuUtility menuUtility, Earth earthPlugin){
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
        player = db.getPlayer(uuid);
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


        double treasury = db.getPlayer(uuid).getAttribute(EPlayerAttribute.TREASURY);
        ItemStack item = e.getCurrentItem();
        if(item != null){
            if(item.getType().equals(Material.BARRIER)){
                e.getWhoClicked().closeInventory();
                new BuildingsMenu(menuUtility, this.earthPlugin).open();
            }
            PersistentDataContainer data = item.getItemMeta().getPersistentDataContainer();

            if(data.has(buildingTechCheckKey)){

                boolean techCheck = data.get(buildingTechCheckKey, PersistentDataType.BOOLEAN);
                int cost = data.get(buildingCostKey, PersistentDataType.INTEGER);
                String name = data.get(buildingNameKey, PersistentDataType.STRING);
                String type = data.get(buildingTypeKey, PersistentDataType.STRING);
                if(techCheck && treasury >= cost){
                    e.getWhoClicked().closeInventory();
                    new StandartBuildingsMenu(menuUtility,this.earthPlugin).open();
                    Tools.buyBuilding(player,cost);
                    ItemStack building = Tools.createBuilding(item.getType(),name,type);
                    p.getInventory().addItem(building);
                }
            }
        }

    }

    @Override
    public void setMenuItems() {


        List<String> pastureLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(32*costMod)+"&dx&fБревно, &6"+(int) Math.ceil(12*costMod)+"&dx&fСноп сена"),
                Tools.colorText("&fПозволяет разводить скот."),
                Tools.colorText("&fПроизводительность &a1")
                ));
        ItemStack pasture = Tools.createBuildingBuy(Material.LEATHER,"Пастбище","pasture",pastureLore,pastureCost,tPasture);

        List<String> farmLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(16*costMod)+"&dx&fБревно; &6"+(int) Math.ceil(32*costMod)+"&dx&fБлок земли; &6"+(int) Math.ceil(6*costMod)+"&dx&fВедро с водой"),
                Tools.colorText(" "),
                Tools.colorText("&fПозволяет выращивать культуры на грядках."),
                Tools.colorText("&fПроизводительность &a1")
        ));
        ItemStack farm = Tools.createBuildingBuy(Material.WHEAT,"Плантация","farm",farmLore,farmCost,tFarm);

        List<String> lumberLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(128*costMod)+"&dx&fБревно; &6"+(int) Math.ceil(1*costMod)+"&dx&fЖелезный блок,Камнерез &6"),
                Tools.colorText(" "),
                Tools.colorText("&fДобывает древесину/кору."),
                Tools.colorText("&fПроизводительность &a1")
        ));
        ItemStack lumber = Tools.createBuildingBuy(Material.OAK_LOG,"Лесопилка","lumber",lumberLore,lumberCost,tLumber);

        List<String> careerLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(128*costMod)+"&dx&fКаменный кирпич; &6"+(int) Math.ceil(64*costMod)+"&dx&fБревно,Фонарь; &6"+(int) Math.ceil(16*costMod)+"&dx&fПорох"),
                Tools.colorText("&6"+(int) Math.ceil(2*costMod)+"&dx&fАлмазный блок; &6"+(int) Math.ceil(6*costMod)+"&dx&fЖелезный блок;"),
                Tools.colorText("&6"+(int) Math.ceil(5*costMod)+"&dx&fКамнерез,Точило; &6"+(int) Math.ceil(3*costMod)+"&dx&fНаковальня;"),
                Tools.colorText(" "),
                Tools.colorText("&fМожет добывать алмазы/кристаллы. "),
                Tools.colorText("&fПроизводительность &a2")
        ));
        ItemStack career = Tools.createBuildingBuy(Material.DIAMOND_PICKAXE,"Карьер","career",careerLore,careerCost,tCareer);

        List<String> mineV2Lore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(128*costMod)+"&dx&fБулыжник; &6"+(int) Math.ceil(32*costMod)+"&dx&fБревно, Фонарь;"),
                Tools.colorText("&6"+(int) Math.ceil(4*costMod)+"&dx&fЖелезный блок; &6"+(int) Math.ceil(3*costMod)+"&dx&fКамнерез, Плавильня"),
                Tools.colorText(" "),
                Tools.colorText("&fМожет добывать: золото,"),
                Tools.colorText("&fобработанные каменные блоки,"),
                Tools.colorText("&fнеобработанное железо."),
                Tools.colorText(" "),
                Tools.colorText("&fПроизводительность &a1.5"),
                Tools.colorText("&fЕдиница золота = &610&f$")
        ));
        ItemStack mineV2 = Tools.createBuildingBuy(Material.IRON_PICKAXE,"Рудник","mineV2",mineV2Lore,mineV2Cost,tMineV2);

        List<String> mineV1Lore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(128*costMod)+"&dx&fБулыжник, &6"+(int) Math.ceil(32*costMod)+"&dx&fБревно,Факел"),
                Tools.colorText("&6"+(int) Math.ceil(2*costMod)+"&dx&fКамнерез,Железный блок;"),
                Tools.colorText(" "),
                Tools.colorText("&fМожет добывать: уголь, необр. медь"),
                Tools.colorText("&fнеобработанные каменные блоки,"),
                Tools.colorText(" "),
                Tools.colorText("&fПроизводительность &a1")
        ));
        ItemStack mineV1 = Tools.createBuildingBuy(Material.STONE_PICKAXE,"Шахта","mineV1",mineV1Lore,mineV1Cost,tMineV1);

        List<String> factoryLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(128*costMod)+"&dx&fКирпичный блок,Бревно; &6"+(int) Math.ceil(64*costMod)+"&dx&fФонарь,Порох;"),
                Tools.colorText("&6"+(int) Math.ceil(3*costMod)+"&dx&fСтол кузнеца,Наковальня,Плавильня;"),
                Tools.colorText("&6"+(int) Math.ceil(5*costMod)+"&dx&fАлмазный блок,Железный блок,Угольный блок"),
                Tools.colorText(" "),
                Tools.colorText("&fПроизводит &a3&f ресурса, которые"),
                Tools.colorText("&fуже производятся или &a2 &dпороха &f"),
                Tools.colorText(" "),
                Tools.colorText("&fМожет перерабатывать сырые"),
                Tools.colorText("&fресурсы в готовые изделия")
        ));
        ItemStack factory = Tools.createBuildingBuy(Material.SMITHING_TABLE,"Завод","factory",factoryLore,factoryCost,tFactory);

        List<String> plantLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(128*costMod)+"&dx&fКаменный кирпич; &6"+(int) Math.ceil(32*costMod)+"&dx&fБревно,Фонарь;"),
                Tools.colorText("&6"+(int) Math.ceil(6*costMod)+"&dx&fВерстак,Камнерез,Точило;"),
                Tools.colorText("&6"+(int) Math.ceil(3*costMod)+"&dx&fЖелезный блок,Угольный блок"),
                Tools.colorText(" "),
                Tools.colorText("&fПроизводит &a2&f ресурса, которые"),
                Tools.colorText("&fуже производятся или &a1 &dпорох"),
                Tools.colorText(" "),
                Tools.colorText("&fМожет перерабатывать сырые"),
                Tools.colorText("&fресурсы в готовые изделия")
        ));
        ItemStack plant = Tools.createBuildingBuy(Material.CRAFTING_TABLE,"Мануфактура","plant",plantLore,plantCost,tPlant);

        List<String> universityLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(64*costMod)+"&dx&fКнижная полка,Стекло; &6"+(int) Math.ceil(32*costMod)+"&dx&fБревно,Фонарь"),
                Tools.colorText("&6"+(int) Math.ceil(5*costMod)+"&dx&fКафедра,Большой сундук;"),
                Tools.colorText(" "),
                Tools.colorText("&fУвеличивает прирост ОИ на &a3."),
                Tools.colorText("&fПроизводит книги."),
                Tools.colorText("&fПотребляет бумагу"),
                Tools.colorText("&eМожно построить только одну в городе")
        ));
        ItemStack university = Tools.createBuildingBuy(Material.ENCHANTED_BOOK,"Университет","university",universityLore,universityCost,tUniversity);

        List<String> schoolLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(8*costMod)+"&dx&fКнижная полка,Сундук; &6"+(int) Math.ceil(1*costMod)+"&dx&fКафедра;"),
                Tools.colorText("&6"+(int) Math.ceil(16*costMod)+"&dx&fБревно,Факел,Стекло"),
                Tools.colorText(" "),
                Tools.colorText("&fУвеличивает прирост ОИ на &a1."),
                Tools.colorText("&fПроизводит бумагу"),
                Tools.colorText("&eМожно построить только одну в городе")
        ));
        ItemStack school = Tools.createBuildingBuy(Material.BOOK,"Школа","school",schoolLore,schoolCost,tSchool);

        List<String> diplomacyLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&f+1 к торговым отношениям и мнению "),
                Tools.colorText("&fсо страной, где построено посольство."),
                Tools.colorText(" "),
                Tools.colorText("&fВ посольство можно телепортироваться"),
                Tools.colorText("&fиз своей столицы"),
                Tools.colorText(" "),
                Tools.colorText("&fМожно строить только в столицах")
        ));
        ItemStack diplomacy = Tools.createBuildingBuy(Material.PAPER,"Посольство","diplomacy",diplomacyLore,diplomacyCost,tDiplomacy);

        List<String> bankLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(8*costMod)+"&dx&fБольшой сундук; &6"+(int) Math.ceil(32*costMod)+"&dx&fБумага"),
                Tools.colorText("&6"+(int) Math.ceil(16*costMod)+"&dx&fСтекло,Бревно"),
                Tools.colorText(" "),
                Tools.colorText("&fПроцентная ставка - 10%"),
                Tools.colorText("&fЕдиничный долг - 10$.")
        ));
        ItemStack bank = Tools.createBuildingBuy(Material.ENDER_CHEST,"Банк","bank",bankLore,bankCost,tBank);

        List<String> marketLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(18*costMod)+"&dx&fЗолото,Сундук; &6"+(int) Math.ceil(16*costMod)+"&dx&fПеро,Бревно"),
                Tools.colorText(" "),
                Tools.colorText("&fДает возможность продавать товары")
        ));
        ItemStack market = Tools.createBuildingBuy(Material.BELL,"Рынок","landHub",marketLore,marketCost,tMarket);

        List<String> portLore = new ArrayList<>(Arrays.asList(
                Tools.colorText("&6"+(int) Math.ceil(32*costMod)+"&dx&fБочка; &6"+(int) Math.ceil(64*costMod)+"&dx&fБревно"),
                Tools.colorText(" "),
                Tools.colorText("&fДает возможность использовать корабли"),
                Tools.colorText("&fНе занимает ячейку строительства"),
                Tools.colorText("&aБонус рынка +10%")
        ));
        ItemStack port = Tools.createBuildingBuy(Material.BARREL,"Порт","port",portLore,portCost,tPort);


        ItemStack back = new ItemStack(Material.BARRIER, 1);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.RED + "BACK");
        back.setItemMeta(backMeta);

        inventory.setItem(0, lumber);
        inventory.setItem(9, pasture);
        inventory.setItem(18, farm);

        inventory.setItem(2, career);
        inventory.setItem(11, mineV2);
        inventory.setItem(20, mineV1);

        inventory.setItem(4, factory);
        inventory.setItem(13, plant);

        inventory.setItem(6, university);
        inventory.setItem(15, school);

        inventory.setItem(8, diplomacy);
        inventory.setItem(17, bank);
        inventory.setItem(26, market);
        inventory.setItem(35, port);

        inventory.setItem(44, back);

    }
}
