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
public class SDPMessage {

    private String ip;
    private int port;
    private ArrayList<Integer> options;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

/**
 * Recupera el ArrayList que contiene las opciones ofrecidas en el mensaje SDP.
 * <p>Las opciones pueden ser 96|97|98.</p>
 * <p>96 genera la linea a=rtpmap:96 L8/8000 </p>
 * <p>97 genera la linea a=rtpmap:97 L16/8000 </p>
 * <p>98 genera la linea a=rtpmap:98 L8/11025/2 </p>
 * 
 * @return      el ArrayList con las opciones
 */

    public ArrayList<Integer> getOptions() {
        return options;
    }

/**
 * Establece el ArrayList que contiene las opciones ofrecidas en el mensaje SDP.
 * <p>Las opciones pueden ser 96|97|98.</p>
 * <p>96 genera la linea a=rtpmap:96 L8/8000 </p>
 * <p>97 genera la linea a=rtpmap:97 L16/8000 </p>
 * <p>98 genera la linea a=rtpmap:98 L8/11025/2 </p>
 * 
 * @param options     el ArrayList con las opciones
 */

    public void setOptions(ArrayList<Integer> options) {
        this.options = options;
    }

    public void parseMessage(String message) throws SIPException {
        String[] lines = message.split("\n");
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("c")) {
                parseC(lines[i]);
            } else if (lines[i].startsWith("m")) {
                parseM(lines[i]);
            }
        }
    }

/**
 * Convierte el mensaje en un String. Para ello concatena la información de las opciones c y m con la IP, puerto y opciones de los atributos de la clase.
 * 
 * @return      el mensaje como String.
 */

    public String toStringMessage() {
        String sdp = "";
        String a = "";
        
        sdp = "c=IN IP4 " + ip + "\n";
        sdp += "m=audio " + port + " RTP/AVP";
        for (int i = 0; i < options.size(); i++) {
            sdp += " " + options.get(i);
            if (options.get(i) == 96) {
                a += "a=rtpmap:96 L8/8000\n";
            } else if (options.get(i) == 97) {
                a += "a=rtpmap:97 L16/8000\n";
            } else if (options.get(i) == 98) {
                a += "a=rtpmap:98 L8/11025/2\n";
            }
        }
        sdp += "\n";
        sdp += a;

        return sdp;
    }

    private void parseC(String line) throws SIPException {
        Pattern pattern = Pattern.compile("c=IN IP4 ([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3})");
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            ip = matcher.group(1);
        } else {
            throw new SIPException("Malformed SDP payload");
        }
    }

    private void parseM(String line) throws SIPException {
        Pattern pattern = Pattern.compile("m=audio (\\d+) RTP/AVP (96|97|98) ?(96|97|98)? ?(96|97|98)?");
        
        //System.out.println(line);
        Matcher matcher = pattern.matcher(line);

        if (matcher.matches()) {
            port = Integer.parseInt(matcher.group(1));
            ArrayList<Integer> options = new ArrayList<Integer>();
            for (int i = 2; i <= matcher.groupCount(); i++) {
                if(matcher.group(i)!=null)
                    options.add(Integer.parseInt(matcher.group(i)));
            }
            this.options = options;
        } else {
            throw new SIPException("Malformed SDP payload");
        }
    }
}
