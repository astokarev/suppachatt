module com.example.chat {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.chat.client to javafx.fxml;
    exports com.example.chat.client;
}