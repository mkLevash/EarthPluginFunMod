//package earthrp.database;
//
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.SQLException;
//
//public class DatabaseManager {
//    private static final String DB_URL = "jdbc:sqlite:database.db";
//    private static DatabaseManager instance;
//
//    // Регистрируем драйвер при загрузке класса
//    static {
//        try {
//            Class.forName("org.sqlite.JDBC");
//        } catch (ClassNotFoundException e) {
//            throw new RuntimeException("Failed to load SQLite JDBC driver", e);
//        }
//    }
//
//    private DatabaseManager() {
//        // Приватный конструктор для синглтона
//    }
//
//    public static synchronized DatabaseManager getInstance() {
//        if (instance == null) {
//            instance = new DatabaseManager();
//        }
//        return instance;
//    }
//
//    public Connection getConnection()  {
//        Connection conn = DriverManager.getConnection(DB_URL);
//        // Включаем foreign keys для SQLite
//        conn.createStatement().execute("PRAGMA foreign_keys = ON");
//        return conn;
//    }
//
//    public void testConnection()  {
//        try (Connection conn = getConnection()) {
//            if (!conn.isValid(1000)) {
//                throw new SQLException("Connection test failed");
//            }
//        }
//    }
//
//    // Дополнительные настройки БД
//    public void optimizeDatabase()  {
//        try (Connection conn = getConnection();
//             Statement stmt = conn.createStatement()) {
//            stmt.execute("PRAGMA journal_mode = WAL");
//            stmt.execute("PRAGMA synchronous = NORMAL");
//            stmt.execute("PRAGMA cache_size = -2000"); // 2MB кеша
//        }
//    }
//}
