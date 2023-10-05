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
public class RingingMessage extends SIPMessage {

    private String recordRoute;
    private String contact;
    private int contentLength;

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

    public String getRecordRoute() {
        return recordRoute;
    }

    public void setRecordRoute(String recordRoute) {
        this.recordRoute = recordRoute;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    @Override
    public String toStringMessage() {
        String ringing;
        ringing = "SIP/2.0 180 Ringing\n";
        for (int i=0; i<vias.size(); i++) {
            ringing += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if (recordRoute != null) {
            ringing += "Record-Route: " + recordRoute + "\n";
        }
        if(getToName()!=null)
            ringing += "To: " + getToName() + " <" + toUri + ">\n";
        else
            ringing += "To: <" + toUri + ">\n";
        if(fromName!=null)
            ringing += "From: " + fromName + " <" + fromUri + ">\n";
        else
            ringing += "From: <" + fromUri + ">\n";
        ringing += "Call-ID: " + callId + "\n";
        ringing += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        ringing += "Contact: <sip:" + contact + ">\n";
        ringing += "Content-Length: " + contentLength + "\n";
        ringing += "\n";

        return ringing;
    }
}
