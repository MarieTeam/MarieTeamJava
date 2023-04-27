module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires kernel;
    requires layout;
    requires java.desktop;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
}