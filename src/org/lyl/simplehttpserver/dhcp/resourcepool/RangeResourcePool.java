package org.lyl.simplehttpserver.dhcp.resourcepool;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.dhcp.NetworkConfiguration;
import org.lyl.simplehttpserver.dhcp.ResourcePool;
import org.lyl.simplehttpserver.dhcp.Utils;

public class RangeResourcePool implements ResourcePool{
	private static Logger log = LogManager.getLogger(RangeResourcePool.class);
	private Map<String, ResourceStatus> resources = new HashMap<String, ResourceStatus>();
	
	private boolean inited = false;
	private String startIP;
	private String endIP;
	private String mask;
	private String gateway;
	private int leaseTime;
	private String dns;
	
	public String getStartIP() {
		return startIP;
	}

	public void setStartIP(String startIP) {
		this.startIP = startIP;
	}

	public String getEndIP() {
		return endIP;
	}

	public void setEndIP(String endIP) {
		this.endIP = endIP;
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) {
		this.mask = mask;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
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

	public Map<String, ResourceStatus> getResources() {
		if(!inited) {
			init();
			inited = true;
			log.info("Initialize IP Range.");
		}
		return this.resources;
	}
	
	private void init() {
		String[] ips = Utils.listIPRange(this.startIP, this.endIP);
		for(String ip : ips) {
			this.resources.put(ip, new ResourceStatus());
		}
	}
	
	
	@Override
	public boolean isOfferTo(String mac) {
		boolean ret = false;
		synchronized(this) {
			for(ResourceStatus rs : getResources().values()) {
				if(rs.getMac().equals(mac) && rs.isInOffering()) {
					ret = true;
					break;
				}
			}
		}
		return ret;
	}

	@Override
	public NetworkConfiguration leaseResource(String mac) {
		ResourceStatus rs = null;
		String ip = null;
		synchronized(this) {
			for(Entry<String, ResourceStatus> e : getResources().entrySet()) {
				if(e.getValue().getMac().equals(mac) &&
				   e.getValue().isInOffering()) {
					rs = e.getValue();
					ip = e.getKey();
					break;
				}
			}
		}
		if(rs != null) {
			rs.setStatus(Status.LEASE);
			rs.setLeaseDate(System.currentTimeMillis());
			return generateConfiguration(ip);
		}
		return null;
	}

	@Override
	public NetworkConfiguration offerResource(String mac) {
		ResourceStatus rs = null;
		String ip = null;
		checkTimeOutLease();
		synchronized(this) {
			for(Entry<String, ResourceStatus> e : getResources().entrySet()) {
				if(e.getValue().getMac().equals(mac) &&
				   e.getValue().isInLeasing()) {
					rs = e.getValue();
					rs.setStatus(Status.INOFFERING);
					ip = e.getKey();
				}
			}
			if(ip == null) {
				for(Entry<String, ResourceStatus> e : getResources().entrySet()) {
					if(e.getValue().isIdle()) {
						rs = e.getValue();
						ip = e.getKey();
						rs.setStatus(Status.INOFFERING);
						rs.setMac(mac);
						break;
					}
				}
			}
		}
		if(ip != null) {
			return generateConfiguration(ip);
		}
		return null;
	}

	@Override
	public void releaseResource(String mac) {
		synchronized(this) {
			for(Entry<String, ResourceStatus> e : getResources().entrySet()) {
				if(e.getValue().getMac().equals(mac)) {
					e.getValue().setStatus(Status.IDLE);
					break;
				}
			}
		}
	}

	@Override
	public boolean reserveIP(String ip) {
		ResourceStatus rs = null;
		synchronized(this) {
			rs = getResources().get(ip);
			if(rs != null) {
			    rs.setStatus(Status.LEASE);
			    rs.setLeaseDate(System.currentTimeMillis());
			    return true;
		    }
		}
		return false;
	}
	
	private void checkTimeOutLease() {
		long now = System.currentTimeMillis();
		synchronized(this) {
			for(ResourceStatus rs : getResources().values()) {
				if(rs.isInLeasing()) {
					long start = rs.getLeaseDate();
					if((now - start) > this.leaseTime * 1000) {
						//lease timeout
						rs.setMac("");
						rs.setStatus(Status.IDLE);
					}
				}
			}
		}
	}
	
	private NetworkConfiguration generateConfiguration(String ip) {
		NetworkConfiguration conf = new NetworkConfiguration();
		conf.addDnsServer(this.dns);
		conf.setDefaultGateway(this.gateway);
		conf.setIpAddress(ip);
		conf.setSubnetMask(this.mask);
		conf.setLeaseTime(this.leaseTime);
		return conf;
	}
	
	private enum Status {
		IDLE,
		INOFFERING,
		LEASE
	}
	
	private class ResourceStatus {
		private Status status;
		private String mac;
		private long leaseDate;
		
		public boolean isInLeasing() {
			return status == Status.LEASE;
		}
		
		public boolean isIdle() {
			return status == Status.IDLE;
		}
		
		public boolean isInOffering() {
			return status == Status.INOFFERING;
		}
		
		public ResourceStatus() {
			this.status = Status.IDLE;
			this.mac = "";
		}
		public Status getStatus() {
			return status;
		}
		public void setStatus(Status status) {
			this.status = status;
		}
		public String getMac() {
			return mac;
		}
		public void setMac(String mac) {
			this.mac = mac;
		}
		public void setLeaseDate(long timeStamp) {
			this.leaseDate = timeStamp;
		}
		
		public long getLeaseDate() {
			return this.leaseDate;
		}
	}
}
