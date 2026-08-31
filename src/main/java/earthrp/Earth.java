package earthrp;

import earthrp.battle.BattleManager;

import earthrp.bot.GeminiManager;
//import earthrp.discord.DiscordBot;
import earthrp.database.ServerDatabase;
import earthrp.menusystem.MenuUtility;
import earthrp.tools.BlueMapManager;
import earthrp.tools.Crafts;
import earthrp.tools.LoadingManager;
import earthrp.tools.maps.RegionMap;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;


import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;


public class Earth extends JavaPlugin {

    @Getter
    private ServerDatabase database;
    @Getter
    private GeminiManager geminiManager;
    @Getter
    private BlueMapManager blueMapManager;
    @Getter
    private BattleManager battleManager;

    @Getter
    private LoadingManager loadingManager;

    @Setter
    @Getter
    private RegionMap regionMap;

    @Getter
    @Setter
    private File mapFile;

    private static final HashMap<Player, MenuUtility> playerMenuUtilityMap = new HashMap<>();

    public static Earth getInstance() {
        return getPlugin(Earth.class);
    }

    //private DiscordBot discordBot;

    @Override
    public void onEnable() {
        geminiManager = new GeminiManager(this);
        battleManager = new BattleManager();
        blueMapManager = new BlueMapManager();
        loadingManager = new LoadingManager(this);
        loadingManager.start();

        getLogger().info("Earth was started");

    }

    @Override
    public void onDisable() {

        // Plugin shutdown logic


        database.saveCache();
        battleManager.shutdownBattles();
        saveFiles();

        getLogger().info("Mora plugin was shut down");
    }


    public void connectFiles(){
        mapFile = new File(getDataFolder(),"regionMap.dat");

        if (mapFile.getParentFile() != null) {
            mapFile.getParentFile().mkdirs();
        }
        try {
            regionMap = RegionMap.loadFromFile(mapFile);
        }catch (Exception e) {
            this.getLogger().severe("Failed to connect to the RegionMap! ");
            e.printStackTrace();
        }

    }

    public void saveFiles(){
        try {
            regionMap.saveToFile(mapFile);
        } catch (IOException e) {
            this.getLogger().severe("Failed to save the RegionMap! ");
            throw new RuntimeException(e);
        }
    }

    public void loadDb(){
        try {
            database = new ServerDatabase();
        } catch (SQLException ex) {
            this.getLogger().severe("Failed to connect to the database! ");
            ex.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }







}
