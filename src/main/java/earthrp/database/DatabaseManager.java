//package earthrp.database;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//
//import javax.sql.DataSource;
//import java.sql.Connection;
//import java.sql.SQLException;
//
//public class DatabaseManager {
//
//    private static HikariDataSource dataSource;
//
//    public static void initialize(String path) {
//        if (dataSource != null) return;
//
//        HikariConfig config = new HikariConfig();
//
//        // Путь к SQLite-файлу
//        config.setJdbcUrl("jdbc:sqlite:" + path);
//
//        // SQLite не использует логин/пароль, но поля обязательны
//        config.setUsername("user");
//        config.setPassword("pass");
//
//        // Оптимальные настройки для SQLite
//        config.setMaximumPoolSize(10); // ограничь количество подключений
//        config.setConnectionTestQuery("SELECT 1"); // быстрая проверка
//        config.setPoolName("MyPlugin-SQLitePool");
//
//        // Настройка для SQLite (обязательно!)
//        config.addDataSourceProperty("journal_mode", "WAL");
//        config.addDataSourceProperty("busy_timeout", "5000");
//
//        dataSource = new HikariDataSource(config);
//    }
//
////    static {
////        HikariConfig config = new HikariConfig();
////
////        // Путь к SQLite-файлу
////        config.setJdbcUrl("jdbc:sqlite:plugins/MyPlugin/database.db");
////
////        // SQLite не использует логин/пароль, но поля обязательны
////        config.setUsername("user");
////        config.setPassword("pass");
////
////        // Оптимальные настройки для SQLite
////        config.setMaximumPoolSize(10); // ограничь количество подключений
////        config.setConnectionTestQuery("SELECT 1"); // быстрая проверка
////        config.setPoolName("MyPlugin-SQLitePool");
////
////        // Настройка для SQLite (обязательно!)
////        config.addDataSourceProperty("journal_mode", "WAL");
////        config.addDataSourceProperty("busy_timeout", "5000");
////
////        dataSource = new HikariDataSource(config);
////    }
//
//    public static Connection getConnection() throws SQLException {
//        return dataSource.getConnection();
//    }
//
//    public static void close() {
//        if (dataSource != null && !dataSource.isClosed()) {
//            dataSource.close();
//        }
//    }
//}
