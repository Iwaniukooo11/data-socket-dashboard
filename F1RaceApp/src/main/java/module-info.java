module wut.f1raceapp {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires eu.hansolo.tilesfx;


    exports wut.f1raceapp.dataModel;
    opens wut.f1raceapp.dataModel to com.fasterxml.jackson.databind;
    opens wut.f1raceapp to javafx.fxml;
    exports wut.f1raceapp;

}