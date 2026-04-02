package earthrp.database;

import earthrp.Earth;
import earthrp.customEnums.EPlayerAttribute;
import earthrp.customEnums.EPlayerTech;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class dbTools {

    public static String toCamelCase(String s) {
        String[] parts = s.toLowerCase().split("_");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase()).append(parts[i].substring(1));
        }
        return sb.toString();
    }
    
    public static void createTables(String path){

        
        try(Connection c = DriverManager.getConnection("jdbc:sqlite:" + path);
            Statement st = c.createStatement()){

            String table = "countries";
            // Создаем саму таблицу, если её нет
            st.execute("CREATE TABLE IF NOT EXISTS " + table + " (id TEXT PRIMARY KEY, ownerName TEXT, displayName TEXT)");

            // АВТО-МИГРАЦИЯ: Добавляем колонки из Enum, если их еще нет в БД
            for (EPlayerAttribute type : EPlayerAttribute.values()) {
                String colName = toCamelCase(type.name());
                String dbType = "REAL";
                try {
                    st.execute("ALTER TABLE " + table + " ADD COLUMN " + colName +
                            " " + dbType + " DEFAULT " + type.getDefaultValue());
                } catch (SQLException e) {
                    // Игнорируем ошибку, если колонка уже существует
                }
            }

            table = "tech";
            // Создаем саму таблицу, если её нет
            st.execute("CREATE TABLE IF NOT EXISTS " + table + " (id TEXT PRIMARY KEY, displayName TEXT)");

            // АВТО-МИГРАЦИЯ: Добавляем колонки из Enum, если их еще нет в БД
            for (EPlayerTech type : EPlayerTech.values()) {
                String colName = toCamelCase(type.name());
                String dbType = "INTEGER";
                try {
                    st.execute("ALTER TABLE " + table + " ADD COLUMN " + colName +
                            " " + dbType + " DEFAULT " + type.isResearched());
                } catch (SQLException e) {
                    // Игнорируем ошибку, если колонка уже существует
                }
            }

            st.execute("""
                CREATE TABLE IF NOT EXISTS players (
                uuid TEXT PRIMARY KEY,
                displayName TEXT NOT NULL)
                """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS buildings (
                town_name TEXT NOT NULL,
                uuid TEXT PRIMARY KEY,
                town_id TEXT NOT NULL,
                market_id TEXT NULL,
                type TEXT NOT NULL,
                status INTEGER NOT NULL DEFAULT 1,
                world TEXT NOT NULL,
                x REAL NOT NULL,
                y REAL NOT NULL,
                z REAL NOT NULL,
                item TEXT NULL)
                """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS towns (
                owner_name TEXT NOT NULL,
                town_name TEXT NOT NULL,
                uuid TEXT PRIMARY KEY,
                owner_id TEXT NOT NULL,
                type TEXT NOT NULL,
                blockade_status INTEGER NOT NULL DEFAULT 0,
                status INTEGER NOT NULL DEFAULT 1,
                core INTEGER NOT NULL DEFAULT 0,
                infrastructure INTEGER NOT NULL DEFAULT 0,
                bonusBuildSites INTEGER NOT NULL DEFAULT 0,
                houses INTEGER NOT NULL,
                world TEXT NOT NULL,
                chunk_x INTEGER NOT NULL,
                chunk_z INTEGER NOT NULL,
                port INTEGER NOT NULL DEFAULT 0,
                landHub INTEGER NOT NULL DEFAULT 0,
                tradeTown TEXT NULL)
                """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS markets (
                town_name TEXT NOT NULL,
                uuid TEXT PRIMARY KEY,
                town_id TEXT NOT NULL,
                trade_id TEXT DEFAULT NULL,
                trade_distance INTEGER DEFAULT NULL,
                status INTEGER NOT NULL DEFAULT 1,
                goods INTEGER NOT NULL DEFAULT 0,
                market_modifier INTEGER NOT NULL DEFAULT 0,
                world TEXT NOT NULL,
                chunk_x INTEGER NOT NULL,
                chunk_z INTEGER NOT NULL)
                """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS claimed_chunks (
                x INTEGER NOT NULL,
                z INTEGER NOT NULL,
                world TEXT NOT NULL,
                town_id TEXT,
                UNIQUE(x, z, world))
                """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS units (
                    uuid TEXT PRIMARY KEY,
                    armyId TEXT NOT NULL,
                    type TEXT,
                    lvl INT,
                    hp INT DEFAULT 1000,
                    morale REAL,
                    maxMorale REAL,
                    disc REAL,
                    fire REAL,
                    shock REAL)
                    """);

            st.execute("""
                    CREATE TABLE IF NOT EXISTS armies (
                    uuid TEXT PRIMARY KEY,
                    ownerId TEXT NOT NULL,
                    infantry INT NOT NULL DEFAULT 0,
                    cavalry INT NOT NULL DEFAULT 0,
                    artillery INT NOT NULL DEFAULT 0,
                    leaderName TEXT DEFAULT null,
                    leaderFire INT DEFAULT null,
                    leaderShock INT DEFAULT null,
                    maxLvl INT NOT NULL DEFAULT 0)
                    """);
        } catch (SQLException e) {Earth.getInstance().getLogger().log(Level.SEVERE, "Критическая ошибка при создании таблиц базы данных!", e);}
        
        
    }
}
