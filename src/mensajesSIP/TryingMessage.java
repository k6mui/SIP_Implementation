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
public class TryingMessage extends SIPMessage {

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

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }
    
    @Override
    public String toStringMessage() {
        String trying;
        trying = "SIP/2.0 100 Trying\n";
        for (int i=0; i<vias.size(); i++) {
            trying += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if(getToName()!=null)
            trying += "To: " + getToName() + " <" + toUri + ">\n";
        else
            trying += "To: <" + toUri + ">\n";
        if(fromName!=null)
            trying += "From: " + fromName + " <" + fromUri + ">\n";
        else
            trying += "From: <" + fromUri + ">\n";
        trying += "Call-ID: " + callId + "\n";
        trying += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        trying += "Content-Length: " + contentLength + "\n";
        trying += "\n";

        return trying;
    }
}
