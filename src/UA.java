import ua.UaUserLayer;

/*java UA usuarioSIP puertoescuchaUA IPProxy puertoescuchaProxy debug(true/false) tiempo registro*/


public class UA {
	public static void main(String[] args) throws Exception {
		System.out.println("UA launching with args: " + String.join(", ", args));
		
		int listenPort = Integer.parseInt(args[1]);
		String proxyAddress = args[2];
		int proxyPort = Integer.parseInt(args[3]);
		
		boolean debugIndicator = Boolean.parseBoolean(args[4]); 

		UaUserLayer userLayer = new UaUserLayer(listenPort, proxyAddress, proxyPort);
		
		
		new Thread() {
			@Override
			public void run() {
				userLayer.startListeningNetwork();
			}
		}.start();
		
		/*Se llama al commandRgister y se fija el temporizador*/
		
		userLayer.startListeningKeyboard();
	}
}
