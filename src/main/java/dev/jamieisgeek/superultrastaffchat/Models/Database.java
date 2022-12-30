package dev.jamieisgeek.superultrastaffchat.Models;

import java.sql.*;

public class Database {
    private Connection connection;
    private String address;
    private String databaseName;
    private String username;
    private String password;
    private String port;

    private static Database database = null;

    public Database(String address, String database, String username, String password, String port) throws SQLException {
        this.address = address;
        this.databaseName = database;
        this.username = username;
        this.password = password;
        this.port = port;

        this.connection = createConnection();

        Database.database = this;
    }

    private Connection createConnection() throws SQLException {
        return DriverManager.getConnection(
                "jdbc:mariadb://" + address + ":" + port + "/" + databaseName + "?autoReconnect=true&useSSL=false",
                username,
                password
        );
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        return connection;
    }

    public static Database getDatabase() {
        return database;
    }

    public void closeConnection() throws SQLException {
        if (this.connection != null) {
            this.connection.close();
        } else {
            throw new SQLException("Connection is null, aborting close!");
        }
    }

    public boolean getPlayerVanished(String UUID) {
        try {
            PreparedStatement stmt = getConnection().prepareStatement("SELECT Name, Vanished FROM premiumvanish_playerdata WHERE UUID = ?");
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