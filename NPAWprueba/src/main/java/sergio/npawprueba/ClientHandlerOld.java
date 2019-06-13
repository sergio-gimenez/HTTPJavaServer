/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sergio.npawprueba;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.net.Socket;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sergio
 */
public class ClientHandlerOld implements Runnable {

    private Socket clientSocket;
    static final String DEFAULT_FILE_PATH = "index.html";
    static final String WEB_ROOT = ".";

    ClientHandlerOld(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void run() {

        // we manage our particular client connection
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;

        InputStream inputToServer = null;
        OutputStream outputFromServer = null;

        try {

            inputToServer = clientSocket.getInputStream();
            outputFromServer = clientSocket.getOutputStream();
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);
            serverPrintOut.println("HTTP/1.1 200 OK");

        } catch (IOException ex) {
            Logger.getLogger(ClientHandlerOld.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (inputToServer != null) {
                    inputToServer.close();
                }
                if (outputFromServer != null) {
                    outputFromServer.close();
                }
                clientSocket.close();

            } catch (IOException ex) {
                Logger.getLogger(ClientHandlerOld.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
