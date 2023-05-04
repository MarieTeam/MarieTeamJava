package com.example.demo;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.DoubleProperty;

public class BateauVoyageur extends Bateau {
    private final DoubleProperty vitesseBatVoy;
    private final StringProperty imageBatVoy;

    public BateauVoyageur(int id, String nom, double longueurBat, double largeurBat, double vitesseBatVoy, String imageBatVoy) {
        super(id, nom, longueurBat, largeurBat);
        this.vitesseBatVoy = new SimpleDoubleProperty(vitesseBatVoy);
        this.imageBatVoy = new SimpleStringProperty(imageBatVoy);
    }
    public double getVitesseBatVoy() {
        return vitesseBatVoy.get();
    }

    public DoubleProperty vitesseBatVoyProperty() {
        return vitesseBatVoy;
    }

    public String getImageBatVoy() {
        return imageBatVoy.get();
    }

    public StringProperty imageBatVoyProperty() {
        return imageBatVoy;
    }
}

