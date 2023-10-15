package org.binekosmac.model;

import javax.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "tecajnica")
@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeRateList {

    @XmlAttribute(name = "datum")
    private String date;

    @XmlElement(name = "tecaj")
    private List<ExchangeRate> exchangeRates;

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

    // ExchangeRate class nested inside ExchangeRateList
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ExchangeRate {

        @XmlAttribute(name = "oznaka")
        private String acronym;

        @XmlAttribute(name = "sifra")
        private int code;

        @XmlValue
        private double rate;

        public String getAcronym() {
            return acronym;
        }

        public void setAcronym(String acronym) {
            this.acronym = acronym;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public double getRate() {
            return rate;
        }

        public void setRate(double rate) {
            this.rate = rate;
        }
    }
}
