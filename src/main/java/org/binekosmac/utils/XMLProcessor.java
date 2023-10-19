package org.binekosmac.utils;

import org.binekosmac.model.DailyRates;
import org.binekosmac.model.DailyRatesWrapper;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class XMLProcessor {

    private static final String XML_SOURCE_URL = "https://www.bsi.si/_data/tecajnice/dtecbs-l.xml";

    public DailyRatesWrapper processXML() throws Exception {
        String xmlData = fetchXML();
        return unmarshalXML(xmlData);
    }

    private String fetchXML() throws Exception {
        URL url = new URL(XML_SOURCE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return response.toString();
        } else {
            throw new RuntimeException("Failed to fetch XML: Server returned HTTP response code: " + responseCode);
        }
    }

    private DailyRatesWrapper parseXmlString(String xmlData) {
        //JAXBContext jaxbContext = JAXBContext.newInstance(DailyRatesWrapper.class);
        // Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        // StringReader reader = new StringReader(xmlData);
        // return (DailyRatesWrapper) unmarshaller.unmarshal(reader);
        DailyRatesWrapper dailyRatesWrapper = JAXB.unmarshal(new StringReader(xmlData), DailyRatesWrapper.class);
        return dailyRatesWrapper;
    }
}
