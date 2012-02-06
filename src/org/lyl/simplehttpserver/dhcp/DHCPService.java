package org.lyl.simplehttpserver.dhcp;

import static org.dhcp4java.DHCPConstants.BOOTREQUEST;
import static org.dhcp4java.DHCPConstants.DHCPDECLINE;
import static org.dhcp4java.DHCPConstants.DHCPDISCOVER;
import static org.dhcp4java.DHCPConstants.DHCPINFORM;
import static org.dhcp4java.DHCPConstants.DHCPRELEASE;
import static org.dhcp4java.DHCPConstants.DHCPREQUEST;
import static org.dhcp4java.DHCPConstants.DHO_DHCP_LEASE_TIME;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dhcp4java.DHCPPacket;
import org.lyl.simplehttpserver.core.DatagramPacketService;

public class DHCPService implements DatagramPacketService {
	private static Logger log = LogManager.getLogger(DHCPService.class);
	private DatagramPacket packet = null;
	private DHCPProcessor processor = null;
	private DatagramSocket socket = null;
	
	public void setProcessor(DHCPProcessor processor) {
		this.processor = processor;
	}
	
	@Override
	public void run() {
		try {
			DHCPPacket dhcp_recv = DHCPPacket.getPacket(this.packet);
			DHCPPacket dhcp_send = null;
			if(!dhcp_recv.isDhcp())
				return;
			Byte messageType = dhcp_recv.getDHCPMessageType();
			if (dhcp_recv.getOp() == BOOTREQUEST) {
				switch (messageType) {
	        	case DHCPDISCOVER: 
	        		dhcp_send = processor.doDiscover(dhcp_recv);
	        		break;
	        	case DHCPREQUEST:  
	        		dhcp_send = processor.doRequest(dhcp_recv);
	        		break;
	        	case DHCPINFORM:   
	        		dhcp_send = processor.doInform(dhcp_recv);
	        		break;
	        	case DHCPDECLINE:  
	        		dhcp_send = processor.doDecline(dhcp_recv);
	        		break;
	        	case DHCPRELEASE:  
	        		dhcp_send = processor.doRelease(dhcp_recv);
	        		break;
	        	default:
	        	    log.info("Unsupported message type " + messageType);
	        	}
			}
			if(dhcp_send != null) {
				InetAddress address = dhcp_send.getAddress();
            	int port = dhcp_send.getPort();
            	byte[] buf = dhcp_send.serialize();
            	DatagramPacket response = new DatagramPacket(buf, buf.length, address, port);
            	log.info("Send Datagram to " + response.getAddress() + 
            			" Assign IP " + dhcp_send.getYiaddr() + 
            			" Lease time " + dhcp_send.getOptionAsInteger(DHO_DHCP_LEASE_TIME));
            	socket.send(response);
			}
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		} finally {
			
		}
	}
	
	@Override
	public void setPacket(DatagramPacket packet) {
		this.packet = packet;
	}
	
	@Override
	public void shutdown() {
		//do nothing.
	}

	@Override
	public void setDatagramSocket(DatagramSocket socket) {
		this.socket = socket;
	}
}
