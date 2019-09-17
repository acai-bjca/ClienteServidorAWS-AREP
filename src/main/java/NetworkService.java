import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

 
class NetworkService implements Runnable {
    private final ServerSocket serverSocket;
    // Iniciamos un pool de threads.    
    private final ExecutorService pool;

    public NetworkService(int port, int poolSize) throws IOException {
        serverSocket = new ServerSocket(port);
        pool = Executors.newFixedThreadPool(poolSize); //Excecutors a través de ella podemos obtener una serie de implementaciones estándar.
    }

    public void run() { // run the service
        try {
            for (;;) {
                // Ejecutamos un thread del pool.
                pool.execute(new HandlerThreads(serverSocket.accept())); //realiza la ejecución de un hilo, recibe un objeto que implemente la interface Runnable en la cual se define cual es el proceso a ejecutar.
            }
        } catch (IOException ex) {
            // Finalizamos el pool.
            pool.shutdown();
        }
    }
    
    //Excecutors es la clase proveedora de ExecutorServices, a través de ella podemos obtener una serie de implementaciones estándar.
    /*newFixedThreadPool: Esta implementación tiene las siguientes características:
    Crea un pool de hilo de ejecuciones con un tamaño fijo.
    Si se trata de ejecutar una tarea nueva cuando todos los hilos de ejecución están trabajando, este último debe esperar.
    Si algún hilo muere por una falla durante su ejecución, uno nuevo será creado en el pool cuando sea solicitado.*/
 }

    
 