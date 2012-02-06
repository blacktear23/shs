package org.lyl.simplehttpserver.core;

import java.net.Socket;

public interface ConnectionService extends Service {
	void setClient(Socket client);
}
