/*
 * Código de base para parsear mensajes SIP
 * Puede ser adaptado, ampliado, modificado por el alumno
 * según sus necesidades para la práctica
 */
package mensajesSIP;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author SMA
 */

public class InviteMessage extends SIPMessage {

    private String destination;
    private String recordRoute;
    private int maxForwards;
    private String contact;
	private String proxyAuthentication;
    private String contentType;
    private int contentLength;
    private SDPMessage sdp;

/**
 * Añade una Via como String. El API añade las vias en formato pila de forma que la última via añadida es la primera en quitarse 
 *
 * @param  via  la Via a añadir
 */

    public void addVia(String via) {
	    if (vias==null)
			this.vias = new ArrayList<String>();
        this.vias.add(0, via);
    }

/**
 * Borra la última Via añadida
 *
 */

    public void deleteVia() {
        this.vias.remove(0);
    }

/**
 * Devuelve el contenido de las Vias como ArrayList de Strings.
 * Las Vias contienen la lista de puntos por los que va pasando el mensaje y se añade el último punto al inicio de las Vias.
 * El mensaje en la red tendra vias con formato: Via: SIP/2.0/UDP identificador donde el identificador podrá contener dirección IP o nombre de máquina con o sin y puerto 
 * Este API guardará solo la parte de identificador en la lista de vias y concatenará la parte de Via: SIP/2.0/UDP al generar el mensaje en formato cadena
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
 * Recupera el nombre del destinatario. 
 * La direccion del destinatario tendrá formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @return      el nombre del destinatario
 */

    public String getToName() {
        return toName;
    }

/**
 * Establece el nombre del destinatario. 
 * La direccion del destinatario tendrá formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @param  toName	el nombre a añadir
 */

    public void setToName(String toName) {
        this.toName = toName;
    }

/**
 * Recupera la Uri del destinatario. 
 * La direccion del destinatario tendrá formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @return      la Uri del destinatario
 */

    public String getToUri() {
        return toUri;
    }

/**
 * Establece la Uri del destinatario. 
 * La direccion del destinatario tendrá formato <strong>toName &lt;toUri&gt; </strong>
 *
 * @param  toUri		la Uri del destinatario a añadir
 */

    public void setToUri(String toUri) {
        this.toUri = toUri;
    }

/**
 * Recupera el nombre del origen. 
 * La direccion del destinatario tendrá formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @return      el nombre del origen
 */

    public String getFromName() {
        return fromName;
    }

/**
 * Establece el nombre del origen. 
 * La direccion del destinatario tendrá formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @param  fromName	el nombre del origen a añadir
 */

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

/**
 * Recupera la Uri del origen. 
 * La direccion del destinatario tendrá formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @return      la Uri del origen
 */

    public String getFromUri() {
        return fromUri;
    }

/**
 * Establece la Uri del origen. 
 * La direccion del destinatario tendrá formato <strong>fromName &lt;fromUri&gt; </strong>
 *
 * @param  fromUri		la Uri del origen a añadir
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
 * Recupera el cSeqNumber de la llamada. En SIP el número de secuencia será la concatenación de cSeqNumber y cSeqStr
 * Para un primer mensaje INVITE por ejemplo, el número de secuencia se formaría como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 * 
 * @return      el cSeqNumber de la llamada
 */

    public String getcSeqNumber() {
        return cSeqNumber;
    }

/**
 * Establece el cSeqNumber de la llamada. En SIP el número de secuencia será la concatenación de cSeqNumber y cSeqStr
 * Para un primer mensaje INVITE por ejemplo, el número de secuencia se formaría como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 *
 * @param	cSeqNumber 	 el cSeqNumber de la llamada
 */

    public void setcSeqNumber(String cSeqNumber) {
        this.cSeqNumber = cSeqNumber;
    }

/**
 * Recupera el cSeqStr de la llamada. En SIP el número de secuencia será la concatenación de cSeqNumber y cSeqStr.
 * Para un primer mensaje INVITE por ejemplo, el número de secuencia se formaría como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 * 
 * @return      el cSeqStr de la llamada
 */

    public String getcSeqStr() {
        return cSeqStr;
    }

/**
 * Establece el cSeqStr de la llamada. En SIP el número de secuencia será la concatenación de cSeqNumber y cSeqStr.
 * Para un primer mensaje INVITE por ejemplo, el número de secuencia se formaría como 1 INVITE, donde cSeqNumber=1 y cSeqStr= INVITE 
 *
 * @param	cSeqStr  el cSeqNumber de la llamada
 */

    public void setcSeqStr(String cSeqStr) {
        this.cSeqStr = cSeqStr;
    }

/**
 * Recupera el destino de la llamada. El destino será la dirección SIP que va en la linea de petición
 * 
 * @return      el destino de la llamada
 */

    public String getDestination() {
        return destination;
    }

/**
 * Establece el destino de la llamada. El destino será la dirección SIP que va en la linea de petición
 * 
 * @param 	el destino de la llamada
 */

    public void setDestination(String destination) {
        this.destination = destination;
    }

/**
 * Recupera el recordRoute. El recordRoute será un String que contiene la concatenación de los puntos intermedios de la llamada por los que tienen que pasar los futuros mensajes. Será una lista separada por comas. El API lo gestiona como un String sin separar cada uno de los puntos intermedios que se incluyen.
 * 
 * @return      el recordRoute
 */

