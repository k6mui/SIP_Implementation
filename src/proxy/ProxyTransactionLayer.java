package proxy;

import java.io.IOException;
import java.net.SocketException;

import mensajesSIP.InviteMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.SIPMessage;

public class ProxyTransactionLayer {
	private static final int IDLE = 0; 	/*to do: Completar con los estados que faltan*/
	private static final int CALL = 1;
	private static final int PROCC = 2;
	private static final int COMPL = 3;
	private int stateA = IDLE;
	private int stateB = IDLE;

	private ProxyUserLayer userLayer;
	private ProxyTransportLayer transportLayer;

	public ProxyTransactionLayer(int listenPort, ProxyUserLayer userLayer) throws SocketException {
		this.userLayer = userLayer;
		this.transportLayer = new ProxyTransportLayer(listenPort, this);
	}

	public void onMessageReceived(SIPMessage sipMessage) throws IOException {
		if (sipMessage instanceof InviteMessage) {
			InviteMessage inviteMessage = (InviteMessage) sipMessage;
			switch (stateA) {
			case IDLE:
				if(userLayer.onInviteReceived(inviteMessage))
					{stateA = PROCC;}
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		}
		
		else if (sipMessage instanceof RegisterMessage) {
			RegisterMessage registerMessage = (RegisterMessage) sipMessage;
			userLayer.onRegisterReceived(registerMessage);

		}
	}
	
	
	
	public void sendResponse(SIPMessage sipMessage, String address, int port) throws IOException {
		transportLayer.send(sipMessage, address, port);
	}

	
	
	public void sendInvite(InviteMessage inviteMessage, String address, int port) throws IOException {
		transportLayer.send(inviteMessage, address, port);
		stateB = CALL;
	}
	

	public void startListening() {
		transportLayer.startListening();
	}
}
