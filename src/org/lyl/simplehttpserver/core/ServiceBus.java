package org.lyl.simplehttpserver.core;

import org.springframework.beans.factory.BeanFactory;

public class ServiceBus {
	private ServiceBusContext serviceBusContext;
	private Shutdowner sd;
	
	public ServiceBus(Shutdowner sd) {
		this.sd = sd;
	}
	
	public void startServiceBus() {
		serviceBusContext = ServiceBusContext.getServiceBusContext();
		BeanFactory factory = serviceBusContext.getBeanFactory();
		ServerContainer sc = (ServerContainer)factory.getBean("ServerContainer");
		for(Service server : sc.getServers()) {
			sd.addShutdowner(server);
			serviceBusContext.runService(server);
		}
	}
	
	public void shutdownServiceBus(){
		sd.shutdownAll();
		ServiceBusContext.clearContext();
	}
}
