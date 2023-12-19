package api;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

public class DataHandler {
    private JSONArray dataStack;
    private Queue<JSONObject> dataQueue1;
    private Queue<JSONObject> dataQueue2;
    private Queue<JSONObject> dataQueue3;
    private Queue<JSONObject> dataQueue4;
    private Queue<JSONObject> dataQueue5;
    private long timeDifference;
    private LocalDateTime currentTime;

    private static String fetchData(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        con.disconnect();

        return content.toString();
    }
    private static Queue<JSONObject> jsonArrayToQueue(JSONArray jsonArray) {
        Queue<JSONObject> queue = new LinkedList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            queue.add(jsonArray.getJSONObject(i));
        }
        return queue;
    }

    public DataHandler() throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5); // Pool of 3 threads

        Callable<String> task1 = () -> fetchData("https://api.openf1.org/v1/car_data?driver_number=40&meeting_key=1219");
        Callable<String> task2 = () -> fetchData("https://api.openf1.org/v1/pit?meeting_key=1219&driver_number=40");
        Callable<String> task3 = () -> fetchData("https://api.openf1.org/v1/position?meeting_key=1219&driver_number=40");
        Callable<String> task4 = () -> fetchData("https://api.openf1.org/v1/race_control?meeting_key=1219");
        Callable<String> task5 = () -> fetchData("https://api.openf1.org/v1/weather?meeting_key=1219");


        Future<String> result1 = executor.submit(task1);
        Future<String> result2 = executor.submit(task2);
        Future<String> result3 = executor.submit(task3);
        Future<String> result4 = executor.submit(task4);
        Future<String> result5 = executor.submit(task5);

        // Process the results
        String data1 = result1.get();
        String data2 = result2.get();
        String data3 = result3.get();
        String data4 = result4.get();
        String data5 = result5.get();

        // Assuming data is JSON and needs to be parsed
        this.dataQueue1 = jsonArrayToQueue(new JSONArray(data1));
        this.dataQueue2 = jsonArrayToQueue(new JSONArray(data2));
        this.dataQueue3 = jsonArrayToQueue(new JSONArray(data3));
        this.dataQueue4 = jsonArrayToQueue(new JSONArray(data4));
        this.dataQueue5 = jsonArrayToQueue(new JSONArray(data5));


        // Do something with the data
        JSONObject removedElement = this.dataQueue1.poll();
        assert removedElement != null;
        this.currentTime=LocalDateTime.parse(removedElement.getString("date"), DateTimeFormatter.ISO_DATE_TIME);
        this.timeDifference=0;


        System.out.println("Removed Element: " + removedElement);

        executor.shutdown(); // Always remember to shut down the executor

        }
        public String getNextElement() {
            String response="";
            JSONObject firstElement = dataQueue1.poll();
            assert firstElement != null;
            firstElement.put("type", "car_data");
            LocalDateTime time = LocalDateTime.parse(firstElement.getString("date"), DateTimeFormatter.ISO_DATE_TIME);
            JSONObject pitElement = this.dataQueue2.peek();
            JSONObject positionElement = this.dataQueue3.peek();
            JSONObject raceControlElement = this.dataQueue4.peek();
            JSONObject weatherElement = this.dataQueue5.peek();

            //add to socket firstElement
            response+=firstElement.toString()+"\n";

            if(pitElement!=null &&  LocalDateTime.parse(pitElement.getString("date"), DateTimeFormatter.ISO_DATE_TIME).isBefore(time)){
                pitElement.put("type", "pit");
                response+=pitElement.toString()+"\n";
                this.dataQueue2.poll();
            }
            if(positionElement!=null && LocalDateTime.parse(positionElement.getString("date"), DateTimeFormatter.ISO_DATE_TIME).isBefore(time)){
                positionElement.put("type", "position");
                response+=positionElement.toString()+"\n";
                this.dataQueue3.poll();
            }
            if(raceControlElement!=null && LocalDateTime.parse(raceControlElement.getString("date"), DateTimeFormatter.ISO_DATE_TIME).isBefore(time)){
                raceControlElement.put("type", "race_control");
                response+=raceControlElement.toString()+"\n";
                this.dataQueue4.poll();
            }
            if(weatherElement!=null && LocalDateTime.parse(weatherElement.getString("date"), DateTimeFormatter.ISO_DATE_TIME).isBefore(time)){
                weatherElement.put("type", "weather");
                response+=weatherElement.toString()+"\n";
                this.dataQueue5.poll();
            }
            this.timeDifference= Duration.between(this.currentTime,time).toMillis();
            this.currentTime=time;
            double randomNumber = Math.random();
            if(randomNumber<0.01){
                return "empty";
            }
            else{
                return response;
            }


            //if time in pit,posiiton,racecontrol,weather AFTER less than time in firstElement, also add to docket


        }

        // a function that creates a socket and returns data
        public LocalDateTime getCurrentTime(){
        return this.currentTime;
    }
    public long getTimeDifference(){
        return this.timeDifference;
    }


}
