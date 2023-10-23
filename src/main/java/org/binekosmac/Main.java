package org.binekosmac;

import javafx.application.Application;
import org.binekosmac.ui.CurrencyRate;
import org.binekosmac.ui.DataProcessor;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
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
import java.util.*;


public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Osnova za levo okno
        VBox rootLayout = new VBox(15);
        rootLayout.setAlignment(Pos.TOP_CENTER);
        rootLayout.setPadding(new Insets(5));

        Separator verticalSeparator = new Separator();
        verticalSeparator.setOrientation(Orientation.VERTICAL);
        verticalSeparator.setStyle("-fx-background-color: #000000;");

        // Naslov levega okna
        Label titleLabel = new Label("Prikaz tečajnic za evro");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        rootLayout.getChildren().add(titleLabel);

        // Izbor datuma
        HBox dateLayout = new HBox(10);
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();
        dateLayout.getChildren().addAll(new Label("Od:"), startDatePicker, new Label("Do:"), endDatePicker);
        rootLayout.getChildren().add(dateLayout);

        // Izbira valut z informacijami o datumih, ko so podatki na voljo
        Label currencyPickingInstructions = new Label("Izberite valute (za izbiro več valut držite CTRL (Windows) ali COMMAND (Mac): ");
        rootLayout.getChildren().add(currencyPickingInstructions);
        // Statični vnos vseh valut, ki so bile ali so na voljo od 1.1.2007 ali kasneje
        ListView<String> currencyListView = new ListView<>();
        currencyListView.setMinHeight(150);
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
        currencyListView.setMinHeight(75);

        // Gumb za prikaz
        Button processButton = new Button("Prikaz");
        rootLayout.getChildren().add(processButton);

        // Hbox za tabelo in graf
        HBox hbox = new HBox();

        // Tabela
        TableView<CurrencyRate> currencyTable = new TableView<>();
        TableColumn<CurrencyRate, LocalDate> datumColumn = new TableColumn<>("Datum (L-M-D)");
        TableColumn<CurrencyRate, String> oznakaColumn = new TableColumn<>("Oznaka");
        TableColumn<CurrencyRate, Integer> sifraColumn = new TableColumn<>("Šifra valute");
        TableColumn<CurrencyRate, Double> vrednostColumn = new TableColumn<>("Tečaj");

        datumColumn.setCellValueFactory(new PropertyValueFactory<>("datumZaTabelo"));
        oznakaColumn.setCellValueFactory(new PropertyValueFactory<>("oznakaZaTabelo"));
        sifraColumn.setCellValueFactory(new PropertyValueFactory<>("sifraZaTabelo"));
        vrednostColumn.setCellValueFactory(new PropertyValueFactory<>("vrednostZaTabelo"));

        currencyTable.getColumns().add(datumColumn);
        currencyTable.getColumns().add(oznakaColumn);
        currencyTable.getColumns().add(sifraColumn);
        currencyTable.getColumns().add(vrednostColumn);

        currencyTable.setMinWidth(400);
        currencyTable.setMaxHeight(500);
        currencyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Graf
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Datum");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setForceZeroInRange(false);
        yAxis.setLabel("Tečaj");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Vrednosti tečajev v izbranem času");
        lineChart.setMinSize(550, 550);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Valuta");

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
                return;
            }

            // Izbrane valute
            ObservableList<String> selectedItems = currencyListView.getSelectionModel().getSelectedItems();
            List<String> selectedCurrencies = new ArrayList<>();

            // Error handling za valute
            if (selectedItems.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Napaka");
                alert.setHeaderText("Prosimo, izberite vsaj eno valuto iz seznama");
                alert.setContentText("Za izbiro več valut držite CTRL (Windows) ali COMMAND (Mac) in klikajte z levo miškino tipko," +
                        " ali pa držite SHIFT za izbiro več zaporednih valut.");
                alert.showAndWait();
                return;
            }

            // Pridobivanje oznake izbranih valut
            for (String item : selectedItems) {
                String abbreviation = item.substring(item.length() - 3);
                selectedCurrencies.add(abbreviation);
            }
            // Procesiranje in prikazovanje podatkov v tabelo
            DataProcessor dataProcessor = new DataProcessor();
            try {
                // Podatki za tabeloin graf
                List<CurrencyRate> rates = dataProcessor.retrieveData(startDate, endDate, selectedCurrencies);
                ObservableList<CurrencyRate> observableRates = FXCollections.observableArrayList(rates);
                currencyTable.setItems(observableRates);

                // Brisanje predhodnega grafa
                lineChart.getData().clear();

                // Podatki za graf
                // Mapa za shranjevanje podatkov za posamezno valuto
                Map<String, XYChart.Series<String, Number>> seriesMap = new HashMap<>();

                // Serija za posamezne valute
                for(String currency : selectedCurrencies) {
                    XYChart.Series<String, Number> currentSeries = new XYChart.Series<>();
                    currentSeries.setName(currency);
                    seriesMap.put(currency, currentSeries);
                }
                // Vstavljanje podatkov v serije
                for (CurrencyRate rate : rates) {
                    String dateAsString = rate.getDatumZaTabelo().toString(); // Or format as needed
                    Number value = rate.getVrednostZaTabelo();
                    String currency = rate.getOznakaZaTabelo();

                    XYChart.Series<String, Number> currentSeries = seriesMap.get(currency);
                    if (currentSeries != null) {
                        currentSeries.getData().add(new XYChart.Data<>(dateAsString, value));
                    }
                }

                // Dodajanje serij v graf
                for (XYChart.Series<String, Number> currencySeries : seriesMap.values()) {
                    lineChart.getData().add(currencySeries);
                }

                // Vzpostavitev tabele in grafa v okno
                hbox.getChildren().addAll(currencyTable, lineChart);
                rootLayout.getChildren().add(hbox);
                primaryStage.setMaximized(true);

            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        });


        // Dodatna funkcionalnost: izračun oportuninetnih zaslužkov/izgub
        VBox rightLayout = new VBox(10);
        Label forexLabel = new Label("Izračun oportunitetnih zaslužkov/izgub");
        forexLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
        rightLayout.getChildren().add(forexLabel);
        // Seznam valut, katerih podatki so na voljo v zadnjem letu dni
        List<String> forexCurrencies = Arrays.asList("EUR", "USD", "JPY", "BGN", "CZK", "DKK", "GBP", "HUF",
                "PLN", "RON", "SEK", "ISK", "CHF", "NOK", "TRY",
                "AUD", "BRL", "CAD", "CNY", "HKD", "IDR", "ILS",
                "INR", "KRW", "MXN", "MYR", "NZD", "PHP", "SGD",
                "THB", "ZAR");
        // Napisi za dropdown menuje
        Label currency1Label = new Label("Izberite željeno valuto:");
        Label currency2Label = new Label("Izberite drugo valuto za primerjavo:");

        // Dropdown menu za 1. valuto
        ComboBox<String> currencyDropdown1 = new ComboBox<>();
        currencyDropdown1.getItems().addAll(forexCurrencies);
        currencyDropdown1.getSelectionModel().selectFirst();

        // Dropdown menu za 2. valuto
        ComboBox<String> currencyDropdown2 = new ComboBox<>();
        currencyDropdown2.getItems().addAll(forexCurrencies);
        currencyDropdown2.getSelectionModel().select(1);

        // Dropdown menu za časovno obdobje
        Label timeRangeLabel = new Label("Izberite časovno obdobje:");
        ComboBox<String> timeRangeDropdown = new ComboBox<>();
        timeRangeDropdown.getItems().addAll("1 teden", "1 mesec", "6 mesecev", "1 leto");

        // Gumb za izračun
        Button calculateButton = new Button("Izračun");

        // Izpis rezultata
        Label resultLabel = new Label();
        resultLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 30px;");

        // Opomnik za uporabo programa
        Label reminderLabel1 = new Label("Opomba: ");
        Label reminderLabel2 = new Label("Banka Slovenije objavlja referenčne");
        Label reminderLabel3 = new Label("tečaje med delovniki, po 16:15. uri.");
        Label reminderLabel4 = new Label("Ker program pridobi podatke le ob");
        Label reminderLabel5 = new Label("zagonu, priporočamo, da program");
        Label reminderLabel6 = new Label("po vsaki uporabi zaprete.");

        // Dodajanje elementov za oportuninetne zaslužke/izgube v layout
        rightLayout.getChildren().addAll(currency1Label, currencyDropdown1, currency2Label, currencyDropdown2,
                timeRangeLabel, timeRangeDropdown,calculateButton, resultLabel, reminderLabel1, reminderLabel2,
                reminderLabel3, reminderLabel4, reminderLabel5, reminderLabel6);


        // Event listener za gumb za oportunitetne zaslužke/izgube
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

        // Krovne nastavitve za layout
        HBox mainLayout = new HBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(15, 5, 15, 5));

        Scene scene = new Scene(mainLayout, 1000, 600);
        primaryStage.setTitle("Izpis tečajnic");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Loading screen
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(300, 300);
        progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        Label loadingLabel = new Label("Pripravljam podatke...");
        loadingLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 40px;");
        mainLayout.getChildren().addAll(progressIndicator, loadingLabel);

        new Thread(() -> {
            try {
                // Prenašanje XML, prepisovanje XML v Java object, shranjevanje v bazo
                DatabaseSetup.initialize();
                XmlDeserializer xmlDeserializer = new XmlDeserializer();
                DtecBS dtecBS = xmlDeserializer.processXML();
                DatabaseSaver dbSaver = new DatabaseSaver();
                dbSaver.save(dtecBS);

                Platform.runLater(() -> {
                    // Prenos iz loading screena v program
                    mainLayout.getChildren().remove(loadingLabel);
                    mainLayout.getChildren().remove(progressIndicator);
                    mainLayout.setAlignment(Pos.TOP_CENTER);
                    mainLayout.getChildren().addAll(rootLayout, verticalSeparator, rightLayout);
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> loadingLabel.setText("Napaka pri pripravi podatkov! Preverite svojo internetno povezavo in ponovno zaženite program."));
            }
        }).start();
    }
    // Samo zavoljo muh JavaFX
        public static void main(String[] args) {
            launch(args);
    }
}


