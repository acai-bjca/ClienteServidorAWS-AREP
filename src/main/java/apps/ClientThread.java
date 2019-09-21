package apps;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author amalia
 */
class ClientThread implements Runnable {

    private static Socket clientSocket;
    private static PrintWriter out;
    private static BufferedReader in;
    private static BufferedOutputStream salidaDatos;
    private static String RUTA_RESOURCES = "src/main/resources";
    private static Service service;

    ClientThread(Socket clientSocket, Service service) {
        this.clientSocket = clientSocket;
        this.service = service;
    }

    public void run() {
        try {
            System.out.println("Listo para recibir. Escuchando puerto ");
            while (!clientSocket.isClosed()) {
                // El in y el out son para el flujo de datos por el socket (streams).
                out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()),
                        true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                salidaDatos = new BufferedOutputStream(clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.
                processRequest();
            }
            out.close();
            in.close();
            clientSocket.close();

        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }  
    }

    public static void processRequest() throws IOException {
        String inputLine, solicitud = "";
        while ((inputLine = in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (inputLine.contains("GET")) {
                solicitud = inputLine; // Lee la primera linea de la solicitud
                System.out.println("Solicitud: " + solicitud); //Ejemplo: GET / HTTP/1.1
                readRequest(solicitud);
            }
            if (!in.ready()) { //Ready devuelve verdadero si la secuencia está lista para ser leída.
                break;
            }
        }
        out.close();
        salidaDatos.flush();
    }

    public static void readRequest(String solicitud) throws IOException {
        StringTokenizer tokens = new StringTokenizer(solicitud); // Divide la solicitud en diferentes "tokens" separados por espacio.
        String metodo = tokens.nextToken().toUpperCase(); // Obtenemos el primer token, que en este caso es el metodo de
        // la solicitud HTTP.
        String requestURI = tokens.nextToken(); // Obtenemos el segundo token: identificador recurso: /apps/archivo.tipo.

        String archivo = requestURI;
        System.out.println("archivo " + archivo);

        if (requestURI.contains("apps")) {
            searchFilesInApps(archivo);
        } else {
            searchFilesInStaticResources(archivo);
        }
    }

    public static void searchFilesInApps(String archivo) throws IOException {
        System.out.println("BUSCANDO ARCHIVOS EN APPS");
        boolean useParam = false;
        String parametro = "";
        if (archivo.contains("?")) {
            parametro = archivo.substring(archivo.indexOf("=") + 1);        
            archivo = archivo.substring(0, archivo.indexOf("?"));
            if(!parametro.equals("")) useParam = true;
        }
        System.out.println("NOMBRE ARCHIVO A BUSCAR : " + archivo);
        try {
            String response = service.search(archivo, useParam, parametro);
            System.out.println("LO ENCONTRO");
            out.println("HTTP/1.1 200 OK\r");
            out.println("Content-Type: text/html\r");
            out.println("\r");            
            out.println(response + "\r");            
        } catch (ExceptionServer ex) {
            System.out.println("Entro a excepciom");
            StringBuffer sb = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + "/notFound.html"))) {
                String infile = null;
                while ((infile = reader.readLine()) != null) {
                    sb.append(infile);
                }
            } 
            
            if(ex.getMessage().equals(ExceptionServer.NOTFOUND_APPS)){
                System.out.println("Revise si el metodo necesita parámetros.");
                out.println("HTTP/1.1 404 Not Found\r");
                out.println("Content-Type: text/html\r");
                out.println("\r");
                out.println(sb.toString() + "\r");
            } else if(ex.getMessage().equals(ExceptionServer.METHOD_NOTPARAMS) || ex.getMessage().equals(ExceptionServer.METHOD_PARAMS)){
                System.out.println("No encotnro archivo.");
                out.println("HTTP/1.1 404 Not Found\r");
                out.println("Content-Type: text/html\r");
                out.println("\r");
                out.println(sb.toString() + "\r");
            }      
        }
    }

    public static void searchFilesInStaticResources(String archivo) throws IOException {
        BufferedReader br = null;
        if (archivo.equals("/")) {
            archivo = "/index.html";
        }
        String path = RUTA_RESOURCES + archivo;
        try {
            br = new BufferedReader(new FileReader(path));
        } catch (Exception e) {
            System.out.println("No lo encontro");
            StringBuffer sb = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + "/notFound.html"))) {
                String infile = null;
                while ((infile = reader.readLine()) != null) {
                    sb.append(infile);
                }
            }
            out.println("HTTP/1.1 404 Not Found\r");
            out.println("Content-Type: text/html\r");
            out.println("\r");
            out.println(sb.toString() + "\r");
        }

        out.println("HTTP/1.1 202 Ok\r");
        if (archivo.contains("jpg")) {
            out.println("Content-Type: image/jpeg\r");
            out.println("\r");
            System.out.println("RUTAAAAAAAAAAAAAAAA: " + RUTA_RESOURCES + archivo);
            BufferedImage image = ImageIO.read(new File(RUTA_RESOURCES + archivo));
            ImageIO.write(image, "JPG", clientSocket.getOutputStream());
        } else if (archivo.contains("html")) {
            StringBuffer sb = new StringBuffer();
            try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + archivo))) {
                String infile = null;
                while ((infile = reader.readLine()) != null) {
                    sb.append(infile);
                }
            }
            out.println("Content-Type: text/html\r");
            out.println("\r");
            out.println(sb.toString());

        } else if (archivo.contains("favicon.ico")) {
            System.out.println("Solicitud de Favicon");
            out.println("Content-Type: image/x-icon\r");
            System.out.println("1");
            out.println("\r");
            List<BufferedImage> images = ICODecoder.read(new File(RUTA_RESOURCES + archivo));
            System.out.println("2");
            ICOEncoder.write(images.get(0), clientSocket.getOutputStream());
            System.out.println("3");
        }
    }

    

}
