package earthrp.listeners;

import earthrp.Earth;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.database.ServerDatabase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;

import static earthrp.customEnums.EPlayerTech.LIVESTOCK;

public class ReceiptHandler implements Listener {

    private final Earth earth;
    private ServerDatabase db;
    public ReceiptHandler(Earth earth) {
        this.earth = earth;
        db = this.earth.getDatabase();
    }



    @EventHandler
    public void onCraft(CraftItemEvent event) {



        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) return;

        String key = recipe.getKey().getKey();

        if (!key.contains("army")) return;
        if(event.isShiftClick()){
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("§cПожалуйста, заберите предмет обычным кликом (без Shift).");
            return;
        }





        EPlayer p = db.getPlayer(event.getWhoClicked().getUniqueId());

        if(key.contains("cav") && !p.getTech(LIVESTOCK)){
            event.setCancelled(true);
            event.getWhoClicked().sendMessage("У вас не исследована нужна технология!");
            return;
        }

        boolean enabled = p.getAttribute(EPlayerAttribute.MANPOWER)>=1000;

        if (!enabled) {
            event.getInventory().setResult(null);
        }else{
            p.addAttribute(EPlayerAttribute.MANPOWER,-1000);

        }
    }

    @EventHandler
    public void onCraftTown(CraftItemEvent event) {



        if (!(event.getRecipe() instanceof ShapedRecipe recipe)) return;

        String key = recipe.getKey().getKey();
        if (!key.contains("town")) return;

        Player p = (Player) event.getWhoClicked();

        // Запрещаем Shift-клик, так как UUID должен быть строго уникальным для каждого предмета
        if (event.isShiftClick()) {
            event.setCancelled(true);
            p.sendMessage("§cПожалуйста, заберите предмет обычным кликом (без Shift).");
            return;
        }

        // Создаем наш уникальный предмет
        ItemStack newResult = new ItemStack(Material.END_CRYSTAL);
        ItemMeta meta = newResult.getItemMeta();

        if (meta != null) {
            String capitalId = String.valueOf(UUID.randomUUID());
            meta.setDisplayName("§aГород");
            meta.setLore(List.of("townHall", capitalId, String.valueOf(p.getUniqueId()), p.getDisplayName()));
            newResult.setItemMeta(meta);
        }

        // ВАЖНО: Кладем предмет прямо в курсор игроку, так как он его уже "взял"
        event.setCurrentItem(newResult);
    }

}
