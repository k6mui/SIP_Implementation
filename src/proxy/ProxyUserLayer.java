package proxy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

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

public class ProxyUserLayer {
	private ProxyTransactionLayer transactionLayer;
	private ArrayList<Usuario> registerList = new ArrayList<>();

	/* TO DO CAMBIAR ESO */
	private OKMessage okMessage;
	private TryingMessage message100;
	private NotFoundMessage notFoundMessage;
	private Usuario alice = new Usuario("sip:alice@SMA");
	private Usuario bob = new Usuario("sip:bob@SMA");
	private Usuario carlos = new Usuario("sip:carlos@SMA");
	private int listenPort;
	boolean debug;
	boolean loose;

	public ProxyUserLayer(int listenPort, boolean debug, boolean loose) throws SocketException {
		this.transactionLayer = new ProxyTransactionLayer(listenPort, this, loose);
		this.registerList.add(alice);
		this.registerList.add(bob);
		this.registerList.add(carlos);
		this.okMessage = new OKMessage();
		this.notFoundMessage = new NotFoundMessage();
		this.message100 = new TryingMessage();
		this.listenPort = listenPort;
		this.debug = debug;
		this.loose = loose;

	}

	/* to do */
	public boolean onInviteReceived(InviteMessage inviteMessage) throws IOException {
		System.out.println("Received INVITE from " + inviteMessage.getFromName());
		ArrayList<String> vias = inviteMessage.getVias();
		ArrayList<String> vias_100 = new ArrayList<String>();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);
		String destAddress = null;
		int destPort = 0;
		Date timeNow = new Date();
		String uri = inviteMessage.getToUri();
	
		vias_100.add(vias.get(0));
		
		String myAddress = FindMyIPv4.findMyIPv4Address().getHostAddress();

		message100.setCallId(inviteMessage.getCallId());
		message100.setcSeqNumber(inviteMessage.getcSeqNumber());
		message100.setcSeqStr(inviteMessage.getcSeqStr());
		message100.setFromName(inviteMessage.getFromName());
		message100.setFromUri(inviteMessage.getFromUri());
		message100.setToName(inviteMessage.getToName());
		message100.setToUri(inviteMessage.getToUri());
		message100.setContentLength(0);
		message100.setVias(vias_100);

		notFoundMessage.setCallId(inviteMessage.getCallId());
		notFoundMessage.setcSeqNumber(inviteMessage.getcSeqNumber());
		notFoundMessage.setcSeqStr(inviteMessage.getcSeqStr());
		notFoundMessage.setFromName(inviteMessage.getFromName());
		notFoundMessage.setFromUri(inviteMessage.getFromUri());
		notFoundMessage.setToName(inviteMessage.getToName());
		notFoundMessage.setToUri(inviteMessage.getToUri());
		notFoundMessage.setContentLength(0);
		notFoundMessage.setVias(vias);

		if (this.debug) {
			System.out.println(inviteMessage.toStringMessage());
		}

		inviteMessage.setMaxForwards(inviteMessage.getMaxForwards() - 1);
		vias.add(myAddress + ":" + listenPort);
		inviteMessage.setVias(vias);

		if (loose) {
			inviteMessage.setRecordRoute(myAddress + ":" + listenPort);
		}

