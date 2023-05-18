package com.example.demo;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.layout.element.Image;

import java.io.FileNotFoundException;
import java.util.List;

public class PdfGenerator {

    public static void createPdf(String destination, List<Bateau> bateaux) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(destination);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4.rotate());

        // Add a title to the PDF document
        document.add(new Paragraph("Liste des bateaux"));

        // Define the number of columns for the grid
        int numColumns = 3;

        // Create a table for the boat cards
        Table table = new Table(numColumns);
        table.setWidth(UnitValue.createPercentValue(100));

        for (Bateau bateau : bateaux) {
            // Create a cell for each boat card
            Cell cell = new Cell();
            cell.setPadding(10);

            // Add boat information to the cell
            cell.add(new Paragraph("Nom: " + bateau.getNom()));
            cell.add(new Paragraph("Longueur: " + bateau.getLongueurBat() + "m"));
            cell.add(new Paragraph("Largeur: " + bateau.getLargeurBat() + "m"));

            // Check if the current boat is an instance of BateauVoyageur
            if (bateau instanceof BateauVoyageur) {
                BateauVoyageur bateauVoyageur = (BateauVoyageur) bateau;
                cell.add(new Paragraph("Vitesse: " + bateauVoyageur.getVitesseBatVoy() + " km/h"));
                List<Equipement> equipements = bateauVoyageur.getEquipements();
                if (!equipements.isEmpty()) {
                    cell.add(new Paragraph("Équipements:"));
                    for (Equipement equipement : equipements) {
                        cell.add(new Paragraph("- " + equipement.getLibEquip()));
                    }

                    // Load the image from the URL and add it to the cell
                    try {
                        ImageData imageData = ImageDataFactory.create(bateauVoyageur.getImageBatVoy());
                        Image image = new Image(imageData);
                        image.setWidth(UnitValue.createPercentValue(100));
                        cell.add(image);
                    } catch (Exception e) {
                        cell.add(new Paragraph("Erreur lors du chargement de l'image"));
                    }
                }
                // Add the cell to the table
                table.addCell(cell);
            }
        }

        // Add the table to the PDF document
        document.add(table);
        // Close the document
        document.close();
    }
}

