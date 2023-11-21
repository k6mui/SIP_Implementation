package ua;

import java.io.IOException;
import java.net.SocketException;

import mensajesSIP.BusyHereMessage;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.RingingMessage;
import mensajesSIP.SIPMessage;
import mensajesSIP.TryingMessage;

public class UaTransactionLayer {
	private static final int IDLE = 0;
	private static final int CALL = 1;
	private static final int PROCC = 2;
	private static final int COMPL = 3;
	private static final int TERM = 4;
	private int state = IDLE;

	private UaUserLayer userLayer;
	private UaTransportLayer transportLayer;

	public UaTransactionLayer(int listenPort, String proxyAddress, int proxyPort, UaUserLayer userLayer)
			throws SocketException {
		this.userLayer = userLayer;
		this.transportLayer = new UaTransportLayer(listenPort, proxyAddress, proxyPort, this);
	}
	
	
	public void onMessageReceived(SIPMessage sipMessage) throws IOException {
		if (sipMessage instanceof InviteMessage) {
			InviteMessage inviteMessage = (InviteMessage) sipMessage;
			switch (state) {
			case IDLE:
				userLayer.onInviteReceived(inviteMessage);
				state = PROCC;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof OKMessage){ /*%%%%%%%%%%%%%%%%%To do%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
			OKMessage okMessage = (OKMessage) sipMessage;
			switch (state) {
			case IDLE:
				userLayer.onOkReceived(okMessage);
				userLayer.setResponseRegister(true);
				break;	
			case CALL:
				userLayer.onOkReceived(okMessage);
				state = TERM;
			case PROCC:
				userLayer.onOkReceived(okMessage);
				System.out.println("Llamada iniciada");
				state = TERM;
// *Creo que habría que poner un estado terminated ya que no es lo mismo estar en llamada que haber colgado después del bye (ahi si que es IDLE)*
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof NotFoundMessage){ 
			NotFoundMessage notFoundMessage = (NotFoundMessage) sipMessage;
			switch (state) {
			case IDLE:
				userLayer.onNFReceived();
				userLayer.setResponseRegister(true);
				break;
			case CALL:
				userLayer.onNFReceived();
				state = COMPL;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof TryingMessage){
			TryingMessage tryingMessage = (TryingMessage) sipMessage;
			switch (state) {
			case CALL:
				userLayer.onTrying();
				state = PROCC;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof RingingMessage) { /*%%%%%%%%%%%%%%%%%To do%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%*/
			RingingMessage ringingMessage = (RingingMessage) sipMessage;
			switch (state) {
			case CALL:
				userLayer.onRinging(ringingMessage);
				state = PROCC;
			case PROCC:
				userLayer.onRinging(ringingMessage);
				state = PROCC;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
		}
		} else if (sipMessage instanceof BusyHereMessage){ /* to do -------------------*/
			BusyHereMessage busyHereMessage = (BusyHereMessage) sipMessage;
			switch (state) {
			case PROCC:
				userLayer.onBusy(busyHereMessage);
				// Enviar ACK
				state = COMPL;
			case CALL:
				userLayer.onBusy(busyHereMessage);
				// Enviar ACK
				state = COMPL;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
			}
		else {
			System.err.println("Unexpected message, throwing away");
		}
	}

	public void startListeningNetwork() {
		transportLayer.startListening();
	}

	public void call(InviteMessage inviteMessage) throws IOException {
		state=CALL;
		transportLayer.sendToProxy(inviteMessage);
	}
	/*DONE*/
	public void register(RegisterMessage registerMessage) throws IOException {
		transportLayer.sendToProxy(registerMessage);
	}
	
	public void send180(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
	}
	
	public void send200(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state=TERM;
	}
	
	public void send486(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state=COMPL;
	}
	
	public void send404(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state = COMPL;
	}
	
	public void sendACK(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
	}
	
	public int getState() {
		return state;
	}

}
