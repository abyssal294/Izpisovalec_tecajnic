package org.binekosmac.ui;

import org.binekosmac.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DataProcessor {
    public List<CurrencyRate> retrieveData(LocalDate startDate, LocalDate endDate, List<String> currencies) throws SQLException {
            StringBuilder queryBuilder = new StringBuilder("SELECT * FROM tecajnice WHERE datum BETWEEN ? AND ? AND oznaka IN (");

            for (int i = 0; i < currencies.size(); i++) {
              queryBuilder.append("?");
              if (i < currencies.size() - 1) {
                queryBuilder.append(",");
              }
            }
            queryBuilder.append(")");
            queryBuilder.append("ORDER BY oznaka");

            Connection connection = DatabaseConnection.getConnection();

        PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());

        statement.setDate(1, Date.valueOf(startDate));
        statement.setDate(2, Date.valueOf(endDate));

        int index = 3;
        for(String currency : currencies) {
          statement.setString(index++, currency);
        }

        List<CurrencyRate> rates = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                LocalDate datum = rs.getDate("datum").toLocalDate();
                String oznaka = rs.getString("oznaka");
                int sifra = rs.getInt("sifra");
                double vrednost = rs.getDouble("vrednost");

                rates.add(new CurrencyRate(datum, oznaka, sifra, vrednost));
            }
        }

        return rates;
    }


}

