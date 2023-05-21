package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();

        // Créer la scène
        Scene scene = new Scene(root, 320, 240);

        // Ajouter le fichier CSS à la scène
        scene.getStylesheets().add(getClass().getResource("/com/example/demo/style.css").toExternalForm());

        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();

        Connection connection = DatabaseConnection.getConnection();
    }


    @Override
    public void stop() {
        // Fermer la connexion lorsque vous avez terminé
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch();
    }
}
