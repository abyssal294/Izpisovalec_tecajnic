package org.binekosmac.ui;

import org.binekosmac.database.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;

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
        for (String currency : currencies) {
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

    public String calculateForexData(String currency1, String currency2, String timeFrame) {
        try {
            // Današnji dan (dinamično) in glede na le-tega, izračun začetnega datuma
              LocalDate today = LocalDate.now();
              LocalDate historicalDate = calculateStartDate(today, timeFrame);

            // SQL string za iskanje vrednosti (modificirano za iskanje zadnje vrednosti, če za izbrano ni podatka)
            String queryTemplate =
                    "SELECT vrednost FROM tecajnice WHERE oznaka = ? AND datum<=?ORDER BY datum DESC LIMIT 1";

            try (Connection connection = DatabaseConnection.getConnection()) {
                // Prva vrednost v časovnem obdobju za prvo valuto
                BigDecimal initialCurrency1 = fetchCurrencyValue(connection, queryTemplate, currency1, historicalDate);

                // Današnja vrendnost za prvo valuto
                BigDecimal finalCurrency1 = fetchCurrencyValue(connection, queryTemplate, currency1, today);

                // Prva vrednost v časovnem obdobju za drugo valuto
                BigDecimal initialCurrency2 = fetchCurrencyValue(connection, queryTemplate, currency2, historicalDate);

                // Današnja vrednost za drugo valuto
                BigDecimal finalCurrency2 = fetchCurrencyValue(connection, queryTemplate, currency2, today);

                // Preverimo, da so vse vrednosti na voljo
                if (initialCurrency1 == null || finalCurrency1 == null || initialCurrency2 == null || finalCurrency2 == null) {
                    return "Ni podatkov za izbrane parametre. Ponovno zaženite program in poskusite znova.";
                }

                // Izračun razmerja med valutama
                BigDecimal historicalRatio = initialCurrency1.divide(initialCurrency2, 8, RoundingMode.HALF_UP);
                BigDecimal recentRatio = finalCurrency1.divide(finalCurrency2, 8, RoundingMode.HALF_UP);

                BigDecimal change = recentRatio.subtract(historicalRatio).divide(historicalRatio, 8, RoundingMode.HALF_UP);
                BigDecimal percentageChange = change.multiply(new BigDecimal("100"));

                percentageChange = percentageChange.setScale(4, RoundingMode.HALF_UP);

                // Izpis rezultata
                return String.format("%s se je glede na %s spremenil: %s%%", currency1, currency2, percentageChange.toPlainString());
            }
        } catch (SQLException e) {
            // Error handling
            e.printStackTrace();
            return "Pri pripravi podatkov je prišlo do napake. Prosim, da znova zaženete program.";
        }
    }

    private LocalDate calculateStartDate(LocalDate endDate, String timeFrame) {
        // Izračun časovnega obdobja
        switch (timeFrame) {
            case "1 teden":
                return endDate.minus(1, ChronoUnit.WEEKS);
            case "1 mesec":
                return endDate.minus(1, ChronoUnit.MONTHS);
            case "6 mesecev":
                return endDate.minus(6, ChronoUnit.MONTHS);
            case "1 leto":
                return endDate.minus(1, ChronoUnit.YEARS);
            default:
                throw new IllegalArgumentException("Težava pri izbiri časovnega obdobja.");
        }
    }
    // Metoda za pridobitev vrednosti iz baze
    private BigDecimal fetchCurrencyValue(Connection connection, String query, String currency, LocalDate date) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, currency);
            statement.setDate(2, Date.valueOf(date));

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal("vrednost");
                }
            }
        }
        return null;
    }
}

