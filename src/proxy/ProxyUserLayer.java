package proxy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Date;

import common.FindMyIPv4;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.SIPMessage;
import mensajesSIP.TryingMessage;

public class ProxyUserLayer {
	private ProxyTransactionLayer transactionLayer;
	private ArrayList<User> registerList = new ArrayList<>(); 
	
	private OKMessage okMessage;
	private TryingMessage message100;
	private NotFoundMessage notFoundMessage;
	private User alice = new User("sip:alice@SMA");
	private User bob= new User("sip:bob@SMA");
	
	
	public ProxyUserLayer(int listenPort) throws SocketException {
		this.transactionLayer = new ProxyTransactionLayer(listenPort, this);
		this.registerList.add(alice);
		this.registerList.add(bob);
		this.okMessage = new OKMessage();
		this.notFoundMessage = new NotFoundMessage();
		this.message100 = new TryingMessage();
	}
	/*to do*/
	public void onInviteReceived(InviteMessage inviteMessage) throws IOException {
		System.out.println("Received INVITE from " + inviteMessage.getFromName());
		ArrayList<String> vias = inviteMessage.getVias();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);
		
		
		/*private String myAddress = FindMyIPv4.findMyIPv4Address().getHostAddress();*/

		
		message100.setCallId(inviteMessage.getCallId());
		message100.setcSeqNumber(inviteMessage.getcSeqNumber());
		message100.setcSeqStr(inviteMessage.getcSeqStr());
		message100.setFromName(inviteMessage.getFromName());
		message100.setFromUri(inviteMessage.getFromUri());
		message100.setToName(inviteMessage.getToName());
		message100.setToUri(inviteMessage.getToUri());
		message100.setContentLength(0);
		message100.setVias(vias);
		
		for (User usuario : registerList ) {
			if(usuario.getUri().equals(inviteMessage.getToUri())) {
				if (usuario.isActive()) {
					
				}
			}
		}

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

		for (User usuario : registerList ) {
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
	

	public void startListening() {
		transactionLayer.startListening();
	}
}
