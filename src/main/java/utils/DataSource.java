package utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import exceptions.ConnectionException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public final class DataSource {
    private static final HikariDataSource dataSource;
    private static final String DRIVER = "driver";
    private static final String URL = "url";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private DataSource() {
    }

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            throw new ConnectionException(e.getMessage());
        }
//        var url = "jdbc:sqlite:C:/Users/Olegarh/IdeaProjects/exchanger_forever/src/main/resources/exchanger.db";
//
//        try (var conn = DriverManager.getConnection(url)) {
//            System.out.println("Connection to SQLite has been established.");
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
        HikariConfig config = new HikariConfig();
//        config.setDataSourceClassName(PropertiesUtil.getProperties(DRIVER));
        config.setJdbcUrl("jdbc:sqlite:C:/Users/Olegarh/IdeaProjects/exchanger_forever/src/main/resources/exchanger.db");
//        config.setUsername(PropertiesUtil.getProperties(USERNAME));
//        config.setPassword(PropertiesUtil.getProperties(PASSWORD));
        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new ConnectionException(e.getMessage());
        }
    }
}
