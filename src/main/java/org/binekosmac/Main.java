package org.binekosmac;

import org.binekosmac.database.*;
import org.binekosmac.utils.*;
import org.binekosmac.model.ExchangeRateList;

import java.io.InputStream;

public class Main {

    public static void main(String[] args) {
        try {
            // Initialize database
            DatabaseSetup.initialize();

            XMLProcessor xmlProcessor = new XMLProcessor();
            ExchangeRateList exchangeRateList = xmlProcessor.processXML();

            DatabaseSaver databaseSaver = new DatabaseSaver();
            databaseSaver.saveExchangeRateList(exchangeRateList);

            System.out.println("Successfully stored the XML data into the database.");


        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
