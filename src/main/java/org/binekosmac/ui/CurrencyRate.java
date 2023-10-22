package org.binekosmac.ui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import java.time.LocalDate;
// Shema za prenašanje podatkov v tabelo
public class CurrencyRate {
    private final SimpleObjectProperty<LocalDate> datumZaTabelo;
    private final SimpleStringProperty oznakaZaTabelo;
    private final SimpleIntegerProperty sifraZaTabelo;
    private final SimpleDoubleProperty vrednostZaTabelo;

    public CurrencyRate(LocalDate datum, String oznaka, int sifra, double vrednost) {
        this.datumZaTabelo = new SimpleObjectProperty<>(datum);
        this.oznakaZaTabelo = new SimpleStringProperty(oznaka);
        this.sifraZaTabelo = new SimpleIntegerProperty(sifra);
        this.vrednostZaTabelo = new SimpleDoubleProperty(vrednost);
    }

    public LocalDate getDatumZaTabelo() {
        return datumZaTabelo.get();
    }

    public SimpleObjectProperty<LocalDate> datumZaTabeloProperty() {
        return datumZaTabelo;
    }

    public void setDatumZaTabelo(LocalDate datumZaTabelo) {
        this.datumZaTabelo.set(datumZaTabelo);
    }

    public String getOznakaZaTabelo() {
        return oznakaZaTabelo.get();
    }

    public SimpleStringProperty oznakaZaTabeloProperty() {
        return oznakaZaTabelo;
    }

    public void setOznakaZaTabelo(String oznakaZaTabelo) {
        this.oznakaZaTabelo.set(oznakaZaTabelo);
    }

    public int getSifraZaTabelo() {
        return sifraZaTabelo.get();
    }

    public SimpleIntegerProperty sifraZaTabeloProperty() {
        return sifraZaTabelo;
    }

    public void setSifraZaTabelo(int sifraZaTabelo) {
        this.sifraZaTabelo.set(sifraZaTabelo);
    }

    public double getVrednostZaTabelo() {
        return vrednostZaTabelo.get();
    }

    public SimpleDoubleProperty vrednostZaTabeloProperty() {
        return vrednostZaTabelo;
    }

    public void setVrednostZaTabelo(double vrednostZaTabelo) {
        this.vrednostZaTabelo.set(vrednostZaTabelo);
    }
}
