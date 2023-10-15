package proxy;

import java.io.IOException;
import java.net.SocketException;

import mensajesSIP.InviteMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.SIPMessage;

public class ProxyTransactionLayer {
	private static final int IDLE = 0; 	/*to do: Completar con los estados que faltan*/
	private int state = IDLE;

	private ProxyUserLayer userLayer;
	private ProxyTransportLayer transportLayer;

	public ProxyTransactionLayer(int listenPort, ProxyUserLayer userLayer) throws SocketException {
		this.userLayer = userLayer;
		this.transportLayer = new ProxyTransportLayer(listenPort, this);
	}

	public void onMessageReceived(SIPMessage sipMessage) throws IOException {
		if (sipMessage instanceof InviteMessage) {
			InviteMessage inviteMessage = (InviteMessage) sipMessage;
			switch (state) {
			case IDLE:
				userLayer.onInviteReceived(inviteMessage);
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		}
		
		else if (sipMessage instanceof RegisterMessage) {
			RegisterMessage registerMessage = (RegisterMessage) sipMessage;
			switch (state) {
			case IDLE:
				userLayer.onRegisterReceived(registerMessage);
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} 
		
		else {
			System.err.println("Unexpected message, throwing away");
		}
	}
	
	
	
	public void sendResponse(SIPMessage sipMessage, String address, int port) throws IOException {
		transportLayer.send(sipMessage, address, port);
	}

	
	
	public void echoInvite(InviteMessage inviteMessage, String address, int port) throws IOException {
		transportLayer.send(inviteMessage, address, port);
	}
	

	public void startListening() {
		transportLayer.startListening();
	}
}
