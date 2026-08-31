package earthrp.tools;

import earthrp.Earth;
import earthrp.battle.BattleHandler;
import earthrp.battle.BattlePhaseHandler;
import earthrp.commands.*;
import earthrp.configs.BuildingConfig;
import earthrp.configs.CustomConfig;
import earthrp.listeners.*;
import earthrp.placeholders.MoraExpansion;
import earthrp.runnable.*;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.Bukkit;

import java.util.List;

public class LoadingManager {
    private final Earth instance;

    public LoadingManager(Earth instance) {
        this.instance = instance;
    }
    
    public void registerExpansion(){
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new MoraExpansion(instance).register();
        }
    }



    public void start(){
        instance.loadDb();
        instance.connectFiles();
        loadConfig();
        instance.getDatabase().loadCache();
        registerCommands();
        runTasks();
        registerExpansion();
        registerListeners();
        instance.getBlueMapManager().refreshAllTowns();
        Crafts.enableCrafts();

    }

    public void reload(){
        CustomConfig.reload();
        BuildingConfig.reload();
        instance.reloadConfig();
        instance.getBlueMapManager().refreshAllTowns();
        instance.getBattleManager().shutdownBattles();
        instance.connectFiles();

    }
    
    public void loadConfig(){


        instance.saveDefaultConfig();
        CustomConfig.setup();
        CustomConfig.get().options().copyDefaults(true);
        CustomConfig.save();

        BuildingConfig.setup();
        BuildingConfig.get().options().copyDefaults(true);
        BuildingConfig.save();





    }
    
    public void registerCommands(){

        List<PaperCommand> commands = List.of(
                new EGet(instance)
        );


        instance.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            commands.forEach(cmd -> cmd.register(event.registrar()));
        });



        instance.getCommand("eset").setExecutor(new Mset(instance));
        //instance.getCommand("mget").setExecutor(new Mget(instance));
        instance.getCommand("mora").setExecutor(new GiveMora(instance));
        instance.getCommand("income").setExecutor(new Income(instance));
        instance.getCommand("expense").setExecutor(new Expense(instance));
        //\\\\instance.getCommand("oi").setExecutor(new OiCommand(instance));
        instance.getCommand("polit").setExecutor(new Polit(instance));
        //instance.getCommand("tax_mod").setExecutor(new TaxModifierCommand(instance));
        instance.getCommand("trade").setExecutor(new Trade(instance));
        instance.getCommand("roll").setExecutor(new Roll(instance));
        instance.getCommand("war").setExecutor(new DeclareWar(instance));
        instance.getCommand("peace").setExecutor(new DeclarePeace(instance));
        instance.getCommand("villager").setExecutor(new GiveVillager(instance));
        //instance.getCommand("manpower").setExecutor(new GiveManpowerCommand(instance));
        instance.getCommand("menu").setExecutor(new MenuCommand(instance));
        instance.getCommand("town").setExecutor(new GiveTown(instance));
        instance.getCommand("earth").setExecutor(new EarthCommand());
        instance.getCommand("createBot").setExecutor(new CreateBot(instance));
        instance.getCommand("battle").setExecutor(new BattleCommand(instance));
        instance.getCommand("leader").setExecutor(new Leader(instance));
        //instance.getCommand("chunk").setExecutor(new ChunkCommand(instance));
        instance.getCommand("tradeway").setExecutor(new TradeWay(instance));
        instance.getCommand("colonial").setExecutor(new Colonial(instance));
        instance.getCommand("start").setExecutor(new Difficulty(instance));
        instance.getCommand("cache").setExecutor(new Cache(instance));
        instance.getCommand("ally").setExecutor(new AllyCommand(instance));
        instance.getCommand("color").setExecutor(new Color());
    }
    
    public void registerListeners(){
        instance.getServer().getPluginManager().registerEvents(new MoraCount(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new JoinHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new VillagerSpawnHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new MenuListener(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new BuildingsHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new BuildingsClickHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new TownsHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new TownCheck(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new ArmyHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new BattlePhaseHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new BattleHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new ReceiptHandler(instance), instance);
        instance.getServer().getPluginManager().registerEvents(new BuildingProduction(), instance);
        instance.getServer().getPluginManager().registerEvents(new ArmyMoveHandler(instance), instance);
    }
    
    
    public void runTasks(){

        new CheckDay().runTaskTimer(instance, 0L, 20L);
        new CheckTowns().runTaskTimer(instance, 0L, 800L);
        new BattlePhase().runTaskTimer(instance, 0L, 60L);
        new ArmyTask().runTaskTimer(instance, 0L, 60L);
        new ArmyActionBarTask().runTaskTimer(instance, 0L, 40L);
        new Meteor().runTaskTimer(instance, 0L, 168000L);

        new SpeedGive().runTaskTimerAsynchronously(instance,0,20);
    }


    
    
    
    
}
