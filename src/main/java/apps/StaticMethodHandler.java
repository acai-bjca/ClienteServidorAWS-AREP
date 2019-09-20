/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author estudiante
 */
public class StaticMethodHandler implements Handler{
    public Method method;          

    StaticMethodHandler(Method method) {
        this.method = method;
    }    
    
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
