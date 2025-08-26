module com.example.demo {
    requires javafx.controls;
    requires javafx.fxml;
	requires javafx.graphics;
	requires java.desktop;
	requires javafx.base;


    opens com.example.demo to javafx.fxml;
    exports com.example.demo;
    exports com.almasb.minesweeper;

}