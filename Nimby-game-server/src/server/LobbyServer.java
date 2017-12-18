package server;

import java.io.IOException;

import com.esotericsoftware.minlog.Log;

import edu.chalmers.nimby.NetworkConstants;
import edu.chalmers.nimby.network.lobby.LobbyNetwork;


/**
 * The server application.
 * @author Adam Gr�nberg, Gustav Dahl
 *
 */
public final class LobbyServer {
		
	private LobbyPacketFactory lobbyServer;
	private ServerNetworkListener serverNetworkListener;
	
	/**
	 * The Lobby/Chat server.
	 * @throws IOException Thrown if binding of port was unsuccessful
	 */
	private LobbyServer() throws IOException {
		serverInit();
		serverStart();
		startCommandInput();
	}
	
	/**
	 * Starts scanning for commands in system.in.
	 */
	private void startCommandInput() {
		new Thread(new CommandInput(lobbyServer)).start();
	}

	/**
	 * Initiates server, registers packages and binds the ports.
	 * @throws IOException Unable to bind ports
	 */
	private void serverInit() throws IOException {
		lobbyServer = new LobbyPacketFactory();
		serverNetworkListener = new ServerNetworkListener(lobbyServer);
		LobbyNetwork.register(lobbyServer);
		lobbyServer.addListener(serverNetworkListener);
		lobbyServer.bind(NetworkConstants.LOBBY_SERVER_TCP_PORT, NetworkConstants.LOBBY_SERVER_UDP_PORT);
	}

	/**
	 * Starts the server.
	 */
	private void serverStart() {
		lobbyServer.start();
	}

	/**
	 * Main. Starts the gameserver and sets the level of logger.
	 * @param args Not used
	 */
	public static void main(final String[] args) {
		try {
			new LobbyServer();
			Log.set(Log.LEVEL_INFO);
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
}
