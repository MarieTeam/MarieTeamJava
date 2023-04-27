package com.example.demo;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

public class PdfGenerator {

    public static void createPdf(String destination, List<Bateau> bateaux) throws FileNotFoundException {
        PdfWriter writer = new PdfWriter(destination);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Ajoutez un titre au document PDF
        document.add(new Paragraph("Liste des bateaux"));

        // Créez un tableau pour les informations des bateaux
        Table table = new Table(new float[]{1, 1});
        table.setWidth(UnitValue.createPercentValue(100)); // Change this line
        table.addHeaderCell("ID");
        table.addHeaderCell("Nom");

        // Ajoutez les informations des bateaux au tableau
        for (Bateau bateau : bateaux) {
            table.addCell(Integer.toString(bateau.getId()));
            table.addCell(bateau.getNom());
        }
        // Ajoutez le tableau au document PDF
        document.add(table);
        // Fermez le document
        document.close();
    }


}
