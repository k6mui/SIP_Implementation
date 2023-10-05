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
public class NotFoundMessage extends SIPMessage {

    private String contact;
    private String expires;
    private int contentLength;

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
    
    @Override
    public String toStringMessage() {
        String nf;
        nf = "SIP/2.0 404 Not Found\n";
        for (int i=0; i<vias.size(); i++) {
            nf += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if(getToName()!=null)
            nf += "To: " + getToName() + " <" + toUri + ">\n";
        else
            nf += "To: <" + toUri + ">\n";
        if(fromName!=null)
            nf += "From: " + fromName + " <" + fromUri + ">\n";
        else
            nf += "From: <" + fromUri + ">\n";
        nf += "Call-ID: " + callId + "\n";
        nf += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        nf += "Contact: <sip:" + contact + ">\n";
        nf += "Content-Length: " + contentLength + "\n";
        nf += "\n";

        return nf;
    }
}
