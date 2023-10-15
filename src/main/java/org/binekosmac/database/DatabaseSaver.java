package org.binekosmac.database;

import org.binekosmac.model.ExchangeRateList;
import org.binekosmac.model.ExchangeRateList.ExchangeRate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DatabaseSaver {

    public void saveExchangeRateList(ExchangeRateList exchangeRateList) throws Exception {
        String sql = "INSERT INTO exchange_rates (date, currency_acronym, currency_code, exchange_rate) VALUES (?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(sql)) {

            // Parse the date from the XML
            SimpleDateFormat xmlDateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date dateParsed = xmlDateFormat.parse(exchangeRateList.getDate());

            // Format the date for SQL
            SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateForSql = sqlDateFormat.format(dateParsed);

            for (ExchangeRate rate : exchangeRateList.getExchangeRates()) {
                pstmt.setString(1, dateForSql);
                pstmt.setString(2, rate.getAcronym());
                pstmt.setInt(3, rate.getCode());
                pstmt.setDouble(4, rate.getRate());

                pstmt.addBatch();
            }

            // Execute batch insert
            pstmt.executeBatch();

            System.out.println("Data saved successfully.");
        } catch (SQLException e) {
            throw new RuntimeException("Error saving exchange rate data to database", e);
        }
    }
}
