package org.binekosmac.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;

import java.time.LocalDate;
import java.util.*;

@JacksonXmlRootElement(namespace = "http://www.bsi.si")
public class Tecajnica {
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JacksonXmlProperty(isAttribute = true, localName = "datum")
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