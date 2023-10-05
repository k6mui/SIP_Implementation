package ua;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import mensajesSIP.SIPMessage;

public class UaTransportLayer {
	private static final int BUFSIZE = 4 * 1024;

	private int listenPort;
	private String proxyAddress;
	private int proxyPort;
	private DatagramSocket socket;
	private UaTransactionLayer transactionLayer;

	public UaTransportLayer(int listenPort, String proxyAddress, int proxyPort, UaTransactionLayer transactionLayer)
			throws SocketException {
		this.transactionLayer = transactionLayer;
		this.listenPort = listenPort;
		this.proxyAddress = proxyAddress;
		this.proxyPort = proxyPort;
		this.socket = new DatagramSocket(listenPort);
	}

	public void sendToProxy(SIPMessage sipMessage) throws IOException {
		send(sipMessage.toStringMessage().getBytes(), this.proxyAddress, this.proxyPort);
	}

	public void send(SIPMessage sipMessage, String address, int port) throws IOException {
		send(sipMessage.toStringMessage().getBytes(), address, port);
	}

	private void send(byte[] bytes, String address, int port) throws IOException {
		InetAddress inetAddress = InetAddress.getByName(address);
		DatagramPacket packet = new DatagramPacket(bytes, bytes.length, inetAddress, port);
		socket.send(packet);
	}

	public void startListening() {
		System.out.println("Listening at " + listenPort + "...");
		while (true) {
			try {
				byte[] buf = new byte[BUFSIZE];
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				String msg = new String(packet.getData());
				SIPMessage sipMessage = SIPMessage.parseMessage(msg);
				transactionLayer.onMessageReceived(sipMessage);
			} catch (Exception e) {
				System.err.println(e.getMessage());
				e.printStackTrace();
			}
		}
	}

}
