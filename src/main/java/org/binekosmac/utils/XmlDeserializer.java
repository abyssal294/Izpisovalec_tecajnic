package org.binekosmac.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.binekosmac.model.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
            xmlMapper.registerModule(new JavaTimeModule());

            xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            xmlMapper.enable(DeserializationFeature.WRAP_EXCEPTIONS);
            xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

            return xmlMapper.readValue(xmlData, DtecBS.class);

    }
}
