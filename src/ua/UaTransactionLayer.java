package ua;

import java.io.IOException;
import java.net.SocketException;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import mensajesSIP.ACKMessage;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.ByeMessage;
import mensajesSIP.InviteMessage;
import mensajesSIP.NotFoundMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.RequestTimeoutMessage;
import mensajesSIP.RingingMessage;
import mensajesSIP.SIPMessage;
import mensajesSIP.ServiceUnavailableMessage;
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
	
	Timer timer486 = new Timer();
	Timer timerB = new Timer();

	
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
				timer486 = new Timer();
				timerB = new Timer();
				userLayer.onInviteReceived(inviteMessage);
				WhoIam = 2;
				state = PROCC;
				System.out.println("Pasando a ---> PROCCEDING");
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof OKMessage){ 
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
				System.out.println("Pasando a ---> TERMINATED");
				break;
			case PROCC:
				userLayer.onOkReceived(okMessage);
				System.out.println("LLAMADA INICIADA");
				userLayer.commandACK_OK(okMessage);
				state = TERM;
				System.out.println("Pasando a ---> TERMINATED");
				break;
			case TERM:
				System.out.println("FIN DE LLAMADA");
				break;

			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof NotFoundMessage){ 
			NotFoundMessage notFoundMessage = (NotFoundMessage) sipMessage;
			switch (state) {
			case IDLE:
				userLayer.onNFReceived(notFoundMessage);
				userLayer.setResponseRegister(true); // False??
				break;
			case CALL:
				userLayer.onNFReceived(notFoundMessage);
				userLayer.commandACK();
				state = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask task404 = new TimerTask() {
		            public void run() {
		                state= IDLE;
		        		System.out.println("Pasando a ---> IDLE");
		            }
		        };
		        timerB.schedule(task404, 1000);
				break;
			case PROCC:
				userLayer.onNFReceived(notFoundMessage);
				userLayer.commandACK();
				state = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask task404_2 = new TimerTask() {
		            public void run() {
		                state= IDLE;
		        		System.out.println("Pasando a ---> IDLE");
		            }
		        };
		        timerB.schedule(task404_2, 1000);
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof TryingMessage){
			TryingMessage tryingMessage = (TryingMessage) sipMessage;
			switch (state) {
			case CALL:
				userLayer.onTrying(tryingMessage);
				state = PROCC;
				System.out.println("Pasando a ---> PROCCEDING");
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof RingingMessage) { 
			RingingMessage ringingMessage = (RingingMessage) sipMessage;
			switch (state) {
			case CALL:
				userLayer.onRinging(ringingMessage);
				state = PROCC;
				System.out.println("Pasando a ---> PROCCEDING");
				break;
			case PROCC:
				userLayer.onRinging(ringingMessage);
				state = PROCC;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
		}
		} else if (sipMessage instanceof BusyHereMessage){ 
			BusyHereMessage busyHereMessage = (BusyHereMessage) sipMessage;
			switch (state) {
			case PROCC:
				userLayer.onBusy(busyHereMessage);
				userLayer.commandACK();
				state = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask taskB = new TimerTask() {
		            public void run() {
		                state= IDLE;
		        		System.out.println("Pasando a ---> IDLE");
		            }
		        };
		        timerB.schedule(taskB, 1000); //
				break;
			case CALL:
				userLayer.onBusy(busyHereMessage);
				userLayer.commandACK();
				state = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask taskB_2 = new TimerTask() {
		            public void run() {
		                state= IDLE;
		        		System.out.println("Pasando a ---> IDLE");
		            }
		        };
		        timerB.schedule(taskB_2, 1000); //
				break;
				
			case COMPL:
				userLayer.commandACK();
				state =COMPL;
				System.out.println("Pasando a ---> COMPLETED");
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
			}
		else if (sipMessage instanceof RequestTimeoutMessage){ 
			RequestTimeoutMessage requestM = (RequestTimeoutMessage) sipMessage;
			switch (state) {
			case PROCC:
				userLayer.onReq(requestM);
				userLayer.commandACK();
				state = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask task408 = new TimerTask() {
		            public void run() {
		                state= IDLE;
		        		System.out.println("Pasando a ---> IDLE");
		            }
		        };
		        timerB.schedule(task408, 1000); //
				break;
			case CALL:
				userLayer.onReq(requestM);
				userLayer.commandACK();
				state = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask task408_2 = new TimerTask() {
		            public void run() {
		                state= IDLE;
		        		System.out.println("Pasando a ---> IDLE");
		            }
		        };
		        timerB.schedule(task408_2, 1000); //
				break;
				
			case COMPL:
				userLayer.commandACK();
				state =COMPL;
				System.out.println("Pasando a ---> COMPLETED");
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
					timer486.cancel();
					System.out.println("ACK received from Proxy");
					state = IDLE;
					System.out.println("Pasando a ---> IDLE");
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
					state = IDLE;
					System.out.println("Pasando a ---> IDLE");
					break;
				default:
					System.err.println("Unexpected message, throwing away");
					break;
				}
		 }
		 else if (sipMessage instanceof ServiceUnavailableMessage){ 
				switch (state) {
				case CALL:
					System.err.println("503 Service Unavailable");
					state = IDLE;
					System.out.println("Pasando a ---> IDLE");
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
		System.out.println("Pasando a ---> CALLING");
		WhoIam = 1;
		transportLayer.sendToProxy(inviteMessage);
	}

	public void register(RegisterMessage registerMessage) throws IOException {
		transportLayer.sendToProxy(registerMessage);
	}
	
	public void send180(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
	}
	
	public void send200(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state=TERM;
		System.out.println("Pasando a ---> TERMINATED");

	}
	
	public void send200_direct(SIPMessage sipMessage, String addr, int port) throws IOException {
		transportLayer.send(sipMessage, addr , port);
	}
	
	public void send408(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state=COMPL;
		System.out.println("Pasando a ---> COMPLETED");

	}
	
	public void send486(SIPMessage sipMessage) throws IOException {
		state=COMPL;
		System.out.println("Pasando a ---> COMPLETED");
		transportLayer.sendToProxy(sipMessage);
        TimerTask task486 = new TimerTask() {
            public void run() {
                // Place the action you want to execute after 2 seconds here.
            	try {
            		transportLayer.sendToProxy(sipMessage);
    			} catch (IOException e) {
    				e.printStackTrace();
    			};
            }
        };
        timer486.scheduleAtFixedRate(task486, 200, 200);
    	
	}
	
	
	public void sendACK_OK(SIPMessage sipMessage, String addr, int port) throws IOException {
		transportLayer.send(sipMessage, addr , port);
	}
	
	public void sendACK(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
	}
	
	public void sendBYE(SIPMessage sipMessage, String addr, int port) throws IOException {
		transportLayer.send(sipMessage, addr , port);
		state = IDLE;
		System.out.println("Pasando a ---> IDLE");
	}
	
	public void sendBYE_loose(SIPMessage sipMessage) throws IOException {
		transportLayer.sendToProxy(sipMessage);
		state = IDLE;
		System.out.println("Pasando a ---> IDLE");
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
