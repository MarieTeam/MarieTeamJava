package com.example.demo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    @FXML
    private Button generatePdfButton;

    @FXML
    private void onGeneratePdfButtonClick(ActionEvent event) {
        // Obtenir la liste des bateaux à partir de la TableView
        ObservableList<Bateau> bateaux = tableView.getItems();

        try {
            // Générer le PDF
            PdfGenerator.createPdf("bateaux.pdf", bateaux);

            // Ouvrir le PDF généré (si possible)
            if (Desktop.isDesktopSupported()) {
                try {
                    File pdfFile = new File("bateaux.pdf");
                    Desktop.getDesktop().open(pdfFile);
                } catch (IOException e) {
                    System.err.println("Erreur lors de l'ouverture du PDF : " + e.getMessage());
                }
            } else {
                System.out.println("Le bureau n'est pas pris en charge, le PDF ne peut pas être ouvert automatiquement.");
            }
        } catch (FileNotFoundException e) {
            System.err.println("Erreur lors de la génération du PDF : " + e.getMessage());
        }
    }

    @FXML
    private TableView<Bateau> tableView;

    @FXML
    private TableColumn<Bateau, Integer> idColumn;

    @FXML
    private TableColumn<Bateau, String> nomColumn;

    @FXML
    public void initialize() {
        // Configurez les colonnes de la table pour utiliser les propriétés des objets Bateau
        idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());

        // Charger les données des bateaux à partir de la base de données et les ajouter à la TableView
        loadBateauData();
    }

    private void loadBateauData() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT * FROM bateau"; // Remplacez "bateaux" par le nom de votre table des bateaux
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            ObservableList<Bateau> bateaux = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                Bateau bateau = new Bateau(id, nom);
                bateaux.add(bateau);
            }

            tableView.setItems(bateaux);

            preparedStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des données des bateaux.");
            e.printStackTrace();
        }
    }
}
