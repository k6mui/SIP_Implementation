package mensajesSIP;

/* Esta clase de ejemplo del API de mensajes proporcionado
*  pretende facilitar su uso.
*  Tambien muestra como el API corta aquellos campos que no se
*  usan en cada uno de los mensajes de ejemplo. */

import java.security.MessageDigest;

public class Prueba {

public static void main(String[] args) {
		System.out.println("Generando mensaje INVITE");
		System.out.println("");
		InviteMessage inv;
		String mensaje = new String("INVITE sip:7170@iptel.org SIP/2.0\n");
		mensaje+="Via: SIP/2.0/UDP 195.37.77.100:5040\n";
		mensaje+="Route: 195.37.77.101:5040\n";
		mensaje+="Record-Route: 195.37.77.101:5040, capi.uc3m.es:5040\n";
		mensaje+="Max-Forwards: 10\n";
		mensaje+="From: jiri <sip:jiri@iptel.org>\n";
		mensaje+="To: <sip:jiri@bat.iptel.org>\n";
		mensaje+="Call-ID: d10815e0-bf17-4afa-8412-d9130a793d96@213.20.128.35\n";
		mensaje+="CSeq: 2 INVITE\n";
		mensaje+="Contact: <sip:213.20.128.35:9315>\n";
		try{
		MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] dataBytes = new String("Hola").getBytes();
        int nread = dataBytes.length;
        md.update(dataBytes, 0, nread);
        byte[] mdbytes = md.digest();
		
        //convert the byte to hex format
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
          sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }
		
