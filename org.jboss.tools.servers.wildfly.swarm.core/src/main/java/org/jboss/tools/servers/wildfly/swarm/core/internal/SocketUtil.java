package org.jboss.tools.servers.wildfly.swarm.core.internal;

import java.io.IOException;
import java.net.Socket;

public class SocketUtil {

	private SocketUtil() {
	}
	
	public static int getNextAvailablePort(int port) {
		int offset = detectPortOffset(port);
		if (offset < 0) {
			//FIXME that's really an error
			return port;
		}
		return port+offset;
	}
	
	public static int detectPortOffset(int port) {
		for (int offset=0; offset<100;offset++) {
		   int newPort = port+offset;
		   try (Socket socket = new Socket("localhost", newPort)) {
		   } catch (IOException ignored) {
			   return offset;
		   }
		}
		return -1;//TODO handle error?
	}
}
