package org.binekosmac.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.util.List;

@JacksonXmlRootElement(localName = "DtecBS", namespace = "http://www.bsi.si")
public class DtecBS {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "tecajnica")
    private List<Tecajnica> tecajnice;

    public DtecBS() {
    }

    public DtecBS(List<Tecajnica> tecajnice) {
        this.tecajnice = tecajnice;
    }

    public List<Tecajnica> getTecajnica() {
        return tecajnice;
    }

    public void setTecajnica(List<Tecajnica> tecajnice) {
        this.tecajnice = tecajnice;
    }

    @Override
    public String toString() {
        return "DtecBS{" +
                "tecajnice=" + tecajnice +
                '}';
    }
}