		mensaje+="ProxyAuthentication: auth= " + sb.toString() + " \n";
		} catch(Exception e){e.printStackTrace(System.out);};
		mensaje+="User-Agent: Windows RTC/1.0\n";
		mensaje+="Content-Type: application/sdp\n";
		mensaje+="Content-Length: 451\n\n";
		mensaje+="c=IN IP4 213.20.128.35\n";
		mensaje+="m=audio 54742 RTP/AVP 97\n";
		mensaje+="a=rtpmap:97 red/8000\n";
		System.out.println(mensaje + "\n\n\n");

		try{
			inv = (InviteMessage) SIPMessage.parseMessage(mensaje);
			inv.addVia("hola.hi.com");
			System.out.println("Imprimiendo mensaje procesado");
			System.out.println(inv.toStringMessage());
		} catch (Exception e){e.printStackTrace(System.out);};


		System.out.println("Generando mensaje 180 Ringing");
		System.out.println("");
		RingingMessage rg;
		mensaje = new String("SIP/2.0 180 Ringing\n");

		mensaje+="Via: SIP/2.0/UDP ss2.biloxi.example.com:5060\n";
		mensaje+="Via: SIP/2.0/UDP ss1.atlanta.example.com:5060\n";
		mensaje+="Via: SIP/2.0/UDP client.atlanta.example.com:5060\n";
		mensaje+="Record-Route: sip:ss2.biloxi.example.com, sip:ss1.atlanta.example.com\n";
		mensaje+="Max-Forwards: 10\n";
		mensaje+="From: Alice <sip:alice@atlanta.example.com>\n";
		mensaje+="To: Bob <sip:bob@biloxi.example.com>\n";
		mensaje+="Call-ID: 3848276298220188511@atlanta.example.com\n";
		mensaje+="CSeq: 2 INVITE\n";
		mensaje+="Contact: <sip:bob@client.biloxi.example.com>\n";
		mensaje+="User-Agent: Windows RTC/1.0\n";
		mensaje+="Content-Type: application/sdp\n";
		mensaje+="Content-Length: 451\n\n";
		mensaje+="c=IN IP4 213.20.128.35\n";
		mensaje+="m=audio 54742 RTP/AVP 97\n";
		mensaje+="m=audio 3456 RTP/AVP 98\n";
		mensaje+="a=rtpmap:97 red/8000\n";
		mensaje+="a=rtpmap:98 PCMU/8000\n";

		System.out.println(mensaje + "\n\n\n");

		try{
			rg = (RingingMessage) SIPMessage.parseMessage(mensaje);
			System.out.println("Imprimiendo mensaje procesado");
			System.out.println(rg.toStringMessage());
		} catch (Exception e){e.printStackTrace(System.out);};



		System.out.println("Generando mensaje 200 OK");
		System.out.println("");
		OKMessage ok;
		mensaje = new String("SIP/2.0 200 OK\n");

		mensaje+="Via: SIP/2.0/UDP ss2.biloxi.example.com:5060\n";
		mensaje+="Via: SIP/2.0/UDP ss1.atlanta.example.com:5060\n";
		mensaje+="Via: SIP/2.0/UDP client.atlanta.example.com:5060\n";
		mensaje+="Record-Route: sip:ss2.biloxi.example.com, sip:ss1.atlanta.example.com\n";
		mensaje+="Max-Forwards: 10\n";
		mensaje+="From: Alice <sip:alice@atlanta.example.com>\n";
		mensaje+="To: Bob <sip:bob@biloxi.example.com>\n";
		mensaje+="Call-ID: 3848276298220188511@atlanta.example.com\n";
		mensaje+="CSeq: 2 INVITE\n";
		mensaje+="Contact: <sip:bob@client.biloxi.example.com>\n";
		mensaje+="User-Agent: Windows RTC/1.0\n";
		mensaje+="Content-Type: application/sdp\n";
		mensaje+="Content-Length: 451\n\n";
		mensaje+="c=IN IP4 213.20.128.35\n";
		mensaje+="m=audio 54742 RTP/AVP 97\n";
		mensaje+="m=audio 3456 RTP/AVP 98\n";
		mensaje+="a=rtpmap:97 red/8000\n";
		mensaje+="a=rtpmap:98 PCMU/8000\n";

		System.out.println(mensaje + "\n\n\n");

		try{
			ok = (OKMessage) SIPMessage.parseMessage(mensaje);
			System.out.println("Imprimiendo mensaje procesado");
			System.out.println(ok.toStringMessage());
		} catch (Exception e){e.printStackTrace(System.out);};

		
		System.out.println("Generando mensaje 407 Proxy Authentication Required");
		System.out.println("");
		ProxyAuthenticationMessage pa;
		mensaje = new String("SIP/2.0 407 Proxy Authentication Required\n");

		mensaje+="Via: SIP/2.0/UDP client.atlanta.example.com:5060\n";
		mensaje+="Max-Forwards: 10\n";
		mensaje+="From: Alice <sip:alice@atlanta.example.com>\n";
		mensaje+="To: Bob <sip:bob@biloxi.example.com>\n";
		mensaje+="Call-ID: 3848276298220188511@atlanta.example.com\n";
		mensaje+="CSeq: 2 INVITE\n";
		mensaje+="ProxyAuthenticate: nonce= HolaHola \n";

		System.out.println(mensaje + "\n\n\n");

		try{
			pa = (ProxyAuthenticationMessage) SIPMessage.parseMessage(mensaje);
			System.out.println("Imprimiendo mensaje procesado");
			System.out.println(pa.toStringMessage());
		} catch (Exception e){e.printStackTrace(System.out);};


		System.out.println("Generando mensaje BYE");
		System.out.println("");
		ByeMessage bye;
		mensaje = new String("BYE sip:7170@iptel.org SIP/2.0\n");
		mensaje+="Via: SIP/2.0/UDP 195.37.77.100:5040\n";
		mensaje+="Route: 195.37.77.101:5040\n";
		mensaje+="Record-Route: 195.37.77.101:5040, capi.uc3m.es:5040\n";
		mensaje+="Max-Forwards: 10\n";
		mensaje+="From: jiri <sip:jiri@iptel.org>\n";
		mensaje+="To: <sip:jiri@bat.iptel.org>\n";
		mensaje+="Call-ID: d10815e0-bf17-4afa-8412-d9130a793d96@213.20.128.35\n";
		mensaje+="CSeq: 2 INVITE\n";
		mensaje+="Contact: <sip:213.20.128.35:9315>\n";
		mensaje+="User-Agent: Windows RTC/1.0\n";
		mensaje+="Content-Type: application/sdp\n";
		mensaje+="Content-Length: 451\n\n";
		mensaje+="c=IN IP4 213.20.128.35\n";
		mensaje+="m=audio 54742 RTP/AVP 97\n";
		mensaje+="a=rtpmap:97 red/8000\n";
		System.out.println(mensaje + "\n\n\n");

		try{
			bye = (ByeMessage) SIPMessage.parseMessage(mensaje);
			System.out.println("Imprimiendo mensaje procesado");
			System.out.println(bye.toStringMessage());
		} catch (Exception e){e.printStackTrace(System.out);};

	
	}

}