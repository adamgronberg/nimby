package edu.chalmers.matchServer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.badlogic.gdx.utils.GdxNativesLoader;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import edu.chalmers.matchServer.matchLogic.Match;
import edu.chalmers.nimby.NetworkConstants;
import edu.chalmers.nimby.model.equipment.skill.SpawnProjectile;
import edu.chalmers.nimby.model.gameLogic.Part;
import edu.chalmers.nimby.network.lobby.LobbyNetwork;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.ConnectionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet5RegisterConnectionType;
import edu.chalmers.nimby.network.lobby.LobbyNetwork.Packet9MatchServerReady;
import edu.chalmers.nimby.network.match.MatchNetwork;
import edu.chalmers.nimby.network.match.PlayerInfo;


/**
 * The match server application.
 * @author Adam Gr�nberg
 *
 */
public final class MatchServer {
	
	/* Time before timeout between this server and Lobby server */
	public static final int TIMEOUT_TIME = 60000;
	
	private Client client;
	private Map<Integer, Match> matches;
	private MatchPacketFactory packetFactory;

	/**
	 * Initiates server, registers packages and binds the ports.
	 * @throws IOException Thrown if binding of port was unsuccessful
	 */
	private MatchServer() throws IOException {
		GdxNativesLoader.load();
		Part.setShouldRender(false);						//TODO Bad implementation but works...
		SpawnProjectile.setShouldSpawnProjectiles(false);	//TODO Bad implementation but works...
		matches = new HashMap<Integer, Match>();
		packetFactory = new MatchPacketFactory();
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
		packetFactory = new MatchPacketFactory();
		MatchNetwork.register(packetFactory);
		packetFactory.addListener(new GameClientListener(this));
		packetFactory.bind(NetworkConstants.MATCH_SERVER_TCP_PORT, NetworkConstants.MATCH_SERVER_UDP_PORT);
		packetFactory.start();
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
			client.connect(TIMEOUT_TIME, NetworkConstants.LOBBY_SERVER_IP, 
							NetworkConstants.LOBBY_SERVER_TCP_PORT, 
								NetworkConstants.LOBBY_SERVER_UDP_PORT);
		} catch (IOException e) {
			Log.info("[CLIENT] ERROR: connection to " + NetworkConstants.LOBBY_SERVER_IP + " failed! Closing Down client, relaunch to try again");
			client.stop();
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
	 * @return gets the {@link MatchPacketFactory} containing functions to send and create packets.
	 */
	public MatchPacketFactory getPacketSender() {
		return packetFactory;
	}
}
