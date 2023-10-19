package ua;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;

import common.FindMyIPv4;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.SDPMessage;

public class UaUserLayer {
	private static final int IDLE = 0;
	private int state = IDLE;

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
	}

	public boolean isResponseRegister() {
		return responseRegister;
	}



	public void setResponseRegister(boolean responseRegister) {
		this.responseRegister = responseRegister;
	}



	public void onInviteReceived(InviteMessage inviteMessage) throws IOException {
		System.out.println("Received INVITE from " + inviteMessage.getFromName());
		runVitextServer();
	}
	
	/* To Do*/
	public void onOkReceived(OKMessage oKMessage) throws IOException {
		System.out.println("Received 200 OK from " + oKMessage.getFromName());
	}
	
	public void onNFReceived(NotFoundMessage notFoundMessage) throws IOException {
		System.out.println("Received 404 Not Found ");
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
		switch (state) {
		case IDLE:
			promptIdle();
			break;
		default:
			throw new IllegalStateException("Unexpected state: " + state);
		}
		System.out.print("> ");
	}

	private void promptIdle() {
		System.out.println("INVITE xxx");
	}

	private void command(String line) throws IOException {
		if (line.startsWith("INVITE")) {
			commandInvite(line);
		} else {
			System.out.println("Bad command");
		}
	}

	private void commandInvite(String line) throws IOException {
		//stopVitextServer();
		//stopVitextClient();
		
		String[] args = line.split(" ");
		
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
	
	
	private static String extractName(String uri) {
		if (uri.contains("@")) {
			String[] partes = uri.split("@");
			return partes[0];
		} else {
			return "Error: Not @";
		}
	}
}
