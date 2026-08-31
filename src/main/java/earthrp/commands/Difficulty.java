package earthrp.commands;

import earthrp.Earth;
import earthrp.customEnums.UnitTech;
import earthrp.customObjects.Army;
import earthrp.tools.Tools;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.EPlayer;
import earthrp.database.ServerDatabase;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.ShulkerBox;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Difficulty implements CommandExecutor, TabCompleter {
    private final ServerDatabase db;
    private final Earth earth;
    public Difficulty(Earth plugin) {
        this.earth = plugin;
        db = plugin.getDatabase();
    }
    private final NamespacedKey armyOwnerKey = new NamespacedKey(Earth.getInstance(), "armyOwner");
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    private final NamespacedKey botNameKey = new NamespacedKey(Earth.getInstance(), "botName");
    private final NamespacedKey unitTypeKey = new NamespacedKey(Earth.getInstance(),"unitType");
    private final NamespacedKey unitLvlKey = new NamespacedKey(Earth.getInstance(),"unitLvl");
    private final NamespacedKey unitDiscKey = new NamespacedKey(Earth.getInstance(),"unitDisc");
    private final NamespacedKey unitFireKey = new NamespacedKey(Earth.getInstance(),"unitFire");
    private final NamespacedKey unitShockKey = new NamespacedKey(Earth.getInstance(),"unitShock");

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        //Bukkit.broadcastMessage(" " + args.length);
        if (sender instanceof Player p){

            if (args.length == 1) {
                ItemStack pickaxe = new ItemStack(Material.IRON_PICKAXE);
                ItemMeta meta = pickaxe.getItemMeta();
                meta.setUnbreakable(true);
                pickaxe.setItemMeta(meta);

                ItemStack axe = new ItemStack(Material.IRON_AXE);
                meta = axe.getItemMeta();
                meta.setUnbreakable(true);
                axe.setItemMeta(meta);

                ItemStack shovel = new ItemStack(Material.IRON_SHOVEL);
                meta = shovel.getItemMeta();
                meta.setUnbreakable(true);
                shovel.setItemMeta(meta);

                ItemStack apple = new ItemStack(Material.APPLE);

                ItemStack bed = new ItemStack(Material.WHITE_BED);

                ItemStack mora = Tools.createMora(1);


                ItemStack inf = Tools.createArmyCraftItem(UnitTech.INF0);

                int infiniteTicks = Integer.MAX_VALUE;
                PotionEffect slowness4 = new PotionEffect(PotionEffectType.SLOWNESS,infiniteTicks,3);
                PotionEffect slowness5 = new PotionEffect(PotionEffectType.SLOWNESS,infiniteTicks,4);
                PotionEffect glowing = new PotionEffect(PotionEffectType.GLOWING,infiniteTicks,1);

                ItemStack war = new ItemStack(Material.SPLASH_POTION);
                PotionMeta potionMeta = (PotionMeta) war.getItemMeta();
                potionMeta.setDisplayName(Tools.colorText("&cWar Potion"));
                potionMeta.setColor(Color.fromRGB(255, 0, 0));
                potionMeta.addCustomEffect(slowness4, true);
                potionMeta.addCustomEffect(glowing, true);
                war.setItemMeta(potionMeta);

                ItemStack trade = new ItemStack(Material.SPLASH_POTION);
                potionMeta = (PotionMeta) trade.getItemMeta();
                potionMeta.setDisplayName(Tools.colorText("&eTrade Potion"));
                potionMeta.setColor(Color.fromRGB(255, 255, 0)); // жёлтый
                potionMeta.addCustomEffect(slowness5, true);
                potionMeta.addCustomEffect(glowing, true);
                trade.setItemMeta(potionMeta);

                List<String> colonialLore = List.of(
                        Tools.colorText("&4-80&f% доход от торговли"),
                        Tools.colorText("&4-60&f% максимальный Людской Ресурс"),
                        Tools.colorText("&2-20&f% стоимость строительства"),
                        Tools.colorText("&2+2&f базовый доход")

                );


                ItemStack shulkerItem = null;


                EPlayer player = db.getPlayer(p.getUniqueId());
                player.setAttribute(EPlayerAttribute.OI_INCOME,5);

                switch (args[0]){
                    case "бомж" -> {
                        shulkerItem = new ItemStack(Material.CYAN_SHULKER_BOX);
                        BlockStateMeta bsm = (BlockStateMeta) shulkerItem.getItemMeta();
                        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
                        Inventory inv = shulker.getInventory();

                        List<String> difLore = List.of(
                                Tools.colorText("&2+2&f Ко всем статистикам страны"),
                                Tools.colorText("&fБоты относятся к вам"),
                                Tools.colorText("&bСнисходительно"),
                                Tools.colorText("&fУ вас&b божественная удача")
                        );
                        String difName = Tools.colorText("&fСложность &bБомж");
                        ItemStack dif = Tools.createItemLegacy(Material.LIGHT_BLUE_DYE,difName,difLore);

                        player.setAttribute(EPlayerAttribute.STABILITY,2);
                        player.setAttribute(EPlayerAttribute.WAR_SUPPORT,2);

                        apple.setAmount(64);
                        mora.setAmount(32);
                        inf.setAmount(10);

                        inv.addItem(pickaxe);
                        inv.addItem(axe);
                        inv.addItem(shovel);
                        inv.addItem(apple);
                        inv.addItem(bed);

                        inv.setItem(18,mora);
                        inv.setItem(19,inf);
                        inv.setItem(9,dif);
                        //inv.setItem(10,colonial);
                        inv.setItem(7,war);
                        inv.setItem(8,trade);
                        inv.setItem(16,war);
                        inv.setItem(17,trade);
                        inv.setItem(25,war);
                        inv.setItem(26,trade);


                        bsm.setBlockState(shulker);
                        shulker.update();
                        bsm.setDisplayName("Бомжатский набор");
                        shulkerItem.setItemMeta(bsm);
                    }
                    case "новичок" -> {
                        shulkerItem = new ItemStack(Material.LIME_SHULKER_BOX);
                        BlockStateMeta bsm = (BlockStateMeta) shulkerItem.getItemMeta();
                        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
                        Inventory inv = shulker.getInventory();

                        List<String> difLore = List.of(
                                Tools.colorText("&2+1&f Ко всем статистикам страны"),
                                Tools.colorText("&fБоты относятся к вам"),
                                Tools.colorText("&aХорошо"),
                                Tools.colorText("&fУ вас&a хорошая удача")
                        );
                        String difName = Tools.colorText("&fСложность &aНовичок");
                        ItemStack dif = Tools.createItemLegacy(Material.LIME_DYE,difName,difLore);

                        player.setAttribute(EPlayerAttribute.STABILITY,1);
                        player.setAttribute(EPlayerAttribute.WAR_SUPPORT,1);

                        apple.setAmount(48);
                        mora.setAmount(16);
                        inf.setAmount(5);

                        inv.addItem(pickaxe);
                        inv.addItem(axe);
                        inv.addItem(shovel);
                        inv.addItem(apple);
                        inv.addItem(bed);

                        inv.setItem(18,mora);
                        inv.setItem(19,inf);
                        inv.setItem(9,dif);
                        //inv.setItem(10,colonial);
                        inv.setItem(7,war);
                        inv.setItem(8,trade);
                        inv.setItem(16,war);
                        inv.setItem(17,trade);
                        inv.setItem(25,war);
                        inv.setItem(26,trade);


                        bsm.setBlockState(shulker);
                        shulker.update();
                        bsm.setDisplayName("Новичковый набор");
                        shulkerItem.setItemMeta(bsm);
                    }
                    case "гражданский" -> {

                        shulkerItem = new ItemStack(Material.YELLOW_SHULKER_BOX);
                        BlockStateMeta bsm = (BlockStateMeta) shulkerItem.getItemMeta();
                        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
                        Inventory inv = shulker.getInventory();

                        List<String> difLore = List.of(
                                Tools.colorText("&fСтандартная сложность")
                        );
                        String difName = Tools.colorText("&fСложность&e Гражданский");
                        ItemStack dif = Tools.createItemLegacy(Material.YELLOW_DYE,difName,difLore);

                        player.setAttribute(EPlayerAttribute.STABILITY,0);
                        player.setAttribute(EPlayerAttribute.WAR_SUPPORT,0);

                        apple.setAmount(32);
                        mora.setAmount(8);
                        inf.setAmount(3);

                        inv.addItem(pickaxe);
                        inv.addItem(axe);
                        inv.addItem(shovel);
                        inv.addItem(apple);
                        inv.addItem(bed);

                        inv.setItem(18,mora);
                        inv.setItem(19,inf);
                        inv.setItem(9,dif);
                        //inv.setItem(10,colonial);
                        inv.setItem(7,war);
                        inv.setItem(8,trade);
                        inv.setItem(16,war);
                        inv.setItem(17,trade);
                        inv.setItem(25,war);
                        inv.setItem(26,trade);


                        bsm.setBlockState(shulker);
                        shulker.update();
                        bsm.setDisplayName("Гражданский набор");
                        shulkerItem.setItemMeta(bsm);
                    }
                    case "гигачад" -> {
                        shulkerItem = new ItemStack(Material.YELLOW_SHULKER_BOX);
                        BlockStateMeta bsm = (BlockStateMeta) shulkerItem.getItemMeta();
                        ShulkerBox shulker = (ShulkerBox) bsm.getBlockState();
                        Inventory inv = shulker.getInventory();

                        List<String> difLore = List.of(
                                Tools.colorText("&4-1&f Ко всем статистикам страны"),
                                Tools.colorText("&fБоты относятся к вам"),
                                Tools.colorText("&6Плохо"),
                                Tools.colorText("&fУ вас&6 плохая удача"),
                                Tools.colorText("&fУ вас &61 &6долг")
                        );
                        String difName = Tools.colorText("&fСложность&e Гигачад");
                        ItemStack dif = Tools.createItemLegacy(Material.ORANGE_DYE,difName,difLore);

                        apple.setAmount(32);
                        mora.setAmount(8);
                        inf.setAmount(1);

                        inv.addItem(pickaxe);
                        inv.addItem(axe);
                        inv.addItem(shovel);
                        inv.addItem(apple);
                        inv.addItem(bed);

                        inv.setItem(18,mora);
                        inv.setItem(19,inf);
                        inv.setItem(9,dif);
                        //inv.setItem(10,colonial);
                        inv.setItem(7,war);
                        inv.setItem(8,trade);
                        inv.setItem(16,war);
                        inv.setItem(17,trade);
                        inv.setItem(25,war);
                        inv.setItem(26,trade);


                        player.setAttribute(EPlayerAttribute.STABILITY,-1);
                        player.setAttribute(EPlayerAttribute.WAR_SUPPORT,-1);
                        UUID debtId = UUID.randomUUID();
                        player.getData().getDebtMap().put(debtId,5);
                        player.getData().getInterestMap().put(debtId,1.0);




                        bsm.setBlockState(shulker);
                        shulker.update();
                        bsm.setDisplayName("Гигачадский набор");
                        shulkerItem.setItemMeta(bsm);
                    }
                }

                p.getInventory().addItem(shulkerItem);
                return true;
            }



        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1){
            return List.of(
                    "бомж",
                    "новичок",
                    "гражданский",
                    "гигачад"
                    //"бессмертный"
            );
        }
        return Collections.singletonList("");
    }
}
