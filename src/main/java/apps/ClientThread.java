package apps;

import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.StringTokenizer;
import javax.imageio.ImageIO;
import net.sf.image4j.codec.ico.ICODecoder;
import net.sf.image4j.codec.ico.ICOEncoder;

/**
 *
 * @author amalia
 */
class ClientThread implements Runnable {

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private BufferedOutputStream salidaDatos;
    private String RUTA_RESOURCES = "src/main/resources";
    private Service service;

    ClientThread(Socket clientSocket, Service service) {
        this.clientSocket = clientSocket;
        this.service = service;
    }

    public void run() {
        try {
            System.out.println("Listo para recibir. Escuchando puerto ");
            while (!this.clientSocket.isClosed()) {
                // El in y el out son para el flujo de datos por el socket (streams).
                this.out = new PrintWriter(new OutputStreamWriter(this.clientSocket.getOutputStream()),
                        true);
                this.in = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
                this.salidaDatos = new BufferedOutputStream(this.clientSocket.getOutputStream()); // Muestra los datos respuesta al cliente.
                processRequest();
            }
            this.out.close();
            this.in.close();
            this.clientSocket.close();

        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }
    }

    /**
     * processRequest Lee la solicitud recibida del cliente en una cadena
     *
     * @throws IOException Posible excepcion al leer la solicitud del cliente
     */
    public void processRequest() throws IOException {
        String inputLine, solicitud = "";
        while ((inputLine = this.in.readLine()) != null) {
            System.out.println("Received: " + inputLine);
            if (inputLine.contains("GET")) {
                solicitud = inputLine; // Lee la primera linea de la solicitud
                System.out.println("Solicitud: " + solicitud); //Ejemplo: GET / HTTP/1.1
                readRequest(solicitud);
            }
            if (!this.in.ready()) { //Ready devuelve verdadero si la secuencia estÃ¡ lista para ser leÃ­da.
                break;
            }
        }
        this.out.close();
        this.salidaDatos.flush();
    }

    /**
     * readRequest Descompone en tockens una solicitud, para obtener el método y
     * requestURI (identificador recurso, ejemplo: /apps/archivo.html.).
     * Determina que tipo de búsqueda hacer (archivos dinámicos o estáticos)
     *
     * @param solicitud Solicitud obtenida de la peticion del cliente
     * @throws IOException Excepcion generada al buscar archivos
     */
    public void readRequest(String solicitud) throws IOException {
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

    /**
     * searchFilesInApps Busca archivos en la aplicación en sus servicios
     *
     * @param archivo nombre del archivo o url a buscar
     * @throws IOException Excepcion generda al no encontrar el archivo
     */
    public void searchFilesInApps(String archivo) throws IOException {
        System.out.println("BUSCANDO ARCHIVOS EN APPS");
        boolean useParam = false;
        String parametro = "";
        if (archivo.contains("?")) {
            parametro = archivo.substring(archivo.indexOf("=") + 1);
            archivo = archivo.substring(0, archivo.indexOf("?"));
            if (!parametro.equals("")) {
                useParam = true;
            }
        }
        try {
            String response = service.search(archivo, useParam, parametro);
            this.out.println("HTTP/1.1 200 OK\r");
            this.out.println("Content-Type: text/html\r");
            this.out.println("\r");
            this.out.println(response + "\r");
        } catch (ExceptionServer ex) {
            responseNotFound();
        }
    }

    /**
     * searchFilesInStaticResources Busca archivos estáticos en los recursos de
     * la aplicación
     *
     * @param archivo nombre de la archivo estático
     * @throws IOException Excepcion generada al no encontrar el archivo
     */
    public void searchFilesInStaticResources(String archivo) throws IOException {
        System.out.println("SIguibuscando la imagen not found " + archivo);
        BufferedReader br = null;
        if (archivo.equals("/")) {
            archivo = "/index.html";
        }
        String path = RUTA_RESOURCES + archivo;
        try {
            br = new BufferedReader(new FileReader(path));
            this.out.println("HTTP/1.1 202 Ok\r");
        } catch (Exception e) {
            responseNotFound();
        }
        if (archivo.contains("jpg")) {
            responseImage(archivo);
        } else if (archivo.contains("html")) {
            responseFileHtml(archivo);
        } else if (archivo.contains("favicon.ico")) {
            responseFavicon(archivo);
        }
    }

    /**
     * responseImagen Responde a peticiones de archivos estáticos (tipo jpg) al
     * cliente con el recurso encontrado
     *
     * @param archivo nombre de la imágen
     * @throws IOException Excepcion generada al no encontrar el archivo
     */
    private void responseImage(String archivo) throws IOException {
        this.out.println("Content-Type: image/jpeg\r");
        this.out.println("\r");
        BufferedImage image = ImageIO.read(new File(RUTA_RESOURCES + archivo));
        ImageIO.write(image, "JPG", clientSocket.getOutputStream());
    }

    /**
     * responseArchivoHtml Responde a peticiones de archivos estáticos (tipo html) al
     * cliente con el recurso encontrado
     *
     * @param archivo nombre de la imágen
     * @throws IOException Excepcion generada al no encontrar el archivo
     */
    private void responseFileHtml(String archivo) throws IOException {
        System.out.println("esta haciendo otro pasooooooooooooo");
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + archivo))) {
            String infile = null;
            while ((infile = reader.readLine()) != null) {
                sb.append(infile);
            }
        }
        this.out.println("Content-Type: text/html\r");
        this.out.println("\r");
        this.out.println(sb.toString());
    }

    /**
     * responseFavicon Responde a peticiones de archivos estáticos (tipo favicon) al
     * cliente con el recurso encontrado
     *
     * @param archivo nombre de la imágen
     * @throws IOException Excepcion generada al no encontrar el archivo
     */
    private void responseFavicon(String archivo) throws IOException {
        System.out.println("Solicitud de Favicon");
        this.out.println("Content-Type: image/x-icon\r");
        this.out.println("\r");
        List<BufferedImage> images = ICODecoder.read(new File(RUTA_RESOURCES + archivo));
        ICOEncoder.write(images.get(0), clientSocket.getOutputStream());
    }

    /**
     * responseFavicon Responde a peticiones de archivos estáticos no encontrados
     * @throws IOException Excepcion generada al no encontrar el archivo
     */
    private void responseNotFound() throws IOException {
        System.out.println("No lo encontro");
        StringBuffer sb = new StringBuffer();
        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_RESOURCES + "/notFound.html"))) {
            String infile = null;
            while ((infile = reader.readLine()) != null) {
                sb.append(infile);
            }
        }
        this.out.println("HTTP/1.1 404 Not Found\r");
        this.out.println("Content-Type: text/html\r");
        this.out.println("\r");
        this.out.println(sb.toString());
    }
}
