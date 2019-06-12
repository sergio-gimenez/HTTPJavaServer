/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sergio.npawprueba;

import java.io.IOException;
import java.net.ServerSocket;

/**
 *
 * @author sergio
 */
public class MainServer {

    private static final int PORT_NUMBER = 5000;
    private static ServerSocket serverSocket;
    private static ClientHandler clientHandler;

    public static void main(String[] args) throws IOException {
        serverSocket = new ServerSocket(PORT_NUMBER);

        while (true) {
            clientHandler = new ClientHandler(serverSocket.accept());
            new Thread(clientHandler).start();
        }
    }

    protected void finalize() throws IOException {
        serverSocket.close();
    }
}
