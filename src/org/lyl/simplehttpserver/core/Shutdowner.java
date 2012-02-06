package org.lyl.simplehttpserver.core;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Shutdowner implements Runnable {
	private static Logger log = LogManager.getLogger(Shutdowner.class);
	private ServiceBusContext scontext;
	ArrayList<Service> sts = new ArrayList<Service>();
	
	public Shutdowner(ServiceBusContext scontext) {
		this.scontext = scontext;
	}
	
	public void addShutdowner(Service e) {
		sts.add(e);
	}
	
	public void cancelAllRegistered(){
		this.sts.clear();
	}
	
	public void shutdownAll(){
		Iterator<Service> it = sts.iterator();
		while(it.hasNext()) {
			Service sta = it.next();
			log.info("Shuting down service " + sta.getClass().getName()+ "...");
			sta.shutdown();
			it.remove();
		}
		if(scontext != null) scontext.shutdownServices();
		log.info("Services shutdown");
	}
	
	public void run() {
		shutdownAll();
	}
}