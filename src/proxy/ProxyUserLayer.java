package proxy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

import common.FindMyIPv4;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.RingingMessage;
import mensajesSIP.SIPMessage;
import mensajesSIP.TryingMessage;

public class ProxyUserLayer {
	private ProxyTransactionLayer transactionLayer;
	private ArrayList<Usuario> registerList = new ArrayList<>(); 
	
	/*TO DO CAMBIAR ESO*/
	private OKMessage okMessage;
	private TryingMessage message100;
	private NotFoundMessage notFoundMessage;
	private Usuario alice = new Usuario("sip:alice@SMA");
	private Usuario bob= new Usuario("sip:bob@SMA");
	private int listenPort;
	
	
	public ProxyUserLayer(int listenPort) throws SocketException {
		this.transactionLayer = new ProxyTransactionLayer(listenPort, this);
		this.registerList.add(alice);
		this.registerList.add(bob);
		this.okMessage = new OKMessage();
		this.notFoundMessage = new NotFoundMessage();
		this.message100 = new TryingMessage();
		this.listenPort = listenPort;
	}
	/*to do*/
	public boolean onInviteReceived(InviteMessage inviteMessage) throws IOException {
		System.out.println("Received INVITE from " + inviteMessage.getFromName());
		ArrayList<String> vias = inviteMessage.getVias();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);
		String destAddress = null;
		int destPort = 0;
		
		String myAddress = FindMyIPv4.findMyIPv4Address().getHostAddress();

		
		message100.setCallId(inviteMessage.getCallId());
		message100.setcSeqNumber(inviteMessage.getcSeqNumber());
		message100.setcSeqStr(inviteMessage.getcSeqStr());
		message100.setFromName(inviteMessage.getFromName());
		message100.setFromUri(inviteMessage.getFromUri());
		message100.setToName(inviteMessage.getToName());
		message100.setToUri(inviteMessage.getToUri());
		message100.setContentLength(0);
		message100.setVias(vias); 
		
		notFoundMessage.setCallId(inviteMessage.getCallId());
		notFoundMessage.setcSeqNumber(inviteMessage.getcSeqNumber());
		notFoundMessage.setcSeqStr(inviteMessage.getcSeqStr());
		notFoundMessage.setFromName(inviteMessage.getFromName());
		notFoundMessage.setFromUri(inviteMessage.getFromUri());
		notFoundMessage.setToName(inviteMessage.getToName());
		notFoundMessage.setToUri(inviteMessage.getToUri());
		notFoundMessage.setContentLength(0);
		notFoundMessage.setVias(vias);
		
		inviteMessage.setMaxForwards(inviteMessage.getMaxForwards()+1);
		vias.add(myAddress + ":" + listenPort);
		inviteMessage.setVias(vias);
		
		boolean bothOK = true;
		for (Usuario usuario : registerList ) {
			if (!usuario.isActive()) {
				bothOK = false;
			}
		}
		
		for (Usuario usuario : registerList ) {
			if(usuario.getUri().equals(inviteMessage.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}
		
		
		if (bothOK) {
			transactionLayer.sendResponse(message100, originAddress, originPort);
			transactionLayer.sendInvite(inviteMessage, destAddress , destPort);
		}else {
			transactionLayer.sendResponse(notFoundMessage, originAddress, originPort);
		}
		
		return bothOK;
	}
	/*to do*/
	public void onRegisterReceived(RegisterMessage registerMessage) throws IOException {
		
		String name = registerMessage.getFromName(); 
		String uri = registerMessage.getToUri();
		boolean enviado = false;
		Date currentDate = new Date();
		
		
		System.out.println("Received REGISTER from " + name);
		ArrayList<String> vias = registerMessage.getVias();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);
		
		/*DONE*/
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
		
		long milistoAdd = Integer.parseInt(registerMessage.getExpires())*1000; 
		
		Date dateExpires = new Date(currentDate.getTime()+ milistoAdd);

		for (Usuario usuario : registerList ) {
			if(usuario.getUri().equals(uri)) {
				transactionLayer.sendResponse(okMessage, originAddress, originPort);
				enviado = true;
				usuario.setActive(true);
				usuario.setAddress(originAddress);
				usuario.setPort(originPort);
				usuario.setExpires(dateExpires);
			}
		}
		
		if (!enviado){
			transactionLayer.sendResponse(notFoundMessage, originAddress, originPort);
		}
	}
	
	public void onRingingMessage(RingingMessage ringingMessage) throws IOException {
		ArrayList<String> vias = ringingMessage.getVias();
		
		String destAddress = null;
		int destPort = 0;
		
		vias.remove(vias.lastIndexOf(vias));
		ringingMessage.setVias(vias);

		for (Usuario usuario : registerList ) {
			if(usuario.getUri().equals(ringingMessage.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}
		
		transactionLayer.sendResponse(ringingMessage, destAddress, destPort);
	
	}
	
	public void onOkMessage(OKMessage okMessage) throws IOException {
		ArrayList<String> vias = okMessage.getVias();
		String destAddress = null;
		int destPort = 0;
		
		vias.remove(vias.lastIndexOf(vias));
		okMessage.setVias(vias);
		
		for (Usuario usuario : registerList ) {
			if(usuario.getUri().equals(okMessage.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}
		
		transactionLayer.sendResponse(okMessage, destAddress, destPort);
		
	}
	
	public void onBusy(BusyHereMessage busyHere) throws IOException {
		ArrayList<String> vias = busyHere.getVias();
		String destAddress = null;
		int destPort = 0;
		
		vias.remove(vias.lastIndexOf(vias));
		busyHere.setVias(vias);
		
		for (Usuario usuario : registerList ) {
			if(usuario.getUri().equals(busyHere.getToUri())) {
				destAddress = usuario.getAddress();
				destPort = usuario.getPort();
			}
		}
		
		transactionLayer.sendResponse(busyHere, destAddress, destPort);
		
	}

	
	
	public void startListening() {
		transactionLayer.startListening();
	}
}
