package ua;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

import common.FindMyIPv4;
import mensajesSIP.ACKMessage;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.ByeMessage;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.RingingMessage;
import mensajesSIP.SDPMessage;
import mensajesSIP.SIPMessage;
import mensajesSIP.TryingMessage;

public class UaUserLayer {
	private static final int IDLE = 0;
	private static final int CALL = 1;
	private static final int PROCC = 2;
	private static final int COMPL = 3;
	private static final int TERM = 4;
	
	public static final ArrayList<Integer> RTPFLOWS = new ArrayList<Integer>(
			Arrays.asList(new Integer[] { 96, 97, 98 }));

	private UaTransactionLayer transactionLayer;

	private String myAddress = FindMyIPv4.findMyIPv4Address().getHostAddress();
	private int rtpPort;
	private int listenPort;
	private String callId;
	private String t_expires;
	private boolean debug;
	private boolean responseRegister;
	private String uri;
	private String proxyAdd;
	private String uriDest;
	private String addrDest;
	private int portDest;
	
	private RingingMessage message180;
	private OKMessage message200;
	private BusyHereMessage message486;
	private ByeMessage messageBYE;

	

	private Process vitextClient = null;
	private Process vitextServer = null;

	public UaUserLayer(String uri, int listenPort, String proxyAddress, int proxyPort, boolean debug, String t_expires)
			throws SocketException, UnknownHostException {
		this.transactionLayer = new UaTransactionLayer(listenPort, proxyAddress, proxyPort, this);
		this.listenPort = listenPort;
		this.rtpPort = listenPort + 1;
		this.debug = debug;
		this.t_expires = t_expires;
		this.responseRegister = false;
		this.uri = uri;
		this.proxyAdd = proxyAddress;
		this.message180 = new RingingMessage();
		this.message200 = new OKMessage();
		this.message486 = new BusyHereMessage();
		this.uriDest = null;
		this.messageBYE = new ByeMessage();
	}

	public boolean isResponseRegister() {
		return responseRegister;
	}



	public void setResponseRegister(boolean responseRegister) {
		this.responseRegister = responseRegister;
	}



	public void onInviteReceived(InviteMessage inviteMessage) throws IOException {
		System.out.println(inviteMessage.getFromName() + " is calling you");
		//runVitextServer();
		ArrayList<String> vias = inviteMessage.getVias();
		
		String[] split = inviteMessage.getFromUri().split(":");
		this.uriDest = split[1];
		this.createBYE(inviteMessage);
		String[] args = inviteMessage.getContact().split(":");
		
		this.addrDest = args[0];
		this.portDest = Integer.parseInt(args[1]);
		
		RingingMessage message180 = new RingingMessage();
		message180.setCallId(inviteMessage.getCallId());
		message180.setcSeqNumber(inviteMessage.getcSeqNumber());
		message180.setcSeqStr(inviteMessage.getcSeqStr());
		message180.setFromName(inviteMessage.getFromName());
		message180.setFromUri(inviteMessage.getFromUri());
		message180.setToName(inviteMessage.getToName());
		message180.setToUri(inviteMessage.getToUri());
		message180.setContentLength(0);
		vias.add(myAddress + ":" + listenPort);
		message180.setVias(vias); 
		
		transactionLayer.send180(message180);
				
		message200.setCallId(inviteMessage.getCallId());
		message200.setcSeqNumber(inviteMessage.getcSeqNumber());
		message200.setcSeqStr(inviteMessage.getcSeqStr());
		message200.setFromName(inviteMessage.getFromName());
		message200.setFromUri(inviteMessage.getFromUri());
		message200.setToName(inviteMessage.getToName());
		message200.setToUri(inviteMessage.getToUri());
		message200.setContact(myAddress + ":" + listenPort);
		message200.setContentLength(0);
		message200.setVias(vias);
		message200.setSdp(inviteMessage.getSdp());
		
				
		message486.setCallId(inviteMessage.getCallId());
		message486.setcSeqNumber(inviteMessage.getcSeqNumber());
		message486.setcSeqStr(inviteMessage.getcSeqStr());
		message486.setFromName(inviteMessage.getFromName());
		message486.setFromUri(inviteMessage.getFromUri());
		message486.setToName(inviteMessage.getToName());
		message486.setToUri(inviteMessage.getToUri());
		message486.setContentLength(0);
		message486.setVias(vias);
		

	}
	
