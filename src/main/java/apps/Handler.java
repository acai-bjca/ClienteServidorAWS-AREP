/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package apps;

/**
 *
 * @author estudiante
 */
public interface Handler {
    public String process()  throws ExceptionServer;
    public String process(String num)  throws ExceptionServer;
}
