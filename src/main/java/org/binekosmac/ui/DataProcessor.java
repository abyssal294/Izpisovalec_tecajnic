package org.binekosmac.ui;

import java.time.LocalDate;
import java.util.List;

public class DataProcessor {
    public void processData(LocalDate startDate, LocalDate endDate, List<String> currencies) {
        System.out.println("Processing data from " + startDate + " to " + endDate + " for currencies: " + currencies);
    }
}
