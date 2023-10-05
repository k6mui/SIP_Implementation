/*
 * Código de base para parsear mensajes SIP
 * Puede ser adaptado, ampliado, modificado por el alumno
 * según sus necesidades para la práctica
 */
package mensajesSIP;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author SMA
 */

public abstract class SIPMessage {
    
    protected ArrayList<String> vias;
    public String toName;
    protected String toUri;
    protected String fromName;
    protected String fromUri;
    protected String callId;
    protected String cSeqNumber;
    protected String cSeqStr;
    
/**
 * Convierte el mensaje en un String. Para ello concatena la información de las cabeceras del mensaje.
 * 
 * @return      el mensaje como String.
 */

    public abstract String toStringMessage();
    
/**
 * Convierte el mensaje en un String. Para ello concatena la información de las cabeceras del mensaje.
 * <p> Nótese que es un método de clase o método estático de cara a poderse invocar sobre la propia clase SIPMessage. Este método se usa como factoría para generar los diferentes mensajes SIP a partir de lo recibido de la red.
 * <p> En realidad, como los mensajes generados heradarán de la clase SIPMessage, al invocar a parseMessage tendremos que hacer un casting a la clase apropiada.
 * 
 * @return      SIPMessage el mensaje parseado. 
 */

    public static SIPMessage parseMessage(String message) throws SIPException{
        
        String[] lines = message.split("\n");
        
        String recordRoute = null, route = null, maxForwards = null, callId = null, contact = null,
                contentLength = null, expires = null, proxyAuthenticate=null, proxyAuthentication=null, authorization=null, wwwAuthenticate=null;
        ArrayList<String> vias = new ArrayList<String>();
        String[] to = null, from = null, cSeq = null;
        for(int i=0; i<lines.length; i++){
            if(lines[i].startsWith("Via")){
                vias.add(parseVia(lines[i]));
            }
            else if(lines[i].startsWith("Record-Route")){
                recordRoute = parseRecordRoute(lines[i]);
            }
            else if(lines[i].startsWith("Route")){
                route = parseRoute(lines[i]);
            }
            else if(lines[i].startsWith("Max-Forwards")){
                maxForwards = parseMaxForwards(lines[i]);
            }
            else if(lines[i].startsWith("From")){
                from = parseFrom(lines[i]);
            }
            else if(lines[i].startsWith("To")){
                to = parseTo(lines[i]);
            }
            else if(lines[i].startsWith("Call-ID")){
                callId = parseCallId(lines[i]);
            }
            else if(lines[i].startsWith("CSeq")){
                cSeq = parseCSeq(lines[i]);
            }
            else if(lines[i].startsWith("Contact")){
                contact = parseContact(lines[i]);
            }
            else if(lines[i].startsWith("Content-Length")){
                contentLength = parseContentLength(lines[i]);
            }
            else if(lines[i].startsWith("Expires")){
                expires = parseExpires(lines[i]);
            }
			else if(lines[i].startsWith("ProxyAuthenticate")){
                proxyAuthenticate = parseProxyAuthenticate(lines[i]);
            }
			else if(lines[i].startsWith("ProxyAuthentication")){
                proxyAuthentication = parseProxyAuthentication(lines[i]);
            }
			else if(lines[i].startsWith("Authorization")){
                authorization = parseAuthorization(lines[i]);
            }
			else if(lines[i].startsWith("WWW-Authenticate")){
                wwwAuthenticate = parsewwwAuthenticate(lines[i]);
            }
        }
        
        
        if(message.startsWith("INVITE")){
            InviteMessage invite = new InviteMessage();
            String[] parts = message.split("\n\n");
            
            //SDP message
            String sdpMessageStr = parts[1];
            
            invite.setDestination(parseRequestHeader(lines[0]));
            invite.setVias(vias);
		 if (recordRoute!=null) invite.setRecordRoute(recordRoute);
            invite.setMaxForwards(Integer.parseInt(maxForwards));
            invite.setToName(to[0]);
            invite.setToUri(to[1]);
            invite.setFromName(from[0]);
            invite.setFromUri(from[1]);
            invite.setCallId(callId);
            invite.setcSeqNumber(cSeq[0]);
            invite.setcSeqStr(cSeq[1]);
			invite.setContact(contact);
			if (proxyAuthentication!=null) invite.setProxyAuthentication(proxyAuthentication);
            invite.setContentType("application/sdp");
            invite.setContentLength(Integer.parseInt(contentLength));
            
            SDPMessage sdp = new SDPMessage();
            sdp.parseMessage(sdpMessageStr);
            invite.setSdp(sdp);
            
            return invite;
        }
        else if(message.startsWith("REGISTER")){
            RegisterMessage register = new RegisterMessage();
            
            register.setDestination(parseRequestHeader(lines[0]));
            register.setVias(vias);
            register.setMaxForwards(Integer.parseInt(maxForwards));
            register.setToName(to[0]);
            register.setToUri(to[1]);
            register.setFromName(from[0]);
            register.setFromUri(from[1]);
            register.setCallId(callId);
            register.setcSeqNumber(cSeq[0]);
            register.setcSeqStr(cSeq[1]);
            register.setContact(contact);
            register.setExpires(expires);
            register.setAuthorization(authorization);
            
            return register;
        }
        else if(message.startsWith("BYE")){
            ByeMessage bye = new ByeMessage();
            
            bye.setDestination(parseRequestHeader(lines[0]));
            bye.setVias(vias);
            if(route!=null){
                bye.setRoute(route);
            }
            bye.setMaxForwards(Integer.parseInt(maxForwards));
            bye.setToName(to[0]);
            bye.setToUri(to[1]);
            bye.setFromName(from[0]);
            bye.setFromUri(from[1]);
            bye.setCallId(callId);
            bye.setcSeqNumber(cSeq[0]);
            bye.setcSeqStr(cSeq[1]);
            bye.setContentLength(0);
            
            return bye;
        }
        else if(message.startsWith("ACK")){
            ACKMessage ack = new ACKMessage();
            
            ack.setDestination(parseRequestHeader(lines[0]));
            ack.setVias(vias);
            if(route!=null){
                ack.setRoute(route);
            }
            ack.setMaxForwards(Integer.parseInt(maxForwards));
            ack.setToName(to[0]);
            ack.setToUri(to[1]);
            ack.setFromName(from[0]);
            ack.setFromUri(from[1]);
            ack.setCallId(callId);
            ack.setcSeqNumber(cSeq[0]);
            ack.setcSeqStr(cSeq[1]);
            ack.setContentLength(0);
            
            return ack;
        }
        else if(message.startsWith("SIP/2.0 100 Trying")){
            TryingMessage trying = new TryingMessage();
            
            trying.setVias(vias);
            trying.setToName(to[0]);
            trying.setToUri(to[1]);
            trying.setFromName(from[0]);
            trying.setFromUri(from[1]);
            trying.setCallId(callId);
            trying.setcSeqNumber(cSeq[0]);
            trying.setcSeqStr(cSeq[1]);
            trying.setContentLength(0);
            
            return trying;
        }
        else if(message.startsWith("SIP/2.0 180 Ringing")){
            RingingMessage ringing = new RingingMessage();
            
            ringing.setVias(vias);
            if(recordRoute!=null){
                ringing.setRecordRoute(recordRoute);
            }
            ringing.setToName(to[0]);
            ringing.setToUri(to[1]);
            ringing.setFromName(from[0]);
            ringing.setFromUri(from[1]);
            ringing.setCallId(callId);
            ringing.setcSeqNumber(cSeq[0]);
            ringing.setcSeqStr(cSeq[1]);
            ringing.setContact(contact);
            ringing.setContentLength(0);
            
            return ringing;
            
        }
        else if(message.startsWith("SIP/2.0 200 OK")){
            OKMessage ok = new OKMessage();
            
            String[] parts = message.split("\n\n");
            
            
            ok.setVias(vias);
            if(route!=null){
                ok.setRoute(route);
            }
            if(recordRoute!=null){
                ok.setRecordRoute(recordRoute);
            }
            ok.setToName(to[0]);
            ok.setToUri(to[1]);
            ok.setFromName(from[0]);
            ok.setFromUri(from[1]);
            ok.setCallId(callId);
            ok.setcSeqNumber(cSeq[0]);
            ok.setcSeqStr(cSeq[1]);
            if(contact!=null){
            	ok.setContact(contact);
            }
            ok.setContentLength(0);
            
//TODO
            if(parts.length==2){
		        //SDP message
		        String sdpMessageStr = parts[1];
		        ok.setContentLength(sdpMessageStr.length());
		        SDPMessage sdp = new SDPMessage();
		        sdp.parseMessage(sdpMessageStr);
		        ok.setSdp(sdp);
		    }
            
            return ok;
        }
        else if(message.startsWith("SIP/2.0 404 Not Found")){
            NotFoundMessage nf = new NotFoundMessage();
            
            nf.setVias(vias);
            nf.setToName(to[0]);
            nf.setToUri(to[1]);
            nf.setFromName(from[0]);
            nf.setFromUri(from[1]);
            nf.setCallId(callId);
            nf.setcSeqNumber(cSeq[0]);
            nf.setcSeqStr(cSeq[1]);
            nf.setContact(contact);
            if(expires!=null){
                nf.setExpires(expires);
            }
            nf.setContentLength(0);
            
            return nf;
        }
        else if(message.startsWith("SIP/2.0 408 Request Timeout")){
            RequestTimeoutMessage rt = new RequestTimeoutMessage();
            
            rt.setVias(vias);
            rt.setToName(to[0]);
            rt.setToUri(to[1]);
            rt.setFromName(from[0]);
            rt.setFromUri(from[1]);
            rt.setCallId(callId);
            rt.setcSeqNumber(cSeq[0]);
            rt.setcSeqStr(cSeq[1]);
            rt.setContentLength(0);
            
            return rt; 
        }
        else if(message.startsWith("SIP/2.0 486 Busy Here")){
            BusyHereMessage bh = new BusyHereMessage();
            
            bh.setVias(vias);
            bh.setToName(to[0]);
            bh.setToUri(to[1]);
            bh.setFromName(from[0]);
            bh.setFromUri(from[1]);
            bh.setCallId(callId);
            bh.setcSeqNumber(cSeq[0]);
            bh.setcSeqStr(cSeq[1]);
            bh.setContentLength(0);
            
            return bh;
        }
        else if(message.startsWith("SIP/2.0 503 Service Unavailable")){
            ServiceUnavailableMessage su = new ServiceUnavailableMessage();
            
            su.setVias(vias);
            su.setToName(to[0]);
            su.setToUri(to[1]);
            su.setFromName(from[0]);
            su.setFromUri(from[1]);
            su.setCallId(callId);
            su.setcSeqNumber(cSeq[0]);
            su.setcSeqStr(cSeq[1]);
            su.setContentLength(0);
            
            return su;
        }
		else if(message.startsWith("SIP/2.0 407 Proxy Authentication Required")){
            ProxyAuthenticationMessage pa = new ProxyAuthenticationMessage();
            
            pa.setVias(vias);
            pa.setToName(to[0]);
            pa.setToUri(to[1]);
            pa.setFromName(from[0]);
            pa.setFromUri(from[1]);
            pa.setCallId(callId);
            pa.setcSeqNumber(cSeq[0]);
            pa.setcSeqStr(cSeq[1]);
			pa.setproxyAuthenticate(proxyAuthenticate);
            pa.setContentLength(0);
            
            return pa;
        }
		else if(message.startsWith("SIP/2.0 401 Unauthorized")){
			UnauthorizedMessage pa = new UnauthorizedMessage();
            
            pa.setVias(vias);
            pa.setToName(to[0]);
            pa.setToUri(to[1]);
            pa.setFromName(from[0]);
            pa.setFromUri(from[1]);
            pa.setCallId(callId);
            pa.setcSeqNumber(cSeq[0]);
            pa.setcSeqStr(cSeq[1]);
			pa.setwwwAuthenticate(wwwAuthenticate);
            pa.setContentLength(0);
            
            return pa;
        }
        else{
            throw new SIPException();
        }
        
        
    }
    
    
    /**
     * Parsea cada una de las líneas de Via del mensaje recibido, les quita la parte de Via: SIP/2.0/UDP y el resultado lo devuelve como String para que pueda ser añadido al ArrayList de las Vias del mensaje
 	* 
     * @param via en el formato red recibido del mensaje SIP
     * @return 
     */
    private static String parseVia(String via) throws SIPException{
        Pattern pattern = Pattern.compile("Via: SIP/2.0/UDP ([\\w\\.\\:\\;\\-\\=]+)");
        Matcher matcher = pattern.matcher(via.split(";")[0]);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect VIA format");
        }
    }
    
    /**
     * 
     * @param from
     * @return 
     */
    private static String[] parseFrom(String toFrom) throws SIPException{
        Pattern pattern = Pattern.compile("From: ?(\\w+)? <(sip:\\w+@[\\w\\.]+)>");
        Matcher matcher = pattern.matcher(toFrom);
        if(matcher.matches()){
            return  new String[]{matcher.group(1),matcher.group(2)};
        }
        else{
            throw new SIPException("Incorrect FROM format");
        }
    }
    
    /**
     * 
     * @param to
     * @return 
     */
    private static String[] parseTo(String toFrom) throws SIPException{
        Pattern pattern = Pattern.compile("To: ?(\\w+)? <(sip:\\w+@[\\w\\.]+)>");
        Matcher matcher = pattern.matcher(toFrom);
        // System.out.println(toFrom);
        if(matcher.matches()){
            return  new String[]{matcher.group(1),matcher.group(2)};
        }
        else{
            throw new SIPException("Incorrect TO format");
        } 
    }
    
    /**
     * 
     * @param contact
     * @return 
     */
    private static String parseContact(String contact) throws SIPException{
        Pattern pattern = Pattern.compile("Contact: <sip:([\\w\\.\\:\\;\\-\\=]+)>");
        //Matcher matcher = pattern.matcher(contact.split(";")[0]+">");
        Matcher matcher = pattern.matcher(contact);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect CONTACT format");
        }
    }
    
    /**
     * 
     * @param cSeq
     * @return 
     */
    private static String[] parseCSeq(String cSeq) throws SIPException{
        Pattern pattern = Pattern.compile("CSeq: (\\d+) (INVITE|REGISTER|BYE|ACK)");
        Matcher matcher = pattern.matcher(cSeq);
        if(matcher.matches()){
            return  new String[]{matcher.group(1),matcher.group(2)};
        }
        else{
            throw new SIPException("Incorrect CSEQ format");
        }
    }
    
    /**
     * 
     * @param callId
     * @return 
     */
    private static String parseCallId(String callId) throws SIPException{
        Pattern pattern = Pattern.compile("Call-ID: ([a-zA-Z0-9@\\.\\-]+)");
        Matcher matcher = pattern.matcher(callId);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect CALL ID format");
        }
    }
    
    /**
     * 
     * @param contentLength
     * @return 
     */
    private static String parseContentLength(String contentLength) throws SIPException{
        Pattern pattern = Pattern.compile("Content-Length: (\\d+)");
        Matcher matcher = pattern.matcher(contentLength);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect CONTENT LENGTH format");
        }
    }
    
    /**
     * 
     * @param maxForwards
     * @return 
     */
    private static String parseMaxForwards(String maxForwards) throws SIPException{
        Pattern pattern = Pattern.compile("Max-Forwards: (\\d+)");
        Matcher matcher = pattern.matcher(maxForwards);
        // System.out.println(maxForwards);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            //throw new SIPException("Incorrect MAX FORWARDS format");
        	return "70";
        }
    }
    
    /**
     * 
     * @param recordRoute
     * @return 
     */
    private static String parseRecordRoute(String recordRoute) throws SIPException{
        Pattern pattern = Pattern.compile("Record-Route: ([\\w\\.\\:\\,\\s@]+)");
        Matcher matcher = pattern.matcher(recordRoute);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect RECORD ROUTE format");
        }
    }
    
    /**
     * 
     * @param route
     * @return 
     */
    private static String parseRoute(String route) throws SIPException{
        Pattern pattern = Pattern.compile("Route: ([\\w\\.\\:\\s\\,@]+)");
        Matcher matcher = pattern.matcher(route);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect ROUTE format");
        }
    }
    
    /**
     * 
     * @param header
     * @return 
     */
    private static String parseRequestHeader(String header) throws SIPException{
        Pattern pattern = Pattern.compile("(INVITE|REGISTER|BYE|ACK) (sip:[\\w\\.@]+) SIP/2.0");
        Matcher matcher = pattern.matcher(header);
        if(matcher.matches()){
            return  matcher.group(2);
        }
        else{
            throw new SIPException("Incorrect HEADER format");
        }
    }
    
    /**
     * 
     * @param expires
     * @return 
     */
    private static String parseExpires(String expires) throws SIPException{
        Pattern pattern = Pattern.compile("Expires: (\\d+)");
        Matcher matcher = pattern.matcher(expires);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect EXPIRES format");
        }
    }
	
	/**
     * 
     * @param proxyAuthenticate
     * @return 
     */
    private static String parseProxyAuthenticate(String proxyAuthenticate) throws SIPException{
        Pattern pattern = Pattern.compile("ProxyAuthenticate: nonce= ([\\w\\.\\:\\s\\,@]+)");
        Matcher matcher = pattern.matcher(proxyAuthenticate);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect ProxyAuthenticate format");
        }
    }
	/**
     * 
     * @param proxyAuthentication
     * @return 
     */
    private static String parseProxyAuthentication(String proxyAuthentication) throws SIPException{
        Pattern pattern = Pattern.compile("ProxyAuthentication: auth= ([\\w\\.\\:\\s\\,@]+)");
        Matcher matcher = pattern.matcher(proxyAuthentication);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect ProxyAuthentication format");
        }
    }
    
    private static String parseAuthorization(String authorization) throws SIPException{
        Pattern pattern = Pattern.compile("Authorization: response= ([\\w\\.\\:\\s\\,@]+)");
        Matcher matcher = pattern.matcher(authorization);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect Authorization format");
        }
    }
	/**
     * 
     * @param proxyAuthentication
     * @return 
     */
    private static String parsewwwAuthenticate(String wwwAuthenticate) throws SIPException{
        Pattern pattern = Pattern.compile("WWW-Authenticate: nonce= ([\\w\\.\\:\\s\\,@]+)");
        Matcher matcher = pattern.matcher(wwwAuthenticate);
        if(matcher.matches()){
            return  matcher.group(1);
        }
        else{
            throw new SIPException("Incorrect WWW-Authenticate format");
        }
    }    
    
}
