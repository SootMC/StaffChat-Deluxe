package dev.jamieisgeek.superultrastaffchat.Models;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Database {
    private HikariDataSource dataSource;

    private String address;
    private String databaseName;
    private String username;
    private String password;
    private String port;

    private static Database database = null;

    public Database(String address, String database, String username, String password, String port) {
        this.address = address;
        this.databaseName = database;
        this.username = username;
        this.password = password;
        this.port = port;

        this.dataSource = createDataSource();

        Database.database = this;
    }

    private HikariDataSource createDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mariadb://%s:%s/%s?autoReconnect=true&useSSL=false", address, port, databaseName));
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        return new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static Database getDatabase() {
        return database;
    }

    public void closeConnection() throws SQLException {
        dataSource.close();
    }

    public boolean getPlayerVanished(String UUID) {
        try (Connection connection = getConnection()) {
            PreparedStatement stmt = connection.prepareStatement("SELECT Name, Vanished FROM premiumvanish_playerdata WHERE UUID = ?");
            stmt.setString(1, UUID);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getInt("Vanished") == 1;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}