    public String getRecordRoute() {
        return recordRoute;
    }

/**
 * Establece el recordRoute. El recordRoute será un String que contiene la concatenación de los puntos intermedios de la llamada por los que tienen que pasar los futuros mensajes. Será una lista separada por comas. El API lo gestiona como un String sin separar cada uno de los puntos intermedios que se incluyen.
 * 
 * @param 	recordRoute
 */

    public void setRecordRoute(String recordRoute) {
        this.recordRoute = recordRoute;
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
 * Recupera el contact. El contact contiene la URI sip del origen a la que se le ha quitado la parte de sip:
 * 
 * @return      el contact
 */

    public String getContact() {
        return contact;
    }

/**
 * Establece el contact. El contact contiene la URI sip del origen a la que se le ha quitado la parte de sip:
 * 
 * @param 	contact 
 */

    public void setContact(String contact) {
        this.contact = contact;
    }
	
	
/**
* Recupera el proxyAuthentication. El proxyAuthentication contiene una cadena con el hash MD5 de lo recibido en proxyAuthenticate del mensaje 407 ProxyAuthentication al que se le concatena la contraseña de usuario para calcular el hash * 
 * @return      el proxyAuthentication
 */

    public String getProxyAuthentication() {
        return proxyAuthentication;
    }

/**
 * Establece el proxyAuthentication. El proxyAuthentication contiene una cadena con el hash MD5 de lo recibido en proxyAuthenticate del mensaje 407 ProxyAuthentication al que se le concatena la contraseña de usuario para calcular el hash
 * 
 * @param 	proxyAuthentication 
 */

    public void setProxyAuthentication(String proxyAuthentication) {
        this.proxyAuthentication = proxyAuthentication;
    }
	

/**
 * Recupera el Content-Type. El Content-Type será el tipo MIME asociado al contenido que se transporta en el mensaje INVITE. Nosotros usaremos Application/SDP 
 * 
 * @return      contentType
 */
 
    public String getContentType() {
        return contentType;
    }

	/**
 * Establece el Content-Type. El Content-Type será el tipo MIME asociado al contenido que se transporta en el mensaje INVITE. Nosotros usaremos Application/SDP 
 * 
 * @param 	contentType 
 */
 
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
	
/**
 * Recupera el Content-Length. El Content-Length será el tamaño del contenido que se transporta en el mensaje INVITE en numero de caracteres. 
 * 
 * @return      contentLength
 */

    public int getContentLength() {
        return contentLength;
    }

		/**
 * Establece el Content-Length. El Content-Length será el tamaño del contenido que se transporta en el mensaje INVITE en numero de caracteres.  
 * 
 * @param 	contentLength 
 */
 
    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

/**
 * Recupera el contenido de la carga útil en SDP mediante la clase de soporte SDPMessage que permite establecer la IP y pueto de la sesión de medios así como los atributos de la misma.
 * 
 * @return      el SDPMessage 
 */

    public SDPMessage getSdp() {
        return sdp;
    }

/**
 * Establece el contenido de la carga útil en SDP mediante la clase de soporte SDPMessage que permite establecer la IP y pueto de la sesión de medios así como los atributos de la misma.
 * 
 * @param 	sdp - el SDPMessage 
 */

    public void setSdp(SDPMessage sdp) {
        this.sdp = sdp;
    }

/**
 * Convierte el mensaje en un String. Para ello concatena la información de las cabeceras del mensaje.
 * El toName y el fromName son opcionales a la hora de componer las cabeceras to y from pero las URIs si han de estar presentes en las variables toUri y fromUri
 * Estas cabeceras se componen como "To: " + toName + " <" + toUri + ">\n"
 * El CSeq se compone como "CSeq: " + cSeqNumber + " " + cSeqStr + "\n"
 * El Contact como "Contact: <sip:" + contact + ">\n" donde la variable contact tiene la información de contacto pero sin la parte sip: de la URI final
 * La carga en SDP debe tener contenido
 * 
 * @return      el mensaje como String.
 */
    
    @Override
    public String toStringMessage() {
        String invite;
        invite = "INVITE " + destination + " SIP/2.0\n";
        for (int i=0; i<vias.size(); i++) {
            invite += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if (recordRoute != null) {
            invite += "Record-Route: " + recordRoute + "\n";
        }
        invite += "Max-Forwards: " + maxForwards + "\n";
        if(toName!=null)
            invite += "To: " + toName + " <" + toUri + ">\n";
        else
            invite += "To: <" + toUri + ">\n";
        if(fromName!=null)
            invite += "From: " + fromName + " <" + fromUri + ">\n";
        else
            invite += "From: <" + fromUri + ">\n";
        invite += "Call-ID: " + callId + "\n";
        invite += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        invite += "Contact: <sip:" + contact + ">\n";
		if (proxyAuthentication!=null) invite += "ProxyAuthentication: auth= " + proxyAuthentication + "\n";
        invite += "Content-Type: " + contentType + "\n";
        invite += "Content-Length: " + contentLength + "\n";
        invite += "\n";
        invite += sdp.toStringMessage();

        return invite;
    }
}
