package org.binekosmac.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

    private static final String EXCHANGE_RATES_TABLE_CREATION_SQL =
            "CREATE TABLE IF NOT EXISTS exchange_rates (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "date DATE NOT NULL, " +
                    "currency_acronym VARCHAR(3) NOT NULL, " +
                    "currency_code INT NOT NULL, " +
                    "exchange_rate DECIMAL(15, 8) NOT NULL" +
                    ");";



    public static void initialize() {
        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement()) {

            stmt.executeUpdate(EXCHANGE_RATES_TABLE_CREATION_SQL);

        } catch (SQLException e) {
            System.err.println("Error setting up database: " + e.getMessage());

            throw new RuntimeException("Error initializing database", e);
        }
    }
}
