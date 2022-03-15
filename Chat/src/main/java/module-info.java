module com.example.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens chatApp.client to javafx.fxml;
    exports chatApp.client;
}