package wut.f1raceapp;

import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import wut.f1raceapp.dataModel.DriverData;
import wut.f1raceapp.dataModel.PositionData;
import wut.f1raceapp.dataModel.RaceControlData;
import wut.f1raceapp.dataModel.WeatherData;
import wut.f1raceapp.utils.F1DataStorage;
import eu.hansolo.tilesfx.Tile;
import wut.f1raceapp.utils.FlagObserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class RaceController implements Initializable, FlagObserver {

    public CategoryAxis xAxis;
    public NumberAxis yAxis;
    public Button saveButton;
    private F1DataStorage dataStorage;

    @FXML
    private Button updateButton;
    @FXML
    private Button weatherButton;
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
    @FXML
    private MediaView flagVideo;
    @FXML
    private Label noFlagLabel;
    @FXML
    private TableView<RowData> table;
    @FXML
    private TableColumn<RowData, Double> rpm;
    @FXML
    private TableColumn<RowData, Double> speed;
    @FXML
    private TableColumn<RowData, Double> throttle;
    @FXML
    private TableColumn<RowData, Double> brakes;
    @FXML
    private TableColumn<RowData, Double> ngear;
    @FXML
    private ComboBox<String> comboBoxAggregation;

    private final File greenFlagFile = new File(RaceApp.class.getResource("checkedFlag.mp4").getFile());
    private final Media greenFlagMedia = new Media(greenFlagFile.toURI().toString());
    private MediaPlayer greenFlagMediaPlayer = new MediaPlayer(greenFlagMedia);
    private boolean updateData = true;

    private Scene mainScene;
    private Scene weatherScene;

    public void updateChart() {
        List<DriverData> dataList = dataStorage.getDataList();

        // Sort dataList by date
        dataList.sort(Comparator.comparing(driverData -> {
            try {
                return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS").parse(driverData.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
                return new Date(0); // default date in case of parsing failure
            }
        }));

        XYChart.Series<String, Number> rpmSeries = findRpmSeries();
        if (rpmSeries == null) {
            rpmSeries = new XYChart.Series<>();
            rpmLineChart.getData().add(rpmSeries);
        }

        rpmSeries.getData().clear();

        // Add new data to the series and hide the symbols
        for (DriverData data : dataList) {
            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(formatDate(parseDate(data.getDate())), data.getRpm());
            rpmSeries.getData().add(dataPoint);

            // Hide the symbol for each data point
            Node symbol = dataPoint.getNode();
            if (symbol != null) symbol.setStyle("-fx-background-color: transparent, transparent;");
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
    private String formatDate(long timeInMillis) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        return dateFormat.format(new Date(timeInMillis));
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
    public void updateFlag(RaceControlData data) {
        if (data.getFlag() != null && !data.getFlag().equals("")) {
            // Ukryj napis
            noFlagLabel.setVisible(false);
            // Ustaw obrazek
            flagVideo.setMediaPlayer(greenFlagMediaPlayer);
            // Wyświetl obrazek
            flagVideo.setVisible(true);
            // Odtwórz dźwięk
            greenFlagMediaPlayer.play();
            // Zamknij gdy skończy się odtwarzanie
            greenFlagMediaPlayer.setOnEndOfMedia(() -> {
                greenFlagMediaPlayer.stop();
                flagVideo.setVisible(false);
                noFlagLabel.setVisible(true);
            });
        } else {
            System.out.println("Race control data read, but flag is empty");
        }

    }

    public void updateTable(){
        List<DriverData> dataList = dataStorage.getDataList();
        String aggregation = comboBoxAggregation.getValue();

        switch (aggregation) {
            case "Average":
                double rpm = dataList.stream().mapToDouble(DriverData::getRpm).average().orElse(0.0);
                double speed = dataList.stream().mapToDouble(DriverData::getSpeed).average().orElse(0.0);
                double throttle = dataList.stream().mapToDouble(DriverData::getThrottle).average().orElse(0.0);
                double brake = dataList.stream().mapToDouble(DriverData::getBrake).average().orElse(0.0);
                double nGear = dataList.stream().mapToDouble(DriverData::getnGear).average().orElse(0.0);
                ObservableList<RowData> data = FXCollections.observableArrayList(
                        new RowData(rpm, speed, throttle, brake, nGear) // Lista zawierająca jeden wiersz
                );
                table.setItems(data);
                break;
            case "Minimum":
                double rpmM = dataList.stream().mapToDouble(DriverData::getRpm).min().orElse(0.0);
                double speedM = dataList.stream().mapToDouble(DriverData::getSpeed).min().orElse(0.0);
                double throttleM = dataList.stream().mapToDouble(DriverData::getThrottle).min().orElse(0.0);
                double brakeM = dataList.stream().mapToDouble(DriverData::getBrake).min().orElse(0.0);
                double nGearM = dataList.stream().mapToDouble(DriverData::getnGear).min().orElse(0.0);
                ObservableList<RowData> dataM = FXCollections.observableArrayList(
                        new RowData(rpmM, speedM, throttleM, brakeM, nGearM) // Lista zawierająca jeden wiersz
                );
                table.setItems(dataM);
                break;
            case "Maximum":
                double rpmMa = dataList.stream().mapToDouble(DriverData::getRpm).max().orElse(0.0);
                double speedMa = dataList.stream().mapToDouble(DriverData::getSpeed).max().orElse(0.0);
                double throttleMa = dataList.stream().mapToDouble(DriverData::getThrottle).max().orElse(0.0);
                double brakeMa = dataList.stream().mapToDouble(DriverData::getBrake).max().orElse(0.0);
                double nGearMa = dataList.stream().mapToDouble(DriverData::getnGear).max().orElse(0.0);
                ObservableList<RowData> dataMa = FXCollections.observableArrayList(
                        new RowData(rpmMa, speedMa, throttleMa, brakeMa, nGearMa) // Lista zawierająca jeden wiersz
                );
                table.setItems(dataMa);
                break;
            case "SD":
                // Calculate means for SD
                double rpmSDMean= dataList.stream().mapToDouble(DriverData::getRpm).average().orElse(0.0);
                double speedSDMean = dataList.stream().mapToDouble(DriverData::getSpeed).average().orElse(0.0);
                double throttleSDMean = dataList.stream().mapToDouble(DriverData::getThrottle).average().orElse(0.0);
                double brakeSDMean = dataList.stream().mapToDouble(DriverData::getBrake).average().orElse(0.0);
                double nGearSDMean = dataList.stream().mapToDouble(DriverData::getnGear).average().orElse(0.0);

                // Calculate empirical SD
                double rpmSD = Math.sqrt(dataList.stream().mapToDouble(DriverData::getRpm).map(rpmValue -> Math.pow(rpmValue - rpmSDMean, 2)).average().orElse(0.0));
                double speedSD = Math.sqrt(dataList.stream().mapToDouble(DriverData::getSpeed).map(speedValue -> Math.pow(speedValue - speedSDMean, 2)).average().orElse(0.0));
                double throttleSD = Math.sqrt(dataList.stream().mapToDouble(DriverData::getThrottle).map(throttleValue -> Math.pow(throttleValue - throttleSDMean, 2)).average().orElse(0.0));
                double brakeSD = Math.sqrt(dataList.stream().mapToDouble(DriverData::getBrake).map(brakeValue -> Math.pow(brakeValue - brakeSDMean, 2)).average().orElse(0.0));
                double nGearSD = Math.sqrt(dataList.stream().mapToDouble(DriverData::getnGear).map(nGearValue -> Math.pow(nGearValue - nGearSDMean, 2)).average().orElse(0.0));

                ObservableList<RowData> dataSD = FXCollections.observableArrayList(
                        new RowData(rpmSD, speedSD, throttleSD, brakeSD, nGearSD) // Lista zawierająca jeden wiersz
                );
                table.setItems(dataSD);
                break;
            case "Outliers":
                // Get first quartile of speed
                double speedFirstQuartile = dataList.stream().mapToDouble(DriverData::getSpeed).sorted().skip(dataList.size() / 4).findFirst().orElse(0.0);
                // Get third quartile of speed
                double speedThirdQuartile = dataList.stream().mapToDouble(DriverData::getSpeed).sorted().skip(dataList.size() / 4 * 3).findFirst().orElse(0.0);
                // Get number of outliers - greater than third quartile + 1 * interquartile range
                double speedInterquartileRange = speedThirdQuartile - speedFirstQuartile;
                double speedOutliers = dataList.stream().mapToDouble(DriverData::getSpeed).filter(speedValue -> speedValue > speedThirdQuartile + 1 * speedInterquartileRange).count();

                double rpmFirstQuartile = dataList.stream().mapToDouble(DriverData::getRpm).sorted().skip(dataList.size() / 4).findFirst().orElse(0.0);
                double rpmThirdQuartile = dataList.stream().mapToDouble(DriverData::getRpm).sorted().skip(dataList.size() / 4 * 3).findFirst().orElse(0.0);
                double rpmInterquartileRange = rpmThirdQuartile - rpmFirstQuartile;
                double rpmOutliers = dataList.stream().mapToDouble(DriverData::getRpm).filter(rpmValue -> rpmValue > rpmThirdQuartile + 1 * rpmInterquartileRange).count();

                double throttleFirstQuartile = dataList.stream().mapToDouble(DriverData::getThrottle).sorted().skip(dataList.size() / 4).findFirst().orElse(0.0);
                double throttleThirdQuartile = dataList.stream().mapToDouble(DriverData::getThrottle).sorted().skip(dataList.size() / 4 * 3).findFirst().orElse(0.0);
                double throttleInterquartileRange = throttleThirdQuartile - throttleFirstQuartile;
                double throttleOutliers = dataList.stream().mapToDouble(DriverData::getThrottle).filter(throttleValue -> throttleValue > throttleThirdQuartile + 1 * throttleInterquartileRange).count();

                double brakeFirstQuartile = dataList.stream().mapToDouble(DriverData::getBrake).sorted().skip(dataList.size() / 4).findFirst().orElse(0.0);
                double brakeThirdQuartile = dataList.stream().mapToDouble(DriverData::getBrake).sorted().skip(dataList.size() / 4 * 3).findFirst().orElse(0.0);
                double brakeInterquartileRange = brakeThirdQuartile - brakeFirstQuartile;
                double brakeOutliers = dataList.stream().mapToDouble(DriverData::getBrake).filter(brakeValue -> brakeValue > brakeThirdQuartile + 1 * brakeInterquartileRange).count();

                double nGearFirstQuartile = dataList.stream().mapToDouble(DriverData::getnGear).sorted().skip(dataList.size() / 4).findFirst().orElse(0.0);
                double nGearThirdQuartile = dataList.stream().mapToDouble(DriverData::getnGear).sorted().skip(dataList.size() / 4 * 3).findFirst().orElse(0.0);
                double nGearInterquartileRange = nGearThirdQuartile - nGearFirstQuartile;
                double nGearOutliers = dataList.stream().mapToDouble(DriverData::getnGear).filter(nGearValue -> nGearValue > nGearThirdQuartile + 1 * nGearInterquartileRange).count();

                ObservableList<RowData> dataOutliers = FXCollections.observableArrayList(
                        new RowData(speedOutliers, rpmOutliers, throttleOutliers, brakeOutliers, nGearOutliers) // Lista zawierająca jeden wiersz
                );
                table.setItems(dataOutliers);
                break;
            default:
                System.out.println("Wrong aggregation type");
        }

    }

    public void switchToWeather() {
        // Switch to scene generated by weather-scene.fxml but without forgetting the current Scene

    }

    private void initializeSpeedTile() {
        xAxis.setLabel("Time");
        yAxis.setLabel("RPM");
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
    }

    private void initializeThrottleTile() {
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
    }

    private void initializeBrakeTile() {
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
    }

    private void initializePositionTile() {
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
    }

    private void initializeTableColumns() {
        rpm.setCellValueFactory(new PropertyValueFactory<RowData, Double>("rpm"));
        speed.setCellValueFactory(new PropertyValueFactory<RowData, Double>("speed"));
        throttle.setCellValueFactory(new PropertyValueFactory<RowData, Double>("throttle"));
        brakes.setCellValueFactory(new PropertyValueFactory<RowData, Double>("brakes"));
        ngear.setCellValueFactory(new PropertyValueFactory<RowData, Double>("ngear"));
    }

    public void saveData() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Data");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File file = fileChooser.showSaveDialog(rpmLineChart.getScene().getWindow());

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (RowData data : table.getItems()) {
                    String line = data.getRpm() + ", " + data.getSpeed() + ", " + data.getThrottle() + ", " + data.getBrakes() + ", " + data.getNgear();
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                // Handle exceptions
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeSpeedTile();
        initializeThrottleTile();
        initializeBrakeTile();
        initializePositionTile();

        // Dodanie opcji do ComboBoxa
        comboBoxAggregation.getItems().addAll("Average", "Minimum", "Maximum", "SD", "Outliers");
        comboBoxAggregation.getSelectionModel().selectFirst();

        initializeTableColumns();


        // Uruchomienie wątku do aktualizacji Tile
        new Thread(() -> {
            while (updateData) {
                try {
                    // Aktualizuj UI na JavaFX Application Thread
                    Platform.runLater(() -> {
                        try {
                            updateTile();
                        } catch (IndexOutOfBoundsException e) {
                            System.out.println("No data to update tile. Perhaps the socket client has stopped.");
                            e.printStackTrace();
                        }
                    });

                    // Odczekaj pewien czas przed ponownym sprawdzeniem
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // Uruchomienie wątku do aktualizacji tabeli
        new Thread(() -> {
            while (updateData) {
                try {
                    // Aktualizuj UI na JavaFX Application Thread
                    Platform.runLater(this::updateTable);

                    // Odczekaj pewien czas przed ponownym sprawdzeniem
                    Thread.sleep(4000);
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

    public Scene getMainScene() {
        return mainScene;
    }

    public void setMainScene(Scene mainScene) {
        this.mainScene = mainScene;
    }

    public Scene getWeatherScene() {
        return weatherScene;
    }

    public void setWeatherScene(Scene weatherScene) {
        this.weatherScene = weatherScene;
    }


    // Klasa lokalna reprezentująca wiersze tabeli
    public static class RowData {

        private SimpleDoubleProperty rpm;
        private SimpleDoubleProperty speed;
        private SimpleDoubleProperty throttle;
        private SimpleDoubleProperty brakes;
        private SimpleDoubleProperty ngear;

        public RowData(double rpm, double speed, double throttle, double brakes, double ngear) {
            this.rpm = new SimpleDoubleProperty(Math.round(rpm * 100.0) / 100.0);
            this.speed = new SimpleDoubleProperty(Math.round(speed * 100.0) / 100.0);
            this.throttle = new SimpleDoubleProperty(Math.round(throttle * 100.0) / 100.0);
            this.brakes = new SimpleDoubleProperty(Math.round(brakes * 100.0) / 100.0);
            this.ngear = new SimpleDoubleProperty(Math.round(ngear * 100.0) / 100.0);
        }


        public double getRpm() {
            return rpm.get();
        }

        public double getSpeed() {
            return speed.get();
        }

        public double getThrottle() {
            return throttle.get();
        }

        public double getBrakes() {
            return brakes.get();
        }

        public double getNgear() {
            return ngear.get();
        }
    }

}