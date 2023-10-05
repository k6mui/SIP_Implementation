/*
 * Código de base para parsear mensajes SIP
 * Puede ser adaptado, ampliado, modificado por el alumno
 * según sus necesidades para la práctica
 */
package mensajesSIP;

/**
 *
 * @author SMA
 */
public class SIPException extends Exception{
    
    public SIPException(String message){
        super(message);
    }

    public SIPException() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
