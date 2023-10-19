package org.binekosmac.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.binekosmac.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class XmlDeserializer {

    private static final String XML_SOURCE_URL = "https://www.bsi.si/_data/tecajnice/dtecbs-l.xml";

    public DtecBS processXML () throws IOException {
        String xmlData = fetchXML();
        return deserializeXml(xmlData);
    }

    private String fetchXML() throws IOException {
        URL url = new URL(XML_SOURCE_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = reader.readLine()) != null) {
                response.append(inputLine);
            }

            reader.close();
            connection.disconnect();

//            System.out.println("Fetched XML data: " + response.toString());

            return response.toString();

        } else {
            throw new RuntimeException("Failed to fetch XML by HTTP response code: " + responseCode);
        }
    }
        private DtecBS deserializeXml(String xmlData) throws IOException {
            XmlMapper xmlMapper = new XmlMapper();

            JavaTimeModule javaTimeModule = new JavaTimeModule();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(formatter));
            xmlMapper.registerModule(javaTimeModule);

            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            return xmlMapper.readValue(xmlData, DtecBS.class);

    }
}
