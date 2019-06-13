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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import static javax.management.remote.JMXConnectorFactory.connect;

/**
 *
 * @author sergio
 */
public class HTTPserver implements Runnable {

    static final File WEB_ROOT = new File("/home/sergio/NetBeansProjects/NPAWprueba/NPAWprueba/src/xml");
    //static final File WEB_ROOT = new File("/home/sergio/NetBeansProjects/NPAWprueba/NPAWprueba/src/html");
    static final String DEFAULT_FILE = "response.xml";
    //static final String DEFAULT_FILE = "index.html";
    
    static final String FILE_NOT_FOUND = "404.html";
    static final String METHOD_NOT_SUPPORTED = "not_supported.html";

    // port to listen connection
    static final int PORT = 8080;

    // verbose mode
    static final boolean verbose = true;

    // Client Connection via Socket Class
    private Socket clientSocket;
    
    private CreateXMLFileJava xmlWritter;

    public HTTPserver(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public static void main(String[] args) {

        try {
            ServerSocket serverConnect = new ServerSocket(PORT);
            System.out.println("Server started.\nListening for connections on port : " + PORT + " ...\n");

            // we listen until user halts server execution
            while (true) {
                HTTPserver myServer = new HTTPserver(serverConnect.accept());

                if (verbose) {
                    System.out.println("Connecton opened. (" + new Date() + ")");
                }

                // create dedicated thread to manage the client connection
                Thread thread = new Thread(myServer);
                thread.start();
            }

        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }

    @Override
    public void run() {

        // we manage our particular client connection
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;
        String queryType = null;

        try {
            
            //this.getQueryMap("http://dataService.com/getData?accountCode=clienteA&targetDevice=XBox&pluginVersion=3.3.1");
            
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
            
            queryType = fileRequested.split("\\?")[0];

            // we support only GET and HEAD methods, we check
            if (!method.equals("GET") && !method.equals("HEAD")) {
                if (verbose) {
                    System.out.println("501 Not Implemented : " + method + " method.");
                }

                // we return the not supported file to the client
                File file = new File(WEB_ROOT, METHOD_NOT_SUPPORTED);
                int fileLength = (int) file.length();
                String contentMimeType = "text/html";
                //read content to return to client
                byte[] fileData = readFileData(file, fileLength);

                // we send HTTP Headers with data to client
                out.println("HTTP/1.1 501 Not Implemented");
                out.println("Server: Java HTTP Server from SSaurel : 1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + contentMimeType);
                out.println("Content-length: " + fileLength);
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
                // file
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();

            } else {
                
                if(queryType.equalsIgnoreCase("/getData")){                                        
                    Map map = getParamsFromQuery(input);
                    //TODO read data from db
                    
                    //TODO cluster logic
                    CreateXMLFileJava.generateXMLResponse("clusterA.com", "10");                    
                }
                
                
                // GET or HEAD method
               // if (fileRequested.endsWith("/")) {
                    //fileRequested += DEFAULT_FILE;
                //}

                fileRequested = DEFAULT_FILE;
                
                File file = new File(WEB_ROOT, fileRequested);
                int fileLength = (int) file.length();
                String content = getContentType(fileRequested);

                if (method.equals("GET") && queryType.equalsIgnoreCase("/getData")) { // GET method so we return content
                    byte[] fileData = readFileData(file, fileLength);                                       

                    // send HTTP Headers
                    out.println("HTTP/1.1 200 OK");
                    out.println("Server: Java HTTP Server from Sergio : 1.0");
                    out.println("Date: " + new Date());
                    out.println("Content-type: " + content);
                    out.println("Content-length: " + fileLength);
                    out.println(); // blank line between headers and content, very important !
                    out.flush(); // flush character output stream buffer

                    dataOut.write(fileData, 0, fileLength);
                    dataOut.flush();
                }

                if (verbose) {
                    System.out.println("File " + fileRequested + " of type " + content + " returned");
                }
            }

        } catch (FileNotFoundException fnfe) {
            try {
                fileNotFound(out, dataOut, fileRequested);
            } catch (IOException ioe) {
                System.err.println("Error with file not found exception : " + ioe.getMessage());
            }

        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                clientSocket.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }

            if (verbose) {
                System.out.println("Connection closed.\n");
            }
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

    // return supported MIME Types
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html")) {
            return "text/html";
        } else {
            return "text/plain";
        }
    }

    private void fileNotFound(PrintWriter out, OutputStream dataOut, String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);

        out.println("HTTP/1.1 404 File Not Found");
        out.println("Server: Java HTTP Server from SSaurel : 1.0");
        out.println("Date: " + new Date());
        out.println("Content-type: " + content);
        out.println("Content-length: " + fileLength);
        out.println(); // blank line between headers and content, very important !
        out.flush(); // flush character output stream buffer

        dataOut.write(fileData, 0, fileLength);
        dataOut.flush();

        if (verbose) {
            System.out.println("File " + fileRequested + " not found");
        }
    }


    public Map<String, String> getParamsFromQuery(String query) {        
        String[] params = query.split("\\?")[1].split("&");
        Map<String, String> map = new HashMap<String, String>();
        for (String param : params) {
            String name = param.split("=")[0];
            String value = param.split("=")[1];
            map.put(name, value);
        }
          return map;      
    }

}
