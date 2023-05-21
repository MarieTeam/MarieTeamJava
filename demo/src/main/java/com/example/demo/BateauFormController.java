package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BateauFormController {
    @FXML
    private TextField nomField, longueurBatField, largeurBatField, vitesseBatVoyField, newEquipementField;
    @FXML
    private ListView<Equipement> equipementList;
    @FXML
    private ComboBox<Equipement> equipementAvailableList;

    private ObservableList<Equipement> availableEquipements;
    private BateauVoyageur currentBateau;

    public void setBateau(BateauVoyageur bateau) {
        currentBateau = bateau;
        nomField.setText(bateau.getNom());
        longueurBatField.setText(String.valueOf(bateau.getLongueurBat()));
        largeurBatField.setText(String.valueOf(bateau.getLargeurBat()));
        vitesseBatVoyField.setText(String.valueOf(bateau.getVitesseBatVoy()));

        equipementList.setItems(currentBateau.getEquipements());
    }

    @FXML
    public void initialize() {
        availableEquipements = FXCollections.observableArrayList(loadAvailableEquipements());
        equipementAvailableList.setItems(availableEquipements);
    }

    private List<Equipement> loadAvailableEquipements() {
        List<Equipement> availableEquipements = new ArrayList<>();
        String sql = "SELECT DISTINCT libEquip FROM Equipement";
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String equipName = resultSet.getString("libEquip");
                Equipement equipement = new Equipement(equipName, 0);
                availableEquipements.add(equipement);
            }
        } catch (SQLException e) {
            System.out.println("Error loading available equipements: " + e.getMessage());
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return availableEquipements;
    }

    @FXML
    public void onAddButtonClick() {
        Equipement selectedEquipement = equipementAvailableList.getSelectionModel().getSelectedItem();
        if (selectedEquipement != null) {
            currentBateau.getEquipements().add(selectedEquipement);
            availableEquipements.remove(selectedEquipement);
        }
    }

    @FXML
    public void onRemoveButtonClick() {
        Equipement selectedEquipement = equipementList.getSelectionModel().getSelectedItem();
        if (selectedEquipement != null) {
            availableEquipements.add(selectedEquipement);
            currentBateau.getEquipements().remove(selectedEquipement);
        }
    }

    public void saveBateau(BateauVoyageur bateau) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        try {
            connection.setAutoCommit(false); // Disable auto-commit

            String sqlBateau = "UPDATE Bateau SET nom = ?, longueurBat = ?, largeurBat = ? WHERE id = ?";
            try (PreparedStatement statementBateau = connection.prepareStatement(sqlBateau)) {
                statementBateau.setString(1, bateau.getNom());
                statementBateau.setDouble(2, bateau.getLongueurBat());
                statementBateau.setDouble(3, bateau.getLargeurBat());
                statementBateau.setInt(4, bateau.getId());
                statementBateau.executeUpdate();
            }

            String sqlBateauVoyageur = "UPDATE bateauVoyageur SET vitesseBatVoy = ? WHERE id = ?";
            try (PreparedStatement statementBateauVoyageur = connection.prepareStatement(sqlBateauVoyageur)) {
                statementBateauVoyageur.setDouble(1, bateau.getVitesseBatVoy());
                statementBateauVoyageur.setInt(2, bateau.getId());
                statementBateauVoyageur.executeUpdate();
            }
            String sqlDeleteEquipement = "DELETE FROM Equipement WHERE idBatVoy = ?";
            try (PreparedStatement statementDeleteEquipement = connection.prepareStatement(sqlDeleteEquipement)) {
                statementDeleteEquipement.setInt(1, bateau.getId());
                statementDeleteEquipement.executeUpdate();
            }

            for (Equipement equipement : bateau.getEquipements()) {
                String sqlInsertEquipement = "INSERT INTO Equipement (libEquip, idBatVoy) VALUES (?, ?)";
                try (PreparedStatement statementInsertEquipement = connection.prepareStatement(sqlInsertEquipement)) {
                    statementInsertEquipement.setString(1, equipement.getLibEquip());
                    statementInsertEquipement.setInt(2, bateau.getId());
                    statementInsertEquipement.executeUpdate();
                }
            }


            connection.commit(); // Commit the changes
        } catch (SQLException e) {
            connection.rollback(); // Roll back the changes in case of an error
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true); // Re-enable auto-commit
            }
        }
    }



    @FXML
    public void onSaveButtonClick() {
        if (currentBateau != null) {
            currentBateau.setNom(nomField.getText());
            currentBateau.setLongueurBat(Double.parseDouble(longueurBatField.getText()));
            currentBateau.setLargeurBat(Double.parseDouble(largeurBatField.getText()));
            currentBateau.setVitesseBatVoy(Double.parseDouble(vitesseBatVoyField.getText()));

            try {
                saveBateau(currentBateau);
            } catch (SQLException e) {
                System.out.println("Error updating Bateau: " + e.getMessage());
            }
        }
    }

    @FXML
    public void onAddNewEquipementButtonClick() {
        String equipementName = newEquipementField.getText();
        if (!equipementName.isEmpty()) {
            Equipement newEquipement = new Equipement(equipementName, 0);
            availableEquipements.add(newEquipement);
            newEquipementField.clear();
        }
    }
}