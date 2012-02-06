package org.lyl.simplehttpserver.core;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AutoRestartDaemon implements Runnable {
	private static Logger log =  LogManager.getLogger(AutoRestartDaemon.class);
	private long lasttime = 0;
	private File watchFile = null;
	private ServiceBus serviceBus;
	
	public AutoRestartDaemon(String filename, ServiceBus serviceBus){
		watchFile = new File(filename);
		this.serviceBus = serviceBus;
	}
	
	public void run() {
		while(true){
			if(isFileChanged()){
				//restart service bus
				log.info("Config file modified, server restart.");
				if(this.serviceBus != null){
					serviceBus.shutdownServiceBus();
					serviceBus.startServiceBus();
				}
			}
			try {
				TimeUnit.SECONDS.sleep(1);
			} catch (InterruptedException e) {
				//do nothing
			}
		}
	}
	
	private boolean isFileChanged(){
		//when first scan file
		if(lasttime == 0){
			lasttime = watchFile.lastModified();
			return false;
		}
		//when file changed!
		if(lasttime != watchFile.lastModified()){
			lasttime = watchFile.lastModified();
			return true;
		}
		return false;
	}
}
