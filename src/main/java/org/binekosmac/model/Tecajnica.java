package org.binekosmac.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.time.LocalDate;
import java.util.*;

public class Tecajnica {
    @JacksonXmlProperty(isAttribute = true)
    private LocalDate datum;
@JacksonXmlElementWrapper(useWrapping = false)
@JacksonXmlProperty(localName = "tecaj")
    private List<Tecaj> tecaj;

    public Tecajnica(LocalDate datum, List<Tecaj> tecaj) {
        this.datum = datum;
        this.tecaj = tecaj;
    }

    public Tecajnica(){}

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public List<Tecaj> getTecaj() {
        return tecaj;
    }

    public void setTecaj(List<Tecaj> tecaj) {
        this.tecaj = tecaj;
    }

    @Override
    public String toString() {
        return "Tecajnica{" +
                "datum=" + datum +
                ", tecaj=" + tecaj +
                '}';
    }
}