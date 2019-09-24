/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

/**
 * ExceptionServer Contiene las posibles excepciones generadas al realizar
 * b�squedas de archivos en la aplicaci�n
 *
 * @author Amalia
 */
public class ExceptionServer extends Exception {

    public static final String NOTFOUND_APPS = "Archivo no encontrado en apps.";
    public static final String METHOD_NOTPARAMS = "El método no requiere parámetros.";
    public static final String METHOD_PARAMS = "El método requiere parámetros.";

    ExceptionServer(String message) {
        super(message);
    }

}
