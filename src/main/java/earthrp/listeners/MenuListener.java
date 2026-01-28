package earthrp.listeners;

import earthrp.Earth;
import earthrp.menusystem.Menu;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.InventoryHolder;

import java.sql.Blob;
import java.sql.SQLException;

public class MenuListener implements Listener {

    private final Earth earthPlugin;

    public MenuListener(Earth moraPlugin) {this.earthPlugin = moraPlugin;}

    @EventHandler
    public void onMenuClick(InventoryClickEvent e)  {

        InventoryHolder holder = e.getInventory().getHolder();
        



        //If the inventoryholder of the inventory clicked on
        // is an instance of Menu, then gg. The reason that
        // an InventoryHolder can be a Menu is because our Menu
        // class implements InventoryHolder!!
        if (holder instanceof Menu menu) {

            e.setCancelled(true); //prevent them from fucking with the inventory
            if (e.getCurrentItem() == null) { //deal with null exceptions
                return;
            }
            //Since we know our inventoryholder is a menu, get the Menu Object representing
            // the menu we clicked on
            //Call the handleMenu object which takes the event and processes it
            menu.handleMenu(e);
        }

    }

}
