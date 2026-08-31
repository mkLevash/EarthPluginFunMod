package earthrp.menusystem.menu;

import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;
import earthrp.customObjects.EPlayer;
import earthrp.configs.CustomConfig;
import earthrp.menusystem.Menu;
import earthrp.menusystem.MenuUtility;
import earthrp.menusystem.menu.tech.*;
import earthrp.tools.Tools;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static earthrp.tools.PDCKeys.*;

public class    TechnologyMenu extends Menu {
    public TechnologyMenu(MenuUtility menuUtility) {
        super(menuUtility);
    }

    Player p = menuUtility.getOwner();
    EPlayer player = menuUtility.getPlayer();

    @Override
    public String getMenuName() {
        return "Технологии";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack item = e.getCurrentItem();
        if (item != null) {
            if (item.getType().equals(Material.BARRIER)) {
                p.closeInventory();
                new MainMenu(menuUtility).open();
                return;
            }

            PersistentDataContainerView data = item.getPersistentDataContainer();
            if (data.has(menuIdKey)) {
                String menuId = data.get(menuIdKey, PersistentDataType.STRING);

                if(Objects.equals(menuId, "idea")){
                    if(e.isShiftClick()){
                        if(player.getAttribute(EPlayerAttribute.OI_BALANCE) >= player.getIdeaCost()){
                            player.addAttribute(EPlayerAttribute.OI_BALANCE,-player.getIdeaCost());
                            p.getInventory().addItem(Tools.createIdea());
                        }else{
                            p.sendMessage("Недостаточно ои");
                        }

                    }else{
                        p.closeInventory();
                        new MainIdeasMenu(menuUtility).open();
                    }
                    return;

                }
                if(Objects.equals(menuId, "militaryIdea")){
                    if(e.isShiftClick()){
                        if(player.getAttribute(EPlayerAttribute.OI_BALANCE) >= 10 && player.getAttribute(EPlayerAttribute.TRADITION)>=2){
                            player.addAttribute(EPlayerAttribute.OI_BALANCE,-10);
                            player.addAttribute(EPlayerAttribute.TRADITION,-2);
                            p.getInventory().addItem(Tools.createMilitaryIdea());
                        }else{
                            p.sendMessage("Недостаточно ои");
                        }

                    }else{
                        p.closeInventory();
                        new MilIdeasMenu(menuUtility).open();
                    }
                    return;

                }
                boolean isUnlocked = false;
                if(data.has(epochUnlockedKey)){
                    isUnlocked = data.get(epochUnlockedKey, PersistentDataType.BOOLEAN);
                }else{
                    return;
                }




                // ЛКМ - открыть меню эпохи для просмотра
                if (e.isLeftClick()) {
                    p.closeInventory();
                    openEpochMenu(menuId);
                    return;
                }

                // ПКМ - открыть эпоху (если выполнена)
                if (e.isRightClick()) {
                    if (isUnlocked) {
                        p.sendMessage(colorText("<green>✓ Эта эпоха уже открыта!"));
                        p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
                    } else {
                        // Проверяем условия
                        if (canUnlockEpoch(menuId, player)) {
                            unlockEpoch(menuId);

                        } else {
                            p.sendMessage(colorText("<red>✗ Недостаточно технологий предыдущей эпохи!"));
                            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                        }
                    }
                    p.closeInventory();
                    new TechnologyMenu(menuUtility).open();
                }
            }
        }
    }

    private void openEpochMenu(String menuId) {
        switch (menuId) {
            case "tribal" -> new TribalTechMenu(menuUtility).open();
            case "feudalism" -> new FeudalTechMenu(menuUtility).open();
            case "renaissance" -> new RenaissanceTechMenu(menuUtility).open();
            case "manufacture" -> new ManufactureTechMenu(menuUtility).open();
            case "industrial" -> new TechnologyMenu(menuUtility).open();
        }
    }


    private void unlockEpoch(String menuId) {
        double costMod = player.getAttribute(EPlayerAttribute.TECH_COST);
        int techCost = (int) Math.round(CustomConfig.get().getInt("tech.cost."+menuId) * costMod );
        int oiBalance = (int) player.getAttribute(EPlayerAttribute.OI_BALANCE);
        earthrp.customEnums.EPlayerTech tech = EPlayerTech.fromString(menuId);
        if(oiBalance >= techCost){
            player.setTech(tech,true);
            player.addAttribute(EPlayerAttribute.OI_BALANCE, -techCost);
            for(EPlayerAttribute effect:tech.getEffect().keySet()){
                player.addAttribute(effect,tech.getEffect().get(effect));
            }

            p.sendMessage(colorText("<gold>Эпоха открыта!"));
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.0f);
        }else {
            p.sendMessage(colorText("<red>Недостаточно ОИ!"));
            p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
        }
        // Помечаем эпоху как открытую (можно сохранить в конфиг или БД)
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        ItemStack economy = createEpochItem("<red>","Племя", "tribal", player);
        ItemStack reusable = createEpochItem("<green>","Феодализм", "feudalism", player);
        ItemStack social = createEpochItem(Material.BOOK,"<blue>","Ренессанс", "renaissance", player);
        ItemStack craft = createEpochItem(Material.SMITHING_TABLE,"<light_purple>","Мануфактуры", "manufacture", player);
        ItemStack industrial = createEpochItem(Material.BLAST_FURNACE,"<aqua>","Индустриализация", "industrial", player);

        List<String> ideaLore = new ArrayList<>();
        ideaLore.add("SHIFT + ЛКМ - получить идею за " + player.getIdeaCost() + " ОИ");
        ItemStack idea = makeItem("Идеи","idea","menuIdea",ideaLore);

        List<String> milIdeaLore = new ArrayList<>();
        milIdeaLore.add("SHIFT + ЛКМ - получить мил идею за 10 ОИ 2 традиции");
        inventory.setItem(9, makeItem("Военные идеи","militaryIdea","militaryIdea",milIdeaLore));

        List<String> techStatsList = List.of(
                "Баланс ОИ <aqua>" + (int) player.getAttribute(EPlayerAttribute.OI_BALANCE),
                "Прирост ОИ <green>" + player.getOiIncome(),
                "Потрачено ОИ <yellow>" + (int) player.getAttribute(EPlayerAttribute.OI_SPENT)
        );
        ItemStack tech = makeItem("Технологии","tech","menuTech",techStatsList);

        inventory.setItem(0, tech);
        inventory.setItem(1, idea);



        inventory.setItem(20, economy);
        inventory.setItem(21, reusable);
        inventory.setItem(22, social);
        inventory.setItem(23, craft);
        inventory.setItem(24, industrial);

        for (int i = 0; i <= 9; i++) {
            fillIfEmpty(i);
            fillIfEmpty(i + 35);
        }

        fillIfEmpty(17);
        fillIfEmpty(18);
        fillIfEmpty(26);
        fillIfEmpty(27);

        inventory.setItem(40, createBackItem());
    }


}
