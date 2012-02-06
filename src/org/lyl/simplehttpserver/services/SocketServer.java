package org.lyl.simplehttpserver.services;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.ConnectionService;

public class SocketServer extends AbstractServer {
	private static Logger log = LogManager.getLogger(SocketServer.class);
	private boolean statu = true;
	private int port = 80;
	private ServerSocket server = null;


	public SocketServer(){
	}

	public void run() {
		_runServerWithIO();
		log.info("Socket server shutdowned.");
	}

	private void _runServerWithIO() {
		try {
			server = new ServerSocket(this.port);
			log.info("Server Socket Server Listening:" + this.port);
		} catch(IOException ex) {
			log.error(ex);
			return;
		}
		Socket client = null;
		while(statu) {
			try {
				client = server.accept();
				ConnectionService cs = (ConnectionService)getServerContext().getService(getConnectionServiceName());
				cs.setClient(client);
				getServerContext().runService(cs);
			} catch(Exception ex) {
				log.error(ex);
			}
		}
		try {
			if(!server.isClosed())
				server.close();
		} catch(IOException ex) {
			log.error(ex);
		}
	}
	
	public void shutdown() {
		this.statu = false;
		if(server != null && !server.isClosed()){
			try {
				server.close();
			} catch (IOException e) {
				log.error(e.toString());
			}
		}
	}
	
	
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
