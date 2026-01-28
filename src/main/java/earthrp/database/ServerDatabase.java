package earthrp.database;

import earthrp.*;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;
import earthrp.customObjects.*;
import earthrp.files.CustomConfig;
import earthrp.tools.Tools;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static earthrp.tools.PDCKeys.*;
import static earthrp.database.dbTools.toCamelCase;


public class ServerDatabase {
    
    private final Earth instance;
    private final String path;
    public ServerDatabase() throws  SQLException {
        this.instance = Earth.getInstance();
        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdirs();
        }
        path = instance.getDataFolder().getAbsolutePath() + "/server.db";
        dbTools.createTables(path);
    }

    
    private final Map<UUID, Building> buildingCache = new ConcurrentHashMap<>();
    private final Map<UUID, HashSet<Building>> townBuildingCache = new ConcurrentHashMap<>();

    private final Map<UUID, SkullMeta> botHeads = new ConcurrentHashMap<>();

    private final Map<UUID, Town> townCache = new ConcurrentHashMap<>();
    private final Map<UUID, HashSet<Town>> playerTownCache = new ConcurrentHashMap<>();


    private final Map<UUID, Unit> unitCache = new ConcurrentHashMap<>();
    private final Map<UUID, HashSet<Unit>> armyUnitCache = new ConcurrentHashMap<>();

    private final Map<UUID, Army> armyCache = new ConcurrentHashMap<>();
    private final Map<UUID, HashSet<Army>> playerArmyCache = new ConcurrentHashMap<>();

    private final Map<UUID, EPlayer> playerCache = new ConcurrentHashMap<>();
    private final Map<Long, UUID> chunkCache = new ConcurrentHashMap<>();

    private static long getChunkKey(int x, int z) {
        return ((long) x << 32) | (z & 0xffffffffL);
    }

    private static final UUID EMPTY_UUID = new UUID(0L, 0L);

    public void markChunk(int x, int z) {
        chunkCache.put(getChunkKey(x,z), EMPTY_UUID);
    }

    public void markChunk(int x, int z, UUID townId) {
        chunkCache.put(getChunkKey(x,z), townId);
    }

    public boolean isChunkClaimed(int x, int z){
        UUID owner = chunkCache.get(getChunkKey(x, z));
        return owner != null && !owner.equals(EMPTY_UUID);
    }

    public Town getTownAtChunk(int x, int z){
        if(isChunkClaimed(x,z)){
            return getTown(chunkCache.get(getChunkKey(x,z)));
        }
        return null;
    }

    public void loadCache(){
        loadBuildings();
        loadTowns();
        loadUnits();
        loadArmies();
        loadPlayers();
    }

    public void saveCache(){
        saveBuildings();
        saveTowns();
        saveUnits();
        saveArmies();
        savePlayers();
    }


    public Connection getConnection(String connId)  {

        try {
            Connection conn = DriverManager.getConnection("jdbc:sqlite:" + path);
            if (Objects.equals(instance.getConfig().getString("debug"), "true")){
                instance.getLogger().info("DB Connection "+connId+" opened: " + conn);
            }
            return conn;
        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while creatingConnection " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>getConnection <red>critical error"));
            throw new RuntimeException(e);
        }


    }

    public void printCacheStatus() {
        System.out.println("[Earth]=== [Cache Status Report] ===");

        // Считаем основные объекты
        System.out.println("[Earth]Players: " + playerCache.size());
        System.out.println("[Earth]Towns: " + townCache.size());
        System.out.println("[Earth]Buildings: " + buildingCache.size());
        System.out.println("[Earth]Armies: " + armyCache.size());
        System.out.println("[Earth]Units: " + unitCache.size());

        // Проверяем глубину индексов (группировок)
        int totalTownLinks = playerTownCache.values().stream().mapToInt(HashSet::size).sum();
        int totalBuildingLinks = townBuildingCache.values().stream().mapToInt(HashSet::size).sum();

        System.out.println("[Earth]--- Grouping Stats ---");
        System.out.println("[Earth]Total Player-Town links: " + totalTownLinks);
        System.out.println("[Earth]Total Town-Building links: " + totalBuildingLinks);

        // Примерная оценка "здоровья" кэша
        if (totalTownLinks != townCache.size()) {
            System.out.println("[Earth]WARN: Рассинхрон! В основном кэше " + townCache.size() +
                    " городов, а в списках игроков — " + totalTownLinks);
        }

        // Информация о JVM (сколько памяти занято всего)
        long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024;
        System.out.println("[Earth]Total JVM Used Memory: " + usedMemory + " MB");
        System.out.println("[Earth]==============================");
    }
    
    private void saveBuildings(){
        String connId = "saveBuilding";
        String sql = "UPDATE buildings SET " +
                "town_name = ?, " +
                "town_id = ?, " +
                "type = ?, " +
                "item = ?, " +
                "status = ?, " +
                "world = ?, " +
                "chunk_x = ?, " +
                "chunk_z = ?, " +
                "market_id = ? " +
                "WHERE uuid = ?";
        Set<Building> buildings = getBuildings();
        try (Connection conn = getConnection(connId);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            conn.setAutoCommit(false);
            for (Building building:buildings){
                int paramIndex = 1;
                pstmt.setString(paramIndex++, building.getTownName());
                pstmt.setString(paramIndex++, building.getTownId().toString());
                pstmt.setString(paramIndex++, building.getType());
                pstmt.setString(paramIndex++, building.getItem() != null ? building.getItem().toString() : null);
                pstmt.setInt(paramIndex++, building.getStatus());
                pstmt.setString(paramIndex++, building.getWorld());
                pstmt.setInt(paramIndex++, building.getX());
                pstmt.setInt(paramIndex++, building.getZ());
                pstmt.setString(paramIndex++, building.getMarketId() != null ? building.getMarketId().toString() : null);
                pstmt.setString(paramIndex, building.getUniqueId().toString());
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            conn.commit();


        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }

    private void saveTowns() {
        String connId = "saveTowns";
        Set<Town> towns = getTowns();
        String sql = "UPDATE towns SET " +
                "owner_name = ?, " +
                "town_name = ?, " +
                "owner_id = ?, " +
                "type = ?, " +
                "blockade_status = ?, " +
                "status = ?, " +
                "core = ?, " +
                "infrastructure = ?, " +
                "bonusBuildSites = ?, " +
                "houses = ?, " +
                "port = ?, " +
                "landHub = ?, " +
                "tradeTown = ?, " +
                "world = ?, " +
                "chunk_x = ?, " +
                "chunk_z = ? " +
                "WHERE uuid = ?";

        try (Connection conn = getConnection("saveTowns");
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // Выключаем авто-коммит для ускорения

            for (Town town : towns) {
                int paramIndex = 0;
                pstmt.setString(++paramIndex, town.getOwnerName());
                pstmt.setString(++paramIndex, town.getName());
                pstmt.setString(++paramIndex, town.getOwnerId().toString());
                pstmt.setString(++paramIndex, town.getType());
                pstmt.setBoolean(++paramIndex, town.getBlockadeStatus());
                pstmt.setBoolean(++paramIndex, town.isStatus());
                pstmt.setBoolean(++paramIndex, town.isCore());
                pstmt.setInt(++paramIndex, town.getInfrastructure());
                pstmt.setInt(++paramIndex, town.getBonusBuildSite());
                pstmt.setInt(++paramIndex, town.getHouses());

                pstmt.setBoolean(++paramIndex,town.isPort());
                pstmt.setBoolean(++paramIndex,town.isLandHub());
//                // Устанавливаем port
//                UUID port = town.getPortId();
//                if (port != null) {
//                    pstmt.setString(++paramIndex, port.toString());
//                } else {
//                    pstmt.setNull(++paramIndex, Types.VARCHAR);
//                }
//
//                // Устанавливаем landHub
//                UUID landHub = town.getLandHubId();
//                if (landHub != null) {
//                    pstmt.setString(++paramIndex, landHub.toString());
//                } else {
//                    pstmt.setNull(++paramIndex, Types.VARCHAR);
//                }

                UUID tradeTown = town.getTradeTownId();
                if (tradeTown != null) {
                    pstmt.setString(++paramIndex, tradeTown.toString());
                } else {
                    pstmt.setNull(++paramIndex, Types.VARCHAR);
                }

                pstmt.setString(++paramIndex, town.getWorld());
                pstmt.setInt(++paramIndex, town.getChunkX());
                pstmt.setInt(++paramIndex, town.getChunkZ());

                pstmt.setString(++paramIndex, town.getUniqueId().toString());

                pstmt.addBatch(); // Добавляем в пакет
            }

            pstmt.executeBatch(); // Выполняем всё разом
            conn.commit();        // Фиксируем изменения
        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }

    private void savePlayers() {
        String connId = "savePlayer";
        // Генерируем запрос вида: UPDATE countries SET techCost=?, morale=? ... WHERE id=?
        String countryColumns = Stream.of(EPlayerAttribute.values())
                .map(type -> toCamelCase(type.name()) + " = ?")
                .collect(Collectors.joining(", "));
        String techColumns = Stream.of(EPlayerTech.values())
                .map(tech -> toCamelCase(tech.name()) + " = ?")
                .collect(Collectors.joining(", "));

        String sql = "UPDATE " + "countries" + " SET displayName = ?, " + countryColumns + " WHERE id = ?";
        String sqlTech = "UPDATE " + "tech" + " SET " + techColumns + " WHERE id = ?";
        Set<EPlayer> players = getPlayers();
        try (Connection conn = getConnection(connId);
             PreparedStatement stCountry = conn.prepareStatement(sql);
             PreparedStatement stTech = conn.prepareStatement(sqlTech)) {
            conn.setAutoCommit(false);
            
            for (EPlayer p:players){
                int i = 1;
                stCountry.setString(i++, p.getCountryName()); // displayName
                for (EPlayerAttribute type : EPlayerAttribute.values()) {
                    stCountry.setDouble(i++, p.getAttribute(type));
                }
                stCountry.setString(i, p.getUniqueId().toString());// WHERE id = ?


                i = 1;
                for (EPlayerTech tech : EPlayerTech.values()) {
                    stTech.setBoolean(i++, p.getTech(tech));
                }
                stTech.setString(i, p.getUniqueId().toString());

                stTech.addBatch();
                stCountry.addBatch();
                
            }
            stTech.executeBatch();
            stCountry.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }

    private void saveArmies(){
        String connId = "saveArmy";
        String sql = "UPDATE armies SET " +
                "ownerId = ? , " +
                "infantry = ? , " +
                "cavalry = ? , " +
                "artillery = ? , " +
                "leaderName = ? , " +
                "leaderFire = ? , " +
                "leaderShock = ? , " +
                "maxLvl = ? " +
                "WHERE uuid = ?";
        Set<Army> armyList = getArmies();
        try (Connection c = getConnection(connId)){
            try(PreparedStatement st = c.prepareStatement(sql)){
                c.setAutoCommit(false);
                for(Army a:armyList){
                    int i = 0;
                    st.setString(++i, a.getOwnerId().toString());
                    st.setInt(++i, a.getInfantry());
                    st.setInt(++i, a.getCavalry());
                    st.setInt(++i, a.getArtillery());
                    st.setString(++i, a.getLeaderName());
                    st.setInt(++i, a.getLeaderFire());
                    st.setInt(++i, a.getLeaderShock());
                    st.setInt(++i, a.getTechLvl());

                    st.setString(++i, a.getUuid().toString());
                    st.addBatch();
                }


                st.executeBatch();
                c.commit();
            }

        }catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }

    private void saveUnits(){
        Set<Unit> units = getUnits();
        String connId = "saveUnits";
        String sql = "UPDATE units SET " +
                "armyId = ? , " +
                "type = ? , " +
                "lvl = ? , " +
                "hp = ? , " +
                "morale = ? , " +
                "maxMorale = ? , " +
                "disc = ? , " +
                "fire = ? , " +
                "shock = ? " +
                "WHERE uuid = ?";

        try (Connection c = getConnection(connId)){
            try(PreparedStatement st = c.prepareStatement(sql)){
                c.setAutoCommit(false);
                
                for(Unit unit:units){
                    int i = 0;
                    st.setString(++i, unit.getArmyId().toString());
                    st.setString(++i, unit.getType());
                    st.setInt(++i, unit.getLvl());
                    st.setInt(++i, unit.getHp());
                    st.setDouble(++i, unit.getMorale());
                    st.setDouble(++i, unit.getBaseMorale());
                    st.setDouble(++i, unit.getDisc());
                    st.setDouble(++i, unit.getFire());
                    st.setDouble(++i, unit.getShock());

                    st.setString(++i, unit.getUniqueId().toString());
                    st.addBatch();
                }

                st.executeBatch();
                c.commit();
            }

        }catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }
    

    private void loadBuildings(){
        buildingCache.clear();
        townBuildingCache.clear();

        String query = "SELECT * FROM buildings";
        String connId = "loadBuildings";
        try (Connection conn = getConnection(connId);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Building building = createBuildingFromResultSet(rs);
                buildingCache.put(building.getUniqueId(),building);
                townBuildingCache.computeIfAbsent(building.getTownId(), k -> new HashSet<>()).add(building);
            }
        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }

    }

    private void loadTowns(){
        townCache.clear();
        playerTownCache.clear();
        chunkCache.clear();
        String connId = "loadTowns";
        try (Connection conn = getConnection(connId);
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM towns")) {

            while (rs.next()) {
                Town town = createTownFromResultSet(rs);

                Set<Building> buildings = townBuildingCache.get(town.getUniqueId());
                if (buildings != null) for (Building building : buildings) town.addBuilding(building);

                townCache.put(town.getUniqueId(), town);
                markChunk(town.getX(),town.getChunkZ(),town.getUniqueId());
                playerTownCache.computeIfAbsent(town.getOwnerId(), k -> new HashSet<>()).add(town);
            }
        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }



    private void loadUnits(){
        unitCache.clear();
        armyUnitCache.clear();
        String sql = "SELECT * from units";
        String connId = "loadUnits";
        try(
                Connection c = getConnection(connId);
                Statement st = c.createStatement();
                ResultSet rs = st.executeQuery(sql)
        ){
            while(rs.next()){
                Unit unit = createUnitFromRS(rs);
                unitCache.put(unit.getUniqueId(),unit);
                armyUnitCache.computeIfAbsent(unit.getArmyId(), k -> new HashSet<>()).add(unit);
            }
        }catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }

    private void loadArmies(){
        armyCache.clear();
        playerArmyCache.clear();
        String connId = "loadArmies";
        String sql = "SELECT * FROM armies";
        try (Connection conn = getConnection(connId);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()){
                Army army = createArmyFromResultSet(rs);

                Set<Unit> units = armyUnitCache.get(army.getUuid());
                if (units != null) for (Unit unit : units) army.addUnit(unit);
                armyCache.put(army.getUuid(),army);
                playerArmyCache.computeIfAbsent(army.getOwnerId(), k -> new HashSet<>()).add(army);
            }

        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
    }


    private void loadPlayers(){
        playerCache.clear();
        String connId = "loadPlayers";
        try (Connection conn = getConnection(connId);
             Statement statement = conn.createStatement();
             ResultSet rs = statement.executeQuery("SELECT * FROM players");) {

            while (rs.next()) {
                try {
                    String id = rs.getString("uuid");
                    EPlayer player = getPlayerFromDb(id);
                    Set<Town> towns = playerTownCache.get(player.getUniqueId());
                    if (towns != null) for (Town town : towns) player.addTown(town);

                    // 2. Привязываем армии
                    Set<Army> armies = playerArmyCache.get(player.getUniqueId());
                    if (armies != null) for (Army army : armies) player.addArmy(army);

                    playerCache.put(player.getUniqueId(), player);
                } catch (SQLException e) {
                    System.err.println("Error creating player from record: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }

    }




    //CREATE TABLE IF NOT EXISTS players (
    //uuid TEXT PRIMARY KEY,
    //username TEXT NOT NULL,
    //mora INTEGER NOT NULL DEFAULT 0



    // 3. УНИВЕРСАЛЬНАЯ ЗАГРУЗКА (SELECT)
    public EPlayer getPlayerFromDb(String id) {
        EPlayer player = null;
        String connId = "getPlayerFromDb";
        try(Connection conn = getConnection(connId)){

            // 2. Запрос в БД, если в кэше нет

            String query = "SELECT * FROM players WHERE uuid = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        player = new EPlayer(UUID.fromString(rs.getString("uuid")));
                        player.setDisplayName(rs.getString("displayName"));
                    }
                }
            }
            if (player == null) return null;
            String sql = "SELECT * FROM " + "countries" + " WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                ResultSet rs = pstmt.executeQuery();
                player.setCountryName(rs.getString("displayName"));
                if (rs.next()) {
                    for (EPlayerAttribute type : EPlayerAttribute.values()) {
                        String colName = toCamelCase(type.name());
                        player.setAttribute(type, rs.getDouble(colName));
                    }
                }
            }
            sql = "SELECT * FROM " + "tech" + " WHERE id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, id);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    for (EPlayerTech type : EPlayerTech.values()) {
                        String colName = toCamelCase(type.name());
                        player.setTech(type, rs.getBoolean(colName));
                    }
                }
            }

        } catch (SQLException e) {
            instance.getLogger().severe("DataBase error while processing " + connId);
            Bukkit.broadcast(MiniMessage.miniMessage().deserialize("Database <yellow>"+connId+" <red>critical error"));
            throw new RuntimeException(e);
        }
        return player;
    }
//
//    private EPlayer createPlayerFromResultSet(ResultSet rs) throws SQLException {
//        UUID uuid = UUID.fromString(rs.getString("uuid"));
//        String displayName = rs.getString("displayName");
//        EPlayer player = new EPlayer(uuid);
//        player.setDisplayName(displayName);
//
//
//
//
//        String SQLCountry = "SELECT * FROM countries WHERE ownerId = ?";
//        String SQLArmy = "SELECT * FROM armyStats WHERE ownerId = ?";
//        ResultSet rsCountry;
//        ResultSet rsArmyStats;
//        try(Connection conn = getConnection("createPlayerFromResultSet")){
//            try(PreparedStatement st = conn.prepareStatement(SQLCountry)){
//                st.setString(1,uuid.toString());
//                rsCountry = st.executeQuery();
//                if(rsCountry.next()){
//                    Country country = player.getCountry();
//                    country.setDisplayName(rsCountry.getString("displayName"));
//                    country.setOiBalance(rsCountry.getInt("oiBalance"));
//                    country.setOiIncome(rsCountry.getInt("oiIncome"));
//                    country.setOiSpent(rsCountry.getInt("oiSpent"));
//                    country.setPolitBalance(rsCountry.getInt("politBalance"));
//                    country.setPolitIncome(rsCountry.getInt("politIncome"));
//                    country.setPolitIncomeMod(rsCountry.getInt("politIncomeMod"));
//                    country.setPolitMax(rsCountry.getInt("politMax"));
//                    country.setPolitMaxMod(rsCountry.getInt("politMaxMod"));
//                    country.setIncome(rsCountry.getInt("income"));
//                    country.setTradeIncome(rsCountry.getInt("tradeIncome"));
//                    country.setTradeMod(rsCountry.getInt("tradeMod"));
//                    country.setTaxIncome(rsCountry.getInt("taxIncome"));
//                    country.setTaxMod(rsCountry.getInt("taxMod"));
//                    country.setProdMod(rsCountry.getDouble("prodMod"));
//                    country.setGoodsMod(rsCountry.getDouble("goodsMod"));
//                    country.setExpense(rsCountry.getInt("expense"));
//                    country.setCorruption(rsCountry.getInt("corruption"));
//                    country.setInflation(rsCountry.getInt("inflation"));
//                    country.setWarSup(rsCountry.getInt("warSupport"));
//                    country.setWarStatus(rsCountry.getInt("warStatus"));
//                    country.setTreasury(rsCountry.getInt("treasury"));
//                    player.setCountry(country);
//                }
//            }
//            try(PreparedStatement st = conn.prepareStatement(SQLArmy)){
//                st.setString(1,uuid.toString());
//                rsArmyStats = st.executeQuery();
//                if(rsArmyStats.next()){
//                    ArmyStats armyStats = player.getArmyStats();
//                    int index = 2;
//                    armyStats.setLimitMod(rsArmyStats.getDouble(++index));
//                    armyStats.setExpenseMod(rsArmyStats.getDouble(++index));
//                    armyStats.setManpower(rsArmyStats.getInt(++index));
//                    armyStats.setManpowerLimitMod(rsArmyStats.getDouble(++index));
//                    armyStats.setManpowerIncMod(rsArmyStats.getInt(++index));
//                    armyStats.setDisciple(rsArmyStats.getDouble(++index));
//                    armyStats.setTac(rsArmyStats.getDouble(++index));
//                    armyStats.setMorale(rsArmyStats.getDouble(++index));
//                    armyStats.setFireDamage(rsArmyStats.getDouble(++index));
//                    armyStats.setFireResist(rsArmyStats.getDouble(++index));
//                    armyStats.setShockDamage(rsArmyStats.getDouble(++index));
//                    armyStats.setShockResist(rsArmyStats.getDouble(++index));
//                    armyStats.setMoraleDamage(rsArmyStats.getDouble(++index));
//                    armyStats.setMoraleResist(rsArmyStats.getDouble(++index));
//                    armyStats.setInfCost(rsArmyStats.getDouble(++index));
//                    armyStats.setInfCombatAbility(rsArmyStats.getDouble(++index));
//                    armyStats.setCavCost(rsArmyStats.getDouble(++index));
//                    armyStats.setCavRatio(rsArmyStats.getDouble(++index));
//                    armyStats.setCavCombatAbility(rsArmyStats.getDouble(++index));
//                    armyStats.setArtCost(rsArmyStats.getDouble(++index));
//                    armyStats.setArtCombatAbility(rsArmyStats.getDouble(++index));
//                    player.setArmyStats(armyStats);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return player;
//    }

    private Town createTownFromResultSet(ResultSet rs) throws SQLException {
        UUID townId = UUID.fromString(rs.getString("uuid"));
        UUID playerId = UUID.fromString(rs.getString("owner_id"));


        UUID tradeTown = null;
        String tradeTownStr = rs.getString("tradeTown");
        if (tradeTownStr != null && !tradeTownStr.isEmpty()) {
            tradeTown = UUID.fromString(tradeTownStr);
        }

        Town town =  new Town(
                townId,
                playerId,
                rs.getString("type"),
                rs.getString("town_name"),
                rs.getString("owner_name"),
                rs.getInt("houses"),
                rs.getInt("bonusBuildSites"),
                rs.getString("world"),
                rs.getInt("chunk_x"),
                rs.getInt("chunk_z"),
                rs.getBoolean("port"),
                rs.getBoolean("landHub"),
                tradeTown
        );
        town.setCore(rs.getBoolean("core"));
        town.setInfrastructure(rs.getInt("infrastructure"));
        town.setStatus(rs.getBoolean("status"));
        town.setBStatus(rs.getBoolean("blockade_status"));

        Entity[] tileEntities = Bukkit.getWorld(town.getWorld()).getChunkAt(town.getChunkX(),town.getChunkZ()).getEntities();
        for(Entity en: tileEntities){
            if (en instanceof ArmorStand armor){
                if(Objects.equals(armor.getCustomName(), town.getUniqueId().toString())){
                    town.setLocation(armor.getLocation());
                    //System.out.println("[Earth]check");
                    break;
                }

            }
        }

        return town;



    }

    private Building createBuildingFromResultSet(ResultSet rs) throws SQLException {
        String marketIdStr = rs.getString("market_id");
        UUID marketId = marketIdStr != null ? UUID.fromString(marketIdStr) : null;
        return new Building(
                UUID.fromString(rs.getString("uuid")),
                UUID.fromString(rs.getString("town_id")),
                rs.getString("town_name"),
                marketId,
                rs.getString("type"),
                rs.getInt("status"),
                rs.getString("item"),
                rs.getString("world"),
                rs.getInt("chunk_x"),
                rs.getInt("chunk_z")
        );
    }


    private Army createArmyFromResultSet(ResultSet rs) throws SQLException {
        Army army = new Army(UUID.fromString(rs.getString("uuid")), UUID.fromString(rs.getString("ownerId")));
        army.setLeaderName(rs.getString("leaderName"));
        army.setLeaderFire(rs.getInt("leaderFire"));
        army.setLeaderShock(rs.getInt("leaderShock"));
        army.setTechLvl(rs.getInt("maxLvl"));
        return army;
    }

    private Unit createUnitFromRS(ResultSet rs) throws SQLException {
        Unit unit = new Unit(UUID.fromString(rs.getString("uuid")));
        unit.setArmyId(UUID.fromString(rs.getString("armyId")));
        unit.setType(rs.getString("type"));
        unit.setLvl(rs.getInt("lvl"));
        unit.setHp(rs.getInt("hp"));
        unit.setMorale(rs.getDouble("morale"));
        unit.setDisc(rs.getDouble("disc"));
        unit.setFire(rs.getDouble("fire"));
        unit.setShock(rs.getDouble("shock"));
        String path = "pips.standard."+unit.getType() + unit.getLvl() + ".";
        unit.setPipsFire(CustomConfig.get().getInt(path+"fire"));
        unit.setPipsShock(CustomConfig.get().getInt(path + "shock"));
        unit.setPipsMorale(CustomConfig.get().getInt(path + "morale"));

        return unit;
    }

    private void removeArmorStands(Chunk chunk, Location[] locations) {
        for (Entity entity : chunk.getEntities()) {
            if (entity instanceof ArmorStand armorStand) {
                Location loc = armorStand.getLocation();
                if (loc.equals(locations[0]) || loc.equals(locations[1]) || loc.equals(locations[2])) {
                    armorStand.remove();
                }
            }
        }
    }

    private void removeItemFromChest(Chest chest, String type) {
        Inventory inventory = chest.getBlockInventory();
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                PersistentDataContainer data = meta.getPersistentDataContainer();
                if (meta.hasLore() && type.equals(meta.getLore().get(0))) {
                    inventory.remove(item);
                }else if(data.has(buildingIdKey)){
                    inventory.remove(item);
                }
            }
        }
    }

    private void processChest(Chunk chunk, Location chestLocation, String type) {
        for (BlockState blockState : chunk.getTileEntities()) {
            if (blockState instanceof Chest chest && chestLocation.equals(chest.getLocation())) {
                removeItemFromChest(chest, type);
                return;
            }
        }
    }

    public void addArmy(Army a){
        String sql = "INSERT INTO armies (" +
                "uuid, " +
                "ownerId, " +
                "infantry, " +
                "cavalry, " +
                "artillery) " +
                "VALUES (?, ?, ?, ?, ?)";
        try(Connection c = getConnection("addArmy")){
            try(PreparedStatement st = c.prepareStatement(sql)){
                int i = 1;
                st.setString(i++,a.getUuid().toString());
                st.setString(i++,a.getOwnerId().toString());
                st.setInt(i++,a.getInfantry());
                st.setInt(i++,a.getCavalry());
                st.setInt(i++,a.getArtillery());
                st.executeUpdate();
                armyCache.put(a.getUuid(),a);
                getPlayer(a.getOwnerId()).addArmy(a);


            }
        }catch (SQLException e) {
            System.out.println("[Earth]Earth DataBase error in 'addArmy': " + e.getMessage());
            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error 'addArmy'");
            throw new RuntimeException(e);
        }
    }