	public void onByeReceived(ByeMessage messageBye) throws IOException {
		

		ArrayList<String> vias = messageBye.getVias();
		
		
		message200.setCallId(messageBye.getCallId());
		message200.setcSeqNumber(messageBye.getcSeqNumber());
		message200.setcSeqStr(messageBye.getcSeqStr());
		message200.setFromName(messageBye.getFromName());
		message200.setFromUri(messageBye.getFromUri());
		message200.setToName(messageBye.getToName());
		message200.setToUri(messageBye.getToUri());
		message200.setContact(myAddress + ":" + listenPort);
		message200.setContentLength(0);
		message200.setVias(vias);
	
		transactionLayer.send200_direct(message200, this.addrDest , this.portDest );
	}
	
	/* To Do*/
	public void onOkReceived(OKMessage oKMessage) throws IOException {
		String[] args = oKMessage.getContact().split(":");
		
		this.addrDest = args[0];
		this.portDest = Integer.parseInt(args[1]);
		System.out.println("Received 200 OK from " + oKMessage.getFromName());
	}
	
	public void onNFReceived( ) throws IOException {
		System.out.println("Received 404 Not Found ");
	}
	
	public void onTrying( ) throws IOException {
		System.out.println("Received 100 Trying Message :)");
	}
	public void onRinging(RingingMessage ringingMessage ) throws IOException {
		System.out.println("PIII PIII PIII PIII --->  LLamando a " + ringingMessage.getToName());
	}
	
	public void onBusy(BusyHereMessage busyHereMessage) throws IOException {
		System.out.println(busyHereMessage.getToName() + "Esta buuuuusy :(" );
	}
	/* To Do*/
	
	public void startListeningNetwork() {
		transactionLayer.startListeningNetwork();
	}

