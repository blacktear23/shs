package org.lyl.simplehttpserver.services;

import org.lyl.simplehttpserver.core.Service;
import org.lyl.simplehttpserver.core.ServiceBusContext;

public abstract class AbstractServer implements Service {
	private ServiceBusContext serverContext = ServiceBusContext.getServiceBusContext();
	private String connectionServiceName;
	private String datagramPacketServiceName;
	
	public String getDatagramPacketServiceName() {
		return datagramPacketServiceName;
	}

	public void setDatagramPacketServiceName(String datagramPacketServiceName) {
		this.datagramPacketServiceName = datagramPacketServiceName;
	}

	public ServiceBusContext getServerContext() {
		return serverContext;
	}

	public String getConnectionServiceName() {
		return connectionServiceName;
	}

	public void setConnectionServiceName(String connectionServiceName) {
		this.connectionServiceName = connectionServiceName;
	}
	
	
}
