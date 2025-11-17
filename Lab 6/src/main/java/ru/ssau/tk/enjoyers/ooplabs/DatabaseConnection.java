package ru.ssau.tk.enjoyers.ooplabs;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:54322/postgres";
    private static final String username = "postgres";
    private static final String password = "example";

    public static Connection getConnection() throws SQLException {
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(URL, username, password);
        } catch (ClassNotFoundException e) {
            System.out.println("PostgreSQL JDBC Driver not found!");
        } catch (SQLException e) {
            System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
            System.out.println();
            throw new SQLException("Failed to connect to database!", e);
        }
        
        return connection;
    }
}