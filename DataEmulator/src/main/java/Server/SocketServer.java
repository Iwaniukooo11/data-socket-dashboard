package Server;

import api.DataHandler;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class SocketServer {
    private DataHandler dh;

    public void start(int port) throws IOException, ExecutionException, InterruptedException {
        this.dh = new DataHandler();
        System.out.println("SocketServer.start()");
        ServerSocket serverSocket = new ServerSocket(port);

        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> {
                    try {
                        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                        while (!clientSocket.isClosed()) {
                            // Get time difference from DataHandler
                            long timeDifference = this.dh.getTimeDifference(); // assuming this method exists

                            // Wait for the specified duration
                            Thread.sleep(timeDifference);

                            // Send the response
                            String response = this.dh.getNextElement(); // assuming this method exists
                            out.println(response);
                            System.out.println("SocketServer.start() -> response = " + response);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + port);
            System.out.println(e.getMessage());
        } finally {
            serverSocket.close();
        }
    }
}
