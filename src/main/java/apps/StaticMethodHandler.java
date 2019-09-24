/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * StaticMethodHandler Maneja e invoca m�todos de la aplicaci�n
 * @author Amalia
 */
public class StaticMethodHandler implements Handler{
    public Method method;          

    StaticMethodHandler(Method method) {
        this.method = method;
    }    
    
    /**
     * Invoca al m�todo para obtener un rsultado
     * @return String con la respuesta del m�todo
     * @throws ExceptionServer Ocurre cuando el m�todo no puede ser invocado
     */
    public String process()throws ExceptionServer{
        String answer = "";
        try {
            answer = method.invoke(null, null).toString();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        }catch (IllegalArgumentException ex) {
            throw new ExceptionServer(ExceptionServer.METHOD_NOTPARAMS);
        }
        return answer;
    }    
    
    /**
     * Invoca al m�todo para obtener un rsultado dado un par�metro
     * @param num cadena usada para dar respuesta
     * @return String con la respuesta del m�todo
     * @throws ExceptionServer Ocurre cuando el m�todo no puede ser invocado
     */
    public String process(String num) throws ExceptionServer{
        String answer = "";
        try {          
            answer = method.invoke(null, num).toString();
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
        } catch (IllegalArgumentException ex) {
            throw new ExceptionServer(ExceptionServer.METHOD_PARAMS);
        }
        return answer;
    }
    
}
