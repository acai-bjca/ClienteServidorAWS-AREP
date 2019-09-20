package apps;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Controller 
{
    public static HttpServer httpServer;
    private static final int POOLSIZE = 50;
    
    public static void main( String[] args ) throws IOException
    {        
        httpServer = new HttpServer(POOLSIZE);
        httpServer.listen();
    }
}
