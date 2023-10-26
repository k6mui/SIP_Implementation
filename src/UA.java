import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import ua.UaUserLayer;

/*java UA usuarioSIP puertoescuchaUA IPProxy puertoescuchaProxy debug(true/false) tiempo registro*/


public class UA {
	public static void main(String[] args) throws Exception {
		System.out.println("UA launching with args: " + String.join(", ", args));
		
		int listenPort = Integer.parseInt(args[1]);
		String proxyAddress = args[2];
		int proxyPort = Integer.parseInt(args[3]);
		
		boolean debugIndicator = Boolean.parseBoolean(args[4]); 
		String t_expires = args[5];

		UaUserLayer userLayer = new UaUserLayer(args[0], listenPort, proxyAddress, proxyPort, debugIndicator, t_expires);
		
		
		Timer timer = new Timer();
		TimerTask task = new TimerTask() {
            public void run() {
                // Place the action you want to execute after 2 seconds here.
            	try {
					userLayer.commandRegister(null);
				} catch (IOException e) {
					e.printStackTrace();
				};
            }
        };
		
		new Thread() {
			@Override
			public void run() {
				userLayer.startListeningNetwork();
				
			}
		}.start();
		
		/* Call commandRgister and set the timer */
        timer.schedule(task, 2000); // 2 seconds
        
        boolean temp = true;
        while (temp) {
        	if (userLayer.isResponseRegister() == true) {
        		temp = false;
        		timer.cancel();
        	}
        	Thread.sleep(1000);
        }
        
		userLayer.startListeningKeyboard();
	}
}
