package earthrp.listeners;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ShapedRecipe;

public class ReceiptHandler implements Listener {

    private final Earth earth;
    private ServerDatabase db;
    public ReceiptHandler(Earth earth) {
        this.earth = earth;
        db = this.earth.getServerDatabase();
    }



    @EventHandler
    public void onCraft(CraftItemEvent event) {



        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) return;

        String key = recipe.getKey().getKey();

        if (!key.contains("newarmy")) return;
        if(event.isShiftClick()){
            event.setCancelled(true);
            return;
        }

        int cost = 0;
        if(key.contains("inf")){
            cost = 9;
        }
        if(key.contains("cav")){
            cost = 18;
        }
        if(key.contains("art")){
            cost = 81;
        }

        EPlayer p = db.getPlayer(event.getWhoClicked().getUniqueId());
        boolean enabled = p.getAttribute(EPlayerAttribute.MANPOWER)>=1 && p.getAttribute(EPlayerAttribute.TREASURY)>=cost;

        if (!enabled) {
            event.getInventory().setResult(null);
        }else{
            p.addAttribute(EPlayerAttribute.MANPOWER,-1);
            p.addAttribute(EPlayerAttribute.TREASURY,-cost);

        }
    }

}
