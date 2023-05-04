package com.example.demo;

import com.example.demo.Bateau;
import com.example.demo.BateauVoyageur;
import com.example.demo.DatabaseConnection;
import com.example.demo.PdfGenerator;
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
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import static com.example.demo.DatabaseConnection.*;

public class HelloController {
    @FXML
    private Button generatePdfButton;

    @FXML
    private void onGeneratePdfButtonClick(ActionEvent event) {
        // Obtenir la liste des bateaux à partir de la TreeTableView
        TreeItem<Bateau> root = treeTableView.getRoot();
        ObservableList<Bateau> bateaux = FXCollections.observableArrayList();

        for (TreeItem<Bateau> bateauItem : root.getChildren()) {
            bateaux.add(bateauItem.getValue());

            for (TreeItem<Bateau> categorieItem : bateauItem.getChildren()) {
                bateaux.add(categorieItem.getValue());
            }
        }

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
    private TreeTableView<Bateau> treeTableView;

    @FXML
    private TreeTableColumn<Bateau, String> nomColumn;

    @FXML
    private TreeTableColumn<Bateau, Double> longueurBatColumn;

    @FXML
    private TreeTableColumn<Bateau, Double> largeurBatColumn;
    @FXML
    private TreeTableColumn<Bateau, Double> vitesseBatVoyColumn;


    @FXML
    public void initialize() {
        // Configurez les colonnes de la table pour utiliser les propriétés des objets Bateau
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().nomProperty());
        longueurBatColumn.setCellValueFactory(cellData -> {
            Bateau bateau = cellData.getValue().getValue();
            return bateau.getCategories().isEmpty() ? bateau.longueurBatProperty().asObject() : null;
        });
        largeurBatColumn.setCellValueFactory(cellData -> {
            Bateau bateau = cellData.getValue().getValue();
            return bateau.getCategories().isEmpty() ? bateau.largeurBatProperty().asObject() : null;
        });
        vitesseBatVoyColumn.setCellValueFactory(cellData -> {
            Bateau bateau = cellData.getValue().getValue();
            return bateau instanceof BateauVoyageur ? ((BateauVoyageur) bateau).vitesseBatVoyProperty().asObject() : null;
        });



        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getParent() != null && newValue.getParent().getValue() != null) {
                longueurBatColumn.setVisible(true);
                largeurBatColumn.setVisible(true);
                vitesseBatVoyColumn.setVisible(true);
            } else {
                longueurBatColumn.setVisible(false);
                largeurBatColumn.setVisible(false);
                vitesseBatVoyColumn.setVisible(false);
            }
        });


        // Charger les données des bateaux à partir de la base de données et les ajouter à la TreeTableView
        loadBateauData();

        largeurBatColumn.setVisible(false);
        longueurBatColumn.setVisible(false);
        vitesseBatVoyColumn.setVisible(false);

    }

    private void loadBateauData() {
        String sql = "SELECT b.id, b.nom, b.longueurBat, b.largeurBat, bv.vitesseBatVoy, bv.imageBatVoy " +
                "FROM Bateau b " +
                "JOIN bateauVoyageur bv ON b.id = bv.id";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            TreeItem<Bateau> root = new TreeItem<>(new Bateau(0, "Bateaux", 0, 0));
            root.setExpanded(true);
            treeTableView.setRoot(root);

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                double longueurBat = resultSet.getDouble("longueurBat");
                double largeurBat = resultSet.getDouble("largeurBat");
                double vitesseBatVoy = resultSet.getDouble("vitesseBatVoy");
                String imageBatVoy = resultSet.getString("imageBatVoy");

                BateauVoyageur bateauVoyageur = new BateauVoyageur(id, nom, longueurBat, largeurBat, vitesseBatVoy, imageBatVoy);
                TreeItem<Bateau> bateauItem = new TreeItem<>(bateauVoyageur);
                root.getChildren().add(bateauItem);
            }
        } catch (SQLException e) {

        }
    }
}