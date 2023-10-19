package org.binekosmac.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

@XmlAccessorType(XmlAccessType.FIELD)
public class ExchangeRate {

    @XmlAttribute(name = "oznaka")
    private String acronym;

    @XmlAttribute(name = "sifra")
    private Integer code;

    @XmlValue
    private Double rate;

    public ExchangeRate(String acronym, Integer code, Double rate) {
        this.acronym = acronym;
        this.code = code;
        this.rate = rate;
    }

    public ExchangeRate() {
    }

    public String getAcronym() {
        return acronym;
    }

    public void setAcronym(String acronym) {
        this.acronym = acronym;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}