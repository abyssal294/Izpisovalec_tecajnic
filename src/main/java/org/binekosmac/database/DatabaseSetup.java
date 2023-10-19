package org.binekosmac.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String TECAJNICE_TABLE_CREATION_SQL =
            "CREATE TABLE IF NOT EXISTS tecajnice (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "datum DATE NOT NULL, " +
                    "oznaka VARCHAR(3) NOT NULL, " +
                    "sifra INT NOT NULL, " +
                    "vrednost DECIMAL(10, 8) NOT NULL" +
                    ");";



    public static void initialize() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(TECAJNICE_TABLE_CREATION_SQL);

        } catch (SQLException e) {
            System.err.println("Error setting up database: " + e.getMessage());

            throw new RuntimeException("Error initializing database", e);
        }
    }
}
