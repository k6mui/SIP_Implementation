import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
		
		
		ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

        Runnable task = () -> {
            try {
                userLayer.commandRegister(null);
                if (userLayer.isResponseRegister()) {
                    executor.shutdown();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        // Ejecuta la tarea cada 2 segundos
        executor.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
        
		new Thread() {
			@Override
			public void run() {
				userLayer.startListeningNetwork();
				
			}
		}.start();
		
        
        boolean temp = true;
        while (temp) {
        	if (userLayer.isResponseRegister() == true) {
        		temp = false;
        		executor.shutdown();
        	}
        	Thread.sleep(1000);
        }
        
		userLayer.startListeningKeyboard();
	}
}
