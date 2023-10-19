package org.binekosmac.database;

import org.binekosmac.model.DtecBS;
import org.binekosmac.model.Tecaj;
import org.binekosmac.model.Tecajnica;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
public class DatabaseSaver {

    private static final String INSERT_TECAJNICE_SQL =
            "INSERT INTO tecajnice (datum, oznaka, sifra, vrednost) VALUES (?, ?, ?, ?)";

    public void save(DtecBS dtecBS) throws Exception {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement pstmt = connection.prepareStatement(INSERT_TECAJNICE_SQL)) {

            for (Tecajnica tecajnica : dtecBS.getTecajnica()) {
                LocalDate dateForSql = tecajnica.getDatum();

                for (Tecaj rate : tecajnica.getTecaj()) {
                    pstmt.setObject(1, dateForSql);
                    pstmt.setString(2, rate.getOznaka());
                    pstmt.setInt(3, rate.getSifra());
                    pstmt.setDouble(4, rate.getVrednost());

                    pstmt.addBatch();
                }
            }
            pstmt.executeBatch();
            System.out.println("Data saved successfully.");
        } catch (SQLException e) {
            System.err.println("Error saving exchange rate data to database: " + e.getMessage());
            throw new RuntimeException("Error saving data to database", e);
        }
    }
}
