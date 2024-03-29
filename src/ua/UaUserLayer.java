package ua;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import common.FindMyIPv4;
import mensajesSIP.ACKMessage;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.ByeMessage;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.RequestTimeoutMessage;
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
	// private String myAddress = "163.117.172.173";
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
	private int cSeq;
	private int proxyPort;
	private boolean looseRouting = false;
	private String multicastAddress = "239.1.2.3";

	private RingingMessage message180;
	private OKMessage message200;
	private BusyHereMessage message486;
	private ByeMessage messageBYE;
	private RequestTimeoutMessage message408;
	Timer timer408 = new Timer();


	private Process vitextClient = null;
	private Process vitextServer = null;

	public UaUserLayer(String uri, int listenPort, String proxyAddress, int proxyPort, boolean debug, String t_expires)
			throws SocketException, UnknownHostException {
		this.transactionLayer = new UaTransactionLayer(listenPort, proxyAddress, proxyPort, this);
		this.proxyPort = proxyPort;
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
		this.message408 = new RequestTimeoutMessage();
		this.uriDest = null;
		this.messageBYE = new ByeMessage();
		this.cSeq = 0;
	}

	public boolean isResponseRegister() {
		return responseRegister;
	}

	public void setResponseRegister(boolean responseRegister) {
		this.responseRegister = responseRegister;
	}

	public void onInviteReceived(InviteMessage inviteMessage) throws IOException {
		timer408 = new Timer();

		System.out.println(inviteMessage.getFromName() + " is calling you");
		runVitextServer();
		ArrayList<String> vias = inviteMessage.getVias();

		String[] split = inviteMessage.getFromUri().split(":");
		this.uriDest = split[1];
		this.createBYE(inviteMessage);
		String[] args = inviteMessage.getContact().split(":");

		this.addrDest = args[0];
		this.portDest = Integer.parseInt(args[1]);

		if (this.debug) {
			System.out.println(inviteMessage.toStringMessage());
		}

		RingingMessage message180 = new RingingMessage();
		message180.setCallId(inviteMessage.getCallId());
		message180.setcSeqNumber(inviteMessage.getcSeqNumber());
		message180.setcSeqStr(inviteMessage.getcSeqStr());
		message180.setFromName(inviteMessage.getFromName());
		message180.setFromUri(inviteMessage.getFromUri());
		message180.setToName(inviteMessage.getToName());
		message180.setToUri(inviteMessage.getToUri());
		message180.setContentLength(0);
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

		if (inviteMessage.getRecordRoute() != null) {
			message200.setRecordRoute(inviteMessage.getRecordRoute());
			looseRouting = true;

		}
		
		runVitextServer();

		message486.setCallId(inviteMessage.getCallId());
		message486.setcSeqNumber(inviteMessage.getcSeqNumber());
		message486.setcSeqStr(inviteMessage.getcSeqStr());
		message486.setFromName(inviteMessage.getFromName());
		message486.setFromUri(inviteMessage.getFromUri());
		message486.setToName(inviteMessage.getToName());
		message486.setToUri(inviteMessage.getToUri());
		message486.setContentLength(0);
		message486.setVias(vias);

		message408.setCallId(inviteMessage.getCallId());
		message408.setcSeqNumber(inviteMessage.getcSeqNumber());
		message408.setcSeqStr(inviteMessage.getcSeqStr());
		message408.setFromName(inviteMessage.getFromName());
		message408.setFromUri(inviteMessage.getFromUri());
		message408.setToName(inviteMessage.getToName());
		message408.setToUri(inviteMessage.getToUri());
		message408.setContentLength(0);
		message408.setVias(vias);

		TimerTask task408 = new TimerTask() {
			public void run() {
				// Place the action you want to execute after 2 seconds here.
				try {
					transactionLayer.send408(message408);
				} catch (IOException e) {
					e.printStackTrace();
				}
				;
			}
		};
		timer408.schedule(task408, 10000);

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

		if (this.debug) {
			System.out.println(messageBye.toStringMessage());
		}

		if (looseRouting) {
			transactionLayer.send200_direct(message200, this.proxyAdd, this.proxyPort); // Se manda directo al Proxy
		} else {
			transactionLayer.send200_direct(message200, this.addrDest, this.portDest);
		}
	}

	/* To Do */
	public void onOkReceived(OKMessage oKMessage) throws IOException {
		String[] args = oKMessage.getContact().split(":");

		this.addrDest = args[0];
		this.portDest = Integer.parseInt(args[1]);
		System.out.println("Received 200 OK from " + oKMessage.getFromName());
		oKMessage.setSdp(null);

		if (this.debug) {
			System.out.println(oKMessage.toStringMessage());
		}

		if (oKMessage.getRecordRoute() != null)
			looseRouting = true;
	}

	public void onNFReceived(NotFoundMessage messageNotFound) throws IOException {
		System.err.println("Received 404 Not Found ");

		if (this.debug) {
			System.out.println(messageNotFound.toStringMessage());
		}
	}

	public void onTrying(TryingMessage messageTrying) throws IOException {
		System.out.println("Received 100 Trying Message :)");

		if (this.debug) {
			System.out.println(messageTrying.toStringMessage());
		}
	}

	public void onRinging(RingingMessage ringingMessage) throws IOException {
		System.out.println("PIII PIII PIII PIII --->  LLamando a " + ringingMessage.getToName() + " (180 Ringing)");

		if (this.debug) {
			System.out.println(ringingMessage.toStringMessage());
		}
	}

	public void onBusy(BusyHereMessage busyHereMessage) throws IOException {
		System.err.println(busyHereMessage.getToName() + " esta buuuuusy :(  (486 Busy Here)");

		if (this.debug) {
			System.out.println(busyHereMessage.toStringMessage());
		}
	}

	public void startListeningNetwork() {
		transactionLayer.startListeningNetwork();
	}

	public void startListeningKeyboard() {
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				prompt(); // Muestra al usuario lo que tiene que mostrar segun el estado de transacion
							// layer
				String line = scanner.nextLine(); // Lee la línea completa de entrada del usuario 'line'
				if (!line.isEmpty()) { // Si la linea no está vacia se llama a command(line)

					command(line); // Procesa el comando ingresado
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
			if (whoiam == 2) {
				System.out.println("Enter [YES/NO] to accept/decline the call: ");
			}
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
		System.out.println("INVITE xxx@SMA");
	}

	private void command(String line) throws IOException {
		if (line.startsWith("INVITE")) {
			commandInvite(line);
		} else if (line.startsWith("YES")) {
			timer408.cancel();
			command200(line);
		} else if (line.startsWith("NO")) {
			timer408.cancel();
			command486(line);
		} else if (line.startsWith("BYE")) {
			commandBYE(line);
		} else {
			System.out.println("Bad command");
		}
	}

	private void commandInvite(String line) throws IOException {
		stopVitextServer();
		stopVitextClient();

		String[] args = line.split(" ");

		this.uriDest = args[1];

		System.out.println("Inviting...");

		runVitextClient();

		callId = UUID.randomUUID().toString();

		SDPMessage sdpMessage = new SDPMessage();
		sdpMessage.setIp(multicastAddress);
		sdpMessage.setPort(this.rtpPort);
		sdpMessage.setOptions(RTPFLOWS);

		InviteMessage inviteMessage = new InviteMessage();
		inviteMessage.setDestination("sip:" + args[1]); // Poner ejemplo@SMA
		inviteMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		inviteMessage.setMaxForwards(70);
		inviteMessage.setToName(extractName(args[1]));
		inviteMessage.setToUri("sip:" + args[1]);
		inviteMessage.setFromName(extractName(uri));
		inviteMessage.setFromUri("sip:" + uri);
		inviteMessage.setCallId(callId);
		inviteMessage.setcSeqNumber(String.valueOf(cSeq++));
		inviteMessage.setcSeqStr(args[0]); // Poner en Mayuscula el INVITE
		inviteMessage.setContact(myAddress + ":" + listenPort);
		inviteMessage.setContentType("application/sdp");
		inviteMessage.setContentLength(sdpMessage.toStringMessage().getBytes().length);
		inviteMessage.setSdp(sdpMessage);

		this.createBYE(inviteMessage);

		transactionLayer.call(inviteMessage);
	}

	public void commandRegister(String line) throws IOException {

		System.out.println("Registering...");

		callId = UUID.randomUUID().toString();

		RegisterMessage registerMessage = new RegisterMessage();
		registerMessage.setDestination("sip:" + proxyAdd + "@SMA");
		registerMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		registerMessage.setMaxForwards(70);
		registerMessage.setToName(extractName(uri));
		registerMessage.setToUri("sip:" + uri);
		registerMessage.setFromName(extractName(uri));
		registerMessage.setFromUri("sip:" + uri);
		registerMessage.setCallId(callId);
		registerMessage.setcSeqNumber(String.valueOf(cSeq++));
		registerMessage.setcSeqStr("REGISTER");
		registerMessage.setContact(myAddress + ":" + listenPort);
		registerMessage.setExpires(this.t_expires);

		transactionLayer.register(registerMessage);
	}

	private void command200(String line) throws IOException {
		transactionLayer.send200(message200);

	}

	private void command486(String line) throws IOException {
		transactionLayer.send486(message486);
	}

	private void commandBYE(String line) throws IOException {
		stopVitextServer();
		stopVitextClient();
		if (looseRouting) {
			messageBYE.setRoute(proxyAdd + ":" + proxyPort);
			transactionLayer.sendBYE_loose(messageBYE);
		} else {
			transactionLayer.sendBYE(messageBYE, this.addrDest, this.portDest);
		}
	}

	public void commandACK_OK(OKMessage okMessage) throws IOException {
		ACKMessage ackMessage = new ACKMessage();

		ackMessage.setDestination("sip:" + uriDest);
		ackMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		ackMessage.setMaxForwards(70);
		ackMessage.setFromName(extractName(uri));
		ackMessage.setFromUri("sip:" + uri);
		ackMessage.setcSeqNumber(String.valueOf(cSeq++));
		ackMessage.setcSeqStr("ACK");
		ackMessage.setContentLength(0);
		ackMessage.setToUri("sip:" + this.uriDest);
		ackMessage.setToName(extractName(this.uriDest));

		if (looseRouting) {
			ackMessage.setRoute(proxyAdd + ":" + proxyPort);
			transactionLayer.sendACK(ackMessage);
		} else {
			transactionLayer.sendACK_OK(ackMessage, this.addrDest, this.portDest);
		}
	}

	public void commandACK() throws IOException {
		ACKMessage ackMessage = new ACKMessage();

		ackMessage.setDestination("sip:" + proxyAdd + "@SMA");
		ackMessage.setVias(new ArrayList<String>(Arrays.asList(this.myAddress + ":" + this.listenPort)));
		ackMessage.setMaxForwards(70);
		ackMessage.setFromName(extractName(uri));
		ackMessage.setFromUri("sip:" + uri);
		ackMessage.setcSeqNumber(String.valueOf(cSeq++)); 
		ackMessage.setcSeqStr("ACK");
		ackMessage.setContentLength(0);
		ackMessage.setToUri("sip:" + this.uriDest);
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
		vitextServer = Runtime.getRuntime()
				.exec("xterm -iconic -e vitext/vitextserver -r 10 -p 5000 vitext/1.vtx 239.1.2.3");
	}

	private void stopVitextServer() {
		if (vitextServer != null) {
			vitextServer.destroy();
		}
	}

	private void createBYE(InviteMessage inviteMessage) {

		messageBYE.setDestination("sip:" + uriDest);
		messageBYE.setCallId(inviteMessage.getCallId());
		messageBYE.setcSeqNumber(String.valueOf(Integer.parseInt(inviteMessage.getcSeqNumber()) + 1));
		messageBYE.setcSeqStr("BYE");
		messageBYE.setMaxForwards(70);
		messageBYE.setFromName(extractName(uri));
		messageBYE.setFromUri("sip:" + uri);
		messageBYE.setToUri("sip:" + this.uriDest);
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

	public void onReq(RequestTimeoutMessage requestM) {
		System.out.println(requestM.getToName() + " no responde (408)");
	}
}
