package proxy;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;

import mensajesSIP.InviteMessage;
import mensajesSIP.RegisterMessage;

public class ProxyUserLayer {
	private ProxyTransactionLayer transactionLayer;

	public ProxyUserLayer(int listenPort) throws SocketException {
		this.transactionLayer = new ProxyTransactionLayer(listenPort, this);
	}

	public void onInviteReceived(InviteMessage inviteMessage) throws IOException {
		System.out.println("Received INVITE from " + inviteMessage.getFromName());
		ArrayList<String> vias = inviteMessage.getVias();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);
		transactionLayer.echoInvite(inviteMessage, originAddress, originPort);
	}
	/*DONE*/
	public void onRegisterReceived(RegisterMessage registerMessage) throws IOException {
		System.out.println("Received REGISTER from " + registerMessage.getFromName());
		ArrayList<String> vias = registerMessage.getVias();
		String origin = vias.get(0);
		String[] originParts = origin.split(":");
		String originAddress = originParts[0];
		int originPort = Integer.parseInt(originParts[1]);
		
	}
	
	

	public void startListening() {
		transactionLayer.startListening();
	}
}
