package wut.f1raceapp.sockets;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import wut.f1raceapp.dataModel.DriverData;
import wut.f1raceapp.dataModel.PositionData;
import wut.f1raceapp.dataModel.RaceControlData;
import wut.f1raceapp.dataModel.WeatherData;
import wut.f1raceapp.utils.F1DataProcessor;
import wut.f1raceapp.utils.F1DataStorage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Objects;

public class SocketClient extends Thread {

    private F1DataStorage dataStorage;
    private int port;
    private boolean isRunning = true;
    private ObjectMapper objectMapper = new ObjectMapper();

    public SocketClient(F1DataStorage dataStorage, int port) {
        this.dataStorage = dataStorage;
        this.port = port;
    }

    @Override
    public void run() {
        try (Socket socket = new Socket("localhost", port);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            String serverResponse;
            while ((serverResponse = reader.readLine()) != null && isRunning) {
                processAndAddData(serverResponse);
            }
            this.isRunning = false;
        }
        catch (IOException e) {
            System.err.println("Couldn't connect to the server");
            e.printStackTrace();
        }
    }

    public void stopClient() {
        isRunning = false;
    }

    private void processAndAddData(String jsonData) {
        // Sprawdzenie, czy odpowiedź od serwera nie jest pusta
        if(jsonData.equals("") || jsonData.equals("empty")) {
            System.out.println("Empty response from server");
            dataStorage.incrementFaultyDataCounter();
            return;
        }
        try {
            // Deserializacja JSONa do obiektu JsonNode
            JsonNode jsonNode = objectMapper.readTree(jsonData);

            // Pobranie wartości pola "type"
            String type = jsonNode.get("type").asText();

            if ("car_data".equals(type)) {
                System.out.println("Read data type: " + type);
                // Obsługa, jeżeli type to "car_data"

                DriverData driverData = F1DataProcessor.parseJson(jsonData);
                if (driverData != null) {
                    dataStorage.addData(driverData);
                }
            }
            else if ("weather".equals(type)) {
                System.out.println("Read data type: " + type);
                // Obsługa, jeżeli type to "weather"

                WeatherData weatherData = F1DataProcessor.parseJsonWeather(jsonData);
                if (weatherData != null) {
                    dataStorage.addWeather(weatherData);
                }

            }
            else if ("position".equals(type)){
                System.out.println("Read data type: " + type);
                // Obsługa, jeżeli type to "position"

                PositionData positionData = F1DataProcessor.parseJsonPosition(jsonData);
                if (positionData != null) {
                    dataStorage.addPosition(positionData);
                }

            }
            else if("race_control".equals(type)){
                System.out.println("Read data type: " + type);
                // Obsługa, jeżeli type to race_control

                RaceControlData raceControlData = F1DataProcessor.parseJsonRaceControl(jsonData);
                if (raceControlData != null) {
                    dataStorage.addRaceControl(raceControlData);
                }

            }
            else if("pit".equals(type)){
                System.out.println("Read data type: " + type);
                // Obsługa, jeżeli type to pit

            }
            else {
                System.out.println("Unknown type: " + type);
            }
        } catch (JsonProcessingException e) {
            System.err.println("Couldn't parse JSON");
            e.printStackTrace();
        }
    }

    public boolean isRunning() {
        return isRunning;
    }
}