	public void startListeningKeyboard() {
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				prompt();
				String line = scanner.nextLine();
				if (!line.isEmpty()) {
					command(line);
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private void prompt() {
		System.out.println("");
		int estado = transactionLayer.getState();
		int whoiam = transactionLayer.getWhoIam();
		switch (estado) {
		case IDLE:
			promptIdle();
			break;
		case CALL:
			break;
		case PROCC:
			if (whoiam==2) {
				System.out.println("Enter [YES/NO] to accept/decline the call: ");}
			break;
		case COMPL:	
			break;
		case TERM:
			System.out.println("Enter [BYE] to finish the call: ");
			break;
		default:
			throw new IllegalStateException("Unexpected state: " + estado);
		}
		System.out.print("> ");
	}

	private void promptIdle() {
		System.out.println("INVITE xxx");
	}
/*TO DO LA PARTE DE AÑADIR MAS MENSAJES AL LEER*/
	private void command(String line) throws IOException {
		if (line.startsWith("INVITE")) {
			commandInvite(line);
		}
		else if (line.startsWith("YES")) {
			command200(line);
		}else if (line.startsWith("NO")){
			command486(line);
		}else if (line.startsWith("BYE")){
			commandBYE(line);
		}else {
			System.out.println("Bad command");
		}
	}

	private void commandInvite(String line) throws IOException {
		//stopVitextServer();
		//stopVitextClient();
		
		
		String[] args = line.split(" ");
		
		this.uriDest = args[1];
		
		
		System.out.println("Inviting...");

		//runVitextClient();
		
		callId = UUID.randomUUID().toString();

		SDPMessage sdpMessage = new SDPMessage();
		sdpMessage.setIp(this.myAddress);
		sdpMessage.setPort(this.rtpPort);
		sdpMessage.setOptions(RTPFLOWS);

		InviteMessage inviteMessage = new InviteMessage();
		inviteMessage.setDestination("sip:" + args[1]);   // Poner ejemplo@SMA
		inviteMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		inviteMessage.setMaxForwards(70);
		inviteMessage.setToName(extractName(args[1]));
		inviteMessage.setToUri("sip:"+ args[1]);
		inviteMessage.setFromName(extractName(uri));
		inviteMessage.setFromUri("sip:" + uri);
		inviteMessage.setCallId(callId);
		inviteMessage.setcSeqNumber("1");
		inviteMessage.setcSeqStr(args[0]);    // Poner en Mayuscula el INVITE
		inviteMessage.setContact(myAddress + ":" + listenPort);
		inviteMessage.setContentType("application/sdp");
		inviteMessage.setContentLength(sdpMessage.toStringMessage().getBytes().length);
		inviteMessage.setSdp(sdpMessage);
		
		this.createBYE(inviteMessage);

		transactionLayer.call(inviteMessage);
	}
	
	/*TO DO*/
	public void commandRegister(String line) throws IOException { // Preguntar al profe !!!!!!!!!!!!!
		//stopVitextServer();
		//stopVitextServer();    /*No se si mantener esto*/
		
		System.out.println("Registering...");
		
		
		callId = UUID.randomUUID().toString(); 

		RegisterMessage registerMessage = new RegisterMessage();
		registerMessage.setDestination("sip:" + proxyAdd  + "@SMA");
		registerMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		registerMessage.setMaxForwards(70);
		registerMessage.setToName(extractName(uri)); 
		registerMessage.setToUri("sip:"+uri); 
		registerMessage.setFromName(extractName(uri)); 
		registerMessage.setFromUri("sip:"+uri);
		registerMessage.setCallId(callId);
		registerMessage.setcSeqNumber("1");
		registerMessage.setcSeqStr("REGISTER");
		registerMessage.setContact(myAddress + ":" + listenPort);
		registerMessage.setExpires(this.t_expires);

		transactionLayer.register(registerMessage);
	}
	
	private void command200(String line) throws IOException {
		/*SEND 200 CREADO EN EL INVITERECEIVED*/
		transactionLayer.send200(message200);

	}
	
	private void command486(String line) throws IOException {
		transactionLayer.send486(message486);
	}
	
	private void commandBYE(String line) throws IOException {
		transactionLayer.sendBYE(messageBYE, this.addrDest, this.portDest);
	}
	
	public void commandACK_OK(OKMessage okMessage) throws IOException {
		 ACKMessage ackMessage = new ACKMessage();
		 
		 
		 ackMessage.setDestination("sip:" + uriDest );
		 ackMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		 ackMessage.setMaxForwards(70);
		 ackMessage.setFromName(extractName(uri)); 
		 ackMessage.setFromUri("sip:"+uri);
		 ackMessage.setcSeqNumber("1");
		 ackMessage.setcSeqStr("ACK");
		 ackMessage.setContentLength(0);
		 ackMessage.setToUri("sip:"+this.uriDest); 
		 ackMessage.setToName(extractName(this.uriDest)); 

		transactionLayer.sendACK_OK(ackMessage, this.addrDest, this.portDest);

	}
	
	public void commandACK() throws IOException {
		 ACKMessage ackMessage = new ACKMessage();
		 
		 ackMessage.setDestination("sip:" + proxyAdd  + "@SMA" );
		 ackMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		 ackMessage.setMaxForwards(70);
		 ackMessage.setFromName(extractName(uri)); 
		 ackMessage.setFromUri("sip:"+uri);
		 ackMessage.setcSeqNumber("1");                      //*  ******************   PREGUNTAR MARIO*/
		 ackMessage.setcSeqStr("ACK");
		 ackMessage.setContentLength(0);
		 ackMessage.setToUri("sip:"+this.uriDest); 
		 ackMessage.setToName(extractName(this.uriDest)); 

		transactionLayer.sendACK(ackMessage);

	}
	
	
	private void runVitextClient() throws IOException {
		vitextClient = Runtime.getRuntime().exec("xterm -e vitext/vitextclient -p 5000 239.1.2.3");
	}

	private void stopVitextClient() {
		if (vitextClient != null) {
			vitextClient.destroy();
		} 
	}

	private void runVitextServer() throws IOException {
		vitextServer = Runtime.getRuntime().exec("xterm -iconic -e vitext/vitextserver -r 10 -p 5000 vitext/1.vtx 239.1.2.3");
	}

	private void stopVitextServer() {
		if (vitextServer != null) {
			vitextServer.destroy();
		}
	}
	
	private void createBYE(InviteMessage inviteMessage){
		
		messageBYE.setDestination("sip:" + uriDest );
		messageBYE.setCallId(inviteMessage.getCallId());
		messageBYE.setcSeqNumber(String.valueOf(Integer.parseInt(inviteMessage.getcSeqNumber()) +1));
		messageBYE.setcSeqStr("BYE");
		messageBYE.setMaxForwards(70);
		messageBYE.setFromName(extractName(uri)); 
		messageBYE.setFromUri("sip:"+uri);
		messageBYE.setToUri("sip:"+this.uriDest); 
		messageBYE.setToName(extractName(this.uriDest)); 
		messageBYE.setContentLength(0);
		messageBYE.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		
		
	}
	
	private static String extractName(String uri) {
		if (uri.contains("@")) {
			String[] partes = uri.split("@");
			return partes[0];
		} else {
			return "Error: Not @";
		}
	}
}
