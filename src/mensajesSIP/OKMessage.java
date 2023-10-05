/*
 * Código de base para parsear mensajes SIP
 * Puede ser adaptado, ampliado, modificado por el alumno
 * según sus necesidades para la práctica
 */
package mensajesSIP;

import java.util.ArrayList;

/**
 *
 * @author SMA
 */
public class OKMessage extends SIPMessage {

    private String route;
    private String recordRoute; 
    private String contact;
    private String expires;
    private int contentLength;
    SDPMessage sdp;

    public void addVia(String via) {
        this.vias.add(0, via);
    }

    public void deleteVia() {
        this.vias.remove(0);
    }

    public ArrayList<String> getVias() {
        return vias;
    }

    public void setVias(ArrayList<String> vias) {
        this.vias = vias;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getToUri() {
        return toUri;
    }

    public void setToUri(String toUri) {
        this.toUri = toUri;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromUri() {
        return fromUri;
    }

    public void setFromUri(String fromUri) {
        this.fromUri = fromUri;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getcSeqNumber() {
        return cSeqNumber;
    }

    public void setcSeqNumber(String cSeqNumber) {
        this.cSeqNumber = cSeqNumber;
    }

    public String getcSeqStr() {
        return cSeqStr;
    }

    public void setcSeqStr(String cSeqStr) {
        this.cSeqStr = cSeqStr;
    }

    public OKMessage() { //TODO: EAM. Warning
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
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

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
    
    public SDPMessage getSdp() {
        return sdp;
    }

    public void setSdp(SDPMessage sdp) {
        this.sdp = sdp;
    }

    @Override
    public String toStringMessage() {
        String ok;
        ok = "SIP/2.0 200 OK\n";
        for (int i=0; i<vias.size(); i++) {
            ok += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if (route != null) {
            ok += "Route: " + route + "\n";
        }
        if (recordRoute != null) {
            ok += "Record-Route: " + recordRoute + "\n";
        }
        if(getToName()!=null)
            ok += "To: " + getToName() + " <" + toUri + ">\n";
        else
            ok += "To: <" + toUri + ">\n";
        if(fromName!=null)
            ok += "From: " + fromName + " <" + fromUri + ">\n";
        else
            ok += "From: <" + fromUri + ">\n";
        ok += "Call-ID: " + callId + "\n";
        ok += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        ok += "Contact: <sip:" + contact + ">\n";
        if(expires != null)
            ok += "Expires: " + expires + "\n";
        ok += "Content-Length: " + contentLength + "\n";
        ok += "\n";
        if(sdp!=null){
            ok += sdp.toStringMessage();
        }

        return ok;
    }
}
