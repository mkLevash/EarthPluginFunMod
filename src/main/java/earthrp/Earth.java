package earthrp;

import earthrp.battle.BattleHandler;
import earthrp.battle.BattleManager;
import earthrp.battle.BattlePhaseHandler;

import earthrp.bot.GeminiManager;
import earthrp.customObjects.EPlayer;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customObjects.Town;
//import earthrp.discord.DiscordBot;
import earthrp.listeners.*;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.tools.BlueMapManager;
import earthrp.tools.Crafts;
import earthrp.tools.LoadingManager;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;


import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Level;




public class Earth extends JavaPlugin {

    @Getter
    private ServerDatabase serverDatabase;
    @Getter
    private GeminiManager geminiManager;
    @Getter
    private BlueMapManager blueMapManager;
    @Getter
    private BattleManager battleManager;
    private static final HashMap<Player, MenuUtility> playerMenuUtilityMap = new HashMap<>();

    //private DiscordBot discordBot;

    @Override
    public void onEnable() {
        geminiManager = new GeminiManager(this);
        battleManager = new BattleManager();
        blueMapManager = new BlueMapManager();
        LoadingManager lm = new LoadingManager(this);
        lm.loadConfig();
        try {serverDatabase = new ServerDatabase();} catch (SQLException ex) {
            ex.printStackTrace();
            this.getLogger().severe("Failed to connect to the database! ");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        serverDatabase.loadCache();

        lm.registerCommands();
        lm.runTasks();
        lm.registerExpansion();
        lm.registerListeners();
        blueMapManager.refreshAllTowns(serverDatabase.getTowns());
        Crafts.enableCrafts();








        //getServer().getPluginManager().registerEvents(new MarketsHandler(this), this);

//        getLogger().info("Плагин DiscordBridge активирован.");
//
//        discordBot = new DiscordBot(this);
//        discordBot.startBot();


    }

    @Override
    public void onDisable() {
        System.out.println("[Earth RP Plugins] Mora plugin was shut down");
        // Plugin shutdown logic
        serverDatabase.saveCache();
//        getLogger().info("Плагин DiscordBridge отключается.");
//        if (discordBot != null) {
//            discordBot.shutdown();
//        }
    }
    



    public static Earth getInstance() {
        return getPlugin(Earth.class);
    }



}
