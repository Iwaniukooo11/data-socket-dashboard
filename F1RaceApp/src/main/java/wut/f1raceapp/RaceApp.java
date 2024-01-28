package wut.f1raceapp;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import wut.f1raceapp.sockets.SocketClient;
import wut.f1raceapp.utils.F1DataProcessor;
import wut.f1raceapp.utils.F1DataStorage;

import java.io.IOException;

public class RaceApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        F1DataStorage dataStorage = new F1DataStorage();
        SocketClient socketClient = new SocketClient(dataStorage, 8080);

        FXMLLoader fxmlLoader = new FXMLLoader(RaceApp.class.getResource("main-scene.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        scene.getStylesheets().add(RaceApp.class.getResource("styles/main-styles.css").toExternalForm());

        RaceController rcontroller = fxmlLoader.getController();
        rcontroller.setDataStorage(dataStorage); // rcontroller korzysta z dataStorage do pobierania danych
        rcontroller.setMainScene(scene);
        dataStorage.registerFlagObserver(rcontroller); // dataStorage powiadamia rcontroller o aktualizacji flagi

        socketClient.start();

        stage.setTitle("Formula 1 races data analysis");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(windowEvent -> {
            socketClient.stopClient();
            System.out.println("Client stopped");
            System.exit(0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}