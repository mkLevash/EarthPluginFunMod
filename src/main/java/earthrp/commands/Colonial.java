package earthrp.commands;

import earthrp.Earth;
import earthrp.database.ServerDatabase;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Colonial implements CommandExecutor, TabCompleter {
    private final Earth earth;
    public Colonial(Earth plugin) {
        this.earth = plugin;
    }
    private final NamespacedKey armyOwnerKey = new NamespacedKey(Earth.getInstance(), "armyOwner");
    private final NamespacedKey armyIdKey = new NamespacedKey(Earth.getInstance(), "armyId");
    private final NamespacedKey unitTypeKey = new NamespacedKey(Earth.getInstance(),"unitType");
    private final NamespacedKey unitLvlKey = new NamespacedKey(Earth.getInstance(),"unitLvl");
    private final NamespacedKey unitDiscKey = new NamespacedKey(Earth.getInstance(),"unitDisc");
    private final NamespacedKey unitFireKey = new NamespacedKey(Earth.getInstance(),"unitFire");
    private final NamespacedKey unitShockKey = new NamespacedKey(Earth.getInstance(),"unitShock");
    private final NamespacedKey botNameKey = new NamespacedKey(Earth.getInstance(), "botName");
    private final NamespacedKey holoKey = new NamespacedKey(Earth.getInstance(), "holoType");

    private final NamespacedKey leaderFireKey = new NamespacedKey(Earth.getInstance(),"leaderFire");
    private final NamespacedKey leaderShockKey = new NamespacedKey(Earth.getInstance(),"leaderShock");
    private final NamespacedKey leaderMoveKey = new NamespacedKey(Earth.getInstance(),"leaderMove");
    private final NamespacedKey leaderSiegeKey = new NamespacedKey(Earth.getInstance(),"leaderSiege");


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        ServerDatabase db = Earth.getInstance().getDatabase();

        if(sender instanceof Player player){

//            List<String> colonialLore = List.of(
//                    Tools.colorText("&4-80&f% доход от торговли"),
//                    Tools.colorText("&4-60&f% максимальный Людской Ресурс"),
//                    Tools.colorText("&2-20&f% стоимость строительства"),
//                    Tools.colorText("&2+2&f базовый доход")
//
//            );
//            ItemStack colonial = Tools.createItem(Material.HEART_OF_THE_SEA,ChatColor.DARK_BLUE+"Колониальное знамя",colonialLore);
//
//            EPlayer p = db.getPlayer(player.getUniqueId());
//            p.setAttribute(EPlayerAttribute.TRADE_MOD,0.2);
//            p.setAttribute(EPlayerAttribute.MANPOWER_LIMIT_MOD,0.4);
//            p.setAttribute(EPlayerAttribute.BUILDING_COST,0.8);
//            p.setAttribute(EPlayerAttribute.INCOME,2);
//            p.setAttribute(EPlayerAttribute.OI_INCOME,5);
//
//            ItemStack shipyard = Tools.createBuilding(Material.FOX_SPAWN_EGG, "Верфь", "shipyard");
//            ItemStack port = Tools.createBuilding(Material.BARREL, "Порт", "port");
//
//            player.getInventory().addItem(port);
//            player.getInventory().addItem(shipyard);
//            player.getInventory().addItem(colonial);
//
//
//
//            p.setTech(EPlayerTech.SHIPPING,true);

            createBeam(player.getLocation());

            if (args.length == 1 && args[0].equals("clean")){
                player.getNearbyEntities(10, 10, 10).forEach(entity -> {
                    if (entity.getScoreboardTags().contains("lighthouse_beam")) {
                        entity.remove();
                    }
                });
            }

            return true;


        }
        return false;
    }

    private void createBeam(Location loc) {
        // 1. Создаем сущность BlockDisplay
        BlockDisplay beam = (BlockDisplay) loc.getWorld().spawnEntity(loc, EntityType.BLOCK_DISPLAY);

        // Устанавливаем материал (светящееся стекло или морефонарь)
        beam.setBlock(Material.LIGHT_BLUE_STAINED_GLASS.createBlockData());
        beam.setBrightness(new org.bukkit.entity.Display.Brightness(15, 15));

        // 2. Настраиваем трансформацию (растягиваем блок в длинный луч)
        // Масштаб: 0.4 по X и Z (ширина), 20.0 по Y (длина)
        Transformation transformation = new Transformation(
                new Vector3f(-0.2f, 0, -0.2f), // Смещение для центрирования
                new Quaternionf(),             // Поворот (по умолчанию)
                new Vector3f(0.2f, 80.0f, 0.2f), // Масштаб (40 блоков в длину)
                new Quaternionf()              // Поворот в конце
        );
        beam.setTransformation(transformation);

        beam.addScoreboardTag("lighthouse_beam");

        beam.setGlowing(true);

        Scoreboard scoreboard = Bukkit.getScoreboardManager().getMainScoreboard();
        Team beamTeam = scoreboard.getTeam("LighthouseTeam");

        if (beamTeam == null) {
            beamTeam = scoreboard.registerNewTeam("LighthouseTeam");
        }

        beamTeam.setColor(ChatColor.AQUA); // Цвет свечения
        beamTeam.addEntry(beam.getUniqueId().toString());

        // Чтобы луч плавно вращался, используем интерполяцию
        beam.setInterpolationDuration(1); // Длительность перехода в тиках
        beam.setInterpolationDelay(0);

        // 3. Цикл вращения
        new BukkitRunnable() {
            float yaw = 0;

            @Override
            public void run() {
                if (!beam.isValid()) {
                    this.cancel();
                    return;
                }

                // Увеличиваем угол поворота
                yaw += 2.0f;
                if (yaw >= 360) yaw = 0;

                // Устанавливаем поворот
                // Важно: чтобы BlockDisplay вращался вокруг своей оси
                beam.setRotation(yaw, 0);
            }
        }.runTaskTimer(Earth.getInstance(), 0L, 1L);
    }

    private List<Integer> roll(int d, int k){
        List<Integer> res = new ArrayList<>();
        for (int i = 0; i < k; i++) {
            res.add((int) (Math.random()*d));
        }
        return res;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length==1) {
            return  List.of("<Количество полит власти>");
        }else if(args.length==2){
            return  List.of("<Имя генерала>");}
        else{
            return List.of();
        }


    }




}
