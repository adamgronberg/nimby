package edu.chalmers.nimby.network.lobby;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.minlog.Log;

/**
 * Contains shared functions of LobbyServerHandle and MatchLobbyHandle.
 * @author Adam Gr�nberg
 *
 */
public abstract class ServerHandle {

	public static final int TIMEOUT_TIME = 30000;
	protected Client client;

	/**
	 * Tries to connect the client to a server.
	 * @param ip	The IP to connect to
	 * @param tcpPort	TCP port to connect to
	 * @param udpPort	UDP port to connect to
	 */
	public final void connectToServer(final String ip, final int tcpPort, final int udpPort) {
		client.start();
		try {
			client.connect(TIMEOUT_TIME, ip, tcpPort, udpPort);
		} catch (IOException e) {
			Log.error("[CLIENT] ERROR: connection to " + ip + " failed");
			client.stop();
			Log.error("[CLIENT] Client stoped, restart the client to try again");
		}
	}

	/**
	 * Creates the client and registers the network to the client.
	 */
	public abstract void init();
}
