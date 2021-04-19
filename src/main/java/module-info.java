module org.tulipInd {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.desktop;

    opens org.tulipInd to javafx.fxml;
    exports org.tulipInd;
}