package apps;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

class HttpServer {

    private static ServerSocket serverSocket;
    private static final int PUERTO = getPort();
    private static Service service;
    // Iniciamos un pool de threads.    
    private final ExecutorService pool;

    /**
     * Inicializa la aplicaci�n creando un socket que escucha por un puerto
     * esp�cifico y crea el pool de hilos para peticiones concurrentes
     *
     * @param poolSize tama�o del pool
     * @throws IOException Excepcion que peude ocurrir al crear el sochet
     */
    public HttpServer(int poolSize) throws IOException {
        service = new Service();
        serverSocket = new ServerSocket(PUERTO);
        service.init();
        pool = Executors.newFixedThreadPool(poolSize); //Excecutors a través de ella podemos obtener una serie de implementaciones estándar.
    }

    /**
     * Escucha las peticiones de clietnes que se conentec al servidor, una vez
     * iniciada la aplicaci�n. Cada vez que detecta una conexi�n inicia un
     * socket de cliente y ejecuta un hilo para cada conexion
     */
    public void listen() { // run the service
        try {
            while (true) {
                // Ejecutamos un thread del pool.
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientThread(clientSocket, service)); //realiza la ejecución de un hilo, recibe un objeto que implemente la interface Runnable en la cual se define cual es el proceso a ejecutar.
            }
        } catch (IOException ex) {
            // Finalizamos el pool.
            pool.shutdown();
            //shutdownAndAwaitTermination(pool);
            try {
                serverSocket.close();
            } catch (IOException ex1) {
                ex1.printStackTrace();
            }
        }
    }

    /**
     * shutdownAndAwaitTermination Cierra el pool en dos fases, primero llamando
     * a shutdown para rechazar las tareas entrantes y luego llamando a
     * shutdownNow , si es necesario, para cancelar cualquier tarea persistente
     *
     * @param pool Excecutors apra tener un pool de hijos en ejecucion
     */
    public void shutdownAndAwaitTermination(ExecutorService pool) {
        pool.shutdown(); // Desactiva el envío de nuevas tareas 
        try {
            // Espera un momento a que finalicen las tareas existentes 
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancelar las tareas actualmente en ejecución 
                // Espere un momento a que las tareas respondan a la cancelación
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-) Cancelar si el hilo actual también interrumpió 
            pool.shutdownNow();
            // Preservar el estado de interrupción
            Thread.currentThread().interrupt();
        }

        try {
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(HttpServer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567; //returns default port if heroku-port isn't set (i.e.on localhost)
    }

    //Excecutors es la clase proveedora de ExecutorServices, a través de ella podemos obtener una serie de implementaciones estándar.
    /*newFixedThreadPool: Esta implementación tiene las siguientes características:
    Crea un pool de hilo de ejecuciones con un tamaño fijo.
    Si se trata de ejecutar una tarea nueva cuando todos los hilos de ejecución están trabajando, este último debe esperar.
    Si algún hilo muere por una falla durante su ejecución, uno nuevo será creado en el pool cuando sea solicitado.*/
}
