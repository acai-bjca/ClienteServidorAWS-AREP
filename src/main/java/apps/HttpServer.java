package apps;

import java.net.*;
import java.util.Date;
import java.util.StringTokenizer;
import java.io.*;

public class HttpServer{
    private static final int PUERTO = getPort();
    private static Service service;
    
    public static void listen() throws IOException {
        service = new Service();
        service.init();
        while (true) {
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PUERTO);
            } catch (IOException e) {
                System.err.println("Could not listen on port: " + PUERTO + ".");
                System.exit(1);
            }

            Socket clientSocket = null;
            PrintWriter out = null;
            BufferedReader in = null;
            BufferedOutputStream salidaDatos = null;
            //Recibir multiples solicitudes no concurrentes
            try {
                System.out.println("Listo para recibir. Escuchando puerto " + PUERTO);
                clientSocket = serverSocket.accept();
                while (!clientSocket.isClosed()) {
                    // El in y el out son para el flujo de datos por el socket (streams).
                    out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()),
                            true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    salidaDatos = new BufferedOutputStream(clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.
                    service.processRequest(clientSocket, out, in, salidaDatos);
                }
                out.close();
                in.close();
                clientSocket.close();
                serverSocket.close();

            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }
        }
    }
    
    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e.on localhost)
    }
    
}
