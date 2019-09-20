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
 *
 * @author estudiante
 */
public class Service {

    public static ConcurrentHashMap<String, Handler> urlsHandler = new ConcurrentHashMap<String, Handler>();   
    private static String RUTA_RESOURCES = "src/main/resources";

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

    public static void addMethod(String className) {
        Class<?> clase;
        try {
            clase = Class.forName(className);
            Method[] methods = clase.getMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(Web.class)) {
                    Class[] argTypes = new Class[] { String[].class };
                    System.out.println("Metodo guardado: "+method.getName());
                    System.out.println("Nombre  a guradar en handler: apps/" + method.getAnnotation(Web.class).value());
                    urlsHandler.put("/apps/" + method.getAnnotation(Web.class).value(), new StaticMethodHandler(method));
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Service.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String search(String archivo, boolean useParam, String parametro) throws ExceptionServer{
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
