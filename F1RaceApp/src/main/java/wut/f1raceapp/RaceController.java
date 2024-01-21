package wut.f1raceapp;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;
import wut.f1raceapp.dataModel.DriverData;
import wut.f1raceapp.dataModel.PositionData;
import wut.f1raceapp.utils.F1DataStorage;
import eu.hansolo.tilesfx.Tile;

import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class RaceController implements Initializable {

    private F1DataStorage dataStorage;
    @FXML
    private Label testLabel;
    @FXML
    private Button updateButton;
    @FXML
    private Tile speedTile;
    @FXML
    private Tile throttleTile;
    @FXML
    private Tile brakesTile;
    @FXML
    private Tile positionTile;
    @FXML
    private Tile positionBTile;
    @FXML
    private LineChart<String, Number> rpmLineChart;

    private String[] position = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20"};
    private boolean updateData = true;


    public void updateChart() {
        List<DriverData> dataList = dataStorage.getDataList();

        XYChart.Series<String, Number> rpmSeries = findRpmSeries();
        if (rpmSeries == null) {
            // Jeśli nie, utwórz nową serię danych
            rpmSeries = new XYChart.Series<>();
            rpmLineChart.getData().add(rpmSeries);
        }

        // Wyczyść poprzednie dane z serii - jeśli istniały
        rpmSeries.getData().clear();

        // Dodaj nowe dane do serii
        for (DriverData data : dataList) {
            rpmSeries.getData().add(new XYChart.Data<>(String.valueOf(parseDate(data.getDate())), data.getRpm()));
        }
    }

    private long parseDate(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

        try {
            Date date = dateFormat.parse(dateString);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0L; // Jeśli wystąpi błąd parsowania, zwracamy 0
        }
    }

    private XYChart.Series<String, Number> findRpmSeries() {
        for (XYChart.Series<String, Number> series : rpmLineChart.getData()) {
            return series;
        }
        return null;
    }

    private void updateTile() {
        DriverData lastDriverState = dataStorage.peekLastData();
        speedTile.setValue(lastDriverState.getSpeed());
        throttleTile.setValue(lastDriverState.getThrottle());
        brakesTile.setValue(lastDriverState.getBrake());
        // positionTile.setDescription(position[new Random().nextInt(position.length)]);
        PositionData lastPosition = dataStorage.peekLastPosition();
        positionTile.setValue(lastPosition.getPosition());

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Ustawienie początkowych wartości dla Tile Speed
        speedTile.setSkinType(Tile.SkinType.GAUGE2);
        speedTile.setTitle("Speed");
        speedTile.setUnit("km/h");
        speedTile.setGradientStops(new Stop(0, Tile.BLUE),
                new Stop(0.25, Tile.GREEN),
                new Stop(0.5, Tile.YELLOW),
                new Stop(0.75, Tile.ORANGE),
                new Stop(1, Tile.RED));
        speedTile.setMinValue(0);
        speedTile.setMaxValue(300);
        speedTile.setStrokeWithGradient(true);
        speedTile.setAnimated(true);
        speedTile.setTextSize(Tile.TextSize.BIGGER);
        speedTile.setValue(0);

        // Ustawienie początkowych wartości dla Tile Throttle
        throttleTile.setSkinType(Tile.SkinType.GAUGE2);
        throttleTile.setTitle("Throttle");
        throttleTile.setUnit("%");
        throttleTile.setGradientStops(new Stop(0, Tile.BLUE),
                new Stop(0.25, Tile.GREEN),
                new Stop(0.5, Tile.YELLOW),
                new Stop(0.75, Tile.ORANGE),
                new Stop(1, Tile.RED));
        throttleTile.setMinValue(0);
        throttleTile.setMaxValue(100);
        throttleTile.setStrokeWithGradient(true);
        throttleTile.setAnimated(true);
        throttleTile.setTextSize(Tile.TextSize.BIGGER);
        throttleTile.setValue(0);

        // Ustawienie początkowych wartości dla Tile Brake
        brakesTile.setSkinType(Tile.SkinType.GAUGE2);
        brakesTile.setTitle("Brake");
        brakesTile.setUnit("%");
        brakesTile.setGradientStops(new Stop(0, Tile.BLUE),
                new Stop(0.25, Tile.GREEN),
                new Stop(0.5, Tile.YELLOW),
                new Stop(0.75, Tile.ORANGE),
                new Stop(1, Tile.RED));
        brakesTile.setMinValue(0);
        brakesTile.setMaxValue(100);
        brakesTile.setStrokeWithGradient(true);
        brakesTile.setAnimated(true);
        brakesTile.setTextSize(Tile.TextSize.BIGGER);
        brakesTile.setValue(0);

        // Ustawienie początkowych wartości dla Tile Position
        positionBTile.setSkinType(Tile.SkinType.CHARACTER);
        positionBTile.setTitle("Position");
        positionBTile.setTextSize(Tile.TextSize.BIGGER);
        positionBTile.setTitleAlignment(TextAlignment.CENTER);

        positionTile.setSkinType(Tile.SkinType.COLOR);
        positionTile.setAnimated(true);
        positionTile.setRoundedCorners(true);
        // Brak progress baru
        // positionTile.setThreshold(1);
        positionTile.setBarBackgroundColor(javafx.scene.paint.Color.TRANSPARENT);
        positionTile.setBarColor(javafx.scene.paint.Color.TRANSPARENT);
        positionTile.setBackgroundColor(javafx.scene.paint.Color.web("#DF2C63"));
        positionTile.setMinValue(1);
        positionTile.setMaxValue(20);

        // Inna opcja - character
//        positionTile.setSkinType(Tile.SkinType.CHARACTER);
//        positionTile.setBackgroundColor(javafx.scene.paint.Color.web("#DF2C63"));
//        positionTile.setAnimated(true);
//        positionTile.setRoundedCorners(true);
//        // Ustawienie numerka na środku
//        // positionTile.setDescription("15");


        // Uruchomienie wątku do aktualizacji Tile
        new Thread(() -> {
            while (updateData) {
                try {
                    // Aktualizuj UI na JavaFX Application Thread
                    Platform.runLater(this::updateTile);

                    // Odczekaj pewien czas przed ponownym sprawdzeniem
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    public void setDataStorage(F1DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    public void setUpdateData(boolean updateData) {
        this.updateData = updateData;
    }
}