package org.binekosmac.ui;

import org.binekosmac.database.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.math.RoundingMode;
    // Del backenda za pridobivanje podatkov iz baze
public class DataProcessor {
    // Priprava podatkov iz baze za tabelo in graf
    public List<CurrencyRate> retrieveData(LocalDate startDate, LocalDate endDate, List<String> currencies) throws SQLException {
        // SQL string
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM tecajnice WHERE datum BETWEEN ? AND ? AND oznaka IN (");
        // Sprotno prilagajanje stringa
        for (int i = 0; i < currencies.size(); i++) {
            queryBuilder.append("?");
            if (i < currencies.size() - 1) {
                queryBuilder.append(",");
            }
        }
        queryBuilder.append(")");
        queryBuilder.append("ORDER BY oznaka");
        // Povezava z bazo
        Connection connection = DatabaseConnection.getConnection();
        // Priprava SQL stavka
        PreparedStatement statement = connection.prepareStatement(queryBuilder.toString());
        // Casting datuma iz LocalDate v Date
        statement.setDate(1, Date.valueOf(startDate));
        statement.setDate(2, Date.valueOf(endDate));
        // Dodajanje valut v SQL stavku
        int index = 3;
        for (String currency : currencies) {
            statement.setString(index++, currency);
        }
        // Izvedba SQL stavka in shranjevanje rezultatov
        List<CurrencyRate> rates = new ArrayList<>();
        try (ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                LocalDate datum = rs.getDate("datum").toLocalDate();
                String oznaka = rs.getString("oznaka");
                int sifra = rs.getInt("sifra");
                double vrednost = rs.getDouble("vrednost");

                rates.add(new CurrencyRate(datum, oznaka, sifra, vrednost));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Zapiranje povezave
            DatabaseConnection.closeConnection();
        }
        return rates;
    }
    // Iskanje in priprava podatkov za izračun oportunitetnih zaslužkov/izgub
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

                return String.format("%s%%", percentageChange.toPlainString());
            }
        } catch (SQLException e) {
            // Error handling
            e.printStackTrace();
            return "Pri pripravi podatkov je prišlo do napake. Prosim, da znova zaženete program.";
        } finally {
            try {
                DatabaseConnection.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // Metoda za definiranje začetnega datuma
    private LocalDate calculateStartDate(LocalDate endDate, String timeFrame) {
        return switch (timeFrame) {
            case "1 teden" -> endDate.minus(1, ChronoUnit.WEEKS);
            case "1 mesec" -> endDate.minus(1, ChronoUnit.MONTHS);
            case "6 mesecev" -> endDate.minus(6, ChronoUnit.MONTHS);
            case "1 leto" -> endDate.minus(1, ChronoUnit.YEARS);
            default -> throw new IllegalArgumentException("Težava pri izbiri časovnega obdobja.");
        };
    }
    // Metoda za pridobitev vrednosti iz baze
    private BigDecimal fetchCurrencyValue(Connection connection, String query, String currency, LocalDate date) throws SQLException {
        if("EUR".equals(currency)) {
            return BigDecimal.ONE;
        } else {
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
}

