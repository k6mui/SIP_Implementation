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
public class RequestTimeoutMessage extends SIPMessage {

    private int contentLength;

    public ArrayList<String> getVias() {
        return vias;
    }

    public void setVias(ArrayList<String> vias) {
        this.vias = vias;
    }
    
    public void addVia(String via) {
        this.vias.add(0, via);
    }

    public void deleteVia() {
        this.vias.remove(0);
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
        String rt;
        rt = "SIP/2.0 408 Request Timeout\n";
        for (int i=0; i<vias.size(); i++) {
            rt += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if(getToName()!=null)
            rt += "To: " + getToName() + " <" + toUri + ">\n";
        else
            rt += "To: <" + toUri + ">\n";
        if(fromName!=null)
            rt += "From: " + fromName + " <" + fromUri + ">\n";
        else
            rt += "From: <" + fromUri + ">\n";
        rt += "Call-ID: " + callId + "\n";
        rt += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        rt += "Content-Length: " + contentLength + "\n";
        rt += "\n";

        return rt;
    }
}
