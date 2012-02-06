package org.lyl.simplehttpserver.core;

import java.util.List;

public class ServerContainer {
	public List<Service> servers;
	
	public ServerContainer(){
		
	}
	
	public void setServers(List<Service> servers) {
		this.servers = servers;
	}

	public List<Service> getServers(){
		return servers;
	}
}