//
//    public void updateArmy(Army a){
//
//        String connId = "updateArmy";
//        String sql = "UPDATE armies SET " +
//                "ownerId = ? , " +
//                "infantry = ? , " +
//                "cavalry = ? , " +
//                "artillery = ? , " +
//                "leaderName = ? , " +
//                "leaderFire = ? , " +
//                "leaderShock = ? , " +
//                "botName = ? , " +
//                "maxLvl = ? " +
//                "WHERE uuid = ?";
//
//        try (Connection c = getConnection(connId)){
//            try(PreparedStatement st = c.prepareStatement(sql)){
//                int i = 0;
//                st.setString(++i, a.getOwnerId().toString());
//                st.setInt(++i, a.getInfantry());
//                System.out.println("[Earth]"+a.getInfantry());
//                st.setInt(++i, a.getCavalry());
//                st.setInt(++i, a.getArtillery());
//                st.setString(++i, a.getLeaderName());
//                st.setInt(++i, a.getLeaderFire());
//                st.setInt(++i, a.getLeaderShock());
//                st.setInt(++i, a.getTechLvl());
//
//                st.setString(++i, a.getUuid().toString());
//
//                st.executeUpdate();
//                armyCache.put(a.getUuid(),a);
//            }
//
//        }catch (SQLException e) {
//            System.out.println("[Earth]Earth DataBase error in "+connId+": " + e.getMessage());
//            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error " + connId);
//            throw new RuntimeException(e);
//        }
//
//    }

    public Army getArmy(UUID armyId){
        if (armyCache.containsKey(armyId)) {
            return armyCache.get(armyId);
        }
        String sql = "SELECT * FROM armies WHERE uuid = ?";

        try (Connection conn = getConnection("getArmy");
             PreparedStatement statement = conn.prepareStatement(sql)) {

            statement.setString(1, armyId.toString());

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Army army = createArmyFromResultSet(rs);
                    armyCache.put(army.getUuid(), army); // Кэшируем результат
                    return army;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public HashSet<Army> getArmies(){
        if (armyCache.isEmpty()) {
            return new HashSet<>();
        }
        // Создаем новый HashSet и копируем в него все ссылки на города из мапы
        return new HashSet<>(armyCache.values());

    }


    public void deleteArmy(Army army){
        List<Unit> units = new ArrayList<>(army.getUnits());
        for(Unit u:units){
            deleteUnit(u);
        }
        armyCache.remove(army.getUuid());
        army.getOwner().removeArmy(army);
        try (Connection conn = getConnection("deleteArmy");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM armies WHERE uuid = ?")) {
            stmt.setString(1, army.getUuid().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUnit(Unit u){
        unitCache.put(u.getUniqueId(),u);

        String sql = "INSERT INTO units (" +
                "uuid, " +
                "armyId, " +
                "type, " +
                "lvl, " +
                "morale, " +
                "maxMorale, " +
                "disc, " +
                "fire, " +
                "shock) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(Connection c = getConnection("addUnit")){
            try(PreparedStatement st = c.prepareStatement(sql)){
                int i = 1;
                st.setString(i++, u.getUniqueId().toString());
                st.setString(i++, u.getArmyId().toString());
                st.setString(i++, u.getType());
                st.setInt(i++, u.getLvl());
                st.setDouble(i++, u.getMorale());
                st.setDouble(i++, u.getBaseMorale());
                st.setDouble(i++, u.getDisc());
                st.setDouble(i++, u.getFire());
                st.setDouble(i, u.getShock());

                st.executeUpdate();

            }


        }catch (SQLException e) {
            System.out.println("[Earth]Earth DataBase error in 'addUnit': " + e.getMessage());
            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error 'addUnit'");
            throw new RuntimeException(e);
        }
    }

    public HashSet<Unit> getUnits(){
        if (unitCache.isEmpty()) {
            return new HashSet<>();
        }
        // Создаем новый HashSet и копируем в него все ссылки на города из мапы
        return new HashSet<>(unitCache.values());
    }

    public Unit getUnit(UUID id){
        return unitCache.get(id);
    }

    public void deleteUnit(Unit unit){
        unitCache.remove(unit.getUniqueId());
        unit.getArmy().removeUnit(unit);
        try (Connection conn = getConnection("deleteUnit");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM units WHERE uuid = ?")) {
            stmt.setString(1, unit.getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBot(String botName, UUID botId) {
        String sqlPlayer = "INSERT INTO players (uuid, displayName) VALUES (?, ?)";
        String sqlTech = "INSERT INTO tech (uuid, username) VALUES (?, ?)";
        String sqlCountry = "INSERT INTO countries (id, ownerName, displayName) VALUES (?, ?, ?)";
        try (Connection c = getConnection("addBot")) {
            try (PreparedStatement preparedStatement = c.prepareStatement(sqlPlayer)) {
                preparedStatement.setString(1, String.valueOf(botId));
                preparedStatement.setString(2, botName);
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = c.prepareStatement(sqlTech)) {
                preparedStatement.setString(1, String.valueOf(botId));
                preparedStatement.setString(2, botName);
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement st = c.prepareStatement(sqlCountry)) {
                int i = 0;
                st.setString(++i, String.valueOf(botId));
                st.setString(++i, botName);
                st.setString(++i, botName);
                st.executeUpdate();
            }
            playerCache.put(botId, getPlayerFromDb(botId.toString()));
        } catch (SQLException e) {
            System.out.println("[Earth]Earth DataBase error: " + e.getMessage());
            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error");

        }
    }



    public void addPlayer(Player p){
        String sqlPlayer = "INSERT OR IGNORE INTO players (uuid, displayName) VALUES (?, ?)";
        String sqlTech = "INSERT OR IGNORE INTO tech (id, displayName) VALUES (?, ?)";
        String sqlCountry = "INSERT OR IGNORE INTO countries (id, ownerName, displayName) VALUES (?, ?, ?)";
        try(Connection c = getConnection("addPlayer")){
            try (PreparedStatement preparedStatement = c.prepareStatement(sqlPlayer)){
                preparedStatement.setString(1, p.getUniqueId().toString());
                preparedStatement.setString(2, p.getDisplayName());
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement preparedStatement = c.prepareStatement(sqlTech)){
                preparedStatement.setString(1, p.getUniqueId().toString());
                preparedStatement.setString(2, p.getDisplayName());
                preparedStatement.executeUpdate();
            }
            try (PreparedStatement st = c.prepareStatement(sqlCountry)){
                int i = 0;
                st.setString(++i, p.getUniqueId().toString());
                st.setString(++i, p.getDisplayName());
                st.setString(++i, p.getDisplayName());
                st.executeUpdate();
            }
            UUID id = p.getUniqueId();
            playerCache.put(id, getPlayerFromDb(id.toString()));
        } catch (SQLException e) {
            System.out.println("[Earth]Earth DataBase error: " + e.getMessage());
            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error");
            throw new RuntimeException(e);
        }

    }

//    public void updatePlayer(EarthPlayer p) {
//        try (PreparedStatement preparedStatement = connection.prepareStatement("UPDATE players SET username = ? WHERE uuid = ?")){
//            preparedStatement.setString(1,p.getDisplayName());
//            preparedStatement.setString(2,p.getUniqueId().toString());
//            preparedStatement.executeUpdate();
//        }
//    }

    public boolean playerExists(UUID id){
        try (Connection conn = getConnection("playerExists");
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM players WHERE uuid = ?")){
            preparedStatement.setString(1, id.toString());
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return true;
                }
            }
        } catch (SQLException e) {
            System.out.println("[Earth]Earth DataBase playerExists error: " + e.getMessage());
            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error");
            throw new RuntimeException(e);
        }
        return false;
    }

    public boolean playerNameActual(Player p){
        try (Connection conn = getConnection("playerNameActual");
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT displayName FROM players WHERE uuid = ?")){
            preparedStatement.setString(1,p.getUniqueId().toString());
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return resultSet.getString("displayName").equals(p.getDisplayName());
                }
            }
        } catch (SQLException e) {
            System.out.println("[Earth]Earth DataBase error: " + e.getMessage());
            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error");
            throw new RuntimeException(e);
        }
        return false;
    }

    public UUID getPlayerUuid(String playerName)  {
        try (Connection conn = getConnection("getPlayerUuid");
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT uuid FROM players WHERE displayName = ?")){
            preparedStatement.setString(1, playerName);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                UUID id = UUID.fromString(resultSet.getString("uuid"));
                OfflinePlayer[] players = Bukkit.getServer().getOfflinePlayers();
                return id;
            }else{
                return null;
            }
        } catch (SQLException e) {
            System.out.println("[Earth]Earth DataBase error: " + e.getMessage());
            Bukkit.broadcastMessage(ChatColor.RED + "DataBase error");
            throw new RuntimeException(e);
        }
    }









    public HashSet<EPlayer> getPlayers(){
        if (playerCache.isEmpty()) {
            return new HashSet<>();
        }
        // Создаем новый HashSet и копируем в него все ссылки на города из мапы
        return new HashSet<>(playerCache.values());
    }

    public EPlayer getPlayer(UUID uuid) {
        if(uuid == null) return null;
        return playerCache.get(uuid);
    }

    public EPlayer getPlayer(String name) {
        UUID id = getPlayerUuid(name);
        if(id == null) return null;
        return playerCache.get(id);
    }



//    // 2. УНИВЕРСАЛЬНОЕ СОХРАНЕНИЕ (UPDATE)
//    public void updatePlayer(EPlayer p) {
//        // Генерируем запрос вида: UPDATE countries SET techCost=?, morale=? ... WHERE id=?
//        String columns = Stream.of(EPlayerAttribute.values())
//                .map(type -> toCamelCase(type.name()) + " = ?")
//                .collect(Collectors.joining(", "));
//
//        String sql = "UPDATE " + "countries" + " SET displayName = ?, " + columns + " WHERE id = ?";
//
//        try (Connection conn = getConnection("updatePlayer");
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            int i = 1;
//            pstmt.setString(i++, p.getCountryName()); // displayName
//
//            for (EPlayerAttribute type : EPlayerAttribute.values()) {
//                pstmt.setDouble(i++, p.getAttribute(type));
//            }
//
//            pstmt.setString(i, p.getUniqueId().toString()); // WHERE id = ?
//            pstmt.executeUpdate();
//            playerCache.put(p.getUniqueId(), p);
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


//    public void updatePlayer(EPlayer p){
//
//        String sqlPlayers = "UPDATE players SET " +
//                "displayName = ? " +
//                "WHERE uuid = ?";
//
//        String sqlCountry = "UPDATE countries SET " +
//                "displayName = ? , " +
//                "oiBalance = ? , " +
//                "oiIncome = ? , " +
//                "oiSpent = ? , " +
//                "politBalance = ? , " +
//                "politIncome = ? , " +
//                "politIncomeMod = ? , " +
//                "politMax = ? , " +
//                "politMaxMod = ? , " +
//                "income = ? , " +
//                "tradeIncome = ? , " +
//                "tradeMod = ? , " +
//                "taxIncome = ? , " +
//                "taxMod = ? , " +
//                "expense = ? , " +
//                "corruption = ? , " +
//                "inflation = ? , " +
//                "warSupport = ? , " +
//                "warStatus = ? , " +
//                "treasury = ? " +
//                "WHERE ownerId = ?";
//
//        String sqlArmy = "UPDATE armyStats SET " +
//                "country = ? , " +
//                "limitMod = ? , " +
//                "expenseMod = ? , " +
//
//                "manpower = ? , " +
//                "manpowerLimitMod = ? , " +
//                "manpowerIncMod = ? , " +
//
//                "disciple = ? , " +
//                "tactic = ? , " +
//                "morale = ? , " +
//
//                "fireDamage = ? , " +
//                "fireResist = ? , " +
//
//                "shockDamage = ? , " +
//                "shockResist = ? , " +
//
//                "moraleDamage = ? , " +
//                "moraleResist = ? , " +
//
//                "infCost = ? , " +
//                "infCombatAbility = ? , " +
//
//                "cavCost = ? , " +
//                "cavRatio = ? , " +
//                "cavCombatAbility = ? , " +
//
//                "artCost = ? , " +
//                "artCombatAbility = ? " +
//                "WHERE ownerId = ?";
//        Country country = p.getCountry();
//        ArmyStats army = p.getArmyStats();
//        try (Connection conn = getConnection("updatePlayer")) {
//
//
//            try(PreparedStatement st = conn.prepareStatement(sqlPlayers)){
//                int index = 1;
//                st.setString(index++, p.getDisplayName());
//                st.setString(index,p.getUniqueId().toString());
//                st.executeUpdate();
//            }
//            try(PreparedStatement st = conn.prepareStatement(sqlCountry)){
//                int index = 1;
//                st.setString(index++,country.getDisplayName());
//                st.setInt(index++,country.getOiBalance());
//                st.setInt(index++,country.getOiIncome());
//                st.setInt(index++,country.getOiSpent());
//                st.setInt(index++,country.getPolitBalance());
//                st.setInt(index++,country.getPolitIncome());
//                st.setDouble(index++,country.getPolitIncomeMod());
//                st.setInt(index++,country.getPolitMax());
//                st.setDouble(index++,country.getPolitMaxMod());
//                st.setInt(index++,country.getIncome());
//                st.setInt(index++,country.getTradeIncome());
//                st.setDouble(index++,country.getTradeMod());
//                st.setInt(index++,country.getTaxIncome());
//                st.setDouble(index++,country.getTaxMod());
//                st.setInt(index++,country.getExpense());
//                st.setInt(index++,country.getCorruption());
//                st.setInt(index++,country.getInflation());
//                st.setInt(index++,country.getWarSup());
//                st.setInt(index++,country.getWarStatus());
//                st.setInt(index++,country.getTreasury());
//                st.setString(index,p.getUniqueId().toString());
//                st.executeUpdate();
//            }
//            try(PreparedStatement st = conn.prepareStatement(sqlArmy)){
//                int index = 1;
//                st.setString(index++,country.getDisplayName());
//                st.setDouble(index++, army.getLimitMod());
//                st.setDouble(index++, army.getExpenseMod());
//                st.setInt(index++, army.getManpower());
//                st.setDouble(index++, army.getManpowerLimitMod());
//                st.setInt(index++, army.getManpowerIncMod());
//                st.setDouble(index++, army.getDisciple());
//                st.setDouble(index++, army.getTac());
//                st.setDouble(index++, army.getMorale());
//                st.setDouble(index++, army.getFireDamage());
//                st.setDouble(index++, army.getFireResist());
//                st.setDouble(index++, army.getShockDamage());
//                st.setDouble(index++, army.getShockResist());
//                st.setDouble(index++, army.getMoraleDamage());
//                st.setDouble(index++, army.getMoraleResist());
//                st.setDouble(index++, army.getInfCost());
//                st.setDouble(index++, army.getInfCombatAbility());
//                st.setDouble(index++, army.getCavCost());
//                st.setDouble(index++, army.getCavRatio());
//                st.setDouble(index++, army.getCavCombatAbility());
//                st.setDouble(index++, army.getArtCost());
//                st.setDouble(index++, army.getArtCombatAbility());
//                st.setString(index, p.getUniqueId().toString());
//                st.executeUpdate();
//            }
//            playerCache.put(p.getUniqueId(), p);
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }


//
//
//    public boolean marketExists(UUID uuid){
//        try (Connection conn = getConnection("marketExists");
//             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM markets WHERE uuid = ?")){
//            preparedStatement.setString(1, uuid.toString());
//            try(ResultSet resultSet = preparedStatement.executeQuery()){
//                if (resultSet.next()){
//                    return true;
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return false;
//    }
//
//    public void addMarket(Town town, UUID marketId, String type, Location loc){
//        String sql = "INSERT INTO markets (town_name, uuid, town_id, world, chunk_x, chunk_z) VALUES (?, ?, ?, ?, ?, ?)";
//        try (Connection conn = getConnection("addMarket");
//             PreparedStatement preparedStatement = conn.prepareStatement(sql)){
//            int paramIndex = 1;
//            preparedStatement.setString(paramIndex++, town.getName());
//            preparedStatement.setString(paramIndex++, marketId.toString());
//            preparedStatement.setString(paramIndex++, town.getUniqueId().toString());
//            preparedStatement.setString(paramIndex++, Objects.requireNonNull(loc.getWorld()).getName());
//            preparedStatement.setInt(paramIndex++, loc.getChunk().getX());
//            preparedStatement.setInt(paramIndex, loc.getChunk().getZ());
//            preparedStatement.executeUpdate();
//        }
//        getMarket(marketId);
//    }
//
//    public void deleteMarket(Market market){
//
//        Town town = getTown(market.getTownId());
//        town.setBuildings(town.getBuildings()-1);
//        town.setLandHubId(null);
//
//
//        // Получаем мир и ищем ArmorStand
//        World world = Bukkit.getWorld(market.getWorld());
//        if (world == null) return;
//
//        Chunk chunk = world.getChunkAt(market.getLocation());
//        Location[] armorStandLocs = new Location[2];
//        armorStandLocs[0] = market.getLocation();
//        armorStandLocs[1] = market.getLocation().clone().add(0.5, -1, 0.5);
//        processChest(chunk,armorStandLocs[0],"market");
//        removeArmorStands(chunk,armorStandLocs);
//
//
//        try (Connection conn = getConnection("deleteMarket");
//             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM markets WHERE uuid = ?")){
//            preparedStatement.setString(1, market.getUniqueId().toString());
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//
//    public void updateMarket(Market market){
//
//        String sql = "UPDATE markets SET " +
//                "status = ? , " +
//                "goods = ? , " +
//                "trade_id = ? , " +
//                "market_modifier = ? " +
//                "WHERE uuid = ?";
//        try (Connection conn = getConnection("updateMarket");
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            int paramIndex = 1;
//            pstmt.setInt(paramIndex++, market.getStatus());
//            pstmt.setInt(paramIndex++, market.getGoods());
//            UUID tradeId = market.getTradeId();
//            if (tradeId != null) {
//                pstmt.setString(paramIndex++, tradeId.toString());
//            } else {
//                pstmt.setNull(paramIndex++, Types.VARCHAR);
//            }
//            pstmt.setInt(paramIndex++,market.getMarketModifier());
//            pstmt.setString(paramIndex, market.getUniqueId().toString());
//            int rows = pstmt.executeUpdate();
//            if (rows > 0) {
//                // Обновляем маркет в кэше
//                marketCache.put(market.getUniqueId(), market);
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public Market getMarket(UUID marketId){
//
//        if (marketCache.containsKey(marketId)) {
//            return marketCache.get(marketId);
//        }
//
//
//        // 2. Запрос в БД, если в кэше нет
//        String query = "SELECT * FROM markets WHERE uuid = ?";
//
//        try (Connection conn = getConnection("getMarket");
//             PreparedStatement statement = conn.prepareStatement(query)) {
//
//            statement.setString(1, marketId.toString());
//
//            try (ResultSet rs = statement.executeQuery()) {
//                if (rs.next()) {
//                    Market market = createMarketFromResultSet(rs);
//                    marketCache.put(marketId, market); // Кэшируем результат
//                    return market;
//                }
//            }
//
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        return null;
//    }
//
//    public List<Market> getMarkets() {
//
//        if (!marketCache.isEmpty()) {
//            return new ArrayList<>(marketCache.values());
//        }
//
//
//
//        List<Market> markets = new ArrayList<>();
//
//        try (Connection conn = getConnection("getMarkets");
//             Statement statement = conn.createStatement();
//             ResultSet rs = statement.executeQuery("SELECT * FROM markets")) {
//
//            while (rs.next()) {
//                try {
//                    Market market = createMarketFromResultSet(rs);
//                    markets.add(market);
//                    marketCache.put(market.getUniqueId(), market); // Кэшируем каждый рынок
//                } catch (SQLException e) {
//                    System.err.println("Error creating market from record: " + e.getMessage());
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return markets;
//    }
//
//    public List<Building> getMarketGoods(Town town){
//        List<Building> result = new ArrayList<>();
//        List<Building> buildings = getBuildings();
//        if(town.getLandHubId()==null){
//        }else{
//            for(Building b:buildings){
//                if (b.getMarketId().equals(town.getLandHubId()) && b.getItem()!=null){
//                    result.add(b);
//                }
//            }
//        }
//
//        return result;
//    }
//
//    private void cacheMarkets(){
//        marketCache.clear();
//        try (Connection conn = getConnection("cacheMarkets");
//             Statement statement = conn.createStatement();
//             ResultSet rs = statement.executeQuery("SELECT * FROM markets")) {
//
//            while (rs.next()) {
//                try {
//                    Market market = createMarketFromResultSet(rs);
//                    marketCache.put(market.getUniqueId(), market); // Кэшируем каждый рынок
//                } catch (SQLException e) {
//                    System.err.println("Error creating market from record: " + e.getMessage());
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public boolean townExists(UUID uuid){
        try (Connection conn = getConnection("townExists");
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM towns WHERE uuid = ?")){
            preparedStatement.setString(1, uuid.toString());
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    public void addTown(Town town){

        String sql = "INSERT INTO towns (owner_name, town_name, uuid, owner_id, type, world, chunk_x, chunk_z, houses) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection("addTown");
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            preparedStatement.setString(paramIndex++, town.getOwnerName());
            preparedStatement.setString(paramIndex++, town.getName());
            preparedStatement.setString(paramIndex++, town.getUniqueId().toString());
            preparedStatement.setString(paramIndex++, town.getOwnerId().toString());
            preparedStatement.setString(paramIndex++, town.getType());
            preparedStatement.setString(paramIndex++, town.getWorld());
            preparedStatement.setInt(paramIndex++, town.getChunkX());
            preparedStatement.setInt(paramIndex++, town.getChunkZ());
            preparedStatement.setInt(paramIndex, town.getHouses());
            preparedStatement.executeUpdate();
            townCache.put(town.getUniqueId(),town);
            markChunk(town.getChunkX(),town.getChunkZ(),town.getUniqueId());
            getPlayer(town.getOwnerId()).addTown(town);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void deleteTown(Town town) {
        List<Building> buildings = new ArrayList<>(town.getBuildings());
        for (Building b:buildings){
            deleteBuilding(b);
        }
        // Получаем мир и ищем ArmorStand
        World world = Bukkit.getWorld(town.getWorld());
        if (world == null) return;
        int radius = instance.getConfig().getInt("townSize")*16;
        for (Entity entity : world.getNearbyEntities(town.getLocation(),radius,radius,radius)) {
            if (Objects.equals(entity.getCustomName(), town.getOwnerName())) {
                entity.remove();
            }
        }
        Chunk chunk = world.getChunkAt(town.getLocation());
        Location[] armorStandLocs = new Location[3];
        armorStandLocs[0] = town.getLocation();
        armorStandLocs[1] = town.getLocation().clone().add(0.5, 1, 0.5);
        armorStandLocs[2] = town.getLocation().clone().add(0.5, -1, 0.5);
        //processChest(chunk,armorStandLocs[0],town.getType());
        removeArmorStands(chunk,armorStandLocs);
        town.getOwner().addAttribute(EPlayerAttribute.PEOPLE,-town.getPeople());

        markChunk(town.getChunkX(), town.getChunkZ());
        townCache.remove(town.getUniqueId());
        getPlayer(town.getOwnerId()).removeTown(town);
        try (Connection conn = getConnection("deleteTown");
             PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM towns WHERE uuid = ?")) {
            preparedStatement.setString(1, town.getUniqueId().toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
//
//    public void updateTown(Town town){
//        townCache.put(town.getUniqueId(),town);
//        String sql = "UPDATE towns SET " +
//                "owner_name = ?, " +
//                "town_name = ?, " +
//                "owner_id = ?, " +
//                "type = ?, " +
//                "blockade_status = ?, " +
//                "status = ?, " +
//                "buildings = ?, " +
//                "bonusBuildSites = ?, " +
//                "houses = ?, " +
//                "port_id = ?, " +       // заменили market_id на port
//                "landHub_id = ?, " +
//                "tradeTown = ?, " +// добавили landHub
//                "world = ?, " +
//                "chunk_x = ?, " +
//                "chunk_z = ? " +
//                "WHERE uuid = ?";
//        try (Connection conn = getConnection("updateTown");
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//
//            int paramIndex = 1;
//            pstmt.setString(paramIndex++, town.getOwnerName());
//            pstmt.setString(paramIndex++, town.getName());
//            pstmt.setString(paramIndex++, town.getOwnerId().toString());
//            pstmt.setString(paramIndex++, town.getType());
//            pstmt.setInt(paramIndex++, town.getBlockadeStatus());
//            pstmt.setInt(paramIndex++, town.getStatus());
//            pstmt.setInt(paramIndex++, town.getBuildingsAmount());
//            pstmt.setInt(paramIndex++, town.getBonusBuildSite());
//            pstmt.setInt(paramIndex++, town.getHouses());
//
//            // Устанавливаем port
//            UUID port = town.getPortId();
//            if (port != null) {
//                pstmt.setString(paramIndex++, port.toString());
//            } else {
//                pstmt.setNull(paramIndex++, Types.VARCHAR);
//            }
//
//            // Устанавливаем landHub
//            UUID landHub = town.getLandHubId();
//            if (landHub != null) {
//                pstmt.setString(paramIndex++, landHub.toString());
//            } else {
//                pstmt.setNull(paramIndex++, Types.VARCHAR);
//            }
//
//            UUID tradeTown = town.getTradeTownId();
//            if (tradeTown != null) {
//                pstmt.setString(paramIndex++, tradeTown.toString());
//            } else {
//                pstmt.setNull(paramIndex++, Types.VARCHAR);
//            }
//
//            pstmt.setString(paramIndex++, town.getWorld());
//            pstmt.setInt(paramIndex++, town.getChunkX());
//            pstmt.setInt(paramIndex++, town.getChunkZ());
//
//            pstmt.setString(paramIndex, town.getUniqueId().toString());
//
//            int affectedRows = pstmt.executeUpdate();
//            if (affectedRows == 0) {
//                throw new SQLException("Updating town failed, no rows affected.");
//            }
//        } catch (SQLException e) {
//            System.err.println("Error updating town " + town.getUniqueId() + ": " + e.getMessage());
//            throw new RuntimeException(e);
//        }
//    }


    // Уже должен существовать где-то в классе:




    public HashSet<Town> getTowns() {
        if (townCache.isEmpty()) {
            return new HashSet<>();
        }
        // Создаем новый HashSet и копируем в него все ссылки на города из мапы
        return new HashSet<>(townCache.values());
    }







    public Town getTown(UUID townId) {

        return townCache.get(townId);
    }



    public boolean buildingExists(UUID buildingId){
        try (Connection conn = getConnection("buildingExists");
             PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM buildings WHERE uuid = ?")){
            preparedStatement.setString(1, buildingId.toString());
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if (resultSet.next()){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }


    public void addBuilding(Building building){
        try (Connection conn = getConnection("addBuilding");
             PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO buildings (town_name, uuid, town_id, type, world, chunk_x, chunk_z) VALUES (?, ?, ?, ?, ?, ?, ?)")){
            int paramIndex = 1;
            preparedStatement.setString(paramIndex++, building.getTownName());
            preparedStatement.setString(paramIndex++, building.getUniqueId().toString());
            preparedStatement.setString(paramIndex++, building.getTownId().toString());
            preparedStatement.setString(paramIndex++, building.getType());
            preparedStatement.setString(paramIndex++, building.getWorld());
            preparedStatement.setInt(paramIndex++, building.getX());
            preparedStatement.setInt(paramIndex, building.getZ());
            preparedStatement.executeUpdate();
            buildingCache.put(building.getUniqueId(),building);
            Town town = getTown(building.getTownId());
            town.addBuilding(building);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void deleteBuilding(Building building) {
        // Обновляем количество зданий в городе
        Town town = getTown(building.getTownId());
        town.removeBuilding(building);

        // Получаем мир и ищем ArmorStand
        World world = Bukkit.getWorld(building.getWorld());
        if (world == null) return;

        Chunk chunk = world.getChunkAt(building.getLocation());
        Location[] armorStandLocs = new Location[3];
        armorStandLocs[0] = building.getLocation();
        armorStandLocs[1] = building.getLocation().clone().add(0.5, 1, 0.5);
        armorStandLocs[2] = building.getLocation().clone().add(0.5, -1, 0.5);
        //processChest(chunk,armorStandLocs[0],"building");
        removeArmorStands(chunk,armorStandLocs);

        // Удаляем здание из кэша и БД
        buildingCache.remove(building.getUniqueId());
        try (Connection conn = getConnection("deleteBuilding");
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM buildings WHERE uuid = ?")) {
            stmt.setString(1, building.getUniqueId().toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



//
//    public List<Building> getPlayerBuildings( UUID playerId) {
//        List<Building> buildings = getBuildings();
//        List<Town> towns = new ArrayList<>(getPlayerTowns(playerId));
//
//        if (buildings == null) return null;
//
//        // Собираем ID всех городов игрока в Set для быстрого поиска
//        Set<UUID> playerTownIds = Arrays.stream(towns)
//                .map(Town::getUniqueId)
//                .collect(Collectors.toSet());
//
//        List<Building> result = new ArrayList<>();
//
//        for (Building b : buildings) {
//            if (b.getStatus() != 0 && playerTownIds.contains(b.getTownId())) {
//                result.add(b);
//            }
//        }
//
//        return result.isEmpty() ? null : result;
//    }


    public Building getBuilding(UUID buildingId) {
        return buildingCache.get(buildingId);
    }
    public HashSet<Building> getBuildings() {
        if (buildingCache.isEmpty()) {
            return new HashSet<>();
        }
        // Создаем новый HashSet и копируем в него все ссылки на города из мапы
        return new HashSet<>(buildingCache.values());
    }


//    public Building getBuildingFromDb(UUID buildingId){
//        // Оптимизация: запрашиваем только нужное здание
//        String sql = "SELECT * FROM buildings WHERE uuid = ?";
//
//        try (Connection conn = getConnection("getBuildingFromDb");
//             PreparedStatement stmt = conn.prepareStatement(sql)) {
//
//            stmt.setString(1, buildingId.toString());
//
//            try (ResultSet rs = stmt.executeQuery()) {
//                if (rs.next()) {
//                    return createBuildingFromResultSet(rs);
//                }
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//        return null;
//    }


//
//    public void cacheBuildings(){
//        buildingCache.clear();
//        townBuildingCache.clear();
//
//        String query = "SELECT * FROM buildings";
//
//        try (Connection conn = getConnection("getBuildings");
//             PreparedStatement stmt = conn.prepareStatement(query);
//             ResultSet rs = stmt.executeQuery()) {
//            while (rs.next()) {
//                Building building = createBuildingFromResultSet(rs);
//                buildingCache.put(building.getUniqueId(),building);
//                townBuildingCache.computeIfAbsent(building.getTownId(), k -> new HashSet<>()).add(building);
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    private void updateBuildingInCache(Building building) {
//        buildingCache.put(building.getUniqueId(), building);
//        townBuildingCache.computeIfAbsent(building.getTownId(), k -> new HashSet<>()).add(building);
//    }

//
//    public void updateBuilding(Building building) {
//        updateBuildingInCache(building);
//        String sql = "UPDATE buildings SET " +
//                "town_name = ?, " +
//                "town_id = ?, " +
//                "type = ?, " +
//                "item = ?, " +
//                "status = ?, " +
//                "world = ?, " +
//                "chunk_x = ?, " +
//                "chunk_z = ?, " +
//                "market_id = ? " +
//                "WHERE uuid = ?";
//
//        try (Connection conn = getConnection("updateBuilding");
//             PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            int paramIndex = 1;
//            pstmt.setString(paramIndex++, building.getTownName());
//            pstmt.setString(paramIndex++, building.getTownId().toString());
//            pstmt.setString(paramIndex++, building.getType());
//            pstmt.setString(paramIndex++, building.getItem() != null ? building.getItem().toString() : null);
//            pstmt.setInt(paramIndex++, building.getStatus());
//            pstmt.setString(paramIndex++, building.getWorld());
//            pstmt.setInt(paramIndex++, building.getX());
//            pstmt.setInt(paramIndex++, building.getZ());
//            pstmt.setString(paramIndex++, building.getMarketId() != null ? building.getMarketId().toString() : null);
//            pstmt.setString(paramIndex, building.getUniqueId().toString());
//
//            pstmt.executeUpdate();
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public boolean markChunkAsClaimed(int chunkX, int chunkZ, Town town){
//        String world = town.getWorld();
//        ChunkPosition chunk = new ChunkPosition(chunkX,chunkZ,world);
//
//        // Попробуем сразу обновить, если чанк существует и не принадлежит другому городу
//        String updateSql = "UPDATE claimed_chunks SET town_id = ? " +
//                "WHERE x = ? AND z = ? AND world = ? AND town_id IS NULL";
//        try (Connection conn = getConnection("markChunkAsClaimed");
//             PreparedStatement ps = conn.prepareStatement(updateSql)) {
//
//            ps.setString(1, town.getUniqueId().toString());
//            ps.setInt(2, chunkX);
//            ps.setInt(3, chunkZ);
//            ps.setString(4, world);
//            int updated = ps.executeUpdate();
//            if (updated > 0) {
//                chunkClaimCache.put(chunk, town.getUniqueId());
//                return true;
//            }
//
//        } catch (SQLException e) {
//            System.err.println("Ошибка при обновлении чанка: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//
//
//
//        if(getChunk(chunk)==null){
//            // Если не получилось обновить, пробуем вставить новый
//            String insertSql = "INSERT INTO claimed_chunks (x, z, town_id, world) " +
//                    "VALUES (?, ?, ?, ?)";
//
//            try (Connection conn = getConnection("markChunkAsClaimed");
//                 PreparedStatement ps = conn.prepareStatement(insertSql)) {
//
//                ps.setInt(1, chunkX);
//                ps.setInt(2, chunkZ);
//                ps.setString(3, town.getUniqueId().toString());
//                ps.setString(4, world);
//                ps.executeUpdate();
//                chunkClaimCache.put(chunk, town.getUniqueId());
//                return true;
//
//
//            } catch (SQLException e) {
//                System.err.println("Ошибка при обновлении чанка: " + e.getMessage());
//                return false;
//            }
//        }
//        return false;
//
//
//    }
//
//
//    public UUID isChunkClaimed(ChunkPosition chunk) {
//        ChunkClaimCache.ChunkCacheResult cached = chunkClaimCache.get(chunk);
//        if (cached == null) {
//            int townSize = (int) Math.pow(instance.getConfig().getInt("townSize"),2);
//            Town town = null;
//            Set<Town> towns = getTowns();
//            Town closestTown = towns.stream()
//                    .min(Comparator.comparingDouble(t ->
//                            Tools.getDistanceSqrd(t.getX(), t.getZ(), chunk.getX(), chunk.getZ())
//                    ))
//                    .orElse(null);
//            if (closestTown == null) return null;
//            //Bukkit.broadcastMessage(closestTown.getName() + " " + townSize + " " + Tools.getDistanceSqrd(closestTown.getX(), closestTown.getZ(), chunk.getX(), chunk.getZ()));
//            if (Tools.getDistanceSqrd(closestTown.getX(), closestTown.getZ(), chunk.getX(), chunk.getZ())<townSize-1) town = closestTown;
//            UUID townId = null;
//            if (town!=null) {
//                townId = town.getUniqueId();
//            }
//            chunkClaimCache.put(chunk, townId);
//            return townId;
//        } else {
//            if (!cached.claimed()) {
//                return null; // чанк свободен
//            }
//            return cached.townId();
//        }
//    }
//
//    public EarthChunk getChunk(ChunkPosition chunk) {
//        String sql0 = "SELECT * FROM claimed_chunks WHERE x = ? AND z = ? AND world = ?";
//        try (Connection conn = getConnection("getChunk");
//             PreparedStatement ps0 = conn.prepareStatement(sql0)) {
//            ps0.setInt(1, chunk.x());
//            ps0.setInt(2, chunk.z());
//            ps0.setString(3, chunk.world().toString());
//
//            try (ResultSet rs = ps0.executeQuery()) {
//                if (rs.next()) {
//                    return new EarthChunk(rs.getInt("x"), rs.getInt("z"), rs.getString("town_id"));
//                }
//                return null; // или throw new ChunkNotFoundException();
//            }
//        } catch (SQLException e) {
//            throw new RuntimeException("Database error while fetching chunk", e);
//        }
//
//    }
//
//    public void unclaimAllTownChunk(Town town) {
//        String sql = "DELETE FROM claimed_chunks WHERE town_id = ?";
//
//
//        try (Connection conn = getConnection("unclaimAllTownChunk");
//             PreparedStatement statement = conn.prepareStatement(sql)) {
//            // или setNull(1, Types.VARCHAR)
//            statement.setString(1, town.getUniqueId().toString());
//
//            int affectedRows = statement.executeUpdate();
//            if (affectedRows == 0) {
//                System.out.println("[Earth]Чанки не найдены для города: " + town.getName());
//            } else {
//                System.out.println("[Earth]Обновлено чанков: " + affectedRows);
//            }
//            chunkClaimCache.invalidateAllByTownId(town.getUniqueId());
//
//
//        } catch (SQLException e) {
//            System.err.println("Ошибка при обновлении чанков для города " + town.getName());
//            e.printStackTrace();
//        }
//    }
//
//    public void unclaimChunk(int chunkX, int chunkZ, String worldName) {
//        String sql = "SET town_id = ? FROM claimed_chunks WHERE x = ? AND z = ? AND world = ?";
//        try (Connection conn = getConnection("unclaimChunk");
//             PreparedStatement statement = conn.prepareStatement(sql);) {
//
//            statement.setInt(1, chunkX);
//            statement.setInt(2, chunkZ);
//            statement.setString(3, worldName);
//
//            int affectedRows = statement.executeUpdate();
//            if (affectedRows == 0) {
//                System.out.println("[Earth]Чанк не найден: " + chunkX + ", " + chunkZ + " в мире " + worldName);
//            }
//
//
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public Set<ChunkPosition> getClaimedChunksByTownId(UUID townId) {
//        Set<ChunkPosition> claimedChunks = new HashSet<>();
//        String world = getTown(townId).getWorld();
//        String sql = "SELECT x, z FROM claimed_chunks WHERE town_id = ?";
//
//        try (Connection conn = getConnection("getClaimedChunksByTownId");
//             PreparedStatement statement = conn.prepareStatement(sql)) {
//            statement.setString(1, townId.toString());
//
//            try (ResultSet resultSet = statement.executeQuery()) {
//                while (resultSet.next()) {
//                    int x = resultSet.getInt("x");
//                    int z = resultSet.getInt("z");
//                    claimedChunks.add(new ChunkPosition(x, z,world));
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        return claimedChunks;
//    }
//    public Set<ChunkPosition> getAllClaimedChunks(World world) {
//        Set<ChunkPosition> claimedChunks = new HashSet<>();
//        String sql = "SELECT x, z FROM claimed_chunks WHERE town_id IS NOT NULL";
//
//        try (Connection conn = getConnection("getAllClaimedChunks");
//             PreparedStatement statement = conn.prepareStatement(sql);
//             ResultSet resultSet = statement.executeQuery()) {
//
//            while (resultSet.next()) {
//                int x = resultSet.getInt("x");
//                int z = resultSet.getInt("z");
//                claimedChunks.add(new ChunkPosition(x, z, world.getName()));
//            }
//        } catch (SQLException e) {
//            System.err.println("Ошибка при получении всех занятых чанков: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return claimedChunks;
//    }
//

    public void updateStatusMora(String value){CustomConfig.set("status.mora", value);}

    public void updateStatusDay(int value) {CustomConfig.set("status.day",value);}

    public boolean getStatusMora()  {return Objects.equals(CustomConfig.get().getString("status.mora"), "on");}

    public int getStatusDay() {return CustomConfig.get().getInt("status.day");}




}
