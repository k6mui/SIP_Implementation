/*
 * C�digo de base para parsear mensajes SIP
 * Puede ser adaptado, ampliado, modificado por el alumno
 * seg�n sus necesidades para la pr�ctica
 */
package mensajesSIP;

import java.util.ArrayList;

/**
 *
 * @author SMA
 */

public class ACKMessage extends SIPMessage {

    private String destination;
    private String route;
    private int maxForwards;
    private int contentLength;

/**
 * Devuelve el contenido de las Vias como ArrayList de Strings.
 * Las Vias contienen la lista de puntos por los que va pasando el mensaje y se a�ade el �ltimo punto al inicio de las Vias.
 * El mensaje en la red tendra vias con formato: Via: SIP/2.0/UDP identificador donde el identificador podr� contener direcci�n IP o nombre de m�quina con o sin y puerto 
 * Este API guardar� solo la parte de identificador en la lista de vias y concatenar� la parte de Via: SIP/2.0/UDP al generar el mensaje en formato cadena
 *
 * @return      las Vias del mensaje
 */

    public ArrayList<String> getVias() {
        return vias;
    }

/**
 * Establece el contenido de las Vias como ArrayList de Strings 
 * Cada String contiene el contenido del punto del camino de que se ha quitado la parte de Via: SIP/2.0/UDP  
 *
 * @param  vias  las Vias a establecer como ArrayList 
  */

    public void setVias(ArrayList<String> vias) {
        this.vias = vias;
    }

/**
 * A�ade una Via como String. El API a�ade las vias en formato pila de forma que la �ltima via a�adida es la primera en quitarse 
 *
 * @param  via  la Via a a�adir
 */

    public void addVia(String via) {
        this.vias.add(0, via);
    }

/**
 * Borra la �ltima Via a�adida
 *
 */

    public void deleteVia() {
        this.vias.remove(0);
    }

/**
 * Recupera el nombre del destinatario. 
 * La direccion del destinatario tendr� formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @return      el nombre del destinatario
 */

    public String getToName() {
        return toName;
    }

/**
 * Establece el nombre del destinatario. 
 * La direccion del destinatario tendr� formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @param  toName	el nombre a a�adir
 */

    public void setToName(String toName) {
        this.toName = toName;
    }

/**
 * Recupera la Uri del destinatario. 
 * La direccion del destinatario tendr� formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @return      la Uri del destinatario
 */

    public String getToUri() {
        return toUri;
    }

/**
 * Establece la Uri del destinatario. 
 * La direccion del destinatario tendr� formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @param  toUri		la Uri del destinatario a a�adir
 */

    public void setToUri(String toUri) {
        this.toUri = toUri;
    }

/**
 * Recupera el nombre del origen. 
 * La direccion del destinatario tendr� formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @return      el nombre del origen
 */

    public String getFromName() {
        return fromName;
    }

/**
 * Establece el nombre del origen. 
 * La direccion del destinatario tendr� formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @param  fromName	el nombre del origen a a�adir
 */

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

/**
 * Recupera la Uri del origen. 
 * La direccion del destinatario tendr� formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @return      la Uri del origen
 */

    public String getFromUri() {
        return fromUri;
    }

/**
 * Establece la Uri del origen. 
 * La direccion del destinatario tendr� formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @param  fromUri		la Uri del origen a a�adir
 */

    public void setFromUri(String fromUri) {
        this.fromUri = fromUri;
    }

/**
 * Recupera el CallId de la llamada.
 * El Call Id de la llamada se forma como se especifica en la RFC 3261 "by the combination of a random string and the softphone's host name or IP address". Este API no hace comprabaciones del formato en esta linea.
 * 
 * @return      el CallId de la llamada
 */

    public String getCallId() {
        return callId;
    }

/**
 * Establece el CallId de la llamada. 
 *
 * @param	callId	 el CallId de la llamada
 */

    public void setCallId(String callId) {
        this.callId = callId;
    }

/**
 * Recupera el cSeqNumber de la llamada. En SIP el n�mero de secuencia ser� la concatenaci�n de cSeqNumber y cSeqStr
 * Para un primer mensaje INVITE por ejemplo, el n�mero de secuencia se formar�a como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 * 
 * @return      el cSeqNumber de la llamada
 */

