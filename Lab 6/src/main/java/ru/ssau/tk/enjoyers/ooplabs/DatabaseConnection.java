package ru.ssau.tk.enjoyers.ooplabs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://192.168.0.250:5432/postgres";
    private static final Properties PROPERTIES = new Properties();

    static {
        PROPERTIES.setProperty("user", "postgres");
        PROPERTIES.setProperty("password", "46144164");
        PROPERTIES.setProperty("ssl", "false");
    }

    public static Connection getConnection() throws SQLException {
        try {
            return DriverManager.getConnection(URL, PROPERTIES);
        } catch (SQLException e) {
            throw new SQLException("Failed to connect to database", e);
        }
    }
}