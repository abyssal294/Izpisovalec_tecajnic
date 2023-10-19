package org.binekosmac.model;

import javax.xml.bind.annotation.*;
import java.util.List;

// @XmlRootElement(name = "tecajnica")
// @XmlAccessorType(XmlAccessType.FIELD)
public class DailyRates {

    @XmlAttribute(name = "datum")
    private String date;

    @XmlElement(name = "tecaj")
    private List<ExchangeRate> exchangeRates;

    public DailyRates(String date, List<ExchangeRate> exchangeRates) {
        this.date = date;
        this.exchangeRates = exchangeRates;
    }

    public DailyRates() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    public void setExchangeRates(List<ExchangeRate> exchangeRates) {
        this.exchangeRates = exchangeRates;
    }

}
