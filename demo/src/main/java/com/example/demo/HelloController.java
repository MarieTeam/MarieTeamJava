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
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
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
    private TreeTableColumn<Bateau, String> libelleColumn;

    @FXML
    private TreeTableColumn<Bateau, Integer> capaciteMaxColumn;
    @FXML
    public void initialize() {
        // Configurez les colonnes de la table pour utiliser les propriétés des objets Bateau
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().getValue().nomProperty());
        libelleColumn.setCellValueFactory(cellData -> {
            Bateau bateau = cellData.getValue().getValue();
            return bateau.getCategories().isEmpty() ? bateau.libelleProperty() : null;
        });
        capaciteMaxColumn.setCellValueFactory(cellData -> {
            Bateau bateau = cellData.getValue().getValue();
            return bateau.getCategories().isEmpty() ? bateau.capaciteMaxProperty().asObject() : null;
        });
        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getParent() != null && newValue.getParent().getValue() != null) {
                libelleColumn.setVisible(true);
                capaciteMaxColumn.setVisible(true);
            } else {
                libelleColumn.setVisible(false);
                capaciteMaxColumn.setVisible(false);
            }
        });


        // Charger les données des bateaux à partir de la base de données et les ajouter à la TreeTableView
        loadBateauData();

        libelleColumn.setVisible(false);
        capaciteMaxColumn.setVisible(false);

    }

    private void loadBateauData() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "SELECT DISTINCT b.id, b.nom, ca.libelle, c.capacite_max " +
                    "FROM `Contenir` c " +
                    "INNER JOIN Bateau b on b.id = c.id_bateau " +
                    "INNER JOIN Categorie ca on ca.lettre = c.lettre_cat " +
                    "ORDER BY b.nom";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            Map<Integer, Bateau> bateauxMap = new HashMap<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                String libelle = resultSet.getString("libelle");
                int capaciteMax = resultSet.getInt("capacite_max");

                Bateau bateau = bateauxMap.computeIfAbsent(id, k -> new Bateau(id, nom));
                bateau.addCategorie(new Categorie(libelle, capaciteMax));
            }

            TreeItem<Bateau> root = new TreeItem<>();
            root.setExpanded(true);

            for (Bateau bateau : bateauxMap.values()) {
                TreeItem<Bateau> bateauItem = new TreeItem<>(bateau);
                root.getChildren().add(bateauItem);

                // Ajoutez un écouteur à la propriété expandedProperty de chaque TreeItem de bateau
                bateauItem.expandedProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue) {
                        libelleColumn.setVisible(true);
                        capaciteMaxColumn.setVisible(true);
                    } else {
                        libelleColumn.setVisible(false);
                        capaciteMaxColumn.setVisible(false);
                    }
                });

                for (Categorie categorie : bateau.getCategories()) {
                    bateauItem.getChildren().add(new TreeItem<>(new Bateau(bateau.getId(), bateau.getNom(), categorie.getLibelle(), categorie.getCapaciteMax())));
                }
            }

            treeTableView.setRoot(root);
            treeTableView.setShowRoot(false);

            preparedStatement.close();
            resultSet.close();
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des données des bateaux.");
            e.printStackTrace();
        }
    }


}
