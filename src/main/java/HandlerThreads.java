
import java.net.Socket;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author amalia
 */
class HandlerThreads implements Runnable {

    private final Socket serverSocket;

    HandlerThreads(Socket socket) {
        this.serverSocket = socket;
    }

    public void run() {
        // read and service request on socket
    }
}
