/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

import java.io.*;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contiene los servicios de la aplicaci�n. Busca y almacena los m�todos para
 * poder manejarlos y dar la respuesta generada por cada uno Contiene
 *
 * @author Amalia
 */
public class Service {

    public static ConcurrentHashMap<String, Handler> urlsHandler = new ConcurrentHashMap<String, Handler>();
    private static String RUTA_RESOURCES = "src/main/resources";

    /**
     * Inicia la aplicaci�n, buscando todas las clases ubicadas en la ra�z del
     * proyecto, para despu�s alamcenar sus m�todos
     */
    public static void init() {
        String ruta = "src/main/java";
        File directorio = new File(ruta);
        File[] ficheros = directorio.listFiles();
        for (int f = 0; f < ficheros.length; f++) {
            if (!ficheros[f].isDirectory()) {
                String className = ficheros[f].getName();
                className = className.substring(0, className.indexOf("."));
                addMethod(className);
            }
        }
    }

    /**
     * Almacena en un hashMap concurrente los m�todos procesados de una clase
     * dado su nombre. En la llave se almacena el nombre o url del m�todo
     * (apps/metodo). En el valor, se guarda un objeto StaticMethodHandler dado
     * el m�todo
     *
     * @param className Nombre de clase de la que se obtendr�n los m�todos
     */
    public static void addMethod(String className) {
        Class<?> clase;
        try {
            clase = Class.forName(className);
            Method[] methods = clase.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Web.class)) {
                    Class[] argTypes = new Class[]{String[].class};
                    System.out.println("Metodo guardado: " + method.getName());
                    System.out.println("Nombre  a guradar en handler: apps/" + method.getAnnotation(Web.class).value());
                    urlsHandler.put("/apps/" + method.getAnnotation(Web.class).value(), new StaticMethodHandler(method));
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * search Dado el nombre de busqueda o url (m�todo), y par�metros
     * dependiendo la petici�n, retorna la respuesta generada por el m�todo
     * llamado, en caso de encontrarse en el manejador de metodos. De lo
     * contrario se genera una expeci�n.
     *
     * @param archivo url o nombre de b�squeda
     * @param useParam Determina si se detect� un par�emtro en el request URI
     * @param parametro En caso de detectarse par�metro, se recibe una cadena no vac�a
     * @return Repuesta generada por el m�todo o url a buscar
     * @throws ExceptionServer
     */
    public static String search(String archivo, boolean useParam, String parametro) throws ExceptionServer {
        String response = "";
        if (urlsHandler.containsKey(archivo)) {
            if (useParam) {
                response = urlsHandler.get(archivo).process(parametro);
            } else {
                System.out.println("RTA sin parametro: " + urlsHandler.get(archivo).process());
                response = urlsHandler.get(archivo).process();
            }
        } else {
            throw new ExceptionServer(ExceptionServer.NOTFOUND_APPS);
        }
        return response;
    }
}
