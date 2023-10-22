package org.binekosmac.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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


import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class GUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Osnova za okno
        VBox rootLayout = new VBox(10);
        rootLayout.setAlignment(Pos.CENTER);
        rootLayout.setPadding(new Insets(5));

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
        List<String> currencies = Arrays.asList("Ameriški dolar - USD", "Japonski jen - JPY", "Bolgarski lev - BGN",
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
        TableColumn<CurrencyRate, LocalDate> datumColumn = new TableColumn<>("Datum (leto-mesec-dan)");
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
            // Izbrane valute
            ObservableList<String> selectedItems = currencyListView.getSelectionModel().getSelectedItems();
            List<String> selectedCurrencies = new ArrayList<>();
            // Pridobivanje oznake izbranih valut
            for (String item : selectedItems) {
                String abbreviation = item.substring(item.length() - 3);
                selectedCurrencies.add(abbreviation);
            }
            // Procesiranje in prikazovanje podatkov v tabelo
            DataProcessor dataProcessor = new DataProcessor();
            try {
                List<CurrencyRate> rates = dataProcessor.retrieveData(startDate, endDate, selectedCurrencies);
                ObservableList<CurrencyRate> observableRates = FXCollections.observableArrayList(rates);
                currencyTable.setItems(observableRates);

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });

        // Dodatna funkcionalnost: izračun oportuninetnih zaslužkov/izgub
        VBox rightLayout = new VBox(10);
        rightLayout.getChildren().add(new Label("Izračun oportunitetnih zaslužkov/izgub"));
        List<String> forexCurrencies = Arrays.asList("EUR", "USD", "JPY", "BGN", "CZK", "DKK", "GBP", "HUF",
                "PLN", "RON", "SEK", "ISK", "CHF", "NOK", "TRY",
                "AUD", "BRL", "CAD", "CNY", "HKD", "IDR", "ILS",
                "INR", "KRW", "MXN", "MYR", "NZD", "PHP", "SGD",
                "THB", "ZAR");

        Label currency1Label = new Label("Izberite željeno valuto:");
        Label currency2Label = new Label("Izberite drugo valuto za primerjavo:");

        ComboBox<String> currencyDropdown1 = new ComboBox<>();
        currencyDropdown1.getItems().addAll(forexCurrencies);
        currencyDropdown1.getSelectionModel().selectFirst();

        ComboBox<String> currencyDropdown2 = new ComboBox<>();
        currencyDropdown2.getItems().addAll(forexCurrencies);
        currencyDropdown2.getSelectionModel().select(1);

        rightLayout.getChildren().addAll(currency1Label, currencyDropdown1, currency2Label, currencyDropdown2);

        Label timeRangeLabel = new Label("Izberite časovno obdobje:");
        ComboBox<String> timeRangeDropdown = new ComboBox<>();
        timeRangeDropdown.getItems().addAll("1 teden", "1 mesec", "6 mesecev", "1 leto");

        rightLayout.getChildren().addAll(timeRangeLabel, timeRangeDropdown);

        Button calculateButton = new Button("Izračunaj");

        Label resultLabel = new Label("Tukaj se bo izpisal izračun");
        rightLayout.getChildren().addAll(calculateButton, resultLabel);


        calculateButton.setOnAction(event -> {
            String selectedCurrency1 = currencyDropdown1.getSelectionModel().getSelectedItem();
            String selectedCurrency2 = currencyDropdown2.getSelectionModel().getSelectedItem();
            String selectedTimeFrame = timeRangeDropdown.getSelectionModel().getSelectedItem();

            // Check if all selections are made
            if (selectedCurrency1 != null && selectedCurrency2 != null && selectedTimeFrame != null) {
                DataProcessor dataProcessor = new DataProcessor();
                String result = dataProcessor.calculateForexData(selectedCurrency1, selectedCurrency2, selectedTimeFrame);
                resultLabel.setText(result);
            } else {
                resultLabel.setText("Izberite časovno obdobje.");
            }
        });

        //Nastavitev za layout
        HBox mainLayout = new HBox(20, rootLayout, rightLayout);
        mainLayout.setPadding(new Insets(10));

        Scene scene = new Scene(mainLayout, 1000, 600);
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

                Platform.runLater(() -> {
                    loadingLabel.setText("Podatki so pripravljeni!");
                });

            } catch (Exception e) {
                e.printStackTrace();
                // Handle exceptions, e.g., show a dialog with the error, log the error, etc.
                Platform.runLater(() -> {
                    loadingLabel.setText("Napaka pri pripravi podatkov! Ponovno zaženite program.");
                });
            }
        }).start();
    }
}
