package earthrp.menusystem.menu.countryMenu;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.customObjects.PlayerModifier;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.PaginatedMenu;
import earthrp.tools.Tools;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

import static earthrp.tools.PDCKeys.debtIdKey;
import static earthrp.tools.PDCKeys.debtSizeKey;

public class ModifiersMenu extends PaginatedMenu {

    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();

    public ModifiersMenu(MenuUtility menuUtility) {
        super(menuUtility);

    }






    @Override
    public String getMenuName() {
        return "Модификаторы. Страница "+page;
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e)  {
        ItemStack item = e.getCurrentItem();
        if (item != null ) {
            switch (item.getType()){
                case BARRIER -> {
                    new CountryMenu(menuUtility).open();
                }
                case DARK_OAK_BUTTON -> {
                    if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Left")){
                        if (page == 0){
                            p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                        }else{
                            page = page - 1;
                            super.open();
                        }
                    }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                        if (!((index + 1) > player.getData().getModifiers().size())){
                            page = page + 1;
                            super.open();
                        }else{
                            p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                        }
                    }
                }
            }
        }


    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        addMenuBorder();



        List<PlayerModifier> modifiers = player.getData().getModifiers().stream().toList();
        for(int i = 0; i < getMaxItemsPerPage(); i++) {
            index = getMaxItemsPerPage() * page + i;

            if(index >= modifiers.size()) break;
            PlayerModifier modifier = modifiers.get(index);
            if (modifier != null){
                List<String> lore = new ArrayList<>();
                lore = modifier.getDesc();
                if(modifier.getDateEnd()== -1){
                    //lore.add("Истекает через <yellow>нет");
                }else{
                    int dur = modifier.getDateEnd() - Earth.getInstance().getDatabase().getStatusDay();
                    lore.add("Истекает через <yellow>"+ dur + " д");
                }

                inventory.addItem(makeItem(modifier.getMaterial(),modifier.getName(),lore));

            }
        }



    }
}
