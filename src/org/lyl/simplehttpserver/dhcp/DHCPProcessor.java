package org.lyl.simplehttpserver.dhcp;

import static org.dhcp4java.DHCPConstants.DHO_DHCP_LEASE_TIME;
import static org.dhcp4java.DHCPConstants.DHO_DHCP_SERVER_IDENTIFIER;
import static org.dhcp4java.DHCPConstants.DHO_DOMAIN_NAME_SERVERS;
import static org.dhcp4java.DHCPConstants.DHO_ROUTERS;
import static org.dhcp4java.DHCPConstants.DHO_SUBNET_MASK;

import java.net.InetAddress;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dhcp4java.DHCPPacket;
import org.dhcp4java.DHCPResponseFactory;

public class DHCPProcessor {
	private static Logger log = LogManager.getLogger(DHCPProcessor.class);
	private final ResourcePool resourcesPool;
	private InetAddress host = null;
	
	public DHCPProcessor(ResourcePool pool) {
		this.resourcesPool = pool;
		try {
			this.host = InetAddress.getLocalHost();
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public DHCPPacket doDiscover(DHCPPacket request) {
		log.info("Receive Discover from " + request.getChaddrAsHex()
				+ " Request IP " + request.getCiaddr().toString());
		NetworkConfiguration config = resourcesPool.offerResource(request.getChaddrAsHex());
		if(config == null) {
			return null;
		}
		return makeOffer(request, config);
    }
    
	public DHCPPacket doRequest(DHCPPacket request) {
		log.info("Receive Request from " + request.getChaddrAsHex()
				+ " Request IP " + request.getCiaddr().toString()
				+ " Serve host " + request.getSiaddr());
		boolean reserved = true;
		if(!request.getSiaddr().equals(host) &&
				this.resourcesPool.isOfferTo(request.getChaddrAsHex())) {
			//client choose other DHCP server's response,
			//reserve this IP and release IP for this mac.
			reserved = this.resourcesPool.reserveIP(request.getCiaddr().toString());
			this.resourcesPool.releaseResource(request.getChaddrAsHex());
		}
		NetworkConfiguration config = null;
		if(reserved)
		    config = resourcesPool.leaseResource(request.getChaddrAsHex());
		else
		    config = resourcesPool.offerResource(request.getChaddrAsHex());
		if(config == null) {
			return makeNak(request);
		}
		return makeAck(request, config);
    }

	private DHCPPacket makeNak(DHCPPacket request) {
		DHCPPacket ret = DHCPResponseFactory.makeDHCPNak(request, this.host, "");
		return ret;
	}
	
	private DHCPPacket makeAck(DHCPPacket request, NetworkConfiguration config) {
		DHCPOptionList options = new DHCPOptionList();
		options.add(DHO_SUBNET_MASK, config.getSubnetMask());
		options.add(DHO_ROUTERS, config.getDefaultGateway());
		options.add(DHO_DHCP_LEASE_TIME, config.getLeaseTime());
		options.add(DHO_DOMAIN_NAME_SERVERS, config.getDnsServers());
		options.add(DHO_DHCP_SERVER_IDENTIFIER, host);
		InetAddress offer = config.getIpAddress();
		DHCPPacket ret = DHCPResponseFactory.makeDHCPAck(
				request, offer, config.getLeaseTime(), host, "", options.toArray());
		ret.setSiaddr(host);
		return ret;
	}
	
	private DHCPPacket makeOffer(DHCPPacket request, NetworkConfiguration config) {
		DHCPOptionList options = new DHCPOptionList();
		options.add(DHO_SUBNET_MASK, config.getSubnetMask());
		options.add(DHO_ROUTERS, config.getDefaultGateway());
		options.add(DHO_DHCP_LEASE_TIME, config.getLeaseTime());
		options.add(DHO_DOMAIN_NAME_SERVERS, config.getDnsServers());
		options.add(DHO_DHCP_SERVER_IDENTIFIER, host);
		InetAddress offer = config.getIpAddress();
		DHCPPacket ret = DHCPResponseFactory.makeDHCPOffer(
				request, offer, config.getLeaseTime(), host, "", options.toArray());
		ret.setSiaddr(host);
		return ret;
	}
    
    public DHCPPacket doInform(DHCPPacket request) {
    	log.info("Receive Inform from " + request.getChaddrAsHex());
    	return null;
    }
    
    public DHCPPacket doDecline(DHCPPacket request) {
    	log.info("Receive Decline from " + request.getChaddrAsHex());
    	return null;
    }
    
    public DHCPPacket doRelease(DHCPPacket request) {
    	log.info("Receive Release from " + request.getChaddrAsHex()
    			+ " Request IP " + request.getCiaddr().toString());
    	this.resourcesPool.releaseResource(request.getChaddrAsHex());
    	return null;
    }
}
