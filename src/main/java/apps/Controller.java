package apps;

import java.io.IOException;

/**
 * Hello world!
 *
 */
public class Controller 
{
    public static HttpServer httpServer;
    
    public static void main( String[] args ) throws IOException
    {
        httpServer.listen();
    }
}
