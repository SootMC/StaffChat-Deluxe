package dev.jamieisgeek.superultrastaffchat.Models;

import java.sql.*;

public class Database {
    private Connection connection;
    private static Database database = null;

    public Database(String address, String database, String username, String password, String port) throws SQLException {
        this.connection = DriverManager.getConnection(
                "jdbc:mariadb://" + address + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false",
                username,
                password
        );

        Database.database = this;
    }

    public Connection getConnection() {
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
            PreparedStatement stmt = this.connection.prepareStatement("SELECT Name, Vanished FROM premiumvanish_playerdata WHERE UUID = ?");
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