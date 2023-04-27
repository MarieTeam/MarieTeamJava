package com.example.demo;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.ArrayList;
import java.util.List;

public class Bateau {
    private final IntegerProperty id;
    private final StringProperty nom;
    private final StringProperty libelle;
    private final IntegerProperty capaciteMax;
    private final List<Categorie> categories;

    public Bateau(int id, String nom) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.libelle = new SimpleStringProperty();
        this.capaciteMax = new SimpleIntegerProperty();
        this.categories = new ArrayList<>();
    }

    public Bateau(int id, String nom, String libelle, int capaciteMax) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.libelle = new SimpleStringProperty(libelle);
        this.capaciteMax = new SimpleIntegerProperty(capaciteMax);
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

    public List<Categorie> getCategories() {
        return categories;
    }

    public void addCategorie(Categorie categorie) {
        categories.add(categorie);
    }
}
