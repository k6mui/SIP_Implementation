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
public class ServiceUnavailableMessage extends SIPMessage {

    private int contentLength;

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
        String su;
        su = "SIP/2.0 503 Service Unavailable\n";
        for (int i=0; i<vias.size(); i++) {
            su += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if(getToName()!=null)
            su += "To: " + getToName() + " <" + toUri + ">\n";
        else
            su += "To: <" + toUri + ">\n";
        if(fromName!=null)
            su += "From: " + fromName + " <" + fromUri + ">\n";
        else
            su += "From: <" + fromUri + ">\n";
        su += "Call-ID: " + callId + "\n";
        su += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        su += "Content-Length: " + contentLength + "\n";
        su += "\n";

        return su;
    }

}
