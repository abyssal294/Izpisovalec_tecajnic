package org.binekosmac;

import org.binekosmac.database.DatabaseConnection;
import org.binekosmac.model.DailyRatesWrapper;
import org.binekosmac.database.*;
import org.binekosmac.utils.XMLProcessor;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection.getConnection();

            // Create an instance of XMLProcessor
            XMLProcessor xmlProcessor = new XMLProcessor();

            // Process the XML and retrieve the data
            DailyRatesWrapper dailyRatesWrapper = xmlProcessor.processXML
                    (); // This method should now return DailyRatesWrapper



                // Save the data to the database
                DatabaseSaver dbSaver = new DatabaseSaver(); // Ensure you have a constructor or a static method for connection initialization
                dbSaver.saveDailyRates(dailyRatesWrapper); // You may need to adjust the saveDailyRates method depending on how you want to save multiple records


        } catch (Exception e) {
            // Log and handle exceptions appropriately - Don't leave this block empty.
            e.printStackTrace(); // For simplicity, we're just printing the stack trace. Consider using logging software.
        }
    }
}
