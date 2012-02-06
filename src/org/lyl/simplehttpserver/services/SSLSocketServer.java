package org.lyl.simplehttpserver.services;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.TrustManagerFactory;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.lyl.simplehttpserver.core.ConnectionService;

public class SSLSocketServer extends AbstractServer {
	
	private static Logger log = LogManager.getLogger(SSLSocketServer.class);
	private boolean statu = true;
	private int port = 443;
	private String keyStorePasswd = "";
	private String keyKeyPasswd = "";
	private String keyCertFile = "";
	private String trustCertFile = "";
	private String trustStorePasswd = "";
	private SSLServerSocket server = null;
	
	public SSLSocketServer() {
	}

	public void run() {
		runServer();
		log.info("SSL server shutdowned.");
	}
	
	private SSLServerSocketFactory getServerSocketFactory() throws Exception {
			char keyStorePass[]=getKeyStorePasswd().toCharArray();
			char keyKeyPassword[]=getKeyKeyPasswd().toCharArray();
			char trustStorePass[]=getTrustStorePasswd().toCharArray();
			
			KeyStore kks=KeyStore.getInstance("JKS");
			KeyStore tks=KeyStore.getInstance("JKS");
			InputStream kcfis = null;
			InputStream tcfis = null;
			try{
				kcfis = new FileInputStream(getKeyCertFile());
				tcfis = new FileInputStream(getTrustCertFile());
				kks.load(kcfis, keyStorePass);
				tks.load(tcfis, trustStorePass);
				
				KeyManagerFactory kmf=KeyManagerFactory.getInstance("SunX509");
				kmf.init(kks,keyKeyPassword);
				TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
				tmf.init(tks);
				
				SSLContext sslContext=SSLContext.getInstance("SSLv3");
				sslContext.init(kmf.getKeyManagers(),tmf.getTrustManagers(),null);
				return sslContext.getServerSocketFactory();
			} catch(Exception e) {
				throw e;
			} finally {
				if(kcfis != null)
					kcfis.close();
				if(tcfis != null)
					tcfis.close();
			}
	}
	
	public void runServer() {
		SSLServerSocketFactory ssf = null;
		try {
			ssf = getServerSocketFactory();
		} catch(Exception ex) {
			log.error(ex);
			log.error("SSL initialize failed! SSL Server shut down.");
			statu = false;
			return;
		}

		try {
			server = (SSLServerSocket)ssf.createServerSocket(this.port);
			log.info("SSL Socket Server Listening:" + this.port);
		} catch(IOException ex) {
			log.error(ex);
			return;
		}
		while(statu) {
			try{
				Socket ssock = server.accept();
				ConnectionService cs = (ConnectionService)getServerContext().getService(getConnectionServiceName());
				cs.setClient(ssock);
				getServerContext().runService(cs);
			} catch (IOException ex) {
				log.error(ex);
			}
		}
		try {
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

	public String getKeyStorePasswd() {
		return keyStorePasswd;
	}

	public void setKeyStorePasswd(String keyStorePasswd) {
		this.keyStorePasswd = keyStorePasswd;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getKeyKeyPasswd() {
		return keyKeyPasswd;
	}

	public void setKeyKeyPasswd(String keyKeyPasswd) {
		this.keyKeyPasswd = keyKeyPasswd;
	}

	public String getKeyCertFile() {
		return keyCertFile;
	}

	public void setKeyCertFile(String keyCertFile) {
		this.keyCertFile = keyCertFile;
	}

	public String getTrustCertFile() {
		return trustCertFile;
	}

	public void setTrustCertFile(String trustCertFile) {
		this.trustCertFile = trustCertFile;
	}

	public String getTrustStorePasswd() {
		return trustStorePasswd;
	}

	public void setTrustStorePasswd(String trustStorePasswd) {
		this.trustStorePasswd = trustStorePasswd;
	}
}
