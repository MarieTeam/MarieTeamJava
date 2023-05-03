package com.example.demo;

import javafx.beans.property.*;

public class Categorie {
    private final DoubleProperty longueurBat;
    private final DoubleProperty largeurBat;


    public Categorie(double largeurBat, double longueurBat) {
        this.longueurBat = new SimpleDoubleProperty(longueurBat);
        this.largeurBat = new SimpleDoubleProperty(largeurBat);
    }

    public Double getLongueurBat() { return longueurBat.get(); }
    public DoubleProperty longueurBatProperty() {
        return longueurBat;
    }
    public Double getLargeurBat() { return largeurBat.get(); }
    public DoubleProperty largeurBatProperty() {
        return largeurBat;
    }


}
