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
public class RegisterMessage extends SIPMessage {

    public String destination;
    public int maxForwards;
    public String contact;
    private String authorization;
    public String expires;
    public int contentLength;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getMaxForwards() {
        return maxForwards;
    }

    public void setMaxForwards(int maxForwards) {
        this.maxForwards = maxForwards;
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
    
    public String getAuthorization() {
        return authorization;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
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

    @Override
    public String toStringMessage() {
        String register;
        register = "REGISTER " + destination + " SIP/2.0\n";
        for (int i=0; i<vias.size(); i++) {
            register += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        register += "Max-Forwards: " + maxForwards + "\n";
        if(getToName()!=null)
            register += "To: " + getToName() + " <" + toUri + ">\n";
        else
            register += "To: <" + toUri + ">\n";
        if(fromName!=null)
            register += "From: " + fromName + " <" + fromUri + ">\n";
        else
            register += "From: <" + fromUri + ">\n";
        register += "Call-ID: " + callId + "\n";
        register += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        register += "Contact: <sip:" + contact + ">\n";
        if(getAuthorization()!=null)
        	register += "Authorization: response= " + authorization + "\n";
        register += "Expires: " + expires + "\n";
        register += "Content-Length: " + contentLength + "\n";
        register += "\n";

        return register;
    }
}
