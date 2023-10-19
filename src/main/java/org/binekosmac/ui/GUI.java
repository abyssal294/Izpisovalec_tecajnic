package org.binekosmac.ui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.binekosmac.database.DatabaseSaver;
import org.binekosmac.database.DatabaseSetup;
import org.binekosmac.model.DtecBS;
import org.binekosmac.utils.XmlDeserializer;

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
}
