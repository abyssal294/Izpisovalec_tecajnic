package org.binekosmac;

import org.binekosmac.database.*;
import org.binekosmac.model.*;
import org.binekosmac.utils.XmlDeserializer;


public class Main {
    public static void main(String[] args) {
        try {
            DatabaseSetup.initialize();

            XmlDeserializer xmlDeserializer = new XmlDeserializer();
            DtecBS dtecBS = xmlDeserializer.processXML();

            //System.out.println("Parsed object: " + dtecBS);

               DatabaseSaver dbSaver = new DatabaseSaver();
               dbSaver.save(dtecBS);

            DatabaseConnection.closeConnection();

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("An error occured: " +e.getMessage());
            }
    }
}
