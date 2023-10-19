package org.binekosmac.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

@JacksonXmlRootElement(namespace = "http://www.bsi.si")
public class Tecaj {
    @JacksonXmlProperty(isAttribute = true, localName = "oznaka")
    private String oznaka;
    @JacksonXmlProperty(isAttribute = true, localName = "sifra")
    private Integer sifra;
    @JacksonXmlText
    private Float vrednost;

    public Tecaj(String oznaka, Integer sifra, Float vrednost) {
        this.oznaka = oznaka;
        this.sifra = sifra;
        this.vrednost = vrednost;
    }

    public Tecaj(){}

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public Integer getSifra() {
        return sifra;
    }

    public void setSifra(Integer sifra) {
        this.sifra = sifra;
    }

    public Float getVrednost() {
        return vrednost;
    }

    public void setVrednost(Float vrednost) {
        this.vrednost = vrednost;
    }

    @Override
    public String toString() {
        return "Tecaj{" +
                "oznaka='" + oznaka + '\'' +
                ", sifra=" + sifra +
                ", vrednost=" + vrednost +
                '}';
    }
}