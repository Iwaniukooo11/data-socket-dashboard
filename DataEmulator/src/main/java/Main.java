import Server.SocketServer;
import api.DataHandler;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException, ExecutionException {
        DataHandler dh = new DataHandler();
        for(int i = 0; i < 10; i++) {
            System.out.println(dh.getNextElement());
            System.out.println("dh.getNextElement()");

        }
//
        SocketServer socketServer = new SocketServer();
        socketServer.start(8080);
    }


}



