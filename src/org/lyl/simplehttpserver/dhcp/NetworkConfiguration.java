package org.lyl.simplehttpserver.dhcp;

import java.net.InetAddress;
import java.util.ArrayList;
import static org.lyl.simplehttpserver.dhcp.Utils.parse;

public class NetworkConfiguration {
	public static final int DEFAULT_LEASE_TIME = 3600;
	private InetAddress ipAddress;
	private InetAddress subnetMask;
	private InetAddress defaultGateway;
	private int leaseTime = DEFAULT_LEASE_TIME;
	private ArrayList<InetAddress> dnsServers = new ArrayList<InetAddress>();
	
	public void setIpAddress(String ipaddress) {
		this.ipAddress = parse(ipaddress);
	}
	
	public void setIpAddress(InetAddress ipaddress) {
		this.ipAddress = ipaddress;
	}
	
	public void setSubnetMask(InetAddress mask) {
		this.subnetMask = mask;
	}
	
	public void setSubnetMask(String mask) {
		this.subnetMask = parse(mask);
	}
	
	public void setDefaultGateway(InetAddress gateway) {
		this.defaultGateway = gateway;
	}
	
	public void setDefaultGateway(String gateway) {
		this.defaultGateway = parse(gateway);
	}
	
	public void setLeaseTime(int sec) {
		this.leaseTime = sec;
	}
	
	public void addDnsServer(InetAddress address) {
		this.dnsServers.add(address);
	}
	
	public void addDnsServer(String address) {
		this.dnsServers.add(parse(address));
	}
	
	public InetAddress getIpAddress() {
		return ipAddress;
	}
	
	public InetAddress getSubnetMask() {
		return subnetMask;
	}
	
	public InetAddress getDefaultGateway() {
		return defaultGateway;
	}
	
	public int getLeaseTime() {
		return leaseTime;
	}
	
	public InetAddress[] getDnsServers() {
		InetAddress[] a = new InetAddress[dnsServers.size()];
		return dnsServers.toArray(a);
	}
}
