package org.lyl.simplehttpserver;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.AutoRestartDaemon;
import org.lyl.simplehttpserver.core.MimeType;
import org.lyl.simplehttpserver.core.ServiceBusContext;
import org.lyl.simplehttpserver.core.ServiceBus;
import org.lyl.simplehttpserver.core.Shutdowner;

public class Launcher {
	private static Logger log = LogManager.getLogger(Launcher.class);
	public static void main(String[] args) {
		runBaseService();
	}
	
	public static void runBaseService() {
		log.info("Welcome to use Simple Http Server " + Config.VERSION);
		log.info("Load basic configuration...");
		Config conf = Config.getConfig();
		MimeType.getMimeType();
		ServiceBusContext context = ServiceBusContext.getServiceBusContext();
		Shutdowner std = new Shutdowner(context);
		ServiceBus sb = new ServiceBus(std);
		sb.startServiceBus();
		
		//process for shutdown
		Runtime.getRuntime().addShutdownHook(new Thread(std));
		//for auto reatart
		if(conf.getAutoRestart()){
			AutoRestartDaemon ard = new AutoRestartDaemon(conf.getBeanConfigFileName(), sb);
			Thread ardt = new Thread(ard);
			ardt.setDaemon(true);
			ardt.start();
		}
	}
}
