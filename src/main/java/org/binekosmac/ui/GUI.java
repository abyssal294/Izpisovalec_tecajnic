package org.binekosmac.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.binekosmac.database.DatabaseConnection;
import org.binekosmac.database.DatabaseSaver;
import org.binekosmac.database.DatabaseSetup;
import org.binekosmac.model.DtecBS;
import org.binekosmac.utils.XmlDeserializer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        VBox rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(10));

        Label titleLabel = new Label("Prikazovalec tečajev");
        rootLayout.getChildren().add(titleLabel);

        HBox dateLayout = new HBox(10);
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        dateLayout.getChildren().addAll(new Label("Od:"), startDatePicker, new Label("Do:"), endDatePicker);
        rootLayout.getChildren().add(dateLayout);

        ListView<String> currencyListView = new ListView<>();  // Will be populated with currencies later
        currencyListView.setPrefHeight(150);
        rootLayout.getChildren().add(currencyListView);
        currencyListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> currencies = Arrays.asList("Ameriški dolar (USD)", "Japonski jen - JPY)", "Bolgarski lev - BGN", "Češka krona - CZK", "Danska krona - DKK", "Britanski funt - GBP", "Madžarski forint - HUF", "Poljski zlot - PLN", "Romunski lev - RON", "Švedska krona - SEK",
                "Islandska krona - ISK", "Švicarski frank - CHF", "Norveška krona - NOK", "Turška lira - TRY", "Avstralski dolar - AUD", "Kanadski dolar - CAD", "Mehiški peso - MXN (z 2.1.2008)", "Kitajski juan renminbi - CNY", "Hongkonški dolar - HKD", "Indijska rupija - INR (z 2.1.2009)", "Indonezijska rupija - IDR", "Izraelski šakel - ILS", "Južnokorejski von - KRW",
                "Malezijski ringit - MYR", "Novozelandski dolar - NZD", "Filipinski peso - PHP", "Singapurski dolar - SGD", "Tajski baht - THB", "Južnoafriški rand - ZAR", "Ciprski funt - CYP (nadomestil evro 1.1.2008)", "Slovaška krona - SKK (nadomestil evro 1.1.2009)", "Ruski rubelj - RUB (ni podatkov po 1.3.2022)",
                "Malteška lira - MTL (nadomestil evro 1.1.2008)", "Latvijski lats - LVL(nadomestil evro 1.1.2014)", "Litvanski litas - LTL (nadomestil evro 1.1.2015)", "Hrvaška kuna - HRK (nadomestil evro 1.1.2023)", "Estonska krona - EEK (nadomestil evro 1.1.2011)");
        currencyListView.getItems().addAll(currencies);

        Button prikazButton = new Button("Prikaz");
        rootLayout.getChildren().add(prikazButton);



        VBox rightLayout = new VBox(10);
        rightLayout.getChildren().add(new Label("Izračun oportunitetnih zaslužkov/izgub"));
        ComboBox<String> currencyDropdown1 = new ComboBox<>();
        ComboBox<String> currencyDropdown2 = new ComboBox<>();
        rightLayout.getChildren().addAll(currencyDropdown1, currencyDropdown2);

        ComboBox<String> timeRangeDropdown = new ComboBox<>();
        timeRangeDropdown.getItems().addAll("1 dan", "1 teden", "1 mesec", "6 mesecev", "1 leto");
        rightLayout.getChildren().add(timeRangeDropdown);

        Label resultLabel = new Label("Rezultat tukaj");
        rightLayout.getChildren().add(resultLabel);

        HBox mainLayout = new HBox(20, rootLayout, rightLayout);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 800, 400);
        primaryStage.setTitle("Izpis tečajnic");
        primaryStage.setScene(scene);
        primaryStage.show();

        Label loadingLabel = new Label("Pripravljam podatke...");
        rootLayout.getChildren().add(loadingLabel);

        new Thread(() -> {
            try {
                DatabaseSetup.initialize();
                XmlDeserializer xmlDeserializer = new XmlDeserializer();
                DtecBS dtecBS = xmlDeserializer.processXML();
                DatabaseSaver dbSaver = new DatabaseSaver();
                dbSaver.save(dtecBS);

                displayData(); // TODO: na koncu odstrani!



                // If we're here, data is loaded. Update the GUI on the JavaFX Application Thread
                Platform.runLater(() -> {
                    loadingLabel.setText("Podatki so pripravljeni!"); // Indicate that data is ready
                    // You can also initiate actions that should happen after loading is complete
                    // For instance, populating the currencyListView or other UI elements
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions, e.g., show a dialog with the error, log the error, etc.
                Platform.runLater(() -> {
                    loadingLabel.setText("Napaka pri pripravi podatkov!"); // Indicate that there was an error
                });
            }
        }).start();
    }
    // TODO: na koncu odstrani!
    public static void displayData() {
        // SQL query to retrieve the first 50 records from the 'tecajnice' table
        String query = "SELECT * FROM tecajnice LIMIT 50";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String datum = rs.getString("datum");
                String oznaka = rs.getString("oznaka");
                int sifra = rs.getInt("sifra");
                double vrednost = rs.getDouble("vrednost");

                System.out.format("ID: %d, Datum: %s, Oznaka: %s, Sifra: %d, Vrednost: %f\n", id, datum, oznaka, sifra, vrednost);
            }
        } catch (Exception e) {
            System.err.println("Error querying the database: " + e.getMessage());
            // Optional: Print stack trace for debugging
            e.printStackTrace();
        }
    }
}
