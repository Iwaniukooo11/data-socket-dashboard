package wut.f1raceapp;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.colors.Bright;
import eu.hansolo.tilesfx.colors.Dark;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import wut.f1raceapp.dataModel.WeatherData;
import wut.f1raceapp.dataModel.WeatherData;
import wut.f1raceapp.utils.F1DataStorage;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class WeatherController implements Initializable {

    private F1DataStorage dataStorage;

    @FXML
    private Button driverButton;
    @FXML
    private ComboBox<String> comboBoxWeather;
    @FXML
    private TableView<WeatherRowData> tableWeather;
    @FXML
    private TableColumn<WeatherRowData, Double> track;
    @FXML
    private TableColumn<WeatherRowData, Double> temperature;
    @FXML
    private TableColumn<WeatherRowData, Double> humidity;
    @FXML
    private TableColumn<WeatherRowData, Double> pressure;
    @FXML
    private TableColumn<WeatherRowData, Double> wind;
    @FXML
    private Tile trackTempTile;
    @FXML
    private Tile airTempTile;
    @FXML
    private Tile windDirectionTile;
    @FXML
    private Tile windSpeedTile;
    @FXML
    private Gauge windDirectionGauge;


    private Scene mainScenePrev;


    public void switchToDriver() {

        Stage stage = (Stage) driverButton.getScene().getWindow();
        stage.setScene(mainScenePrev);
        stage.show();

    }

    private void updateTiles() {
        WeatherData weatherData = dataStorage.peekLastWeather();
        if (weatherData != null) {
            trackTempTile.setValue(weatherData.getTrackTemperature());
            airTempTile.setValue(weatherData.getAirTemperature());
            windDirectionGauge.setValue(weatherData.getWindDirection());
            windSpeedTile.setValue(weatherData.getWindSpeed());
        }
    }

    private void updateTable() {

        List<WeatherData> weatherData = dataStorage.getWeatherList();
        String selected = comboBoxWeather.getSelectionModel().getSelectedItem();

        switch(selected) {
            case "Average":
                double track = weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).average().orElse(0.0);
                double temperature = weatherData.stream().mapToDouble(WeatherData::getAirTemperature).average().orElse(0.0);
                double humidity = weatherData.stream().mapToDouble(WeatherData::getHumidity).average().orElse(0.0);
                double pressure = weatherData.stream().mapToDouble(WeatherData::getPressure).average().orElse(0.0);
                double wind = weatherData.stream().mapToDouble(WeatherData::getWindSpeed).average().orElse(0.0);
                ObservableList<WeatherRowData> data = FXCollections.observableArrayList(
                        new WeatherRowData(track, temperature, humidity, pressure, wind) // Lista zawierająca jeden wiersz
                );
                tableWeather.setItems(data);
                break;
            case "Minimum":
                double trackMin = weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).min().orElse(0.0);
                double temperatureMin = weatherData.stream().mapToDouble(WeatherData::getAirTemperature).min().orElse(0.0);
                double humidityMin = weatherData.stream().mapToDouble(WeatherData::getHumidity).min().orElse(0.0);
                double pressureMin = weatherData.stream().mapToDouble(WeatherData::getPressure).min().orElse(0.0);
                double windMin = weatherData.stream().mapToDouble(WeatherData::getWindSpeed).min().orElse(0.0);

                ObservableList<WeatherRowData> dataMin = FXCollections.observableArrayList(
                        new WeatherRowData(trackMin, temperatureMin, humidityMin, pressureMin, windMin) // Lista zawierająca jeden wiersz
                );
                tableWeather.setItems(dataMin);
                break;
            case "Maximum":
                double trackMax = weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).max().orElse(0.0);
                double temperatureMax = weatherData.stream().mapToDouble(WeatherData::getAirTemperature).max().orElse(0.0);
                double humidityMax = weatherData.stream().mapToDouble(WeatherData::getHumidity).max().orElse(0.0);
                double pressureMax = weatherData.stream().mapToDouble(WeatherData::getPressure).max().orElse(0.0);
                double windMax = weatherData.stream().mapToDouble(WeatherData::getWindSpeed).max().orElse(0.0);

                ObservableList<WeatherRowData> dataMax = FXCollections.observableArrayList(
                        new WeatherRowData(trackMax, temperatureMax, humidityMax, pressureMax, windMax) // Lista zawierająca jeden wiersz
                );
                tableWeather.setItems(dataMax);
                break;
            case "SD":
                // Calculate means for SD
                double trackMean = weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).average().orElse(0.0);
                double temperatureMean = weatherData.stream().mapToDouble(WeatherData::getAirTemperature).average().orElse(0.0);
                double humidityMean = weatherData.stream().mapToDouble(WeatherData::getHumidity).average().orElse(0.0);
                double pressureMean = weatherData.stream().mapToDouble(WeatherData::getPressure).average().orElse(0.0);
                double windMean = weatherData.stream().mapToDouble(WeatherData::getWindSpeed).average().orElse(0.0);

                // Calculate SD
                double trackSD = Math.sqrt(weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).map(x -> Math.pow(x - trackMean, 2)).average().orElse(0.0));
                double temperatureSD = Math.sqrt(weatherData.stream().mapToDouble(WeatherData::getAirTemperature).map(x -> Math.pow(x - temperatureMean, 2)).average().orElse(0.0));
                double humiditySD = Math.sqrt(weatherData.stream().mapToDouble(WeatherData::getHumidity).map(x -> Math.pow(x - humidityMean, 2)).average().orElse(0.0));
                double pressureSD = Math.sqrt(weatherData.stream().mapToDouble(WeatherData::getPressure).map(x -> Math.pow(x - pressureMean, 2)).average().orElse(0.0));
                double windSD = Math.sqrt(weatherData.stream().mapToDouble(WeatherData::getWindSpeed).map(x -> Math.pow(x - windMean, 2)).average().orElse(0.0));

                ObservableList<WeatherRowData> dataSD = FXCollections.observableArrayList(
                        new WeatherRowData(trackSD, temperatureSD, humiditySD, pressureSD, windSD) // Lista zawierająca jeden wiersz
                );
                tableWeather.setItems(dataSD);
                break;
            case "Outliers":
                // Calculate first quartile for track
                double trackQ1 = weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).sorted().skip((long) (weatherData.size() * 0.25)).findFirst().orElse(0.0);
                // Calculate third quartile for track
                double trackQ3 = weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).sorted().skip((long) (weatherData.size() * 0.75)).findFirst().orElse(0.0);
                // Calculate IQR for track
                double trackIQR = trackQ3 - trackQ1;
                // Get number of outliers - greater than third quartile + 1 * interquartile range
                double trackOutliers = weatherData.stream().mapToDouble(WeatherData::getTrackTemperature).filter(x -> x > trackQ3 + 1 * trackIQR).count();

                double temperatureQ1 = weatherData.stream().mapToDouble(WeatherData::getAirTemperature).sorted().skip((long) (weatherData.size() * 0.25)).findFirst().orElse(0.0);
                double temperatureQ3 = weatherData.stream().mapToDouble(WeatherData::getAirTemperature).sorted().skip((long) (weatherData.size() * 0.75)).findFirst().orElse(0.0);
                double temperatureIQR = temperatureQ3 - temperatureQ1;
                double temperatureOutliers = weatherData.stream().mapToDouble(WeatherData::getAirTemperature).filter(x -> x > temperatureQ3 + 1 * temperatureIQR).count();

                double humidityQ1 = weatherData.stream().mapToDouble(WeatherData::getHumidity).sorted().skip((long) (weatherData.size() * 0.25)).findFirst().orElse(0.0);
                double humidityQ3 = weatherData.stream().mapToDouble(WeatherData::getHumidity).sorted().skip((long) (weatherData.size() * 0.75)).findFirst().orElse(0.0);
                double humidityIQR = humidityQ3 - humidityQ1;
                double humidityOutliers = weatherData.stream().mapToDouble(WeatherData::getHumidity).filter(x -> x > humidityQ3 + 1 * humidityIQR).count();

                double pressureQ1 = weatherData.stream().mapToDouble(WeatherData::getPressure).sorted().skip((long) (weatherData.size() * 0.25)).findFirst().orElse(0.0);
                double pressureQ3 = weatherData.stream().mapToDouble(WeatherData::getPressure).sorted().skip((long) (weatherData.size() * 0.75)).findFirst().orElse(0.0);
                double pressureIQR = pressureQ3 - pressureQ1;
                double pressureOutliers = weatherData.stream().mapToDouble(WeatherData::getPressure).filter(x -> x > pressureQ3 + 1 * pressureIQR).count();

                double windQ1 = weatherData.stream().mapToDouble(WeatherData::getWindSpeed).sorted().skip((long) (weatherData.size() * 0.25)).findFirst().orElse(0.0);
                double windQ3 = weatherData.stream().mapToDouble(WeatherData::getWindSpeed).sorted().skip((long) (weatherData.size() * 0.75)).findFirst().orElse(0.0);
                double windIQR = windQ3 - windQ1;
                double windOutliers = weatherData.stream().mapToDouble(WeatherData::getWindSpeed).filter(x -> x > windQ3 + 1 * windIQR).count();

                ObservableList<WeatherRowData> dataOutliers = FXCollections.observableArrayList(
                        new WeatherRowData(trackOutliers, temperatureOutliers, humidityOutliers, pressureOutliers, windOutliers) // Lista zawierająca jeden wiersz
                );
                tableWeather.setItems(dataOutliers);
                break;
        }
    }

    private void initializeWindDirectionTile() {

        windDirectionTile.setSkinType(Tile.SkinType.CHARACTER);
        windDirectionTile.setTitle("Wind direction");
        windDirectionTile.setTextSize(Tile.TextSize.BIGGER);

        windDirectionGauge.setMinValue(0);
        windDirectionGauge.setMaxValue(360);
        windDirectionGauge.setStartAngle(180);
        windDirectionGauge.setAngleRange(360);
        windDirectionGauge.setCustomTickLabelsEnabled(true);
        windDirectionGauge.setCustomTickLabels("N", "NE", "E", "SE", "S", "SW", "W", "NW", "N");
        windDirectionGauge.setCustomTickLabelFontSize(26);
        windDirectionGauge.setAnimated(true);
        windDirectionGauge.setAnimationDuration(1000);
        windDirectionGauge.setValueVisible(false);

    }

    private void initializeWindSpeedTile() {

        windSpeedTile.setSkinType(Tile.SkinType.GAUGE2);
        windSpeedTile.setTitle("Wind speed");
        windSpeedTile.setUnit("m/s");
        windSpeedTile.setGradientStops(new Stop(0, Tile.BLUE),
                new Stop(0.25, Tile.GREEN),
                new Stop(0.5, Tile.YELLOW),
                new Stop(0.75, Tile.ORANGE),
                new Stop(1, Tile.RED));
        windSpeedTile.setMinValue(0);
        windSpeedTile.setMaxValue(100);
        windSpeedTile.setStrokeWithGradient(true);
        windSpeedTile.setAnimated(true);
        windSpeedTile.setTextSize(Tile.TextSize.BIGGER);
        windSpeedTile.setValue(0);

    }

    private void initializeAirTempTile() {

            airTempTile.setSkinType(Tile.SkinType.GAUGE2);
            airTempTile.setTitle("Air temperature");
            airTempTile.setUnit("°C");
            airTempTile.setGradientStops(new Stop(0, Tile.BLUE),
                    new Stop(0.25, Tile.GREEN),
                    new Stop(0.5, Tile.YELLOW),
                    new Stop(0.75, Tile.ORANGE),
                    new Stop(1, Tile.RED));
            airTempTile.setMinValue(-5);
            airTempTile.setMaxValue(50);
            airTempTile.setStrokeWithGradient(true);
            airTempTile.setAnimated(true);
            airTempTile.setTextSize(Tile.TextSize.BIGGER);
            airTempTile.setValue(0);
    }

    public void initializeTrackTempTile() {

        trackTempTile.setSkinType(Tile.SkinType.GAUGE2);
        trackTempTile.setTitle("Track temperature");
        trackTempTile.setUnit("°C");
        trackTempTile.setGradientStops(new Stop(0, Tile.BLUE),
                new Stop(0.25, Tile.GREEN),
                new Stop(0.5, Tile.YELLOW),
                new Stop(0.75, Tile.ORANGE),
                new Stop(1, Tile.RED));
        trackTempTile.setMinValue(0);
        trackTempTile.setMaxValue(60);
        trackTempTile.setStrokeWithGradient(true);
        trackTempTile.setAnimated(true);
        trackTempTile.setTextSize(Tile.TextSize.BIGGER);
        trackTempTile.setValue(0);

    }

    private void initializeTableColumns() {
        track.setCellValueFactory(new PropertyValueFactory<>("track"));
        temperature.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        humidity.setCellValueFactory(new PropertyValueFactory<>("humidity"));
        pressure.setCellValueFactory(new PropertyValueFactory<>("pressure"));
        wind.setCellValueFactory(new PropertyValueFactory<>("wind"));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        initializeWindDirectionTile();
        initializeWindSpeedTile();
        initializeAirTempTile();
        initializeTrackTempTile();

        // Dodanie opcji do ComboBoxa
        comboBoxWeather.getItems().addAll("Average", "Minimum", "Maximum", "SD", "Outliers");
        comboBoxWeather.getSelectionModel().selectFirst();

        initializeTableColumns();

        new Thread(() -> {
            while (true) {
                try {
                    // Aktualizuj UI na JavaFX Application Thread
                    Platform.runLater(this::updateTiles);

                    // Odczekaj pewien czas przed ponownym sprawdzeniem
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(() -> {
            while (true) {
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

    public F1DataStorage getDataStorage() {
        return dataStorage;
    }

    public void setMainScenePrev(Scene mainScenePrev) {
        this.mainScenePrev = mainScenePrev;
    }

    public static class WeatherRowData {

        private SimpleDoubleProperty track;
        private SimpleDoubleProperty temperature;
        private SimpleDoubleProperty humidity;
        private SimpleDoubleProperty pressure;
        private SimpleDoubleProperty wind;

        public WeatherRowData(double track, double temperature, double humidity, double pressure, double wind) {
            this.track = new SimpleDoubleProperty(Math.round(track * 100.0) / 100.0);
            this.temperature = new SimpleDoubleProperty(Math.round(temperature * 100.0) / 100.0);
            this.humidity = new SimpleDoubleProperty(Math.round(humidity * 100.0) / 100.0);
            this.pressure = new SimpleDoubleProperty(Math.round(pressure * 100.0) / 100.0);
            this.wind = new SimpleDoubleProperty(Math.round(wind * 100.0) / 100.0);
        }

        public double getTrack() {
            return track.get();
        }

        public double getTemperature() {
            return temperature.get();
        }

        public double getHumidity() {
            return humidity.get();
        }


        public double getPressure() {
            return pressure.get();
        }


        public double getWind() {
            return wind.get();
        }

    }
}
