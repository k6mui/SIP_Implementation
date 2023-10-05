import proxy.ProxyUserLayer;

public class Proxy {
	public static void main(String[] args) throws Exception {
		System.out.println("Proxy launching with args: " + String.join(", ", args));
		int listenPort = Integer.parseInt(args[0]);
		ProxyUserLayer userLayer = new ProxyUserLayer(listenPort);
		userLayer.startListening();
	}
}
