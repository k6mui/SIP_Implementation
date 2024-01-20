package proxy;

import java.io.IOException;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

import mensajesSIP.ACKMessage;
import mensajesSIP.BusyHereMessage;
import mensajesSIP.ByeMessage;
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
	private boolean loose;

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

	public ProxyTransactionLayer(int listenPort, ProxyUserLayer userLayer, boolean loose) throws SocketException {
		this.userLayer = userLayer;
		this.transportLayer = new ProxyTransportLayer(listenPort, this);
		this.loose = loose;
	}

	public void onMessageReceived(SIPMessage sipMessage) throws IOException {
		if (sipMessage instanceof InviteMessage) {
			InviteMessage inviteMessage = (InviteMessage) sipMessage;
			switch (stateA) {
			case IDLE:
				timerB = new Timer();
				timer486 = new Timer();
				if (userLayer.onInviteReceived(inviteMessage)) {
					stateA = PROCC;
					System.out.println("Pasando a ---> PROCCEDING");
				}
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

		} else if (sipMessage instanceof RingingMessage) {
			RingingMessage ringingMessage = (RingingMessage) sipMessage;
			switch (stateB) {
			case CALL:
				userLayer.onRingingMessage(ringingMessage);
				stateB = PROCC;
				System.out.println("Pasando a estado ---> PROCCEDING");
				break;
			case PROCC:
				stateB = PROCC;
				System.out.println("Pasando a estado ---> PROCCEDING");
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof OKMessage) {
			OKMessage okMessage = (OKMessage) sipMessage;
			switch (stateB) {
			case CALL:
				userLayer.onOkMessage(okMessage);
				if (loose) {
					stateB= TERM;
					System.out.println("Pasando a estado ---> TERMINATED");
				}else {
					stateB = IDLE;
					System.out.println("Pasando a estado ---> IDLE");
				}
				break;
			case PROCC:
				userLayer.onOkMessage(okMessage);
				if (loose) {
					stateB= TERM;
					System.out.println("Pasando a estado ---> TERMINATED");
				}else {
					stateB = IDLE;
					System.out.println("Pasando a estado ---> IDLE");
				}
				break;
				
			case TERM:
				userLayer.onOkMessage(okMessage);
				stateB = IDLE;
				System.out.println("Reenviando 200 OK (Loose Routing)");
				System.out.println("Pasando a estado ---> IDLE");
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
			switch (stateA) {
			case PROCC:
				if (loose) {
					stateA= TERM;
					System.out.println("Pasando a estado ---> TERMINATED");
				}else {
					stateA = IDLE;
					System.out.println("Pasando a estado ---> IDLE");
				}
				break;
			case TERM:
				stateA = IDLE;
				System.out.println("Pasando a estado ---> IDLE");
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
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask taskB = new TimerTask() {
					public void run() {
						stateB = IDLE;
						System.out.println("Pasando a ---> IDLE");
					}
				};
				timerB.schedule(taskB, 1000); //
				break;
			case PROCC:
				userLayer.onBusy(busyHereMessage);
				userLayer.commandACK(busyHereMessage);
				stateB = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask taskB_2 = new TimerTask() {
					public void run() {
						stateB = IDLE;
						System.out.println("Pasando a ---> IDLE");
					}
				};
				timerB.schedule(taskB_2, 1000); //
				break;
			case COMPL:
				userLayer.commandACK(busyHereMessage);
				stateB = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
			switch (stateA) {
			case PROCC:
				stateA = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask task486 = new TimerTask() {
					public void run() {
						// Place the action you want to execute after 2 seconds here.
						try {
							userLayer.onBusy(busyHereMessage);
						} catch (IOException e) {
							e.printStackTrace();
						}
						;
					}
				};
				timer486.schedule(task486, 200); // 200 miliseconds
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof ACKMessage) {
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
				System.out.println("Pasando a ---> IDLE");
				break;
			case TERM:
				System.out.println("ACK Received from " + ackMessage.getFromName());
				userLayer.onAckReceived(ackMessage);
				System.out.println("Reenviando ACK (Loose Routing)");
				break;
			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof RequestTimeoutMessage) {
			RequestTimeoutMessage requestMessage = (RequestTimeoutMessage) sipMessage;
			switch (stateB) {
			case CALL:
				userLayer.onRequest(requestMessage);
				userLayer.commandACK_408(requestMessage);
				stateB = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask task408 = new TimerTask() {
					public void run() {
						stateB = IDLE;
						stateA = IDLE;
						System.out.println("Pasando a ---> IDLE");
					}
				};
				timerB.schedule(task408, 1000); //
				break;
			case PROCC:
				userLayer.onRequest(requestMessage);
				userLayer.commandACK_408(requestMessage);
				stateB = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				TimerTask task408_2 = new TimerTask() {
					public void run() {
						stateB = IDLE;
						stateA = IDLE; 
						System.out.println("Pasando a ---> IDLE");
					}
				};
				timerB.schedule(task408_2, 1000); //
				break;
			case COMPL:
				userLayer.commandACK_408(requestMessage);
				stateB = COMPL;
				System.out.println("Pasando a ---> COMPLETED");
				break;

			default:
				System.err.println("Unexpected message, throwing away");
				break;
			}
		} else if (sipMessage instanceof ByeMessage) {
			ByeMessage byeMessage = (ByeMessage) sipMessage;
			switch (stateB) {
			case TERM:
				userLayer.onByeReceived(byeMessage);
				System.out.println("Reenviando BYE (Loose Routing)");
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
		System.out.println("Pasando a ---> CALLING");
	}

	public void startListening() {
		transportLayer.startListening();
	}
}