    public String getcSeqNumber() {
        return cSeqNumber;
    }

/**
 * Establece el cSeqNumber de la llamada. En SIP el n�mero de secuencia ser� la concatenaci�n de cSeqNumber y cSeqStr
 * Para un primer mensaje INVITE por ejemplo, el n�mero de secuencia se formar�a como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 *
 * @param	cSeqNumber 	 el cSeqNumber de la llamada
 */


    public void setcSeqNumber(String cSeqNumber) {
        this.cSeqNumber = cSeqNumber;
    }

/**
 * Recupera el cSeqStr de la llamada. En SIP el n�mero de secuencia ser� la concatenaci�n de cSeqNumber y cSeqStr.
 * Para un primer mensaje INVITE por ejemplo, el n�mero de secuencia se formar�a como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 * 
 * @return      el cSeqStr de la llamada
 */

    public String getcSeqStr() {
        return cSeqStr;
    }

/**
 * Establece el cSeqStr de la llamada. En SIP el n�mero de secuencia ser� la concatenaci�n de cSeqNumber y cSeqStr.
 * Para un primer mensaje INVITE por ejemplo, el n�mero de secuencia se formar�a como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 *
 * @param	cSeqStr  el cSeqNumber de la llamada
 */

    public void setcSeqStr(String cSeqStr) {
        this.cSeqStr = cSeqStr;
    }

/**
 * Recupera el destino de la llamada. El destino ser� la direcci�n SIP que va en la linea de petici�n
 * 
 * @return      el destino de la llamada
 */

    public String getDestination() {
        return destination;
    }

/**
 * Establece el destino de la llamada. El destino ser� la direcci�n SIP que va en la linea de petici�n
 * 
 * @param 	el destino de la llamada
 */

    public void setDestination(String destination) {
        this.destination = destination;
    }

/**
 * Recupera el maxForwards.
 * 
 * @return      el maxForwards 
 */

    public int getMaxForwards() {
        return maxForwards;
    }

/**
 * Establece el maxForwards.
 * 
 * @param 	maxForwards
 */

    public void setMaxForwards(int maxForwards) {
        this.maxForwards = maxForwards;
    }

/**
 * Recupera el route.
 * 
 * @return      el route
 */
    public String getRoute() {
        return route;
    }

/**
 * Establece el route.
 * 
 * @param 	route
 */
    public void setRoute(String route) {
        this.route = route;
    }

/**
 * Recupera el contentLength.
 * 
 * @return      el contentLength
 */

    public int getContentLength() {
        return contentLength;
    }

/**
 * Establece el contentLength.
 * 
 * @param 	contentLength
 */

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

/**
 * Convierte el mensaje en un String. Para ello concatena la informaci�n de las cabeceras del mensaje.
 * El toName y el fromName son opcionales a la hora de componer las cabeceras to y from pero las URIs si han de estar presentes en las variables toUri y fromUri
 * Estas cabeceras se componen como "To: " + toName + " <" + toUri + ">\n"
 * El CSeq se compone como "CSeq: " + cSeqNumber + " " + cSeqStr + "\n"
 * No hay contact
 * No hay carga en SDP debe tener contenido
 * la cabecera route es opcional (en funci�n de si se ha especificado loose routing en el INVITE)
 * 
 * @return      el mensaje como String.
 */

    @Override
    public String toStringMessage() {
        String ack;
        ack = "ACK " + destination + " SIP/2.0\n";
        for (int i=0; i<vias.size(); i++) {
            ack += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if (route != null) {
            ack += "Route: " + route + "\n";
        }
        ack += "Max-Forwards: " + maxForwards + "\n";
        if(getToName()!=null)
            ack += "To: " + getToName() + " <" + toUri + ">\n";
        else
            ack += "To: <" + toUri + ">\n";
        if(fromName!=null)
            ack += "From: " + fromName + " <" + fromUri + ">\n";
        else
            ack += "From: <" + fromUri + ">\n";
        ack += "Call-ID: " + callId + "\n";
        ack += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        ack += "\n";

        return ack;
    }
}
