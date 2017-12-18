package edu.chalmers.matchServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.badlogic.gdx.utils.GdxNativesLoader;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.matchServer.matchLogic.Match;
import edu.chalmers.nimby.network.lobby.LobbyNetwork;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.AbstractPacket;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.ConnectionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet5RegisterConnectionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet9MatchServerReady;
import edu.chalmers.nimby.network.match.MatchNetwork;
import edu.chalmers.nimby.network.match.PlayerConnection;
import edu.chalmers.nimby.network.match.PlayerInfo;


/**
 * The match server application.
 * @author Adam Grönberg
 *
 */
public final class MatchServer {
		
	/* Ports for this server */
	private static final int MATCH_SERVER_TCP_PORT = 54652;
	private static final int MATCH_SERVER_UDP_PORT = 54653;
	
	/* Ports for lobby server */
	public static final int LOBBY_SERVER_TCP_PORT = 54650;
	public static final int LOBBY_SERVER_UDP_PORT = 54655;
	
	/* IP of Lobby server */
	public static final String LOBBY_SERVER_IP = "127.0.0.1";
	
	/* Time before timeout between this server and Lobby server */
	public static final int TIMEOUT_TIME = 60000;
	
	private Server matchServer;
	private Client client;
	private Map<Integer, Match> matches;

	/**
	 * Initiates server, registers packages and binds the ports.
	 * @throws IOException Thrown if binding of port was unsuccessful
	 */
	private MatchServer() throws IOException {
		GdxNativesLoader.load();
		matches = new HashMap<Integer, Match>();
		serverInit();
		clientInit();		
		connectToLobbyServer();
	}

	/**
	 * Main. Starts the gameserver and sets the level of logger.
	 * @param args Not used
	 */
	public static void main(final String[] args) {
		try {
			new MatchServer();
			Log.set(Log.LEVEL_INFO);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}	
	}

	/**
	 * Gets the match server.
	 * @return the used match server
	 */
	public Server getMatchServer() {
		return matchServer;
	}
	
	/**
	 * Creates a new match.
	 * @param playersToConnect The players who will join the game.
	 * @param matchID the match ID of the game.
	 * @param conn the lobby server who sent the create request.
	 */
	public void createMatch(final int matchID, final HashSet<PlayerInfo> playersToConnect, final Connection conn) {
		Log.info("[SERVER] Starting Match with ID: " + matchID);
		Match match = new Match(matchID, playersToConnect, this);
		matches.put(matchID, match);
		new Thread(match).start();
	}
	
	/**
	 * Sends ready status to lobby server.
	 * @param matchID the match that is ready to receive players.
	 * @param conn the lobby connection.
	 */
	public void sendMatchReadyStatusToLobby(final int matchID, final Connection conn) {
		Log.info("[SERVER] Sending ready status to lobby of MatchID: " + matchID);
		Packet9MatchServerReady serverReady = new Packet9MatchServerReady();
		serverReady.matchID = matchID;
		client.sendTCP(serverReady);
	}
	
	/**
	 * Gets the match with matchID id.
	 * @param matchId the id of the match to find.
	 * @return	null if no match was found.
	 */
	public Match getMatch(final int matchId) {
		return matches.get(matchId);
	}
	
	/**
	 * Creates a match server, registers it and starts it.
	 * @throws IOException thrown if bind did not work
	 */
	private void serverInit() throws IOException {
		matchServer = new Server() {
			@Override
			protected Connection newConnection() {			//The server will only use UserConnections
				return new PlayerConnection();
			}
		};
		
		MatchNetwork.register(matchServer);
		matchServer.addListener(new GameClientListener(this));
		matchServer.bind(MATCH_SERVER_TCP_PORT, MATCH_SERVER_UDP_PORT);
		matchServer.start();
	}
	
	/**
	 * Creates a lobby client, registers it and starts it.
	 */
	private void clientInit() {
		client = new Client();
		LobbyNetwork.register(client);
		client.addListener(new LobbyServerListener(this));
		client.start();
	}
	
	/**
	 * Connects the client to the lobby server.
	 */
	private void connectToLobbyServer() {
		try {
			client.connect(TIMEOUT_TIME, LOBBY_SERVER_IP, LOBBY_SERVER_TCP_PORT, LOBBY_SERVER_UDP_PORT);
		} catch (IOException e) {
			Log.info("[CLIENT] ERROR: connection to " + LOBBY_SERVER_IP + " failed! Closing Down client");
			client.stop();
			e.printStackTrace();
		}
	}
	
	/**
	 * Registers the connection between the Match client and Lobby client as a server.
	 */
	public void registerConnectionType() {
		Packet5RegisterConnectionType registerRequest = new Packet5RegisterConnectionType();
		registerRequest.connectionType = ConnectionType.MATCH_SERVER;
		client.sendTCP(registerRequest);
	}
	
	/**
	 * Sends a message to everyone on the list.
	 * @param message the message to send
	 * @param recivers the receivers
	 */
	public void sendToList(final AbstractPacket message, final HashSet<PlayerConnection> recivers) {		
		for (PlayerConnection playerConn: recivers) {
			sendPrivateMessage(message, playerConn);
		}
	}
	
	/**
	 * Sends a message to a specific connection.
	 * @param message message to send
	 * @param userID User ID of receiver
	 */
	public void sendPrivateMessage(final AbstractPacket message, final Connection conn) {
		matchServer.sendToTCP(conn.getID(), message);
	}
}
