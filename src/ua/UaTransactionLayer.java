package ua;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;

import mensajesSIP.ACKMessage;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.ByeMessage;
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
	
	private int WhoIam = 0; // 1 para llamante; 2 para llamado

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
				WhoIam = 2;
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
				userLayer.commandACK_OK(okMessage);
				state = TERM;
				break;
			case PROCC:
				userLayer.onOkReceived(okMessage);
				System.out.println("LLAMADA INICIADA");
				userLayer.commandACK_OK(okMessage);
				state = TERM;
				break;
			case TERM:
				System.out.println("FIN DE LLAMADA");
				break;
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
				userLayer.commandACK();
				state = COMPL;
				break;
			case PROCC:
				userLayer.onNFReceived();
				userLayer.commandACK();
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
				break;
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
				break;
			case PROCC:
				userLayer.onRinging(ringingMessage);
				state = PROCC;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
		}
		} else if (sipMessage instanceof BusyHereMessage){ /* to do -------------------*/
			BusyHereMessage busyHereMessage = (BusyHereMessage) sipMessage;
			switch (state) {
			case PROCC:
				userLayer.onBusy(busyHereMessage);
				userLayer.commandACK();
				state = COMPL;
				break;
			case CALL:
				userLayer.onBusy(busyHereMessage);
				userLayer.commandACK();
				state = COMPL;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
			}
		 else if (sipMessage instanceof ACKMessage){ 
			 ACKMessage ackMessage = (ACKMessage) sipMessage;
				switch (state) {
				case COMPL:
					System.out.println("ACK received from Proxy");
					state = TERM;
					break;
				case TERM:
					System.out.println("ACK received from " + ackMessage.getFromName());
					System.out.println("LLAMADA INICIADA");
					break;
				default:
					System.err.println("Unexpected message, throwing away");
					break;
				}
		 }
		 else if (sipMessage instanceof ByeMessage){ 
			 ByeMessage byeMessage = (ByeMessage) sipMessage;
				switch (state) {
				case TERM:
					System.out.println("FIN DE LLAMADA");
					userLayer.onByeReceived(byeMessage);
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

	public void startListeningNetwork() {
		transportLayer.startListening();
	}

	public void call(InviteMessage inviteMessage) throws IOException {
		state=CALL;
		WhoIam = 1;
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
	
	public void send200_direct(SIPMessage sipMessage, String addr, int port) throws IOException {
		transportLayer.send(sipMessage, addr , port);
	}
	
	public void send486(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state=COMPL;
	}
	
	public void send404(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state = COMPL;
	}
	
	public void sendACK_OK(SIPMessage sipMessage, String addr, int port) throws IOException {
		transportLayer.send(sipMessage, addr , port);
	}
	
	public void sendACK(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
	}
	
	public void sendBYE(SIPMessage sipMessage, String addr, int port) throws IOException {
		transportLayer.send(sipMessage, addr , port);
	}
	
	public int getState() {
		return state;
	}


	public int getWhoIam() {
		return WhoIam;
	}


	public void setWhoIam(int whoIam) {
		WhoIam = whoIam;
	}

}
