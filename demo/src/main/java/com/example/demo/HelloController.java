package com.example.demo;

import com.example.demo.Bateau;
import com.example.demo.BateauVoyageur;
import com.example.demo.DatabaseConnection;
import com.example.demo.PdfGenerator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.awt.Desktop;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.scene.layout.VBox;

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
    private TreeTableColumn<Bateau, String> equipementsColumn;


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
        equipementsColumn.setCellValueFactory(cellData -> {
            Bateau bateau = cellData.getValue().getValue();
            if (bateau instanceof BateauVoyageur) {
                BateauVoyageur bateauVoyageur = (BateauVoyageur) bateau;
                List<Equipement> equipements = bateauVoyageur.getEquipements();
                if (bateauVoyageur.showAllEquipmentsProperty().get() || equipements.size() <= 3) {
                    String equipementsString = equipements.stream()
                            .map(Equipement::getLibEquip)
                            .collect(Collectors.joining("\n"));
                    return new SimpleStringProperty(equipementsString);
                } else {
                    String equipementsString = equipements.subList(0, 1).stream()
                            .map(Equipement::getLibEquip)
                            .collect(Collectors.joining("\n"));
                    return new SimpleStringProperty(equipementsString + "\n...(plus)");
                }
            }
            return null;
        });

        equipementsColumn.setCellFactory(column -> {
            return new TreeTableCell<Bateau, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setText(item);
                        setOnMouseClicked(event -> {
                            Bateau bateau = getTreeTableRow().getItem();
                            if (bateau instanceof BateauVoyageur) {
                                BateauVoyageur bateauVoyageur = (BateauVoyageur) bateau;
                                if (item.endsWith("...(plus)")) {
                                    bateauVoyageur.showAllEquipmentsProperty().set(true);
                                } else {
                                    bateauVoyageur.showAllEquipmentsProperty().set(false);
                                }
                                // Forcer la mise à jour de la table
                                treeTableView.refresh();
                            }
                        });
                    }
                }
            };
        });

        treeTableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.getParent() != null && newValue.getParent().getValue() != null) {
                longueurBatColumn.setVisible(true);
                largeurBatColumn.setVisible(true);
                vitesseBatVoyColumn.setVisible(true);
                equipementsColumn.setVisible(true);
            } else {
                longueurBatColumn.setVisible(false);
                largeurBatColumn.setVisible(false);
                vitesseBatVoyColumn.setVisible(false);
                equipementsColumn.setVisible(false);
            }
        });

        // Charger les données des bateaux à partir de la base de données et les ajouter à la TreeTableView
        loadBateauData();

        largeurBatColumn.setVisible(false);
        longueurBatColumn.setVisible(false);
        vitesseBatVoyColumn.setVisible(false);
        equipementsColumn.setVisible(false);
    }

    private void loadBateauData() {
        String sql = "SELECT b.id, b.nom, b.longueurBat, b.largeurBat, bv.vitesseBatVoy, bv.imageBatVoy, e.idEquip, e.libEquip " +
                "FROM Bateau b " +
                "JOIN bateauVoyageur bv ON b.id = bv.id " +
                "LEFT JOIN Equipement e ON bv.id = e.idBatVoy";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            TreeItem<Bateau> root = new TreeItem<>(new Bateau(0, "Bateaux", 0, 0));
            root.setExpanded(true);
            treeTableView.setRoot(root);

            Map<Integer, BateauVoyageur> bateaux = new HashMap<>();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String nom = resultSet.getString("nom");
                double longueurBat = resultSet.getDouble("longueurBat");
                double largeurBat = resultSet.getDouble("largeurBat");
                double vitesseBatVoy = resultSet.getDouble("vitesseBatVoy");
                String imageBatVoy = resultSet.getString("imageBatVoy");

                // Check if the BateauVoyageur already exists
                BateauVoyageur bateauVoyageur = bateaux.get(id);
                if (bateauVoyageur == null) {
                    bateauVoyageur = new BateauVoyageur(id, nom, longueurBat, largeurBat, vitesseBatVoy, imageBatVoy);
                    bateaux.put(id, bateauVoyageur);

                    TreeItem<Bateau> bateauItem = new TreeItem<>(bateauVoyageur);
                    root.getChildren().add(bateauItem);
                }

                // Add the equipement to the BateauVoyageur
                int idEquip = resultSet.getInt("idEquip");
                String libEquip = resultSet.getString("libEquip");
                int idBatVoy = resultSet.getInt("id"); // Assuming "id" is the idBatVoy in the Equipement table
                Equipement equipement = new Equipement(idEquip, libEquip, idBatVoy);
                bateauVoyageur.addEquipement(equipement);
            }
        } catch (SQLException e) {
            // handle the exception
        }
    }
}