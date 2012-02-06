package org.lyl.simplehttpserver.dhcp;

public interface ResourcePool {
	NetworkConfiguration offerResource(String mac);
	NetworkConfiguration leaseResource(String mac);	
	void releaseResource(String mac);
	boolean reserveIP(String ip);
	boolean isOfferTo(String mac);
}
