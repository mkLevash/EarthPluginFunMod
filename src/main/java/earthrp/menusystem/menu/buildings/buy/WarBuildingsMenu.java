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

import static earthrp.tools.Tools.*;
import static earthrp.tools.PDCKeys.*;

public class WarBuildingsMenu extends Menu {
    private final Earth earthPlugin;
    private final ServerDatabase db;
    Player p = this.menuUtility.getOwner();
    UUID uuid = p.getUniqueId();
    EPlayer player;
    double costMod = player.getWarBuildingCost();
    double traditionMod = 1.0;
    boolean tBarack = player.getTech(EPlayerTech.INF2);
    boolean tStable = player.getTech(EPlayerTech.CAV2);
    boolean tGunFactory = player.getTech(EPlayerTech.ART1);
    boolean tFort = player.getTech(EPlayerTech.FORT);
    boolean tForge = player.getTech(EPlayerTech.FORGE);
    boolean tShipyard = player.getTech(EPlayerTech.SHIPYARD);
    int barrackCost = (int) Math.ceil(128*costMod);
    int stableCost = (int) Math.ceil(160*costMod);
    int gunFactoryCost = (int) Math.ceil(256*costMod);
    int fortCost = (int) Math.ceil(48*costMod);
    int forgeCost = (int) Math.ceil(64*costMod);
    int shipyardCost = (int) Math.ceil(32*costMod);


    public WarBuildingsMenu(MenuUtility menuUtility, Earth earthPlugin) {
        super(menuUtility);
        this.earthPlugin = earthPlugin;
        db = Earth.getInstance().getServerDatabase();
        player = db.getPlayer(uuid);
        if(player.getAttribute(EPlayerAttribute.TRADITION)>=20){
            traditionMod = 0.25;
        }
        barrackCost = (int) Math.ceil(barrackCost*traditionMod);
        stableCost = (int) Math.ceil(stableCost*traditionMod);
        gunFactoryCost = (int) Math.ceil(gunFactoryCost*traditionMod);
    }




    @Override
    public String getMenuName() {
        return "Военные";
    }

    @Override
    public int getSlots() {
        return 18;
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



        List<String> barrackLore = new ArrayList<>(Arrays.asList(
                Tools.colorText( "&6"+(int) Math.ceil(64*costMod*traditionMod)+"&dx&fБревно,Стрел; &6"+(int) Math.ceil(16*costMod*traditionMod)+"&dx&fМишень,Перо"),
                (" "),
                Tools.colorText( "&a20 &fТрадиций для скидки"),
                (" "),
                Tools.colorText("&fПозволяет создавать Пехоту 2+ уровня.")
        ));
        ItemStack barrack = Tools.createBuildingBuy(Material.ICE,"Казарма","barrack",barrackLore,barrackCost,tBarack, "inf");


        List<String> stableLore = new ArrayList<>(Arrays.asList(
                Tools.colorText( "&6"+(int) Math.ceil(64*costMod*traditionMod)+"&dx&fБревно; &6"+(int) Math.ceil(32*costMod*traditionMod)+"&dx&fПеро,Кожа,Мишень"),
                (" "),
                Tools.colorText( "&a20 &fТрадиций для скидки"),
                (" "),
                Tools.colorText("&fПозволяет создавать Кавалерию.")
        ));
        ItemStack stable = Tools.createBuildingBuy(Material.ICE, "Конюшня","stable",stableLore,stableCost,tStable, "cav");


        List<String> gunFactoryLore = new ArrayList<>(Arrays.asList(
                Tools.colorText( "&6"+(int) Math.ceil(256*costMod*traditionMod)+"&dx&fБревно; &6"+(int) Math.ceil(64*costMod*traditionMod)+"&dx&fПорох,Каменный Кирпич"),
                Tools.colorText( "&6"+(int) Math.ceil(6*costMod*traditionMod)+"&dx&fЖелезный блок,Алмазный блок"),
                (" "),
                Tools.colorText( "&a20 &fТрадиций для скидки"),
                (" "),
                Tools.colorText("&fПозволяет создавать Артиллерию.")
        ));
        ItemStack gunFactory = Tools.createBuildingBuy(Material.ICE,"Оружейная фабрика","gunFactory",gunFactoryLore,gunFactoryCost,tGunFactory,"art");

        List<String> fortLore = new ArrayList<>(Arrays.asList(
                Tools.colorText( "&6"+(int) Math.ceil(128*costMod)+"&dx&fКаменный кирпич; &6"+(int) Math.ceil(64*costMod)+"&dx&fФакел"),
                (" "),
                Tools.colorText("&fНе даёт противнику оккупировать город."),
                Tools.colorText("&fНужно будет провести &5осаду."),
                Tools.colorText("&fПредупреждает о врагах в области &5250."),
                Tools.colorText("&fНе занимает &6ячейку строительства&f")
        ));
        ItemStack fort = Tools.createBuildingBuy(Material.STONE_BRICKS,"Крепость","fort",fortLore,fortCost,tFort);


        List<String> forgeLore = new ArrayList<>(Arrays.asList(
                Tools.colorText( "&6"+(int) Math.ceil(16*costMod)+"&dx&fБревно, &6"+(int) Math.ceil(6*costMod)+"&dx&fНаковальня,Ведро лавы"),
                (""),
                Tools.colorText("&a+5%&d к резисту&f в фазе&6 шока."),
                Tools.colorText("&fПроизводит &dОружие&f,"),
                Tools.colorText("&fМожно построить&6 только одну&f в городе.")
        ));
        ItemStack forge = Tools.createBuildingBuy(Material.ANVIL,"Кузня","forge",forgeLore,forgeCost,tForge);

        List<String> shipyardLore = new ArrayList<>(Arrays.asList(
                Tools.colorText( "&6"+(int) Math.ceil(32*costMod)+"&dx&fБревно, &6"+(int) Math.ceil(12*costMod)+"&dx&fБочка"),
                Tools.colorText( "&6"+(int) Math.ceil(1*costMod)+"&dx&fЖелезный блок"),
                (""),
                Tools.colorText("&fОткрывает возможность строить корабли."),
                Tools.colorText("&2+5&f Лимит флота"),
                Tools.colorText("&fМожно построить&6 только одну&f в городе.")
        ));
        ItemStack shipyard = Tools.createBuildingBuy(Material.ICE,"Верфь","shipyard",shipyardLore,shipyardCost,tShipyard,"ship");


        ItemStack next = new ItemStack(Material.BARRIER, 1);
        ItemMeta nextMeta = next.getItemMeta();
        nextMeta.setDisplayName(ChatColor.RED + "BACK");
        next.setItemMeta(nextMeta);


        inventory.setItem(3, barrack);
        inventory.setItem(4, stable);
        inventory.setItem(5, gunFactory);

        inventory.setItem(12, fort);
        inventory.setItem(13, forge);
        inventory.setItem(14, shipyard);
//
//        inventory.setItem(6, tech7);
//
//        inventory.setItem(7, tech8);

        inventory.setItem(17, next);

    }
}
