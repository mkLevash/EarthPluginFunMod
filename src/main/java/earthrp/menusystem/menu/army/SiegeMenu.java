package earthrp.menusystem.menu.army;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.UnitTech.UnitType;
import earthrp.customObjects.Army;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.tools.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SiegeMenu extends Menu {
    public SiegeMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }
    private final ItemStack bItem = menuUtility.getBuildingItem();
    Town town = menuUtility.getSiegeTown();
    Army army = menuUtility.getArmy();
    private ServerDatabase db = Earth.getInstance().getDatabase();
    @Override
    public String getMenuName() {
        return "";
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if(e.getCurrentItem() != null){
            Army sieger = town.getSieger();
            Material item = e.getCurrentItem().getType();
            if (sieger == null) return;
            int def = (int) (3 * town.getController().getAttribute(EPlayerAttribute.FORT_LVL)) * 1000;
            int att = (int) (3 * sieger.getOwner().getAttribute(EPlayerAttribute.FORT_LVL) ) * 1000;
            boolean canStorm = army.getOwner().getData().getSiegeStorm().get(town.getId()) != Boolean.FALSE;

            if(item == Material.IRON_SWORD){
                if(army.getTypeTroops(UnitType.INF)>= def){
                    if(canStorm){
                        army.getOwner().getData().getSiegeStorm().put(town.getId(),false);
                        army.killInfantry(def);
                        town.getData().addSiegeChance(5);
                        int siegeChance = town.getSiegeChance();
                        int d = (int) (Math.random() * 100) + 1;
                        if(siegeChance>=d){
                            town.besieged();
                            e.getWhoClicked().closeInventory();
                        }else{
                            inventory.clear();
                            setMenuItems();
                        }
                    }else e.getWhoClicked().sendMessage("Это действие доступно только раз в день!");
                }else e.getWhoClicked().sendMessage("У вас недостаточно пехоты!");

            }
            if(item == Material.SHIELD){
                if(army.getTypeTroops(UnitType.INF)>= att){
                    if(canStorm){
                        army.killInfantry(att);
                        town.getData().addSiegeChance(-10);
                        army.getOwner().getData().getSiegeStorm().put(town.getId(),false);
                        inventory.clear();
                        setMenuItems();
                    }else e.getWhoClicked().sendMessage("Это действие доступно только раз в день!");
                }else e.getWhoClicked().sendMessage("У вас недостаточно пехоты!");
            }

        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        Army attacker = town.getSieger();

        int siegeAbility = (int) (attacker.getOwner().getAttribute(EPlayerAttribute.SIEGE_ABILITY) * 100 );
        int fortAbility = (int) (town.getOwner().getAttribute(EPlayerAttribute.FORT_ABILITY) * 100 );
        int art = 0;
        int siege = 0;
        for (Army army : town.getSiegeArmyList()){
            art += army.getArtilleryTroops() ;
            if(army.getData().getLeaderSiege()>siege){
                siege = army.getData().getLeaderSiege();
            }
        }



        List<String> lore = new ArrayList<>();
        lore.add(("Защитник - <green>"+ town.getController().getCountryName()));
        if(town.isFort()) lore.add(("Воентех крепостей <green>"+ (int) town.getOwner().getAttribute(EPlayerAttribute.FORT_LVL)));
        lore.add(("Защита крепости <green>" + fortAbility + "%"));
        lore.add(("Шанс захвата " + town.getSiegeChanceColor(town.getController().equals(army.getOwner()))));



        ItemStack townItem = Tools.createItem(Material.END_CRYSTAL,"<light_purple>"+town.getName(),lore);

        lore = new ArrayList<>();
        String name = "ЛКМ - чтобы потратить " + (3 * town.getOwner().getAttribute(EPlayerAttribute.FORT_LVL)) + " пехоты";
        lore.add(("И произвести штурм с шансом " + (town.getSiegeChance() + 5) + "%" ));
        ItemStack storm =  Tools.createItem(Material.IRON_SWORD,name,lore);


        lore = new ArrayList<>();
        name = "ЛКМ - чтобы потратить " + (3 * attacker.getOwner().getAttribute(EPlayerAttribute.FORT_LVL)) + " пехоты";
        lore.add(("И уменьшить шанс осады на <green>10%" ));
        ItemStack antiStorm =  Tools.createItem(Material.SHIELD,name,lore);

        lore = new ArrayList<>();
        name = attacker.getOwner().getDisplayName();
        lore.add(("Воентех крепостей <red>"+ (int) attacker.getOwner().getAttribute(EPlayerAttribute.FORT_LVL)));
        lore.add(("Артиллерия <red>" + (art/1000)));
        lore.add(("Сила осады <red>" + siegeAbility + "%"));
        lore.add(("Осада генерала <red>" + siege));
        ItemStack att =  Tools.createItem(Material.PLAYER_HEAD,"Осаждает <red>"+name,lore);
        SkullMeta ownerMeta = (SkullMeta) att.getItemMeta();
        ownerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(attacker.getOwnerId()));
        att.setItemMeta(ownerMeta);




        inventory.setItem(3, townItem);
        if(town.isFort()){
            if (army.getOwner().equals(town.getController())){
                inventory.setItem(4, antiStorm);
            }else{
                inventory.setItem(4, storm);
            }
        }
        inventory.setItem(5, att);


    }
}
