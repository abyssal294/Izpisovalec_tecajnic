package org.binekosmac.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.binekosmac.database.DatabaseSaver;
import org.binekosmac.database.DatabaseSetup;
import org.binekosmac.model.DtecBS;
import org.binekosmac.utils.XmlDeserializer;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Osnova za okno
        VBox rootLayout = new VBox(10);
        rootLayout.setPadding(new Insets(10));

        // Naslov
        Label titleLabel = new Label("Prikaz tečajnic za evro");
        rootLayout.getChildren().add(titleLabel);

        // Datuma
        HBox dateLayout = new HBox(10);
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        dateLayout.getChildren().addAll(new Label("Od:"), startDatePicker, new Label("Do:"), endDatePicker);
        rootLayout.getChildren().add(dateLayout);


        // Izbira valut z informacijami o datumih, ko so podatki na voljo
        Label currencyPickingInstructions = new Label("Izberite valute (za izbiro več valut držite CTRL (Windows) ali COMMAND (Mac): ");
        rootLayout.getChildren().add(currencyPickingInstructions);

        ListView<String> currencyListView = new ListView<>();
        currencyListView.setPrefHeight(150);
        rootLayout.getChildren().add(currencyListView);
        currencyListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        List<String> currencies = Arrays.asList("Ameriški dolar USD", "Japonski jen - JPY", "Bolgarski lev - BGN",
                "Češka krona - CZK", "Danska krona - DKK", "Britanski funt - GBP", "Madžarski forint - HUF",
                "Poljski zlot - PLN", "Romunski lev - RON", "Švedska krona - SEK",
                "Islandska krona - ISK", "Švicarski frank - CHF", "Norveška krona - NOK", "Turška lira - TRY",
                "Avstralski dolar - AUD", "Kanadski dolar - CAD", "Mehiški peso (z 2.1.2008) - MXN",
                "Kitajski juan renminbi - CNY", "Hongkonški dolar - HKD", "Indijska rupija (z 2.1.2009) - INR",
                "Indonezijska rupija - IDR", "Izraelski šakel - ILS", "Južnokorejski von - KRW",
                "Malezijski ringit - MYR", "Novozelandski dolar - NZD", "Filipinski peso - PHP",
                "Singapurski dolar - SGD", "Tajski baht - THB", "Južnoafriški rand - ZAR",
                "Ciprski funt (nadomestil evro 1.1.2008) - CYP", "Slovaška krona (nadomestil evro 1.1.2009) - SKK",
                "Ruski rubelj (ni podatkov po 1.3.2022) - RUB",
                "Malteška lira (nadomestil evro 1.1.2008) - MTL", "Latvijski lats (nadomestil evro 1.1.2014) - LVL",
                "Litvanski litas (nadomestil evro 1.1.2015) - LTL", "Hrvaška kuna (nadomestil evro 1.1.2023) - HRK",
                "Estonska krona (nadomestil evro 1.1.2011) - EEK");
        currencyListView.getItems().addAll(currencies);

        // Gumb za prikaz
        Button processButton = new Button("Prikaz");
        rootLayout.getChildren().add(processButton);

        // Tabela
        TableView<CurrencyRate> currencyTable = new TableView<>();
        TableColumn<CurrencyRate, LocalDate> datumColumn = new TableColumn<>("Datum");
        TableColumn<CurrencyRate, String> oznakaColumn = new TableColumn<>("Oznaka");
        TableColumn<CurrencyRate, Integer> sifraColumn = new TableColumn<>("Šifra valute");
        TableColumn<CurrencyRate, Double> vrednostColumn = new TableColumn<>("Tečaj");

        datumColumn.setCellValueFactory(new PropertyValueFactory<>("datumZaTabelo"));
        oznakaColumn.setCellValueFactory(new PropertyValueFactory<>("oznakaZaTabelo"));
        sifraColumn.setCellValueFactory(new PropertyValueFactory<>("sifraZaTabelo"));
        vrednostColumn.setCellValueFactory(new PropertyValueFactory<>("vrednostZaTabelo"));

        // Add columns to the table
        currencyTable.getColumns().add(datumColumn);
        currencyTable.getColumns().add(oznakaColumn);
        currencyTable.getColumns().add(sifraColumn);
        currencyTable.getColumns().add(vrednostColumn);

        currencyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        HBox hbox = new HBox(15);
        hbox.getChildren().add(currencyTable);

        rootLayout.getChildren().add(hbox);



        // Event listener za gumb za prikaz
        processButton.setOnAction(e -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            // Error handling za datume
            if (startDate == null || endDate == null || endDate.isBefore(startDate)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Napaka");
                alert.setHeaderText("Prosimo, izberite veljavna datuma");
                alert.setContentText("Datum 'od:' naj bo pred datumom 'do:' ");
                alert.showAndWait();
            }
            ObservableList<String> selectedItems = currencyListView.getSelectionModel().getSelectedItems();
            List<String> selectedCurrencies = new ArrayList<>();

            for (String item : selectedItems) {
                String abbreviation = item.substring(item.length() - 3);
                selectedCurrencies.add(abbreviation);
            }

            // Procesiranje in prikazovanje podatkov
            DataProcessor dataProcessor = new DataProcessor();
            try {
                List<CurrencyRate> rates = dataProcessor.retrieveData(startDate, endDate, selectedCurrencies);
                ObservableList<CurrencyRate> observableRates = FXCollections.observableArrayList(rates);
                currencyTable.setItems(observableRates);



            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });

        // Dodatna funkcionalnost
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

        //Nastavitev za layout
        HBox mainLayout = new HBox(20, rootLayout, rightLayout);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 800, 400);
        primaryStage.setTitle("Izpis tečajnic");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
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

                //               displayData();  TODO: na koncu odstrani!


                Platform.runLater(() -> {
                    loadingLabel.setText("Podatki so pripravljeni!");
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions, e.g., show a dialog with the error, log the error, etc.
                Platform.runLater(() -> {
                    loadingLabel.setText("Napaka pri pripravi podatkov!");
                });
            }
        }).start();
    }
}
//    // TODO: na koncu odstrani!
//    public static void displayData() {
//        // SQL query to retrieve the first 50 records from the 'tecajnice' table
//        String query = "SELECT * FROM tecajnice LIMIT 5";
//
//        try (Connection connection = DatabaseConnection.getConnection();
//             Statement stmt = connection.createStatement();
//             ResultSet rs = stmt.executeQuery(query)) {
//
//            while (rs.next()) {
//                int id = rs.getInt("id");
//                String datum = rs.getString("datum");
//                String oznaka = rs.getString("oznaka");
//                int sifra = rs.getInt("sifra");
//                double vrednost = rs.getDouble("vrednost");
//
//                System.out.format("ID: %d, Datum: %s, Oznaka: %s, Sifra: %d, Vrednost: %f\n", id, datum, oznaka, sifra, vrednost);
//            }
//        } catch (Exception e) {
//            System.err.println("Error querying the database: " + e.getMessage());
//            // Optional: Print stack trace for debugging
//            e.printStackTrace();
//        }
//    }
//}