		boolean bothOK = true;
		boolean sendRegisterAgain = false;

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(uri) && !usuario.isActive()) {
				bothOK = false;
			} else if (usuario.isActive()) {
				if (timeNow.after(usuario.getExpires())) {
					usuario.setActive(false);
					bothOK = false;
					if (usuario.getUri().equals(inviteMessage.getFromUri())) {
						sendRegisterAgain = true;
					}
				}
			}
		}

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(inviteMessage.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}

		if (bothOK) {
			transactionLayer.sendResponse(message100, originAddress, originPort);
			transactionLayer.sendInvite(inviteMessage, destAddress, destPort);
		} else {
			transactionLayer.sendResponse(notFoundMessage, originAddress, originPort);
		}

		return bothOK;
	}

	/* to do */
	public void onRegisterReceived(RegisterMessage registerMessage) throws IOException {

		String name = registerMessage.getFromName();
		String uri = registerMessage.getToUri();
		boolean enviado = false;
		Date currentDate = new Date();

		ArrayList<String> vias = registerMessage.getVias();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);

		/* DONE */
		okMessage.setCallId(registerMessage.getCallId());
		okMessage.setcSeqNumber(registerMessage.getcSeqNumber());
		okMessage.setcSeqStr(registerMessage.getcSeqStr());
		okMessage.setFromName(registerMessage.getFromName());
		okMessage.setFromUri(registerMessage.getFromUri());
		okMessage.setToName(registerMessage.getToName());
		okMessage.setToUri(registerMessage.getToUri());
		okMessage.setContact(registerMessage.getContact());
		okMessage.setContentLength(0);
		okMessage.setVias(vias);
		okMessage.setSdp(null);
		okMessage.setExpires(registerMessage.getExpires());

		notFoundMessage.setCallId(registerMessage.getCallId());
		notFoundMessage.setcSeqNumber(registerMessage.getcSeqNumber());
		notFoundMessage.setcSeqStr(registerMessage.getcSeqStr());
		notFoundMessage.setFromName(registerMessage.getFromName());
		notFoundMessage.setFromUri(registerMessage.getFromUri());
		notFoundMessage.setToName(registerMessage.getToName());
		notFoundMessage.setToUri(registerMessage.getToUri());
		notFoundMessage.setContentLength(0);
		notFoundMessage.setVias(vias);

		if (this.debug) {
			System.out.println(registerMessage.toStringMessage());
		}

		long milistoAdd = Integer.parseInt(registerMessage.getExpires()) * 1000;

		Date dateExpires = new Date(currentDate.getTime() + milistoAdd);

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(uri)) {
				enviado = true;
				if (!usuario.isActive()) {
					System.out.println("Received REGISTER from " + name);
					usuario.setActive(true);
					usuario.setAddress(originAddress);
					usuario.setPort(originPort);
					usuario.setExpires(dateExpires);
					transactionLayer.sendResponse(okMessage, originAddress, originPort);
				}
			}
		}

		if (!enviado) {
			transactionLayer.sendResponse(notFoundMessage, originAddress, originPort);
		}
	}

	public void onRingingMessage(RingingMessage ringingMessage) throws IOException {
		ArrayList<String> vias = new ArrayList<String>();
		String destAddress = null;
		int destPort = 0;

		vias.add(ringingMessage.getVias().get(0));
		ringingMessage.setVias(vias);

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(ringingMessage.getFromUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}

		if (this.debug) {
			System.out.println(ringingMessage.toStringMessage());
		}

		transactionLayer.sendResponse(ringingMessage, destAddress, destPort);

	}

	public void onOkMessage(OKMessage okMessage) throws IOException {
		ArrayList<String> vias = new ArrayList<String>();
		String destAddress = null;
		int destPort = 0;

		vias.add(okMessage.getVias().get(0));
		okMessage.setVias(vias);
		okMessage.setSdp(null);

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(okMessage.getFromUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}

		if (this.debug) {
			System.out.println(okMessage.toStringMessage());
		}

		transactionLayer.sendResponse(okMessage, destAddress, destPort);

	}

	public void onBusy(BusyHereMessage busyHere) throws IOException {
		ArrayList<String> vias = new ArrayList<String>();
		String destAddress = null;
		int destPort = 0;

		vias.add(busyHere.getVias().get(0));
		busyHere.setVias(vias);

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(busyHere.getFromUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}

		if (this.debug) {
			System.out.println(busyHere.toStringMessage());
		}

		transactionLayer.sendResponse(busyHere, destAddress, destPort);
	}

	public void onRequest(RequestTimeoutMessage requestM) throws IOException {
		ArrayList<String> vias = new ArrayList<String>();
		String destAddress = null;
		int destPort = 0;

		vias.add(requestM.getVias().get(0));
		requestM.setVias(vias);

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(requestM.getFromUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}

		if (this.debug) {
			System.out.println(requestM.toStringMessage());
		}

		transactionLayer.sendResponse(requestM, destAddress, destPort);
	}

	public void onAckReceived(ACKMessage ackMess) throws IOException {

		ArrayList<String> vias = ackMess.getVias();

		String myAddress = FindMyIPv4.findMyIPv4Address().getHostAddress();

		ackMess.setRoute(null);
		ackMess.setMaxForwards(ackMess.getMaxForwards() - 1);
		vias.add(myAddress + ":" + listenPort);
		ackMess.setVias(vias);

		String destAddress = null;
		int destPort = 0;

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(ackMess.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}
		transactionLayer.sendResponse(ackMess, destAddress, destPort);
	}

	public void onByeReceived(ByeMessage byeMessage) throws IOException {

		ArrayList<String> vias = byeMessage.getVias();

		String myAddress = FindMyIPv4.findMyIPv4Address().getHostAddress();

		byeMessage.setRoute(null);
		byeMessage.setMaxForwards(byeMessage.getMaxForwards() - 1);
		vias.add(myAddress + ":" + listenPort);
		byeMessage.setVias(vias);

		String destAddress = null;
		int destPort = 0;

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(byeMessage.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}
		transactionLayer.sendResponse(byeMessage, destAddress, destPort);
	}

	public void commandACK(BusyHereMessage busyhere) throws IOException {
		ACKMessage ackMessage = new ACKMessage();

		String destAddress = null;
		int destPort = 0;

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(busyhere.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}

		ackMessage.setDestination(busyhere.getToUri());
		ackMessage.setVias(busyhere.getVias());
		ackMessage.setMaxForwards(70);
		ackMessage.setFromName(busyhere.getFromName());
		ackMessage.setFromUri(busyhere.getFromUri());
		ackMessage.setcSeqNumber(busyhere.getcSeqNumber() + 1); // * ****************** PREGUNTAR MARIO*/
		ackMessage.setcSeqStr("ACK");
		ackMessage.setContentLength(0);
		ackMessage.setToUri(busyhere.getToUri());
		ackMessage.setToName(busyhere.getToName());

		if (this.debug) {
			System.out.println(ackMessage.toStringMessage());
		}

		transactionLayer.sendResponse(ackMessage, destAddress, destPort);

	}

	public void commandACK_408(RequestTimeoutMessage request) throws IOException {
		ACKMessage ackMessage = new ACKMessage();

		String destAddress = null;
		int destPort = 0;

		for (Usuario usuario : registerList) {
			if (usuario.getUri().equals(request.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}

		ackMessage.setDestination(request.getToUri());
		ackMessage.setVias(request.getVias());
		ackMessage.setMaxForwards(70);
		ackMessage.setFromName(request.getFromName());
		ackMessage.setFromUri(request.getFromUri());
		ackMessage.setcSeqNumber(request.getcSeqNumber() + 1); // * ****************** PREGUNTAR MARIO*/
		ackMessage.setcSeqStr("ACK");
		ackMessage.setContentLength(0);
		ackMessage.setToUri(request.getToUri());
		ackMessage.setToName(request.getToName());

		if (this.debug) {
			System.out.println(ackMessage.toStringMessage());
		}

		transactionLayer.sendResponse(ackMessage, destAddress, destPort);

	}

	public void startListening() {
		transactionLayer.startListening();
	}
}
