import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello and welcome!");

        for (int i = 1; i <= 5; i++) {
            System.out.println("i = " + i);
        }

        Socket socket = new Socket("localhost", 8080);

        try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            // Continuously read messages from the server
            String serverResponse;
            while ((serverResponse = input.readLine()) != null) {
                System.out.println("Server says: " + serverResponse);
            }
        } catch (IOException e) {
            System.err.println("Couldn't connect to the server");
            e.printStackTrace();
        }

    }
}
