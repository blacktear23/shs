package org.lyl.simplehttpserver.core;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public interface DatagramPacketService extends Service {
	void setPacket(DatagramPacket packet);
	void setDatagramSocket(DatagramSocket socket);
}
