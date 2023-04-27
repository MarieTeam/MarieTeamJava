package com.example.demo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Categorie {
    private final StringProperty libelle;
    private final IntegerProperty capaciteMax;

    public Categorie(String libelle, int capaciteMax) {
        this.libelle = new SimpleStringProperty(libelle);
        this.capaciteMax = new SimpleIntegerProperty(capaciteMax);
    }

    public String getLibelle() {
        return libelle.get();
    }

    public StringProperty libelleProperty() {
        return libelle;
    }

    public int getCapaciteMax() {
        return capaciteMax.get();
    }

    public IntegerProperty capaciteMaxProperty() {
        return capaciteMax;
    }
}
