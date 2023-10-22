package org.binekosmac.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String DATABASE_URL = "jdbc:h2:file:./data/db";
    private static final String USERNAME = "sa";
    private static final String PASSWORD = "";

    private static final Object lock = new Object();

    private static Connection connection;

    public static Connection getConnection() throws SQLException {
        synchronized (lock) {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);
            }
            return connection;
        }
    }

    public static void closeConnection() throws SQLException {
        synchronized (lock) {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            connection = null;
        }
    }
}
