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
     * Inicializa la aplicaci髇 creando un socket que escucha por un puerto
     * esp閏ifico y crea el pool de hilos para peticiones concurrentes
     *
     * @param poolSize tama駉 del pool
     * @throws IOException Excepcion que peude ocurrir al crear el sochet
     */
    public HttpServer(int poolSize) throws IOException {
        service = new Service();
        serverSocket = new ServerSocket(PUERTO);
        service.init();
        pool = Executors.newFixedThreadPool(poolSize); //Excecutors a trav茅s de ella podemos obtener una serie de implementaciones est谩ndar.
    }

    /**
     * Escucha las peticiones de clietnes que se conentec al servidor, una vez
     * iniciada la aplicaci髇. Cada vez que detecta una conexi髇 inicia un
     * socket de cliente y ejecuta un hilo para cada conexion
     */
    public void listen() { // run the service
        try {
            while (true) {
                // Ejecutamos un thread del pool.
                Socket clientSocket = serverSocket.accept();
                pool.execute(new ClientThread(clientSocket, service)); //realiza la ejecuci贸n de un hilo, recibe un objeto que implemente la interface Runnable en la cual se define cual es el proceso a ejecutar.
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
        pool.shutdown(); // Desactiva el env铆o de nuevas tareas 
        try {
            // Espera un momento a que finalicen las tareas existentes 
            if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                pool.shutdownNow(); // Cancelar las tareas actualmente en ejecuci贸n 
                // Espere un momento a que las tareas respondan a la cancelaci贸n
                if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
                    System.err.println("Pool did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-) Cancelar si el hilo actual tambi茅n interrumpi贸 
            pool.shutdownNow();
            // Preservar el estado de interrupci贸n
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

    //Excecutors es la clase proveedora de ExecutorServices, a trav茅s de ella podemos obtener una serie de implementaciones est谩ndar.
    /*newFixedThreadPool: Esta implementaci贸n tiene las siguientes caracter铆sticas:
    Crea un pool de hilo de ejecuciones con un tama帽o fijo.
    Si se trata de ejecutar una tarea nueva cuando todos los hilos de ejecuci贸n est谩n trabajando, este 煤ltimo debe esperar.
    Si alg煤n hilo muere por una falla durante su ejecuci贸n, uno nuevo ser谩 creado en el pool cuando sea solicitado.*/
}
