package proxy;

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import mensajesSIP.ACKMessage;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.InviteMessage;
import mensajesSIP.OKMessage;
import mensajesSIP.RegisterMessage;
import mensajesSIP.RequestTimeoutMessage;
import mensajesSIP.RingingMessage;
import mensajesSIP.SIPMessage;
import mensajesSIP.ServiceUnavailableMessage;

public class ProxyTransactionLayer {
	private static final int IDLE = 0; 	
	private static final int CALL = 1;
	private static final int PROCC = 2;
	private static final int COMPL = 3;
	private static final int TERM = 4;
	private int stateA = IDLE;
	private int stateB = IDLE;

	public int getStateA() {
		return stateA;
	}

	public void setStateA(int stateA) {
		this.stateA = stateA;
	}

	public int getStateB() {
		return stateB;
	}

	public void setStateB(int stateB) {
		this.stateB = stateB;
	}
	
	Timer timerB = new Timer();
	Timer timer486 = new Timer();

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
				ServiceUnavailableMessage serviceUnav = new ServiceUnavailableMessage();
				
				serviceUnav.setCallId(inviteMessage.getCallId());
				serviceUnav.setContentLength(0);
				serviceUnav.setcSeqNumber(inviteMessage.getcSeqNumber());
				serviceUnav.setcSeqStr(inviteMessage.getcSeqStr());
				serviceUnav.setFromName(inviteMessage.getFromName());
				serviceUnav.setFromUri(inviteMessage.getFromUri());
				serviceUnav.setToName(inviteMessage.getToName());
				serviceUnav.setToUri(inviteMessage.getToUri());
				serviceUnav.setVias(inviteMessage.getVias());
				
				String[] args = inviteMessage.getContact().split(":");
				
				String CharAddress = args[0];
				int CharPort = Integer.parseInt(args[1]);
				
				this.sendResponse((SIPMessage) serviceUnav, CharAddress, CharPort);
				
				System.err.println("No more Invites allowed");
				break;
			}
		}
		
		else if (sipMessage instanceof RegisterMessage) {
			RegisterMessage registerMessage = (RegisterMessage) sipMessage;
			userLayer.onRegisterReceived(registerMessage);

		} else if (sipMessage instanceof RingingMessage){
			RingingMessage ringingMessage = (RingingMessage) sipMessage;
			switch (stateB) {
			case CALL:
				userLayer.onRingingMessage(ringingMessage);
				stateB = PROCC;
				break;
			case PROCC:
				stateB = PROCC;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof OKMessage){
			OKMessage okMessage = (OKMessage) sipMessage;
			switch (stateB) {
			case CALL:
				userLayer.onOkMessage(okMessage);
				stateB = IDLE;
				break;
			case PROCC:
				userLayer.onOkMessage(okMessage);
				stateB = IDLE;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
			switch (stateA) {
			case PROCC:
				stateA = IDLE;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof BusyHereMessage) {
			BusyHereMessage busyHereMessage = (BusyHereMessage) sipMessage;
			switch (stateB) {
			case CALL:
				userLayer.onBusy(busyHereMessage);
				userLayer.commandACK(busyHereMessage);
				stateB = COMPL;
				TimerTask taskB = new TimerTask() {
		            public void run() {
		                stateB= IDLE;;
		            }
		        };
		        timerB.schedule(taskB, 1000); //
				break;
			case PROCC:
				userLayer.onBusy(busyHereMessage);
				userLayer.commandACK(busyHereMessage);
				stateB = COMPL;
				TimerTask taskB_2 = new TimerTask() {
		            public void run() {
		                stateB= IDLE;;
		            }
		        };
		        timerB.schedule(taskB_2, 1000); //
				break;
			case COMPL:
				userLayer.commandACK(busyHereMessage);
				stateB = COMPL; 
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
			switch (stateA) {
			case PROCC:
				stateA = COMPL;
				TimerTask task486 = new TimerTask() {
		            public void run() {
		                // Place the action you want to execute after 2 seconds here.
		            	try {
		            		userLayer.onBusy(busyHereMessage);
		    			} catch (IOException e) {
		    				e.printStackTrace();
		    			};
		            }
		        };
		        timer486.schedule(task486, 200); // 200 miliseconds
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		}
		else if (sipMessage instanceof ACKMessage) {
			ACKMessage ackMessage = (ACKMessage) sipMessage;
			switch (stateA) {
			case IDLE:
				System.out.println("ACK Received from " + ackMessage.getFromName());
				break;
			case PROCC:
				System.out.println("ACK Received from " + ackMessage.getFromName());
				break;
			case COMPL:
				timer486.cancel();
				System.out.println("ACK Received from " + ackMessage.getFromName());
				stateA = IDLE;
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		}
		else if(sipMessage instanceof RequestTimeoutMessage) {
			RequestTimeoutMessage requestMessage = (RequestTimeoutMessage) sipMessage;
			switch(stateB) {
			case CALL:
				userLayer.onRequest(requestMessage);
				userLayer.commandACK_408(requestMessage);
				stateB = COMPL;
				TimerTask task408 = new TimerTask() {
		            public void run() {
		                stateB= IDLE;;
		            }
		        };
		        timerB.schedule(task408, 1000); //
				break;
			case PROCC:
				userLayer.onRequest(requestMessage);
				userLayer.commandACK_408(requestMessage);
				stateB = COMPL;
				TimerTask task408_2 = new TimerTask() {
		            public void run() {
		                stateB= IDLE;;
		            }
		        };
		        timerB.schedule(task408_2, 1000); //
				break;
			case COMPL:
				userLayer.commandACK_408(requestMessage);
				stateB = COMPL; 
				break;
				
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}	
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
