package org.lyl.simplehttpserver.services;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.DatagramPacketService;

public class DatagramSocketServer extends AbstractServer {
	private static Logger log = LogManager.getLogger(DatagramSocketServer.class);
	private boolean statu = true;
	private int port = 67;
	private DatagramSocket server = null;
	private int bufferSize = 2048;
	
	@Override
	public void shutdown() {
		this.statu = false;
		if(server != null && !server.isClosed()){
			server.close();
		}
	}

	@Override
	public void run() {
		runDatagramSocket();
		log.info("DatagramSocket server shutdowned.");
	}
	
	private void runDatagramSocket() {
		try {
			this.server = new DatagramSocket(this.port);
			log.info("DatagramSocket Server Listening:" + this.port);
		} catch (IOException e) {
			log.error(e);
			return;
		}
		
		while(this.statu) {
			try {
				DatagramPacket packet = new DatagramPacket(new byte[this.bufferSize], this.bufferSize);
				server.receive(packet);
				DatagramPacketService dps = (DatagramPacketService)getServerContext().getService(getDatagramPacketServiceName());
				dps.setDatagramSocket(server);
				dps.setPacket(packet);
				getServerContext().runService(dps);
			} catch(Exception ex) {
				log.error(ex);
			}
		}
		try {
			if(!server.isClosed()) {
				server.close();
			}
		} catch (Exception e) {
			log.error(e);
		}
	}
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
	public int getBufferSize() {
		return bufferSize;
	}

	public void setBufferSize(int bufferSize) {
		this.bufferSize = bufferSize;
	}
}
