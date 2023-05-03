package com.example.demo;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Bateau {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final DoubleProperty longueurBat;
    private final DoubleProperty largeurBat;
    private final List<Categorie> categories;

    public Bateau(int id, String nom) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.largeurBat = new SimpleDoubleProperty();
        this.longueurBat = new SimpleDoubleProperty();
        this.categories = new ArrayList<>();
    }

    public Bateau(int id, String nom, double longueurBat, double largeurBat) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.longueurBat = new SimpleDoubleProperty(longueurBat);
        this.largeurBat = new SimpleDoubleProperty(largeurBat);
        this.categories = new ArrayList<>();
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public double getLongueurBat() {
        return longueurBat.get();
    }

    public DoubleProperty longueurBatProperty() {
        return longueurBat;
    }

    public double getLargeurBat() {
        return largeurBat.get();
    }

    public DoubleProperty largeurBatProperty() {
        return largeurBat;
    }

    public List<Categorie> getCategories() {
        return categories;
    }

    public void addCategorie(Categorie categorie) {
        categories.add(categorie);
    }
}
