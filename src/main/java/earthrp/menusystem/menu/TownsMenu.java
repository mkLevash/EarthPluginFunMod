package earthrp.menusystem.menu;

import earthrp.Earth;
import earthrp.customEnums.TownItem;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.Town;
import earthrp.database.ServerDatabase;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

            case CRAFTING_TABLE -> {
                e.getWhoClicked().closeInventory();
                new TownItemsMenu(menuUtility).open();
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
                if(!t.getOwner().getUniqueId().equals(p.getUniqueId())){
                    if(t.isStatus()){
                        p.closeInventory();
                        new OccupationConfirmMenu(menuUtility).open();
                    }else{
                        p.closeInventory();
                        new AnnexConfirmMenu(menuUtility).open();
                    }
                }
                else if (!t.isStatus()) {
                    p.closeInventory();
                    p.sendMessage(ChatColor.GREEN+"Город успешно освобождён");
                    t.setStatus(true);
                    new TownsMenu(menuUtility).open();
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


                }else{
                    if(t.getOwner().getAttribute(EPlayerAttribute.POLIT_BALANCE)>=t.getInfrastructureCost()){
                        p.closeInventory();
                        p.sendMessage(ChatColor.GREEN+"Инфраструктура успешно увеличена");
                        t.getOwner().addAttribute(EPlayerAttribute.POLIT_BALANCE,-t.getInfrastructureCost());
                        t.setInfrastructure(t.getInfrastructure()+1);
                        new TownsMenu(menuUtility).open();
                    }else{
                        p.sendMessage(ChatColor.YELLOW+"У вас недостаточно полит власти");
                    }
                }

            }

        }

    }

    @Override
    public void setMenuItems() {

        ItemStack owner = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta ownerMeta = (SkullMeta) owner.getItemMeta();

        List<String> ownerLore = new ArrayList<>();
        ownerLore.add(Tools.colorText("Владелец &d"+t.getOwnerName()));
        ownerLore.add(Tools.colorText("&fНаселение &a" + t.getPeople()));
        ownerLore.add(t.getCoreStatus());
        if(!t.getOwner().getUniqueId().equals(menuUtility.getOwner().getUniqueId())){
            if(t.isStatus()){
                ownerLore.add(ChatColor.YELLOW + "Нажмите для оккупации");
            }else{
                ownerLore.add(ChatColor.YELLOW + "Нажмите для аннексии");
                ownerLore.add(ChatColor.DARK_RED + "ТОЛЬКО ПО МИРНОМУ ДОГОВОРУ");
            }

        } else if (!t.isStatus()) {
            ownerLore.add(ChatColor.GREEN + "Нажмите для освобождения");
        } else if(!t.isCore()){
            ownerLore.add(Tools.colorText("&fСтоимость национализации " + t.getCoreCost()) + "£");
            ownerLore.add(ChatColor.YELLOW + "Нажмите для национализации");
        }else {
            ownerLore.add(Tools.colorText("&fИнфраструктура &6" + t.getInfrastructure() + " &fур"));
            ownerLore.add(Tools.colorText("&eНажмите чтобы расширить за &f" + t.getInfrastructureCost() + "£"));
        }
        ownerMeta.setDisplayName(Tools.colorText("&f"+ t.getName()  ));
        ownerMeta.setOwningPlayer(Bukkit.getOfflinePlayer(t.getOwnerId()));
        ownerMeta.setLore(ownerLore);
        owner.setItemMeta(ownerMeta);


        ItemStack trade = new ItemStack(Material.BELL);
        ItemMeta tradeMeta = trade.getItemMeta();
        tradeMeta.setDisplayName(ChatColor.WHITE + "Меню торговли");
        Town tradeTown = t.getTradeTown();
        List<String> tradeLore = new ArrayList<>();
        if(t.getTradeTown()!=null){
            tradeLore.add(Tools.colorText("&fТовары перенаправляются в &d") + tradeTown.getName());
            tradeLore.add(Tools.colorText("&fБонус рынка: " + tradeTown.getColorTradeMod()));
            tradeLore.add(Tools.colorText("&dДоход: &a" + (int) Math.round(t.getTradeIncome())));
            tradeLore.add(Tools.colorText("&dИздержки за расстояние: &a" + Tools.getColorMod(t.getTradeCost())));


        }else if (!t.isLandHub()){
            tradeLore.add(ChatColor.RED + "Отсутствует рынок, разрешено только перенаправление товаров");
        }else {
            tradeLore.add(Tools.colorText("&fБонус рынка:" + t.getColorTradeMod()));
            tradeLore.add(Tools.colorText("&fКоличество товаров: " + t.getMarketGoods()));
            tradeLore.add(Tools.colorText("&dДоход: &a" + (int) Math.round(t.getTradeIncome())));
        }
        tradeMeta.setLore(tradeLore);
        trade.setItemMeta(tradeMeta);
        EPlayer player = t.getOwner();
        List<String> statsLore = List.of(

                Tools.colorText("&fНалоги &a" + t.getTaxIncome()),
                Tools.colorText("&fПроизводство &a" +  (int) Math.round( t.getProdIncome()*player.getAttribute(EPlayerAttribute.PROD_MOD))),
                Tools.colorText("&fЗдания = &d" + t.getBuildings().size()+"&f/&e"+t.getBuildSite()),
                Tools.colorText("&fЗерна = " + t.getItem(TownItem.WHEAT)),
                Tools.colorText("&fГолод - " + t.getFamine())

        );
        ItemStack stats = Tools.createItem(Material.CRAFTING_TABLE,"Основная информация о городе",statsLore);

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
        deleteMeta.setDisplayName(ChatColor.RED + "Удалить здание");
        deleteMeta.setLore(List.of(ChatColor.WHITE + "Безвозвратно удаляет здание"));
        delete.setItemMeta(deleteMeta);






        inventory.setItem(3, owner);
        inventory.setItem(4, trade);
        inventory.setItem(5, stats);

        inventory.setItem(8, delete);


    }
}
