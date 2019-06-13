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
            //Old response
            /*
            inputToServer = clientSocket.getInputStream();
            outputFromServer = clientSocket.getOutputStream();
            PrintWriter serverPrintOut = new PrintWriter(new OutputStreamWriter(outputFromServer, "UTF-8"), true);
            serverPrintOut.println("HTTP/1.1 200 OK");           
             */

            // we read characters from the client via input stream on the socket
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // we get character output stream to client (for headers)
            out = new PrintWriter(clientSocket.getOutputStream());
            // get binary output stream to client (for requested data)
            dataOut = new BufferedOutputStream(clientSocket.getOutputStream());

            // get first line of the request from the client
            String input = in.readLine();
            // we parse the request with a string tokenizer
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client

            // we get file requested
            fileRequested = parse.nextToken().toLowerCase();

            //Petición GET
            if (fileRequested.endsWith("/")) {
                //Devuelve el archivo por defecto si la peticion solo tiene "/"
                fileRequested += DEFAULT_FILE_PATH;
            }

            //Crea el archivo en WEB_ROOT que se le devolverá al cliente
            File file = new File(WEB_ROOT, fileRequested);
            int fileLength = (int) file.length();
            String content = getContentType(fileRequested);

            if (method.equals("GET")) { // GET method so we return content
                byte[] fileData = readFileData(file, fileLength);

                // send HTTP Headers
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Java HTTP Server");
                out.println("Date: " + new Date());
                out.println("Content-type: " + content);
                out.println("Content-length: " + fileLength);
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer

                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
            }

            System.out.println("File " + fileRequested + " of type " + content + " returned");
            
            

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

    // return supported MIME Types
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        } else {
            return "text/plain";
        }
    }

    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];

        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null) {
                fileIn.close();
            }
        }

        return fileData;
    }
}
