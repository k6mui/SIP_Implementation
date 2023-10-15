package proxy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.SIPMessage;

public class ProxyUserLayer {
	private ProxyTransactionLayer transactionLayer;
	private ArrayList<String> registerList = new ArrayList<>(); 
	
	private OKMessage okMessage;
	private NotFoundMessage notFoundMessage;
	
	
	public ProxyUserLayer(int listenPort) throws SocketException {
		this.transactionLayer = new ProxyTransactionLayer(listenPort, this);
		this.registerList.add("Bob");
		this.registerList.add("Alice");
		this.okMessage = new OKMessage();
		this.notFoundMessage = new NotFoundMessage();
	}
	/*to do*/
	public void onInviteReceived(InviteMessage inviteMessage) throws IOException {
		System.out.println("Received INVITE from " + inviteMessage.getFromName());
		ArrayList<String> vias = inviteMessage.getVias();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);
		transactionLayer.echoInvite(inviteMessage, originAddress, originPort);
	}
	/*to do*/
	public void onRegisterReceived(RegisterMessage registerMessage) throws IOException {
		
		String name = registerMessage.getFromName(); 
		String uri = registerMessage.getToUri();
		
		
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
		

		for (String usuario : registerList ) {
			if(usuario.equals(uri))
				transactionLayer.sendResponse(okMessage, originAddress, originPort);
			else
				transactionLayer.sendResponse(notFoundMessage, originAddress, originPort);
				
		}
	}
	

	public void startListening() {
		transactionLayer.startListening();
	}
}
