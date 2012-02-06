package org.lyl.simplehttpserver.dhcp.resourcepool;

import org.lyl.simplehttpserver.dhcp.NetworkConfiguration;
import org.lyl.simplehttpserver.dhcp.ResourcePool;

/**
 * If take offer, the mac is set to request client's mac.
 * If take lease, the mac must equals to request client's mac.
 * Then set leaseDate.
 * 
 * @author mac
 *
 */
public class SingleResourcePool implements ResourcePool {
	private String mac = null;
	private String ip;
	private String mask;
	private String gateway;
	private int leaseTime;
	private String dns;
	private long leaseDate;
	private boolean reserved = false;
	
	@Override
	public boolean isOfferTo(String mac) {
		assert mac != null;
		return mac.equals(this.mac);
	}

	@Override
	public synchronized NetworkConfiguration leaseResource(String mac) {
		if(mac.equals(this.mac)) {
			setLeaseDate();		
			return generateConfiguration();
		}
		return null;
	}

	@Override
	public synchronized NetworkConfiguration offerResource(String mac) {
		if(!this.inLeasing() || mac.equals(this.mac)) {
			this.mac = mac;
			return generateConfiguration();
		}
		return null;
	}

	@Override
	public void releaseResource(String mac) {
		if(mac.equals(this.mac))
			this.mac = null;
		if(this.reserved)
			this.reserved = false;
	}
	
	private boolean inLeasing() {
		if(this.mac == null)
			return false;
		if(System.currentTimeMillis() > (this.leaseDate + this.leaseTime*1000)) {
			//Leasing time out.
			//System.out.println("LeaseDate: "+ this.leaseDate + " LeaseTime: " + this.leaseTime + " Timeout");
			this.mac = null;
			return false;
		}
		return true;
	}
	
	private NetworkConfiguration generateConfiguration() {
		NetworkConfiguration conf = new NetworkConfiguration();
		conf.addDnsServer(this.dns);
		conf.setDefaultGateway(this.gateway);
		conf.setIpAddress(this.ip);
		conf.setSubnetMask(this.mask);
		conf.setLeaseTime(this.leaseTime);
		return conf;
	}
	
	@Override
	public boolean reserveIP(String ip) {
		String pip = ip.replace("/", "");
		if(pip.equals(this.ip)) {
			this.reserved = true;
			return true;
		}
		return false;
	}
	
	private void setLeaseDate() {
		this.leaseDate = System.currentTimeMillis();
	}
	
	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	
	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public int getLeaseTime() {
		return leaseTime;
	}

	public void setLeaseTime(int leaseTime) {
		this.leaseTime = leaseTime;
	}

	public String getDns() {
		return dns;
	}

	public void setDns(String dns) {
		this.dns = dns;
	}
	
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
}
