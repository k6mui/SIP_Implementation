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
public class ByeMessage extends SIPMessage {

    private String destination;
    private String route;
    private int maxForwards;
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

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public int getMaxForwards() {
        return maxForwards;
    }

    public void setMaxForwards(int maxForwards) {
        this.maxForwards = maxForwards;
    }

    public int getContentLength() {
        return contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    @Override
    public String toStringMessage() {
        String bye;
        bye = "BYE " + destination + " SIP/2.0\n";
        for (int i=0; i<vias.size(); i++) {
            bye += "Via: SIP/2.0/UDP " + vias.get(i) + "\n";
        }
        if (route != null) {
            bye += "Route: " + route + "\n";
        }
        bye += "Max-Forwards: " + maxForwards + "\n";
        if(getToName()!=null)
            bye += "To: " + getToName() + " <" + toUri + ">\n";
        else
            bye += "To: <" + toUri + ">\n";
        if(fromName!=null)
            bye += "From: " + fromName + " <" + fromUri + ">\n";
        else
            bye += "From: <" + fromUri + ">\n";
        bye += "Call-ID: " + callId + "\n";
        bye += "CSeq: " + cSeqNumber + " " + cSeqStr + "\n";
        bye += "Content-Length: " + contentLength + "\n";
        bye += "\n";

        return bye;
    }
}
