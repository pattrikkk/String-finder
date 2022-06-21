module com.example.vlakna {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.vlakna to javafx.fxml;
    exports com.example.vlakna;
}