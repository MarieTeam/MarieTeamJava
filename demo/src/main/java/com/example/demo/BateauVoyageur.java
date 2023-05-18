package com.example.demo;

import javafx.beans.property.*;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.FXCollections;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

    public class BateauVoyageur extends Bateau {
        private final DoubleProperty vitesseBatVoy;
        private final StringProperty imageBatVoy;
        private final ObservableList<Equipement> equipements;
        private final StringProperty equipementsProperty;
        private final SimpleBooleanProperty showAllEquipments;

        public BateauVoyageur(int id, String nom, double longueurBat, double largeurBat, double vitesseBatVoy, String imageBatVoy) {
            super(id, nom, longueurBat, largeurBat);
            this.vitesseBatVoy = new SimpleDoubleProperty(vitesseBatVoy);
            this.imageBatVoy = new SimpleStringProperty(imageBatVoy);
            this.equipements = FXCollections.observableArrayList();
            this.equipementsProperty = new SimpleStringProperty();

            // Update the equipementsProperty whenever the equipements list changes
            this.equipements.addListener((ListChangeListener.Change<? extends Equipement> change) -> {
                String equipementsString = equipements.stream().map(Equipement::getLibEquip).collect(Collectors.joining("\n"));
                this.equipementsProperty.set(equipementsString);
            });

            this.showAllEquipments = new SimpleBooleanProperty(this, "showAllEquipments", false);
        }

        public ObservableList<Equipement> getEquipements() {
            return equipements;
        }

        public void addEquipement(Equipement equipement) {
            equipements.add(equipement);
        }

        public StringProperty equipementsProperty() {
            return equipementsProperty;
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

        public SimpleBooleanProperty showAllEquipmentsProperty() {
            return showAllEquipments;
        }

        public boolean getShowAllEquipments() {
            return showAllEquipments.get();
        }

        public void setShowAllEquipments(boolean showAllEquipments) {
            this.showAllEquipments.set(showAllEquipments);
        }
    }


